package entity;

import java.time.LocalDate;

public class PhieuKham {
    private String maPhieuKham;
    private LocalDate ngayKham;
    private int soThuTu;
    private String lyDoKham;
    private String chanDoan;
    private String ketQuaDieuTri;
    private String ghiChuThuocSuDung;
    private String ghiChuThuocTuMua;
    private TrangThaiPhieuKham trangThai;
    private String maBenhNhan;
    private String maNhanVien;       // bac si
    private String maPhong;
    private String maHoSo;
    private String maHoaDonPhiKham;

    // Cot hien thi (JOIN, khong luu DB)
    private String tenBenhNhan;
    private String tenBacSi;
    private String tenPhong;

    public PhieuKham() { }

    public PhieuKham(String maPhieuKham) { this.maPhieuKham = maPhieuKham; }

    public String getMaPhieuKham() { return maPhieuKham; }
    public void setMaPhieuKham(String maPhieuKham) { this.maPhieuKham = maPhieuKham; }
    public LocalDate getNgayKham() { return ngayKham; }
    public void setNgayKham(LocalDate ngayKham) { this.ngayKham = ngayKham; }
    public int getSoThuTu() { return soThuTu; }
    public void setSoThuTu(int soThuTu) { this.soThuTu = soThuTu; }
    public String getLyDoKham() { return lyDoKham; }
    public void setLyDoKham(String lyDoKham) { this.lyDoKham = lyDoKham; }
    public String getChanDoan() { return chanDoan; }
    public void setChanDoan(String chanDoan) { this.chanDoan = chanDoan; }
    public String getKetQuaDieuTri() { return ketQuaDieuTri; }
    public void setKetQuaDieuTri(String ketQuaDieuTri) { this.ketQuaDieuTri = ketQuaDieuTri; }
    public String getGhiChuThuocSuDung() { return ghiChuThuocSuDung; }
    public void setGhiChuThuocSuDung(String ghiChuThuocSuDung) { this.ghiChuThuocSuDung = ghiChuThuocSuDung; }
    public String getGhiChuThuocTuMua() { return ghiChuThuocTuMua; }
    public void setGhiChuThuocTuMua(String ghiChuThuocTuMua) { this.ghiChuThuocTuMua = ghiChuThuocTuMua; }
    public TrangThaiPhieuKham getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiPhieuKham trangThai) { this.trangThai = trangThai; }
    public String getMaBenhNhan() { return maBenhNhan; }
    public void setMaBenhNhan(String maBenhNhan) { this.maBenhNhan = maBenhNhan; }
    public String getMaNhanVien() { return maNhanVien; }
    public void setMaNhanVien(String maNhanVien) { this.maNhanVien = maNhanVien; }
    public String getMaPhong() { return maPhong; }
    public void setMaPhong(String maPhong) { this.maPhong = maPhong; }
    public String getMaHoSo() { return maHoSo; }
    public void setMaHoSo(String maHoSo) { this.maHoSo = maHoSo; }
    public String getMaHoaDonPhiKham() { return maHoaDonPhiKham; }
    public void setMaHoaDonPhiKham(String maHoaDonPhiKham) { this.maHoaDonPhiKham = maHoaDonPhiKham; }
    public String getTenBenhNhan() { return tenBenhNhan; }
    public void setTenBenhNhan(String tenBenhNhan) { this.tenBenhNhan = tenBenhNhan; }
    public String getTenBacSi() { return tenBacSi; }
    public void setTenBacSi(String tenBacSi) { this.tenBacSi = tenBacSi; }
    public String getTenPhong() { return tenPhong; }
    public void setTenPhong(String tenPhong) { this.tenPhong = tenPhong; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PhieuKham other)) return false;
        return java.util.Objects.equals(maPhieuKham, other.maPhieuKham);
    }

    @Override
    public int hashCode() { return java.util.Objects.hash(maPhieuKham); }
}
