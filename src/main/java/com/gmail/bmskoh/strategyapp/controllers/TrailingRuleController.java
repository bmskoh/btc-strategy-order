package com.gmail.bmskoh.strategyapp.controllers;

import java.util.ArrayList;
import java.util.List;

import com.gmail.bmskoh.strategyapp.model.TrailingStopRule;
import com.gmail.bmskoh.strategyapp.processors.ITriggeringRuleManager;
import com.gmail.bmskoh.strategyapp.processors.TriggeringRuleNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest controller for trailing rules.
 */
@RestController
public class TrailingRuleController {

    ITriggeringRuleManager triggeringRuleManager;

    TrailingRuleController(final ITriggeringRuleManager triggeringRuleManager) {
        this.triggeringRuleManager = triggeringRuleManager;
    }

    @GetMapping("/trailingrules")
    public List<TrailingStopRule> allTrailingRules() {
        return this.triggeringRuleManager.getAllTrailingRules();
    }

    @GetMapping("/trailingrules/{ruleId}")
    public TrailingStopRule oneTrailingRule(@PathVariable final String ruleId) throws TriggeringRuleNotFoundException {
        return this.triggeringRuleManager.getTrailingRule(ruleId);
    }

    @PostMapping("/trailingrules")
    public TrailingStopRule newTrailingRule(@RequestBody final TrailingStopRule rule) {
        return triggeringRuleManager.addTrailingRule(rule);
    }

    @PutMapping("/trailingrules/{ruleId}")
    public void updateTrailingRule(@RequestBody final TrailingStopRule ruleParam, @PathVariable final String ruleId)
            throws TriggeringRuleNotFoundException {
        ruleParam.setRuleId(ruleId);
        this.triggeringRuleManager.updateTrailingRule(ruleParam);
    }

    @DeleteMapping("/trailingrules/{ruleId}")
    public void deleteRule(@PathVariable final String ruleId) throws TriggeringRuleNotFoundException {
        this.triggeringRuleManager.deleteTrailingRule(ruleId);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(TriggeringRuleNotFoundException.class)
    public List<String> handleTriggeringRuleNotFoundException(TriggeringRuleNotFoundException ex) {
        List<String> error = new ArrayList<>();
        error.add(ex.getMessage());
        return error;
    }
}