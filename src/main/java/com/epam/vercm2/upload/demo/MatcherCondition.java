package com.epam.vercm2.upload.demo;

public class MatcherCondition extends Condition {

    private String type;
    private String attribute;
    private String pattern;

    public MatcherCondition() {
    }

    public MatcherCondition(String type, String attribute, String pattern) {
        this.type = type;
        this.attribute = attribute;
        this.pattern = pattern;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }


}
