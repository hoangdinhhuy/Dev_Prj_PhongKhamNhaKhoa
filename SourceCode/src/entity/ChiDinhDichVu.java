package entity;

import java.math.BigDecimal;

public class ChiDinhDichVu {
    private String maPhieuKham;
    private String maDichVu;

    // Cot hien thi (JOIN DichVu)
    private String tenDichVu;
    private BigDecimal donGia;

    public ChiDinhDichVu() { }

    public ChiDinhDichVu(String maPhieuKham, String maDichVu) {
        this.maPhieuKham = maPhieuKham;
        this.maDichVu = maDichVu;
    }

    public String getMaPhieuKham() { return maPhieuKham; }
    public void setMaPhieuKham(String maPhieuKham) { this.maPhieuKham = maPhieuKham; }
    public String getMaDichVu() { return maDichVu; }
    public void setMaDichVu(String maDichVu) { this.maDichVu = maDichVu; }
    public String getTenDichVu() { return tenDichVu; }
    public void setTenDichVu(String tenDichVu) { this.tenDichVu = tenDichVu; }
    public BigDecimal getDonGia() { return donGia; }
    public void setDonGia(BigDecimal donGia) { this.donGia = donGia; }
}
