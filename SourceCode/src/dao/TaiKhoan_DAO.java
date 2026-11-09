package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import connectDB.ConnectDB;
import entity.TaiKhoan;
import entity.TrangThaiTaiKhoan;

public class TaiKhoan_DAO {

    public static TaiKhoan getTaiKhoanTheoTenDangNhap(String tenDangNhap) {
        String sql = "SELECT tenDangNhap, matKhau, trangThai, maNhanVien FROM TaiKhoan WHERE tenDangNhap = ?";
        Connection con = ConnectDB.getInstance().getConnection();
        try (PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setString(1, tenDangNhap.trim());
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    return new TaiKhoan(rs.getString(1), rs.getString(2),
                            TrangThaiTaiKhoan.valueOf(rs.getString(3)), rs.getString(4));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi truy vấn TaiKhoan", ex);
        }
        return null;
    }

    public static boolean kiemTraTenDangNhapTonTai(String tenDangNhap) {
        return getTaiKhoanTheoTenDangNhap(tenDangNhap) != null;
    }

    public static void themTaiKhoan(Connection con, TaiKhoan tk) throws SQLException {
        String sql = "INSERT INTO TaiKhoan (tenDangNhap, matKhau, trangThai, maNhanVien) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setString(1, tk.getTenDangNhap().trim());
            pstm.setString(2, tk.getMatKhau());
            pstm.setString(3, tk.getTrangThai() == null ? "HOAT_DONG" : tk.getTrangThai().name());
            pstm.setString(4, tk.getMaNhanVien());
            pstm.executeUpdate();
        }
    }

    public static void capNhatTaiKhoan(Connection con, TaiKhoan tk) throws SQLException {
        String sql = "UPDATE TaiKhoan SET matKhau = ?, trangThai = ? WHERE tenDangNhap = ?";
        try (PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setString(1, tk.getMatKhau());
            pstm.setString(2, tk.getTrangThai().name());
            pstm.setString(3, tk.getTenDangNhap());
            pstm.executeUpdate();
        }
    }

    public static TaiKhoan layTaiKhoanTheoMaNhanVien(String maNhanVien) {
        String sql = "SELECT tenDangNhap, matKhau, trangThai, maNhanVien FROM TaiKhoan WHERE maNhanVien = ?";
        Connection con = ConnectDB.getInstance().getConnection();
        try (PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setString(1, maNhanVien);
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    return new TaiKhoan(rs.getString(1), rs.getString(2),
                            TrangThaiTaiKhoan.valueOf(rs.getString(3)), rs.getString(4));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi truy vấn TaiKhoan", ex);
        }
        return null;
    }

    /**
     * Lay map tai khoan theo ma nhan vien de toi uu hieu nang (tranh truy van N+1).
     */
    public static Map<String, TaiKhoan> getMapTaiKhoanTheoMaNV() {
        String sql = "SELECT tenDangNhap, matKhau, trangThai, maNhanVien FROM TaiKhoan";
        Connection con = ConnectDB.getInstance().getConnection();
        Map<String, TaiKhoan> map = new HashMap<>();
        try (PreparedStatement pstm = con.prepareStatement(sql);
             ResultSet rs = pstm.executeQuery()) {
            while (rs.next()) {
                TaiKhoan tk = new TaiKhoan(rs.getString(1), rs.getString(2),
                        TrangThaiTaiKhoan.valueOf(rs.getString(3)), rs.getString(4));
                map.put(tk.getMaNhanVien(), tk);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi truy vấn danh sách TaiKhoan", ex);
        }
        return map;
    }

    private TaiKhoan_DAO() { }
}
