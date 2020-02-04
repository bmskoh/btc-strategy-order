package com.gmail.bmskoh.strategyapp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import com.gmail.bmskoh.strategyapp.services.IMarketTickerConnService;
import com.gmail.bmskoh.strategyapp.services.IStrategyOrderService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StrategyApplicationTests {
    @Mock
    IStrategyOrderService strategyOrderService;
    @InjectMocks
    StrategyApplication strategyApplication;

    @Test
    public void testAppStarts() {
        strategyApplication.run();

        verify(strategyOrderService).startService();
    }
}