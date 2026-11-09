package entity;

import java.time.LocalDate;

public class NhanVien {
    private String maNhanVien;
    private String hoTen;
    private String soDienThoai;
    private LocalDate ngaySinh;
    private Boolean gioiTinh;      // true = nam
    private String cccd;
    private ChucVu chucVu;
    private boolean conLamViec;
    private TaiKhoan taiKhoan;     // lien ket doi tuong OOP dung hien thi

    public NhanVien() { }

    public NhanVien(String maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public NhanVien(String maNhanVien, String hoTen, String soDienThoai, LocalDate ngaySinh,
                    Boolean gioiTinh, String cccd, ChucVu chucVu, boolean conLamViec) {
        this.maNhanVien = maNhanVien;
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.cccd = cccd;
        this.chucVu = chucVu;
        this.conLamViec = conLamViec;
    }

    public String getMaNhanVien() { return maNhanVien; }
    public void setMaNhanVien(String maNhanVien) { this.maNhanVien = maNhanVien; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }
    public LocalDate getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(LocalDate ngaySinh) { this.ngaySinh = ngaySinh; }
    public Boolean getGioiTinh() { return gioiTinh; }
    public void setGioiTinh(Boolean gioiTinh) { this.gioiTinh = gioiTinh; }
    public String getCccd() { return cccd; }
    public void setCccd(String cccd) { this.cccd = cccd; }
    public ChucVu getChucVu() { return chucVu; }
    public void setChucVu(ChucVu chucVu) { this.chucVu = chucVu; }
    public boolean isConLamViec() { return conLamViec; }
    public void setConLamViec(boolean conLamViec) { this.conLamViec = conLamViec; }

    public TaiKhoan getTaiKhoan() { return taiKhoan; }
    public void setTaiKhoan(TaiKhoan taiKhoan) { this.taiKhoan = taiKhoan; }

    // Phương thức dẫn xuất OOP
    public String getGioiTinhString() { return gioiTinh == null ? "" : (gioiTinh ? "Nam" : "Nữ"); }
    public String getTrangThaiLamViecString() { return conLamViec ? "Đang làm việc" : "Đã nghỉ việc"; }
    public String getTenDangNhap() { return taiKhoan != null ? taiKhoan.getTenDangNhap() : null; }
    public String getTrangThaiTaiKhoanString() {
        if (taiKhoan == null) return "Chưa tạo tài khoản";
        return taiKhoan.getTrangThai() == TrangThaiTaiKhoan.HOAT_DONG ? "Hoạt động" : "Bị khóa";
    }
    public boolean hasTaiKhoan() { return taiKhoan != null; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof NhanVien other)) return false;
        return java.util.Objects.equals(maNhanVien, other.maNhanVien);
    }

    @Override
    public int hashCode() { return java.util.Objects.hash(maNhanVien); }

    @Override
    public String toString() { return hoTen; }
}
