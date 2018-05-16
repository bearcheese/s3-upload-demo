package com.epam.vercm2.upload.demo;

public class MatcherCondition extends Condition {

    private MatchType type;
    private String attribute;
    private String pattern;

    private MatcherCondition() {
    }

    private MatcherCondition(MatchType type, String attribute, String pattern) {
        this.type = type;
        this.attribute = attribute;
        this.pattern = pattern;
    }

    public MatchType getType() {
        return type;
    }

    public String getAttribute() {
        return attribute;
    }

    public String getPattern() {
        return pattern;
    }

    public static MatcherCondition matcherCondition(MatchType type, String attribute, String pattern) {
        return new MatcherCondition(type, attribute, pattern);
    }

}
