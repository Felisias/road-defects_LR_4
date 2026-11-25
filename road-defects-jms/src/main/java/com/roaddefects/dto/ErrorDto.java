package com.roaddefects.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JacksonXmlRootElement(localName = "error")
public class ErrorDto {

    private final int status;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "message")
    private final List<String> messages;

    public ErrorDto(int status, List<String> messages) {
        this.status = status;
        this.messages = messages;
    }

    public int getStatus() {
        return status;
    }

    public List<String> getMessages() {
        return messages;
    }
}
