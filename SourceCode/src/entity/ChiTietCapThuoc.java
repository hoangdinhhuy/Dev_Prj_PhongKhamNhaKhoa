package entity;

/**
 * Chi tiết cấp thuốc — entity OOP theo mẫu N11: quan hệ với Thuốc biểu diễn bằng đối tượng
 * (không khai báo lại mã thuốc dạng String, không giữ cột hiển thị riêng).
 */
public class ChiTietCapThuoc {
    private String maPhieuKham;         // id phieu kham cha (khóa liên kết truy vấn)
    private Thuoc thuoc;                // quan he voi Thuoc
    private int soLuong;
    private String ghiChuCapThuoc;

    public ChiTietCapThuoc() { }

    public ChiTietCapThuoc(String maPhieuKham, String maThuoc, int soLuong, String ghiChuCapThuoc) {
        this.maPhieuKham = maPhieuKham;
        this.thuoc = new Thuoc(maThuoc);
        this.soLuong = soLuong;
        this.ghiChuCapThuoc = ghiChuCapThuoc;
    }

    public String getMaPhieuKham() { return maPhieuKham; }
    public void setMaPhieuKham(String maPhieuKham) { this.maPhieuKham = maPhieuKham; }
    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
    public String getGhiChuCapThuoc() { return ghiChuCapThuoc; }
    public void setGhiChuCapThuoc(String ghiChuCapThuoc) { this.ghiChuCapThuoc = ghiChuCapThuoc; }

    // ---- Quan he Thuoc ----
    public Thuoc getThuoc() { return thuoc; }
    public void setThuoc(Thuoc thuoc) { this.thuoc = thuoc; }
    public String getMaThuoc() { return thuoc != null ? thuoc.getMaThuoc() : null; }
    public void setMaThuoc(String maThuoc) { this.thuoc = new Thuoc(maThuoc); }
    public String getTenThuoc() { return thuoc != null ? thuoc.getTenThuoc() : null; }
    public String getDonViTinh() { return thuoc != null ? thuoc.getDonViTinh() : null; }
    public String getThuocString() {
        return thuoc == null ? "" : thuoc.getMaThuoc() + " - " + thuoc.getTenThuoc();
    }
}
