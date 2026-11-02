-- 010_chitietcapthuoc.sql (chủ file: Thắng)
USE N15_PTUD_DB;
GO
CREATE TABLE ChiTietCapThuoc (
    maPhieuKham     varchar(12)   NOT NULL REFERENCES PhieuKham(maPhieuKham),
    maThuoc         varchar(6)    NOT NULL REFERENCES Thuoc(maThuoc),
    soLuong         int           NOT NULL CHECK (soLuong > 0),
    ghiChuCapThuoc  nvarchar(300) NULL,
    CONSTRAINT PK_ChiTietCapThuoc PRIMARY KEY (maPhieuKham, maThuoc)
);
GO
PRINT N'010_chitietcapthuoc OK';
GO
