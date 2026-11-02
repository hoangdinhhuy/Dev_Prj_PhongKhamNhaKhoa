package bus;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import connectDB.ConnectDB;
import dao.BoDem_DAO;
import dao.DichVu_DAO;
import entity.ChucVu;
import entity.DichVu;
import entity.SessionContext;
import entity.TrangThaiDichVu;
import util.MaThongBao;
import util.QuyenUtil;
import util.ValidateUtil;

public class DichVu_BUS {

    public static ArrayList<DichVu> danhSachDichVuDangPhucVu(SessionContext session) {
        ArrayList<DichVu> ds = new ArrayList<>();
        for (DichVu dv : DichVu_DAO.getAllDichVu()) {
            if (dv.getTrangThai() == TrangThaiDichVu.DANG_PHUC_VU) {
                ds.add(dv);
            }
        }
        return ds;
    }

    public static ArrayList<DichVu> danhSachDichVu(SessionContext session) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.QUAN_LY);
        return DichVu_DAO.getAllDichVu();
    }

    public static DichVu layDichVuTheoMa(String maDichVu) {
        return DichVu_DAO.layDichVuTheoMa(maDichVu);
    }

    public static DichVu themDichVu(SessionContext session, String tenDichVu, String donGia) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.QUAN_LY);
        String ten = ValidateUtil.khongRong(tenDichVu, MaThongBao.VL01);
        BigDecimal gia = ValidateUtil.donGia(donGia);
        if (DichVu_DAO.kiemTraTenTonTai(null, ten)) {
            throw new IllegalArgumentException(MaThongBao.TB12);
        }
        Connection con = ConnectDB.getInstance().getConnection();
        try {
            con.setAutoCommit(false);
            String maDV = BoDem_DAO.layMaMoi(con, "DV", LocalDate.now());
            DichVu dv = new DichVu(maDV, ten, gia, TrangThaiDichVu.DANG_PHUC_VU);
            DichVu_DAO.themDichVu(con, dv);
            con.commit();
            con.setAutoCommit(true);
            return dv;
        } catch (SQLException ex) {
            rollback(con);
            throw new RuntimeException("Lỗi thêm dịch vụ: " + ex.getMessage(), ex);
        }
    }

    public static void suaDichVu(SessionContext session, DichVu dv, String tenMoi, String giaMoi) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.QUAN_LY);
        String ten = ValidateUtil.khongRong(tenMoi, MaThongBao.VL01);
        BigDecimal gia = ValidateUtil.donGia(giaMoi);
        if (DichVu_DAO.kiemTraTenTonTai(dv.getMaDichVu(), ten)) {
            throw new IllegalArgumentException(MaThongBao.TB12);
        }
        dv.setTenDichVu(ten);
        dv.setDonGiaNiemYet(gia);
        Connection con = ConnectDB.getInstance().getConnection();
        try {
            DichVu_DAO.suaDichVu(con, dv);
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi sửa dịch vụ: " + ex.getMessage(), ex);
        }
    }

    public static void doiTrangThai(SessionContext session, DichVu dv, TrangThaiDichVu trangThaiMoi) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.QUAN_LY);
        dv.setTrangThai(trangThaiMoi);
        Connection con = ConnectDB.getInstance().getConnection();
        try {
            DichVu_DAO.suaDichVu(con, dv);
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi đổi trạng thái dịch vụ: " + ex.getMessage(), ex);
        }
    }

    private static void rollback(Connection con) {
        try {
            con.rollback();
            con.setAutoCommit(true);
        } catch (SQLException ignored) { }
    }

    private DichVu_BUS() { }
}
