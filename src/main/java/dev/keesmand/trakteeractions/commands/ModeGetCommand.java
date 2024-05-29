package dev.keesmand.trakteeractions.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import dev.keesmand.trakteeractions.TrakteerActionsMod;
import dev.keesmand.trakteeractions.model.OperationMode;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class ModeGetCommand implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) {
        OperationMode mode = TrakteerActionsMod.OPERATION_CONFIG.getMode();

        context.getSource().sendFeedback(
                () -> Text.of(String.format("Mode is %s", mode.name())),
                false);

        return SINGLE_SUCCESS;
    }
}
