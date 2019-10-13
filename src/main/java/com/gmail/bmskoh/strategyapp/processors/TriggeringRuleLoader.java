package com.gmail.bmskoh.strategyapp.processors;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.annotation.PostConstruct;

import com.gmail.bmskoh.strategyapp.model.TrailingStopRule;
import com.gmail.bmskoh.strategyapp.model.TriggeringRule;
import com.gmail.bmskoh.strategyapp.repositories.TrailingRuleRepository;

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
    TrailingRuleRepository trailingRepository;

    public List<TriggeringRule> loadTriggeringRules() {

        List<TriggeringRule> trailingRules = StreamSupport.stream(trailingRepository.findAll().spliterator(), false)
                .map(rule -> (TriggeringRule) rule).collect(Collectors.toList());

        return trailingRules;
    }

    @PostConstruct
    void insertTestData() {

        TrailingStopRule rule1 = new TrailingStopRule(null, "ETH-BTC", 0.00001, TrailingStopRule.pointType.point,
                TrailingStopRule.directionType.below);
        TrailingStopRule rule2 = new TrailingStopRule(null, "ETH-BTC", 1, TrailingStopRule.pointType.point,
                TrailingStopRule.directionType.above);

        trailingRepository.save(rule1);
        trailingRepository.save(rule2);
    }
}