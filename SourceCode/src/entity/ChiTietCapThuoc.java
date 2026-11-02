package entity;

public class ChiTietCapThuoc {
    private String maPhieuKham;
    private String maThuoc;
    private int soLuong;
    private String ghiChuCapThuoc;

    // Cot hien thi (JOIN Thuoc)
    private String tenThuoc;
    private String donViTinh;

    public ChiTietCapThuoc() { }

    public ChiTietCapThuoc(String maPhieuKham, String maThuoc, int soLuong, String ghiChuCapThuoc) {
        this.maPhieuKham = maPhieuKham;
        this.maThuoc = maThuoc;
        this.soLuong = soLuong;
        this.ghiChuCapThuoc = ghiChuCapThuoc;
    }

    public String getMaPhieuKham() { return maPhieuKham; }
    public void setMaPhieuKham(String maPhieuKham) { this.maPhieuKham = maPhieuKham; }
    public String getMaThuoc() { return maThuoc; }
    public void setMaThuoc(String maThuoc) { this.maThuoc = maThuoc; }
    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
    public String getGhiChuCapThuoc() { return ghiChuCapThuoc; }
    public void setGhiChuCapThuoc(String ghiChuCapThuoc) { this.ghiChuCapThuoc = ghiChuCapThuoc; }
    public String getTenThuoc() { return tenThuoc; }
    public void setTenThuoc(String tenThuoc) { this.tenThuoc = tenThuoc; }
    public String getDonViTinh() { return donViTinh; }
    public void setDonViTinh(String donViTinh) { this.donViTinh = donViTinh; }
}
