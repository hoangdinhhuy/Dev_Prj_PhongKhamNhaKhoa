package entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Hóa đơn — entity OOP theo mẫu chuẩn N11: quan hệ biểu diễn bằng đối tượng
 * (BenhNhan = khách hàng, NhanVien = nhân viên lập, PhieuKham = lượt khám cuối lượt),
 * KHÔNG khai báo lại khóa ngoại dạng String. Thuộc tính dẫn xuất (tongTien) KHÔNG cho phép
 * set tùy ý — chỉ được tính lại qua logic của chính entity (themChiTiet).
 */
public class HoaDon {
    private String maHoaDon;
    private LocalDateTime ngayThu;
    private String hinhThucThanhToan;   // TIEN_MAT | CHUYEN_KHOAN
    private boolean daThanhToan;
    private BigDecimal tienKhachDua;

    // Quan he doi tuong
    private BenhNhan benhNhan;          // khach hang
    private NhanVien nhanVien;          // nhan vien lap hoa don
    private PhieuKham phieuKham;        // luot kham (NULL voi hoa don phi kham)

    // Thuoc tinh dan xuat: chi entity duoc phep tinh lai, ben ngoai chi doc
    private final List<ChiTietHoaDon> chiTiets = new ArrayList<>();
    private BigDecimal tongTien = BigDecimal.ZERO;

    public HoaDon() { }

    public HoaDon(String maHoaDon) { this.maHoaDon = maHoaDon; }

    /** Them chi tiet vao hoa don — tong tien duoc TINH LAI tu dong, khong ai set gia tri ngoai. */
    public void themChiTiet(ChiTietHoaDon ct) {
        if (ct == null) return;
        chiTiets.add(ct);
        tinhTongTien();
    }

    /** Cong thuc dan xuat: tongTien = SUM(thanhTien cua cac chi tiet). */
    private void tinhTongTien() {
        BigDecimal tong = BigDecimal.ZERO;
        for (ChiTietHoaDon ct : chiTiets) {
            if (ct.getThanhTien() != null) {
                tong = tong.add(ct.getThanhTien());
            }
        }
        tongTien = tong;
    }

    public List<ChiTietHoaDon> getChiTiets() { return chiTiets; }
    public BigDecimal getTongTien() { return tongTien; }

    public String getMaHoaDon() { return maHoaDon; }
    public void setMaHoaDon(String maHoaDon) { this.maHoaDon = maHoaDon; }
    public LocalDateTime getNgayThu() { return ngayThu; }
    public void setNgayThu(LocalDateTime ngayThu) { this.ngayThu = ngayThu; }
    public String getHinhThucThanhToan() { return hinhThucThanhToan; }
    public void setHinhThucThanhToan(String hinhThucThanhToan) { this.hinhThucThanhToan = hinhThucThanhToan; }
    public boolean isDaThanhToan() { return daThanhToan; }
    public void setDaThanhToan(boolean daThanhToan) { this.daThanhToan = daThanhToan; }
    public BigDecimal getTienKhachDua() { return tienKhachDua; }
    public void setTienKhachDua(BigDecimal tienKhachDua) { this.tienKhachDua = tienKhachDua; }
    public String getHinhThucString() { return "TIEN_MAT".equals(hinhThucThanhToan) ? "Tiền mặt" : "Chuyển khoản"; }

    // ---- Quan he BenhNhan (khach hang) ----
    public BenhNhan getBenhNhan() { return benhNhan; }
    public void setBenhNhan(BenhNhan benhNhan) { this.benhNhan = benhNhan; }
    public String getMaBenhNhan() { return benhNhan != null ? benhNhan.getMaBenhNhan() : null; }
    public void setMaBenhNhan(String maBenhNhan) { this.benhNhan = new BenhNhan(maBenhNhan); }
    public String getBenhNhanString() {
        return benhNhan == null ? "" : benhNhan.getMaBenhNhan() + " - " + benhNhan.getHoTen();
    }
    public String getTenBenhNhan() { return benhNhan != null ? benhNhan.getHoTen() : null; }

    // ---- Quan he NhanVien (nhan vien lap) ----
    public NhanVien getNhanVien() { return nhanVien; }
    public void setNhanVien(NhanVien nhanVien) { this.nhanVien = nhanVien; }
    public String getMaNhanVien() { return nhanVien != null ? nhanVien.getMaNhanVien() : null; }
    public void setMaNhanVien(String maNhanVien) { this.nhanVien = new NhanVien(maNhanVien); }
    public String getNhanVienString() {
        return nhanVien == null ? "" : nhanVien.getMaNhanVien() + " - " + nhanVien.getHoTen();
    }

    // ---- Quan he PhieuKham (luot kham cuoi luot) ----
    public PhieuKham getPhieuKham() { return phieuKham; }
    public void setPhieuKham(PhieuKham phieuKham) { this.phieuKham = phieuKham; }
    public String getMaPhieuKham() { return phieuKham != null ? phieuKham.getMaPhieuKham() : null; }
    public void setMaPhieuKham(String maPhieuKham) {
        this.phieuKham = maPhieuKham == null ? null : new PhieuKham(maPhieuKham);
    }

    /** Thuoc tinh dan xuat: tienThua = tienKhachDua - tongTien (chi doc, khong co setter). */
    public BigDecimal getTienThua() {
        if (tienKhachDua == null || tongTien == null) return null;
        return tienKhachDua.subtract(tongTien);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof HoaDon other)) return false;
        return java.util.Objects.equals(maHoaDon, other.maHoaDon);
    }

    @Override
    public int hashCode() { return java.util.Objects.hash(maHoaDon); }
}
