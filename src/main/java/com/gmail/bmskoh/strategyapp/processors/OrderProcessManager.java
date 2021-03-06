package com.gmail.bmskoh.strategyapp.processors;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.annotation.PostConstruct;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmail.bmskoh.strategyapp.model.MarketTicker;
import com.gmail.bmskoh.strategyapp.model.TrailingStopRule;
import com.gmail.bmskoh.strategyapp.model.TriggeringRule;
import com.gmail.bmskoh.strategyapp.repositories.ITriggeringRuleRepository;

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
 * {@link com.gmail.bmskoh.strategyapp.processors.ITriggeringRuleProcessor
 * TriggeringRuleProcessor} know how to process triggering rules and current
 * market tickers.
 */
@Component
public class OrderProcessManager implements ITriggeringRuleManager, IMarketTickerHandler {
    private final Logger logger = LoggerFactory.getLogger(OrderProcessManager.class);

    private ITriggeringRuleRepository triggeringRuleRepository;

    private Thread processingThread;
    private List<ITriggeringRuleProcessor> triggeringProcessor = new LinkedList<>();
    private final BlockingQueue<String> incomingTickQueue = new LinkedBlockingDeque<>();

    private IRuleProcessorFactory ruleProcessorFactory;
    private boolean running = true;

    public OrderProcessManager(IRuleProcessorFactory ruleProcessorFactory,
            ITriggeringRuleRepository triggeringRuleRepository) {
        this.triggeringRuleRepository = triggeringRuleRepository;
        this.ruleProcessorFactory = ruleProcessorFactory;
    }

    @PostConstruct
    void init() {
        this.logger.info("OrderProcessorManager initiatalized.");

        // Load trailing rules
        List<TriggeringRule> trailingOrderRules = this.getAllTriggeringRules();
        // Create order processor for each trailing rule
        this.triggeringProcessor.addAll(trailingOrderRules.stream()
                .map(rule -> ruleProcessorFactory.createTriggeringRuleProcessor(rule)).collect(Collectors.toList()));

        // Start a thread to process market ticker strings.
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

    public List<ITriggeringRuleProcessor> getOrderProcessors() {
        return this.triggeringProcessor;
    }

    public void cleanUp() {
        this.running = false;
        this.processingThread.interrupt();
    }

    public static int MAX_PROCESSED_TICKER_SIZE = 3;
    private BlockingQueue<MarketTicker> lastProcessedTickers = new ArrayBlockingQueue<>(
            OrderProcessManager.MAX_PROCESSED_TICKER_SIZE);

    private void putLastProcessedTicker(MarketTicker ticker) {
        if (this.lastProcessedTickers.size() >= OrderProcessManager.MAX_PROCESSED_TICKER_SIZE) {
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

    private String generateNewRuleID() {
        return UUID.randomUUID().toString();
    }

    // TODO: Temporary CRUD which needs proper implementation to consider both of
    // TrailingStopRule and StopLossRule.
    // These CUD actions should be applied to triggeringProcessor list as well.

    @Override
    public List<TriggeringRule> getAllTriggeringRules() {
        // Just return the list of trailing rules. Will have to change once stop order
        // is implemented.
        return this.getAllTrailingRules().stream().map(trailingRule -> (TriggeringRule) trailingRule)
                .collect(Collectors.toList());
    }

    @Override
    public TrailingStopRule addTrailingRule(TrailingStopRule rule) {
        rule.setRuleId(this.generateNewRuleID());
        return triggeringRuleRepository.save(rule);
    }

    @Override
    public List<TrailingStopRule> getAllTrailingRules() {
        return StreamSupport.stream(triggeringRuleRepository.findAll().spliterator(), false)
                .filter(rule -> (rule instanceof TrailingStopRule))
                .map(triggeringRule -> (TrailingStopRule) triggeringRule).collect(Collectors.toList());
    }

    @Override
    public TrailingStopRule getTrailingRule(String ruleId) throws TriggeringRuleNotFoundException {
        return (TrailingStopRule) triggeringRuleRepository.findById(ruleId)
                .orElseThrow(() -> new TriggeringRuleNotFoundException("Could not find rule: " + ruleId));
    }

    @Override
    public void updateTrailingRule(TrailingStopRule newRule) throws TriggeringRuleNotFoundException {
        triggeringRuleRepository.findById(newRule.getRuleId()).map(rule -> {
            TrailingStopRule trailingRule = (TrailingStopRule) rule;
            trailingRule.setMarketId(newRule.getMarketId());
            trailingRule.setTrailingPoints(newRule.getTrailingPoints());
            trailingRule.setTrailingType(newRule.getTrailingType());
            trailingRule.setTrailingDirection(newRule.getTrailingDirection());
            return triggeringRuleRepository.save(trailingRule);
        }).orElseThrow(() -> new TriggeringRuleNotFoundException("Could not find rule: " + newRule.getRuleId()));
    }

    @Override
    public void deleteTrailingRule(String ruleId) throws TriggeringRuleNotFoundException {
        TrailingStopRule rule = this.getTrailingRule(ruleId);
        if (rule != null) {
            triggeringRuleRepository.deleteById(ruleId);
        }
    }
}