package dev.keesmand.trakteerfunctions.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class TrakteerFunctionsCommand {
    public static LiteralCommandNode<ServerCommandSource> register() {
        LiteralCommandNode<ServerCommandSource> baseNode = CommandManager
                .literal("trakteer-functions")
                .requires(ctx -> ctx.hasPermissionLevel(4))
                .build();

        LiteralCommandNode<ServerCommandSource> intervalNode = CommandManager
                .literal("interval")
                .executes(new IntervalGetCommand())
                .then(CommandManager.argument("interval", IntegerArgumentType.integer())
                        .executes(new IntervalSetCommand())
                )
                .build();

        LiteralCommandNode<ServerCommandSource> apiKeyNode = CommandManager
                .literal("apiKey")
                .executes(new ApiKeyGetCommand())
                .then(CommandManager.argument("api_key", StringArgumentType.word())
                        .executes(new ApiKeySetCommand())
                )
                .build();

        LiteralCommandNode<ServerCommandSource> modeNode = CommandManager
                .literal("mode")
                .executes(new ModeGetCommand())
                .then(CommandManager.argument("mode", StringArgumentType.word())
                        .suggests(new ModeSetCommand())
                        .executes(new ModeSetCommand())
                )
                .build();

        baseNode.addChild(intervalNode);
        baseNode.addChild(apiKeyNode);
        baseNode.addChild(modeNode);

        return baseNode;
    }
}
