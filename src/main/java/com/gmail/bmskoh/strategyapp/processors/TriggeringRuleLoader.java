package com.gmail.bmskoh.strategyapp.processors;

import java.util.LinkedList;
import java.util.List;

import com.gmail.bmskoh.strategyapp.model.TriggeringRule;
import com.gmail.bmskoh.strategyapp.repositories.TriggeringRuleRepository;

import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    TriggeringRuleRepository triggeringRuleRepository;

    public List<TriggeringRule> loadTriggeringRules() {
        final List<TriggeringRule> triggeringRules = new LinkedList<TriggeringRule>();
        triggeringRuleRepository.findAll().forEach(rule -> triggeringRules.add(rule));
        return triggeringRules;
    }
}