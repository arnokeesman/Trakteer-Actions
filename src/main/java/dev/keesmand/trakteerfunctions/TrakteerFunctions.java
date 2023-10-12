package dev.keesmand.trakteerfunctions;

import dev.keesmand.trakteerfunctions.commands.TrakteerFunctionsCommand;
import dev.keesmand.trakteerfunctions.config.TrakteerFunctionsConfig;
import dev.keesmand.trakteerfunctions.config.TrakteerFunctionsConfigManager;
import dev.keesmand.trakteerfunctions.model.Donation;
import dev.keesmand.trakteerfunctions.util.Web;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TrakteerFunctions implements ModInitializer {
    public static final String MOD_CONTAINER_ID = "trakteer-functions";
    public static final ModMetadata MOD_METADATA = FabricLoader.getInstance().getModContainer(MOD_CONTAINER_ID).map(ModContainer::getMetadata).orElse(null);
    public static final String MOD_ID = MOD_METADATA != null ? MOD_METADATA.getId() : "trakteer-functions | I guess, couldn't find it";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static TrakteerFunctionsConfig CONFIG;
    public static Set<String> knownTimestamps;

    @Override
    public void onInitialize() {
        log("loading...");
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.getRoot().addChild(TrakteerFunctionsCommand.register()));

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            CONFIG = TrakteerFunctionsConfigManager.read(server);
            if (CONFIG.isValid()) {
                try {
                    initializeDonations();
                } catch (IOException ignored) {}
            }
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            CONFIG.save(TrakteerFunctionsConfigManager.getSettingsFile(server));
            CONFIG = null;
        });

        log("loaded!");
    }

    public static void initializeDonations() throws IOException {
        clearKnowTimestamps();
        Donation[] donations = Web.getLatestDonations(CONFIG.getApiKey());
        if (donations == null) return;

        knownTimestamps = new HashSet<>();
        knownTimestamps.addAll(Arrays.stream(donations).map(dono -> dono.updated_at).toList());
    }

    public static void clearKnowTimestamps() {
        knownTimestamps = null;
    }

    private static final String logPrefix = "["+MOD_METADATA.getName()+"] ";
    public static void log(String message) {
        LOGGER.info(logPrefix+message);
    }
    public static void error(String message) {
        LOGGER.error(logPrefix+message);
    }
}
