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
        String ins = "INSERT INTO BoDem (loaiMa, ngay, giaTri) VALUES (?, ?, 1)";
        try (PreparedStatement p = con.prepareStatement(ins)) {
            p.setString(1, loaiMa);
            p.setDate(2, Date.valueOf(ngay));
            p.executeUpdate();
        }
        return 1;
    }

    /** Ma dinh danh cho loaiMa: NV/BN/HS/PK/HD -> <loaiMa>yyMMddNNNN; DV/TH -> <loaiMa>NNNN. */
    public static String layMaMoi(Connection con, String loaiMa, LocalDate ngay) throws SQLException {
        int n = tangGiaTri(con, loaiMa, ngay);
        if (n > 9999) {
            throw new SQLException("Hết dải mã cho loại " + loaiMa + " ngày " + ngay);
        }
        boolean coNgay = !"DV".equals(loaiMa) && !"TH".equals(loaiMa);
        if (coNgay) {
            return String.format("%s%02d%02d%04d", loaiMa, ngay.getYear() % 100, ngay.getMonthValue(), n);
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
