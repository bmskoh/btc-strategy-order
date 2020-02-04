package com.gmail.bmskoh.strategyapp.processors;

import com.gmail.bmskoh.strategyapp.model.MarketTicker;

/**
 * This is a super class of order processors.
 */
public interface ITriggeringRuleProcessor {
    /**
     * Process given ticker string and decide to trigger order or not.
     *
     * @param marketTicker Current market ticker
     * @return true if the order is triggered. false otherwise
     */
    public abstract boolean processTicker(MarketTicker marketTicker) throws TriggeringRuleException;

}