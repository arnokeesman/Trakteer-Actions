package dev.keesmand.trakteeractions.util;

import dev.keesmand.trakteeractions.config.Action;
import dev.keesmand.trakteeractions.model.Donation;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;

import static dev.keesmand.trakteeractions.TrakteerActionsMod.ACTION_CONFIG;
import static dev.keesmand.trakteeractions.TrakteerActionsMod.COMMAND_QUEUE;

public class Game {
    public static void handleDonation(MinecraftServer server, Donation donation) {
        boolean offline = server.getPlayerManager().getPlayer(donation.receiver) == null;
        Action action = ACTION_CONFIG.getActionForDonation(donation, offline);

        handleAction(donation, action);
    }

    private static void handleAction(Donation donation, Action action) {
        List<Action> actions = new ArrayList<>();
        addChildren(actions, action);

        for (Action a : actions) {
            runCommands(donation, a);
        }
    }

    private static void addChildren(List<Action> actions, Action action) {
        actions.add(0, action);
        List<Action> children = action.includes.stream().map(i -> ACTION_CONFIG.getActionByName(i)).toList();
        for (int i = children.size() - 1; i >= 0; i--) {
            Action child = children.get(i);
            addChildren(actions, child);
        }
    }

    private static void runCommands(Donation donation, Action action) {
        for (String command : action.commands) {
            String parsedCommand = donation.parseString(command);
            COMMAND_QUEUE.add(parsedCommand);
        }
    }
}
