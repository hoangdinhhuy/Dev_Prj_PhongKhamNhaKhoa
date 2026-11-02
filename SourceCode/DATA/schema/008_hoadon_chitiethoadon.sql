-- 008_hoadon_chitiethoadon.sql (chủ file: Huy)
USE N15_PTUD_DB;
GO
CREATE TABLE HoaDon (
    maHoaDon            varchar(12)   NOT NULL PRIMARY KEY,  -- HDyyMMddNNNN
    ngayThu             datetime2(0)  NOT NULL,
    hinhThucThanhToan   nvarchar(15)  NOT NULL CHECK (hinhThucThanhToan IN (N'TIEN_MAT', N'CHUYEN_KHOAN')),
    daThanhToan         bit           NOT NULL DEFAULT 0,
    tienKhachDua        decimal(18,2) NULL,
    maBenhNhan          varchar(12)   NOT NULL REFERENCES BenhNhan(maBenhNhan),
    maNhanVien          varchar(12)   NOT NULL REFERENCES NhanVien(maNhanVien)  -- le tan thu tien
);
GO
-- thanhTien la cot thuong + CHECK nhat quan (thay cho computed PERSISTED:
-- cot computed bat buoc SET option tren moi phien ket noi — rui ro voi JDBC).
CREATE TABLE ChiTietHoaDon (
    maHoaDon  varchar(12)   NOT NULL REFERENCES HoaDon(maHoaDon),
    maDichVu  varchar(6)    NOT NULL REFERENCES DichVu(maDichVu),
    soLuong   int           NOT NULL CHECK (soLuong > 0),
    donGia    decimal(18,2) NOT NULL,                 -- snapshot gia tai thoi diem thu
    thanhTien decimal(18,2) NOT NULL,
    CONSTRAINT CK_CTHD_ThanhTien CHECK (thanhTien = soLuong * donGia),
    CONSTRAINT PK_ChiTietHoaDon PRIMARY KEY (maHoaDon, maDichVu)
);
GO
-- FK hoa don phi kham them sau vi PhieuKham tao o 007 truoc HoaDon
ALTER TABLE PhieuKham ADD CONSTRAINT FK_PhieuKham_HoaDon
    FOREIGN KEY (maHoaDonPhiKham) REFERENCES HoaDon(maHoaDon);
GO
PRINT N'008_hoadon_chitiethoadon OK';
GO
