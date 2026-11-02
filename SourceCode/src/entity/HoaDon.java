package entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class HoaDon {
    private String maHoaDon;
    private LocalDateTime ngayThu;
    private String hinhThucThanhToan;   // TIEN_MAT | CHUYEN_KHOAN
    private boolean daThanhToan;
    private BigDecimal tienKhachDua;
    private String maBenhNhan;
    private String maNhanVien;          // le tan
    private String maPhieuKham;         // lien ket hoa don dich vu cuoi luot (NULL voi hoa don phi kham)

    // Cot hien thi
    private String tenBenhNhan;
    private BigDecimal tongTien;

    public HoaDon() { }

    public HoaDon(String maHoaDon) { this.maHoaDon = maHoaDon; }

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
    public String getMaBenhNhan() { return maBenhNhan; }
    public void setMaBenhNhan(String maBenhNhan) { this.maBenhNhan = maBenhNhan; }
    public String getMaNhanVien() { return maNhanVien; }
    public void setMaNhanVien(String maNhanVien) { this.maNhanVien = maNhanVien; }
    public String getMaPhieuKham() { return maPhieuKham; }
    public void setMaPhieuKham(String maPhieuKham) { this.maPhieuKham = maPhieuKham; }
    public String getTenBenhNhan() { return tenBenhNhan; }
    public void setTenBenhNhan(String tenBenhNhan) { this.tenBenhNhan = tenBenhNhan; }
    public BigDecimal getTongTien() { return tongTien; }
    public void setTongTien(BigDecimal tongTien) { this.tongTien = tongTien; }

    public String getHinhThucString() { return "TIEN_MAT".equals(hinhThucThanhToan) ? "Tiền mặt" : "Chuyển khoản"; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof HoaDon other)) return false;
        return java.util.Objects.equals(maHoaDon, other.maHoaDon);
    }

    @Override
    public int hashCode() { return java.util.Objects.hash(maHoaDon); }
}
