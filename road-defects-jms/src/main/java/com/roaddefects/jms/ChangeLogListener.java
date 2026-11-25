package com.roaddefects.jms;

import com.roaddefects.entity.ChangeLog;
import com.roaddefects.service.ChangeLogService;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/** MDP журналирования. */
@Component
public class ChangeLogListener {

    private static final Logger log = LoggerFactory.getLogger(ChangeLogListener.class);

    private final ChangeLogService changeLogService;

    public ChangeLogListener(ChangeLogService changeLogService) {
        this.changeLogService = changeLogService;
    }

    @JmsListener(destination = "${app.jms.topic}")
    public void onMessage(Message message) throws JMSException {
        ChangeMessage change;
        try {
            change = ChangeMessage.from(message);
        } catch (IllegalArgumentException e) {
            log.warn("Сообщение пропущено: {}", e.getMessage());
            return;
        }
        changeLogService.save(new ChangeLog(change.getTime(), change.getChangeType(), change.getEntity(),
                change.getEntityId(), change.getData()));
    }
}
