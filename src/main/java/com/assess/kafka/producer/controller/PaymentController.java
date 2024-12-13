package com.assess.kafka.producer.controller;

import com.assess.kafka.producer.domain.PaymentDto;
import com.assess.kafka.producer.domain.PaymentEvent;
import com.assess.kafka.producer.producer.PaymentEventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    @Autowired
    private PaymentEventProducer paymentEventProducer;

    @PostMapping("/")
    public ResponseEntity<?> createPayment(@RequestBody PaymentDto requestedPaymentDto) {
        try {
            PaymentEvent paymentEvent = paymentEventProducer.sendCreatePaymentEvent(requestedPaymentDto, "Payment Process");
            return ResponseEntity.status(HttpStatus.CREATED).body(paymentEvent);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending Payment event");
        }
    }
}
