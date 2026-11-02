-- 002_nhanvien_taikhoan.sql (chủ file: Duy)
USE N15_PTUD_DB;
GO
CREATE TABLE NhanVien (
    maNhanVien  varchar(12)   NOT NULL PRIMARY KEY,   -- NVyyMMddNNNN
    hoTen       nvarchar(100) NOT NULL,
    soDienThoai varchar(10)   NULL,
    ngaySinh    date          NULL,
    gioiTinh    bit           NULL,
    cccd        varchar(12)   NULL,   -- khong UNIQUE o DB (unique index khong chap nhan nhieu NULL); kiem tra o BUS khi can
    chucVu      nvarchar(10)  NOT NULL CHECK (chucVu IN (N'LE_TAN', N'BAC_SI', N'PHU_TA', N'QUAN_LY')),
    conLamViec  bit           NOT NULL DEFAULT 1
);
GO
CREATE TABLE TaiKhoan (
    tenDangNhap nvarchar(50) NOT NULL PRIMARY KEY,
    matKhau     varchar(64)  NOT NULL,                 -- hash SHA-256 (hex 64 ky tu), KHONG luu mat khau goc
    trangThai   nvarchar(10) NOT NULL DEFAULT N'HOAT_DONG' CHECK (trangThai IN (N'HOAT_DONG', N'BI_KHOA')),
    maNhanVien  varchar(12)  NOT NULL UNIQUE REFERENCES NhanVien(maNhanVien)
);
GO
PRINT N'002_nhanvien_taikhoan OK';
GO
