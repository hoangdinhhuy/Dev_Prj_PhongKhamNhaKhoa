package bus;

import connectDB.ConnectDB;
import dao.BoDem_DAO;
import dao.HangHoa_DAO;
import entity.ChucVu;
import entity.HangHoa;
import entity.SessionContext;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import util.MaThongBao;
import util.QuyenUtil;
import util.ValidateUtil;

public final class HangHoa_BUS {
    private HangHoa_BUS() { }

    public static ArrayList<HangHoa> danhSach(SessionContext session) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.QUAN_LY);
        return HangHoa_DAO.getAllHangHoa();
    }

    /**
     * Tra cứu theo tiêu chí (chuẩn OOP, lọc trên đối tượng HangHoa):
     * - tuKhoa: Tên hàng hóa hoặc Mã hàng hóa.
     * - tinhTrang: "CAN_BO_SUNG", "DU_HANG", "TAT_CA".
     */
    public static ArrayList<HangHoa> timKiemHangHoa(SessionContext session, String tuKhoa, String tinhTrang) {
        ArrayList<HangHoa> tatCa = danhSach(session);
        ArrayList<HangHoa> ketQua = new ArrayList<>();
        String tkNorm = tuKhoa == null ? "" : tuKhoa.trim().toLowerCase();

        for (HangHoa h : tatCa) {
            if ("CAN_BO_SUNG".equalsIgnoreCase(tinhTrang) && !h.isCanBoSung()) {
                continue;
            }
            if ("DU_HANG".equalsIgnoreCase(tinhTrang) && h.isCanBoSung()) {
                continue;
            }
            if (!tkNorm.isEmpty()) {
                boolean matchTen = h.getTenHangHoa() != null && h.getTenHangHoa().toLowerCase().contains(tkNorm);
                boolean matchMa = h.getMaHangHoa() != null && h.getMaHangHoa().toLowerCase().contains(tkNorm);
                if (!matchTen && !matchMa) {
                    continue;
                }
            }
            ketQua.add(h);
        }
        return ketQua;
    }

    private static HangHoa duLieu(String ma, String ten, String donVi, String soLuong, String mucToiThieu) {
        String tenHopLe = ValidateUtil.khongRong(ten, MaThongBao.VL15);
        String donViHopLe = ValidateUtil.khongRong(donVi, MaThongBao.VL16);
        if (tenHopLe.length() > 100 || donViHopLe.length() > 30) throw new IllegalArgumentException(MaThongBao.VL17);
        try {
            int ton = Integer.parseInt(soLuong.trim());
            int toiThieu = Integer.parseInt(mucToiThieu.trim());
            if (ton < 0 || toiThieu < 0) throw new NumberFormatException();
            return new HangHoa(ma, tenHopLe, donViHopLe, ton, toiThieu);
        } catch (NullPointerException | NumberFormatException ex) {
            throw new IllegalArgumentException(MaThongBao.VL18);
        }
    }

    public static HangHoa them(SessionContext session, String ten, String donVi, String soLuong, String mucToiThieu) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.QUAN_LY);
        HangHoa nhap = duLieu(null, ten, donVi, soLuong, mucToiThieu);
        if (HangHoa_DAO.tenDaCo(null, nhap.getTenHangHoa())) throw new IllegalArgumentException(MaThongBao.TB18);
        Connection con = ConnectDB.getInstance().getConnection();
        try {
            con.setAutoCommit(false);
            String ma;
            do { ma = BoDem_DAO.layMaMoi(con, "HH", LocalDate.now()); }
            while (HangHoa_DAO.tonTaiMa(con, ma));
            HangHoa moi = new HangHoa(ma, nhap.getTenHangHoa(), nhap.getDonViTinh(), nhap.getSoLuongTon(), nhap.getMucTonToiThieu());
            HangHoa_DAO.them(con, moi);
            con.commit();
            return moi;
        } catch (SQLException ex) {
            try { con.rollback(); } catch (SQLException ignored) { }
            throw new RuntimeException(MaThongBao.TB09, ex);
        } finally {
            try { con.setAutoCommit(true); } catch (SQLException ignored) { }
        }
    }

    public static void capNhat(SessionContext session, String ma, String ten, String donVi, String soLuong, String mucToiThieu) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.QUAN_LY);
        HangHoa h = duLieu(ma, ten, donVi, soLuong, mucToiThieu);
        if (HangHoa_DAO.tenDaCo(ma, h.getTenHangHoa())) throw new IllegalArgumentException(MaThongBao.TB18);
        try {
            if (!HangHoa_DAO.capNhat(ConnectDB.getInstance().getConnection(), h)) throw new IllegalArgumentException(MaThongBao.TB19);
        } catch (SQLException ex) {
            throw new RuntimeException(MaThongBao.TB09, ex);
        }
    }
}
