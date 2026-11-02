package dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import entity.ChiTietHoaDon;

public class ChiTietHoaDon_DAO {

    public static void themChiTiet(Connection con, ChiTietHoaDon ct) throws SQLException {
        String sql = "INSERT INTO ChiTietHoaDon (maHoaDon, maDichVu, soLuong, donGia, thanhTien)"
                + " VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setString(1, ct.getMaHoaDon());
            pstm.setString(2, ct.getMaDichVu());
            pstm.setInt(3, ct.getSoLuong());
            pstm.setBigDecimal(4, ct.getDonGia());
            pstm.setBigDecimal(5, ct.getThanhTien());
            pstm.executeUpdate();
        }
    }

    public static ArrayList<ChiTietHoaDon> layChiTietTheoHoaDon(String maHoaDon) {
        String sql = "SELECT ct.maHoaDon, ct.maDichVu, ct.soLuong, ct.donGia, ct.thanhTien, dv.tenDichVu"
                + " FROM ChiTietHoaDon ct JOIN DichVu dv ON dv.maDichVu = ct.maDichVu WHERE ct.maHoaDon = ?";
        ArrayList<ChiTietHoaDon> ds = new ArrayList<>();
        Connection con = connectDB.ConnectDB.getInstance().getConnection();
        try (PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setString(1, maHoaDon);
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    ChiTietHoaDon ct = new ChiTietHoaDon(rs.getString(1), rs.getString(2), rs.getInt(3),
                            rs.getBigDecimal(4));
                    ct.setThanhTien(rs.getBigDecimal(5));
                    ct.setTenDichVu(rs.getString(6));
                    ds.add(ct);
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi truy vấn ChiTietHoaDon", ex);
        }
        return ds;
    }

    private ChiTietHoaDon_DAO() { }
}
