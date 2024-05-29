package dev.keesmand.trakteeractions.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static dev.keesmand.trakteeractions.TrakteerActionsMod.OPERATION_CONFIG;

public class DisableCommand implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Text.of("This command can only be run as player"));
            return 0;
        }

        boolean oldValue = OPERATION_CONFIG.isEnabled(player);
        if (!oldValue) {
            context.getSource().sendError(Text.of("Polling is already disabled for your account!"));
            return 0;
        }

        OPERATION_CONFIG.setEnabled(player, false);

        context.getSource().sendFeedback(() -> Text.of("Disabled polling."), false);

        return SINGLE_SUCCESS;
    }
}
