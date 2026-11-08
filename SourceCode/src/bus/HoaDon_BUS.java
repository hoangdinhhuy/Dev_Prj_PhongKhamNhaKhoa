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
            String maHD = maHoaDonMoi(con);
            HoaDon hd = new HoaDon(maHD);
            hd.setNgayThu(LocalDateTime.now());
            hd.setHinhThucThanhToan(hinhThucThanhToan);
            hd.setDaThanhToan(true);
            hd.setTienKhachDua(tienDua);
            hd.setMaBenhNhan(maBN);
            hd.setMaNhanVien(session.getMaNhanVien());
            ChiTietHoaDon cdPhi = new ChiTietHoaDon(maHD, MA_DICH_VU_PHI_KHAM, 1, phiKham.getDonGiaNiemYet());
            hd.themChiTiet(cdPhi);          // entity TU TINH tongTien — khong co setter
            HoaDon_DAO.themHoaDon(con, hd);
            ChiTietHoaDon_DAO.themChiTiet(con, cdPhi);
            con.commit();
            con.setAutoCommit(true);
            return hd;
        } catch (SQLException ex) {
            rollback(con);
            throw new RuntimeException("Lỗi thu phí khám: " + ex.getMessage(), ex);
        }
    }

    public static HoaDon layHoaDonPhiChuaDung(SessionContext session, String maBenhNhan) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.LE_TAN);
        return HoaDon_DAO.layHoaDonPhiChuaDungTheoBN(maBenhNhan);
    }

    public static List<ChiDinhDichVu> layChiDinhChoThanhToan(SessionContext session, String maPhieuKham) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.LE_TAN);
        return ChiDinhDichVu_DAO.layTheoPhieu(maPhieuKham).stream()
                .filter(cd -> cd.getDonGia() != null && cd.getDonGia().signum() > 0)
                .toList();
    }

    public static List<ChiTietHoaDon> layChiTietHoaDon(SessionContext session, String maHoaDon) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.LE_TAN);
        HoaDon hd = HoaDon_DAO.layHoaDonTheoMa(maHoaDon);
        if (hd == null || !hd.isDaThanhToan()) {
            throw new IllegalArgumentException(MaThongBao.TB17);
        }
        return ChiTietHoaDon_DAO.layChiTietTheoHoaDon(maHoaDon);
    }

    /** LT-06 — Thanh toan dich vu cuoi luot: hoa don gom tat ca dich vu da chi dinh cua phieu HOAN_TAT. */
    public static HoaDon thanhToanDichVu(SessionContext session, String maPhieuKham,
                                         String hinhThucThanhToan, String tienKhachDua) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.LE_TAN);
        PhieuKham pk = PhieuKham_DAO.layPhieuKhamTheoMa(maPhieuKham);
        if (pk == null || pk.getTrangThai() != TrangThaiPhieuKham.HOAN_TAT) {
            throw new IllegalArgumentException(MaThongBao.TB10);
        }
        List<ChiDinhDichVu> chiDinh = ChiDinhDichVu_DAO.layTheoPhieu(maPhieuKham).stream()
                .filter(cd -> cd.getDonGia() != null && cd.getDonGia().signum() > 0)
                .toList();
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
            String maHD = maHoaDonMoi(con);
            HoaDon hd = new HoaDon(maHD);
            hd.setNgayThu(LocalDateTime.now());
            hd.setHinhThucThanhToan(hinhThucThanhToan);
            hd.setDaThanhToan(true);
            hd.setTienKhachDua(tienDua);
            hd.setBenhNhan(pk.getBenhNhan());   // dung doi tuong tu phieu kham (P2-1)
            hd.setMaNhanVien(session.getMaNhanVien());
            hd.setMaPhieuKham(maPhieuKham);
            HoaDon_DAO.themHoaDon(con, hd);
            for (ChiDinhDichVu cdd : chiDinh) {
                ChiTietHoaDon ctd = new ChiTietHoaDon(maHD, cdd.getMaDichVu(), 1, cdd.getDonGia());
                hd.themChiTiet(ctd);    // dan xuat tu tinh
                ChiTietHoaDon_DAO.themChiTiet(con, ctd);
            }
            con.commit();
            con.setAutoCommit(true);
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

    private static String maHoaDonMoi(Connection con) throws SQLException {
        String maHD;
        do {
            maHD = BoDem_DAO.layMaMoi(con, "HD", LocalDateTime.now().toLocalDate());
        } while (HoaDon_DAO.tonTaiMa(con, maHD));
        return maHD;
    }

    /** (Chốt ca thu ngân) Bảng kê hóa đơn đã thu trong ngày — lễ tân kiêm thu ngân kiểm kê. */
    public static java.util.List<HoaDon> layHoaDonDaThuHomNay(SessionContext session) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.LE_TAN, ChucVu.QUAN_LY);
        return HoaDon_DAO.layHoaDonDaThuHomNay(java.time.LocalDate.now());
    }

    /** (Chốt ca — đúng nghiệp vụ thu ngân) DOANH THU CA = tiền thu ở DỊCH VỤ cuối lượt
     *  (hóa đơn gắn phiếu khám). Phí khám đầu vào là tiền thu hộ, KHÔNG tính vào doanh thu ca. */
    public static java.math.BigDecimal doanhThuCaHomNay(SessionContext session) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.LE_TAN, ChucVu.QUAN_LY);
        java.math.BigDecimal ca = java.math.BigDecimal.ZERO;
        for (HoaDon hd : layHoaDonDaThuHomNay(session)) {
            if (hd.getMaPhieuKham() != null && hd.getTongTien() != null) {
                ca = ca.add(hd.getTongTien());
            }
        }
        return ca;
    }

    /** (Chốt ca) Phí khám đầu vào đã thu hộ trong ngày — liệt kê, không tính vào doanh thu ca. */
    public static java.math.BigDecimal phiThuHoHomNay(SessionContext session) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.LE_TAN, ChucVu.QUAN_LY);
        java.math.BigDecimal ho = java.math.BigDecimal.ZERO;
        for (HoaDon hd : layHoaDonDaThuHomNay(session)) {
            if (hd.getMaPhieuKham() == null && hd.getTongTien() != null) {
                ho = ho.add(hd.getTongTien());
            }
        }
        return ho;
    }

    private HoaDon_BUS() { }
}
