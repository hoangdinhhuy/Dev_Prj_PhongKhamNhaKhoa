package dao;

import connectDB.ConnectDB;
import entity.HangHoa;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public final class HangHoa_DAO {
    private HangHoa_DAO() { }

    public static ArrayList<HangHoa> getAllHangHoa() {
        ArrayList<HangHoa> ds = new ArrayList<>();
        String sql = "SELECT maHangHoa, tenHangHoa, donViTinh, soLuongTon, mucTonToiThieu FROM HangHoa ORDER BY maHangHoa";
        try (PreparedStatement p = ConnectDB.getInstance().getConnection().prepareStatement(sql);
             ResultSet rs = p.executeQuery()) {
            while (rs.next()) {
                ds.add(new HangHoa(rs.getString(1), rs.getString(2), rs.getString(3), rs.getInt(4), rs.getInt(5)));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi truy vấn hàng hóa", ex);
        }
        return ds;
    }

    public static boolean tenDaCo(String maBoQua, String ten) {
        String sql = "SELECT 1 FROM HangHoa WHERE tenHangHoa = ? AND maHangHoa <> ?";
        try (PreparedStatement p = ConnectDB.getInstance().getConnection().prepareStatement(sql)) {
            p.setString(1, ten);
            p.setString(2, maBoQua == null ? "" : maBoQua);
            try (ResultSet rs = p.executeQuery()) { return rs.next(); }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi kiểm tra tên hàng hóa", ex);
        }
    }

    public static void them(Connection con, HangHoa h) throws SQLException {
        String sql = "INSERT INTO HangHoa (maHangHoa, tenHangHoa, donViTinh, soLuongTon, mucTonToiThieu) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement p = con.prepareStatement(sql)) {
            p.setString(1, h.getMaHangHoa()); p.setString(2, h.getTenHangHoa());
            p.setString(3, h.getDonViTinh()); p.setInt(4, h.getSoLuongTon());
            p.setInt(5, h.getMucTonToiThieu()); p.executeUpdate();
        }
    }

    public static boolean capNhat(Connection con, HangHoa h) throws SQLException {
        String sql = "UPDATE HangHoa SET tenHangHoa = ?, donViTinh = ?, soLuongTon = ?, mucTonToiThieu = ? WHERE maHangHoa = ?";
        try (PreparedStatement p = con.prepareStatement(sql)) {
            p.setString(1, h.getTenHangHoa()); p.setString(2, h.getDonViTinh());
            p.setInt(3, h.getSoLuongTon()); p.setInt(4, h.getMucTonToiThieu());
            p.setString(5, h.getMaHangHoa()); return p.executeUpdate() == 1;
        }
    }
}
