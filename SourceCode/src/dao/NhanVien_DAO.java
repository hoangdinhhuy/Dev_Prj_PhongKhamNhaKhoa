package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import connectDB.ConnectDB;
import entity.ChucVu;
import entity.NhanVien;

public class NhanVien_DAO {

    private static final String COLS = "maNhanVien, hoTen, soDienThoai, ngaySinh, gioiTinh, cccd, chucVu, conLamViec";

    private static NhanVien map(ResultSet rs) throws SQLException {
        NhanVien nv = new NhanVien(rs.getString("maNhanVien"), rs.getString("hoTen"),
                rs.getString("soDienThoai"), rs.getDate("ngaySinh") == null ? null : rs.getDate("ngaySinh").toLocalDate(),
                rs.getObject("gioiTinh") == null ? null : rs.getBoolean("gioiTinh"),
                rs.getString("cccd"), ChucVu.valueOf(rs.getString("chucVu")), rs.getBoolean("conLamViec"));
        return nv;
    }

    public static ArrayList<NhanVien> getAllNhanVien() {
        String sql = "SELECT " + COLS + " FROM NhanVien ORDER BY maNhanVien";
        ArrayList<NhanVien> ds = new ArrayList<>();
        Connection con = ConnectDB.getInstance().getConnection();
        try (PreparedStatement pstm = con.prepareStatement(sql); ResultSet rs = pstm.executeQuery()) {
            while (rs.next()) {
                ds.add(map(rs));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi truy vấn NhanVien", ex);
        }
        return ds;
    }

    public static NhanVien layNhanVienTheoMa(String maNhanVien) {
        String sql = "SELECT " + COLS + " FROM NhanVien WHERE maNhanVien = ?";
        Connection con = ConnectDB.getInstance().getConnection();
        try (PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setString(1, maNhanVien);
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi truy vấn NhanVien", ex);
        }
        return null;
    }

    /** Danh sach bac si con lam viec va tai khoan HOAT_DONG - dung cho man hinh lap phieu. */
    public static ArrayList<NhanVien> layDanhSachBacSiHoatDong() {
        String sql = "SELECT nv.maNhanVien, nv.hoTen, nv.soDienThoai, nv.ngaySinh, nv.gioiTinh,"
                + " nv.cccd, nv.chucVu, nv.conLamViec"
                + " FROM NhanVien nv JOIN TaiKhoan tk ON tk.maNhanVien = nv.maNhanVien"
                + " WHERE nv.chucVu = 'BAC_SI' AND nv.conLamViec = 1 AND tk.trangThai = 'HOAT_DONG'"
                + " ORDER BY nv.maNhanVien";
        ArrayList<NhanVien> ds = new ArrayList<>();
        Connection con = ConnectDB.getInstance().getConnection();
        try (PreparedStatement pstm = con.prepareStatement(sql); ResultSet rs = pstm.executeQuery()) {
            while (rs.next()) {
                ds.add(map(rs));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi truy vấn danh sách bác sĩ", ex);
        }
        return ds;
    }

    public static void themNhanVien(Connection con, NhanVien nv) throws SQLException {
        String sql = "INSERT INTO NhanVien (maNhanVien, hoTen, soDienThoai, ngaySinh, gioiTinh, cccd, chucVu, conLamViec)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setString(1, nv.getMaNhanVien());
            pstm.setString(2, nv.getHoTen());
            pstm.setString(3, nv.getSoDienThoai());
            pstm.setDate(4, nv.getNgaySinh() == null ? null : Date.valueOf(nv.getNgaySinh()));
            pstm.setObject(5, nv.getGioiTinh());
            pstm.setString(6, nv.getCccd());
            pstm.setString(7, nv.getChucVu().name());
            pstm.setBoolean(8, nv.isConLamViec());
            pstm.executeUpdate();
        }
    }

    public static void suaNhanVien(Connection con, NhanVien nv) throws SQLException {
        String sql = "UPDATE NhanVien SET hoTen = ?, soDienThoai = ?, ngaySinh = ?, gioiTinh = ?, cccd = ?,"
                + " chucVu = ?, conLamViec = ? WHERE maNhanVien = ?";
        try (PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setString(1, nv.getHoTen());
            pstm.setString(2, nv.getSoDienThoai());
            pstm.setDate(3, nv.getNgaySinh() == null ? null : Date.valueOf(nv.getNgaySinh()));
            pstm.setObject(4, nv.getGioiTinh());
            pstm.setString(5, nv.getCccd());
            pstm.setString(6, nv.getChucVu().name());
            pstm.setBoolean(7, nv.isConLamViec());
            pstm.setString(8, nv.getMaNhanVien());
            pstm.executeUpdate();
        }
    }

    public static void xoaNhanVien(Connection con, String maNhanVien) throws SQLException {
        try (PreparedStatement pstm = con.prepareStatement("DELETE FROM NhanVien WHERE maNhanVien = ?")) {
            pstm.setString(1, maNhanVien);
            pstm.executeUpdate();
        }
    }

    private NhanVien_DAO() { }
}
