package com.assess.kafka.retry.schedular;


import com.assess.kafka.retry.service.FailureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class RetryScheduler {

    private final FailureService failureService;

    @Autowired
    public RetryScheduler(FailureService failureService) {
        this.failureService = failureService;
    }

    @Scheduled(fixedDelay = 5000)
    public void retryFailedOperations() {
        failureService.retryFailedOperations();
    }
}