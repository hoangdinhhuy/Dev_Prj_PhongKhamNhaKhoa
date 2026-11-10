package bus;

import java.time.LocalDate;
import java.util.ArrayList;
import dao.PhieuKham_DAO;
import dao.PhongKham_DAO;
import entity.ChucVu;
import entity.PhongKham;
import entity.Quyen;
import entity.SessionContext;
import entity.TrangThaiPhieuKham;
import util.MaThongBao;
import util.QuyenUtil;

public class PhongKham_BUS {

    /** Giữ nguyên: lễ tân dùng để chọn phòng khi lập phiếu. */
    public static ArrayList<PhongKham> danhSachPhongDangNhanBenh(SessionContext session) {
        return PhongKham_DAO.layPhongDangNhanBenh();
    }

    public static ArrayList<PhongKham> danhSachTatCaPhong(SessionContext session) {
        QuyenUtil.kiemTraQuyen(session, Quyen.XEM_DANH_SACH_PHONG);
        return PhongKham_DAO.getAllPhongKham();
    }

    /** Phòng đang nhận bệnh mà người dùng phụ trách (phụ tá: phòng của mình; quản lý: tất cả). */
    public static ArrayList<PhongKham> danhSachPhongPhuTrachDangNhan(SessionContext session) {
        QuyenUtil.kiemTraQuyen(session, Quyen.XEM_HANG_CHO);
        ArrayList<PhongKham> kq = new ArrayList<>();
        for (PhongKham p : PhongKham_DAO.layPhongDangNhanBenh()) {
            if (phuTrach(session, p)) {
                kq.add(p);
            }
        }
        return kq;
    }

    /** Phòng được bật/tắt nhận bệnh (cả phòng đang tắt) mà người dùng phụ trách. */
    public static ArrayList<PhongKham> danhSachPhongBatTat(SessionContext session) {
        QuyenUtil.kiemTraQuyen(session, Quyen.BAT_TAT_NHAN_BENH);
        ArrayList<PhongKham> kq = new ArrayList<>();
        for (PhongKham p : PhongKham_DAO.getAllPhongKham()) {
            if (phuTrach(session, p)) {
                kq.add(p);
            }
        }
        return kq;
    }

    public static void capNhatNhanBenh(SessionContext session, String maPhong, boolean dangNhanBenh) {
        QuyenUtil.kiemTraQuyen(session, Quyen.BAT_TAT_NHAN_BENH);
        kiemTraPhuTrachPhong(session, maPhong);
        PhongKham_DAO.capNhatNhanBenh(maPhong, dangNhanBenh);
    }

    /** Số phiếu đang chờ khám hôm nay của phòng (để cảnh báo trước khi tắt nhận bệnh). */
    public static int demPhieuDangCho(SessionContext session, String maPhong) {
        QuyenUtil.kiemTraQuyen(session, Quyen.BAT_TAT_NHAN_BENH);
        kiemTraPhuTrachPhong(session, maPhong);
        return PhieuKham_DAO.layDanhSachTheoNgayVaPhong(LocalDate.now(), maPhong,
                TrangThaiPhieuKham.CHO_KHAM).size();
    }

    /** Quản lý: mọi phòng. Phụ tá: chỉ phòng có maPhuTa trùng mã nhân viên đăng nhập. */
    public static void kiemTraPhuTrachPhong(SessionContext session, String maPhong) {
        if (session == null || session.getChucVu() == null) {
            throw new SecurityException(MaThongBao.TB08);
        }
        if (session.getChucVu() == ChucVu.QUAN_LY) {
            return;
        }
        PhongKham p = maPhong == null ? null : PhongKham_DAO.layPhongTheoMa(maPhong);
        if (p == null || !phuTrach(session, p)) {
            throw new SecurityException(MaThongBao.TB08);
        }
    }

    // private static boolean phuTrach(SessionContext session, PhongKham p) {
    //     if (session.getChucVu() == ChucVu.QUAN_LY) {
    //         return true;
    //     }
    //     return session.getMaNhanVien() != null && session.getMaNhanVien().equals(p.getMaPhuTa());
    // }
       private static boolean phuTrach(SessionContext session, PhongKham p) {
    return session.getChucVu() == ChucVu.QUAN_LY || session.getChucVu() == ChucVu.PHU_TA;
}

    private PhongKham_BUS() { }
}