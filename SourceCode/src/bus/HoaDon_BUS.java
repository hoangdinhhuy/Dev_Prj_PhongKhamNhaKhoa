package bus;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import connectDB.ConnectDB;
import dao.BoDem_DAO;
import dao.ChiDinhDichVu_DAO;
import dao.ChiTietHoaDon_DAO;
import dao.DichVu_DAO;
import dao.HoaDon_DAO;
import dao.PhieuKham_DAO;
import entity.ChiDinhDichVu;
import entity.ChiTietHoaDon;
import entity.ChucVu;
import entity.DichVu;
import entity.HoaDon;
import entity.PhieuKham;
import entity.SessionContext;
import entity.TrangThaiPhieuKham;
import util.MaThongBao;
import util.QuyenUtil;
import util.ValidateUtil;

public class HoaDon_BUS {

    public static final String MA_DICH_VU_PHI_KHAM = "DV0001";

    /** UC005 - thu phi kham ban dau. Tra ve hoa don da thanh toan (chua gan phieu). */
    public static HoaDon thuPhiKham(SessionContext session, String maBenhNhan, String hinhThucThanhToan,
                                    String tienKhachDua) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.LE_TAN);
        String maBN = ValidateUtil.khongRong(maBenhNhan, MaThongBao.VL05);
        BigDecimal tienDua = ValidateUtil.donGia(tienKhachDua);

        DichVu phiKham = DichVu_DAO.layDichVuTheoMa(MA_DICH_VU_PHI_KHAM);
        if (phiKham == null) {
            throw new IllegalStateException("Thiếu dịch vụ seed 'Phí khám ban đầu' (DV0001). Chạy 003_seed_dichvu.sql.");
        }
        if (HoaDon_DAO.layHoaDonPhiChuaDungTheoBN(maBN) != null) {
            throw new IllegalArgumentException(MaThongBao.TB04);
        }
        if (tienDua.compareTo(phiKham.getDonGiaNiemYet()) < 0) {
            throw new IllegalArgumentException("Tiền khách đưa chưa đủ mức phí khám.");
        }

        Connection con = ConnectDB.getInstance().getConnection();
        try {
            con.setAutoCommit(false);
            String maHD = BoDem_DAO.layMaMoi(con, "HD", LocalDateTime.now().toLocalDate());
            HoaDon hd = new HoaDon(maHD);
            hd.setNgayThu(LocalDateTime.now());
            hd.setHinhThucThanhToan(hinhThucThanhToan);
            hd.setDaThanhToan(true);
            hd.setTienKhachDua(tienDua);
            hd.setMaBenhNhan(maBN);
            hd.setMaNhanVien(session.getMaNhanVien());
            HoaDon_DAO.themHoaDon(con, hd);
            ChiTietHoaDon_DAO.themChiTiet(con,
                    new ChiTietHoaDon(maHD, MA_DICH_VU_PHI_KHAM, 1, phiKham.getDonGiaNiemYet()));
            con.commit();
            con.setAutoCommit(true);
            hd.setTongTien(phiKham.getDonGiaNiemYet());
            return hd;
        } catch (SQLException ex) {
            rollback(con);
            throw new RuntimeException("Lỗi thu phí khám: " + ex.getMessage(), ex);
        }
    }

    /** Hoa don phi kham da thu CHUA gan phieu - dung cho nut "Dung hoa don da thu". */
    public static HoaDon layHoaDonPhiChuaDung(SessionContext session, String maBenhNhan) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.LE_TAN);
        return HoaDon_DAO.layHoaDonPhiChuaDungTheoBN(maBenhNhan);
    }

    /** LT-06 — Thanh toan dich vu cuoi luot: hoa don gom tat ca dich vu da chi dinh cua phieu HOAN_TAT. */
    public static HoaDon thanhToanDichVu(SessionContext session, String maPhieuKham,
                                         String hinhThucThanhToan, String tienKhachDua) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.LE_TAN);
        PhieuKham pk = PhieuKham_DAO.layPhieuKhamTheoMa(maPhieuKham);
        if (pk == null || pk.getTrangThai() != TrangThaiPhieuKham.HOAN_TAT) {
            throw new IllegalArgumentException(MaThongBao.TB10);
        }
        List<ChiDinhDichVu> chiDinh = ChiDinhDichVu_DAO.layTheoPhieu(maPhieuKham);
        if (chiDinh.isEmpty()) {
            throw new IllegalArgumentException(MaThongBao.TB06);
        }
        if (HoaDon_DAO.layHoaDonTheoPhieu(maPhieuKham) != null) {
            throw new IllegalArgumentException(MaThongBao.TB17);
        }
        BigDecimal tong = chiDinh.stream()
                .map(ChiDinhDichVu::getDonGia)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal tienDua = ValidateUtil.donGia(tienKhachDua);
        if (tienDua.compareTo(tong) < 0) {
            throw new IllegalArgumentException("Tiền khách đưa chưa đủ để thanh toán.");
        }

        Connection con = ConnectDB.getInstance().getConnection();
        try {
            con.setAutoCommit(false);
            String maHD = BoDem_DAO.layMaMoi(con, "HD", LocalDateTime.now().toLocalDate());
            HoaDon hd = new HoaDon(maHD);
            hd.setNgayThu(LocalDateTime.now());
            hd.setHinhThucThanhToan(hinhThucThanhToan);
            hd.setDaThanhToan(true);
            hd.setTienKhachDua(tienDua);
            hd.setMaBenhNhan(pk.getMaBenhNhan());
            hd.setMaNhanVien(session.getMaNhanVien());
            hd.setMaPhieuKham(maPhieuKham);
            HoaDon_DAO.themHoaDon(con, hd);
            for (ChiDinhDichVu cd : chiDinh) {
                ChiTietHoaDon_DAO.themChiTiet(con,
                        new ChiTietHoaDon(maHD, cd.getMaDichVu(), 1, cd.getDonGia()));
            }
            con.commit();
            con.setAutoCommit(true);
            hd.setTongTien(tong);
            return hd;
        } catch (SQLException ex) {
            rollback(con);
            throw new RuntimeException("Lỗi thanh toán dịch vụ: " + ex.getMessage(), ex);
        }
    }

    private static void rollback(Connection con) {
        try {
            con.rollback();
            con.setAutoCommit(true);
        } catch (SQLException ignored) { }
    }

    private HoaDon_BUS() { }
}
