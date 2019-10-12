package com.gmail.bmskoh.strategyapp.comm;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import javax.websocket.RemoteEndpoint.Async;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import com.gmail.bmskoh.strategyapp.conf.BTCWebsocketProperties;
import com.gmail.bmskoh.strategyapp.processors.OrderProcessorManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ConnectionClientTests {
    @Mock
    OrderProcessorManager orderProcessorManager;
    @Mock
    BTCWebsocketProperties properties;

    @InjectMocks
    ConnectionClient connectionClient;

    @Test
    @DisplayName("Test if WebSockerContainer's connectToServer is called with correct URI when startConnection is called.")
    public void testStartConnection() throws Exception {
        final String uriPath = "fake://path";
        when(properties.getEndpointUrl()).thenReturn(uriPath);
        WebSocketContainer container = mock(WebSocketContainer.class);

        connectionClient.startConnection(container);

        verify(container).connectToServer(any(ConnectionClient.class), argThat(uri -> uriPath.equals(uri.toString())));
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

        when(properties.getChannelNames()).thenReturn(channels);
        when(properties.getMarketIds()).thenReturn(marketIds);

        when(session.getAsyncRemote()).thenReturn(asyncEndpoint);

        connectionClient.onOpen(session);

        String expectedMsg = "{\"channels\":[\"tick\"],\"marketIds\":[\"btc\",\"eth\"],\"messageType\":\"subscribe\"}";

        verify(asyncEndpoint).sendText(eq(expectedMsg));
    }

    @Test
    @DisplayName("OrderProcessorManager's pushMarketTicker should be called for all the msgs coming through onMessage")
    public void testOnMessage() {
        String fakeMsg = "What the fake";
        connectionClient.OnMessage(fakeMsg);

        verify(orderProcessorManager).pushMarketTicker(eq(fakeMsg));
    }
}