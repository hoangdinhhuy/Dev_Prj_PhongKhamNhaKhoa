package entity;

public class TaiKhoan {
    private String tenDangNhap;
    private String matKhau;      // hash SHA-256
    private TrangThaiTaiKhoan trangThai;
    private String maNhanVien;
    private NhanVien nhanVien;   // hien thi, khong luu DB

    public TaiKhoan() { }

    public TaiKhoan(String tenDangNhap, String matKhau, TrangThaiTaiKhoan trangThai, String maNhanVien) {
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.trangThai = trangThai;
        this.maNhanVien = maNhanVien;
    }

    public String getTenDangNhap() { return tenDangNhap; }
    public void setTenDangNhap(String tenDangNhap) { this.tenDangNhap = tenDangNhap; }
    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }
    public TrangThaiTaiKhoan getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiTaiKhoan trangThai) { this.trangThai = trangThai; }
    public String getMaNhanVien() { return maNhanVien; }
    public void setMaNhanVien(String maNhanVien) { this.maNhanVien = maNhanVien; }
    public NhanVien getNhanVien() { return nhanVien; }
    public void setNhanVien(NhanVien nhanVien) { this.nhanVien = nhanVien; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof TaiKhoan other)) return false;
        return java.util.Objects.equals(tenDangNhap, other.tenDangNhap);
    }

    @Override
    public int hashCode() { return java.util.Objects.hash(tenDangNhap); }
}
