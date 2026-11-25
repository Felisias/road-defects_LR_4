package com.roaddefects.jms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.MapMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/** Отправка в топик после коммита. */
@Component
public class ChangeMessageSender {

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;
    private final String topic;

    public ChangeMessageSender(JmsTemplate jmsTemplate, ObjectMapper objectMapper,
                               @Value("${app.jms.topic}") String topic) {
        this.jmsTemplate = jmsTemplate;
        this.objectMapper = objectMapper;
        this.topic = topic;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void send(EntityChangedEvent event) throws JsonProcessingException {
        String data = objectMapper.writeValueAsString(event.data());
        jmsTemplate.send(topic, session -> {
            MapMessage message = session.createMapMessage();
            message.setString(ChangeMessage.CHANGE_TYPE, event.type().name());
            message.setString(ChangeMessage.ENTITY, event.entity());
            message.setString(ChangeMessage.ENTITY_ID, event.entityId());
            message.setString(ChangeMessage.DATA, data);
            message.setStringProperty(ChangeMessage.ENTITY, event.entity());
            return message;
        });
    }
}
