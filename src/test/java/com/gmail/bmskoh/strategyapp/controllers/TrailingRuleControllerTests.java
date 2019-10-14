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
import java.util.Optional;

import com.gmail.bmskoh.strategyapp.model.TrailingStopRule;
import com.gmail.bmskoh.strategyapp.model.TriggeringRule;
import com.gmail.bmskoh.strategyapp.processors.TriggeringRuleLoader;
import com.gmail.bmskoh.strategyapp.repositories.TriggeringRuleRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

public class TrailingRuleControllerTests {

    TriggeringRuleLoader triggeringRuleLoader;
    TriggeringRuleRepository triggeringRuleRepository;
    TrailingRuleController controller;

    @BeforeEach
    void init() {
        triggeringRuleRepository = mock(TriggeringRuleRepository.class);
        triggeringRuleLoader = mock(TriggeringRuleLoader.class);

        controller = new TrailingRuleController(triggeringRuleLoader, triggeringRuleRepository);
    }

    @Test
    @DisplayName("Test if controller is returning the list of rules as given by repository")
    public void testAllTrailingRules() {
        TriggeringRule[] rules = {
                new TrailingStopRule(null, "ETH-BTC", 0.00001, TrailingStopRule.pointType.point,
                        TrailingStopRule.directionType.below),
                new TrailingStopRule(null, "ETH-BTC", 1, TrailingStopRule.pointType.point,
                        TrailingStopRule.directionType.above) };

        when(triggeringRuleLoader.loadTriggeringRules()).thenReturn(Arrays.asList(rules));

        List<TrailingStopRule> allRules = controller.allTrailingRules();

        assertThat(allRules).hasSize(2);
        assertThat(allRules.get(0).getTrailingPoints()).isEqualTo(0.00001);
        assertThat(allRules.get(1).getTrailingPoints()).isEqualTo(1);
    }

    @Test
    @DisplayName("Test if controller is returning the rule object as given by repository")
    public void testOneTrailingRule() {
        when(triggeringRuleRepository.findById(anyString())).thenReturn(Optional.of(new TrailingStopRule(null,
                "ETH-FAKE", 12345, TrailingStopRule.pointType.point, TrailingStopRule.directionType.below)));

        TrailingStopRule rule = controller.oneTrailingRule("fakeId");

        assertThat(rule.getMarketId()).isEqualTo("ETH-FAKE");
        assertThat(rule.getTrailingPoints()).isEqualTo(12345);
    }

    @Test
    @DisplayName("ResponseStatuException is supposed to be thrown if no rule with the given ruleId could be found")
    public void testEmptyOneTrailingRule() {
        when(triggeringRuleRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.oneTrailingRule("fakeId")).isInstanceOf(ResponseStatusException.class);
    }

    @Test
    @DisplayName("Test if controller uses repository's save method to persist the given argument")
    public void testNewTrailingRule() {
        when(triggeringRuleRepository.save(any(TrailingStopRule.class))).thenAnswer(mock -> mock.getArguments()[0]);
        TrailingStopRule newRule = new TrailingStopRule(null, "ETH-BTC", 0.00001, TrailingStopRule.pointType.point,
                TrailingStopRule.directionType.below);

        TrailingStopRule rule = controller.newTrailingRule(newRule);

        assertThat(rule).isEqualTo(newRule);
        verify(triggeringRuleRepository).save(eq(newRule));
    }

    @Test
    @DisplayName("Controller must update existing rule with new rule that's given as arguement, by calling save method")
    public void testUpdateTrailingRule() {
        TrailingStopRule newRule = new TrailingStopRule("fakeId", "NEW-MKT", 12345,
                TrailingStopRule.pointType.percentage, TrailingStopRule.directionType.above);

        when(triggeringRuleRepository.findById("fakeId")).thenReturn(Optional.of(new TrailingStopRule("fakeId",
                "ETH-FAKE", 12345, TrailingStopRule.pointType.point, TrailingStopRule.directionType.below)));

        when(triggeringRuleRepository.save(any(TrailingStopRule.class))).thenAnswer(mock -> mock.getArguments()[0]);

        TrailingStopRule returnedRule = controller.updateTrailingRule(newRule, "fakeId");

        assertThat(returnedRule).isEqualTo(newRule);
        verify(triggeringRuleRepository).save(argThat(rule -> {
            TrailingStopRule trailingRule = (TrailingStopRule) rule;
            return trailingRule.getMarketId().equals(newRule.getMarketId())
                    && trailingRule.getTrailingDirection() == newRule.getTrailingDirection()
                    && trailingRule.getTrailingType() == newRule.getTrailingType()
                    && trailingRule.getTrailingPoints() == newRule.getTrailingPoints();
        }));
    }

    @Test
    @DisplayName("ResponseStatuException is supposed to be thrown if no rule with the given ruleId could be found")
    public void testEmptyUpdateTrailingRule() {
        when(triggeringRuleRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.updateTrailingRule(null, "fakeId"))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    public void testDeleteTrailingRule() {

        controller.deleteRule("fakeId");

        verify(triggeringRuleRepository).deleteById(eq("fakeId"));
    }
}