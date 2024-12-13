package com.assess.kafka.consumer.service;


import com.assess.kafka.consumer.entity.Payment;
import com.assess.kafka.consumer.jpa.PaymentRepository;
import com.assess.kafka.producer.domain.PaymentDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class KafkaPaymentConsumerService {
    @Autowired
    private PaymentRepository paymentRepository;

    public Payment listenCreatePayment(PaymentDto paymentDto) {
        Payment dbPayment = Payment.builder()
                .orderId(paymentDto.getOrderId())
                .paymentMethod(paymentDto.getPaymentMethod())
                .totalAmount(paymentDto.getTotalAmount())
                .creditCardNumber(paymentDto.getCreditCardNumber())
                .build();
        return paymentRepository.save(dbPayment);
    }

}
