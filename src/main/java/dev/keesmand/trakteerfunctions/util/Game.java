package dev.keesmand.trakteerfunctions.util;

import dev.keesmand.trakteerfunctions.config.Action;
import dev.keesmand.trakteerfunctions.model.Donation;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;

import static dev.keesmand.trakteerfunctions.TrakteerFunctions.ACTION_CONFIG;

public class Game {
    public static void handleDonation(MinecraftServer server, Donation donation) {
        Action action = ACTION_CONFIG.getActionForDonation(donation);
        boolean offline = server.getPlayerManager().getPlayer(donation.receiver) == null;

        handleAction(server, donation, action, offline);
    }

    private static void handleAction(MinecraftServer server, Donation donation, Action action, boolean offline) {
        List<Action> actions = new ArrayList<>();
        addChildren(actions, action);

        for (Action a : actions) {
            runCommands(server, donation, a, offline);
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

    private static void runCommands(MinecraftServer server, Donation donation, Action action, boolean offline) {
        if (!offline || action.offline) {
            for (String command : action.commands) {
                String parsedCommand = donation.parseString(command);
                server.getCommandManager().executeWithPrefix(server.getCommandSource(), parsedCommand);
            }
        }
    }
}
