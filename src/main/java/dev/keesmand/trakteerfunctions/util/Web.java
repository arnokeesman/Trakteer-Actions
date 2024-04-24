package dev.keesmand.trakteerfunctions.util;

import com.google.gson.*;
import com.mojang.authlib.GameProfile;
import dev.keesmand.trakteerfunctions.TrakteerFunctions;
import dev.keesmand.trakteerfunctions.config.UserSettings;
import dev.keesmand.trakteerfunctions.model.Donation;
import dev.keesmand.trakteerfunctions.model.OperationMode;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Web {
    private static final String trakteerURL = "https://api.trakteer.id/v1/public/supports?limit=10";
    private static final String testURL = "http://localhost:6883/supports";

    public static Donation[] getLatestDonations(UserSettings userSettings, GameProfile gameProfile) throws IOException {
        String apiKey = userSettings.getApiKey();
        String url = TrakteerFunctions.OPERATION_CONFIG.getMode() == OperationMode.real
                ? trakteerURL
                : testURL;
        JsonElement apiResult = executeGet(url, apiKey);
        if (apiResult == null) return null;
        JsonObject json = apiResult.getAsJsonObject();
        if (!json.has("status")
                || !Objects.equals(json.get("status").getAsString(), "success")) return null;

        Donation[] donations = parseDonations(json);
        if (gameProfile != null) {
            for (Donation donation : donations) {
                donation.receiver = gameProfile.getName();
            }
        }
        return donations;
    }

    public static boolean verifyApiKey(UserSettings settings) {
        try {
            Donation[] donations = getLatestDonations(settings, null);
            if (donations == null) return false;
            setKnownTimestamps(settings, donations);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static void setKnownTimestamps(UserSettings settings, Donation[] donations) {
        Set<String> knownTimestamps = new HashSet<>(Arrays.stream(donations).map(d -> d.updated_at).toList());
        TrakteerFunctions.knownTimestamps.put(settings.uuid, knownTimestamps);
    }

    private static JsonElement executeGet(final String request_url, final String apiKey) throws IOException {
        Object ret;

        URL url = new URL(request_url);
        URLConnection con = url.openConnection();
        con.setRequestProperty("key", apiKey);
        con.setRequestProperty("User-Agent", String.format("%s/%s",
                TrakteerFunctions.MOD_METADATA.getName(),
                TrakteerFunctions.MOD_METADATA.getVersion().getFriendlyString()));
        ret = con.getContent();

        if (ret == "") return null;
        return JsonParser.parseReader(new InputStreamReader((InputStream) ret));
    }

    private static Donation[] parseDonations(JsonObject apiData) {
        JsonArray list = apiData.get("result").getAsJsonObject().get("data").getAsJsonArray();
        return new Gson().fromJson(list, Donation[].class);
    }
}
