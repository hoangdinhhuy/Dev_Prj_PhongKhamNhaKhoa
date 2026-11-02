package bus;

import java.time.LocalDate;
import java.util.List;
import dao.PhieuKham_DAO;
import entity.ChucVu;
import entity.PhieuKham;
import entity.SessionContext;
import entity.TrangThaiPhieuKham;
import util.MaThongBao;
import util.QuyenUtil;
import util.ValidateUtil;

public class HangCho_BUS {

    /** UC009 - danh sach CHO_KHAM theo phong/ngay (rong -> GUI bao TB07). */
    public static List<PhieuKham> danhSachCho(SessionContext session, LocalDate ngay, String maPhong) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.PHU_TA);
        if (maPhong == null || maPhong.isBlank()) {
            throw new IllegalArgumentException(MaThongBao.VL07);
        }
        return PhieuKham_DAO.layDanhSachTheoNgayVaPhong(ngay, maPhong, TrangThaiPhieuKham.CHO_KHAM);
    }

    public static List<PhieuKham> danhSachDangKham(SessionContext session, LocalDate ngay, String maPhong) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.PHU_TA);
        return PhieuKham_DAO.layDanhSachTheoNgayVaPhong(ngay, maPhong, TrangThaiPhieuKham.DANG_KHAM);
    }

    /** Goi so: KHONG doi trang thai phieu (goi != vao kham). Chi phi CHO_KHAM duoc goi. */
    public static PhieuKham goiSo(SessionContext session, String maPhieuKham) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.PHU_TA);
        PhieuKham pk = PhieuKham_DAO.layPhieuKhamTheoMa(maPhieuKham);
        if (pk == null || pk.getTrangThai() != TrangThaiPhieuKham.CHO_KHAM) {
            throw new IllegalArgumentException(MaThongBao.TB10);
        }
        return pk;
    }

    /** Xac nhan vao kham: CHO_KHAM -> DANG_KHAM bang UPDATE co dieu kien, 0 dong = TB10. */
    public static void tiepNhanVaoKham(SessionContext session, String maPhieuKham) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.PHU_TA);
        ValidateUtil.khongRong(maPhieuKham, MaThongBao.VL05);
        int n = PhieuKham_DAO.capNhatTrangThai(maPhieuKham, TrangThaiPhieuKham.DANG_KHAM,
                TrangThaiPhieuKham.CHO_KHAM);
        if (n == 0) {
            throw new IllegalArgumentException(MaThongBao.TB10);
        }
    }

    private HangCho_BUS() { }
}
