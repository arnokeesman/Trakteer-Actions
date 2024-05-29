package dev.keesmand.trakteeractions.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static dev.keesmand.trakteeractions.TrakteerActionsMod.OPERATION_CONFIG;

public class EnableCommand implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Text.of("This command can only be run as player"));
            return 0;
        }

        boolean verified = OPERATION_CONFIG.isVerified(player);
        String apiKey = OPERATION_CONFIG.getApiKey(player);
        if (apiKey != null && !apiKey.isEmpty() && !verified) {
            context.getSource().sendFeedback(() -> Text.of("Verifying API key..."), false);
            verified = OPERATION_CONFIG.verify(player);
        }

        if (!verified) {
            context.getSource().sendError(Text.of("Set your API key first"));
            return 0;
        }

        boolean oldValue = OPERATION_CONFIG.isEnabled(player);
        if (oldValue) {
            context.getSource().sendError(Text.of("Polling is already enabled for your account!"));
            return 0;
        }

        OPERATION_CONFIG.setEnabled(player, true);

        context.getSource().sendFeedback(() -> Text.of("Enabled polling."), false);

        return SINGLE_SUCCESS;
    }
}
