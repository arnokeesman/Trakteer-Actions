package dev.keesmand.trakteerfunctions.config;

import dev.keesmand.trakteerfunctions.model.Donation;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.MissingFormatArgumentException;

public class ActionRule {
    public static final List<String> keys = Arrays.stream(Donation.class.getFields()).map(Field::getName).toList();
    public static final List<String> operations = List.of("contains", "equals", "=", "gte", ">=", "lte", "<=", "gt", ">", "lt", "<");
    public final String key;
    public final String operation;
    public final String value;

    public ActionRule(String key, String operation, String value) {
        if (!keys.contains(key)) throw new MissingFormatArgumentException("you can only check against these ");

        this.key = key;
        this.operation = operation;
        this.value = value;
    }

    @SuppressWarnings("DataFlowIssue")
    public boolean check(Donation donation) {
        return switch (operation) {
            case "contains" -> getStringFromDonation(donation, this.key).contains(this.value);
            case "equals", "=" -> getStringFromDonation(donation, this.key).equals(this.value);
            case "gte", ">=" -> getIntFromDonation(donation, this.key) >= Integer.parseInt(this.value);
            case "lte", "<=" -> getIntFromDonation(donation, this.key) <= Integer.parseInt(this.value);
            case "gt", ">" -> getIntFromDonation(donation, this.key) > Integer.parseInt(this.value);
            case "lt", "<" -> getIntFromDonation(donation, this.key) < Integer.parseInt(this.value);
            default ->
                    throw new MissingFormatArgumentException(String.format("Operation %s does not exist", operation));
        };
    }

    private String getStringFromDonation(Donation donation, String key) {
        try {
            Field field = donation.getClass().getField(key);
            return field.get(donation).toString();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }

    private Integer getIntFromDonation(Donation donation, String key) {
        try {
            Field field = donation.getClass().getField(key);
            return Integer.parseInt(field.get(donation).toString());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", key, operation, value);
    }
}
