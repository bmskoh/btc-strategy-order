package com.gmail.bmskoh.strategyapp.model;

public abstract class TriggeringRule {
    public enum TriggeringRuleType {
        trailingStop, stopOrder
    };

    public abstract TriggeringRuleType getRuleType();
}