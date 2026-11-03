package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

/** Cap ma tu dong nguyen tu (BoDem) - cam MAX+1/COUNT+1. Chu file: Thanh. */
public class BoDem_DAO {

    private static int tangGiaTri(Connection con, String loaiMa, LocalDate ngay) throws SQLException {
        if (con.getAutoCommit()) {
            throw new SQLException("Cấp mã phải chạy trong transaction.");
        }
        // Khóa cả khoảng khóa khi chưa có dòng, để hai máy không cùng INSERT bộ đếm đầu ngày.
        String lock = "SELECT giaTri FROM BoDem WITH (UPDLOCK, HOLDLOCK) WHERE loaiMa = ? AND ngay = ?";
        boolean daCo;
        try (PreparedStatement p = con.prepareStatement(lock)) {
            p.setString(1, loaiMa);
            p.setDate(2, Date.valueOf(ngay));
            try (ResultSet rs = p.executeQuery()) {
                daCo = rs.next();
            }
        }
        if (!daCo) {
            String ins = "INSERT INTO BoDem (loaiMa, ngay, giaTri) VALUES (?, ?, 1)";
            try (PreparedStatement p = con.prepareStatement(ins)) {
                p.setString(1, loaiMa);
                p.setDate(2, Date.valueOf(ngay));
                p.executeUpdate();
            }
            return 1;
        }
        String upd = "UPDATE BoDem SET giaTri = giaTri + 1 OUTPUT inserted.giaTri WHERE loaiMa = ? AND ngay = ?";
        try (PreparedStatement p = con.prepareStatement(upd)) {
            p.setString(1, loaiMa);
            p.setDate(2, Date.valueOf(ngay));
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Bộ đếm vừa thay đổi khi cấp mã " + loaiMa);
    }

    /** Ma dinh danh cho loaiMa: NV/BN/HS/PK/HD -> <loaiMa>yyMMddNNNN; DV/TH -> <loaiMa>NNNN. */
    public static String layMaMoi(Connection con, String loaiMa, LocalDate ngay) throws SQLException {
        boolean coNgay = !"DV".equals(loaiMa) && !"TH".equals(loaiMa);
        // DV/TH không mang ngày trong mã, nên dùng một bộ đếm chung qua mọi ngày.
        int n = tangGiaTri(con, loaiMa, coNgay ? ngay : LocalDate.of(1970, 1, 1));
        if (n > 9999) {
            throw new SQLException("Hết dải mã cho loại " + loaiMa + " ngày " + ngay);
        }
        if (coNgay) {
            return String.format("%s%02d%02d%02d%04d", loaiMa, ngay.getYear() % 100,
                    ngay.getMonthValue(), ngay.getDayOfMonth(), n);
        }
        return String.format("%s%04d", loaiMa, n);
    }

    /** So thu tu kham theo phong/ngay - dung loaiMa 'STT-<maPhong>' trong BoDem. */
    public static int laySoThuTuTiepTheo(Connection con, LocalDate ngay, String maPhong) throws SQLException {
        int n = tangGiaTri(con, "STT-" + maPhong, ngay);
        if (n > 9999) {
            throw new SQLException("Hết dải số thứ tự cho phòng " + maPhong + " ngày " + ngay);
        }
        return n;
    }

    private BoDem_DAO() { }
}
