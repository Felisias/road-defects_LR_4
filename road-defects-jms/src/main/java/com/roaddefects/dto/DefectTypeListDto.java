package com.roaddefects.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JacksonXmlRootElement(localName = "types")
public class DefectTypeListDto {

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "type")
    private final List<DefectTypeDto> types;

    public DefectTypeListDto(List<DefectTypeDto> types) {
        this.types = types;
    }

    public List<DefectTypeDto> getTypes() {
        return types;
    }
}
