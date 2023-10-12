package dev.keesmand.trakteerfunctions.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import dev.keesmand.trakteerfunctions.TrakteerFunctions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class IntervalGetCommand implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) {
        int interval = TrakteerFunctions.CONFIG.getInterval();
        context.getSource().sendFeedback(
                () -> Text.of(interval <= 0 ? "Interval is disabled" : String.format("Interval is %ds", interval)),
                false);
        return 1;
    }
}
