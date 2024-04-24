package dev.keesmand.trakteerfunctions.mixin;

import com.mojang.authlib.GameProfile;
import dev.keesmand.trakteerfunctions.TrakteerFunctions;
import dev.keesmand.trakteerfunctions.config.UserSettings;
import dev.keesmand.trakteerfunctions.model.Donation;
import dev.keesmand.trakteerfunctions.util.Game;
import dev.keesmand.trakteerfunctions.util.Web;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.BooleanSupplier;

import static dev.keesmand.trakteerfunctions.TrakteerFunctions.OPERATION_CONFIG;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Unique
    private final Thread.UncaughtExceptionHandler exceptionHandler = (thread, throwable) ->
            TrakteerFunctions.LOGGER.error("Exception while reading from API", throwable);
    @Unique
    private final Executor threadExecutor = Executors.newCachedThreadPool(runnable -> {
        var thread = new Thread(runnable, "Trakteer API Thread");

        thread.setUncaughtExceptionHandler(exceptionHandler);

        return thread;
    });

    @Unique
    private static @Nullable UserSettings getUserToCheck(MinecraftServer server) {
        int timeBetween = OPERATION_CONFIG.getInterval() * 20;
        List<UserSettings> readyUserSettings = OPERATION_CONFIG.getReadyUserSettings();
        int spread = readyUserSettings.size();
        if (spread > timeBetween / 4) {
            int newInterval = spread / 2;
            OPERATION_CONFIG.setInterval(newInterval);
            server.sendMessage(Text.literal(
                    String.format("[%s]Too many players tracked, increased interval to %d",
                            TrakteerFunctions.MOD_METADATA.getName(),
                            newInterval)
            ).formatted(Formatting.RED));
            return null;
        }

        int part = server.getTicks() % timeBetween;
        UserSettings userSettings = null;
        for (int i = 0; i < spread; i++) {
            if (part == 0) {
                userSettings = readyUserSettings.get(0);
                break;
            }

            if (part == timeBetween * i / spread) {
                userSettings = readyUserSettings.get(i);
                break;
            }
        }
        return userSettings;
    }

    @Shadow
    public abstract PlayerManager getPlayerManager();

    @SuppressWarnings("UnreachableCode")
    @Inject(method = "tick", at = @At("TAIL"))
    void onTick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        if (!TrakteerFunctions.modEnabled) return;
        MinecraftServer server = (MinecraftServer) (Object) this;

        UserSettings userSettings = getUserToCheck(server);
        if (userSettings == null) return;

        assert server.getUserCache() != null;
        Optional<GameProfile> optionalGameProfile = server.getUserCache().getByUuid(userSettings.uuid);

        if (optionalGameProfile.isPresent()) {
            GameProfile gameProfile = optionalGameProfile.get();
            threadExecutor.execute(() -> {
                Donation[] donations = null;
                try {
                    donations = Web.getLatestDonations(userSettings, gameProfile);
                } catch (IOException ioe) {
                    if (ioe instanceof ConnectException) {
                        TrakteerFunctions.LOGGER.warn("Unable to connect to API: {}", ioe.getMessage());
                        return;
                    }

                    ServerPlayerEntity player = getPlayerManager().getPlayer(gameProfile.getId());
                    if (player != null) {
                        player.sendMessage(Text.literal("[Trakteer Functions] API key no longer valid, removing...").formatted(Formatting.RED));
                    }

                    try {
                        OPERATION_CONFIG.setApiKey(gameProfile.getId(), "");
                    } catch (Exception ignored) {
                    }
                }
                if (donations == null) return;

                Arrays.stream(donations)
                        .filter(donation -> TrakteerFunctions.knownTimestamps.get(userSettings.uuid).add(donation.updated_at))
                        .forEach(donation -> Game.handleDonation(server, donation));
            });
        }
    }
}
