package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import connectDB.ConnectDB;
import entity.HoSoBenhAn;

public class HoSoBenhAn_DAO {

    public static HoSoBenhAn layTheoBenhNhan(String maBenhNhan) {
        String sql = "SELECT maHoSo, tienSuBenhLy, ghiChuChuyenMon, maBenhNhan FROM HoSoBenhAn WHERE maBenhNhan = ?";
        Connection con = ConnectDB.getInstance().getConnection();
        try (PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setString(1, maBenhNhan);
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    return new HoSoBenhAn(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi truy vấn HoSoBenhAn", ex);
        }
        return null;
    }

    public static void themHoSo(Connection con, HoSoBenhAn hs) throws SQLException {
        String sql = "INSERT INTO HoSoBenhAn (maHoSo, tienSuBenhLy, ghiChuChuyenMon, maBenhNhan) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setString(1, hs.getMaHoSo());
            pstm.setString(2, hs.getTienSuBenhLy());
            pstm.setString(3, hs.getGhiChuChuyenMon());
            pstm.setString(4, hs.getMaBenhNhan());
            pstm.executeUpdate();
        }
    }

    private HoSoBenhAn_DAO() { }
}
