package com.gmail.bmskoh.strategyapp.repositories;

import com.gmail.bmskoh.strategyapp.model.TrailingStopRule;

import org.springframework.data.repository.CrudRepository;

public interface TrailingRuleRepository extends CrudRepository<TrailingStopRule, String> {

}