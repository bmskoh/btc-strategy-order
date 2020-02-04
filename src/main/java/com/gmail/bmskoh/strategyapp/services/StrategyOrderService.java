package com.gmail.bmskoh.strategyapp.services;

import com.gmail.bmskoh.strategyapp.processors.IOrderProcessManager;

import org.springframework.stereotype.Service;

/**
 * Simple implementation of IStrategyOrderService. This just starts
 * IMarketTickerCommService.
 */
@Service
public class StrategyOrderService implements IStrategyOrderService {
    IMarketTickerConnService marketTickerCommService;
    IOrderProcessManager orderProcessManager;

    public StrategyOrderService(IMarketTickerConnService marketTickerCommService,
            IOrderProcessManager orderProcessManager) {
        this.marketTickerCommService = marketTickerCommService;
        this.orderProcessManager = orderProcessManager;
    }

    public void startService() {
        this.marketTickerCommService.startConnService();
    }
}