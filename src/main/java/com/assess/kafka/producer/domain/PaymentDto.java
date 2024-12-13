package com.assess.kafka.producer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class PaymentDto {
    private Long paymentId;
    private Long userId;
    private Long orderId;
    private double totalAmount;
    private String paymentMethod;
    private String creditCardNumber;

    public PaymentDto() {
    }
}
