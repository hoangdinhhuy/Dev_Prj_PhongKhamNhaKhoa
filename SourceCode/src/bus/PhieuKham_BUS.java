package bus;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import connectDB.ConnectDB;
import dao.BoDem_DAO;
import dao.HoaDon_DAO;
import dao.NhanVien_DAO;
import dao.PhieuKham_DAO;
import dao.PhongKham_DAO;
import entity.BenhNhan;
import entity.ChucVu;
import entity.HoaDon;
import entity.NhanVien;
import entity.PhieuKham;
import entity.PhongKham;
import entity.SessionContext;
import entity.TrangThaiTaiKhoan;
import entity.TrangThaiPhieuKham;
import util.MaThongBao;
import util.QuyenUtil;
import util.ValidateUtil;

public class PhieuKham_BUS {

    /** UC003 - lap phieu kham: ma PK + STT (BoDem) + INSERT trong 1 transaction, trang thai CHO_KHAM. */
    public static PhieuKham lapPhieuKham(SessionContext session, BenhNhan benhNhan, String maBacSi,
                                         String maPhong, String lyDoKham, HoaDon hoaDonPhi) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.LE_TAN);
        if (benhNhan == null || benhNhan.getMaBenhNhan() == null) {
            throw new IllegalArgumentException(MaThongBao.VL05);
        }
        String lyDo = ValidateUtil.khongRong(lyDoKham, MaThongBao.VL08);
        if (maBacSi == null || maBacSi.isBlank() || maPhong == null || maPhong.isBlank()) {
            throw new IllegalArgumentException(MaThongBao.VL07);
        }

        NhanVien bacSi = NhanVien_DAO.layNhanVienTheoMa(maBacSi);
        PhongKham phong = PhongKham_DAO.layPhongTheoMa(maPhong);
        boolean bacSiTrongCa = bacSi != null && bacSi.isConLamViec() && bacSi.getChucVu() == ChucVu.BAC_SI
                && dao.TaiKhoan_DAO.layTaiKhoanTheoMaNhanVien(maBacSi) != null
                && dao.TaiKhoan_DAO.layTaiKhoanTheoMaNhanVien(maBacSi)
                        .getTrangThai() == TrangThaiTaiKhoan.HOAT_DONG;
        if (!bacSiTrongCa || phong == null || !phong.isDangNhanBenh()) {
            throw new IllegalArgumentException(MaThongBao.TB03);
        }

        // Hoa don phi phai da thanh toan va CHUA gan phieu nao
        HoaDon chuaDung = HoaDon_DAO.layHoaDonPhiChuaDungTheoBN(benhNhan.getMaBenhNhan());
        if (hoaDonPhi == null || chuaDung == null
                || !chuaDung.getMaHoaDon().equals(hoaDonPhi.getMaHoaDon())) {
            throw new IllegalArgumentException(MaThongBao.TB04);
        }

        LocalDate ngayKham = LocalDate.now();
        Connection con = ConnectDB.getInstance().getConnection();
        try {
            con.setAutoCommit(false);
            String maPK = BoDem_DAO.layMaMoi(con, "PK", ngayKham);
            int stt = BoDem_DAO.laySoThuTuTiepTheo(con, ngayKham, maPhong);
            PhieuKham pk = new PhieuKham();
            pk.setMaPhieuKham(maPK);
            pk.setNgayKham(ngayKham);
            pk.setSoThuTu(stt);
            pk.setLyDoKham(lyDo);
            pk.setTrangThai(TrangThaiPhieuKham.CHO_KHAM);
            pk.setMaBenhNhan(benhNhan.getMaBenhNhan());
            pk.setMaNhanVien(maBacSi);
            pk.setMaPhong(maPhong);
            pk.setMaHoaDonPhiKham(hoaDonPhi.getMaHoaDon());
            PhieuKham_DAO.themPhieuKham(con, pk);
            con.commit();
            con.setAutoCommit(true);
            return PhieuKham_DAO.layPhieuKhamTheoMa(maPK);
        } catch (SQLException ex) {
            rollback(con);
            throw new RuntimeException("Lỗi lập phiếu khám: " + ex.getMessage(), ex);
        }
    }

    /** Lich su kham toan he thong (man hinh Lich su kham — BAC_SI). */
    public static java.util.List<PhieuKham> layToanBoLichSu(SessionContext session) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.BAC_SI);
        return PhieuKham_DAO.layToanBoLichSu();
    }

    /** Phieu hoan tat chua thanh toan dich vu (man hinh Thanh toan — LE_TAN). */
    public static java.util.List<PhieuKham> layPhieuHoanTatChuaThanhToan(SessionContext session) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.LE_TAN);
        return PhieuKham_DAO.layPhieuHoanTatChuaThanhToan();
    }

    private static void rollback(Connection con) {
        try {
            con.rollback();
            con.setAutoCommit(true);
        } catch (SQLException ignored) { }
    }

    private PhieuKham_BUS() { }
}
