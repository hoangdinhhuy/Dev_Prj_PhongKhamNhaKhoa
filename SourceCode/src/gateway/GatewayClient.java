package gateway;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import entity.PhieuKham;
import java.net.URLEncoder;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/** JavaFX-side client; API credentials are environment variables, never stored in source/config files. */
public final class GatewayClient {
    private static final String BASE = System.getenv().getOrDefault("GATEWAY_URL", "http://localhost:8088").replaceAll("/$", "");
    private static final HttpClient HTTP = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build();

    private GatewayClient() { }

    public static CompletableFuture<Void> publishCall(PhieuKham pk, String type) {
        String token = System.getenv().getOrDefault("GATEWAY_CALL_TOKEN", "");
        String id = UUID.randomUUID().toString();
        String body = form(Map.of("eventId", id, "stt", String.valueOf(pk.getSoThuTu()),
                "room", pk.getMaPhong(), "type", type, "token", token));
        HttpRequest request = HttpRequest.newBuilder(URI.create(BASE + "/api/call"))
                .timeout(Duration.ofSeconds(4)).header("Content-Type", "application/x-www-form-urlencoded; charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString(body)).build();
        return HTTP.sendAsync(request, HttpResponse.BodyHandlers.discarding()).thenApply(response -> {
            if (response.statusCode() != 202) throw new IllegalStateException("Gateway HTTP " + response.statusCode());
            return null;
        });
    }

    public static CompletableFuture<JsonObject> analyzeInventory(Map<String, Object> metrics) {
        String token = System.getenv().getOrDefault("GATEWAY_INVENTORY_TOKEN", "");
        String body = form(Map.of("token", token, "metrics", new com.google.gson.Gson().toJson(metrics)));
        HttpRequest request = HttpRequest.newBuilder(URI.create(BASE + "/api/inventory-analysis"))
                .timeout(Duration.ofSeconds(20)).header("Content-Type", "application/x-www-form-urlencoded; charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString(body)).build();
        return HTTP.sendAsync(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)).thenApply(response -> {
            JsonObject result = JsonParser.parseString(response.body()).getAsJsonObject();
            if (response.statusCode() != 200) throw new IllegalStateException(result.has("message")
                    ? result.get("message").getAsString() : "Không gọi được gateway (HTTP " + response.statusCode() + ").");
            return result;
        });
    }

    private static String form(Map<String, String> fields) {
        return fields.entrySet().stream().map(e -> enc(e.getKey()) + "=" + enc(e.getValue()))
                .collect(java.util.stream.Collectors.joining("&"));
    }

    private static String enc(String value) { return URLEncoder.encode(value, StandardCharsets.UTF_8); }
}
