package com.gmail.bmskoh.strategyapp.processors;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.gmail.bmskoh.strategyapp.model.TriggeringRule;
import com.gmail.bmskoh.strategyapp.model.TrailingStopRule;

import org.springframework.stereotype.Component;

/**
 * This class loads order triggering rules.
 *
 * Rules are hardcoded for testing at the moment.
 *
 * These rules are supposed to be loaded from database, or other services.
 *
 */
@Component
public class TriggeringRuleLoader {

    public List<TriggeringRule> loadTriggeringRules() {
        List<TriggeringRule> triggeringRules = new LinkedList<>(Arrays.asList(
                new TrailingStopRule("order1", "ETH-BTC", 0.00001, TrailingStopRule.pointType.point,
                        TrailingStopRule.directionType.below),
                new TrailingStopRule("order2", "ETH-BTC", 1, TrailingStopRule.pointType.point,
                        TrailingStopRule.directionType.above)));

        return triggeringRules;
    }
}