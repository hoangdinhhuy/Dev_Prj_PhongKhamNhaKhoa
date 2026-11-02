package entity;

public class Thuoc {
    private String maThuoc;
    private String tenThuoc;
    private String donViTinh;

    public Thuoc() { }

    public Thuoc(String maThuoc) { this.maThuoc = maThuoc; }

    public Thuoc(String maThuoc, String tenThuoc, String donViTinh) {
        this.maThuoc = maThuoc;
        this.tenThuoc = tenThuoc;
        this.donViTinh = donViTinh;
    }

    public String getMaThuoc() { return maThuoc; }
    public void setMaThuoc(String maThuoc) { this.maThuoc = maThuoc; }
    public String getTenThuoc() { return tenThuoc; }
    public void setTenThuoc(String tenThuoc) { this.tenThuoc = tenThuoc; }
    public String getDonViTinh() { return donViTinh; }
    public void setDonViTinh(String donViTinh) { this.donViTinh = donViTinh; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Thuoc other)) return false;
        return java.util.Objects.equals(maThuoc, other.maThuoc);
    }

    @Override
    public int hashCode() { return java.util.Objects.hash(maThuoc); }

    @Override
    public String toString() { return maThuoc + " - " + tenThuoc + " (" + donViTinh + ")"; }
}
