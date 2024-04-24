package dev.keesmand.trakteerfunctions.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import dev.keesmand.trakteerfunctions.TrakteerFunctions;
import dev.keesmand.trakteerfunctions.model.OperationMode;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class ModeGetCommand implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) {
        OperationMode mode = TrakteerFunctions.OPERATION_CONFIG.getMode();

        context.getSource().sendFeedback(
                () -> Text.of(String.format("Mode is %s", mode.name())),
                false);

        return SINGLE_SUCCESS;
    }
}
