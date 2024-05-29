package dev.keesmand.trakteeractions.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class TrakteerModCommand {
    public static LiteralCommandNode<ServerCommandSource> register() {
        LiteralCommandNode<ServerCommandSource> baseNode = CommandManager
                .literal("trakteermod")
                .executes(new InfoCommand())
                .build();

        LiteralCommandNode<ServerCommandSource> statusNode = CommandManager
                .literal("status")
                .executes(new StatusCommand())
                .build();

        LiteralCommandNode<ServerCommandSource> intervalNode = CommandManager
                .literal("interval")
                .requires(ctx -> ctx.hasPermissionLevel(4))
                .executes(new IntervalGetCommand())
                .then(CommandManager.argument("interval", IntegerArgumentType.integer())
                        .executes(new IntervalSetCommand())
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

        LiteralCommandNode<ServerCommandSource> apiKeyNode = CommandManager
                .literal("apiKey")
                .executes(new ApiKeyGetCommand())
                .then(CommandManager.argument("api_key", StringArgumentType.word())
                        .executes(new ApiKeySetCommand())
                )
                .build();

        LiteralCommandNode<ServerCommandSource> enableNode = CommandManager
                .literal("enable")
                .executes(new EnableCommand())
                .build();

        LiteralCommandNode<ServerCommandSource> disableNode = CommandManager
                .literal("disable")
                .executes(new DisableCommand())
                .build();

        LiteralCommandNode<ServerCommandSource> reloadNode = CommandManager
                .literal("reload")
                .requires(ctx -> ctx.hasPermissionLevel(4))
                .executes(new ReloadCommand())
                .build();

        baseNode.addChild(statusNode);
        baseNode.addChild(intervalNode);
        baseNode.addChild(modeNode);
        baseNode.addChild(apiKeyNode);
        baseNode.addChild(enableNode);
        baseNode.addChild(disableNode);
        baseNode.addChild(reloadNode);

        return baseNode;
    }
}
