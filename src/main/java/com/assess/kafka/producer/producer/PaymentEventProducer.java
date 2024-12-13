package com.assess.kafka.producer.producer;

import com.assess.kafka.producer.domain.PaymentDto;
import com.assess.kafka.producer.domain.PaymentEvent;
import com.assess.kafka.producer.domain.PaymentEventType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class PaymentEventProducer {

    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    @Value("${spring.kafka.payment.topic.create-payment}")
    private String topic;

    @Autowired
    public PaymentEventProducer(KafkaTemplate<String, PaymentEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public PaymentEvent sendCreatePaymentEvent(PaymentDto paymentDto, String eventDetails) {
        PaymentEvent paymentEvent = PaymentEvent.
                builder()
                .paymentDto(paymentDto)
                .eventType(PaymentEventType.PAYMENT_SUCCESS)
                .eventDetails(eventDetails + paymentDto.getOrderId())
                .build();
        try {
            CompletableFuture<SendResult<String, PaymentEvent>> sendResultCompletableFuture = kafkaTemplate.send(topic, paymentEvent.getEventType().toString(), paymentEvent);
           return sendResultCompletableFuture.get().getProducerRecord().value();
        } catch (Exception e) {
            log.debug("Error occurred while publishing message due to " + e.getMessage());
        }
        return paymentEvent;
    }
}
