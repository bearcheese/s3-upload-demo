package com.epam.vercm2.upload.demo;

public class ExactValueCondition extends Condition {

    private String attribute;
    private String value;

    private ExactValueCondition() {
    }

    private ExactValueCondition(String attribute, String value) {
        this.attribute = attribute;
        this.value = value;
    }

    public String getAttribute() {
        return attribute;
    }

    public String getValue() {
        return value;
    }

    public static ExactValueCondition exactValueCondition(String attribute, String value) {
        return new ExactValueCondition(attribute, value);
    }
}
