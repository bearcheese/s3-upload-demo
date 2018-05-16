package com.epam.vercm2.upload.demo;

import com.epam.vercm2.upload.demo.model.Policy;
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

import static com.epam.vercm2.upload.demo.model.ExactValueCondition.exactValueCondition;
import static com.epam.vercm2.upload.demo.model.MatchType.STARTS_WITH;
import static com.epam.vercm2.upload.demo.model.MatcherCondition.matcherCondition;

@JsonTest
@RunWith(SpringRunner.class)
public class PolicySerializationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void serialize() throws JsonProcessingException {
        Policy policy = new Policy();
        policy.setExpiration(LocalDateTime.of(2018, 5, 16, 9, 35, 17).atZone(ZoneId.of("UTC")));
        policy.addCondition(exactValueCondition("bucket", "formuploaddemobucket"));
        policy.addCondition(matcherCondition(STARTS_WITH, "$key", "test/"));

        String expectedJson = "{\"expiration\":\"2018-05-16T09:35:17Z\",\"conditions\":[{\"bucket\":\"formuploaddemobucket\"},[\"starts-with\",\"$key\",\"test/\"]]}";
        String policyJson = objectMapper.writeValueAsString(policy);

        Assertions.assertThat(policyJson).isEqualTo(expectedJson);
    }

}
