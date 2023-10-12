package dev.keesmand.trakteerfunctions.mixin;

import dev.keesmand.trakteerfunctions.TrakteerFunctions;
import dev.keesmand.trakteerfunctions.config.TrakteerFunctionsConfig;
import dev.keesmand.trakteerfunctions.model.Donation;
import dev.keesmand.trakteerfunctions.util.Game;
import dev.keesmand.trakteerfunctions.util.Web;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Shadow public abstract PlayerManager getPlayerManager();

    @Unique
    private final Thread.UncaughtExceptionHandler exceptionHandler = (thread, throwable) ->
            TrakteerFunctions.LOGGER.error("Exception while reading from API", throwable);
    @Unique
    private final Executor threadExecutor = Executors.newCachedThreadPool(runnable -> {
        var thread = new Thread(runnable, "Trakteer API Thread");

        thread.setUncaughtExceptionHandler(exceptionHandler);

        return thread;
    });

    @Inject(method = "tick", at = @At("TAIL"))
    void onTick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        TrakteerFunctionsConfig config = TrakteerFunctions.CONFIG;
        if (!config.isValid()) return;

        MinecraftServer server = (MinecraftServer)(Object)this;

        if (server.getTicks() % (config.getInterval() * 20) != 0) return;

        threadExecutor.execute(() -> {
            Donation[] donations = null;
            try {
                donations = Web.getLatestDonations(config.getApiKey());
            } catch (IOException ioe) {
                if (ioe instanceof ConnectException) {
                    TrakteerFunctions.LOGGER.warn("Unable to connect to API: " + ioe.getMessage());
                    return;
                }

                String message = "Invalid API configuration, removing API key.";
                TrakteerFunctions.LOGGER.error(message);
                for (ServerPlayerEntity player : getPlayerManager().getPlayerList()) {
                    if (player.hasPermissionLevel(4)) player.sendMessage(Text.literal(message).formatted(Formatting.RED));
                }
                try { TrakteerFunctions.CONFIG.setApiKey(""); } catch (IOException ignored) {}
            }
            if (donations == null) return;

            Arrays.stream(donations)
                    .filter(donation -> TrakteerFunctions.knownTimestamps.add(donation.updated_at))
                    .forEach(donation -> Game.handleDonation(server, donation));
        });
    }
}
