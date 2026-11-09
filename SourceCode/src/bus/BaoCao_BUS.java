package bus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import dao.BaoCao_DAO;
import dao.DichVu_DAO;
import dao.HangHoa_DAO;
import dao.NhanVien_DAO;
import entity.ChucVu;
import entity.DichVu;
import entity.HangHoa;
import entity.NhanVien;
import entity.SessionContext;
import entity.TrangThaiDichVu;
import util.QuyenUtil;
import util.ValidateUtil;

public class BaoCao_BUS {

    /** UC008 - thong ke luot kham theo ngay (SQL GROUP BY). */
    public static ArrayList<Object[]> thongKeLuotKham(SessionContext session, LocalDate tuNgay, LocalDate denNgay) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.QUAN_LY);
        ValidateUtil.khoangThoiGian(tuNgay, denNgay);
        return BaoCao_DAO.thongKeLuotKham(tuNgay, denNgay);
    }

    /** UC008 - tan suat dich vu duoc chi dinh. */
    public static ArrayList<Object[]> thongKeTanSuatDichVu(SessionContext session, LocalDate tuNgay,
                                                           LocalDate denNgay) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.QUAN_LY);
        ValidateUtil.khoangThoiGian(tuNgay, denNgay);
        return BaoCao_DAO.thongKeTanSuatDichVu(tuNgay, denNgay);
    }

    /**
     * Dữ liệu tổng hợp KPI cho Dashboard Quản lý (đóng gói trực tiếp trong BUS, không cần package dto).
     */
    public static class ThongKeTongQuan {
        public final int tongNhanVienDangLamViec;
        public final Map<String, Integer> coCauNhanSu;
        public final int tongDichVuDangPhucVu;
        public final int soHangHoaCanBoSung;
        public final int tongLuotKham7Ngay;
        public final List<Object[]> luotKham7Ngay;
        public final List<HangHoa> danhSachCanBoSung;

        public ThongKeTongQuan(int tongNhanVienDangLamViec, Map<String, Integer> coCauNhanSu,
                               int tongDichVuDangPhucVu, int soHangHoaCanBoSung,
                               int tongLuotKham7Ngay, List<Object[]> luotKham7Ngay,
                               List<HangHoa> danhSachCanBoSung) {
            this.tongNhanVienDangLamViec = tongNhanVienDangLamViec;
            this.coCauNhanSu = coCauNhanSu;
            this.tongDichVuDangPhucVu = tongDichVuDangPhucVu;
            this.soHangHoaCanBoSung = soHangHoaCanBoSung;
            this.tongLuotKham7Ngay = tongLuotKham7Ngay;
            this.luotKham7Ngay = luotKham7Ngay;
            this.danhSachCanBoSung = danhSachCanBoSung;
        }
    }

    public static ThongKeTongQuan layTongQuanKpi(SessionContext session) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.QUAN_LY, ChucVu.BAC_SI, ChucVu.LE_TAN, ChucVu.PHU_TA);

        // 1. Thống kê nhân viên đang làm việc & cơ cấu
        ArrayList<NhanVien> dsNV = NhanVien_DAO.getAllNhanVien();
        int tongNVDangLam = 0;
        Map<String, Integer> coCau = new HashMap<>();
        coCau.put("Bác sĩ", 0);
        coCau.put("Lễ tân", 0);
        coCau.put("Phụ tá", 0);
        coCau.put("Quản lý", 0);

        for (NhanVien nv : dsNV) {
            if (nv.isConLamViec()) {
                tongNVDangLam++;
                if (nv.getChucVu() != null) {
                    String tenCV = nv.getChucVu().toString();
                    coCau.put(tenCV, coCau.getOrDefault(tenCV, 0) + 1);
                }
            }
        }

        // 2. Thống kê dịch vụ đang phục vụ
        ArrayList<DichVu> dsDV = DichVu_DAO.getAllDichVu();
        int tongDVDangPhucVu = 0;
        for (DichVu dv : dsDV) {
            if (dv.getTrangThai() == TrangThaiDichVu.DANG_PHUC_VU) {
                tongDVDangPhucVu++;
            }
        }

        // 3. Hàng hóa cần bổ sung
        ArrayList<HangHoa> dsHH = HangHoa_DAO.getAllHangHoa();
        List<HangHoa> hhCanBoSung = new ArrayList<>();
        for (HangHoa h : dsHH) {
            if (h.isCanBoSung()) {
                hhCanBoSung.add(h);
            }
        }

        // 4. Lượt khám 7 ngày gần nhất
        LocalDate den = LocalDate.now();
        LocalDate tu = den.minusDays(6);
        ArrayList<Object[]> rawLK = BaoCao_DAO.thongKeLuotKham(tu, den);
        int tongLK = 0;
        for (Object[] r : rawLK) {
            tongLK += ((Number) r[1]).intValue();
        }

        return new ThongKeTongQuan(
                tongNVDangLam, coCau, tongDVDangPhucVu,
                hhCanBoSung.size(), tongLK, rawLK, hhCanBoSung
        );
    }

    private BaoCao_BUS() { }
}
