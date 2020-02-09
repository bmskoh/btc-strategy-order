package com.gmail.bmskoh.strategyapp.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import javax.websocket.RemoteEndpoint.Async;
import javax.websocket.DeploymentException;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import com.gmail.bmskoh.strategyapp.conf.BTCWebsocketProperties;
import com.gmail.bmskoh.strategyapp.processors.OrderProcessManager;
import com.gmail.bmskoh.strategyapp.services.MarketTickerWebSocketService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StrategyOrderServiceTests {
    @Mock
    private IMarketTickerConnService marketTickerConnService;

    @InjectMocks
    private StrategyOrderService strategyOrderSerivce;

    @Test
    @DisplayName("Given marketTickerConnService's startConnService should be called with an instance of WebSocketContainer.")
    public void testStartService() throws IOException, DeploymentException {
        Class<WebSocketContainer> expectedClass = WebSocketContainer.class;

        this.strategyOrderSerivce.startService();

        verify(marketTickerConnService).startConnService(any(expectedClass));
    }

}