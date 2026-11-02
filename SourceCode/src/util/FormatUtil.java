package util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Dinh dang hien thi chuan (RULES muc 4.2) - cam tu String.format rieng le. */
public final class FormatUtil {
    private static final DateTimeFormatter F_NGAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter F_NGAYGIO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final NumberFormat F_TIEN = NumberFormat.getNumberInstance(new Locale("vi", "VN"));

    private FormatUtil() { }

    public static String ngay(LocalDate d) {
        return d == null ? "" : d.format(F_NGAY);
    }

    public static String ngayGio(LocalDateTime t) {
        return t == null ? "" : t.format(F_NGAYGIO);
    }

    public static String tien(BigDecimal so) {
        return so == null ? "" : F_TIEN.format(so) + " ₫";
    }
}
