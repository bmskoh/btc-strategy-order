package com.gmail.bmskoh.strategyapp.processors;

public class TriggeringRuleException extends Exception {
    private static final long serialVersionUID = -8357419336490096752L;

    public TriggeringRuleException(String errMsg) {
        super(errMsg);
    }
}