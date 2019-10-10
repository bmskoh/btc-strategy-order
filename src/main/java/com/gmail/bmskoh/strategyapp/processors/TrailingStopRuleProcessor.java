package com.gmail.bmskoh.strategyapp.processors;

import java.util.Date;

import com.gmail.bmskoh.strategyapp.model.MarketTicker;
import com.gmail.bmskoh.strategyapp.model.TrailingStopRule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Process trailing order. This order processor decides when to trigger trailing
 * order based on current ticker.
 */
public class TrailingStopRuleProcessor extends TriggeringRuleProcessor {
    private Logger logger = LoggerFactory.getLogger(TrailingStopRuleProcessor.class);

    private TrailingStopRule trailingRule;
    private double prevLastPrice = -1;
    private double stopOrderPrice = -1;
    private boolean isTrailing;

    public TrailingStopRuleProcessor(TrailingStopRule orderRule) {
        this.trailingRule = orderRule;
    }

    @Override
    public boolean processTicker(MarketTicker marketTicker) throws TriggeringRuleException {
        if (this.trailingRule.isTriggered()) {
            throw new TriggeringRuleException(this.trailingRule.getRuleId() + " has been triggered already.");
        }

        // This ticker is not about the market that this processor is interested in.
        if (!this.trailingRule.getMarketId().equals(marketTicker.getMarketId())) {
            // Just ignore.
            return false;
        }

        // We are only interested in the last price.
        final double lastPrice = marketTicker.getLastPrice();

        if (this.prevLastPrice >= 0) {
            if ((this.trailingRule.getTrailingDirection() == TrailingStopRule.directionType.below
                    && lastPrice <= this.prevLastPrice)
                    || (this.trailingRule.getTrailingDirection() == TrailingStopRule.directionType.above
                            && lastPrice >= this.prevLastPrice)) {
                this.isTrailing = true;
            } else {
                this.stopOrderPrice = lastPrice;
                this.isTrailing = false;
            }
        }
        if (this.stopOrderPrice < 0) {
            // If stopOrderPrice has never been set (this is the first call of
            // processTicker),
            // then set stopOrderPrice as current lastPrice.
            this.stopOrderPrice = lastPrice;
        }

        if (this.isTrailing) {
            double priceDifference = 0;
            if (this.trailingRule.getTrailingDirection() == TrailingStopRule.directionType.below) {
                priceDifference = this.stopOrderPrice - lastPrice;
            } else if (this.trailingRule.getTrailingDirection() == TrailingStopRule.directionType.above) {
                priceDifference = lastPrice - this.stopOrderPrice;
            }

            if (priceDifference < 0) {
                logger.warn(
                        "Price difference between stopOrderPrice and lastPrice can't be negative.\n" + " orderId: {}",
                        this.trailingRule.getRuleId());
            }

            if (this.trailingRule.getTrailingType() == TrailingStopRule.pointType.point) {
                if (priceDifference >= this.trailingRule.getTrailingPoints()) {
                    this.trailingRule.setTriggered(true);
                    this.trailingRule.setTriggeredPrice(lastPrice);
                    this.trailingRule.setTriggeredDate(new Date());

                    this.logger.info(
                            "Order is triggered. OrderId: {}, Triggered Price: {}, Previous Price: {}, StopOrder Price: {}",
                            this.trailingRule.getRuleId(), lastPrice, this.prevLastPrice, this.stopOrderPrice);
                    return true;
                }
            } else if (this.trailingRule.getTrailingType() == TrailingStopRule.pointType.percentage) {
                // TODO: Implement percentage logic
            }
        }

        this.prevLastPrice = lastPrice;
        return false;
    }
}