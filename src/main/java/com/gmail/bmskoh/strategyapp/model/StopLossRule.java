package com.gmail.bmskoh.strategyapp.model;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "STOP_ORDER_RULE")
@PrimaryKeyJoinColumn(name = "RULE_ID")
public class StopLossRule extends TriggeringRule {

    @Override
    public TriggeringRuleType getRuleType() {
        // TODO Auto-generated method stub
        return null;
    }
}