package com.gmail.bmskoh.strategyapp.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Table(name = "TRIGGERING_RULE")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class TriggeringRule {
    public enum TriggeringRuleType {
        trailingStop, stopOrder
    };

    @Id
    @Column(name = "RULE_ID")
    protected String ruleId;

    @Column(name = "MARKET_ID")
    protected String marketId;

    @Column(name = "IS_TRIGGERED")
    protected boolean triggered;

    @Column(name = "TRIGGERED_PRICE")
    protected double triggeredPrice;

    @Column(name = "TRIGGERED_DATE")
    protected Date triggeredDate;

    public abstract TriggeringRuleType getRuleType();
}