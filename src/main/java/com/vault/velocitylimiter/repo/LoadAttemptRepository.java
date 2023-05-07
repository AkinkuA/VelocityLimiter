package com.vault.velocitylimiter.repo;

import com.vault.velocitylimiter.model.Customer;
import com.vault.velocitylimiter.model.LoadAttempt;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface LoadAttemptRepository extends CrudRepository<LoadAttempt, Long> {

    @Query("SELECT SUM(la.loadAmount) FROM LoadAttempt la WHERE la.accepted = true AND la.customer = :customer AND la.time BETWEEN :startOfDay AND :currentTime")
    Float getDailyLoadAmountByCustomerAndTime(@Param("customer") Customer customer, @Param("startOfDay") LocalDateTime startOfDay, @Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT COUNT(la) FROM LoadAttempt la WHERE la.accepted = true AND la.customer = :customer AND la.time BETWEEN :startOfDay AND :currentTime")
    int getDailyLoadCountByCustomerAndTime(@Param("customer") Customer customer, @Param("startOfDay") LocalDateTime startOfDay, @Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT SUM(la.loadAmount) FROM LoadAttempt la WHERE la.accepted = true AND la.customer = :customer AND la.time BETWEEN :startOfWeek AND :currentTime")
    Float getWeeklyLoadAmountByCustomerAndTime(@Param("customer") Customer customer, @Param("startOfWeek") LocalDateTime startOfWeek, @Param("currentTime") LocalDateTime currentTime);

}
