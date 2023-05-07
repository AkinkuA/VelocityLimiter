package com.vault.velocitylimiter.service;

import com.vault.velocitylimiter.exception.LoadAttemptException;
import com.vault.velocitylimiter.model.Customer;
import com.vault.velocitylimiter.model.LoadAttempt;
import com.vault.velocitylimiter.repo.CustomerRepository;
import com.vault.velocitylimiter.repo.LoadAttemptRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoadAttemptServiceImplTest {

    @InjectMocks
    private LoadAttemptServiceImpl loadAttemptService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private LoadAttemptRepository loadAttemptRepository;

    private Customer customer;
    private LoadAttempt loadAttempt;
    private static final LocalDateTime FIXED_LOCAL_DATE_TIME =
            LocalDateTime.of(2023, 5, 6, 20, 14, 10, 10);

    @BeforeEach
    public void setUp() {
        customer = new Customer(1, 0f, 0, 0f);
        loadAttempt = new LoadAttempt(1, customer, 1000f, FIXED_LOCAL_DATE_TIME, false);
    }

    @Test
    public void testProcessLoadAttempt() {
        when(customerRepository.findById(customer.getCustomerId()))
                .thenReturn(Optional.of(customer));
        when(loadAttemptRepository.save(loadAttempt)).thenReturn(loadAttempt);

        LoadAttempt result = loadAttemptService.processLoadAttempt(loadAttempt);

        assertEquals(loadAttempt, result);
        verify(customerRepository, times(1)).save(customer);
        verify(loadAttemptRepository, times(1)).save(loadAttempt);
    }

    @Test
    public void testProcessLoadAttempt_ThrowsException_WhenLoadAttemptExists() {
        when(loadAttemptRepository.save(loadAttempt)).
                thenThrow(DataIntegrityViolationException.class);
        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> {
            loadAttemptService.processLoadAttempt(loadAttempt);
        });

    }

    @Test
    public void testProcessLoadAttempt_ThrowsException_WhenDailyAmountLimitExceeded() {
        when(customerRepository.findById(customer.getCustomerId()))
                .thenReturn(Optional.of(customer));
        when(loadAttemptRepository.getDailyLoadAmountByCustomerAndTime(
                customer,FIXED_LOCAL_DATE_TIME.toLocalDate().atStartOfDay(),FIXED_LOCAL_DATE_TIME))
                .thenReturn(4900f);

        LoadAttemptException exception = assertThrows(LoadAttemptException.class, () -> {
            loadAttemptService.processLoadAttempt(loadAttempt);
        });

        assertEquals("Daily load amount limit exceeded.", exception.getMessage());
    }

    @Test
    public void testProcessLoadAttempt_ThrowsException_WhenWeeklyAmountLimitExceeded() {
        when(customerRepository.findById(customer.getCustomerId()))
                .thenReturn(Optional.of(customer));
        when(loadAttemptRepository.getWeeklyLoadAmountByCustomerAndTime(
                customer,FIXED_LOCAL_DATE_TIME.toLocalDate().
                        atStartOfDay().
                        with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)),FIXED_LOCAL_DATE_TIME))
                .thenReturn(19500f);


        LoadAttemptException exception = assertThrows(LoadAttemptException.class, () -> {
            loadAttemptService.processLoadAttempt(loadAttempt);
        });

        assertEquals("Weekly load amount limit exceeded.", exception.getMessage());
    }

    @Test
    public void testProcessLoadAttempt_ThrowsException_WhenDailyLoadCountExceeded() {
        when(customerRepository.findById(customer.getCustomerId()))
                .thenReturn(Optional.of(customer));
        when(loadAttemptRepository.getDailyLoadCountByCustomerAndTime(
                customer,FIXED_LOCAL_DATE_TIME.toLocalDate().atStartOfDay(),FIXED_LOCAL_DATE_TIME))
                .thenReturn(3);


        LoadAttemptException exception = assertThrows(LoadAttemptException.class, () -> {
            loadAttemptService.processLoadAttempt(loadAttempt);
        });

        assertEquals("Daily load count limit exceeded.", exception.getMessage());
    }
}
