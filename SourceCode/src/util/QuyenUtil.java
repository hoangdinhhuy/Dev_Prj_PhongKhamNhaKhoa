package util;

import entity.ChucVu;
import entity.SessionContext;

/** Chan quyen o BUS - khong chi ẩn menu o GUI (ghi chu dac ta). */
public final class QuyenUtil {
    private QuyenUtil() { }

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
