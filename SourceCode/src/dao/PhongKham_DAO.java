package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import connectDB.ConnectDB;
import entity.PhongKham;

public class PhongKham_DAO {

    public static ArrayList<PhongKham> getAllPhongKham() {
        String sql = "SELECT maPhong, tenPhong, dangNhanBenh FROM PhongKham ORDER BY maPhong";
        ArrayList<PhongKham> ds = new ArrayList<>();
        Connection con = ConnectDB.getInstance().getConnection();
        try (PreparedStatement pstm = con.prepareStatement(sql); ResultSet rs = pstm.executeQuery()) {
            while (rs.next()) {
                ds.add(map(rs));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi truy vấn PhongKham", ex);
        }
        return ds;
    }

    public static ArrayList<PhongKham> layPhongDangNhanBenh() {
        String sql = "SELECT maPhong, tenPhong, dangNhanBenh FROM PhongKham WHERE dangNhanBenh = 1 ORDER BY maPhong";
        ArrayList<PhongKham> ds = new ArrayList<>();
        Connection con = ConnectDB.getInstance().getConnection();
        try (PreparedStatement pstm = con.prepareStatement(sql); ResultSet rs = pstm.executeQuery()) {
            while (rs.next()) {
                ds.add(map(rs));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi truy vấn PhongKham", ex);
        }
        return ds;
    }

    public static PhongKham layPhongTheoMa(String maPhong) {
        String sql = "SELECT maPhong, tenPhong, dangNhanBenh FROM PhongKham WHERE maPhong = ?";
        Connection con = ConnectDB.getInstance().getConnection();
        try (PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setString(1, maPhong);
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi truy vấn PhongKham", ex);
        }
        return null;
    }

    public static int capNhatNhanBenh(String maPhong, boolean dangNhanBenh) {
        String sql = "UPDATE PhongKham SET dangNhanBenh = ? WHERE maPhong = ?";
        Connection con = ConnectDB.getInstance().getConnection();
        try (PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setBoolean(1, dangNhanBenh);
            pstm.setString(2, maPhong);
            return pstm.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi cập nhật phòng khám", ex);
        }
    }

    private static PhongKham map(ResultSet rs) throws SQLException {
        return new PhongKham(rs.getString("maPhong"), rs.getString("tenPhong"), rs.getBoolean("dangNhanBenh"));
    }

    private PhongKham_DAO() { }
}
