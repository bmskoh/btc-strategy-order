package com.gmail.bmskoh.strategyapp.processors;

/**
 * IOrderProcessManager process market ticker data.
 */
public interface IOrderProcessManager {
    /**
     * Use this method to push current market ticker JSON string that needs to be
     * processed.
     *
     * @param tickerStr Current market ticker string in JSON format.
     * @return true if successful, false otherwise.
     */
    public boolean pushMarketTicker(String tickerStr);
}