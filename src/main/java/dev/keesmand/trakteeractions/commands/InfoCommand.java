package dev.keesmand.trakteeractions.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import dev.keesmand.trakteeractions.TrakteerActionsMod;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class InfoCommand implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) {
        context.getSource().sendFeedback(() -> Text.of(String.format("Running Trakteer Functions v%s",
                TrakteerActionsMod.MOD_METADATA.getVersion().getFriendlyString())), false);

        if (context.getSource().hasPermissionLevel(4)) {
            int activeUsers = TrakteerActionsMod.OPERATION_CONFIG.getReadyUserSettings().size();
            context.getSource().sendFeedback(
                    () -> Text.of(TrakteerActionsMod.isObstructed()
                                    ? "Mod disabled due to bad config."
                                    : String.format("Polling for %d users", activeUsers)
                            ),
                    false);
        }

        return SINGLE_SUCCESS;
    }
}
