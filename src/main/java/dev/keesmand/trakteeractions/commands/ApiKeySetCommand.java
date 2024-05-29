package dev.keesmand.trakteeractions.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static dev.keesmand.trakteeractions.TrakteerActionsMod.OPERATION_CONFIG;

public class ApiKeySetCommand implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Text.of("This command can only be run as player"));
            return 0;
        }

        String apiKey = StringArgumentType.getString(context, "api_key");
        String currentApiKey = OPERATION_CONFIG.getApiKey(player);

        if (currentApiKey != null && currentApiKey.equals(apiKey)) {
            context.getSource().sendFeedback(() -> Text.of("API key already set, verifying..."), false);
            boolean result = OPERATION_CONFIG.verify(player);
            if (result) {
                context.getSource().sendFeedback(() -> Text.of("API key verified!"), false);
            } else {
                context.getSource().sendError(Text.of("API key verification failed"));
            }
            return 0;
        }

        try {
            OPERATION_CONFIG.setApiKey(player, apiKey);
        } catch (Exception e) {
            context.getSource().sendError(Text.of(String.format("Error: %s", e.getMessage())));
            return 0;
        }

        boolean verified = OPERATION_CONFIG.verify(player);

        if (!verified) {
            context.getSource().sendError(Text.of("API key verification failed"));

            try {
                OPERATION_CONFIG.setApiKey(player, "");
            } catch (Exception ignored) {
            }

            return 0;
        }

        context.getSource().sendFeedback(() -> Text.of("API key set"), false);

        return SINGLE_SUCCESS;
    }
}
