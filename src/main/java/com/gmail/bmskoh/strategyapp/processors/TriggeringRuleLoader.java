package com.gmail.bmskoh.strategyapp.processors;

import java.util.LinkedList;
import java.util.List;

import com.gmail.bmskoh.strategyapp.model.TriggeringRule;
import com.gmail.bmskoh.strategyapp.repositories.ITriggeringRuleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class loads order triggering rules.
 *
 * Still a temporary implementation. This class was to provide temporary test data in the beginning.
 * 
 * Once ITriggeringRuleManager is implemented in OrderProcessManager or other separate class,
 * we may consider removing ITriggeringRuleLoader.
 */
@Component
public class TriggeringRuleLoader implements ITriggeringRuleLoader {
    @Autowired
    ITriggeringRuleRepository triggeringRuleRepository;

    public List<TriggeringRule> loadTriggeringRules() {
        final List<TriggeringRule> triggeringRules = new LinkedList<TriggeringRule>();
        triggeringRuleRepository.findAll().forEach(rule -> triggeringRules.add(rule));
        return triggeringRules;
    }
}