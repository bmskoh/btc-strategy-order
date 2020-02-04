package com.gmail.bmskoh.strategyapp.processors;

import com.gmail.bmskoh.strategyapp.model.MarketTicker;

public class StopLossRuleProcessor implements ITriggeringRuleProcessor {

    @Override
    public boolean processTicker(MarketTicker marketTicker) throws TriggeringRuleException {
        // TODO implement stop loss strategy
        return false;
    }
}