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
}
