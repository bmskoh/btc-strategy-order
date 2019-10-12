package com.gmail.bmskoh.strategyapp.processors;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmail.bmskoh.strategyapp.model.MarketTicker;
import com.gmail.bmskoh.strategyapp.model.TriggeringRule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * OrderProcessorManager manages order triggering rules and process market
 * ticker by calling appropriate order processors.
 *
 * Market ticker string in JSON format pushed into pushMarketTicker method and
 * the string goes into a blocking queue. The thread that is started in init()
 * method pulls the ticker string from the queue and process it.
 *
 * Order triggering rules are defined in subclasses of
 * {@link com.gmail.bmskoh.strategyapp.model.TriggeringRule ContingentOrder}
 * interface. And subclasses of
 * {@link com.gmail.bmskoh.strategyapp.processors.TriggeringRuleProcessor
 * TriggeringRuleProcessor} know how to process triggering rules and current
 * market tickers.
 */
@Component
public class OrderProcessorManager {
    private final Logger logger = LoggerFactory.getLogger(OrderProcessorManager.class);

    TriggeringRuleLoader triggeringRuleLoader;
    private Thread processingThread;
    private List<TriggeringRuleProcessor> triggeringProcessor = new LinkedList<>();
    private final BlockingQueue<String> incomingTickQueue = new LinkedBlockingDeque<>();
    private RuleProcessorFactory ruleProcessorFactory;
    private boolean running = true;

    public OrderProcessorManager(TriggeringRuleLoader trailingRuleLoader, RuleProcessorFactory ruleProcessorFactory) {
        this.triggeringRuleLoader = trailingRuleLoader;
        this.ruleProcessorFactory = ruleProcessorFactory;
    }

    @PostConstruct
    void init() {
        this.logger.info("OrderProcessorManager initiatalized.");

        // Load trailing rules
        List<TriggeringRule> trailingOrderRules = this.triggeringRuleLoader.loadTriggeringRules();
        // Create order processor for each trailing rule
        this.triggeringProcessor.addAll(trailingOrderRules.stream()
                .map(rule -> ruleProcessorFactory.createTriggeringRuleProcessor(rule)).collect(Collectors.toList()));

        processingThread = new Thread() {
            public void run() {
                while (running) {
                    try {
                        processMarketTicker(incomingTickQueue.take());
                    } catch (InterruptedException e) {
                        logger.warn("Interrupted while taking tick from queue. Finishing processing ticker.");
                    }
                }
            }
        };
        processingThread.start();
    }

    /**
     * Use this method to push current market ticker JSON string that needs to be
     * processed.
     *
     * @param tickerStr Current market ticker string in JSON format.
     * @return true if successful, false otherwise.
     */
    public boolean pushMarketTicker(String tickerStr) {
        try {
            this.incomingTickQueue.put(tickerStr);
        } catch (Exception e) {
            this.logger.error("Failed to add a market tick string into queue. {}", tickerStr);
            return false;
        }
        return true;
    }

    private void processMarketTicker(String marketTickerStr) {
        try {
            final MarketTicker marketTicker = this.convertJsonToMarketTicker(marketTickerStr);
            this.triggeringProcessor = this.triggeringProcessor.stream().parallel().filter(processor -> {
                try {
                    return processor.processTicker(marketTicker) == false;
                } catch (TriggeringRuleException ex) {
                    return false;
                }
            }).collect(Collectors.toList());

            if (this.logger.isTraceEnabled()) {
                this.logger.trace("{} non-triggered orderProcessors are remaining.", this.triggeringProcessor.size());
            }

            this.putLastProcessedTicker(marketTicker);
        } catch (IOException ex) {
            this.logger.error("Error while converting JSON market ticker to MarketTicker object", ex);
        }
    }

    /**
     * Converts given JSON string to MarketTicker object.
     *
     * @param jsonStr
     * @return MarketTicker object that is converted from given JSON string
     * @throws IOException
     */
    private MarketTicker convertJsonToMarketTicker(String jsonStr) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(jsonStr, MarketTicker.class);
    }

    public List<TriggeringRuleProcessor> getOrderProcessors() {
        return this.triggeringProcessor;
    }

    public void cleanUp() {
        this.running = false;
        this.processingThread.interrupt();
    }

    public static int MAX_PROCESSED_TICKER_SIZE = 3;
    private BlockingQueue<MarketTicker> lastProcessedTickers = new ArrayBlockingQueue<>(
            OrderProcessorManager.MAX_PROCESSED_TICKER_SIZE);

    private void putLastProcessedTicker(MarketTicker ticker) {
        if (this.lastProcessedTickers.size() >= OrderProcessorManager.MAX_PROCESSED_TICKER_SIZE) {
            this.lastProcessedTickers.remove();
        }
        this.lastProcessedTickers.add(ticker);
    }

    /**
     * Return a BlockingQueue that contains recently processed MarketTicker objects.
     * The maximum size of the queue is defined in
     * OrderProcessorManager.MAX_PROCESSED_TICKER_SIZE
     *
     * @return
     */
    public BlockingQueue<MarketTicker> getLastProcessedTickers() {
        return this.lastProcessedTickers;
    }
}