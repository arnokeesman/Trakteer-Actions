package dev.keesmand.trakteerfunctions;

import dev.keesmand.trakteerfunctions.commands.TrakteerFunctionsCommand;
import dev.keesmand.trakteerfunctions.config.ActionConfig;
import dev.keesmand.trakteerfunctions.config.ConfigManager;
import dev.keesmand.trakteerfunctions.config.OperationConfig;
import dev.keesmand.trakteerfunctions.config.UserSettings;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class TrakteerFunctions implements ModInitializer {
    public static final String MOD_CONTAINER_ID = "trakteer-functions";
    public static final ModMetadata MOD_METADATA = FabricLoader.getInstance().getModContainer(MOD_CONTAINER_ID)
            .map(ModContainer::getMetadata).orElse(null);
    public static final String MOD_ID = MOD_METADATA != null ? MOD_METADATA.getId() : "trakteer-functions | I guess, couldn't find it";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static final String logPrefix = "[" + (MOD_METADATA != null ? MOD_METADATA.getName() : MOD_CONTAINER_ID) + "] ";
    public static ActionConfig ACTION_CONFIG;
    public static OperationConfig OPERATION_CONFIG;
    public static Map<UUID, Set<String>> knownTimestamps;
    public static boolean modEnabled = false;

    public static void initializeDonations() {
        knownTimestamps = new HashMap<>();
        List<UserSettings> userSettingsList = OPERATION_CONFIG.getUserSettingsList();
        for (UserSettings userSettings : userSettingsList) {
            boolean verified = userSettings.verify();
            if (!verified) OPERATION_CONFIG.setEnabled(userSettings.uuid, false);
        }
    }

    public static void log(String message) {
        LOGGER.info("{}{}", logPrefix, message);
    }

    public static void error(String message) {
        LOGGER.error("{}{}", logPrefix, message);
    }

    @Override
    public void onInitialize() {
        log("loading...");
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.getRoot()
                .addChild(TrakteerFunctionsCommand.register()));

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            log("Reading Configurations");
            try {
                ACTION_CONFIG = ConfigManager.readActionConfig();
                OPERATION_CONFIG = ConfigManager.readOperationConfig(server);
                log("Configurations loaded");
                modEnabled = true;
            } catch (Exception e) {
                error("Failed to load configs:\n" + e);
                modEnabled = false;
            }

            if (!modEnabled) return;

            initializeDonations();

            log(String.format("%d accounts scanning for donations", OPERATION_CONFIG.getReadyUserSettings().size()));
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            if (!modEnabled && player.hasPermissionLevel(4)) {
                player.sendMessage(Text.literal("[Trakteer Functions] actions config invalid, mod disabled.")
                        .formatted(Formatting.RED), false);
                return;
            }
            String currentApiKey = OPERATION_CONFIG.getApiKey(player);
            boolean apiKeySet = currentApiKey != null && !currentApiKey.isEmpty();
            boolean enabled = OPERATION_CONFIG.isEnabled(player);

            if (apiKeySet && !enabled) {
                player.sendMessage(Text.of("[Trakteer Functions] you have an API key set, be sure to enable " +
                        "polling if you want to run actions from donations. Remove your API key to disable this message."));
            }
            if (!apiKeySet && enabled) {
                player.sendMessage(Text.of("[Trakteer Functions] Polling is enabled but API key is missing, " +
                        "please set your new API key."));
            }
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            ACTION_CONFIG = null;
            OPERATION_CONFIG.save(ConfigManager.getSettingsFile(server));
            OPERATION_CONFIG = null;
        });

        log("loaded!");
    }
}
