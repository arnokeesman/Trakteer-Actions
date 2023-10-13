package dev.keesmand.trakteerfunctions.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import dev.keesmand.trakteerfunctions.TrakteerFunctions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class InfoCommand implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) {
        context.getSource().sendFeedback(() -> Text.of("Running Trakteer Functions v"+TrakteerFunctions.MOD_METADATA.getVersion().getFriendlyString()), false);
        if (context.getSource().hasPermissionLevel(4)) {
            boolean valid = TrakteerFunctions.CONFIG.isValid();
            context.getSource().sendFeedback(
                    () -> Text.of(valid ? "Polling is active" : "Config is not valid"),
                    false);

        }
        return 1;
    }
}
