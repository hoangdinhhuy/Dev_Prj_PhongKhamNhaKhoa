package bus;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import connectDB.ConnectDB;
import dao.ChiDinhDichVu_DAO;
import dao.ChiTietCapThuoc_DAO;
import dao.DichVu_DAO;
import dao.PhieuKham_DAO;
import entity.ChiDinhDichVu;
import entity.ChiTietCapThuoc;
import entity.ChucVu;
import entity.DichVu;
import entity.PhieuKham;
import entity.SessionContext;
import entity.TrangThaiDichVu;
import entity.TrangThaiPhieuKham;
import util.MaThongBao;
import util.QuyenUtil;
import util.ValidateUtil;

public class KhamBenh_BUS {

    /** Danh sach phieu cua chinh bac si dang dang nhap (CHO_KHAM + DANG_KHAM) hom nay. */
    public static List<PhieuKham> danhSachKhamCuaToi(SessionContext session) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.BAC_SI);
        return PhieuKham_DAO.layDanhSachTheoBacSi(session.getMaNhanVien(), LocalDate.now());
    }

    /** UC004 - luu kham: UPDATE phieu (dieu kien DANG_KHAM + chu phieu) + ho so + chi dinh + thuoc, 1 transaction. */
    public static void luuKham(SessionContext session, String maPhieuKham, String chanDoan, String ketQuaDieuTri,
                               String ghiChuThuocTuMua, List<ChiDinhDichVu> dsChiDinh,
                               List<ChiTietCapThuoc> dsThuocCap) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.BAC_SI);
        String cd = ValidateUtil.khongRong(chanDoan, MaThongBao.VL09);
        if (dsChiDinh == null || dsChiDinh.isEmpty()) {
            throw new IllegalArgumentException(MaThongBao.TB06);
        }
        if (dsThuocCap == null) {
            dsThuocCap = new ArrayList<>();
        }
        for (ChiTietCapThuoc ct : dsThuocCap) {
            if (ct.getSoLuong() < 1) {
                throw new IllegalArgumentException(MaThongBao.VL12);
            }
        }

        PhieuKham pk = PhieuKham_DAO.layPhieuKhamTheoMa(maPhieuKham);
        if (pk == null || pk.getTrangThai() != TrangThaiPhieuKham.DANG_KHAM
                || !session.getMaNhanVien().equals(pk.getMaNhanVien())) {
            throw new IllegalArgumentException(MaThongBao.TB10);
        }

        Connection con = ConnectDB.getInstance().getConnection();
        try {
            con.setAutoCommit(false);
            String maHoSo = HoSoBenhAn_BUS.taoHoSoNeuChuaCo(con, pk.getMaBenhNhan(), null);
            int n = PhieuKham_DAO.capNhatThongTinKham(con, maPhieuKham, session.getMaNhanVien(),
                    cd, emptyThanNull(ketQuaDieuTri), emptyThanNull(ghiChuThuocTuMua), maHoSo);
            if (n == 0) {
                throw new SQLException(MaThongBao.TB10);
            }
            // Chi dinh: xoa cu + insert lai toan bo trong cung transaction (cach chot de sua chi dinh)
            ChiDinhDichVu_DAO.xoaTheoPhieu(con, maPhieuKham);
            for (ChiDinhDichVu cdv : dsChiDinh) {
                DichVu dv = DichVu_DAO.layDichVuTheoMa(cdv.getMaDichVu());
                if (dv == null || dv.getTrangThai() != TrangThaiDichVu.DANG_PHUC_VU) {
                    throw new SQLException(MaThongBao.TB15);
                }
                ChiDinhDichVu_DAO.themChiDinh(con, cdv);
            }
            // Thuoc cap: xoa cu + insert lai
            ChiTietCapThuoc_DAO.xoaTheoPhieu(con, maPhieuKham);
            for (ChiTietCapThuoc ct : dsThuocCap) {
                ChiTietCapThuoc_DAO.themChiTiet(con, ct);
            }
            con.commit();
            con.setAutoCommit(true);
        } catch (SQLException | RuntimeException ex) {
            rollback(con);
            throw new RuntimeException(ex.getMessage() == null ? MaThongBao.TB09 : ex.getMessage(), ex);
        }
    }

    /** Hoan tat: phai co chan doan; neu co chi dinh thi phai da luu; DANG_KHAM -> HOAN_TAT. */
    public static void hoanTat(SessionContext session, String maPhieuKham) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.BAC_SI);
        PhieuKham pk = PhieuKham_DAO.layPhieuKhamTheoMa(maPhieuKham);
        if (pk == null) {
            throw new IllegalArgumentException(MaThongBao.TB10);
        }
        if (!session.getMaNhanVien().equals(pk.getMaNhanVien())) {
            throw new SecurityException(MaThongBao.TB08);
        }
        if (pk.getChanDoan() == null || pk.getChanDoan().isBlank()) {
            throw new IllegalArgumentException(MaThongBao.VL09);
        }
        if (ChiDinhDichVu_DAO.layTheoPhieu(maPhieuKham).isEmpty()) {
            throw new IllegalArgumentException(MaThongBao.TB06);
        }
        int n = PhieuKham_DAO.capNhatTrangThai(maPhieuKham, TrangThaiPhieuKham.HOAN_TAT,
                TrangThaiPhieuKham.DANG_KHAM);
        if (n == 0) {
            throw new IllegalArgumentException(MaThongBao.TB10);
        }
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

    private KhamBenh_BUS() { }
}
