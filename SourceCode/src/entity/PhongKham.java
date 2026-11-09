package entity;

public class PhongKham {
    private String maPhong;
    private String tenPhong;
    private boolean dangNhanBenh;
    private String maBacSi;
    private String maPhuTa;

    public PhongKham() { }

    public PhongKham(String maPhong) { this.maPhong = maPhong; }

    public PhongKham(String maPhong, String tenPhong, boolean dangNhanBenh) {
        this.maPhong = maPhong;
        this.tenPhong = tenPhong;
        this.dangNhanBenh = dangNhanBenh;
    }

    public PhongKham(String maPhong, String tenPhong, boolean dangNhanBenh,
                     String maBacSi, String maPhuTa) {
        this(maPhong, tenPhong, dangNhanBenh);
        this.maBacSi = maBacSi;
        this.maPhuTa = maPhuTa;
    }

    public String getMaPhong() { return maPhong; }
    public void setMaPhong(String maPhong) { this.maPhong = maPhong; }
    public String getTenPhong() { return tenPhong; }
    public void setTenPhong(String tenPhong) { this.tenPhong = tenPhong; }
    public boolean isDangNhanBenh() { return dangNhanBenh; }
    public void setDangNhanBenh(boolean dangNhanBenh) { this.dangNhanBenh = dangNhanBenh; }
    public String getMaBacSi() { return maBacSi; }
    public void setMaBacSi(String maBacSi) { this.maBacSi = maBacSi; }
    public String getMaPhuTa() { return maPhuTa; }
    public void setMaPhuTa(String maPhuTa) { this.maPhuTa = maPhuTa; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PhongKham other)) return false;
        return java.util.Objects.equals(maPhong, other.maPhong);
    }

    @Override
    public int hashCode() { return java.util.Objects.hash(maPhong); }

    @Override
    public String toString() { return maPhong + " - " + tenPhong + (dangNhanBenh ? " (đang nhận bệnh)" : ""); }
}
