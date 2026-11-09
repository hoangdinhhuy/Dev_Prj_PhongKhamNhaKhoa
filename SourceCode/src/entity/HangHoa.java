package entity;

public class HangHoa {
    private String maHangHoa;
    private String tenHangHoa;
    private String donViTinh;
    private int soLuongTon;
    private int mucTonToiThieu;

    public HangHoa(String maHangHoa, String tenHangHoa, String donViTinh, int soLuongTon, int mucTonToiThieu) {
        this.maHangHoa = maHangHoa;
        this.tenHangHoa = tenHangHoa;
        this.donViTinh = donViTinh;
        this.soLuongTon = soLuongTon;
        this.mucTonToiThieu = mucTonToiThieu;
    }

    public String getMaHangHoa() { return maHangHoa; }
    public String getTenHangHoa() { return tenHangHoa; }
    public String getDonViTinh() { return donViTinh; }
    public int getSoLuongTon() { return soLuongTon; }
    public int getMucTonToiThieu() { return mucTonToiThieu; }

    // Phương thức nghiệp vụ OOP (thuộc tính dẫn xuất, không có setter)
    public boolean isCanBoSung() {
        return soLuongTon <= mucTonToiThieu;
    }

    public String getTinhTrangTonKho() {
        return isCanBoSung() ? "Cần bổ sung" : "Đủ hàng";
    }

    public int getSoLuongThieu() {
        return Math.max(0, mucTonToiThieu - soLuongTon);
    }
}
