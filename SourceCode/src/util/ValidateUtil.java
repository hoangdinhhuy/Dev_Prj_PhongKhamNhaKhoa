package util;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Kiem tra du lieu dau vao (RULES muc 4.3). Loi thi nem IllegalArgumentException voi message chuan VLxx. */
public final class ValidateUtil {
    private ValidateUtil() { }

    private static String trim(String s) { return s == null ? "" : s.trim(); }

    public static String khongRong(String giaTri, String message) {
        if (trim(giaTri).isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return trim(giaTri);
    }

    public static String sdt(String sdt) {
        String v = khongRong(sdt, MaThongBao.VL02);
        if (!v.matches("^0\\d{9}$")) {
            throw new IllegalArgumentException(MaThongBao.VL02);
        }
        return v;
    }

    public static String cccd(String cccd) {
        String v = trim(cccd);
        if (v.isEmpty()) return null;               // khong bat buoc nhap
        if (!v.matches("^\\d{12}$")) {
            throw new IllegalArgumentException(MaThongBao.VL03);
        }
        return v;
    }

    public static LocalDate ngaySinh(LocalDate d) {
        if (d == null) {
            throw new IllegalArgumentException(MaThongBao.VL04);
        }
        if (d.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(MaThongBao.VL04);
        }
        return d;
    }

    public static LocalDate nhanVienDu18Tuoi(LocalDate ngaySinh) {
        ngaySinh(ngaySinh);
        if (ngaySinh.isAfter(LocalDate.now().minusYears(18))) {
            throw new IllegalArgumentException(MaThongBao.VL14);
        }
        return ngaySinh;
    }

    public static BigDecimal donGia(String so) {
        BigDecimal v;
        try {
            v = new BigDecimal(trim(so));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(MaThongBao.VL06);
        }
        if (v.signum() <= 0) {
            throw new IllegalArgumentException(MaThongBao.VL06);
        }
        return v;
    }

    public static int soLuong(String so) {
        int v;
        try {
            v = Integer.parseInt(trim(so));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(MaThongBao.VL12);
        }
        if (v < 1) {
            throw new IllegalArgumentException(MaThongBao.VL12);
        }
        return v;
    }

    public static void khoangThoiGian(LocalDate tuNgay, LocalDate denNgay) {
        if (tuNgay == null || denNgay == null || tuNgay.isAfter(denNgay)) {
            throw new IllegalArgumentException(MaThongBao.VL10);
        }
    }
}
