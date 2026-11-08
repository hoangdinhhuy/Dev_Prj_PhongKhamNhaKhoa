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
            + " pk.phacDieuTri, pk.ngayHenTiep,"
            + " pk.maNhanVien, pk.maPhong, pk.maHoSo, pk.maHoaDonPhiKham, bn.hoTen AS tenBenhNhan, nv.hoTen AS tenBacSi,"
            + " p.tenPhong AS tenPhong";
    // ghep data tao thanh phieu kham - phuc vu in phieu kham
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
        // Quan he doi tuong (N11-style): nap object tu JOIN, khong giu FK String rieng
        entity.BenhNhan bn = new entity.BenhNhan(rs.getString("maBenhNhan"));
        bn.setHoTen(rs.getString("tenBenhNhan"));
        pk.setBenhNhan(bn);
        entity.NhanVien nv = new entity.NhanVien(rs.getString("maNhanVien"));
        nv.setHoTen(rs.getString("tenBacSi"));
        pk.setNhanVien(nv);
        entity.PhongKham phong = new entity.PhongKham(rs.getString("maPhong"));
        phong.setTenPhong(rs.getString("tenPhong"));
        pk.setPhong(phong);
        pk.setHoaDonPhiKham(new entity.HoaDon(rs.getString("maHoaDonPhiKham")));  // setter chap nhan null
        pk.setMaHoSo(rs.getString("maHoSo"));
        pk.setPhacDieuTri(rs.getString("phacDieuTri"));
        Date hen = rs.getDate("ngayHenTiep");
        pk.setNgayHenTiep(hen == null ? null : hen.toLocalDate());
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
                                          String ketQuaDieuTri, String ghiChuThuocTuMua, String maHoSo,
                                          String phacDieuTri, java.time.LocalDate ngayHenTiep) throws SQLException {
        String sql = "UPDATE PhieuKham SET chanDoan = ?, ketQuaDieuTri = ?, ghiChuThuocTuMua = ?, maHoSo = ?,"
                + " phacDieuTri = ?, ngayHenTiep = ?"
                + " WHERE maPhieuKham = ? AND maNhanVien = ? AND trangThai = 'DANG_KHAM'";
        try (PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setString(1, chanDoan);
            pstm.setString(2, ketQuaDieuTri);
            pstm.setString(3, ghiChuThuocTuMua);
            pstm.setString(4, maHoSo);
            pstm.setString(5, phacDieuTri == null || phacDieuTri.isBlank() ? null : phacDieuTri);
            pstm.setDate(6, ngayHenTiep == null ? null : Date.valueOf(ngayHenTiep));
            pstm.setString(7, maPhieuKham);
            pstm.setString(8, maBacSi);
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

    /** (Gop y GV 2/7) So phieu kham cua mot benh nhan (moi trang thai) — de hien lan dieu tri thu N. */
    public static int demTheoBenhNhan(String maBenhNhan) {
        String sql = "SELECT COUNT(*) FROM PhieuKham WHERE maBenhNhan = ?";
        Connection con = ConnectDB.getInstance().getConnection();
        try (PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setString(1, maBenhNhan);
            try (ResultSet rs = pstm.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi đếm phiếu khám theo bệnh nhân", ex);
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

    /** (Gop y GV 2/5) Lich su kham theo khoang ngay + gioi han — khong tai toan bo. */
    public static ArrayList<PhieuKham> layLichSuKhoang(java.time.LocalDate tu, java.time.LocalDate den, int gioiHan) {
        String sql = "SELECT " + COLS + JOINS
                + " WHERE pk.ngayKham >= ? AND pk.ngayKham <= ?"
                + " ORDER BY pk.ngayKham DESC, pk.soThuTu"
                + " OFFSET 0 ROWS FETCH NEXT ? ROWS ONLY";
        ArrayList<PhieuKham> ds = new ArrayList<>();
        Connection con = ConnectDB.getInstance().getConnection();
        try (PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setDate(1, Date.valueOf(tu));
            pstm.setDate(2, Date.valueOf(den));
            pstm.setInt(3, Math.max(1, gioiHan));
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    ds.add(map(rs));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi truy vấn lịch sử khám", ex);
        }
        return ds;
    }

    /** (Gop y GV P1-7) Lich su khoang ngay kem "lan dieu tri thu N" (ROW_NUMBER theo benh nhan). */
    public static ArrayList<PhieuKham> layLichSuKhoangLanThu(java.time.LocalDate tu, java.time.LocalDate den,
                                                             int gioiHan) {
        String sql = "SELECT x.* FROM (SELECT " + COLS + ","
                + " ROW_NUMBER() OVER (PARTITION BY pk.maBenhNhan ORDER BY pk.ngayKham, pk.soThuTu) AS lanThu"
                + JOINS + ") x"
                + " WHERE x.ngayKham >= ? AND x.ngayKham <= ?"
                + " ORDER BY x.ngayKham DESC, x.soThuTu"
                + " OFFSET 0 ROWS FETCH NEXT ? ROWS ONLY";
        ArrayList<PhieuKham> ds = new ArrayList<>();
        Connection con = ConnectDB.getInstance().getConnection();
        try (PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setDate(1, Date.valueOf(tu));
            pstm.setDate(2, Date.valueOf(den));
            pstm.setInt(3, Math.max(1, gioiHan));
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    PhieuKham pk = map(rs);
                    pk.setLanThu(rs.getInt("lanThu"));
                    ds.add(pk);
                }
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
                + " AND EXISTS (SELECT 1 FROM ChiDinhDichVu cd JOIN DichVu dv ON dv.maDichVu = cd.maDichVu"
                + " WHERE cd.maPhieuKham = pk.maPhieuKham"
                + " AND CASE WHEN cd.maDichVu = 'DV0001' AND pk.maHoaDonPhiKham IS NOT NULL"
                + " THEN 0 ELSE dv.donGiaNiemYet END > 0)"
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

    /** (De xuat cua Thanh) Thong ke so phieu theo phong x trang thai trong khoang ngay. */
    public static ArrayList<Object[]> thongKeTheoPhong(java.time.LocalDate tu, java.time.LocalDate den) {
        String sql = "SELECT p.tenPhong, p.maPhong,"
                + " SUM(CASE WHEN pk.trangThai = 'CHO_KHAM' THEN 1 ELSE 0 END),"
                + " SUM(CASE WHEN pk.trangThai = 'DANG_KHAM' THEN 1 ELSE 0 END),"
                + " SUM(CASE WHEN pk.trangThai = 'HOAN_TAT' THEN 1 ELSE 0 END),"
                + " COUNT(*)"
                + " FROM PhieuKham pk JOIN PhongKham p ON p.maPhong = pk.maPhong"
                + " WHERE pk.ngayKham >= ? AND pk.ngayKham <= ?"
                + " GROUP BY p.tenPhong, p.maPhong ORDER BY COUNT(*) DESC";
        ArrayList<Object[]> ds = new ArrayList<>();
        Connection con = ConnectDB.getInstance().getConnection();
        try (PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setDate(1, Date.valueOf(tu));
            pstm.setDate(2, Date.valueOf(den));
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    ds.add(new Object[]{rs.getString(1) + " (" + rs.getString(2) + ")",
                            rs.getInt(3), rs.getInt(4), rs.getInt(5), rs.getInt(6)});
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi thống kê hàng chờ theo phòng", ex);
        }
        return ds;
    }

    private PhieuKham_DAO() { }
}
