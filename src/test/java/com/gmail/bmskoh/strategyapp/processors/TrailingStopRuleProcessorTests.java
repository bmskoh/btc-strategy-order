package com.gmail.bmskoh.strategyapp.processors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.gmail.bmskoh.strategyapp.model.MarketTicker;
import com.gmail.bmskoh.strategyapp.model.TrailingStopRule;
import com.gmail.bmskoh.strategyapp.model.TrailingStopRule.directionType;
import com.gmail.bmskoh.strategyapp.model.TrailingStopRule.pointType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TrailingStopRuleProcessorTests {

    TrailingStopRuleProcessor processor;

    @BeforeEach
    void init() {
        TrailingStopRule rule = new TrailingStopRule("rule1", "BTC", 5, pointType.point, directionType.below);

        processor = new TrailingStopRuleProcessor(rule);
    }

    @Test
    @DisplayName("Test processTicker returns false if marketId doesn't match.")
    public void testWrongMarketId() throws Exception {
        MarketTicker ticker = new MarketTicker();
        ticker.setMarketId("NBTC");
        ticker.setBestAsk(1);
        ticker.setBestBid(2);
        ticker.setLastPrice(3);

        assertThat(processor.processTicker(ticker)).isFalse();
    }

    @Test
    @DisplayName("Test processTicker returns false when MarketTicker is given"
            + " for the first time regardless of given value.")
    public void testFirstTimeProcessing() throws Exception {
        MarketTicker ticker = new MarketTicker();
        ticker.setMarketId("BTC");
        ticker.setBestAsk(1);
        ticker.setBestBid(2);
        ticker.setLastPrice(3);

        assertThat(processor.processTicker(ticker)).isFalse();
    }

    @Test
    @DisplayName("Test processTicker throws exception if the rule has been triggered already.")
    public void testThrowsExceptionForTriggeredRule() throws Exception {
        MarketTicker ticker = new MarketTicker();
        ticker.setMarketId("BTC");
        ticker.setLastPrice(30);
        processor.processTicker(ticker);
        ticker.setLastPrice(20);
        assertThat(processor.processTicker(ticker)).as("this rule should've been triggered").isTrue();

        ticker.setLastPrice(10);
        assertThatThrownBy(() -> processor.processTicker(ticker)).isInstanceOf(TriggeringRuleException.class)
                .hasMessageContaining("triggered already");
    }
}

class TrailingAboveTypeTests {

    TrailingStopRuleProcessor processor;

    @BeforeEach
    void init() {
        TrailingStopRule rule = new TrailingStopRule("rule1", "BTC", 5, pointType.point, directionType.above);

        processor = new TrailingStopRuleProcessor(rule);
    }

    @Test
    @DisplayName("Test processTicker returns false if the price goes down.")
    public void testFallingPrice() throws Exception {
        MarketTicker ticker = new MarketTicker();
        ticker.setMarketId("BTC");

        for (double d = 100; d > 0; d -= 10) {
            ticker.setLastPrice(d);
            assertThat(processor.processTicker(ticker)).as("price is %d", d).isFalse();
        }
    }

    @Test
    @DisplayName("Test processTicker returns false if the price goes down and doesn't rise enough.")
    public void testFallAndRiseABit() throws Exception {
        MarketTicker ticker = new MarketTicker();
        ticker.setMarketId("BTC");

        // goes down
        ticker.setLastPrice(50);
        processor.processTicker(ticker);
        ticker.setLastPrice(40);
        processor.processTicker(ticker);
        ticker.setLastPrice(30);
        processor.processTicker(ticker);

        // up by 3
        ticker.setLastPrice(33);
        assertThat(processor.processTicker(ticker)).isFalse();
    }

    @Test
    @DisplayName("Test processTicker handles that the price falls down and doesn't rise enough " + "and falls again")
    public void testFallRiseAbitAndFall() throws Exception {
        MarketTicker ticker = new MarketTicker();
        ticker.setMarketId("BTC");

        // goes down
        ticker.setLastPrice(50);
        processor.processTicker(ticker);
        ticker.setLastPrice(40);
        processor.processTicker(ticker);
        ticker.setLastPrice(30);
        processor.processTicker(ticker);

        // up by 4
        ticker.setLastPrice(34);
        processor.processTicker(ticker);

        ticker.setLastPrice(30);
        processor.processTicker(ticker);
        ticker.setLastPrice(20);
        assertThat(processor.processTicker(ticker)).isFalse();
    }

    @Test
    @DisplayName("Test processTicker handles that the price rises from the beginning.")
    public void testRisingPrice() throws Exception {
        MarketTicker ticker = new MarketTicker();
        ticker.setMarketId("BTC");

        ticker.setLastPrice(100);
        assertThat(processor.processTicker(ticker)).as("initial process").isFalse();

        ticker.setLastPrice(101);
        assertThat(processor.processTicker(ticker)).as("rises by 1").isFalse();

        ticker.setLastPrice(105);
        assertThat(processor.processTicker(ticker)).as("rises by 5").isTrue();
    }

    @Test
    @DisplayName("Test processTicker handles that the price falls down and rise enough")
    public void testFallAndRise() throws Exception {
        MarketTicker ticker = new MarketTicker();
        ticker.setMarketId("BTC");

        // goes down
        ticker.setLastPrice(40);
        processor.processTicker(ticker);
        ticker.setLastPrice(30);
        processor.processTicker(ticker);
        ticker.setLastPrice(20);
        processor.processTicker(ticker);

        // up by 3
        ticker.setLastPrice(23);
        assertThat(processor.processTicker(ticker)).as("not enough rise yet").isFalse();

        // up by 5 in total
        ticker.setLastPrice(25);
        assertThat(processor.processTicker(ticker)).isTrue();
    }

    @Test
    @DisplayName("Test processTicker handles that the price falls down and doesn't rise enough "
            + "and falls down and rise enough")
    public void testFallRiseFallRise() throws Exception {
        MarketTicker ticker = new MarketTicker();
        ticker.setMarketId("BTC");

        // goes down
        ticker.setLastPrice(100);
        processor.processTicker(ticker);
        ticker.setLastPrice(90);
        processor.processTicker(ticker);
        ticker.setLastPrice(80);
        processor.processTicker(ticker);

        // up by 4
        ticker.setLastPrice(84);
        processor.processTicker(ticker);

        // down
        ticker.setLastPrice(80);
        processor.processTicker(ticker);
        ticker.setLastPrice(70);
        processor.processTicker(ticker);

        // up by 7
        ticker.setLastPrice(77);
        assertThat(processor.processTicker(ticker)).isTrue();
    }

}

class TrailingBelowTypeTests {

    TrailingStopRuleProcessor processor;

    @BeforeEach
    void init() {
        TrailingStopRule rule = new TrailingStopRule("rule1", "BTC", 5, pointType.point, directionType.below);

        processor = new TrailingStopRuleProcessor(rule);
    }

    @Test
    @DisplayName("Test processTicker returns false if the price goes up.")
    public void testRisingPrice() throws Exception {
        MarketTicker ticker = new MarketTicker();
        ticker.setMarketId("BTC");

        for (double d = 10; d < 100; d += 10) {
            ticker.setLastPrice(d);
            assertThat(processor.processTicker(ticker)).as("price is %d", d).isFalse();
        }
    }

    @Test
    @DisplayName("Test processTicker returns false if the price goes up and doesn't fall down enough.")
    public void testRisingAndFallAbit() throws Exception {
        MarketTicker ticker = new MarketTicker();
        ticker.setMarketId("BTC");

        // goes up
        ticker.setLastPrice(10);
        processor.processTicker(ticker);
        ticker.setLastPrice(20);
        processor.processTicker(ticker);
        ticker.setLastPrice(30);
        processor.processTicker(ticker);

        // down by 4
        ticker.setLastPrice(26);
        assertThat(processor.processTicker(ticker)).isFalse();
    }

    @Test
    @DisplayName("Test processTicker handles that the price goes up and doesn't fall down enough "
            + "and goes up again")
    public void testRisingAndFallAbitAndUp() throws Exception {
        MarketTicker ticker = new MarketTicker();
        ticker.setMarketId("BTC");

        // goes up
        ticker.setLastPrice(10);
        processor.processTicker(ticker);
        ticker.setLastPrice(20);
        processor.processTicker(ticker);
        ticker.setLastPrice(30);
        processor.processTicker(ticker);

        // down by 4
        ticker.setLastPrice(26);
        processor.processTicker(ticker);

        ticker.setLastPrice(30);
        processor.processTicker(ticker);
        ticker.setLastPrice(40);
        assertThat(processor.processTicker(ticker)).isFalse();
    }

    @Test
    @DisplayName("Test processTicker handles that the price falls down from the beginning.")
    public void testFallingPrice() throws Exception {
        MarketTicker ticker = new MarketTicker();
        ticker.setMarketId("BTC");

        ticker.setLastPrice(100);
        assertThat(processor.processTicker(ticker)).as("initial process").isFalse();

        ticker.setLastPrice(98);
        assertThat(processor.processTicker(ticker)).as("falls by 2").isFalse();

        ticker.setLastPrice(95);
        assertThat(processor.processTicker(ticker)).as("falls by 5").isTrue();
    }

    @Test
    @DisplayName("Test processTicker handles that the price goes up and falls down enough")
    public void testRisingAndFalling() throws Exception {
        MarketTicker ticker = new MarketTicker();
        ticker.setMarketId("BTC");

        // goes up
        ticker.setLastPrice(10);
        processor.processTicker(ticker);
        ticker.setLastPrice(20);
        processor.processTicker(ticker);
        ticker.setLastPrice(30);
        processor.processTicker(ticker);

        // down by 4
        ticker.setLastPrice(26);
        assertThat(processor.processTicker(ticker)).as("not enough fall yet").isFalse();

        // down by 6 in total
        ticker.setLastPrice(24);
        assertThat(processor.processTicker(ticker)).isTrue();
    }

    @Test
    @DisplayName("Test processTicker handles that the price goes up and doesn't fall down enough "
            + "and goes up and falls down enough")
    public void testRiseFallRiseFall() throws Exception {
        MarketTicker ticker = new MarketTicker();
        ticker.setMarketId("BTC");

        // goes up
        ticker.setLastPrice(10);
        processor.processTicker(ticker);
        ticker.setLastPrice(20);
        processor.processTicker(ticker);
        ticker.setLastPrice(30);
        processor.processTicker(ticker);

        // down by 4
        ticker.setLastPrice(26);
        processor.processTicker(ticker);

        // up
        ticker.setLastPrice(30);
        processor.processTicker(ticker);
        ticker.setLastPrice(40);
        processor.processTicker(ticker);

        // down by 7
        ticker.setLastPrice(33);
        assertThat(processor.processTicker(ticker)).isTrue();
    }

}