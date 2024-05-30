package dev.keesmand.trakteeractions.config;

import dev.keesmand.trakteeractions.TrakteerActionsMod;
import dev.keesmand.trakteeractions.model.Donation;
import dev.keesmand.trakteeractions.model.OperationMode;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigManager {
    public static ActionConfig readActionConfig() throws Exception {
        Path configFile = FabricLoader.getInstance().getConfigDir().resolve("trakteer-actions.txt");

        if (!Files.exists(configFile)) {
            ActionConfig config = getDefaultConfig();
            writeActionConfig(config);
            return config;
        }

        List<String> lines = new ArrayList<>(Files.readAllLines(configFile));
        lines.add(Syntax.TITLE + "end");

        Action defaultAction = null;
        List<Action> actions = new ArrayList<>();

        Action currentAction = null;

        int lineNr = 0;
        for (String line : lines) {
            lineNr++;
            if (line.isBlank()) continue;

            if (line.startsWith(Syntax.TITLE)) {
                if (currentAction != null) {
                    if (currentAction.name.equals("default")) {
                        if (!currentAction.rules.isEmpty()) throw new Exception("default action cannot have rules");
                        if (!currentAction.includes.isEmpty())
                            throw new Exception("default action cannot have includes");
                        if (defaultAction == null) defaultAction = currentAction;
                        else throw new Exception("cannot have multiple default actions");
                    } else {
                        if (!currentAction.rules.isEmpty()) actions.add(currentAction);
                        else
                            throw new Exception(String.format("custom actions must have at least 1 rule (%s)", currentAction.name));
                    }
                }
                String actionName = line.replaceFirst(Syntax.TITLE, "");
                currentAction = new Action(actionName);
            } else if (line.startsWith(Syntax.OFFLINE)) {
                assert currentAction != null;
                currentAction.offline = true;
            } else if (line.startsWith(Syntax.RULE)) {
                List<String> split = Arrays.stream(line.split(" +")).toList();
                if (split.size() < 4)
                    throw new Exception(String.format("not enough arguments for action rule on line %d", lineNr));

                String key = split.get(1);
                if (!ActionRule.keys.contains(key)) throw new Exception(String.format("key %s does not exist", key));

                String operation = split.get(2);
                if (!ActionRule.operations.contains(operation))
                    throw new Exception(String.format("operation %s does not exist", operation));

                String value = String.join(" ", split.subList(3, split.size()));

                assert currentAction != null;
                currentAction.rules.add(new ActionRule(key, operation, value));
            } else if (line.startsWith(Syntax.INCLUDE)) {
                List<String> split = Arrays.stream(line.split(" +")).toList();
                if (split.size() < 2)
                    throw new Exception(String.format("not enough arguments for action include on line %d", lineNr));

                assert currentAction != null;
                currentAction.includes.add(line.replaceFirst(Syntax.INCLUDE, ""));
            } else {
                assert currentAction != null;
                currentAction.commands.add(line);
            }
        }

        if (defaultAction == null) throw new Exception("default action required");

        for (Action action : actions) {
            for (String include : action.includes) {
                if (include.equals("default")) continue;
                if (actions.stream().noneMatch(a -> a.name.equals(include)))
                    throw new Exception(String.format("include '%s' from action '%s' does not exist", include, action.name));
            }
        }

        ActionConfig config = new ActionConfig(defaultAction, actions);

        // test the config for invalid checks (invalid numbers mostly)
        Donation donation = getTestDonation();
        config.getActionForDonation(donation, false);

        return config;
    }

    public static void writeActionConfig(ActionConfig config) throws IOException {
        Path configFile = FabricLoader.getInstance().getConfigDir().resolve("trakteer-actions.txt");

        List<String> configLines = new ArrayList<>(
                actionToTextLines(config.defaultAction)
        );
        config.actions.forEach(action -> configLines.addAll(actionToTextLines(action)));

        String configString = String.join(System.lineSeparator(), configLines);
        Files.writeString(configFile, configString, StandardCharsets.UTF_8);
    }

    private static List<String> actionToTextLines(Action action) {
        List<String> lines = new ArrayList<>();
        lines.add(Syntax.TITLE + action.name);
        if (action.offline) lines.add(Syntax.OFFLINE);
        action.rules.forEach(rule -> lines.add(Syntax.RULE + rule.toString()));
        action.includes.forEach(include -> lines.add(Syntax.INCLUDE + include));
        lines.addAll(action.commands);
        lines.add("");
        return lines;
    }

    private static ActionConfig getDefaultConfig() {
        Action defaultAction = new Action(
                "default",
                true,
                new ArrayList<>(),
                new ArrayList<>(),
                List.of(
                        "function {supporter_name}.mcfunction",
                        "say {supporter_name} donated {amount}", "say something else"
                )
        );

        List<Action> actions = List.of(
                new Action(
                        "kaboom",
                        List.of(
                                new ActionRule("support_message", "contains", "boom"),
                                new ActionRule("amount", ">=", "10000")
                        ),
                        List.of("default"),
                        List.of("execute at {receiver} run summon creeper")
                ),
                new Action(
                        "splash",
                        List.of(
                                new ActionRule("support_message", "contains", "splash")
                        ),
                        new ArrayList<>(),
                        List.of("execute at {receiver} run setblock ~ ~ ~ water")
                )
        );

        return new ActionConfig(defaultAction, actions);
    }

    private static Donation getTestDonation() {
        return new Donation(
                "Arno",
                "Kaboom",
                5,
                10_000,
                "creeper",
                "sometime"
        );
    }

    public static OperationConfig readOperationConfig(MinecraftServer server) {
        File saveFile = getSettingsFile(server);
        if (Files.exists(saveFile.toPath()) && saveFile.length() != 0) {
            try {
                NbtCompound nbtCompound = NbtIo.readCompressed(new FileInputStream(saveFile), NbtSizeTracker.ofUnlimitedBytes());
                return OperationConfig.fromNbt(nbtCompound.getCompound("data"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // no config exists yet
        OperationConfig config = new OperationConfig(10, OperationMode.real, new ArrayList<>());
        config.markDirty();
        return config;
    }

    public static File getSettingsFile(MinecraftServer server) {
        Path dataDirectoryPath = server.getSavePath(WorldSavePath.ROOT);
        File settingsFile = null;
        try {
            settingsFile = dataDirectoryPath.resolve("trakteer-actions.nbt").toFile();
            //noinspection ResultOfMethodCallIgnored
            settingsFile.createNewFile();
        } catch (IOException e) {
            TrakteerActionsMod.error(String.format("Failed to create config file: %s", e));
        }

        return settingsFile;
    }

    private static class Syntax {
        public static final String TITLE = "### ";
        public static final String RULE = ":if ";
        public static final String INCLUDE = ":include ";
        public static final String OFFLINE = ":offline";
    }
}
