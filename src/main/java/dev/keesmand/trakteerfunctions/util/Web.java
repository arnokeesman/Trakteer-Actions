package dev.keesmand.trakteerfunctions.util;

import com.google.gson.*;
import dev.keesmand.trakteerfunctions.TrakteerFunctions;
import dev.keesmand.trakteerfunctions.model.Donation;
import dev.keesmand.trakteerfunctions.model.OperationMode;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;

public class Web {

    public static Donation[] getLatestDonations(String apiKey) throws IOException {
        final String trakteerURL = "https://api.trakteer.id/v1/public/supports?limit=10";
        final String testURL = "http://localhost:6883/supports";
        String url = TrakteerFunctions.CONFIG.getMode() == OperationMode.real
                ? trakteerURL
                : testURL;
        JsonElement apiResult = executeGet(url, apiKey);
        if (apiResult == null) return null;
        JsonObject json = apiResult.getAsJsonObject();
        if (!json.has("status")
            || !Objects.equals(json.get("status").getAsString(), "success")) return null;

        return parseDonations(json);
    }

    private static JsonElement executeGet(final String request_url, final String apiKey) throws IOException {
        Object ret;

        URL url = new URL(request_url);
        URLConnection con = url.openConnection();
        con.setRequestProperty("key", apiKey);
        ret = con.getContent();

        if (ret == "") return null;
        return JsonParser.parseReader(new InputStreamReader((InputStream) ret));
    }

    private static Donation[] parseDonations(JsonObject apiData) {
        JsonArray list = apiData.get("result").getAsJsonObject().get("data").getAsJsonArray();
        return new Gson().fromJson(list, Donation[].class);
    }
}
