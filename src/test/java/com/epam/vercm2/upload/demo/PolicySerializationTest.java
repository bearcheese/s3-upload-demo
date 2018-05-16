package com.epam.vercm2.upload.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.time.ZoneId;

@JsonTest
@RunWith(SpringRunner.class)
public class PolicySerializationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void serialize() throws JsonProcessingException {
        Policy policy = new Policy();
        policy.setExpiration(LocalDateTime.of(2018, 5, 16, 9, 35, 17).atZone(ZoneId.of("UTC")));
        policy.addCondition(new ExactValueCondition("bucket", "formuploaddemobucket"));
        policy.addCondition(new MatcherCondition("starts-with", "$key", "test/"));

        String expectedJson = "{\"expiration\":\"2018-05-16T09:35:17Z\",\"conditions\":[{\"bucket\":\"formuploaddemobucket\"},[\"starts-with\",\"$key\",\"test/\"]]}";
        String policyJson = objectMapper.writeValueAsString(policy);

        Assertions.assertThat(policyJson).isEqualTo(expectedJson);
    }

}
