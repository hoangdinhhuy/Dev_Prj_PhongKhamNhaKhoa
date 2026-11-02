package entity;

public class HoSoBenhAn {
    private String maHoSo;
    private String tienSuBenhLy;
    private String ghiChuChuyenMon;
    private String maBenhNhan;
    private BenhNhan benhNhan;   // hien thi

    public HoSoBenhAn() { }

    public HoSoBenhAn(String maHoSo, String tienSuBenhLy, String ghiChuChuyenMon, String maBenhNhan) {
        this.maHoSo = maHoSo;
        this.tienSuBenhLy = tienSuBenhLy;
        this.ghiChuChuyenMon = ghiChuChuyenMon;
        this.maBenhNhan = maBenhNhan;
    }

    public String getMaHoSo() { return maHoSo; }
    public void setMaHoSo(String maHoSo) { this.maHoSo = maHoSo; }
    public String getTienSuBenhLy() { return tienSuBenhLy; }
    public void setTienSuBenhLy(String tienSuBenhLy) { this.tienSuBenhLy = tienSuBenhLy; }
    public String getGhiChuChuyenMon() { return ghiChuChuyenMon; }
    public void setGhiChuChuyenMon(String ghiChuChuyenMon) { this.ghiChuChuyenMon = ghiChuChuyenMon; }
    public String getMaBenhNhan() { return maBenhNhan; }
    public void setMaBenhNhan(String maBenhNhan) { this.maBenhNhan = maBenhNhan; }
    public BenhNhan getBenhNhan() { return benhNhan; }
    public void setBenhNhan(BenhNhan benhNhan) { this.benhNhan = benhNhan; }
}
