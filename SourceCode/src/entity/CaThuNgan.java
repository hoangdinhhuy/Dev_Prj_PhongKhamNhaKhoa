package entity;

import java.time.LocalDateTime;

/**
 * Ca làm việc của thu ngân (mẫu quản lý ca POS): Mở ca → thu trong ca → CHỐT CA
 * (snapshot doanh thu + xác nhận) → nhân viên sau Nhận ca.
 */
public class CaThuNgan {

    public static final String DANG_MO = "DANG_MO";
    public static final String DA_CHOT = "DA_CHOT";

    private String maCa;
    private NhanVien nhanVien;              // nhân viên mở ca
    private LocalDateTime thoiGianMo;
    private LocalDateTime thoiGianChot;     // NULL = đang mở
    private java.math.BigDecimal doanhThuDichVu;  // snapshot lúc chốt
    private java.math.BigDecimal phiThuHo;        // snapshot lúc chốt
    private String trangThai;               // DANG_MO | DA_CHOT

    public CaThuNgan() { }

    public CaThuNgan(String maCa) { this.maCa = maCa; }

    public String getMaCa() { return maCa; }
    public void setMaCa(String maCa) { this.maCa = maCa; }

    public NhanVien getNhanVien() { return nhanVien; }
    public void setNhanVien(NhanVien nhanVien) { this.nhanVien = nhanVien; }
    public String getMaNhanVien() { return nhanVien != null ? nhanVien.getMaNhanVien() : null; }
    public void setMaNhanVien(String maNhanVien) { this.nhanVien = new NhanVien(maNhanVien); }
    public String getNhanVienString() {
        return nhanVien == null ? "" : nhanVien.getMaNhanVien() + " - " + nhanVien.getHoTen();
    }

    public LocalDateTime getThoiGianMo() { return thoiGianMo; }
    public void setThoiGianMo(LocalDateTime thoiGianMo) { this.thoiGianMo = thoiGianMo; }
    public LocalDateTime getThoiGianChot() { return thoiGianChot; }
    public void setThoiGianChot(LocalDateTime thoiGianChot) { this.thoiGianChot = thoiGianChot; }

    public java.math.BigDecimal getDoanhThuDichVu() { return doanhThuDichVu; }
    public void setDoanhThuDichVu(java.math.BigDecimal doanhThuDichVu) {
        this.doanhThuDichVu = doanhThuDichVu;
    }
    public java.math.BigDecimal getPhiThuHo() { return phiThuHo; }
    public void setPhiThuHo(java.math.BigDecimal phiThuHo) { this.phiThuHo = phiThuHo; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public boolean isDangMo() { return DANG_MO.equals(trangThai); }
}
