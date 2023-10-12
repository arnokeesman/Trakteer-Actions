package dev.keesmand.trakteerfunctions.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.keesmand.trakteerfunctions.TrakteerFunctions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.io.IOException;
import java.net.ConnectException;

public class IntervalSetCommand implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) {
        int interval = IntegerArgumentType.getInteger(context, "interval");

        boolean wasValid = TrakteerFunctions.CONFIG.isValid();
        try {
            TrakteerFunctions.CONFIG.setInterval(interval);
        } catch (IOException ioe) {
            if (ioe instanceof ConnectException) {
                context.getSource().sendError(Text.of("Unable to connect to this server, not setting interval. Try again later?"));
                try { TrakteerFunctions.CONFIG.setInterval(0); } catch (IOException ignored) {}
            } else {
                context.getSource().sendError(Text.of("Invalid API configuration: " + ioe.getMessage()));
                context.getSource().sendError(Text.of("Removing API key."));
                try { TrakteerFunctions.CONFIG.setApiKey(""); } catch (IOException ignored) {}
            }
            return 0;
        }

        if (interval <= 0) context.getSource().sendFeedback(() -> Text.of("Disabled interval"), false);
        else context.getSource().sendFeedback(() -> Text.of(String.format("Set interval to %ds", interval)), false);
        if (!wasValid && TrakteerFunctions.CONFIG.isValid())
            context.getSource().sendFeedback(() -> Text.of("Api polling is now enabled"), false);
        return 1;
    }
}
