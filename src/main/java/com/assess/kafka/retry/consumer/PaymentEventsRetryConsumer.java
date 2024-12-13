package com.assess.kafka.retry.consumer;


import com.assess.kafka.producer.domain.PaymentEvent;
import com.assess.kafka.retry.entity.FailureRecord;
import com.assess.kafka.retry.entity.FailureStatus;
import com.assess.kafka.retry.jpa.FailureRecordRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.stereotype.Service;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class PaymentEventsRetryConsumer {

    @Value(value = "${spring.kafka.payment.bootstrap-servers:")
    private String bootstrapServer;
    @Value(value = "${spring.kafka.payment.consumer.group-id:")
    private String groupId;
    @Autowired
    private FailureRecordRepository failureRecordRepository;

    @Autowired
    private ObjectMapper objectMapper;


    @Bean
    public ConsumerFactory<String, PaymentEvent> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(),
                new JsonDeserializer<>(PaymentEvent.class));
    }

    @Bean("kafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, PaymentEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PaymentEvent> concurrentKafkaListenerContainerFactory
                = new ConcurrentKafkaListenerContainerFactory<>();

        concurrentKafkaListenerContainerFactory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        concurrentKafkaListenerContainerFactory.setConsumerFactory(consumerFactory());
        concurrentKafkaListenerContainerFactory.setCommonErrorHandler(getDefaultErrorHandler());
        return concurrentKafkaListenerContainerFactory;
    }

    private DefaultErrorHandler getDefaultErrorHandler() {
        return new DefaultErrorHandler((record, exception) -> {

            try {
                FailureRecord failureRecord = FailureRecord.builder().message(objectMapper.writeValueAsString(record.value()))
                        .exception(exception.getMessage())
                        .topic(record.topic())
                        .status(FailureStatus.FAILED).consumerOffset(record.offset()).build();
                failureRecordRepository.save(failureRecord);
                log.error("Saved the failed message to db {}", failureRecord);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }, new FixedBackOff(10000L, 2L));
    }
}

