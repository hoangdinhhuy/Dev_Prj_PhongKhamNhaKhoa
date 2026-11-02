package dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import entity.ChiDinhDichVu;

public class ChiDinhDichVu_DAO {

    public static void xoaTheoPhieu(Connection con, String maPhieuKham) throws SQLException {
        try (PreparedStatement pstm = con.prepareStatement("DELETE FROM ChiDinhDichVu WHERE maPhieuKham = ?")) {
            pstm.setString(1, maPhieuKham);
            pstm.executeUpdate();
        }
    }

    public static void themChiDinh(Connection con, ChiDinhDichVu cd) throws SQLException {
        try (PreparedStatement pstm = con.prepareStatement(
                "INSERT INTO ChiDinhDichVu (maPhieuKham, maDichVu) VALUES (?, ?)")) {
            pstm.setString(1, cd.getMaPhieuKham());
            pstm.setString(2, cd.getMaDichVu());
            pstm.executeUpdate();
        }
    }

    public static ArrayList<ChiDinhDichVu> layTheoPhieu(String maPhieuKham) {
        String sql = "SELECT cd.maPhieuKham, cd.maDichVu, dv.tenDichVu, dv.donGiaNiemYet"
                + " FROM ChiDinhDichVu cd JOIN DichVu dv ON dv.maDichVu = cd.maDichVu WHERE cd.maPhieuKham = ?";
        ArrayList<ChiDinhDichVu> ds = new ArrayList<>();
        Connection con = connectDB.ConnectDB.getInstance().getConnection();
        try (PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setString(1, maPhieuKham);
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    ChiDinhDichVu cd = new ChiDinhDichVu(rs.getString(1), rs.getString(2));
                    cd.setTenDichVu(rs.getString(3));
                    cd.setDonGia(rs.getBigDecimal(4));
                    ds.add(cd);
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi truy vấn ChiDinhDichVu", ex);
        }
        return ds;
    }

    /** So dich vu trung binh moi phieu kham (dung cho AI du doan thoi gian cho). */
    public static double trungBinhSoDichVuMoiPhieu() {
        String sql = "SELECT AVG(CAST(c AS float)) FROM (SELECT COUNT(*) AS c FROM ChiDinhDichVu GROUP BY maPhieuKham) t";
        Connection con = connectDB.ConnectDB.getInstance().getConnection();
        try (PreparedStatement pstm = con.prepareStatement(sql); ResultSet rs = pstm.executeQuery()) {
            return rs.next() && rs.getObject(1) != null ? rs.getDouble(1) : 0.0;
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi tính trung bình dịch vụ mỗi lượt khám", ex);
        }
    }

    private ChiDinhDichVu_DAO() { }
}
