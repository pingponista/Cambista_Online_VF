package com.cambistaonline.order.adapters.outbound.kafka;

import com.cambistaonline.common.security.SecurityContextMetadata;
import com.cambistaonline.common.tracing.CorrelationContext;
import com.cambistaonline.order.domain.events.OrderCreatedEvent;
import com.cambistaonline.order.domain.model.CurrencyType;
import com.cambistaonline.order.domain.model.OperationType;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class KafkaOrderEventPublisherAdapterTracingTest {

    @AfterEach
    void tearDown() {
        CorrelationContext.clearContext();
    }

    @Test
    @DisplayName("Kafka Producer: Debe inyectar Correlation ID y estado MFA en los RecordHeaders")
    void shouldPropagateCorrelationIdAndMfaHeadersInKafkaRecord() {
        @SuppressWarnings("unchecked")
        KafkaTemplate<String, Object> kafkaTemplate = mock(KafkaTemplate.class);
        KafkaOrderEventPublisherAdapter publisher = new KafkaOrderEventPublisherAdapter(kafkaTemplate);

        String testCorrelationId = "cid-trace-test-999";
        String userEmail = "financiero@cambistaonline.pe";
        CorrelationContext.setContext(testCorrelationId, userEmail, true);

        OrderCreatedEvent event = new OrderCreatedEvent(
                "ORD-TRACE-100",
                userEmail,
                OperationType.COMPRA,
                CurrencyType.USD,
                CurrencyType.PEN,
                BigDecimal.valueOf(1500.00),
                BigDecimal.valueOf(5670.00),
                BigDecimal.valueOf(3.7800)
        );

        publisher.publishOrderCreated(event);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<ProducerRecord<String, Object>> captor = ArgumentCaptor.forClass(ProducerRecord.class);
        verify(kafkaTemplate, times(1)).send(captor.capture());

        ProducerRecord<String, Object> record = captor.getValue();
        assertNotNull(record);
        assertEquals("cambista.orders.created", record.topic());
        assertEquals("ORD-TRACE-100", record.key());

        var cidHeader = record.headers().lastHeader(SecurityContextMetadata.KAFKA_HEADER_CORRELATION_ID);
        var mfaHeader = record.headers().lastHeader(SecurityContextMetadata.KAFKA_HEADER_MFA_VERIFIED);
        var userHeader = record.headers().lastHeader(SecurityContextMetadata.KAFKA_HEADER_USER_EMAIL);

        assertNotNull(cidHeader, "Header correlation_id debe estar presente en el mensaje Kafka");
        assertNotNull(mfaHeader, "Header x_mfa_verified debe estar presente en el mensaje Kafka");
        assertNotNull(userHeader, "Header x_user_email debe estar presente en el mensaje Kafka");

        assertEquals(testCorrelationId, new String(cidHeader.value(), StandardCharsets.UTF_8));
        assertEquals("true", new String(mfaHeader.value(), StandardCharsets.UTF_8));
        assertEquals(userEmail, new String(userHeader.value(), StandardCharsets.UTF_8));
    }
}