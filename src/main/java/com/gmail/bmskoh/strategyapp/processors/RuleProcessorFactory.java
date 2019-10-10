package com.gmail.bmskoh.strategyapp.processors;

import com.gmail.bmskoh.strategyapp.model.TrailingStopRule;
import com.gmail.bmskoh.strategyapp.model.TriggeringRule;
import com.gmail.bmskoh.strategyapp.model.TriggeringRule.TriggeringRuleType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RuleProcessorFactory {

    /**
     * Create and return appropriate TriggeringRuleProcessor according to given
     * TriggeringRule' type
     *
     * @param triggeringRule
     * @return
     */
    public TriggeringRuleProcessor createTriggeringRuleProcessor(TriggeringRule triggeringRule) {
        if (triggeringRule.getRuleType() == TriggeringRuleType.trailingStop) {
            return new TrailingStopRuleProcessor((TrailingStopRule) triggeringRule);
        }
        // TODO: support StopLossRuleProcessor

        // Shouldn't reach here.
        Logger logger = LoggerFactory.getLogger(RuleProcessorFactory.class);
        logger.error("Cannot find TriggeringRuleProcessor for {}.", triggeringRule.getRuleType());
        return null;
    }
}