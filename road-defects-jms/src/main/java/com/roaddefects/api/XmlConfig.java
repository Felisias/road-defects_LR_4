package com.roaddefects.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class XmlConfig {

    @Bean
    public XslXmlHttpMessageConverter xslXmlHttpMessageConverter(Jackson2ObjectMapperBuilder builder) {
        return new XslXmlHttpMessageConverter(builder.createXmlMapper(true).build());
    }
}
