-- 006_hosobenhan.sql (chủ file: Thắng) — 1 benh nhan chi co 1 ho so
USE N15_PTUD_DB;
GO
CREATE TABLE HoSoBenhAn (
    maHoSo           varchar(12)   NOT NULL PRIMARY KEY,   -- HSyyMMddNNNN
    tienSuBenhLy     nvarchar(500) NULL,
    ghiChuChuyenMon  nvarchar(500) NULL,
    maBenhNhan       varchar(12)   NOT NULL UNIQUE REFERENCES BenhNhan(maBenhNhan)
);
GO
PRINT N'006_hosobenhan OK';
GO
