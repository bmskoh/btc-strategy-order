package com.gmail.bmskoh.strategyapp.processors;

import static org.assertj.core.api.Assertions.assertThat;

import com.gmail.bmskoh.strategyapp.model.TrailingStopRule;
import com.gmail.bmskoh.strategyapp.model.TriggeringRule;
import com.gmail.bmskoh.strategyapp.model.TrailingStopRule.directionType;
import com.gmail.bmskoh.strategyapp.model.TrailingStopRule.pointType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RuleProcessorFactoryTests {
    RuleProcessorFactory factory;

    @BeforeEach
    void init() {
        factory = new RuleProcessorFactory();
    }

    @Test
    public void testReturnTrailingProcessor() {
        TriggeringRule triggeringRule = new TrailingStopRule("rule1", "BTC", 5, pointType.point, directionType.above);
        ITriggeringRuleProcessor processor = factory.createTriggeringRuleProcessor(triggeringRule);

        assertThat(processor).isInstanceOf(TrailingStopRuleProcessor.class);
    }
}