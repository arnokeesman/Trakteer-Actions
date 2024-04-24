package dev.keesmand.trakteerfunctions.config;

import dev.keesmand.trakteerfunctions.model.Donation;

import java.util.ArrayList;
import java.util.List;

public class Action {
    public final String name;
    public final List<ActionRule> rules;
    public final List<String> includes;
    public final List<String> commands;
    public boolean offline;

    public Action(String name, boolean offline, List<ActionRule> rules, List<String> includes, List<String> commands) {
        this.name = name;
        this.offline = offline;
        this.rules = rules;
        this.includes = includes;
        this.commands = commands;
    }

    public Action(String name, List<ActionRule> rules, List<String> includes, List<String> commands) {
        this.name = name;
        this.offline = false;
        this.rules = rules;
        this.includes = includes;
        this.commands = commands;
    }

    public Action(String name) {
        this.name = name;
        this.offline = false;
        this.rules = new ArrayList<>();
        this.includes = new ArrayList<>();
        this.commands = new ArrayList<>();
    }

    public boolean check(Donation donation) {
        return rules.stream().allMatch(rule -> rule.check(donation));
    }
}
