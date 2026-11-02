package bus;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import dao.NhanVien_DAO;
import dao.TaiKhoan_DAO;
import entity.NhanVien;
import entity.SessionContext;
import entity.TaiKhoan;
import entity.TrangThaiTaiKhoan;
import util.MaThongBao;
import util.ValidateUtil;

public class TaiKhoan_BUS {

    /** UC001 - xac thuc + tra ve phien dang nhap. Quyền được lấy từ DB, KHONG lấy từ form. */
    public static SessionContext dangNhap(String tenDangNhap, String matKhau) {
        String ten = ValidateUtil.khongRong(tenDangNhap, MaThongBao.VL13);
        String mk = ValidateUtil.khongRong(matKhau, MaThongBao.VL13);

        TaiKhoan tk = TaiKhoan_DAO.getTaiKhoanTheoTenDangNhap(ten);
        if (tk == null || !sha256(mk).equalsIgnoreCase(tk.getMatKhau())) {
            throw new IllegalArgumentException(MaThongBao.TB01);
        }
        if (tk.getTrangThai() == TrangThaiTaiKhoan.BI_KHOA) {
            throw new IllegalArgumentException(MaThongBao.TB02);
        }
        NhanVien nv = NhanVien_DAO.layNhanVienTheoMa(tk.getMaNhanVien());
        if (nv == null || !nv.isConLamViec()) {
            throw new IllegalArgumentException(MaThongBao.TB01);
        }
        return new SessionContext(nv.getMaNhanVien(), nv.getHoTen(), nv.getChucVu());
    }

    public static String sha256(String chuoi) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(chuoi.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }

    private TaiKhoan_BUS() { }
}
