package com.vault.velocitylimiter.service;

import com.vault.velocitylimiter.exception.LoadAttemptException;
import com.vault.velocitylimiter.model.Customer;
import com.vault.velocitylimiter.model.LoadAttempt;
import com.vault.velocitylimiter.repo.CustomerRepository;
import com.vault.velocitylimiter.repo.LoadAttemptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import java.time.temporal.TemporalAdjusters;
import java.time.DayOfWeek;

import java.time.LocalDateTime;

@Service
public class LoadAttemptServiceImpl implements LoadAttemptService{
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private LoadAttemptRepository loadAttemptRepository;

    private static final float DAILY_AMOUNT_LIMIT = 5000;
    private static final int DAILY_LOAD_COUNT_LIMIT = 3;
    private static final float WEEKLY_AMOUNT_LIMIT = 20000;

    public LoadAttempt processLoadAttempt(LoadAttempt loadAttempt) throws DataIntegrityViolationException {
        Customer customer = customerRepository.findById(loadAttempt.getCustomer().getCustomerId())
                .orElse(new Customer(loadAttempt.getCustomer().getCustomerId(), 0f, 0, 0f));

        if (customerRepository.findById(loadAttempt.getCustomer().getCustomerId()).isPresent()) {
            updateLimits(customer, loadAttempt.getTime());
        }

        float newDailyLoadAmount = customer.getDailyLoadAmount() + loadAttempt.getLoadAmount();
        int newDailyLoadCount = customer.getDailyLoadCount() + 1;
        float newWeeklyLoadAmount = customer.getWeeklyLoadAmount() + loadAttempt.getLoadAmount();

        if (newDailyLoadAmount > DAILY_AMOUNT_LIMIT) {
            customerRepository.save(customer);
            loadAttemptRepository.save(loadAttempt);
            throw new LoadAttemptException("Daily load amount limit exceeded.");
        }

        if (newWeeklyLoadAmount > WEEKLY_AMOUNT_LIMIT) {
            customerRepository.save(customer);
            loadAttemptRepository.save(loadAttempt);
            throw new LoadAttemptException("Weekly load amount limit exceeded.");
        }

        if (newDailyLoadCount > DAILY_LOAD_COUNT_LIMIT) {
            customerRepository.save(customer);
            loadAttemptRepository.save(loadAttempt);
            throw new LoadAttemptException("Daily load count limit exceeded.");
        }

        customer.setDailyLoadAmount(newDailyLoadAmount);
        customer.setDailyLoadCount(newDailyLoadCount);
        customer.setWeeklyLoadAmount(newWeeklyLoadAmount);
        loadAttempt.setAccepted(true);

        customerRepository.save(customer);
        return loadAttemptRepository.save(loadAttempt);
    }

    private void updateLimits(Customer customer, LocalDateTime currentTime) {
        LocalDateTime startOfDay = currentTime.toLocalDate().atStartOfDay();
        LocalDateTime startOfWeek = startOfDay.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        Float dailyLoadAmount = loadAttemptRepository.getDailyLoadAmountByCustomerAndTime(customer, startOfDay, currentTime);
        int dailyLoadCount = loadAttemptRepository.getDailyLoadCountByCustomerAndTime(customer, startOfDay, currentTime);
        Float weeklyLoadAmount = loadAttemptRepository.getWeeklyLoadAmountByCustomerAndTime(customer, startOfWeek, currentTime);

        customer.setDailyLoadAmount(dailyLoadAmount != null ? dailyLoadAmount : 0f);
        customer.setDailyLoadCount(dailyLoadCount);
        customer.setWeeklyLoadAmount(weeklyLoadAmount != null ? weeklyLoadAmount : 0f);
    }
}
