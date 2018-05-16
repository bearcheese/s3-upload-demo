package com.epam.vercm2.upload.demo;

public class ExactValueCondition extends Condition {

    private String attribute;
    private String value;

    public ExactValueCondition() {
    }

    public ExactValueCondition(String attribute, String value) {
        this.attribute = attribute;
        this.value = value;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
