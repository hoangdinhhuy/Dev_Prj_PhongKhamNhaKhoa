package entity;

/** Phiên đăng nhập - chi duoc tao trong TaiKhoan_BUS.dangNhap, KHONG tao tu form. */
public class SessionContext {
    private final String maNhanVien;
    private final String hoTen;
    private final ChucVu chucVu;

    public SessionContext(String maNhanVien, String hoTen, ChucVu chucVu) {
        this.maNhanVien = maNhanVien;
        this.hoTen = hoTen;
        this.chucVu = chucVu;
    }

    public String getMaNhanVien() { return maNhanVien; }
    public String getHoTen() { return hoTen; }
    public ChucVu getChucVu() { return chucVu; }

    @Override
    public String toString() {
        return hoTen + " (" + chucVu + ")";
    }
}
