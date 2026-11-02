package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import connectDB.ConnectDB;

/** Bao cao thong ke bang SQL GROUP BY (cam nap toan bo ve loc bang Java). Chu file: Duy. */
public class BaoCao_DAO {

    /** Tong so phieu kham theo tung ngay trong khoang. Tra ve Object[]{ngay(LocalDate), soLuong(Integer)}. */
    public static ArrayList<Object[]> thongKeLuotKham(java.time.LocalDate tuNgay, java.time.LocalDate denNgay) {
        String sql = "SELECT ngayKham, COUNT(*) FROM PhieuKham WHERE ngayKham >= ? AND ngayKham <= ?"
                + " GROUP BY ngayKham ORDER BY ngayKham";
        ArrayList<Object[]> ds = new ArrayList<>();
        Connection con = ConnectDB.getInstance().getConnection();
        try (PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setDate(1, Date.valueOf(tuNgay));
            pstm.setDate(2, Date.valueOf(denNgay));
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    ds.add(new Object[]{rs.getDate(1).toLocalDate(), rs.getInt(2)});
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi thống kê lượt khám", ex);
        }
        return ds;
    }

    /** Tan suat dich vu duoc chi dinh trong khoang. Tra ve Object[]{tenDichVu, soLuong(Integer)}. */
    public static ArrayList<Object[]> thongKeTanSuatDichVu(java.time.LocalDate tuNgay, java.time.LocalDate denNgay) {
        String sql = "SELECT dv.tenDichVu, COUNT(*) FROM ChiDinhDichVu cd"
                + " JOIN DichVu dv ON dv.maDichVu = cd.maDichVu"
                + " JOIN PhieuKham pk ON pk.maPhieuKham = cd.maPhieuKham"
                + " WHERE pk.ngayKham >= ? AND pk.ngayKham <= ?"
                + " GROUP BY dv.tenDichVu ORDER BY COUNT(*) DESC";
        ArrayList<Object[]> ds = new ArrayList<>();
        Connection con = ConnectDB.getInstance().getConnection();
        try (PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setDate(1, Date.valueOf(tuNgay));
            pstm.setDate(2, Date.valueOf(denNgay));
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    ds.add(new Object[]{rs.getString(1), rs.getInt(2)});
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi thống kê tần suất dịch vụ", ex);
        }
        return ds;
    }

    private BaoCao_DAO() { }
}
