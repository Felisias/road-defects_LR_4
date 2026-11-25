package com.roaddefects.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class XslXmlHttpMessageConverter extends MappingJackson2XmlHttpMessageConverter {

    public static final String XSL_HREF = "/xsl/view.xsl";

    private static final byte[] HEADER = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<?xml-stylesheet type=\"text/xsl\" href=\"" + XSL_HREF + "\"?>\n")
            .getBytes(StandardCharsets.UTF_8);

    public XslXmlHttpMessageConverter(ObjectMapper xmlMapper) {
        super(xmlMapper);
        setSupportedMediaTypes(List.of(
                new MediaType("application", "xml", StandardCharsets.UTF_8),
                new MediaType("text", "xml", StandardCharsets.UTF_8)));
    }

    @Override
    protected void writeInternal(Object object, Type type, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        outputMessage.getBody().write(HEADER);
        super.writeInternal(object, type, outputMessage);
    }
}
