package dev.keesmand.trakteerfunctions.config;

import dev.keesmand.trakteerfunctions.model.Donation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ActionRuleTest {
    @Test
    void check() {
        ActionRule rule = new ActionRule("amount", ">=", "10000");
        Donation donation = new Donation("Arno", "kaboom", 5, 10000, "creeper", "sometime");

        boolean result = rule.check(donation);

        assertTrue(result);
    }
}