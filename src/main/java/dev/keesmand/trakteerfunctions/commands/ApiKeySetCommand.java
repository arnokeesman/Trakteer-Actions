package dev.keesmand.trakteerfunctions.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.keesmand.trakteerfunctions.TrakteerFunctions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.io.IOException;
import java.net.ConnectException;

public class ApiKeySetCommand implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) {
        String apiKey = StringArgumentType.getString(context, "api_key");
        boolean wasValid = TrakteerFunctions.CONFIG.isValid();

        try {
            TrakteerFunctions.CONFIG.setApiKey(apiKey);
        } catch (IOException ioe) {
            String message;
            if (ioe instanceof ConnectException) {
                message = "Unable to reach the server, try again later?";
            } else {
                message = "Invalid API configuration: " + ioe.getMessage();
            }
            message += "\nClearing API key.";
            context.getSource().sendError(Text.of(message));
            try { TrakteerFunctions.CONFIG.setApiKey(""); } catch (IOException ignored) {}
            return 0;
        }

        if (apiKey.isEmpty()) context.getSource().sendFeedback(() -> Text.of("Removed API key"), false);
        else context.getSource().sendFeedback(() -> Text.of("Set API key"), false);
        if (!wasValid && TrakteerFunctions.CONFIG.isValid())
            context.getSource().sendFeedback(() -> Text.of("Api polling is now enabled"), false);
        return 1;
    }
}
