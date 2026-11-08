package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import connectDB.ConnectDB;
import entity.CaThuNgan;
import entity.NhanVien;

public class CaThuNgan_DAO {

    private static final String COLS = "c.maCa, c.maNhanVien, c.thoiGianMo, c.thoiGianChot,"
            + " c.doanhThuDichVu, c.phiThuHo, c.trangThai, nv.hoTen AS tenNhanVien";

    private static CaThuNgan map(ResultSet rs) throws SQLException {
        CaThuNgan ca = new CaThuNgan(rs.getString("maCa"));
        NhanVien nv = new NhanVien(rs.getString("maNhanVien"));
        nv.setHoTen(rs.getString("tenNhanVien"));
        ca.setNhanVien(nv);
        Timestamp mo = rs.getTimestamp("thoiGianMo");
        ca.setThoiGianMo(mo == null ? null : mo.toLocalDateTime());
        Timestamp chot = rs.getTimestamp("thoiGianChot");
        ca.setThoiGianChot(chot == null ? null : chot.toLocalDateTime());
        ca.setDoanhThuDichVu(rs.getBigDecimal("doanhThuDichVu"));
        ca.setPhiThuHo(rs.getBigDecimal("phiThuHo"));
        ca.setTrangThai(rs.getString("trangThai"));
        return ca;
    }

    /** Ca đang mở (toàn quầy — 1 ca duy nhất). */
    public static CaThuNgan layCaDangMo() {
        String sql = "SELECT " + COLS + " FROM CaThuNgan c"
                + " JOIN NhanVien nv ON nv.maNhanVien = c.maNhanVien"
                + " WHERE c.trangThai = N'DANG_MO' ORDER BY c.thoiGianMo DESC";
        Connection con = ConnectDB.getInstance().getConnection();
        try (PreparedStatement pstm = con.prepareStatement(sql); ResultSet rs = pstm.executeQuery()) {
            return rs.next() ? map(rs) : null;
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi truy vấn ca đang mở", ex);
        }
    }

    public static boolean tonTaiMa(Connection con, String maCa) throws SQLException {
        try (PreparedStatement p = con.prepareStatement("SELECT 1 FROM CaThuNgan WHERE maCa = ?")) {
            p.setString(1, maCa);
            try (ResultSet rs = p.executeQuery()) { return rs.next(); }
        }
    }

    public static void themCa(Connection con, CaThuNgan ca) throws SQLException {
        try (PreparedStatement pstm = con.prepareStatement(
                "INSERT INTO CaThuNgan (maCa, maNhanVien, thoiGianMo, trangThai) VALUES (?, ?, ?, N'DANG_MO')")) {
            pstm.setString(1, ca.getMaCa());
            pstm.setString(2, ca.getMaNhanVien());
            pstm.setTimestamp(3, Timestamp.valueOf(ca.getThoiGianMo()));
            pstm.executeUpdate();
        }
    }

    /** Chốt ca: ghi snapshot doanh thu + thời gian chốt (conditional UPDATE — chỉ chốt khi ĐANG_MO). */
    public static int chotCa(Connection con, String maCa, Timestamp thoiGianChot,
                             java.math.BigDecimal doanhThuDichVu, java.math.BigDecimal phiThuHo) throws SQLException {
        try (PreparedStatement pstm = con.prepareStatement(
                "UPDATE CaThuNgan SET thoiGianChot = ?, doanhThuDichVu = ?, phiThuHo = ?,"
                + " trangThai = N'DA_CHOT' WHERE maCa = ? AND trangThai = N'DANG_MO'")) {
            pstm.setTimestamp(1, thoiGianChot);
            pstm.setBigDecimal(2, doanhThuDichVu);
            pstm.setBigDecimal(3, phiThuHo);
            pstm.setString(4, maCa);
            return pstm.executeUpdate();
        }
    }

    /** Lịch sử ca (mới nhất trước) — màn Chốt ca. */
    public static ArrayList<CaThuNgan> layLichSu(int gioiHan) {
        String sql = "SELECT " + COLS + " FROM CaThuNgan c"
                + " JOIN NhanVien nv ON nv.maNhanVien = c.maNhanVien"
                + " ORDER BY c.thoiGianMo DESC OFFSET 0 ROWS FETCH NEXT ? ROWS ONLY";
        ArrayList<CaThuNgan> ds = new ArrayList<>();
        Connection con = ConnectDB.getInstance().getConnection();
        try (PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setInt(1, Math.max(1, gioiHan));
            try (ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    ds.add(map(rs));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi truy vấn lịch sử ca", ex);
        }
        return ds;
    }

    private CaThuNgan_DAO() { }
}
