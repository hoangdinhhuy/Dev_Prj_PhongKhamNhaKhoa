package bus;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import connectDB.ConnectDB;
import dao.BoDem_DAO;
import dao.CaThuNgan_DAO;
import dao.HoaDon_DAO;
import entity.CaThuNgan;
import entity.ChucVu;
import entity.HoaDon;
import entity.SessionContext;
import util.QuyenUtil;

/**
 * Quản lý ca thu ngân (mẫu POS): Mở ca → thu trong ca → CHỐT CA (snapshot + xác nhận)
 * → nhân viên sau Nhận ca. Quy trình bắt buộc để bàn giao tiền đúng nghiệp vụ.
 */
public class CaThuNgan_BUS {

    /** Ca đang mở của quầy (null nếu chưa mở — thu ngân sau phải Nhận ca). */
    public static CaThuNgan layCaDangMo(SessionContext session) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.LE_TAN, ChucVu.QUAN_LY);
        return CaThuNgan_DAO.layCaDangMo();
    }

    /** NHẬN CA: mở ca mới (chỉ khi chưa có ca nào đang mở). */
    public static CaThuNgan moCa(SessionContext session) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.LE_TAN, ChucVu.QUAN_LY);
        if (CaThuNgan_DAO.layCaDangMo() != null) {
            throw new IllegalStateException("Còn ca đang mở — hãy chốt ca trước khi nhận ca mới.");
        }
        Connection con = ConnectDB.getInstance().getConnection();
        try {
            con.setAutoCommit(false);
            String maCa;
            do {
                maCa = BoDem_DAO.layMaMoi(con, "CT", LocalDateTime.now().toLocalDate());
            } while (CaThuNgan_DAO.tonTaiMa(con, maCa));
            CaThuNgan ca = new CaThuNgan(maCa);
            ca.setMaNhanVien(session.getMaNhanVien());
            ca.setThoiGianMo(LocalDateTime.now());
            ca.setTrangThai(CaThuNgan.DANG_MO);
            CaThuNgan_DAO.themCa(con, ca);
            con.commit();
            con.setAutoCommit(true);
            return ca;
        } catch (Exception ex) {
            try { con.rollback(); } catch (Exception ignored) { }
            try { con.setAutoCommit(true); } catch (Exception ignored) { }
            throw new RuntimeException("Lỗi mở ca: " + ex.getMessage(), ex);
        }
    }

    /** Tính 2 khoản tiền của ca ĐANG MO: doanh thu dịch vụ + phí khám thu hộ (hóa đơn thu từ lúc mở ca). */
    private static BigDecimal[] tinhTienCa(CaThuNgan ca) {
        BigDecimal ca_tien = BigDecimal.ZERO;
        BigDecimal ho = BigDecimal.ZERO;
        for (HoaDon hd : HoaDon_BUS.layHoaDonDaThuHomNay(null)) {
            if (hd.getNgayThu() == null || hd.getTongTien() == null) continue;
            if (hd.getNgayThu().isBefore(ca.getThoiGianMo())) continue;   // thuộc ca trước
            if (hd.getMaPhieuKham() != null) ca_tien = ca_tien.add(hd.getTongTien());
            else ho = ho.add(hd.getTongTien());
        }
        return new BigDecimal[]{ca_tien, ho};
    }

    /** Doanh thu dịch vụ đã thu TRONG ca đang mở (hiển thị trực tiếp cho thu ngân). */
    public static BigDecimal doanhThuTrongCa(SessionContext session, CaThuNgan ca) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.LE_TAN, ChucVu.QUAN_LY);
        return tinhTienCa(ca)[0];
    }

    /** Phí khám thu hộ TRONG ca đang mở. */
    public static BigDecimal phiThuHoTrongCa(SessionContext session, CaThuNgan ca) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.LE_TAN, ChucVu.QUAN_LY);
        return tinhTienCa(ca)[1];
    }

    /** CHỐT CA: snapshot doanh thu + thời gian chốt. Sau lệnh này nhân viên sau mới Nhận ca được. */
    public static CaThuNgan chotCa(SessionContext session, CaThuNgan ca) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.LE_TAN, ChucVu.QUAN_LY);
        if (ca == null || !ca.isDangMo()) {
            throw new IllegalStateException("Không có ca đang mở để chốt.");
        }
        BigDecimal[] tien = tinhTienCa(ca);
        Connection con = ConnectDB.getInstance().getConnection();
        try {
            con.setAutoCommit(false);
            LocalDateTime chot = LocalDateTime.now();
            int n = CaThuNgan_DAO.chotCa(con, ca.getMaCa(), java.sql.Timestamp.valueOf(chot), tien[0], tien[1]);
            if (n == 0) {
                throw new IllegalStateException("Ca đã được chốt bởi phiên khác.");
            }
            ca.setThoiGianChot(chot);
            ca.setDoanhThuDichVu(tien[0]);
            ca.setPhiThuHo(tien[1]);
            ca.setTrangThai(CaThuNgan.DA_CHOT);
            con.commit();
            con.setAutoCommit(true);
            return ca;
        } catch (IllegalStateException ex) {
            try { con.rollback(); } catch (Exception ignored) { }
            try { con.setAutoCommit(true); } catch (Exception ignored) { }
            throw ex;
        } catch (Exception ex) {
            try { con.rollback(); } catch (Exception ignored) { }
            try { con.setAutoCommit(true); } catch (Exception ignored) { }
            throw new RuntimeException("Lỗi chốt ca: " + ex.getMessage(), ex);
        }
    }

    /** Lịch sử ca đã chốt (màn Chốt ca). */
    public static List<CaThuNgan> lichSuCa(SessionContext session, int gioiHan) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.LE_TAN, ChucVu.QUAN_LY);
        return CaThuNgan_DAO.layLichSu(gioiHan);
    }

    private CaThuNgan_BUS() { }
}
