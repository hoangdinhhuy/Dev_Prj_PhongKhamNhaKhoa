package entity;

import java.time.LocalDate;

/**
 * Phiếu khám — entity OOP theo mẫu chuẩn N11: KHÔNG khai báo lại khóa ngoại dạng String,
 * quan hệ biểu diễn bằng đối tượng (BenhNhan, NhanVien = bác sĩ, PhongKham, HoaDon phí khám).
 * Mã liên quan đọc qua delegate getter; setter nhận mã tạo đối tượng "shell" (chỉ mang mã),
 * khi nạp từ JOIN object được điền đầy đủ.
 */
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
    private String maHoSo;                     // tham chiếu hồ sơ bệnh án (chỉ mã)
    private String phacDieuTri;                // phac do dieu tri (Gop y GV 2/7)
    private LocalDate ngayHenTiep;             // ngay hen tai kham tiep theo
    private int lanThu;                        // (Gop y GV P1-7) lan dieu tri thu N — cot hien thi

    // Quan he doi tuong
    private BenhNhan benhNhan;
    private NhanVien nhanVien;                 // bac si phu trach
    private PhongKham phong;
    private HoaDon hoaDonPhiKham;              // hoa don phi kham da thu truoc khi lap phieu

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
    public String getMaHoSo() { return maHoSo; }
    public void setMaHoSo(String maHoSo) { this.maHoSo = maHoSo; }
    public String getPhacDieuTri() { return phacDieuTri; }
    public void setPhacDieuTri(String phacDieuTri) { this.phacDieuTri = phacDieuTri; }
    public LocalDate getNgayHenTiep() { return ngayHenTiep; }
    public void setNgayHenTiep(LocalDate ngayHenTiep) { this.ngayHenTiep = ngayHenTiep; }
    public int getLanThu() { return lanThu; }
    public void setLanThu(int lanThu) { this.lanThu = lanThu; }

    // ---- Quan he BenhNhan ----
    public BenhNhan getBenhNhan() { return benhNhan; }
    public void setBenhNhan(BenhNhan benhNhan) { this.benhNhan = benhNhan; }
    public String getMaBenhNhan() { return benhNhan != null ? benhNhan.getMaBenhNhan() : null; }
    public void setMaBenhNhan(String maBenhNhan) { this.benhNhan = new BenhNhan(maBenhNhan); }
    public String getBenhNhanString() {
        return benhNhan == null ? "" : benhNhan.getMaBenhNhan() + " - " + benhNhan.getHoTen();
    }
    public String getTenBenhNhan() { return benhNhan != null ? benhNhan.getHoTen() : null; }

    // ---- Quan he NhanVien (bac si) ----
    public NhanVien getNhanVien() { return nhanVien; }
    public void setNhanVien(NhanVien nhanVien) { this.nhanVien = nhanVien; }
    public String getMaNhanVien() { return nhanVien != null ? nhanVien.getMaNhanVien() : null; }
    public void setMaNhanVien(String maNhanVien) { this.nhanVien = new NhanVien(maNhanVien); }
    public String getTenBacSi() { return nhanVien != null ? nhanVien.getHoTen() : null; }

    // ---- Quan he PhongKham ----
    public PhongKham getPhong() { return phong; }
    public void setPhong(PhongKham phong) { this.phong = phong; }
    public String getMaPhong() { return phong != null ? phong.getMaPhong() : null; }
    public void setMaPhong(String maPhong) { this.phong = new PhongKham(maPhong); }
    public String getTenPhong() { return phong != null ? phong.getTenPhong() : null; }

    // ---- Quan he HoaDon (phi kham da thu) ----
    public HoaDon getHoaDonPhiKham() { return hoaDonPhiKham; }
    public void setHoaDonPhiKham(HoaDon hoaDonPhiKham) { this.hoaDonPhiKham = hoaDonPhiKham; }
    public String getMaHoaDonPhiKham() { return hoaDonPhiKham != null ? hoaDonPhiKham.getMaHoaDon() : null; }
    public void setMaHoaDonPhiKham(String maHoaDonPhiKham) {
        this.hoaDonPhiKham = maHoaDonPhiKham == null ? null : new HoaDon(maHoaDonPhiKham);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PhieuKham other)) return false;
        return java.util.Objects.equals(maPhieuKham, other.maPhieuKham);
    }

    @Override
    public int hashCode() { return java.util.Objects.hash(maPhieuKham); }
}
