package com.gmail.bmskoh.strategyapp.processors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.LinkedList;

import com.gmail.bmskoh.strategyapp.model.MarketTicker;
import com.gmail.bmskoh.strategyapp.model.TrailingStopRule;
import com.gmail.bmskoh.strategyapp.model.TriggeringRule;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrderProcessorManagerTests {
    @Mock
    TriggeringRuleLoader triggeringRuleLoader;
    OrderProcessorManager orderProcessorManager;

    @BeforeEach
    void init() {
        orderProcessorManager = new OrderProcessorManager(triggeringRuleLoader, new RuleProcessorFactory());
    }

    @AfterEach
    void cleanUp() {
        orderProcessorManager.cleanUp();
    }

    @Test
    public void testInitWithNoRules() {
        when(triggeringRuleLoader.loadTriggeringRules()).thenReturn(new LinkedList<TriggeringRule>());

        orderProcessorManager.init();

        assertThat(orderProcessorManager.getOrderProcessors().size()).isZero();
    }

    @Test
    public void testInitWithRules() {
        // prepare a list of rules
        LinkedList<TriggeringRule> list = new LinkedList<>();
        list.add(new TrailingStopRule("order1", "market1", 1, TrailingStopRule.pointType.point,
                TrailingStopRule.directionType.above));
        list.add(new TrailingStopRule("order2", "market2", 1, TrailingStopRule.pointType.point,
                TrailingStopRule.directionType.above));
        when(triggeringRuleLoader.loadTriggeringRules()).thenReturn(list);

        orderProcessorManager.init();

        assertThat(orderProcessorManager.getOrderProcessors().size()).isEqualTo(2);
    }

    @Test
    public void testMarketTickerPassedToProcessor() throws Exception {
        // 1. prepare a rule
        LinkedList<TriggeringRule> list = new LinkedList<>();
        list.add(new TrailingStopRule("order1", "market1", 1, TrailingStopRule.pointType.point,
                TrailingStopRule.directionType.above));
        when(triggeringRuleLoader.loadTriggeringRules()).thenReturn(list);

        // 2. prepare a mocked TrailingStopRuleProcessor
        TrailingStopRuleProcessor processor = mock(TrailingStopRuleProcessor.class);
        when(processor.processTicker(any(MarketTicker.class))).thenReturn(false);

        // 3. mock a factory that will return the processor of step 2
        RuleProcessorFactory factory = mock(RuleProcessorFactory.class);
        when(factory.createTriggeringRuleProcessor(any(TriggeringRule.class))).thenReturn(processor);

        // 4. create OrderProcessorManager with prepared mocks.
        orderProcessorManager = new OrderProcessorManager(triggeringRuleLoader, factory);

        orderProcessorManager.init();

        String ticker = "{\"marketId\":\"ETH-BTC\",\"timestamp\":\"2019-10-09T22:49:56.156Z\",\"bestBid\":\"80\","
                + "\"bestAsk\":\"100\",\"lastPrice\":\"90\",\"volume24h\":\"200\","
                + "\"messageType\":\"tick\",\"price24h\":\"80\",\"low24h\":\"60\",\"high24h\":\"110\"}";

        // 5. push fake ticker string
        orderProcessorManager.pushMarketTicker(ticker);

        // 6. last processed ticker in OrderProcessorManager is supposed to match given
        // fake ticker string
        MarketTicker lastProcessedTicker = orderProcessorManager.getLastProcessedTickers().take();
        assertThat(lastProcessedTicker.getMarketId()).isEqualTo("ETH-BTC");
        assertThat(lastProcessedTicker.getTimestamp()).isEqualTo("2019-10-09T22:49:56.156Z");
        assertThat(lastProcessedTicker.getBestAsk()).isEqualTo(100);
        assertThat(lastProcessedTicker.getBestBid()).isEqualTo(80);
        assertThat(lastProcessedTicker.getLastPrice()).isEqualTo(90);

        // 7. MarketTicker which was passed to TrailingStopRuleProcessor is supposed to
        // match given fake ticker string.
        verify(processor).processTicker(argThat(marketTicker -> {
            return marketTicker.getMarketId().equals("ETH-BTC")
                    && marketTicker.getTimestamp().equals("2019-10-09T22:49:56.156Z")
                    && marketTicker.getBestAsk() == 100 && marketTicker.getBestBid() == 80
                    && marketTicker.getLastPrice() == 90;
        }));
    }
}