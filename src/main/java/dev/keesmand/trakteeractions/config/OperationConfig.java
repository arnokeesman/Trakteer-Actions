package dev.keesmand.trakteeractions.config;

import dev.keesmand.trakteeractions.TrakteerActionsMod;
import dev.keesmand.trakteeractions.model.OperationMode;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.PersistentState;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OperationConfig extends PersistentState {
    private final List<UserSettings> userSettingsList;
    private int interval;
    private OperationMode mode;

    public OperationConfig(int interval, OperationMode mode, List<UserSettings> userSettingsList) {
        this.interval = interval;
        this.mode = mode;
        this.userSettingsList = userSettingsList;
    }

    public static OperationConfig fromNbt(NbtCompound nbt) {
        int interval = nbt.getInt("interval");
        String mode = nbt.getString("mode");
        List<UserSettings> userSettingsList = new ArrayList<>();
        NbtList nbtUserSettingsList = nbt.getList("userSettings", NbtCompound.COMPOUND_TYPE);
        for (NbtElement userSettingsElement : nbtUserSettingsList) {
            UserSettings userSettings = UserSettings.fromNbt((NbtCompound) userSettingsElement);
            userSettingsList.add(userSettings);
        }
        return new OperationConfig(interval, OperationMode.valueOf(mode), userSettingsList);
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
        markDirty();
    }

    public OperationMode getMode() {
        return mode;
    }

    public void setMode(OperationMode mode) {
        this.mode = mode;
        markDirty();
        TrakteerActionsMod.initializeDonations();
    }

    public void setApiKey(ServerPlayerEntity player, String apiKey) throws Exception {
        setApiKey(player.getUuid(), apiKey);
    }

    public void setApiKey(UUID uuid, String apiKey) throws Exception {
        if (apiKey != null && !apiKey.isEmpty() && apiKeyInUse(apiKey))
            throw new Exception("apiKey can only be linked to 1 account");

        UserSettings userSettings = getUserSettings(uuid);

        if (userSettings == null) {
            userSettings = new UserSettings(uuid, apiKey);
            userSettingsList.add(userSettings);
        } else {
            userSettings.setApiKey(apiKey);
        }

        markDirty();
    }

    public void setEnabled(ServerPlayerEntity player, boolean enabled) {
        setEnabled(player.getUuid(), enabled);
    }

    public void setEnabled(UUID uuid, boolean enabled) {
        UserSettings userSettings = getUserSettings(uuid);

        if (userSettings == null) {
            userSettings = new UserSettings(uuid, "");
            userSettingsList.add(userSettings);
        }

        userSettings.setEnabled(enabled);

        markDirty();
    }

    public boolean verify(ServerPlayerEntity player) {
        return verify(player.getUuid());
    }

    public boolean verify(UUID uuid) {
        UserSettings userSettings = getUserSettings(uuid);
        if (userSettings == null) return false;
        return userSettings.verify();
    }

    public boolean isVerified(ServerPlayerEntity player) {
        return isVerified(player.getUuid());
    }

    public boolean isVerified(UUID uuid) {
        UserSettings userSettings = getUserSettings(uuid);
        return userSettings != null && userSettings.isVerified();
    }

    public boolean isEnabled(ServerPlayerEntity player) {
        return isEnabled(player.getUuid());
    }

    public boolean isEnabled(UUID uuid) {
        UserSettings userSettings = getUserSettings(uuid);
        return userSettings != null && userSettings.isEnabled();
    }

    public String getApiKey(ServerPlayerEntity player) {
        return getApiKey(player.getUuid());
    }

    public String getApiKey(UUID uuid) {
        UserSettings userSettings = getUserSettings(uuid);
        if (userSettings == null) return null;
        return userSettings.getApiKey();
    }

    private boolean apiKeyInUse(String apiKey) {
        return userSettingsList.stream().anyMatch(us -> us.getApiKey().equals(apiKey));
    }

    public List<UserSettings> getUserSettingsList() {
        return userSettingsList;
    }

    public List<UserSettings> getReadyUserSettings() {
        return userSettingsList.stream().filter(UserSettings::ready).toList();
    }

    public UserSettings getUserSettings(ServerPlayerEntity player) {
        return getUserSettings(player.getUuid());
    }

    public UserSettings getUserSettings(UUID uuid) {
        return userSettingsList.stream().filter(s -> s.uuid.equals(uuid)).findFirst().orElse(null);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        nbt.putInt("interval", interval);
        nbt.putString("mode", mode.name());

        NbtList userSettingList = new NbtList();
        for (UserSettings userSettings : userSettingsList) {
            NbtCompound userSettingCompound = new NbtCompound();
            userSettings.writeNbt(userSettingCompound, registryLookup);
            userSettingList.add(userSettingCompound);
        }
        nbt.put("userSettings", userSettingList);
        return nbt;
    }
}
