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
public class MarketTickerWebSocketServiceTests {
    @Mock
    private OrderProcessManager orderProcessorManager;
    @Mock
    private BTCWebsocketProperties properties;

    @InjectMocks
    private MarketTickerWebSocketService marketTickerService;

    @Test
    @DisplayName("Given WebSocketContainer's connectToServer should be called with proper URI on calling startConnService.")
    public void testStartConnService() throws IOException, DeploymentException {
        String expectedURL = "http://fake.url";
        when(this.properties.getEndpointUrl()).thenReturn(expectedURL);

        WebSocketContainer fakeContainer = mock(WebSocketContainer.class);

        this.marketTickerService.startConnService(fakeContainer);

        verify(fakeContainer).connectToServer(any(MarketTickerWebSocketService.class), argThat((URI uri) -> {
            return uri.toString().equals(expectedURL);
        }));
    }

    @Test
    @DisplayName("Async's sendText should be called with correct json string for subsription")
    public void testOnOpen() {
        Session session = mock(Session.class);
        Async asyncEndpoint = mock(Async.class);

        ArrayList<String> channels = new ArrayList<>();
        channels.add("tick");
        ArrayList<String> marketIds = new ArrayList<>();
        marketIds.add("btc");
        marketIds.add("eth");

        when(this.properties.getChannelNames()).thenReturn(channels);
        when(this.properties.getMarketIds()).thenReturn(marketIds);

        when(session.getAsyncRemote()).thenReturn(asyncEndpoint);

        this.marketTickerService.onOpen(session);

        String expectedMsg = "{\"channels\":[\"tick\"],\"marketIds\":[\"btc\",\"eth\"],\"messageType\":\"subscribe\"}";

        verify(asyncEndpoint).sendText(eq(expectedMsg));
    }

    @Test
    @DisplayName("OrderProcessorManager's pushMarketTicker should be called for all the msgs coming through onMessage")
    public void testOnMessage() {
        String fakeMsg = "What the fake";
        this.marketTickerService.OnMessage(fakeMsg);

        verify(this.orderProcessorManager).pushMarketTicker(eq(fakeMsg));
    }
}