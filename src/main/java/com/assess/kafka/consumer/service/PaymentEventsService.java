package com.assess.kafka.consumer.service;


import com.assess.kafka.consumer.entity.Payment;
import com.assess.kafka.consumer.entity.PaymentEvent;
import com.assess.kafka.consumer.entity.PaymentEventType;
import com.assess.kafka.consumer.jpa.PaymentEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.assess.kafka.consumer.entity.PaymentEventType.PAYMENT_CANCELLED;
import static com.assess.kafka.consumer.entity.PaymentEventType.PAYMENT_SUCCESS;

@Service
public class PaymentEventsService {

    private final PaymentEventRepository paymentEventRepository;

    @Autowired
    public PaymentEventsService(PaymentEventRepository paymentEventRepository) {
        this.paymentEventRepository = paymentEventRepository;
    }


    public void listenCreatePaymentEvent(com.assess.kafka.producer.domain.PaymentEvent paymentEvent, Payment dbPayment) {
        PaymentEvent event = PaymentEvent.builder()
                .eventType(mapEventType(paymentEvent.getEventType()))
                .eventDetails(paymentEvent.getEventDetails())
                .paymentId(dbPayment.getId())
                .build();
        paymentEventRepository.save(event);
    }

    private PaymentEventType mapEventType(com.assess.kafka.producer.domain.PaymentEventType eventType) {
        return switch (eventType) {
            case PAYMENT_SUCCESS -> PAYMENT_SUCCESS;
            case PAYMENT_CANCELLED -> PAYMENT_CANCELLED;
        };
    }
}
