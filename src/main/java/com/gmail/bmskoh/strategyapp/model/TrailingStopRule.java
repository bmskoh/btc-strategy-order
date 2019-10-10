package com.gmail.bmskoh.strategyapp.model;

import java.util.Date;
import java.util.Objects;

/**
 * Basically TrailingOrder includes triggering rules. And once this order has
 * been triggered, triggeredPrice and triggeredDate are supposed to be set too.
 */
public class TrailingStopRule extends TriggeringRule {
    /**
     * Trailing point type. For point type, TrailingOrderProcessor will calculate
     * whether this order met the triggering condition or not by points(price)
     * difference.
     */
    public enum pointType {
        point, percentage
    }

    /**
     * Trailing direction type.
     */
    public enum directionType {
        above, below
    }

    private String ruleId;
    private String marketId;
    private boolean triggered;
    private double trailingPoints;
    private pointType trailingType;
    private directionType trailingDirection;
    private double triggeredPrice;
    private Date triggeredDate;

    public TrailingStopRule(String ruleId, String marketId, double trailingPoints, pointType trailingType,
            directionType trailingDirection) {
        this.ruleId = ruleId;
        this.marketId = marketId;
        this.triggered = false;
        this.trailingPoints = trailingPoints;
        this.trailingType = trailingType;
        this.trailingDirection = trailingDirection;
    }

    @Override
    public TriggeringRule.TriggeringRuleType getRuleType() {
        return TriggeringRuleType.trailingStop;
    }

    @Override
    public boolean equals(Object rule) {
        if (this == rule)
            return true;
        if (!(rule instanceof TrailingStopRule))
            return false;
        TrailingStopRule trailingOrder = (TrailingStopRule) rule;

        boolean isEqual = Objects.equals(this.ruleId, trailingOrder.ruleId)
                && Objects.equals(this.marketId, trailingOrder.marketId) && this.triggered == trailingOrder.triggered
                && this.trailingPoints == trailingOrder.trailingPoints
                && this.trailingDirection == trailingOrder.trailingDirection;

        if (this.triggered) {
            isEqual = isEqual && triggeredPrice == trailingOrder.triggeredPrice
                    && Objects.equals(this.triggeredDate, trailingOrder.triggeredDate);
        }
        return isEqual;
    }

    public String getRuleId() {
        return this.ruleId;
    }

    public String getMarketId() {
        return this.marketId;
    }

    public boolean isTriggered() {
        return this.triggered;
    }

    public boolean getTriggered() {
        return this.triggered;
    }

    public void setTriggered(boolean triggered) {
        this.triggered = triggered;
    }

    public double getTrailingPoints() {
        return this.trailingPoints;
    }

    public pointType getTrailingType() {
        return this.trailingType;
    }

    public directionType getTrailingDirection() {
        return this.trailingDirection;
    }

    public double getTriggeredPrice() {
        return this.triggeredPrice;
    }

    public void setTriggeredPrice(double triggeredPrice) {
        this.triggeredPrice = triggeredPrice;
    }

    public Date getTriggeredDate() {
        return this.triggeredDate;
    }

    public void setTriggeredDate(Date triggeredDate) {
        this.triggeredDate = triggeredDate;
    }
}