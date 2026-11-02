-- 009_chidinhdichvu.sql (chủ file: Thắng)
USE N15_PTUD_DB;
GO
CREATE TABLE ChiDinhDichVu (
    maPhieuKham varchar(12) NOT NULL REFERENCES PhieuKham(maPhieuKham),
    maDichVu    varchar(6)  NOT NULL REFERENCES DichVu(maDichVu),
    CONSTRAINT PK_ChiDinhDichVu PRIMARY KEY (maPhieuKham, maDichVu)
);
GO
PRINT N'009_chidinhdichvu OK';
GO
