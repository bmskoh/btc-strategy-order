package com.gmail.bmskoh.strategyapp.repositories;

import javax.annotation.PostConstruct;

import com.gmail.bmskoh.strategyapp.model.TrailingStopRule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * To add test data for dev profile
 */
@Component
@Profile("dev")
public class InitTempData {
    @Autowired
    TriggeringRuleRepository triggeringRuleRepository;

    // This method is going to add fake TrailingStopRule data
    @PostConstruct
    void insertTestData() {

        TrailingStopRule rule1 = new TrailingStopRule(null, "ETH-BTC", 0.00001, TrailingStopRule.pointType.point,
                TrailingStopRule.directionType.below);
        TrailingStopRule rule2 = new TrailingStopRule(null, "ETH-BTC", 1, TrailingStopRule.pointType.point,
                TrailingStopRule.directionType.above);

        triggeringRuleRepository.save(rule1);
        triggeringRuleRepository.save(rule2);
    }
}