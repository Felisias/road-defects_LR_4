package com.roaddefects.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JacksonXmlRootElement(localName = "defects")
public class DefectListDto {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String type;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "defect")
    private final List<DefectDto> defects;

    public DefectListDto(String type, List<DefectDto> defects) {
        this.type = type;
        this.defects = defects;
    }

    public String getType() {
        return type;
    }

    public List<DefectDto> getDefects() {
        return defects;
    }
}
