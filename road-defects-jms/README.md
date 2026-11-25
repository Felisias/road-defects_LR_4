# road-defects-jms

Лаба 4, JMS (продолжение лабы 3). Журнал изменений в таблице change_log и оповещения на почту.

Брокер Artemis встроенный, топик defects.changes.
Письма уходят при изменении дефектов с типом из notify.types (по умолчанию d01).

```
psql -U postgres -d road_defects -f sql/03_change_log.sql
mvn spring-boot:run
```

Почта: spring.mail.username / spring.mail.password и notify.to в application.properties.
Если не заполнено, письмо выводится в лог.

http://localhost:8081/log
