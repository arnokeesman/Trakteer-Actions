package dev.keesmand.trakteerfunctions.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import static dev.keesmand.trakteerfunctions.TrakteerFunctions.OPERATION_CONFIG;

public class ApiKeyGetCommand implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Text.of("This command can only be run as player"));
            return 0;
        }

        String apiKey = OPERATION_CONFIG.getApiKey(player);
        context.getSource().sendFeedback(
                () -> {
                    if (apiKey == null || apiKey.isEmpty()) return Text.of("No API key is set");
                    MutableText key = Text.literal("########")
                            .setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, apiKey)));
                    return Text.literal("Click to copy API key to clipboard: ").append(key);
                },
                false);
        return SINGLE_SUCCESS;
    }
}
