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

    /** Kiểm tra mã trong cùng transaction cấp mã để bỏ qua các mã đã có từ dữ liệu seed/cũ. */
    public static boolean tonTaiMa(Connection con, String maBenhNhan) throws SQLException {
        String sql = "SELECT 1 FROM BenhNhan WHERE maBenhNhan = ?";
        try (PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setString(1, maBenhNhan);
            try (ResultSet rs = pstm.executeQuery()) {
                return rs.next();
            }
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



    /** Danh sach rut gon cho GOI Y type-ahead (Gop y GV P2-5: goi y "thuong xuyen/gan day").
     *  Chi TOP N theo ma giam dan — moi tao cap nhat thi hay doi, phuc vu le tan tiep benh moi. */
    public static ArrayList<BenhNhan> layBenhNhanMoiNhat(int gioiHan) {
        String sql = "SELECT " + COLS + " FROM BenhNhan ORDER BY maBenhNhan DESC"
                + " OFFSET 0 ROWS FETCH NEXT ? ROWS ONLY";
        ArrayList<BenhNhan> ds = new ArrayList<>();
        Connection con = ConnectDB.getInstance().getConnection();
        try (PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setInt(1, Math.max(1, gioiHan));
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    ds.add(map(rs));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi truy vấn bệnh nhân", ex);
        }
        return ds;
    }

    /** (Dashboard lễ tân) Bệnh nhân tạo HÔM NAY mà chưa thanh toán phí khám nào còn hiệu lực
     *  — danh sách việc cần làm "chờ thu phí" của lễ tân. */
    public static ArrayList<BenhNhan> layChoThuPhiHomNay(String mauMaHomNay) {
        String sql = "SELECT bn.maBenhNhan, bn.hoTen, bn.soDienThoai, bn.cccd, bn.ngaySinh, bn.diaChi, bn.tienSuDiUng"
                + " FROM BenhNhan bn"
                + " WHERE bn.maBenhNhan LIKE ?"
                + " AND NOT EXISTS (SELECT 1 FROM HoaDon hd"
                + "   JOIN ChiTietHoaDon ct ON ct.maHoaDon = hd.maHoaDon AND ct.maDichVu = 'DV0001'"
                + "   WHERE hd.maBenhNhan = bn.maBenhNhan AND hd.daThanhToan = 1 AND hd.maPhieuKham IS NULL)"
                + " ORDER BY bn.maBenhNhan DESC";
        ArrayList<BenhNhan> ds = new ArrayList<>();
        Connection con = ConnectDB.getInstance().getConnection();
        try (PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setString(1, mauMaHomNay);
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    ds.add(map(rs));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi truy vấn chờ thu phí", ex);
        }
        return ds;
    }

    private BenhNhan_DAO() { }
}
