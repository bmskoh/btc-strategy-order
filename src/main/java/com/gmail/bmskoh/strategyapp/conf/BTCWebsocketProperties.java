package com.gmail.bmskoh.strategyapp.conf;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("btcwebsocket")
public class BTCWebsocketProperties {
    private String endpointUrl;
    private List<String> channelNames;
    private List<String> marketIds;

    public String getEndpointUrl() {
        return this.endpointUrl;
    }

    public void setEndpointUrl(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }

    public List<String> getChannelNames() {
        return this.channelNames;
    }

    public void setChannelNames(List<String> channelNames) {
        this.channelNames = channelNames;
    }

    public List<String> getMarketIds() {
        return this.marketIds;
    }

    public void setMarketIds(List<String> marketIds) {
        this.marketIds = marketIds;
    }

}