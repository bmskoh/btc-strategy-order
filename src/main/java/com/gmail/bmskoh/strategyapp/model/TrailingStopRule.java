package com.gmail.bmskoh.strategyapp.model;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Basically TrailingOrder includes triggering rules. And once this order has
 * been triggered, triggeredPrice and triggeredDate are supposed to be set too.
 */
@Entity
@Table(name = "TRAILING_STOP_RULE")
@PrimaryKeyJoinColumn(name = "RULE_ID")
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

    @Column(name = "TRAILING_POINT")
    @NotNull(message = "Traling points is required.")
    private double trailingPoints;

    @Column(name = "TRAILING_TYPE")
    @NotNull(message = "Trailing type is required.")
    private pointType trailingType;

    @Column(name = "TRAILING_DIRECTION")
    @NotNull(message = "Trailing direction is required.")
    private directionType trailingDirection;

    public TrailingStopRule() {
    }

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

    public String getRuleId() {
        return this.ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getMarketId() {
        return this.marketId;
    }

    public void setMarketId(String marketId) {
        this.marketId = marketId;
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

    public void setTrailingPoints(double points) {
        this.trailingPoints = points;
    }

    public pointType getTrailingType() {
        return this.trailingType;
    }

    public void setTrailingType(pointType pointType) {
        this.trailingType = pointType;
    }

    public directionType getTrailingDirection() {
        return this.trailingDirection;
    }

    public void setTrailingDirection(directionType direction) {
        this.trailingDirection = direction;
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

    @Override
    public int hashCode() {
        return Objects.hash(ruleId, trailingPoints, trailingType, trailingDirection);
    }

    @Override
    public String toString() {
        return "{" +
            " trailingPoints='" + getTrailingPoints() + "'" +
            ", trailingType='" + getTrailingType() + "'" +
            ", trailingDirection='" + getTrailingDirection() + "'" +
            "}";
    }

}