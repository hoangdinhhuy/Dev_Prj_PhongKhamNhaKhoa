package entity;

import java.math.BigDecimal;

public class DichVu {
    private String maDichVu;
    private String tenDichVu;
    private BigDecimal donGiaNiemYet;
    private TrangThaiDichVu trangThai;

    public DichVu() { }

    public DichVu(String maDichVu) { this.maDichVu = maDichVu; }

    public DichVu(String maDichVu, String tenDichVu, BigDecimal donGiaNiemYet, TrangThaiDichVu trangThai) {
        this.maDichVu = maDichVu;
        this.tenDichVu = tenDichVu;
        this.donGiaNiemYet = donGiaNiemYet;
        this.trangThai = trangThai;
    }

    public String getMaDichVu() { return maDichVu; }
    public void setMaDichVu(String maDichVu) { this.maDichVu = maDichVu; }
    public String getTenDichVu() { return tenDichVu; }
    public void setTenDichVu(String tenDichVu) { this.tenDichVu = tenDichVu; }
    public BigDecimal getDonGiaNiemYet() { return donGiaNiemYet; }
    public void setDonGiaNiemYet(BigDecimal donGiaNiemYet) { this.donGiaNiemYet = donGiaNiemYet; }
    public TrangThaiDichVu getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiDichVu trangThai) { this.trangThai = trangThai; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof DichVu other)) return false;
        return java.util.Objects.equals(maDichVu, other.maDichVu);
    }

    @Override
    public int hashCode() { return java.util.Objects.hash(maDichVu); }

    @Override
    public String toString() { return maDichVu + " - " + tenDichVu; }
}
