-- 007_phieukham.sql (chủ file: Huy)
USE N15_PTUD_DB;
GO
CREATE TABLE PhieuKham (
    maPhieuKham        varchar(12)   NOT NULL PRIMARY KEY,  -- PKyyMMddNNNN
    ngayKham           date          NOT NULL,
    soThuTu            int           NOT NULL CHECK (soThuTu > 0),
    lyDoKham           nvarchar(500) NOT NULL,
    chanDoan           nvarchar(500) NULL,
    ketQuaDieuTri      nvarchar(500) NULL,
    ghiChuThuocSuDung  nvarchar(500) NULL,
    ghiChuThuocTuMua   nvarchar(500) NULL,
    trangThai          nvarchar(10)  NOT NULL DEFAULT N'CHO_KHAM'
                       CHECK (trangThai IN (N'CHO_KHAM', N'DANG_KHAM', N'HOAN_TAT')),
    maBenhNhan         varchar(12)   NOT NULL REFERENCES BenhNhan(maBenhNhan),
    maNhanVien         varchar(12)   NOT NULL REFERENCES NhanVien(maNhanVien),  -- bac si
    maPhong            varchar(4)    NOT NULL REFERENCES PhongKham(maPhong),
    maHoSo             varchar(12)   NULL REFERENCES HoSoBenhAn(maHoSo),
    -- KHONG UNIQUE o DB: SQL Server khong cho unique index chap nhieu nhieu NULL;
    -- rang buoc "1 hoa don phi chi gan 1 phieu" do BUS kiem tra truoc khi gan.
    maHoaDonPhiKham    varchar(12)   NULL,  -- FK them o 008 (sau khi co bang HoaDon)
    CONSTRAINT UQ_PhieuKham_STT UNIQUE (ngayKham, maPhong, soThuTu)
);
GO
PRINT N'007_phieukham OK';
GO
