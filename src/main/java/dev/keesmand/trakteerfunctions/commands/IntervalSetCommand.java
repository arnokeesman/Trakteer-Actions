package dev.keesmand.trakteerfunctions.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.keesmand.trakteerfunctions.TrakteerFunctions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class IntervalSetCommand implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) {
        int interval = IntegerArgumentType.getInteger(context, "interval");
        int oldInterval = TrakteerFunctions.OPERATION_CONFIG.getInterval();

        if (oldInterval == interval) {
            context.getSource().sendError(Text.of("Same interval, nothing changed"));
            return 0;
        }

        TrakteerFunctions.OPERATION_CONFIG.setInterval(interval);

        context.getSource().sendFeedback(() -> Text.of(String.format("Set interval to %ds", interval)), true);

        return SINGLE_SUCCESS;
    }
}
