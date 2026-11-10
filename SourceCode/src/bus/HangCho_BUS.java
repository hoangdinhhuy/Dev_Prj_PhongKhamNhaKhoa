package bus;

import java.time.LocalDate;
import java.util.List;
import dao.PhieuKham_DAO;
import entity.PhieuKham;
import entity.Quyen;
import entity.SessionContext;
import entity.TrangThaiPhieuKham;
import util.MaThongBao;
import util.QuyenUtil;
import util.ValidateUtil;

public class HangCho_BUS {

    /** UC009 - danh sách CHO_KHAM theo phòng/ngày. Phụ tá chỉ xem phòng mình phụ trách. */
    public static List<PhieuKham> danhSachCho(SessionContext session, LocalDate ngay, String maPhong) {
        QuyenUtil.kiemTraQuyen(session, Quyen.XEM_HANG_CHO);
        if (maPhong == null || maPhong.isBlank()) {
            throw new IllegalArgumentException(MaThongBao.VL07);
        }
        PhongKham_BUS.kiemTraPhuTrachPhong(session, maPhong);
        return PhieuKham_DAO.layDanhSachTheoNgayVaPhong(ngay, maPhong, TrangThaiPhieuKham.CHO_KHAM);
    }

    public static List<PhieuKham> danhSachDangKham(SessionContext session, LocalDate ngay, String maPhong) {
        QuyenUtil.kiemTraQuyen(session, Quyen.XEM_HANG_CHO);
        PhongKham_BUS.kiemTraPhuTrachPhong(session, maPhong);
        return PhieuKham_DAO.layDanhSachTheoNgayVaPhong(ngay, maPhong, TrangThaiPhieuKham.DANG_KHAM);
    }

    /** Gọi số: KHÔNG đổi trạng thái. Chỉ phiếu CHO_KHAM, hôm nay, đúng phòng mình phụ trách. */
    public static PhieuKham goiSo(SessionContext session, String maPhieuKham) {
        QuyenUtil.kiemTraQuyen(session, Quyen.GOI_SO);
        PhieuKham pk = layPhieuHopLe(session, maPhieuKham);
        return pk;
    }

    /** Xác nhận vào khám: CHO_KHAM -> DANG_KHAM bằng UPDATE có điều kiện, 0 dòng = TB10. */
    public static void tiepNhanVaoKham(SessionContext session, String maPhieuKham) {
        QuyenUtil.kiemTraQuyen(session, Quyen.XAC_NHAN_VAO_KHAM);
        ValidateUtil.khongRong(maPhieuKham, MaThongBao.VL05);
        layPhieuHopLe(session, maPhieuKham);
        int n = PhieuKham_DAO.capNhatTrangThai(maPhieuKham, TrangThaiPhieuKham.DANG_KHAM,
                TrangThaiPhieuKham.CHO_KHAM);
        if (n == 0) {
            throw new IllegalArgumentException(MaThongBao.TB10);
        }
    }

    private static PhieuKham layPhieuHopLe(SessionContext session, String maPhieuKham) {
        PhieuKham pk = PhieuKham_DAO.layPhieuKhamTheoMa(maPhieuKham);
        if (pk == null || pk.getTrangThai() != TrangThaiPhieuKham.CHO_KHAM
                || !LocalDate.now().equals(pk.getNgayKham())) {
            throw new IllegalArgumentException(MaThongBao.TB10);
        }
        PhongKham_BUS.kiemTraPhuTrachPhong(session, pk.getMaPhong());
        return pk;
    }

    private HangCho_BUS() { }
}