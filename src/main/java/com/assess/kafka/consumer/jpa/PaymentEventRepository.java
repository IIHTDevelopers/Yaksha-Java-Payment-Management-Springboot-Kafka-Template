package com.assess.kafka.consumer.jpa;

import com.assess.kafka.consumer.entity.PaymentEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentEventRepository extends JpaRepository<PaymentEvent, Long> {
    // Define custom query methods if needed
}