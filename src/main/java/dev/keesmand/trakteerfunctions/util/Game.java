package dev.keesmand.trakteerfunctions.util;

import dev.keesmand.trakteerfunctions.model.Donation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Game {
    private static final String idRegex = "\\[id:([0-9]+)]";

    public static void handleDonation(MinecraftServer server, Donation donation) {
        sendDonationMessage(server, donation);

        server.getCommandManager().executeWithPrefix(server.getCommandSource(), getFunctionCommand(donation));
    }

    private static void sendDonationMessage(MinecraftServer server, Donation donation) {
        Text donationMessage = Text.literal("[!]").formatted(Formatting.YELLOW)
                .append(Text.literal(donation.supporter_name).formatted(Formatting.GOLD))
                .append(Text.literal(" donated ").formatted(Formatting.WHITE))
                .append(Text.literal(String.format("Rp%d", donation.amount)).formatted(Formatting.GOLD))
                .append(Text.literal(String.format(": %s", donation.support_message.replaceAll(idRegex, "").trim()))
                        .formatted(Formatting.WHITE));

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            player.sendMessage(donationMessage);
        }
    }

    private static String getFunctionCommand(Donation donation) {


        int id = getActionId(donation);

        if (id < 0) return "function re:command/noid.mcfunction";

        return String.format("function re:command/%s/%d/%d.mcfunction", donation.unit_name, donation.quantity, id);

    }

    private static int getActionId(Donation donation) {
        Pattern r = Pattern.compile(idRegex);
        Matcher m = r.matcher(donation.support_message);

        if (m.find()) return Integer.parseInt(m.group(1));
        return -1;
    }
}
