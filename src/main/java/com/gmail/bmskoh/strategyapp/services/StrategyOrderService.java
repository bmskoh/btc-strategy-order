package com.gmail.bmskoh.strategyapp.services;

import javax.websocket.ContainerProvider;

import com.gmail.bmskoh.strategyapp.processors.ITriggeringRuleManager;

import org.springframework.stereotype.Service;

/**
 * Simple implementation of IStrategyOrderService. This just starts
 * IMarketTickerConnService.
 */
@Service
public class StrategyOrderService implements IStrategyOrderService {
    IMarketTickerConnService marketTickerConnService;
    ITriggeringRuleManager orderProcessManager;

    public StrategyOrderService(IMarketTickerConnService marketTickerCommService,
            ITriggeringRuleManager orderProcessManager) {
        this.marketTickerConnService = marketTickerCommService;
        this.orderProcessManager = orderProcessManager;
    }

    public void startService() {
        this.marketTickerConnService.startConnService(ContainerProvider.getWebSocketContainer());
    }
}