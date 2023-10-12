package dev.keesmand.trakteerfunctions.model;

public class Donation {
    public final String supporter_name;
    public final String support_message;
    public final int quantity;
    public final int amount;
    public final String unit_name;
    public final String updated_at;

    public Donation(String supporter_name, String supporter_message, int quantity, int amount, String unit_name, String updated_at) {
        this.supporter_name = supporter_name;
        this.support_message = supporter_message;
        this.quantity = quantity;
        this.amount = amount;
        this.unit_name = unit_name;
        this.updated_at = updated_at;
    }
}
