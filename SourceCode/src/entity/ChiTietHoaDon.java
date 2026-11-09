package entity;

import java.math.BigDecimal;

/**
 * Chi tiết hóa đơn — entity OOP theo mẫu N11: quan hệ với Dịch vụ biểu diễn bằng đối tượng
 * (không khai báo lại mã dịch vụ dạng String). thanhTien là thuộc tính dẫn xuất: chỉ
 * constructor tính (donGia × soLuong), không có setter.
 */
public class ChiTietHoaDon {
    private String maHoaDon;            // id hoa don cha (khóa liên kết truy vấn)
    private DichVu dichVu;              // quan he voi Dich Vu ("San pham" — Gop y GV P2-2)
    private int soLuong;
    private BigDecimal donGia;
    private BigDecimal thanhTien;       // dan xuat: chi constructor tinh, khong co setter

    public ChiTietHoaDon() { }

    public ChiTietHoaDon(String maHoaDon, String maDichVu, int soLuong, BigDecimal donGia) {
        this.maHoaDon = maHoaDon;
        this.dichVu = new DichVu(maDichVu);
        this.soLuong = soLuong;
        this.donGia = donGia;
        tinhThanhTien();
    }

    private void tinhThanhTien() {
        this.thanhTien = donGia == null ? null : donGia.multiply(BigDecimal.valueOf(soLuong));
    }

    public String getMaHoaDon() { return maHoaDon; }
    public void setMaHoaDon(String maHoaDon) { this.maHoaDon = maHoaDon; }
    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; tinhThanhTien(); }
    public BigDecimal getDonGia() { return donGia; }
    public void setDonGia(BigDecimal donGia) { this.donGia = donGia; tinhThanhTien(); }

    // ---- Quan he DichVu ----
    public DichVu getDichVu() { return dichVu; }
    public void setDichVu(DichVu dichVu) { this.dichVu = dichVu; }
    public String getMaDichVu() { return dichVu != null ? dichVu.getMaDichVu() : null; }
    public void setMaDichVu(String maDichVu) { this.dichVu = new DichVu(maDichVu); }
    public String getTenDichVu() { return dichVu != null ? dichVu.getTenDichVu() : null; }
    public String getDichVuString() {
        return dichVu == null ? "" : dichVu.getMaDichVu() + " - " + dichVu.getTenDichVu();
    }

    public BigDecimal getThanhTien() { return thanhTien; }
}
