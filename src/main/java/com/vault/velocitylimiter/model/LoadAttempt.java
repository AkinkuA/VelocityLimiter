package com.vault.velocitylimiter.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "load_attempts", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"customer_id", "id"})
})
public class LoadAttempt {

    @Id
    @Column(name = "load_attempt_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loadAttemptId;

    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id")
    @JsonIgnore
    private Customer customer;

    @Column(name = "load_amount")
    private Float loadAmount;

    @Column(name = "time")
    private LocalDateTime time;

    @Column(name = "accepted")
    private boolean accepted;

    public LoadAttempt() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LoadAttempt(Integer id, Customer customer, Float loadAmount, LocalDateTime time, boolean accepted) {
        this.id = id;
        this.customer = customer;
        this.loadAmount = loadAmount;
        this.time = time;
        this.accepted = accepted;
    }

    public Long getLoadAttemptId() {
        return loadAttemptId;
    }

    public void setLoadAttemptIdId(Long id) {
        this.loadAttemptId = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Float getLoadAmount() {
        return loadAmount;
    }

    public void setLoadAmount(Float loadAmount) {
        this.loadAmount = loadAmount;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    @JsonProperty("load_amount")
    public String getLoadAmountAsString() {
        return loadAmount != null ? String.format("$%.2f", loadAmount) : null;
    }

    @JsonProperty("load_amount")
    public void setLoadAmountFromString(String loadAmountString) {
        this.loadAmount = loadAmountString != null ? Float.parseFloat(loadAmountString.replace("$", "")) : null;
    }

    @JsonProperty("customer_id")
    public Integer getCustomerId() {
        return customer != null ? customer.getCustomerId() : null;
    }

    @JsonProperty("customer_id")
    public void setCustomerId(Integer customerId) {
        if (customer == null) {
            customer = new Customer();
        }
        customer.setCustomerId(customerId);
    }

    @JsonProperty("accepted")
    public boolean getAccepted(){
        return accepted;
    }

    @JsonProperty("accepted")
    public void setAccepted(boolean accepted){
        this.accepted = accepted;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof LoadAttempt)) {
            return false;
        }

        LoadAttempt la = (LoadAttempt) o;

        return id.equals(la.getId()) && customer.getCustomerId().equals(la.getCustomerId());
    }

}
