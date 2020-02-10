package com.gmail.bmskoh.strategyapp.processors;

import java.util.List;

import com.gmail.bmskoh.strategyapp.model.TrailingStopRule;
import com.gmail.bmskoh.strategyapp.model.TriggeringRule;

/**
 * ITriggeringRuleManager is responsible to manager triggering rules.
 */
public interface ITriggeringRuleManager {
    public List<TriggeringRule> getAllTriggeringRules();

    public TrailingStopRule addTrailingRule(TrailingStopRule rule);

    public List<TrailingStopRule> getAllTrailingRules();

    public TrailingStopRule getTrailingRule(String ruleId) throws TriggeringRuleNotFoundException;

    public void updateTrailingRule(TrailingStopRule rule) throws TriggeringRuleNotFoundException;

    public void deleteTrailingRule(String ruleId) throws TriggeringRuleNotFoundException;
}