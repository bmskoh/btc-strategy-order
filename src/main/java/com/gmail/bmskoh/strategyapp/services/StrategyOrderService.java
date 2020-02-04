package com.gmail.bmskoh.strategyapp.services;

import org.springframework.stereotype.Service;

/**
 * Simple implementation of IStrategyOrderService. This just starts
 * IMarketTickerCommService.
 */
@Service
public class StrategyOrderService implements IStrategyOrderService {
    IMarketTickerConnService marketTickerCommService;

    public StrategyOrderService(IMarketTickerConnService marketTickerCommService) {
        this.marketTickerCommService = marketTickerCommService;
    }

    public void startService() {
        this.marketTickerCommService.startConnService();
    }
}