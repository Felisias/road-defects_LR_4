package com.roaddefects.dto;

import java.time.LocalDateTime;

public class ChangeLogDto {

    private final Long id;
    private final LocalDateTime changedAt;
    private final String changeType;
    private final String entity;
    private final String entityId;
    private final String data;

    public ChangeLogDto(Long id, LocalDateTime changedAt, String changeType, String entity, String entityId,
                        String data) {
        this.id = id;
        this.changedAt = changedAt;
        this.changeType = changeType;
        this.entity = entity;
        this.entityId = entityId;
        this.data = data;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public String getChangeType() {
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
}
