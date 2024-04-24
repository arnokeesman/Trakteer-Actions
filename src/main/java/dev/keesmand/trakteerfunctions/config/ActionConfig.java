package dev.keesmand.trakteerfunctions.config;

import dev.keesmand.trakteerfunctions.model.Donation;

import java.util.List;

public class ActionConfig {
    public final Action defaultAction;
    public final List<Action> actions;

    public ActionConfig(Action defaultAction, List<Action> actions) {
        this.defaultAction = defaultAction;
        this.actions = actions;
    }

    public Action getActionForDonation(Donation donation) {
        return actions.stream()
                .filter(action -> action.check(donation))
                .findFirst()
                .orElse(this.defaultAction);
    }

    public Action getActionByName(String name) {
        if (name.equals("default")) return defaultAction;
        return actions.stream()
                .filter(a -> a.name.equals(name))
                .findFirst().orElse(null);
    }
}
