package dev.keesmand.trakteerfunctions.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import dev.keesmand.trakteerfunctions.TrakteerFunctions;
import dev.keesmand.trakteerfunctions.config.UserSettings;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class StatusCommand implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Text.of("This command can only be run as player"));
            return 0;
        }

        UserSettings settings = TrakteerFunctions.OPERATION_CONFIG.getUserSettings(player);
        if (settings == null) {
            context.getSource().sendFeedback(() -> Text.of("Not configured"), false);
            return 0;
        }

        String message = String.format("[%s] status", TrakteerFunctions.MOD_METADATA.getName()) +
                String.format("\nAPI key verified: %s", settings.isVerified() ? "yes" : "no") +
                String.format("\nPolling enabled: %s", settings.isEnabled() ? "yes" : "no");
        context.getSource().sendFeedback(() -> Text.of(message), false);

        return SINGLE_SUCCESS;
    }
}
