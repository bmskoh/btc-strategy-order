package com.gmail.bmskoh.strategyapp.controllers;

import java.util.List;
import java.util.stream.Collectors;

import com.gmail.bmskoh.strategyapp.model.TrailingStopRule;
import com.gmail.bmskoh.strategyapp.processors.TriggeringRuleLoader;
import com.gmail.bmskoh.strategyapp.repositories.TriggeringRuleRepository;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Temporary implementation.
 * TODO: Persistence should be done through IStrategyOrderService. IStrategyOrderService should be capable
 * of persisting data and update in memory triggering rules at the same time in a thread safe manner.
 */
@RestController
public class TrailingRuleController {

    TriggeringRuleLoader triggeringRuleLoader;

    TriggeringRuleRepository triggeringRuleRepository;

    TrailingRuleController(TriggeringRuleLoader triggeringRuleLoader,
            TriggeringRuleRepository triggeringRuleRepository) {
        this.triggeringRuleLoader = triggeringRuleLoader;
        this.triggeringRuleRepository = triggeringRuleRepository;
    }

    @GetMapping("/trailingrules")
    public List<TrailingStopRule> allTrailingRules() {
        return triggeringRuleLoader.loadTriggeringRules().stream()
                .map(triggeringRule -> (TrailingStopRule) triggeringRule).collect(Collectors.toList());
    }

    @GetMapping("/trailingrules/{ruleId}")
    public TrailingStopRule oneTrailingRule(@PathVariable String ruleId) {
        return (TrailingStopRule) triggeringRuleRepository.findById(ruleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find rule: " + ruleId));
    }

    // TODO: Apply changes to the rules list in OrderProcessorManager on runtime
    @PostMapping("/trailingrules")
    public TrailingStopRule newTrailingRule(@RequestBody TrailingStopRule rule) {
        return triggeringRuleRepository.save(rule);
    }

    @PutMapping("/trailingrules/{ruleId}")
    public TrailingStopRule updateTrailingRule(@RequestBody TrailingStopRule ruleParam, @PathVariable String ruleId) {
        return triggeringRuleRepository.findById(ruleId).map(rule -> {
            TrailingStopRule trailingRule = (TrailingStopRule) rule;
            trailingRule.setMarketId(ruleParam.getMarketId());
            trailingRule.setTrailingPoints(ruleParam.getTrailingPoints());
            trailingRule.setTrailingType(ruleParam.getTrailingType());
            trailingRule.setTrailingDirection(ruleParam.getTrailingDirection());
            return triggeringRuleRepository.save(trailingRule);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find rule: " + ruleId));
    }

    @DeleteMapping("/trailingrules/{ruleId}")
    public void deleteRule(@PathVariable String ruleId) {
        triggeringRuleRepository.deleteById(ruleId);
    }
}