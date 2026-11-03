package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import connectDB.ConnectDB;
import entity.DichVu;
import entity.TrangThaiDichVu;

public class DichVu_DAO {

    private static DichVu map(ResultSet rs) throws SQLException {
        return new DichVu(rs.getString("maDichVu"), rs.getString("tenDichVu"),
                rs.getBigDecimal("donGiaNiemYet"), TrangThaiDichVu.valueOf(rs.getString("trangThai")));
    }

    public static ArrayList<DichVu> getAllDichVu() {
        String sql = "SELECT maDichVu, tenDichVu, donGiaNiemYet, trangThai FROM DichVu ORDER BY maDichVu";
        ArrayList<DichVu> ds = new ArrayList<>();
        Connection con = ConnectDB.getInstance().getConnection();
        try (PreparedStatement pstm = con.prepareStatement(sql); ResultSet rs = pstm.executeQuery()) {
            while (rs.next()) {
                ds.add(map(rs));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi truy vấn DichVu", ex);
        }
        return ds;
    }

    public static DichVu layDichVuTheoMa(String maDichVu) {
        String sql = "SELECT maDichVu, tenDichVu, donGiaNiemYet, trangThai FROM DichVu WHERE maDichVu = ?";
        Connection con = ConnectDB.getInstance().getConnection();
        try (PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setString(1, maDichVu);
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi truy vấn DichVu", ex);
        }
        return null;
    }

    public static boolean kiemTraTenTonTai(String maDichVuBoQua, String tenDichVu) {
        String sql = "SELECT COUNT(*) FROM DichVu WHERE tenDichVu = ? AND maDichVu <> ?";
        Connection con = ConnectDB.getInstance().getConnection();
        try (PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setString(1, tenDichVu);
            pstm.setString(2, maDichVuBoQua == null ? "" : maDichVuBoQua);
            try (ResultSet rs = pstm.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi kiểm tra tên dịch vụ", ex);
        }
    }

    public static void themDichVu(Connection con, DichVu dv) throws SQLException {
        String sql = "INSERT INTO DichVu (maDichVu, tenDichVu, donGiaNiemYet, trangThai) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setString(1, dv.getMaDichVu());
            pstm.setString(2, dv.getTenDichVu());
            pstm.setBigDecimal(3, dv.getDonGiaNiemYet());
            pstm.setString(4, dv.getTrangThai().name());
            pstm.executeUpdate();
        }
    }

    public static boolean tonTaiMa(Connection con, String ma) throws SQLException {
        try (PreparedStatement p = con.prepareStatement("SELECT 1 FROM DichVu WHERE maDichVu = ?")) {
            p.setString(1, ma);
            try (ResultSet rs = p.executeQuery()) { return rs.next(); }
        }
    }

    public static void suaDichVu(Connection con, DichVu dv) throws SQLException {
        String sql = "UPDATE DichVu SET tenDichVu = ?, donGiaNiemYet = ?, trangThai = ? WHERE maDichVu = ?";
        try (PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setString(1, dv.getTenDichVu());
            pstm.setBigDecimal(2, dv.getDonGiaNiemYet());
            pstm.setString(3, dv.getTrangThai().name());
            pstm.setString(4, dv.getMaDichVu());
            pstm.executeUpdate();
        }
    }

    private DichVu_DAO() { }
}
