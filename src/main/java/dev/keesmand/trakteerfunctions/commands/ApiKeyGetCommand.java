package dev.keesmand.trakteerfunctions.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import dev.keesmand.trakteerfunctions.TrakteerFunctions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.*;

public class ApiKeyGetCommand implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) {
        String apiKey = TrakteerFunctions.CONFIG.getApiKey();
        context.getSource().sendFeedback(
                () -> {
                    if (apiKey.isEmpty()) return Text.of("No API key is set");
                    MutableText key = Text.literal("########")
                            .setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, apiKey)));
                    return Text.literal("Click to copy API key to clipboard: ").append(key);
                },
                false);
        return 1;
    }
}
