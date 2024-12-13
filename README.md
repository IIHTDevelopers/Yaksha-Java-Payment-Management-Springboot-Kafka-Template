### Payment Application

- Provides payment operation like alloted/cancellation using Apache kafka.

### Payment Application Producer Module

- ** Create payment using api endpoint /api/payments/ and publish payment event message to kafka using topic create-payment.

### Payment Application Consumer Module

- ** PaymentEventsConsumerManualOffset Listening payment event and store details in database.
- Incase of any failure happen in PaymentEventsConsumerManualOffset then New message has been published to
  retry-create-payment topic and store failure record in database

### Payment Application Retry Module

- ** PaymentEventsRetryConsumer is Responsible to listen failed consumed message.
- A scheduler RetryScheduler is running wit 5 sec internal to fetch all failure record from database and publish again
  on kafka

---
