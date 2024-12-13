package com.assess.kafka.producer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class PaymentEvent {
    private String eventId;
    private PaymentEventType eventType;
    private PaymentDto paymentDto;
    private String eventDetails;

    public PaymentEvent() {

    }
}

