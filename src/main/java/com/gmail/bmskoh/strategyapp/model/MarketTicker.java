package com.gmail.bmskoh.strategyapp.model;

public class MarketTicker {
    private double bestBid;
    private double bestAsk;
    private double lastPrice;
    private double volume24h;
    private double price24h;
    private double low24h;
    private double high24h;
    private String marketId;
    private String messageType;
    private String timestamp;

    public double getBestBid() {
        return this.bestBid;
    }

    public void setBestBid(double bestBid) {
        this.bestBid = bestBid;
    }

    public double getBestAsk() {
        return this.bestAsk;
    }

    public void setBestAsk(double bestAsk) {
        this.bestAsk = bestAsk;
    }

    public double getLastPrice() {
        return this.lastPrice;
    }

    public void setLastPrice(double lastPrice) {
        this.lastPrice = lastPrice;
    }

    public double getVolume24h() {
        return this.volume24h;
    }

    public void setVolume24h(double volume24h) {
        this.volume24h = volume24h;
    }

    public double getPrice24h() {
        return this.price24h;
    }

    public void setPrice24h(double price24h) {
        this.price24h = price24h;
    }

    public double getLow24h() {
        return this.low24h;
    }

    public void setLow24h(double low24h) {
        this.low24h = low24h;
    }

    public double getHigh24h() {
        return this.high24h;
    }

    public void setHigh24h(double high24h) {
        this.high24h = high24h;
    }

    public String getMarketId() {
        return this.marketId;
    }

    public void setMarketId(String marketId) {
        this.marketId = marketId;
    }

    public String getMessageType() {
        return this.messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}