package com.gmail.bmskoh.strategyapp.controllers;

import java.util.List;
import java.util.stream.Collectors;

import com.gmail.bmskoh.strategyapp.model.TrailingStopRule;
import com.gmail.bmskoh.strategyapp.processors.TriggeringRuleLoader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TrailingRuleController {

    @Autowired
    TriggeringRuleLoader triggeringRuleLoader;

    @GetMapping(path = "/trailingrule")
    public List<TrailingStopRule> trailingRules() {
        return triggeringRuleLoader.loadTriggeringRules().stream()
                .map(triggeringRule -> (TrailingStopRule) triggeringRule).collect(Collectors.toList());
    }

    // @PutMapping(path = "/trailingrule")
    // public List<TrailingStopRule> trailingRules() {
    // return triggeringRuleLoader.loadTriggeringRules().stream()
    // .map(triggeringRule -> (TrailingStopRule)
    // triggeringRule).collect(Collectors.toList());
    // }
}