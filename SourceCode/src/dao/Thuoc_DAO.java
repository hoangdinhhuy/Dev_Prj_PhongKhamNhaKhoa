package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import connectDB.ConnectDB;
import entity.Thuoc;

public class Thuoc_DAO {

    public static ArrayList<Thuoc> getAllThuoc() {
        String sql = "SELECT maThuoc, tenThuoc, donViTinh FROM Thuoc ORDER BY tenThuoc";
        ArrayList<Thuoc> ds = new ArrayList<>();
        Connection con = ConnectDB.getInstance().getConnection();
        try (PreparedStatement pstm = con.prepareStatement(sql); ResultSet rs = pstm.executeQuery()) {
            while (rs.next()) {
                ds.add(new Thuoc(rs.getString(1), rs.getString(2), rs.getString(3)));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi truy vấn Thuoc", ex);
        }
        return ds;
    }

    public static Thuoc layThuocTheoMa(String maThuoc) {
        String sql = "SELECT maThuoc, tenThuoc, donViTinh FROM Thuoc WHERE maThuoc = ?";
        Connection con = ConnectDB.getInstance().getConnection();
        try (PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setString(1, maThuoc);
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    return new Thuoc(rs.getString(1), rs.getString(2), rs.getString(3));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi truy vấn Thuoc", ex);
        }
        return null;
    }

    private Thuoc_DAO() { }
}
