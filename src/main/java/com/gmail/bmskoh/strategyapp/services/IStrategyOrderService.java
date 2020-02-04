package com.gmail.bmskoh.strategyapp.services;

/**
 * This interface defines what strategy order service should offer.
 */
public interface IStrategyOrderService {
    /**
     * Start strategy order service. The implementation of this would include
     * getting market ticker data from BTCMarkets and process them according to
     * triggering rules.
     */
    public void startService();

}