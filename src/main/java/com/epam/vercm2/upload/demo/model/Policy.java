package com.epam.vercm2.upload.demo.model;

import com.epam.vercm2.upload.demo.model.Condition;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class Policy {

    private ZonedDateTime expiration;

    private List<Condition> conditions = new ArrayList<>();

    public ZonedDateTime getExpiration() {
        return expiration;
    }

    public void setExpiration(ZonedDateTime expiration) {
        this.expiration = expiration;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }

    public void addCondition(Condition condition) {
        conditions.add(condition);
    }
}
