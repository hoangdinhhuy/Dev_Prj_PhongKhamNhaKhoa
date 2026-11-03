package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import connectDB.ConnectDB;
import entity.PhieuKham;
import entity.TrangThaiPhieuKham;

public class PhieuKham_DAO {

    public static boolean tonTaiMa(Connection con, String ma) throws SQLException {
        try (PreparedStatement p = con.prepareStatement("SELECT 1 FROM PhieuKham WHERE maPhieuKham = ?")) {
            p.setString(1, ma);
            try (ResultSet rs = p.executeQuery()) { return rs.next(); }
        }
    }

    public static boolean tonTaiSoThuTu(Connection con, java.time.LocalDate ngay, String maPhong, int stt) throws SQLException {
        String sql = "SELECT 1 FROM PhieuKham WHERE ngayKham = ? AND maPhong = ? AND soThuTu = ?";
        try (PreparedStatement p = con.prepareStatement(sql)) {
            p.setDate(1, Date.valueOf(ngay)); p.setString(2, maPhong); p.setInt(3, stt);
            try (ResultSet rs = p.executeQuery()) { return rs.next(); }
        }
    }

    private static final String COLS = "pk.maPhieuKham, pk.ngayKham, pk.soThuTu, pk.lyDoKham, pk.chanDoan,"
            + " pk.ketQuaDieuTri, pk.ghiChuThuocSuDung, pk.ghiChuThuocTuMua, pk.trangThai, pk.maBenhNhan,"
            + " pk.maNhanVien, pk.maPhong, pk.maHoSo, pk.maHoaDonPhiKham, bn.hoTen AS tenBenhNhan, nv.hoTen AS tenBacSi,"
            + " p.tenPhong AS tenPhong";
    private static final String JOINS = " FROM PhieuKham pk"
            + " JOIN BenhNhan bn ON bn.maBenhNhan = pk.maBenhNhan"
            + " JOIN NhanVien nv ON nv.maNhanVien = pk.maNhanVien"
            + " JOIN PhongKham p ON p.maPhong = pk.maPhong";

    private static PhieuKham map(ResultSet rs) throws SQLException {
        PhieuKham pk = new PhieuKham();
        pk.setMaPhieuKham(rs.getString("maPhieuKham"));
        pk.setNgayKham(rs.getDate("ngayKham").toLocalDate());
        pk.setSoThuTu(rs.getInt("soThuTu"));
        pk.setLyDoKham(rs.getString("lyDoKham"));
        pk.setChanDoan(rs.getString("chanDoan"));
        pk.setKetQuaDieuTri(rs.getString("ketQuaDieuTri"));
        pk.setGhiChuThuocSuDung(rs.getString("ghiChuThuocSuDung"));
        pk.setGhiChuThuocTuMua(rs.getString("ghiChuThuocTuMua"));
        pk.setTrangThai(TrangThaiPhieuKham.valueOf(rs.getString("trangThai")));
        pk.setMaBenhNhan(rs.getString("maBenhNhan"));
        pk.setMaNhanVien(rs.getString("maNhanVien"));
        pk.setMaPhong(rs.getString("maPhong"));
        pk.setMaHoSo(rs.getString("maHoSo"));
        pk.setMaHoaDonPhiKham(rs.getString("maHoaDonPhiKham"));
        pk.setTenBenhNhan(rs.getString("tenBenhNhan"));
        pk.setTenBacSi(rs.getString("tenBacSi"));
        pk.setTenPhong(rs.getString("tenPhong"));
        return pk;
    }

    public static void themPhieuKham(Connection con, PhieuKham pk) throws SQLException {
        String sql = "INSERT INTO PhieuKham (maPhieuKham, ngayKham, soThuTu, lyDoKham, trangThai,"
                + " maBenhNhan, maNhanVien, maPhong, maHoaDonPhiKham) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setString(1, pk.getMaPhieuKham());
            pstm.setDate(2, Date.valueOf(pk.getNgayKham()));
            pstm.setInt(3, pk.getSoThuTu());
            pstm.setString(4, pk.getLyDoKham());
            pstm.setString(5, pk.getTrangThai().name());
            pstm.setString(6, pk.getMaBenhNhan());
            pstm.setString(7, pk.getMaNhanVien());
            pstm.setString(8, pk.getMaPhong());
            pstm.setString(9, pk.getMaHoaDonPhiKham());
            pstm.executeUpdate();
        }
    }

    public static PhieuKham layPhieuKhamTheoMa(String maPhieuKham) {
        String sql = "SELECT " + COLS + JOINS + " WHERE pk.maPhieuKham = ?";
        Connection con = ConnectDB.getInstance().getConnection();
        try (PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setString(1, maPhieuKham);
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi truy vấn PhieuKham", ex);
        }
        return null;
    }

    /** Danh sach phieu theo ngay + phong + trang thai (dung cho Hang cho). */
    public static ArrayList<PhieuKham> layDanhSachTheoNgayVaPhong(java.time.LocalDate ngay, String maPhong,
                                                                  TrangThaiPhieuKham trangThai) {
        String sql = "SELECT " + COLS + JOINS + " WHERE pk.ngayKham = ? AND pk.maPhong = ? AND pk.trangThai = ?"
                + " ORDER BY pk.soThuTu";
        ArrayList<PhieuKham> ds = new ArrayList<>();
        Connection con = ConnectDB.getInstance().getConnection();
        try (PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setDate(1, Date.valueOf(ngay));
            pstm.setString(2, maPhong);
            pstm.setString(3, trangThai.name());
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    ds.add(map(rs));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi truy vấn PhieuKham", ex);
        }
        return ds;
    }

    /** Danh sach phieu cua mot bac si trong ngay (CHO_KHAM + DANG_KHAM). */
    public static ArrayList<PhieuKham> layDanhSachTheoBacSi(String maBacSi, java.time.LocalDate ngay) {
        String sql = "SELECT " + COLS + JOINS + " WHERE pk.maNhanVien = ? AND pk.ngayKham = ?"
                + " AND pk.trangThai IN ('CHO_KHAM', 'DANG_KHAM') ORDER BY pk.soThuTu";
        ArrayList<PhieuKham> ds = new ArrayList<>();
        Connection con = ConnectDB.getInstance().getConnection();
        try (PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setString(1, maBacSi);
            pstm.setDate(2, Date.valueOf(ngay));
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    ds.add(map(rs));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi truy vấn PhieuKham", ex);
        }
        return ds;
    }

    public static ArrayList<PhieuKham> layTheoBenhNhan(String maBenhNhan) {
        String sql = "SELECT " + COLS + JOINS + " WHERE pk.maBenhNhan = ? ORDER BY pk.ngayKham DESC, pk.soThuTu";
        ArrayList<PhieuKham> ds = new ArrayList<>();
        Connection con = ConnectDB.getInstance().getConnection();
        try (PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setString(1, maBenhNhan);
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    ds.add(map(rs));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi truy vấn PhieuKham", ex);
        }
        return ds;
    }

    /** Luu chuyen mon: UPDATE co dieu kien trang thai + chu phieu, bao ton tai ghi de truong luong khac. */
    public static int capNhatThongTinKham(Connection con, String maPhieuKham, String maBacSi, String chanDoan,
                                          String ketQuaDieuTri, String ghiChuThuocTuMua, String maHoSo) throws SQLException {
        String sql = "UPDATE PhieuKham SET chanDoan = ?, ketQuaDieuTri = ?, ghiChuThuocTuMua = ?, maHoSo = ?"
                + " WHERE maPhieuKham = ? AND maNhanVien = ? AND trangThai = 'DANG_KHAM'";
        try (PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setString(1, chanDoan);
            pstm.setString(2, ketQuaDieuTri);
            pstm.setString(3, ghiChuThuocTuMua);
            pstm.setString(4, maHoSo);
            pstm.setString(5, maPhieuKham);
            pstm.setString(6, maBacSi);
            return pstm.executeUpdate();
        }
    }

    /** Chuyen trang thai co dieu kien trang thai cu. Tra ve so dong anh huong (0 = da bi doi trang thai). */
    public static int capNhatTrangThai(String maPhieuKham, TrangThaiPhieuKham trangThaiMoi,
                                       TrangThaiPhieuKham trangThaiCu) {
        String sql = "UPDATE PhieuKham SET trangThai = ? WHERE maPhieuKham = ? AND trangThai = ?";
        Connection con = ConnectDB.getInstance().getConnection();
        try (PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setString(1, trangThaiMoi.name());
            pstm.setString(2, maPhieuKham);
            pstm.setString(3, trangThaiCu.name());
            return pstm.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi cập nhật trạng thái phiếu khám", ex);
        }
    }

    public static int demTheoBacSi(String maBacSi) {
        String sql = "SELECT COUNT(*) FROM PhieuKham WHERE maNhanVien = ?";
        Connection con = ConnectDB.getInstance().getConnection();
        try (PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setString(1, maBacSi);
            try (ResultSet rs = pstm.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi đếm phiếu khám", ex);
        }
    }

    /** Toan bo lich su kham (moi trang thai) - man hinh Lich su kham. */
    public static ArrayList<PhieuKham> layToanBoLichSu() {
        String sql = "SELECT " + COLS + JOINS + " ORDER BY pk.ngayKham DESC, pk.soThuTu";
        ArrayList<PhieuKham> ds = new ArrayList<>();
        Connection con = ConnectDB.getInstance().getConnection();
        try (PreparedStatement pstm = con.prepareStatement(sql); ResultSet rs = pstm.executeQuery()) {
            while (rs.next()) {
                ds.add(map(rs));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi truy vấn lịch sử khám", ex);
        }
        return ds;
    }

    /** Phieu HOAN_TAT co dich vu phat sinh va chua co hoa don dich vu. */
    public static ArrayList<PhieuKham> layPhieuHoanTatChuaThanhToan() {
        String sql = "SELECT " + COLS + JOINS
                + " WHERE pk.trangThai = 'HOAN_TAT'"
                + " AND EXISTS (SELECT 1 FROM ChiDinhDichVu cd WHERE cd.maPhieuKham = pk.maPhieuKham)"
                + " AND NOT EXISTS (SELECT 1 FROM HoaDon hd WHERE hd.maPhieuKham = pk.maPhieuKham)"
                + " ORDER BY pk.ngayKham DESC";
        ArrayList<PhieuKham> ds = new ArrayList<>();
        Connection con = ConnectDB.getInstance().getConnection();
        try (PreparedStatement pstm = con.prepareStatement(sql); ResultSet rs = pstm.executeQuery()) {
            while (rs.next()) {
                ds.add(map(rs));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi truy vấn phiếu chờ thanh toán", ex);
        }
        return ds;
    }

    private PhieuKham_DAO() { }
}
