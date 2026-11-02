package bus;

import java.time.LocalDate;
import java.util.ArrayList;
import dao.BaoCao_DAO;
import entity.ChucVu;
import entity.SessionContext;
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

    private BaoCao_BUS() { }
}
