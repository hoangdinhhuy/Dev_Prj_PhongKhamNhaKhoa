package entity;

import java.time.LocalDate;

public class BenhNhan {
    private String maBenhNhan;
    private String hoTen;
    private String soDienThoai;
    private String cccd;
    private LocalDate ngaySinh;
    private String diaChi;
    private String tienSuDiUng;

    public BenhNhan() { }

    public BenhNhan(String maBenhNhan) { this.maBenhNhan = maBenhNhan; }

    public BenhNhan(String maBenhNhan, String hoTen, String soDienThoai, String cccd,
                    LocalDate ngaySinh, String diaChi, String tienSuDiUng) {
        this.maBenhNhan = maBenhNhan;
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
        this.cccd = cccd;
        this.ngaySinh = ngaySinh;
        this.diaChi = diaChi;
        this.tienSuDiUng = tienSuDiUng;
    }

    public String getMaBenhNhan() { return maBenhNhan; }
    public void setMaBenhNhan(String maBenhNhan) { this.maBenhNhan = maBenhNhan; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }
    public String getCccd() { return cccd; }
    public void setCccd(String cccd) { this.cccd = cccd; }
    public LocalDate getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(LocalDate ngaySinh) { this.ngaySinh = ngaySinh; }
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    public String getTienSuDiUng() { return tienSuDiUng; }
    public void setTienSuDiUng(String tienSuDiUng) { this.tienSuDiUng = tienSuDiUng; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BenhNhan other)) return false;
        return java.util.Objects.equals(maBenhNhan, other.maBenhNhan);
    }

    @Override
    public int hashCode() { return java.util.Objects.hash(maBenhNhan); }

    @Override
    public String toString() { return maBenhNhan == null ? hoTen : maBenhNhan + " - " + hoTen; }
}
