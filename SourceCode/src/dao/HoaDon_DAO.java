package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import connectDB.ConnectDB;
import entity.ChiTietHoaDon;
import entity.HoaDon;

public class HoaDon_DAO {

    private static final String COLS = "hd.maHoaDon, hd.ngayThu, hd.hinhThucThanhToan, hd.daThanhToan,"
            + " hd.tienKhachDua, hd.maBenhNhan, hd.maNhanVien, hd.maPhieuKham, bn.hoTen AS tenBenhNhan, bn.soDienThoai AS bnSdt,"
            + " nv.hoTen AS tenNhanVien,"
            + " (SELECT SUM(ct.thanhTien) FROM ChiTietHoaDon ct WHERE ct.maHoaDon = hd.maHoaDon) AS tongTien";

    private static HoaDon map(ResultSet rs) throws SQLException {
        HoaDon hd = new HoaDon();
        hd.setMaHoaDon(rs.getString("maHoaDon"));
        Timestamp ts = rs.getTimestamp("ngayThu");
        hd.setNgayThu(ts == null ? null : ts.toLocalDateTime());
        hd.setHinhThucThanhToan(rs.getString("hinhThucThanhToan"));
        hd.setDaThanhToan(rs.getBoolean("daThanhToan"));
        hd.setTienKhachDua(rs.getBigDecimal("tienKhachDua"));
        hd.setMaBenhNhan(rs.getString("maBenhNhan"));
        hd.setMaNhanVien(rs.getString("maNhanVien"));
        hd.setMaPhieuKham(rs.getString("maPhieuKham"));
        // Quan he doi tuong (Gop y GV P2-2): HoaDon gan BenhNhan (khach hang) + NhanVien (nguoi lap)
        hd.setBenhNhan(new entity.BenhNhan(rs.getString("maBenhNhan"), rs.getString("tenBenhNhan"),
                rs.getString("bnSdt"), null, null, null, null));
        entity.NhanVien nv = new entity.NhanVien(rs.getString("maNhanVien"));
        nv.setHoTen(rs.getString("tenNhanVien"));
        hd.setNhanVien(nv);
        return hd;
    }

    public static void themHoaDon(Connection con, HoaDon hd) throws SQLException {
        String sql = "INSERT INTO HoaDon (maHoaDon, ngayThu, hinhThucThanhToan, daThanhToan, tienKhachDua,"
                + " maBenhNhan, maNhanVien, maPhieuKham) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setString(1, hd.getMaHoaDon());
            pstm.setTimestamp(2, Timestamp.valueOf(hd.getNgayThu()));
            pstm.setString(3, hd.getHinhThucThanhToan());
            pstm.setBoolean(4, hd.isDaThanhToan());
            pstm.setBigDecimal(5, hd.getTienKhachDua());
            pstm.setString(6, hd.getMaBenhNhan());
            pstm.setString(7, hd.getMaNhanVien());
            pstm.setString(8, hd.getMaPhieuKham());
            pstm.executeUpdate();
        }
    }

    /** Bỏ qua mã đã có khi bộ đếm chưa đồng bộ với dữ liệu hóa đơn cũ. */
    public static boolean tonTaiMa(Connection con, String maHoaDon) throws SQLException {
        String sql = "SELECT 1 FROM HoaDon WHERE maHoaDon = ?";
        try (PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setString(1, maHoaDon);
            try (ResultSet rs = pstm.executeQuery()) {
                return rs.next();
            }
        }
    }

    public static HoaDon layHoaDonTheoMa(String maHoaDon) {
        String sql = "SELECT " + COLS + " FROM HoaDon hd"
                + " JOIN BenhNhan bn ON bn.maBenhNhan = hd.maBenhNhan"
                + " LEFT JOIN NhanVien nv ON nv.maNhanVien = hd.maNhanVien"
                + " WHERE hd.maHoaDon = ?";
        Connection con = ConnectDB.getInstance().getConnection();
        try (PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setString(1, maHoaDon);
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi truy vấn HoaDon", ex);
        }
        return null;
    }

    /** Hoa don phi kham (DV0001) da thanh toan, CHUA gan phieu kham nao - de chan thu trung / dung lai. */
    public static HoaDon layHoaDonPhiChuaDungTheoBN(String maBenhNhan) {
        String sql = "SELECT " + COLS + " FROM HoaDon hd"
                + " JOIN BenhNhan bn ON bn.maBenhNhan = hd.maBenhNhan"
                + " LEFT JOIN NhanVien nv ON nv.maNhanVien = hd.maNhanVien"
                + " JOIN ChiTietHoaDon ct ON ct.maHoaDon = hd.maHoaDon AND ct.maDichVu = 'DV0001'"
                + " WHERE hd.maBenhNhan = ? AND hd.daThanhToan = 1"
                + " AND NOT EXISTS (SELECT 1 FROM PhieuKham pk WHERE pk.maHoaDonPhiKham = hd.maHoaDon)"
                + " ORDER BY hd.ngayThu DESC";
        Connection con = ConnectDB.getInstance().getConnection();
        try (PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setString(1, maBenhNhan);
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    return nap(map(rs));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi truy vấn hóa đơn phí khám", ex);
        }
        return null;
    }

    /** Hoa don dich vu da lien ket voi mot luot kham (kiem tra da thanh toan chua). */
    public static HoaDon layHoaDonTheoPhieu(String maPhieuKham) {
        String sql = "SELECT " + COLS + " FROM HoaDon hd"
                + " JOIN BenhNhan bn ON bn.maBenhNhan = hd.maBenhNhan"
                + " LEFT JOIN NhanVien nv ON nv.maNhanVien = hd.maNhanVien"
                + " WHERE hd.maPhieuKham = ?";
        Connection con = ConnectDB.getInstance().getConnection();
        try (PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setString(1, maPhieuKham);
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    return nap(map(rs));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi truy vấn hóa đơn theo phiếu", ex);
        }
        return null;
    }


    /** Nap chi tiet vao hoa don → entity tu tinh tongTien (khong dung setter). */
    private static HoaDon nap(HoaDon hd) {
        for (ChiTietHoaDon ct : ChiTietHoaDon_DAO.layChiTietTheoHoaDon(hd.getMaHoaDon())) {
            hd.themChiTiet(ct);
        }
        return hd;
    }

    /** (Chốt ca thu ngân) Toàn bộ hóa đơn ĐÃ THU trong ngày — bảng kê bàn giao ca. */
    public static ArrayList<HoaDon> layHoaDonDaThuHomNay(java.time.LocalDate homNay) {
        String sql = "SELECT " + COLS
                + " FROM HoaDon hd"
                + " JOIN BenhNhan bn ON bn.maBenhNhan = hd.maBenhNhan"
                + " LEFT JOIN NhanVien nv ON nv.maNhanVien = hd.maNhanVien"
                + " WHERE hd.daThanhToan = 1 AND CAST(hd.ngayThu AS date) = ?"
                + " ORDER BY hd.ngayThu";
        ArrayList<HoaDon> ds = new ArrayList<>();
        Connection con = ConnectDB.getInstance().getConnection();
        try (PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setDate(1, java.sql.Date.valueOf(homNay));
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    ds.add(nap(map(rs)));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi truy vấn bảng kê thu hôm nay", ex);
        }
        return ds;
    }

    /** Hóa đơn thuộc một ca, kể cả ca kéo dài qua nửa đêm. */
    public static ArrayList<HoaDon> layHoaDonTrongKhoang(java.time.LocalDateTime tu,
                                                          java.time.LocalDateTime den) {
        String sql = "SELECT " + COLS + " FROM HoaDon hd"
                + " JOIN BenhNhan bn ON bn.maBenhNhan = hd.maBenhNhan"
                + " LEFT JOIN NhanVien nv ON nv.maNhanVien = hd.maNhanVien"
                + " WHERE hd.daThanhToan = 1 AND hd.ngayThu >= ? AND hd.ngayThu <= ?"
                + " ORDER BY hd.ngayThu";
        ArrayList<HoaDon> ds = new ArrayList<>();
        Connection con = ConnectDB.getInstance().getConnection();
        try (PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setTimestamp(1, java.sql.Timestamp.valueOf(tu));
            pstm.setTimestamp(2, java.sql.Timestamp.valueOf(den));
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) ds.add(nap(map(rs)));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi truy vấn bảng kê trong ca", ex);
        }
        return ds;
    }

    private HoaDon_DAO() { }
}
