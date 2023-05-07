package com.vault.velocitylimiter.model;

import jakarta.persistence.*;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @Column(name = "customer_id")
    private Integer customerId;

    @Column(name = "daily_load_amount")
    private Float dailyLoadAmount;

    @Column(name = "daily_load_count")
    private Integer dailyLoadCount;

    @Column(name = "weekly_load_amount")
    private Float weeklyLoadAmount;

    public Customer() {
    }

    public Customer(Integer customerId, Float dailyLoadAmount, Integer dailyLoadCount, Float weeklyLoadAmount) {
        this.customerId = customerId;
        this.dailyLoadAmount = dailyLoadAmount;
        this.dailyLoadCount = dailyLoadCount;
        this.weeklyLoadAmount = weeklyLoadAmount;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Float getDailyLoadAmount() {
        return dailyLoadAmount;
    }

    public void setDailyLoadAmount(Float dailyLoadAmount) {
        this.dailyLoadAmount = dailyLoadAmount;
    }

    public Integer getDailyLoadCount() {
        return dailyLoadCount;
    }

    public void setDailyLoadCount(Integer dailyLoadCount) {
        this.dailyLoadCount = dailyLoadCount;
    }

    public Float getWeeklyLoadAmount() {
        return weeklyLoadAmount;
    }

    public void setWeeklyLoadAmount(Float weeklyLoadAmount) {
        this.weeklyLoadAmount = weeklyLoadAmount;
    }

}
