package com.vault.velocitylimiter.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class LoadAttemptTest {

    private LoadAttempt loadAttempt;

    @BeforeEach
    public void setUp() {
        loadAttempt = new LoadAttempt();
    }

    @Test
    public void testId() {
        loadAttempt.setLoadAttemptIdId(1L);
        assertEquals(1, loadAttempt.getLoadAttemptId());
    }

    @Test
    public void testCustomer() {
        Customer customer = new Customer(1, 0f, 0, 0f);
        loadAttempt.setCustomer(customer);
        assertEquals(customer, loadAttempt.getCustomer());
    }

    @Test
    public void testLoadAmount() {
        loadAttempt.setLoadAmount(1000.0f);
        assertEquals(1000.0f, loadAttempt.getLoadAmount());
    }

    @Test
    public void testTime() {
        LocalDateTime now = LocalDateTime.now();
        loadAttempt.setTime(now);
        assertEquals(now, loadAttempt.getTime());
    }

    @Test
    public void testConstructor() {
        Customer customer = new Customer(1, 0f, 0, 0f);
        LocalDateTime now = LocalDateTime.now();
        LoadAttempt loadAttempt = new LoadAttempt(1, customer, 1000.0f, now, false);

        assertEquals(1, loadAttempt.getId());
        assertEquals(customer, loadAttempt.getCustomer());
        assertEquals(1000.0f, loadAttempt.getLoadAmount());
        assertEquals(now, loadAttempt.getTime());
    }

    @Test
    public void testLoadAmountAsString() {
        loadAttempt.setLoadAmount(1000.0f);
        assertEquals("$1000.00", loadAttempt.getLoadAmountAsString());
    }

    @Test
    public void testLoadAmountFromString() {
        loadAttempt.setLoadAmountFromString("$1000.00");
        assertEquals(1000.0f, loadAttempt.getLoadAmount());
    }

    @Test
    public void testCustomerId() {
        Customer customer = new Customer(1, 0f, 0, 0f);
        loadAttempt.setCustomer(customer);
        assertEquals(1, loadAttempt.getCustomerId());
    }

    @Test
    public void testSetCustomerId() {
        loadAttempt.setCustomerId(1);
        assertNotNull(loadAttempt.getCustomer());
        assertEquals(1, loadAttempt.getCustomerId());
    }
}
