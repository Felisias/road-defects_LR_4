package com.roaddefects.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JacksonXmlRootElement(localName = "type")
@JsonPropertyOrder({"code", "name"})
public class DefectTypeDto {

    @NotBlank(message = "Укажите код")
    @Size(max = 10, message = "Код не длиннее 10 символов")
    private String code;

    @NotBlank(message = "Укажите название")
    @Size(max = 100, message = "Название не длиннее 100 символов")
    private String name;

    public DefectTypeDto() {
    }

    public DefectTypeDto(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
