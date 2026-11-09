package bus;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import connectDB.ConnectDB;
import dao.BoDem_DAO;
import dao.NhanVien_DAO;
import dao.PhieuKham_DAO;
import dao.TaiKhoan_DAO;
import entity.ChucVu;
import entity.NhanVien;
import entity.SessionContext;
import entity.TaiKhoan;
import entity.TrangThaiTaiKhoan;
import util.MaThongBao;
import util.QuyenUtil;
import util.ValidateUtil;

public class NhanVien_BUS {

    public static ArrayList<NhanVien> danhSachNhanVien(SessionContext session) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.QUAN_LY);
        return NhanVien_DAO.getAllNhanVien();
    }

    public static ArrayList<NhanVien> danhSachBacSiDangLamViec(SessionContext session) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.LE_TAN, ChucVu.QUAN_LY, ChucVu.PHU_TA);
        return NhanVien_DAO.layDanhSachBacSiHoatDong();
    }

    public static NhanVien layNhanVienTheoMa(SessionContext session, String maNhanVien) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.LE_TAN, ChucVu.QUAN_LY, ChucVu.PHU_TA);
        return maNhanVien == null ? null : NhanVien_DAO.layNhanVienTheoMa(maNhanVien);
    }

    public static NhanVien themNhanVien(SessionContext session, String hoTen, String soDienThoai,
                                        LocalDate ngaySinh, Boolean gioiTinh, String cccd, ChucVu chucVu) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.QUAN_LY);
        String ten = ValidateUtil.khongRong(hoTen, MaThongBao.VL01);
        String sdt = ValidateUtil.sdt(soDienThoai);
        ValidateUtil.nhanVienDu18Tuoi(ngaySinh);
        String cmnd = cccd == null || cccd.isBlank() ? null : ValidateUtil.cccd(cccd);

        Connection con = ConnectDB.getInstance().getConnection();
        try {
            con.setAutoCommit(false);
            String maNV;
            do { maNV = BoDem_DAO.layMaMoi(con, "NV", LocalDate.now()); }
            while (NhanVien_DAO.tonTaiMa(con, maNV));
            NhanVien nv = new NhanVien(maNV, ten, sdt, ngaySinh, gioiTinh, cmnd, chucVu, true);
            NhanVien_DAO.themNhanVien(con, nv);
            con.commit();
            con.setAutoCommit(true);
            return nv;
        } catch (SQLException ex) {
            rollback(con);
            throw new RuntimeException("Lỗi thêm nhân viên: " + ex.getMessage(), ex);
        }
    }

    public static void suaNhanVien(SessionContext session, NhanVien nv) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.QUAN_LY);
        ValidateUtil.khongRong(nv.getHoTen(), MaThongBao.VL01);
        ValidateUtil.sdt(nv.getSoDienThoai());
        ValidateUtil.nhanVienDu18Tuoi(nv.getNgaySinh());
        if (nv.getCccd() != null && !nv.getCccd().isBlank()) {
            nv.setCccd(ValidateUtil.cccd(nv.getCccd()));
        }
        Connection con = ConnectDB.getInstance().getConnection();
        try {
            NhanVien_DAO.suaNhanVien(con, nv);
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi sửa nhân viên: " + ex.getMessage(), ex);
        }
    }

    public static void xoaNhanVien(SessionContext session, String maNhanVien) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.QUAN_LY);
        if (TaiKhoan_DAO.layTaiKhoanTheoMaNhanVien(maNhanVien) != null
                || PhieuKham_DAO.demTheoBacSi(maNhanVien) > 0) {
            throw new IllegalArgumentException(MaThongBao.TB11);
        }
        Connection con = ConnectDB.getInstance().getConnection();
        try {
            NhanVien_DAO.xoaNhanVien(con, maNhanVien);
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi xóa nhân viên: " + ex.getMessage(), ex);
        }
    }

    public static void taoTaiKhoan(SessionContext session, String maNhanVien, String tenDangNhap, String matKhau) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.QUAN_LY);
        String ten = ValidateUtil.khongRong(tenDangNhap, MaThongBao.VL13);
        String mk = ValidateUtil.khongRong(matKhau, MaThongBao.VL13);
        if (TaiKhoan_DAO.kiemTraTenDangNhapTonTai(ten)) {
            throw new IllegalArgumentException(MaThongBao.TB14);
        }
        TaiKhoan tk = new TaiKhoan(ten, TaiKhoan_BUS.sha256(mk), TrangThaiTaiKhoan.HOAT_DONG, maNhanVien);
        Connection con = ConnectDB.getInstance().getConnection();
        try {
            TaiKhoan_DAO.themTaiKhoan(con, tk);
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi tạo tài khoản: " + ex.getMessage(), ex);
        }
    }

    public static void khoaMoTaiKhoan(SessionContext session, String tenDangNhap) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.QUAN_LY);
        TaiKhoan tk = TaiKhoan_DAO.getTaiKhoanTheoTenDangNhap(tenDangNhap);
        if (tk == null) {
            throw new IllegalArgumentException(MaThongBao.TB09);
        }
        tk.setTrangThai(tk.getTrangThai() == TrangThaiTaiKhoan.HOAT_DONG
                ? TrangThaiTaiKhoan.BI_KHOA : TrangThaiTaiKhoan.HOAT_DONG);
        Connection con = ConnectDB.getInstance().getConnection();
        try {
            TaiKhoan_DAO.capNhatTaiKhoan(con, tk);
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi cập nhật tài khoản: " + ex.getMessage(), ex);
        }
    }

    private static void rollback(Connection con) {
        try {
            con.rollback();
            con.setAutoCommit(true);
        } catch (SQLException ignored) { }
    }

    private NhanVien_BUS() { }
}
