package com.gmail.bmskoh.strategyapp.services;

import java.net.URI;
import java.util.List;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmail.bmskoh.strategyapp.conf.BTCWebsocketProperties;
import com.gmail.bmskoh.strategyapp.processors.OrderProcessManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Client endpoint of websocket connection to BTC websocket server.
 */
@Component
@ClientEndpoint
@EnableConfigurationProperties(BTCWebsocketProperties.class)
public class MarketTickerWebSocketService implements IMarketTickerConnService{

    /**
     * BTCRequest class represents the request to be sent to BTC websocket server
     */
    static class BTCRequest {
        private List<String> channels;
        private List<String> marketIds;
        private String messageType;

        public void setChannels(List<String> channels) {
            this.channels = channels;
        }

        public List<String> getChannels() {
            return this.channels;
        }

        public void setMarketIds(List<String> marketIds) {
            this.marketIds = marketIds;
        }

        public List<String> getMarketIds() {
            return this.marketIds;
        }

        public void setMessageType(String msgType) {
            this.messageType = msgType;
        }

        public String getMessageType() {
            return this.messageType;
        }
    }

    private Logger logger = LoggerFactory.getLogger(MarketTickerWebSocketService.class);

    @Autowired
    private OrderProcessManager orderProcessorManager;

    @Autowired
    private BTCWebsocketProperties properties;

    /**
     * Try to connection BTC websocket server. Server's address comes from
     * application.properties
     */
    public void startConnService() {
        WebSocketContainer socketContainer = ContainerProvider.getWebSocketContainer();
        logger.info("START web socket connection to {}", properties.getEndpointUrl());

        try {
            socketContainer.connectToServer(this, new URI(properties.getEndpointUrl()));
        } catch (Exception e) {
            logger.error("error creating socket client", e);
            return;
        }
    }

    /**
     * Once websocket connection is established, send subscription request.
     *
     * @param session
     */
    @OnOpen
    public void onOpen(Session session) {
        logger.info("CONNECTED: {}", properties.getEndpointUrl());

        MarketTickerWebSocketService.BTCRequest request = new MarketTickerWebSocketService.BTCRequest();
        request.setChannels(properties.getChannelNames());
        request.setMarketIds(properties.getMarketIds());
        request.setMessageType("subscribe");

        ObjectMapper mapper = new ObjectMapper();
        try {
            String requestStr = mapper.writeValueAsString(request);
            session.getAsyncRemote().sendText(requestStr);

            logger.info("SUBSCRIPTION REQUEST: {}", requestStr);
        } catch (JsonProcessingException e) {
            logger.error("Error on parsing object to JSON string", e);
        }
    }

    @OnClose
    public void onClose(Session session) {
        logger.info("Connection closed : {}", properties.getEndpointUrl());
    }

    @OnMessage
    public void OnMessage(String message) {
        logger.trace("New message: {}", message);

        // Call OrderProcessorManager to process market ticker.
        this.orderProcessorManager.pushMarketTicker(message);
    }
}
