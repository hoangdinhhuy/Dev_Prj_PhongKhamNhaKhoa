package util;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import entity.ChucVu;
import entity.Quyen;
import entity.SessionContext;

/**
 * Phân quyền tập trung: vai trò (ChucVu) -> tập quyền (Quyen).
 * Menu và BUS cùng hỏi ma trận này nên không bị lệch nhau.
 * Chặn ở BUS, không chỉ ẩn menu ở GUI.
 */
public final class QuyenUtil {
    private static final Map<ChucVu, Set<Quyen>> MA_TRAN = new EnumMap<>(ChucVu.class);

    static {
        MA_TRAN.put(ChucVu.LE_TAN, EnumSet.of(
                Quyen.XEM_TONG_QUAN, Quyen.QL_HO_SO_BENH_NHAN, Quyen.TRA_CUU_BENH_NHAN,
                Quyen.THU_TIEN, Quyen.LAP_PHIEU_KHAM, Quyen.QL_CA_THU_NGAN,
                Quyen.XEM_DANH_SACH_PHONG));
        MA_TRAN.put(ChucVu.BAC_SI, EnumSet.of(
                Quyen.XEM_TONG_QUAN, Quyen.KHAM_BENH, Quyen.XEM_LICH_SU_KHAM));
        MA_TRAN.put(ChucVu.PHU_TA, EnumSet.of(
                Quyen.XEM_TONG_QUAN, Quyen.XEM_HANG_CHO, Quyen.GOI_SO, Quyen.XAC_NHAN_VAO_KHAM,
                Quyen.XEM_DANH_SACH_PHONG, Quyen.BAT_TAT_NHAN_BENH));
        MA_TRAN.put(ChucVu.QUAN_LY, EnumSet.of(
                Quyen.XEM_TONG_QUAN, Quyen.QL_HANG_HOA, Quyen.QL_NHAN_VIEN, Quyen.QL_DICH_VU,
                Quyen.XEM_BAO_CAO, Quyen.XEM_DANH_SACH_PHONG, Quyen.BAT_TAT_NHAN_BENH));
    }

    private QuyenUtil() { }

    public static boolean coQuyen(SessionContext session, Quyen quyen) {
        if (session == null || session.getChucVu() == null) {
            return false;
        }
        Set<Quyen> ds = MA_TRAN.get(session.getChucVu());
        return ds != null && ds.contains(quyen);
    }

    /** Cách mới: kiểm theo quyền. Có ít nhất một quyền trong danh sách là được. */
    public static void kiemTraQuyen(SessionContext session, Quyen... choPhep) {
        for (Quyen q : choPhep) {
            if (coQuyen(session, q)) {
                return;
            }
        }
        throw new SecurityException(MaThongBao.TB08);
    }

    /** Cách cũ (giữ lại để các BUS khác chưa đổi vẫn chạy). */
    public static void kiemTraQuyen(SessionContext session, ChucVu... choPhep) {
        if (session == null || session.getChucVu() == null) {
            throw new SecurityException(MaThongBao.TB08);
        }
        for (ChucVu cv : choPhep) {
            if (cv == session.getChucVu()) {
                return;
            }
        }
        throw new SecurityException(MaThongBao.TB08);
    }
}