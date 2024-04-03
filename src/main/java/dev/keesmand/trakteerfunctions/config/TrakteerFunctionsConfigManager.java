package dev.keesmand.trakteerfunctions.config;

import dev.keesmand.trakteerfunctions.model.OperationMode;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TrakteerFunctionsConfigManager {
    public static TrakteerFunctionsConfig read(MinecraftServer server) {
        File saveFile = getSettingsFile(server);
        if (Files.exists(saveFile.toPath()) && saveFile.length() != 0) {
            try {
                NbtCompound nbtCompound = NbtIo.readCompressed(new FileInputStream(saveFile), NbtSizeTracker.ofUnlimitedBytes());
                return TrakteerFunctionsConfig.fromNbt(nbtCompound.getCompound("data"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // no config exists yet
        TrakteerFunctionsConfig config = new TrakteerFunctionsConfig("", 0, OperationMode.test);
        config.markDirty();
        return config;
    }

    public static File getSettingsFile(MinecraftServer server) {
        Path dataDirectoryPath = server.getSavePath(WorldSavePath.ROOT);
        File settingsFile = null;
        try {
            settingsFile = dataDirectoryPath.resolve("trakteer-functions.nbt").toFile();
            settingsFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return settingsFile;
    }
}
