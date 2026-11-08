package entity;

import java.math.BigDecimal;

/**
 * Chỉ định dịch vụ — entity OOP theo mẫu N11: quan hệ với Dịch vụ biểu diễn bằng đối tượng
 * (không khai báo lại mã dịch vụ dạng String). soRang ghi rõ răng điều trị theo hệ FDI
 * (Góp ý GV 2/7), NULL với dịch vụ không theo răng.
 */
public class ChiDinhDichVu {
    private String maPhieuKham;         // id phieu kham cha (khóa liên kết truy vấn)
    private DichVu dichVu;              // quan he voi Dich Vu
    private BigDecimal donGia;          // cot hien thi (niem yet / 0 voi phi kham da thu)
    private String soRang;              // so rang bi dieu tri (FDI, vd "36,37")

    public ChiDinhDichVu() { }

    public ChiDinhDichVu(String maPhieuKham, String maDichVu) {
        this.maPhieuKham = maPhieuKham;
        this.dichVu = new DichVu(maDichVu);
    }

    public String getMaPhieuKham() { return maPhieuKham; }
    public void setMaPhieuKham(String maPhieuKham) { this.maPhieuKham = maPhieuKham; }

    // ---- Quan he DichVu ----
    public DichVu getDichVu() { return dichVu; }
    public void setDichVu(DichVu dichVu) { this.dichVu = dichVu; }
    public String getMaDichVu() { return dichVu != null ? dichVu.getMaDichVu() : null; }
    public void setMaDichVu(String maDichVu) { this.dichVu = new DichVu(maDichVu); }
    public String getTenDichVu() { return dichVu != null ? dichVu.getTenDichVu() : null; }
    public String getDichVuString() {
        return dichVu == null ? "" : dichVu.getMaDichVu() + " - " + dichVu.getTenDichVu();
    }

    public BigDecimal getDonGia() { return donGia; }
    public void setDonGia(BigDecimal donGia) { this.donGia = donGia; }
    public String getSoRang() { return soRang; }
    public void setSoRang(String soRang) { this.soRang = soRang; }
}
