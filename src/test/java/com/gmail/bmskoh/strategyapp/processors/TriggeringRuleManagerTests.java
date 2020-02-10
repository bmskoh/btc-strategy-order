package com.gmail.bmskoh.strategyapp.processors;

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
import com.gmail.bmskoh.strategyapp.repositories.ITriggeringRuleRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class TriggeringRuleManagerTests {

    private ITriggeringRuleRepository triggeringRuleRepository;
    private RuleProcessorFactory ruleProcessorFactory;
    private OrderProcessManager ruleManager;

    @BeforeEach
    void init() {
        this.triggeringRuleRepository = mock(ITriggeringRuleRepository.class);
        this.ruleProcessorFactory = mock(RuleProcessorFactory.class);

        ruleManager = new OrderProcessManager(this.ruleProcessorFactory, this.triggeringRuleRepository);
    }

    @Test
    @DisplayName("Test if triggering rule manager is returning the list of rules as given by repository")
    public void testAllTrailingRules() {
        TrailingStopRule[] rules = {
                new TrailingStopRule(null, "ETH-BTC", 0.00001, TrailingStopRule.pointType.point,
                        TrailingStopRule.directionType.below),
                new TrailingStopRule(null, "ETH-BTC", 1, TrailingStopRule.pointType.point,
                        TrailingStopRule.directionType.above) };

        when(this.ruleManager.getAllTrailingRules()).thenReturn(Arrays.asList(rules));

        List<TrailingStopRule> allRules = ruleManager.getAllTrailingRules();

        assertThat(allRules).hasSize(2);
        assertThat(allRules.get(0).getTrailingPoints()).isEqualTo(0.00001);
        assertThat(allRules.get(1).getTrailingPoints()).isEqualTo(1);
    }

    @Test
    @DisplayName("Test if triggering rule manager is returning the rule object as given by repository")
    public void testOneTrailingRule() throws TriggeringRuleNotFoundException {
        when(this.triggeringRuleRepository.findById(anyString())).thenReturn(Optional.of(new TrailingStopRule(null,
                "ETH-FAKE", 12345, TrailingStopRule.pointType.point, TrailingStopRule.directionType.below)));

        TrailingStopRule rule = ruleManager.getTrailingRule("fakeId");

        assertThat(rule.getMarketId()).isEqualTo("ETH-FAKE");
        assertThat(rule.getTrailingPoints()).isEqualTo(12345);
    }

    @Test
    @DisplayName("TriggeringRuleNotFoundException is supposed to be thrown if no rule with the given ruleId could be found for retrieving")
    public void testEmptyOneTrailingRule() {
        when(this.triggeringRuleRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ruleManager.getTrailingRule("fakeId"))
                .isInstanceOf(TriggeringRuleNotFoundException.class);
    }

    @Test
    @DisplayName("Test if triggering rule manager uses repository's save method to persist the given argument")
    public void testNewTrailingRule() {
        when(this.triggeringRuleRepository.save(any(TrailingStopRule.class)))
                .thenAnswer(mock -> mock.getArguments()[0]);
        TrailingStopRule newRule = new TrailingStopRule(null, "ETH-BTC", 0.00001, TrailingStopRule.pointType.point,
                TrailingStopRule.directionType.below);

        TrailingStopRule rule = ruleManager.addTrailingRule(newRule);

        assertThat(rule).isEqualTo(newRule);
        verify(this.triggeringRuleRepository).save(eq(newRule));
    }

    @Test
    @DisplayName("Triggering rule manager must update existing rule with new rule that's given as arguement, by calling save method")
    public void testUpdateTrailingRule() throws TriggeringRuleNotFoundException {
        TrailingStopRule newRule = new TrailingStopRule("fakeId", "NEW-MKT", 12345,
                TrailingStopRule.pointType.percentage, TrailingStopRule.directionType.above);

        when(this.triggeringRuleRepository.findById("fakeId")).thenReturn(Optional.of(new TrailingStopRule("fakeId",
                "ETH-FAKE", 12345, TrailingStopRule.pointType.point, TrailingStopRule.directionType.below)));

        when(this.triggeringRuleRepository.save(any(TrailingStopRule.class)))
                .thenAnswer(mock -> mock.getArguments()[0]);

        ruleManager.updateTrailingRule(newRule);

        verify(this.triggeringRuleRepository).save(argThat(rule -> {
            TrailingStopRule trailingRule = (TrailingStopRule) rule;
            return trailingRule.getMarketId().equals(newRule.getMarketId())
                    && trailingRule.getTrailingDirection() == newRule.getTrailingDirection()
                    && trailingRule.getTrailingType() == newRule.getTrailingType()
                    && trailingRule.getTrailingPoints() == newRule.getTrailingPoints();
        }));
    }

    @Test
    @DisplayName("TriggeringRuleNotFoundException is supposed to be thrown if no rule with the given ruleId could be found for updating")
    public void testEmptyUpdateTrailingRule() {
        TrailingStopRule newRule = new TrailingStopRule("fakeId", "NEW-MKT", 12345,
                TrailingStopRule.pointType.percentage, TrailingStopRule.directionType.above);

        when(this.triggeringRuleRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ruleManager.updateTrailingRule(newRule))
                .isInstanceOf(TriggeringRuleNotFoundException.class);
    }

    @Test
    @DisplayName("Triggering rule manager should call repository's deleteById with given rule id for deleting")
    public void testDeleteTrailingRule() throws TriggeringRuleNotFoundException {
        ruleManager = Mockito.spy(ruleManager);
        TrailingStopRule fakeRule = new TrailingStopRule("fakeId", "NEW-MKT", 12345,
                TrailingStopRule.pointType.percentage, TrailingStopRule.directionType.above);
        Mockito.doReturn(fakeRule).when(ruleManager).getTrailingRule(any());

        ruleManager.deleteTrailingRule("fakeId");

        verify(this.triggeringRuleRepository).deleteById(eq("fakeId"));
    }

    @Test
    @DisplayName("TriggeringRuleNotFoundException is supposed to be thrown if no rule with the given ruleId could be found for deleting")
    public void testDeleteNonExistingTrailingRule() throws TriggeringRuleNotFoundException {
        ruleManager = Mockito.spy(ruleManager);
        Mockito.doThrow(new TriggeringRuleNotFoundException("")).when(ruleManager).getTrailingRule(any());

        assertThatThrownBy(() -> ruleManager.deleteTrailingRule("fakeId"))
                .isInstanceOf(TriggeringRuleNotFoundException.class);
    }
}