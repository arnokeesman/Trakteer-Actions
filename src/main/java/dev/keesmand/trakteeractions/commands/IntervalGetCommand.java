package dev.keesmand.trakteeractions.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import dev.keesmand.trakteeractions.TrakteerActionsMod;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class IntervalGetCommand implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) {
        int interval = TrakteerActionsMod.OPERATION_CONFIG.getInterval();

        context.getSource().sendFeedback(
                () -> Text.of(interval <= 0 ? "Interval is disabled" : String.format("Interval is %ds", interval)),
                false);

        return SINGLE_SUCCESS;
    }
}
