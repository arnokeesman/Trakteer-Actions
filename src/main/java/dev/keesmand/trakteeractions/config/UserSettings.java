package dev.keesmand.trakteeractions.config;

import dev.keesmand.trakteeractions.util.Web;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.PersistentState;

import java.util.UUID;

public class UserSettings extends PersistentState {
    public final UUID uuid;
    private String apiKey;
    private boolean enabled;
    private boolean verified;

    public UserSettings(UUID uuid, String apiKey) {
        this.uuid = uuid;
        this.apiKey = apiKey;
        this.verified = false;
    }

    public static UserSettings fromNbt(NbtCompound nbt) {
        UUID uuid = nbt.getUuid("uuid");
        String apiKey = nbt.getString("apiKey");
        boolean enabled = nbt.getBoolean("enabled");
        UserSettings setting = new UserSettings(uuid, apiKey);
        setting.setEnabled(enabled);
        return setting;
    }

    public String getApiKey() {
        return this.apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
        verified = false;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isVerified() {
        return this.verified;
    }

    public boolean verify() {
        if (this.apiKey == null || this.apiKey.isEmpty()) return false;
        this.verified = Web.verifyApiKey(this);
        return this.verified;
    }

    public boolean ready() {
        return enabled && verified;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        nbt.putUuid("uuid", this.uuid);
        nbt.putString("apiKey", this.apiKey);
        nbt.putBoolean("enabled", this.enabled);
        return nbt;
    }
}
