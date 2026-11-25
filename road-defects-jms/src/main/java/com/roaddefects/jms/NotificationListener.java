package com.roaddefects.jms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roaddefects.entity.ChangeType;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/** MDP оповещений по почте. */
@Component
public class NotificationListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationListener.class);

    private final ObjectProvider<JavaMailSender> mailSender;
    private final ObjectMapper objectMapper;
    private final String from;
    private final String[] to;
    private final Set<String> types;
    private final String baseUrl;

    public NotificationListener(ObjectProvider<JavaMailSender> mailSender, ObjectMapper objectMapper,
                                @Value("${spring.mail.username:}") String from,
                                @Value("${notify.to:}") String to,
                                @Value("${notify.types:d01}") String types,
                                @Value("${app.base-url:http://localhost:8081}") String baseUrl) {
        this.mailSender = mailSender;
        this.objectMapper = objectMapper;
        this.from = from;
        this.to = split(to).toArray(String[]::new);
        this.types = split(types);
        this.baseUrl = baseUrl;
    }

    @JmsListener(destination = "${app.jms.topic}", selector = "entity = 'Defect'")
    public void onMessage(Message message) throws JMSException, JsonProcessingException {
        ChangeMessage change;
        try {
            change = ChangeMessage.from(message);
        } catch (IllegalArgumentException e) {
            log.warn("Сообщение пропущено: {}", e.getMessage());
            return;
        }

        JsonNode data = objectMapper.readTree(change.getData());
        String typeCode = data.path("typeCode").asText();
        if (!types.contains(typeCode)) {
            return;
        }

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(from);
        mail.setTo(to);
        mail.setSubject("Дефект " + typeCode + " (" + data.path("typeName").asText() + ") "
                + action(change.getChangeType()) + ", id " + change.getEntityId());
        mail.setText(text(change, data));

        JavaMailSender sender = mailSender.getIfAvailable();
        if (sender == null || from.isBlank() || to.length == 0) {
            log.info("Почта не настроена, письмо не отправлено:\n{}\n{}", mail.getSubject(), mail.getText());
            return;
        }
        try {
            sender.send(mail);
            log.info("Письмо отправлено на {}: {}", String.join(", ", to), mail.getSubject());
        } catch (MailException e) {
            log.error("Не удалось отправить письмо: {}", e.getMessage());
        }
    }

    private String text(ChangeMessage change, JsonNode data) {
        StringBuilder text = new StringBuilder();
        text.append("Дефект ").append(action(change.getChangeType())).append(".\n\n");
        text.append("ID: ").append(change.getEntityId()).append('\n');
        text.append("Тип: ").append(data.path("typeCode").asText())
                .append(" (").append(data.path("typeName").asText()).append(")\n");
        text.append("Координаты: ").append(data.path("latitude").asText())
                .append(", ").append(data.path("longitude").asText()).append('\n');
        text.append("Обнаружен: ").append(data.path("detectedAt").asText()).append('\n');
        text.append("Время изменения: ").append(change.getTime().withNano(0)).append('\n');
        if (change.getChangeType() != ChangeType.DELETE) {
            text.append('\n').append(baseUrl).append("/api/defects/").append(change.getEntityId()).append('\n');
        }
        return text.toString();
    }

    private static String action(ChangeType type) {
        return switch (type) {
            case INSERT -> "добавлен";
            case UPDATE -> "изменён";
            case DELETE -> "удалён";
        };
    }

    private static Set<String> split(String value) {
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
