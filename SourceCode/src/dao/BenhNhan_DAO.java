package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import connectDB.ConnectDB;
import entity.BenhNhan;

public class BenhNhan_DAO {

    private static final String COLS = "maBenhNhan, hoTen, soDienThoai, cccd, ngaySinh, diaChi, tienSuDiUng";

    private static BenhNhan map(ResultSet rs) throws SQLException {
        return new BenhNhan(rs.getString("maBenhNhan"), rs.getString("hoTen"), rs.getString("soDienThoai"),
                rs.getString("cccd"), rs.getDate("ngaySinh").toLocalDate(), rs.getString("diaChi"),
                rs.getString("tienSuDiUng"));
    }

    public static void themBenhNhan(Connection con, BenhNhan bn) throws SQLException {
        String sql = "INSERT INTO BenhNhan (maBenhNhan, hoTen, soDienThoai, cccd, ngaySinh, diaChi, tienSuDiUng)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setString(1, bn.getMaBenhNhan());
            pstm.setString(2, bn.getHoTen());
            pstm.setString(3, bn.getSoDienThoai());
            pstm.setString(4, bn.getCccd());
            pstm.setDate(5, Date.valueOf(bn.getNgaySinh()));
            pstm.setString(6, bn.getDiaChi());
            pstm.setString(7, bn.getTienSuDiUng());
            pstm.executeUpdate();
        }
    }

    public static void suaBenhNhan(Connection con, BenhNhan bn) throws SQLException {
        String sql = "UPDATE BenhNhan SET hoTen = ?, soDienThoai = ?, cccd = ?, ngaySinh = ?, diaChi = ?,"
                + " tienSuDiUng = ? WHERE maBenhNhan = ?";
        try (PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setString(1, bn.getHoTen());
            pstm.setString(2, bn.getSoDienThoai());
            pstm.setString(3, bn.getCccd());
            pstm.setDate(4, Date.valueOf(bn.getNgaySinh()));
            pstm.setString(5, bn.getDiaChi());
            pstm.setString(6, bn.getTienSuDiUng());
            pstm.setString(7, bn.getMaBenhNhan());
            pstm.executeUpdate();
        }
    }

    public static ArrayList<BenhNhan> getAllBenhNhan() {
        String sql = "SELECT " + COLS + " FROM BenhNhan ORDER BY maBenhNhan DESC";
        ArrayList<BenhNhan> ds = new ArrayList<>();
        Connection con = ConnectDB.getInstance().getConnection();
        try (PreparedStatement pstm = con.prepareStatement(sql); ResultSet rs = pstm.executeQuery()) {
            while (rs.next()) {
                ds.add(map(rs));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi truy vấn BenhNhan", ex);
        }
        return ds;
    }

    public static BenhNhan layBenhNhanTheoMa(String maBenhNhan) {
        String sql = "SELECT " + COLS + " FROM BenhNhan WHERE maBenhNhan = ?";
        Connection con = ConnectDB.getInstance().getConnection();
        try (PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setString(1, maBenhNhan.trim());
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi truy vấn BenhNhan", ex);
        }
        return null;
    }

    public static ArrayList<BenhNhan> layBenhNhanTheoSDT(String soDienThoai) {
        String sql = "SELECT " + COLS + " FROM BenhNhan WHERE soDienThoai = ?";
        ArrayList<BenhNhan> ds = new ArrayList<>();
        Connection con = ConnectDB.getInstance().getConnection();
        try (PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setString(1, soDienThoai.trim());
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    ds.add(map(rs));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi truy vấn BenhNhan", ex);
        }
        return ds;
    }

    public static BenhNhan layBenhNhanTheoCCCD(String cccd) {
        String sql = "SELECT " + COLS + " FROM BenhNhan WHERE cccd = ?";
        Connection con = ConnectDB.getInstance().getConnection();
        try (PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setString(1, cccd.trim());
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi truy vấn BenhNhan", ex);
        }
        return null;
    }

    public static int demTong() {
        String sql = "SELECT COUNT(*) FROM BenhNhan";
        Connection con = ConnectDB.getInstance().getConnection();
        try (PreparedStatement pstm = con.prepareStatement(sql); ResultSet rs = pstm.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi đếm bệnh nhân", ex);
        }
    }

    private BenhNhan_DAO() { }
}
