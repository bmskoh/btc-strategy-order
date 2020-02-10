package com.gmail.bmskoh.strategyapp.processors;

import com.gmail.bmskoh.strategyapp.model.TriggeringRule;

public interface IRuleProcessorFactory {

    /**
     * Create and return appropriate TriggeringRuleProcessor according to given
     * TriggeringRule' type
     *
     * @param triggeringRule
     * @return An instance of ITriggeringRuleProcessor
     */
    public ITriggeringRuleProcessor createTriggeringRuleProcessor(TriggeringRule triggeringRule);
}