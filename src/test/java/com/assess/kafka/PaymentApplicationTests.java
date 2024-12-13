package com.assess.kafka;

import com.assess.kafka.consumer.consumer.PaymentEventsConsumerManualOffset;
import com.assess.kafka.producer.controller.PaymentController;
import com.assess.kafka.producer.domain.PaymentDto;
import com.assess.kafka.producer.domain.PaymentEvent;
import com.assess.kafka.producer.domain.PaymentEventType;
import com.assess.kafka.producer.producer.PaymentEventProducer;
import com.assess.kafka.testutils.MasterData;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static com.assess.kafka.testutils.TestUtils.*;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;


@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
@EnableKafka
public class PaymentApplicationTests {
    @Autowired
    private MockMvc mockMvc;

    @Mock
    private KafkaTemplate<String, PaymentEvent> kafkaTemplate;


    @MockBean
    private PaymentEventProducer paymentEventProducer;

    @MockBean
    private PaymentEventsConsumerManualOffset paymentEventsConsumerManualOffset;

    @Test
    public void test_BookControllerSendBook() throws Exception {
        final int[] count = new int[1];
        PaymentDto paymentDto = PaymentDto.builder()
                .creditCardNumber("1234")
                .orderId(1L)
                .paymentMethod("DEBIT")
                .totalAmount(10d)
                .build();
        PaymentEvent paymentEvent = PaymentEvent.
                builder()
                .eventDetails("Create Payment")
                .eventType(PaymentEventType.PAYMENT_SUCCESS)
                .paymentDto(paymentDto)
                .build();

        when(paymentEventProducer.sendCreatePaymentEvent(paymentDto, "Payment Success")).then(new Answer<PaymentEvent>() {

            @Override
            public PaymentEvent answer(InvocationOnMock invocation) throws Throwable {
                // TODO Auto-generated method stub
                count[0]++;
                return paymentEvent;
            }
        });

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/payments/")
                .content(MasterData.asJsonString(paymentDto)).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        yakshaAssert(currentTest(), count[0] == 1, businessTestFile);

    }

    @Test
    public void testSendBook() throws Exception {
        PaymentDto paymentDto = PaymentDto.builder()
                .creditCardNumber("1234")
                .orderId(1L)
                .paymentMethod("DEBIT")
                .totalAmount(10d)
                .build();
        PaymentEvent paymentEvent = PaymentEvent.
                builder()
                .eventDetails("Create Payment")
                .eventType(PaymentEventType.PAYMENT_SUCCESS)
                .paymentDto(paymentDto)
                .build();
        try {
            CompletableFuture<SendResult<String, PaymentEvent>> mockFuture = mock(CompletableFuture.class);
            when(kafkaTemplate.send("create-payment", paymentEvent.getEventType().toString(), paymentEvent)).thenReturn(mockFuture);
            this.paymentEventProducer.sendCreatePaymentEvent(paymentDto, "payment Created");
            yakshaAssert(currentTest(), true, businessTestFile);
        } catch (Exception ex) {
            yakshaAssert(currentTest(), false, businessTestFile);
        }

    }

    @Test
    @Disabled
    public void testConsumeBook() {
        PaymentDto paymentDto = PaymentDto.builder()
                .creditCardNumber("1234")
                .orderId(1L)
                .paymentMethod("DEBIT")
                .totalAmount(10d)
                .build();
        PaymentEvent paymentEvent = PaymentEvent.
                builder()
                .eventDetails("Create Payment")
                .eventType(PaymentEventType.PAYMENT_SUCCESS)
                .paymentDto(paymentDto)
                .build();

        kafkaTemplate.send("create-payment", paymentEvent.getEventType().toString(), paymentEvent);


        await().atMost(5, SECONDS).untilAsserted(() -> {
            ConsumerRecord<String, PaymentEvent> mockRecord = mock(ConsumerRecord.class);

            Acknowledgment mockAcknowledgment = mock(Acknowledgment.class);
            paymentEventsConsumerManualOffset.onMessage(mockRecord, mockAcknowledgment);
            yakshaAssert(currentTest(), true, businessTestFile);

        });
    }


}
