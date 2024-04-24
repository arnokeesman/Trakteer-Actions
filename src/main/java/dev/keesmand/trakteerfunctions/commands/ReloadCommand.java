package dev.keesmand.trakteerfunctions.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.keesmand.trakteerfunctions.TrakteerFunctions;
import dev.keesmand.trakteerfunctions.config.ConfigManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class ReloadCommand implements Command<ServerCommandSource> {

    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();

        try {
            TrakteerFunctions.ACTION_CONFIG = ConfigManager.readActionConfig();
            source.sendFeedback(() -> Text.of(TrakteerFunctions.logPrefix + "New Actions config loaded successfully!"), true);
            return SINGLE_SUCCESS;
        } catch (Exception e) {
            TrakteerFunctions.ACTION_CONFIG = null;
            source.sendError(Text.of(String.format("Error reloading config: %s\nMod disabled.", e)));
        }

        return 0;
    }
}
