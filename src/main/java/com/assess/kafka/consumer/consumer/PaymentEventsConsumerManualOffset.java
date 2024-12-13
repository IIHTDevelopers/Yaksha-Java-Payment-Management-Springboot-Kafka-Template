package com.assess.kafka.consumer.consumer;

import com.assess.kafka.consumer.entity.Payment;
import com.assess.kafka.consumer.service.KafkaPaymentConsumerService;
import com.assess.kafka.consumer.service.PaymentEventsService;
import com.assess.kafka.producer.domain.PaymentEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PaymentEventsConsumerManualOffset implements AcknowledgingMessageListener<String, PaymentEvent> {
    @Autowired
    private KafkaPaymentConsumerService paymentConsumerService;
    @Autowired
    private PaymentEventsService paymentEventsService;

    @Override
    @KafkaListener(topics = "${spring.kafka.payment.topic.create-payment}", groupId = "${spring.kafka.payment.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void onMessage(ConsumerRecord<String, PaymentEvent> consumerRecord, Acknowledgment acknowledgment) {
        log.info("PaymentEventsConsumerManualOffset Received message from Kafka: " + consumerRecord.value());
            if (consumerRecord.offset() % 2 == 0) {
                throw new RuntimeException("This is really odd.");
            }
            processMessage(consumerRecord.value());
            acknowledgment.acknowledge();
    }

    private void processMessage(PaymentEvent paymentEvent) {
        Payment dbPayment = paymentConsumerService.listenCreatePayment(paymentEvent.getPaymentDto());
        paymentEventsService.listenCreatePaymentEvent(paymentEvent, dbPayment);
    }

}