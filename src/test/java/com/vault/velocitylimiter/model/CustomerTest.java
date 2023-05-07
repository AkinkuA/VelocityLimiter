package com.vault.velocitylimiter.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomerTest {

    private Customer customer;

    @BeforeEach
    public void setUp() {
        customer = new Customer();
    }

    @Test
    public void testCustomerId() {
        customer.setCustomerId(1);
        assertEquals(1, customer.getCustomerId());
    }

    @Test
    public void testDailyLoadAmount() {
        customer.setDailyLoadAmount(5000.0f);
        assertEquals(5000.0f, customer.getDailyLoadAmount());
    }

    @Test
    public void testDailyLoadCount() {
        customer.setDailyLoadCount(3);
        assertEquals(3, customer.getDailyLoadCount());
    }

    @Test
    public void testWeeklyLoadAmount() {
        customer.setWeeklyLoadAmount(10000.0f);
        assertEquals(10000.0f, customer.getWeeklyLoadAmount());
    }

    @Test
    public void testConstructor() {
        Customer customer = new Customer(1, 5000.0f, 3, 10000.0f);
        assertEquals(1, customer.getCustomerId());
        assertEquals(5000.0f, customer.getDailyLoadAmount());
        assertEquals(3, customer.getDailyLoadCount());
        assertEquals(10000.0f, customer.getWeeklyLoadAmount());
    }
}
