package com.roaddefects.jms;

import com.roaddefects.entity.ChangeType;

public record EntityChangedEvent(ChangeType type, String entity, String entityId, Object data) {
}
