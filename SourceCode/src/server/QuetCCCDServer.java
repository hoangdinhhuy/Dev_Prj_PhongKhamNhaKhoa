package server;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import javafx.scene.image.Image;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Server HTTP nho nhung trong app (com.sun.net.httpserver — co san trong JDK).
 * Dien thoai (cung Wi-Fi/hotspot voi laptop) mo trang web quay CCCD bang Live Text
 * roi gui thong tin (hoTen/cccd/ngaySinh/diaChi) ve app dien vao form tu dong.
 * Chu file: Duy. Chi doc thong tin goi vao — khong gui nguoc du lieu nao ra mang.
 */
public class QuetCCCDServer {

    private static HttpServer server;
    private static int portDangChay;
    /** GUI dang ky nhan thong tin (GD_QLBenhNhan_Controller). */
    public static volatile Consumer<Map<String, String>> nguoiNhan;

    /** Dia chi IP mang noi bo cua may tinh (de dien thoai truy cap). */
    public static String ipLan() {
        try {
            for (Enumeration<NetworkInterface> list = NetworkInterface.getNetworkInterfaces();
                 list.hasMoreElements(); ) {
                java.util.Enumeration<java.net.InetAddress> addrs = list.nextElement().getInetAddresses();
                while (addrs.hasMoreElements()) {
                    java.net.InetAddress addr = addrs.nextElement();
                    if (addr.isSiteLocalAddress()) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (Exception ignored) { }
        return "localhost";
    }

    /** Bat server (thu 8080 truoc, bi chiem thi thu 8081...). Tra ve port da chay. */
    public static synchronized int start() throws IOException {
        if (server != null) {
            return portDangChay;
        }
        int port = 8080;
        while (true) {
            try {
                server = HttpServer.create(new InetSocketAddress(port), 0);
                break;
            } catch (IOException ex) {
                port++;
                if (port > 8090) {
                    throw new IOException("Không còn port trống 8080–8090.");
                }
            }
        }
        server.createContext("/", ex -> phucVu(ex));
        server.setExecutor(null);
        server.start();
        portDangChay = port;
        return port;
    }

    public static synchronized void stop() {
        if (server != null) {
            server.stop(0);
            server = null;
        }
    }

    public static boolean dangChay() {
        return server != null;
    }

    /** Tao anh QR tu mot chuoi (dung ZXing da co trong pom). */
    public static Image taoQR(String noiDung, int size) throws Exception {
        BitMatrix m = new QRCodeWriter().encode(noiDung, BarcodeFormat.QR_CODE, size, size,
                Map.of(EncodeHintType.MARGIN, 1));
        javafx.scene.image.WritableImage img = new javafx.scene.image.WritableImage(size, size);
        javafx.scene.image.PixelWriter pw = img.getPixelWriter();
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                pw.setArgb(x, y, m.get(x, y) ? 0xFF173A5E : 0xFFFFFFFF);
            }
        }
        return img;
    }

    // ---------- noi bo ----------

    private static void phucVu(HttpExchange ex) throws IOException {
        String path = ex.getRequestURI().getPath();
        if ("POST".equals(ex.getRequestMethod()) && path.startsWith("/api/cccd")) {
            byte[] body = ex.getRequestBody().readAllBytes();
            Map<String, String> data = parse(new String(body, java.nio.charset.StandardCharsets.UTF_8));
            Consumer<Map<String, String>> n = nguoiNhan;
            if (n != null) {
                n.accept(data);
            }
            gui(ex, "text/plain; charset=utf-8", "OK");
            return;
        }
        // Mac dinh: phuc vu trang web quay CCCD
        byte[] trang = docTaiNguyen("/serverweb/quetcccd.html");
        gui(ex, "text/html; charset=utf-8", trang == null ? "Thiếu serverweb/quetcccd.html" : new String(trang, java.nio.charset.StandardCharsets.UTF_8));
    }

    private static void gui(HttpExchange ex, String type, String noiDung) throws IOException {
        byte[] bytes = noiDung.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", type);
        ex.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static byte[] docTaiNguyen(String duongDan) {
        try (InputStream is = QuetCCCDServer.class.getResourceAsStream(duongDan)) {
            return is == null ? null : is.readAllBytes();
        } catch (IOException ex) {
            return null;
        }
    }

    /** Parse body dang a=1&b=2 (form urlencoded). */
    private static Map<String, String> parse(String body) {
        Map<String, String> kq = new LinkedHashMap<>();
        for (String cap : body.split("&")) {
            int i = cap.indexOf('=');
            if (i > 0) {
                kq.put(URLDecoder.decode(cap.substring(0, i), java.nio.charset.StandardCharsets.UTF_8),
                        URLDecoder.decode(cap.substring(i + 1), java.nio.charset.StandardCharsets.UTF_8));
            }
        }
        return kq;
    }
}
