package entity;

import java.math.BigDecimal;

public class ChiTietHoaDon {
    private String maHoaDon;
    private String maDichVu;
    private int soLuong;
    private BigDecimal donGia;
    private BigDecimal thanhTien;   // PERSISTED trong DB, tinh lai khi doc

    // Cot hien thi
    private String tenDichVu;

    public ChiTietHoaDon() { }

    public ChiTietHoaDon(String maHoaDon, String maDichVu, int soLuong, BigDecimal donGia) {
        this.maHoaDon = maHoaDon;
        this.maDichVu = maDichVu;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.thanhTien = donGia.multiply(BigDecimal.valueOf(soLuong));
    }

    public String getMaHoaDon() { return maHoaDon; }
    public void setMaHoaDon(String maHoaDon) { this.maHoaDon = maHoaDon; }
    public String getMaDichVu() { return maDichVu; }
    public void setMaDichVu(String maDichVu) { this.maDichVu = maDichVu; }
    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
    public BigDecimal getDonGia() { return donGia; }
    public void setDonGia(BigDecimal donGia) { this.donGia = donGia; }
    public BigDecimal getThanhTien() { return thanhTien; }
    public void setThanhTien(BigDecimal thanhTien) { this.thanhTien = thanhTien; }
    public String getTenDichVu() { return tenDichVu; }
    public void setTenDichVu(String tenDichVu) { this.tenDichVu = tenDichVu; }
}
