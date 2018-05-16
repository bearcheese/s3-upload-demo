package com.epam.vercm2.upload.demo;

public enum MatchType {

    STARTS_WITH("starts-with");

    public final String type;

    MatchType(String s) {
        this.type = s;
    }
}
