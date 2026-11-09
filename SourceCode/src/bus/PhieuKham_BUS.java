package bus;

import java.time.LocalDate;
import java.util.ArrayList;
import dao.PhieuKham_DAO;
import entity.ChucVu;
import entity.PhieuKham;
import entity.SessionContext;
import entity.TrangThaiPhieuKham;
import util.QuyenUtil;
import util.ValidateUtil;

public class PhieuKham_BUS {

    /** UC003 - lap phieu kham: ma PK + STT (BoDem) + INSERT trong 1 transaction, trang thai CHO_KHAM. */
    public static PhieuKham lapPhieuKham(SessionContext session, entity.BenhNhan benhNhan, String maBacSi,
                                         String maPhong, String lyDoKham, entity.HoaDon hoaDonPhi) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.LE_TAN);
        if (benhNhan == null || benhNhan.getMaBenhNhan() == null) {
            throw new IllegalArgumentException(util.MaThongBao.VL05);
        }
        String lyDo = ValidateUtil.khongRong(lyDoKham, util.MaThongBao.VL08);
        if (maBacSi == null || maBacSi.isBlank() || maPhong == null || maPhong.isBlank()) {
            throw new IllegalArgumentException(util.MaThongBao.VL07);
        }

        entity.NhanVien bacSi = dao.NhanVien_DAO.layNhanVienTheoMa(maBacSi);
        entity.PhongKham phong = dao.PhongKham_DAO.layPhongTheoMa(maPhong);
        entity.TaiKhoan taiKhoanBacSi = dao.TaiKhoan_DAO.layTaiKhoanTheoMaNhanVien(maBacSi);
        boolean bacSiTrongCa = bacSi != null && bacSi.isConLamViec() && bacSi.getChucVu() == ChucVu.BAC_SI
                && taiKhoanBacSi != null
                && taiKhoanBacSi.getTrangThai() == entity.TrangThaiTaiKhoan.HOAT_DONG;
        entity.NhanVien phuTa = phong == null || phong.getMaPhuTa() == null ? null
                : dao.NhanVien_DAO.layNhanVienTheoMa(phong.getMaPhuTa());
        entity.TaiKhoan taiKhoanPhuTa = phong == null || phong.getMaPhuTa() == null ? null
                : dao.TaiKhoan_DAO.layTaiKhoanTheoMaNhanVien(phong.getMaPhuTa());
        if (!bacSiTrongCa || phong == null || !phong.isDangNhanBenh()) {
            throw new IllegalArgumentException(util.MaThongBao.TB03);
        }
        if (!maBacSi.equals(phong.getMaBacSi()) || phuTa == null
                || !phuTa.isConLamViec() || phuTa.getChucVu() != ChucVu.PHU_TA
                || taiKhoanPhuTa == null
                || taiKhoanPhuTa.getTrangThai() != entity.TrangThaiTaiKhoan.HOAT_DONG) {
            throw new IllegalArgumentException(util.MaThongBao.TB21);
        }

        // Hoa don phi phai da thanh toan va CHUA gan phieu nao
        entity.HoaDon chuaDung = HoaDon_BUS.layHoaDonPhiChuaDung(session, benhNhan.getMaBenhNhan());
        if (hoaDonPhi == null || chuaDung == null
                || !chuaDung.getMaHoaDon().equals(hoaDonPhi.getMaHoaDon())) {
            throw new IllegalArgumentException(util.MaThongBao.TB04);
        }

        LocalDate ngayKham = LocalDate.now();
        java.sql.Connection con = connectDB.ConnectDB.getInstance().getConnection();
        try {
            con.setAutoCommit(false);
            String maPK;
            do { maPK = dao.BoDem_DAO.layMaMoi(con, "PK", ngayKham); }
            while (PhieuKham_DAO.tonTaiMa(con, maPK));
            int stt;
            do { stt = dao.BoDem_DAO.laySoThuTuTiepTheo(con, ngayKham, maPhong); }
            while (PhieuKham_DAO.tonTaiSoThuTu(con, ngayKham, maPhong, stt));
            PhieuKham pk = new PhieuKham();
            pk.setMaPhieuKham(maPK);
            pk.setNgayKham(ngayKham);
            pk.setSoThuTu(stt);
            pk.setLyDoKham(lyDo);
            pk.setTrangThai(TrangThaiPhieuKham.CHO_KHAM);
            pk.setBenhNhan(benhNhan);        // dung doi tuong co san (P2-1)
            pk.setNhanVien(bacSi);
            pk.setPhong(phong);
            pk.setHoaDonPhiKham(hoaDonPhi);
            PhieuKham_DAO.themPhieuKham(con, pk);
            con.commit();
            con.setAutoCommit(true);
            return PhieuKham_DAO.layPhieuKhamTheoMa(maPK);
        } catch (java.sql.SQLException ex) {
            rollback(con);
            throw new RuntimeException("Lỗi lập phiếu khám: " + ex.getMessage(), ex);
        }
    }

    /** Lich su kham toan he thong (man hinh Lich su kham — BAC_SI). */
    public static java.util.List<PhieuKham> layToanBoLichSu(SessionContext session) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.BAC_SI);
        return PhieuKham_DAO.layToanBoLichSu();
    }

    /** (Gop y GV 2/5) Lich su kham theo khoang ngay + gioi han — khong tai toan bo. */
    public static java.util.List<PhieuKham> layLichSuKhoang(SessionContext session, LocalDate tu,
                                                            LocalDate den, int gioiHan) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.BAC_SI, ChucVu.QUAN_LY, ChucVu.LE_TAN);
        return PhieuKham_DAO.layLichSuKhoang(tu, den, gioiHan);
    }

    /** (Gop y GV P1-7) Nhu layLichSuKhoang nhung kem "lan dieu tri thu N" cua tung benh nhan. */
    public static java.util.List<PhieuKham> layLichSuKhoangLanThu(SessionContext session, LocalDate tu,
                                                                  LocalDate den, int gioiHan) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.BAC_SI, ChucVu.QUAN_LY, ChucVu.LE_TAN);
        return PhieuKham_DAO.layLichSuKhoangLanThu(tu, den, gioiHan);
    }

    /** (Gop y GV P1-7) Chi dinh dich vu (kem so rang FDI) cua mot luot kham — dung cho popup lich su. */
    public static java.util.List<entity.ChiDinhDichVu> layChiDinhTheoPhieu(SessionContext session,
                                                                           String maPhieuKham) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.BAC_SI, ChucVu.QUAN_LY, ChucVu.LE_TAN);
        return PhieuKham_DAO.layPhieuKhamTheoMa(maPhieuKham) == null
                ? java.util.List.of()
                : dao.ChiDinhDichVu_DAO.layTheoPhieu(maPhieuKham);
    }

    /** Phieu hoan tat co dich vu phat sinh, chua thanh toan (LE_TAN). */
    public static java.util.List<PhieuKham> layPhieuHoanTatChuaThanhToan(SessionContext session) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.LE_TAN);
        return PhieuKham_DAO.layPhieuHoanTatChuaThanhToan();
    }

    /** (Báo cáo — UC008) Thống kê lượt khám theo phòng trong khoảng ngày. */
    public static java.util.List<Object[]> thongKeTheoPhong(SessionContext session, LocalDate tu, LocalDate den) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.QUAN_LY);
        return PhieuKham_DAO.thongKeTheoPhong(tu, den);
    }

    /** (Tối ưu UX lễ tân) Số phiếu đang chờ/đang khám HÔM NAY của bác sĩ — gợi ý bác sĩ ít chờ nhất. */
    public static int soPhieuDangChoCuaBacSi(SessionContext session, String maBacSi) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.LE_TAN, ChucVu.PHU_TA, ChucVu.QUAN_LY, ChucVu.BAC_SI);
        return PhieuKham_DAO.layDanhSachTheoBacSi(maBacSi, LocalDate.now()).size();
    }

    /** (Quầy hôm nay — tile SoDoPhong N11) Lượt khám của một phòng trong ngày theo trạng thái. */
    public static java.util.List<PhieuKham> layDanhSachTheoNgayVaPhong(SessionContext session,
            LocalDate ngay, String maPhong, TrangThaiPhieuKham trangThai) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.LE_TAN, ChucVu.PHU_TA, ChucVu.QUAN_LY, ChucVu.BAC_SI);
        return PhieuKham_DAO.layDanhSachTheoNgayVaPhong(ngay, maPhong, trangThai);
    }

    private static void rollback(java.sql.Connection con) {
        try {
            con.rollback();
            con.setAutoCommit(true);
        } catch (java.sql.SQLException ignored) { }
    }

    private PhieuKham_BUS() { }
}
