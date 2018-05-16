package com.epam.vercm2.upload.demo.serializers;

import com.epam.vercm2.upload.demo.model.MatcherCondition;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class MatcherConditionSerializer extends JsonSerializer<MatcherCondition> {
    @Override
    public void serialize(MatcherCondition condition, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartArray();

        jsonGenerator.writeString(condition.getType().type);
        jsonGenerator.writeString(condition.getAttribute());
        jsonGenerator.writeString(condition.getPattern());

        jsonGenerator.writeEndArray();
    }
}
