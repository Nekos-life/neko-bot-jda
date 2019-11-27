package life.nekos.bot.commons.apis;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.Objects;

import static life.nekos.bot.commons.Misc.UA;

public class AlexApis {
    private static final OkHttpClient client = new OkHttpClient();

    public static InputStream getGoolge(String top, String bottom) throws Exception {
        URL url =
                new URL(
                        MessageFormat.format(
                                "https://api.alexflipnote.xyz/didyoumean?top={0}&bottom={1}",
                                top.replace(" ", "%20"), bottom.replace(" ", "%20")));
        URLConnection connection = url.openConnection();
        connection.setRequestProperty(UA[0], UA[1]);
        connection.connect();
        return connection.getInputStream();
    }

    public static JSONObject getColor(String hex) throws Exception {
        Request request =
                new Request.Builder().url("https://api.alexflipnote.xyz/colour/" + hex).build();
        Response response = client.newCall(request).execute();
        try (ResponseBody responseBody = response.body()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return new JSONObject(Objects.requireNonNull(responseBody).string());
        }
    }

    public static String getCoffee() throws Exception {
        Request request =
                new Request.Builder().url("").build();
        Response response = client.newCall(request).execute();
        try (ResponseBody responseBody = response.body()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return new JSONObject(Objects.requireNonNull(responseBody).string()).get("file").toString();
        }
    }
}
