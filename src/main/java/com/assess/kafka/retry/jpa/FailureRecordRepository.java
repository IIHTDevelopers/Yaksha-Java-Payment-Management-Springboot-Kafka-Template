package com.assess.kafka.retry.jpa;

import com.assess.kafka.retry.entity.FailureRecord;
import com.assess.kafka.retry.entity.FailureStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface FailureRecordRepository extends JpaRepository<FailureRecord, Long> {

    List<FailureRecord> findAllByStatus(FailureStatus status);
}