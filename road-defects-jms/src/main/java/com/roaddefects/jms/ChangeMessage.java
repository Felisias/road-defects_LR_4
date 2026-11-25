package com.roaddefects.jms;

import com.roaddefects.entity.ChangeType;
import jakarta.jms.JMSException;
import jakarta.jms.MapMessage;
import jakarta.jms.Message;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/** MapMessage: changeType, entity, entityId, data. */
public class ChangeMessage {

    public static final String CHANGE_TYPE = "changeType";
    public static final String ENTITY = "entity";
    public static final String ENTITY_ID = "entityId";
    public static final String DATA = "data";

    private final ChangeType changeType;
    private final String entity;
    private final String entityId;
    private final String data;
    private final LocalDateTime time;

    private ChangeMessage(ChangeType changeType, String entity, String entityId, String data, LocalDateTime time) {
        this.changeType = changeType;
        this.entity = entity;
        this.entityId = entityId;
        this.data = data;
        this.time = time;
    }

    public static ChangeMessage from(Message message) throws JMSException {
        if (!(message instanceof MapMessage map)) {
            throw new IllegalArgumentException("ожидался MapMessage, пришёл " + message.getClass().getSimpleName());
        }
        String type = required(map, CHANGE_TYPE);
        ChangeType changeType;
        try {
            changeType = ChangeType.valueOf(type);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("неизвестный тип изменения: " + type);
        }
        long timestamp = message.getJMSTimestamp() > 0 ? message.getJMSTimestamp() : System.currentTimeMillis();
        LocalDateTime time = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
        return new ChangeMessage(changeType, required(map, ENTITY), required(map, ENTITY_ID),
                required(map, DATA), time);
    }

    private static String required(MapMessage map, String name) throws JMSException {
        if (!map.itemExists(name)) {
            throw new IllegalArgumentException("нет поля " + name);
        }
        Object value = map.getObject(name);
        if (!(value instanceof String s) || s.isBlank()) {
            throw new IllegalArgumentException("поле " + name + " должно быть непустой строкой");
        }
        return s;
    }

    public ChangeType getChangeType() {
        return changeType;
    }

    public String getEntity() {
        return entity;
    }

    public String getEntityId() {
        return entityId;
    }

    public String getData() {
        return data;
    }

    public LocalDateTime getTime() {
        return time;
    }
}
