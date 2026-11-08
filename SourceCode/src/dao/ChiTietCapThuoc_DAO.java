package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import entity.ChiTietCapThuoc;

public class ChiTietCapThuoc_DAO {

    public static void xoaTheoPhieu(Connection con, String maPhieuKham) throws SQLException {
        try (PreparedStatement pstm = con.prepareStatement("DELETE FROM ChiTietCapThuoc WHERE maPhieuKham = ?")) {
            pstm.setString(1, maPhieuKham);
            pstm.executeUpdate();
        }
    }

    public static void themChiTiet(Connection con, ChiTietCapThuoc ct) throws SQLException {
        try (PreparedStatement pstm = con.prepareStatement(
                "INSERT INTO ChiTietCapThuoc (maPhieuKham, maThuoc, soLuong, ghiChuCapThuoc) VALUES (?, ?, ?, ?)")) {
            pstm.setString(1, ct.getMaPhieuKham());
            pstm.setString(2, ct.getMaThuoc());
            pstm.setInt(3, ct.getSoLuong());
            pstm.setString(4, ct.getGhiChuCapThuoc());
            pstm.executeUpdate();
        }
    }

    public static ArrayList<ChiTietCapThuoc> layTheoPhieu(String maPhieuKham) {
        String sql = "SELECT ct.maPhieuKham, ct.maThuoc, ct.soLuong, ct.ghiChuCapThuoc, t.tenThuoc, t.donViTinh"
                + " FROM ChiTietCapThuoc ct JOIN Thuoc t ON t.maThuoc = ct.maThuoc WHERE ct.maPhieuKham = ?";
        ArrayList<ChiTietCapThuoc> ds = new ArrayList<>();
        Connection con = connectDB.ConnectDB.getInstance().getConnection();
        try (PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setString(1, maPhieuKham);
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    ChiTietCapThuoc ct = new ChiTietCapThuoc(rs.getString(1), rs.getString(2), rs.getInt(3),
                            rs.getString(4));
                    entity.Thuoc th = new entity.Thuoc(rs.getString(2), rs.getString(5), rs.getString(6));
                    ct.setThuoc(th);
                    ds.add(ct);
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi truy vấn ChiTietCapThuoc", ex);
        }
        return ds;
    }

    private ChiTietCapThuoc_DAO() { }
}
