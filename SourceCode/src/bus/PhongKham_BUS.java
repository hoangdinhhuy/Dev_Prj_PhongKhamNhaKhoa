package bus;

import java.util.ArrayList;
import dao.PhongKham_DAO;
import entity.ChucVu;
import entity.PhongKham;
import entity.SessionContext;
import util.QuyenUtil;

public class PhongKham_BUS {

    public static ArrayList<PhongKham> danhSachPhongDangNhanBenh(SessionContext session) {
        return PhongKham_DAO.layPhongDangNhanBenh();
    }

    public static ArrayList<PhongKham> danhSachTatCaPhong(SessionContext session) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.PHU_TA, ChucVu.QUAN_LY, ChucVu.LE_TAN);
        return PhongKham_DAO.getAllPhongKham();
    }

    public static void capNhatNhanBenh(SessionContext session, String maPhong, boolean dangNhanBenh) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.PHU_TA, ChucVu.QUAN_LY);
        PhongKham_DAO.capNhatNhanBenh(maPhong, dangNhanBenh);
    }

    private PhongKham_BUS() { }
}
