package com.gmail.bmskoh.strategyapp.processors;

import com.gmail.bmskoh.strategyapp.model.MarketTicker;

/**
 * This order processor decides when to trigger order based on stop loss rule and current ticker data.
 */
public class StopLossRuleProcessor implements ITriggeringRuleProcessor {

    @Override
    public boolean processTicker(MarketTicker marketTicker) throws TriggeringRuleException {
        // TODO implement stop loss strategy
        return false;
    }
}