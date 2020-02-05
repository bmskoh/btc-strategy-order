package com.gmail.bmskoh.strategyapp.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import com.gmail.bmskoh.strategyapp.model.TrailingStopRule;
import com.gmail.bmskoh.strategyapp.processors.ITriggeringRuleManager;
import com.gmail.bmskoh.strategyapp.processors.TriggeringRuleNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TrailingRuleControllerTests {

    ITriggeringRuleManager triggeringRuleManager;
    TrailingRuleController controller;

    @BeforeEach
    void init() {
        triggeringRuleManager = mock(ITriggeringRuleManager.class);

        controller = new TrailingRuleController(triggeringRuleManager);
    }

    @Test
    @DisplayName("Test if controller is returning the list of rules as given by triggering rule manager")
    public void testAllTrailingRules() {
        TrailingStopRule[] rules = {
                new TrailingStopRule(null, "ETH-BTC", 0.00001, TrailingStopRule.pointType.point,
                        TrailingStopRule.directionType.below),
                new TrailingStopRule(null, "ETH-BTC", 1, TrailingStopRule.pointType.point,
                        TrailingStopRule.directionType.above) };

        when(triggeringRuleManager.getAllTrailingRules()).thenReturn(Arrays.asList(rules));

        List<TrailingStopRule> allRules = controller.allTrailingRules();

        assertThat(allRules).hasSize(2);
        assertThat(allRules.get(0).getTrailingPoints()).isEqualTo(0.00001);
        assertThat(allRules.get(1).getTrailingPoints()).isEqualTo(1);
    }

    @Test
    @DisplayName("Test if controller is returning the rule object as given by triggering rule manager")
    public void testOneTrailingRule() throws TriggeringRuleNotFoundException {
        when(triggeringRuleManager.getTrailingRule(anyString())).thenReturn(new TrailingStopRule(null, "ETH-FAKE",
                12345, TrailingStopRule.pointType.point, TrailingStopRule.directionType.below));

        TrailingStopRule rule = controller.oneTrailingRule("fakeId");

        assertThat(rule.getMarketId()).isEqualTo("ETH-FAKE");
        assertThat(rule.getTrailingPoints()).isEqualTo(12345);
    }

    @Test
    @DisplayName("TriggeringRuleNotFoundException is supposed to be thrown if no rule with the given ruleId could be found")
    public void testEmptyOneTrailingRule() throws TriggeringRuleNotFoundException {
        when(triggeringRuleManager.getTrailingRule(anyString())).thenThrow(new TriggeringRuleNotFoundException(""));

        assertThatThrownBy(() -> controller.oneTrailingRule("fakeId"))
                .isInstanceOf(TriggeringRuleNotFoundException.class);
    }

    @Test
    @DisplayName("Test if controller uses triggering rule manager's addTrailingRule method to persist the given argument")
    public void testNewTrailingRule() {
        when(triggeringRuleManager.addTrailingRule(any(TrailingStopRule.class)))
                .thenAnswer(mock -> mock.getArguments()[0]);
        TrailingStopRule newRule = new TrailingStopRule(null, "ETH-BTC", 0.00001, TrailingStopRule.pointType.point,
                TrailingStopRule.directionType.below);

        TrailingStopRule rule = controller.newTrailingRule(newRule);

        // Returned rule should be same as given rule
        assertThat(rule).isEqualTo(newRule);
        verify(triggeringRuleManager).addTrailingRule(eq(newRule));
    }

    @Test
    @DisplayName("Controller must update existing rule with new rule that's given as arguement, by calling save method")
    public void testUpdateTrailingRule() throws TriggeringRuleNotFoundException {
        TrailingStopRule newRule = new TrailingStopRule("fakeId", "NEW-MKT", 12345,
                TrailingStopRule.pointType.percentage, TrailingStopRule.directionType.above);

        controller.updateTrailingRule(newRule, "fakeId");

        verify(triggeringRuleManager).updateTrailingRule(argThat(rule -> {
            TrailingStopRule trailingRule = (TrailingStopRule) rule;
            return trailingRule.getMarketId().equals(newRule.getMarketId())
                    && trailingRule.getTrailingDirection() == newRule.getTrailingDirection()
                    && trailingRule.getTrailingType() == newRule.getTrailingType()
                    && trailingRule.getTrailingPoints() == newRule.getTrailingPoints();
        }));
    }

    @Test
    public void testDeleteTrailingRule() throws TriggeringRuleNotFoundException {

        controller.deleteRule("fakeId");

        verify(triggeringRuleManager).deleteTrailingRule(eq("fakeId"));
    }
}