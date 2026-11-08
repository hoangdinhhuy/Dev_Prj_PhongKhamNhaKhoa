package bus;

import java.time.LocalDate;
import java.util.ArrayList;
import dao.PhongKham_DAO;
import entity.BenhNhan;
import entity.ChucVu;
import entity.SessionContext;
import util.GoiTuKhoa;
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
        if (cmnd != null && dao.BenhNhan_DAO.layBenhNhanTheoCCCD(cmnd) != null) {
            throw new IllegalArgumentException(MaThongBao.TB13);
        }
        kiemTraSdtChuaTonTai(sdt, null);

        java.sql.Connection con = connectDB.ConnectDB.getInstance().getConnection();
        try {
            con.setAutoCommit(false);
            String maBN;
            do {
                maBN = dao.BoDem_DAO.layMaMoi(con, "BN", LocalDate.now());
            } while (dao.BenhNhan_DAO.tonTaiMa(con, maBN));
            BenhNhan bn = new BenhNhan(maBN, ten, sdt, cmnd, ngaySinh, emptyThanNull(diaChi),
                    emptyThanNull(tienSuDiUng));
            dao.BenhNhan_DAO.themBenhNhan(con, bn);
            con.commit();
            con.setAutoCommit(true);
            return bn;
        } catch (java.sql.SQLException ex) {
            rollback(con);
            if (sdtDaTonTai(sdt, null)) throw new IllegalArgumentException(MaThongBao.TB20, ex);
            throw new RuntimeException("Lỗi tạo hồ sơ bệnh nhân: " + ex.getMessage(), ex);
        }
    }

    public static void capNhatHoSo(SessionContext session, BenhNhan bn) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.LE_TAN);
        ValidateUtil.khongRong(bn.getHoTen(), MaThongBao.VL01);
        String sdt = ValidateUtil.sdt(bn.getSoDienThoai());
        kiemTraSdtChuaTonTai(sdt, bn.getMaBenhNhan());
        ValidateUtil.ngaySinh(bn.getNgaySinh());
        String cmnd = ValidateUtil.cccd(bn.getCccd());
        if (cmnd != null) {
            BenhNhan khac = dao.BenhNhan_DAO.layBenhNhanTheoCCCD(cmnd);
            if (khac != null && !khac.getMaBenhNhan().equals(bn.getMaBenhNhan())) {
                throw new IllegalArgumentException(MaThongBao.TB13);
            }
            bn.setCccd(cmnd);
        }
        java.sql.Connection con = connectDB.ConnectDB.getInstance().getConnection();
        try {
            dao.BenhNhan_DAO.suaBenhNhan(con, bn);
        } catch (java.sql.SQLException ex) {
            if (sdtDaTonTai(sdt, bn.getMaBenhNhan())) throw new IllegalArgumentException(MaThongBao.TB20, ex);
            throw new RuntimeException("Lỗi cập nhật hồ sơ: " + ex.getMessage(), ex);
        }
    }

    /** UC006 - tra cuu theo tieu chi: "Mã" | "SĐT" | "Họ tên". Họ tên so khớp bỏ dấu (Góp ý GV 2/6). */
    public static ArrayList<BenhNhan> traCuu(SessionContext session, String tieuChi, String tuKhoa) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.LE_TAN);
        ArrayList<BenhNhan> ds = new ArrayList<>();
        String khoa = tuKhoa == null ? "" : tuKhoa.trim();
        if (khoa.isEmpty()) {
            return ds;
        }
        switch (tieuChi == null ? "" : tieuChi) {
            case "Mã" -> {
                BenhNhan bn = dao.BenhNhan_DAO.layBenhNhanTheoMa(khoa);
                if (bn != null) {
                    ds.add(bn);
                }
            }
            case "SĐT" -> ds.addAll(dao.BenhNhan_DAO.layBenhNhanTheoSDT(khoa));
            default -> {
                // (Góp ý GV 2/6) Tìm đúng từ khóa không dấu: "nguyen van" vẫn ra "Nguyễn Văn"
                String k = GoiTuKhoa.chuanHoa(khoa);
                for (BenhNhan bn : dao.BenhNhan_DAO.getAllBenhNhan()) {
                    if (GoiTuKhoa.chuanHoa(bn.getHoTen()).contains(k)) {
                        ds.add(bn);
                    }
                }
            }
        }
        return ds;
    }

    /** (Mẫu GD_DatPhong N11) Tim theo CCCD de TU NHAN DIEN khach cu khi tiep nhan. */
    public static BenhNhan layBenhNhanTheoCCCD(String cccd) {
        return dao.BenhNhan_DAO.layBenhNhanTheoCCCD(cccd);
    }

    public static BenhNhan layBenhNhanTheoMa(String maBenhNhan) {
        return dao.BenhNhan_DAO.layBenhNhanTheoMa(maBenhNhan);
    }

    /** Lịch sử phiếu khám của bệnh nhân (đọc qua PhieuKham_DAO, chỉ đọc). */
    public static ArrayList<entity.PhieuKham> layLichSuPhieuKham(SessionContext session,
                                                                 String maBenhNhan) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.LE_TAN, ChucVu.BAC_SI);
        return dao.PhieuKham_DAO.layTheoBenhNhan(maBenhNhan);
    }

    /** (Góp ý GV 2/5 — cách 1) Nút tường minh "Hiện tất cả" mới tải toàn bộ danh sách. */
    public static ArrayList<BenhNhan> danhSachToanBo(SessionContext session) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.LE_TAN);
        return dao.BenhNhan_DAO.getAllBenhNhan();
    }

    /** (Góp ý GV P2-5 — gợi ý chọn nhanh) Gợi ý bệnh nhân theo từ khóa: mã BN, SĐT bắt đầu bằng,
     *  hoặc tên (bỏ dấu). Chỉ quét tối đa 200 hồ sơ gần nhất — không quét toàn bộ. */
    public static ArrayList<BenhNhan> goiYBenhNhan(SessionContext session, String tuKhoa, int gioiHan) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.LE_TAN, ChucVu.BAC_SI);
        String tu = tuKhoa == null ? "" : tuKhoa.trim();
        String k = GoiTuKhoa.chuanHoa(tu);
        ArrayList<BenhNhan> ketQua = new ArrayList<>();
        if (k.isEmpty()) {
            return ketQua;
        }
        for (BenhNhan bn : dao.BenhNhan_DAO.layBenhNhanMoiNhat(200)) {
            String sdt = bn.getSoDienThoai() == null ? "" : bn.getSoDienThoai();
            boolean khop = GoiTuKhoa.chuanHoa(bn.getHoTen()).contains(k)
                    || sdt.startsWith(tu)
                    || bn.getMaBenhNhan().equalsIgnoreCase(tu);
            if (khop) {
                ketQua.add(bn);
                if (ketQua.size() >= Math.max(1, gioiHan)) {
                    break;
                }
            }
        }
        return ketQua;
    }

    public static ArrayList<BenhNhan> layBenhNhanTheoSDT(String soDienThoai) {
        return dao.BenhNhan_DAO.layBenhNhanTheoSDT(soDienThoai);
    }

    private static void kiemTraSdtChuaTonTai(String sdt, String maBoQua) {
        if (sdtDaTonTai(sdt, maBoQua)) throw new IllegalArgumentException(MaThongBao.TB20);
    }

    private static boolean sdtDaTonTai(String sdt, String maBoQua) {
        return dao.BenhNhan_DAO.layBenhNhanTheoSDT(sdt).stream()
                .anyMatch(bn -> maBoQua == null || !maBoQua.equals(bn.getMaBenhNhan()));
    }

    private static String emptyThanNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }

    private static void rollback(java.sql.Connection con) {
        try {
            con.rollback();
            con.setAutoCommit(true);
        } catch (java.sql.SQLException ignored) { }
    }

    private BenhNhan_BUS() { }
}
