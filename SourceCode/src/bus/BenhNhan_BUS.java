package bus;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import connectDB.ConnectDB;
import dao.BenhNhan_DAO;
import dao.BoDem_DAO;
import entity.BenhNhan;
import entity.ChucVu;
import entity.SessionContext;
import util.MaThongBao;
import util.QuyenUtil;
import util.ValidateUtil;

public class BenhNhan_BUS {

    public static BenhNhan taoHoSo(SessionContext session, String hoTen, LocalDate ngaySinh,
                                   String soDienThoai, String cccd, String diaChi, String tienSuDiUng) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.LE_TAN);
        String ten = ValidateUtil.khongRong(hoTen, MaThongBao.VL01);
        String sdt = ValidateUtil.sdt(soDienThoai);
        ValidateUtil.ngaySinh(ngaySinh);
        String cmnd = ValidateUtil.cccd(cccd);
        if (cmnd != null && BenhNhan_DAO.layBenhNhanTheoCCCD(cmnd) != null) {
            throw new IllegalArgumentException(MaThongBao.TB13);
        }

        Connection con = ConnectDB.getInstance().getConnection();
        try {
            con.setAutoCommit(false);
            String maBN = BoDem_DAO.layMaMoi(con, "BN", LocalDate.now());
            BenhNhan bn = new BenhNhan(maBN, ten, sdt, cmnd, ngaySinh, emptyThanNull(diaChi),
                    emptyThanNull(tienSuDiUng));
            BenhNhan_DAO.themBenhNhan(con, bn);
            con.commit();
            con.setAutoCommit(true);
            return bn;
        } catch (SQLException ex) {
            rollback(con);
            throw new RuntimeException("Lỗi tạo hồ sơ bệnh nhân: " + ex.getMessage(), ex);
        }
    }

    public static void capNhatHoSo(SessionContext session, BenhNhan bn) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.LE_TAN);
        ValidateUtil.khongRong(bn.getHoTen(), MaThongBao.VL01);
        ValidateUtil.sdt(bn.getSoDienThoai());
        ValidateUtil.ngaySinh(bn.getNgaySinh());
        String cmnd = ValidateUtil.cccd(bn.getCccd());
        if (cmnd != null) {
            BenhNhan khac = BenhNhan_DAO.layBenhNhanTheoCCCD(cmnd);
            if (khac != null && !khac.getMaBenhNhan().equals(bn.getMaBenhNhan())) {
                throw new IllegalArgumentException(MaThongBao.TB13);
            }
            bn.setCccd(cmnd);
        }
        Connection con = ConnectDB.getInstance().getConnection();
        try {
            BenhNhan_DAO.suaBenhNhan(con, bn);
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi cập nhật hồ sơ: " + ex.getMessage(), ex);
        }
    }

    /** UC006 - tra cuu theo tieu chi: "Mã" | "SĐT" | "Họ tên". Tra ve danh sach (rong = khong thay). */
    public static ArrayList<BenhNhan> traCuu(SessionContext session, String tieuChi, String tuKhoa) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.LE_TAN);
        ArrayList<BenhNhan> ds = new ArrayList<>();
        String khoa = tuKhoa == null ? "" : tuKhoa.trim();
        if (khoa.isEmpty()) {
            return ds;
        }
        switch (tieuChi == null ? "" : tieuChi) {
            case "Mã" -> {
                BenhNhan bn = BenhNhan_DAO.layBenhNhanTheoMa(khoa);
                if (bn != null) {
                    ds.add(bn);
                }
            }
            case "SĐT" -> ds.addAll(BenhNhan_DAO.layBenhNhanTheoSDT(khoa));
            default -> {
                String k = khoa.toLowerCase();
                for (BenhNhan bn : BenhNhan_DAO.getAllBenhNhan()) {
                    if (bn.getHoTen().toLowerCase().contains(k)) {
                        ds.add(bn);
                    }
                }
            }
        }
        return ds;
    }

    public static BenhNhan layBenhNhanTheoMa(String maBenhNhan) {
        return BenhNhan_DAO.layBenhNhanTheoMa(maBenhNhan);
    }

    /** Lich su phieu kham cua benh nhan (doc qua PhieuKham_DAO, chi doc). */
    public static ArrayList<entity.PhieuKham> layLichSuPhieuKham(SessionContext session,
                                                                                  String maBenhNhan) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.LE_TAN, ChucVu.BAC_SI);
        return dao.PhieuKham_DAO.layTheoBenhNhan(maBenhNhan);
    }

    /** Nap toan bo danh sach benh nhan cho man hinh tra cuu/ho so cua le tan. */
    public static ArrayList<BenhNhan> danhSachToanBo(SessionContext session) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.LE_TAN);
        return BenhNhan_DAO.getAllBenhNhan();
    }

    public static ArrayList<BenhNhan> layBenhNhanTheoSDT(String soDienThoai) {
        return BenhNhan_DAO.layBenhNhanTheoSDT(soDienThoai);
    }

    private static String emptyThanNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }

    private static void rollback(Connection con) {
        try {
            con.rollback();
            con.setAutoCommit(true);
        } catch (SQLException ignored) { }
    }

    private BenhNhan_BUS() { }
}
