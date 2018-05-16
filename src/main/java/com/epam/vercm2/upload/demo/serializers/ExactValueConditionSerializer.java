package com.epam.vercm2.upload.demo.serializers;

import com.epam.vercm2.upload.demo.model.ExactValueCondition;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class ExactValueConditionSerializer extends JsonSerializer<ExactValueCondition> {

    @Override
    public void serialize(ExactValueCondition condition, JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {

        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField(
                condition.getAttribute(), condition.getValue());
        jsonGenerator.writeEndObject();
    }
}