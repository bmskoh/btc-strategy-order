package com.gmail.bmskoh.strategyapp.processors;

import java.util.List;

import com.gmail.bmskoh.strategyapp.model.TriggeringRule;

public interface ITriggeringRuleLoader {
    public List<TriggeringRule> loadTriggeringRules();
}