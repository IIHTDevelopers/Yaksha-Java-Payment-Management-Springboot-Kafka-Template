package com.assess.kafka.retry.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
public class FailureRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 10000)
    private String message;

    @Column
    private String topic;

    @Column
    private Long consumerOffset;

    @Column(length = 10000)
    private String exception;
    @Column
    private FailureStatus status;


    @Column
    private LocalDateTime failureTime;
    public FailureRecord() {
    }


}