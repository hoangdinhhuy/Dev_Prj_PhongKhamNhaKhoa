package dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import entity.ChiDinhDichVu;

public class ChiDinhDichVu_DAO {

    public static void xoaTheoPhieu(Connection con, String maPhieuKham) throws SQLException {
        try (PreparedStatement pstm = con.prepareStatement("DELETE FROM ChiDinhDichVu WHERE maPhieuKham = ?")) {
            pstm.setString(1, maPhieuKham);
            pstm.executeUpdate();
        }
    }

    public static void themChiDinh(Connection con, ChiDinhDichVu cd) throws SQLException {
        try (PreparedStatement pstm = con.prepareStatement(
                "INSERT INTO ChiDinhDichVu (maPhieuKham, maDichVu, soRang) VALUES (?, ?, ?)")) {
            pstm.setString(1, cd.getMaPhieuKham());
            pstm.setString(2, cd.getMaDichVu());
            pstm.setString(3, cd.getSoRang());
            pstm.executeUpdate();
        }
    }

    public static ArrayList<ChiDinhDichVu> layTheoPhieu(String maPhieuKham) {
        String sql = "SELECT cd.maPhieuKham, cd.maDichVu, dv.tenDichVu, cd.soRang,"
                + " CASE WHEN cd.maDichVu = 'DV0001' AND pk.maHoaDonPhiKham IS NOT NULL"
                + " THEN CAST(0 AS decimal(18,2)) ELSE dv.donGiaNiemYet END AS donGia"
                + " FROM ChiDinhDichVu cd JOIN DichVu dv ON dv.maDichVu = cd.maDichVu"
                + " JOIN PhieuKham pk ON pk.maPhieuKham = cd.maPhieuKham WHERE cd.maPhieuKham = ?";
        ArrayList<ChiDinhDichVu> ds = new ArrayList<>();
        Connection con = connectDB.ConnectDB.getInstance().getConnection();
        try (PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setString(1, maPhieuKham);
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    ChiDinhDichVu cd = new ChiDinhDichVu(rs.getString(1), rs.getString(2));
                    entity.DichVu dv = new entity.DichVu(rs.getString(2));
                    dv.setTenDichVu(rs.getString(3));
                    cd.setDichVu(dv);
                    cd.setSoRang(rs.getString(4));
                    cd.setDonGia(rs.getBigDecimal(5));
                    ds.add(cd);
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi truy vấn ChiDinhDichVu", ex);
        }
        return ds;
    }

    private ChiDinhDichVu_DAO() { }
}
