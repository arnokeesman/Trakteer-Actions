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
                .executes(new InfoCommand())
                .build();

        LiteralCommandNode<ServerCommandSource> intervalNode = CommandManager
                .literal("interval")
                .requires(ctx -> ctx.hasPermissionLevel(4))
                .executes(new IntervalGetCommand())
                .then(CommandManager.argument("interval", IntegerArgumentType.integer())
                        .executes(new IntervalSetCommand())
                )
                .build();

        LiteralCommandNode<ServerCommandSource> apiKeyNode = CommandManager
                .literal("apiKey")
                .requires(ctx -> ctx.hasPermissionLevel(4))
                .executes(new ApiKeyGetCommand())
                .then(CommandManager.argument("api_key", StringArgumentType.word())
                        .executes(new ApiKeySetCommand())
                )
                .build();

        LiteralCommandNode<ServerCommandSource> modeNode = CommandManager
                .literal("mode")
                .requires(ctx -> ctx.hasPermissionLevel(4))
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
