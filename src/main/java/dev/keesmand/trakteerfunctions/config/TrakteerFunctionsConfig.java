package dev.keesmand.trakteerfunctions.config;

import dev.keesmand.trakteerfunctions.TrakteerFunctions;
import dev.keesmand.trakteerfunctions.model.OperationMode;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtString;
import net.minecraft.world.PersistentState;

import java.io.IOException;

public class TrakteerFunctionsConfig extends PersistentState {
    private String apiKey;
    private int interval;
    private OperationMode mode;

    public TrakteerFunctionsConfig(String apiKey, int interval, OperationMode mode) {
        this.apiKey = apiKey;
        this.interval = interval;
        this.mode = mode;
    }

    public boolean isValid() {
        return !apiKey.isEmpty() && interval > 0;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) throws IOException {
        this.apiKey = apiKey;
        markDirty();

        if (isValid()) {
            TrakteerFunctions.initializeDonations();
        } else {
            TrakteerFunctions.clearKnowTimestamps();
        }
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) throws IOException {
        boolean wasValid = isValid();
        this.interval = interval;
        markDirty();

        if (isValid()) {
            if (!wasValid) TrakteerFunctions.initializeDonations();
        } else {
            TrakteerFunctions.clearKnowTimestamps();
        }
    }

    public OperationMode getMode() {
        return mode;
    }

    public void setMode(OperationMode mode) throws IOException {
        this.mode = mode;
        markDirty();

        if (isValid()) TrakteerFunctions.initializeDonations();
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.put("apiKey", NbtString.of(apiKey));
        nbt.put("interval", NbtInt.of(interval));
        nbt.put("mode", NbtString.of(mode.name()));
        return nbt;
    }

    public static TrakteerFunctionsConfig fromNbt(NbtCompound nbt) {
        String apiKey = nbt.getString("apiKey");
        int interval = nbt.getInt("interval");
        OperationMode mode = nbt.contains("mode")
                ? OperationMode.valueOf(nbt.getString("mode"))
                : OperationMode.test;

        return new TrakteerFunctionsConfig(apiKey, interval, mode);
    }
}
