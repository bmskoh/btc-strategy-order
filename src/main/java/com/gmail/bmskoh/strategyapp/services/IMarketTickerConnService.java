package com.gmail.bmskoh.strategyapp.services;

/**
 * Connection service which looks after connection with
 * BTCMarkets server.
 */
public interface IMarketTickerConnService {
    /**
     * Start connection service. This method will trigger connecting
     * BTCMarket server and retrieving market data.
     */
    public void startConnService();
}