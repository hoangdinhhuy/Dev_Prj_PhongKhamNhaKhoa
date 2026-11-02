-- 003_dichvu.sql (chủ file: Duy)
USE N15_PTUD_DB;
GO
CREATE TABLE DichVu (
    maDichVu       varchar(6)    NOT NULL PRIMARY KEY,  -- DVNNNN
    tenDichVu      nvarchar(100) NOT NULL UNIQUE,
    donGiaNiemYet  decimal(18,2) NOT NULL CHECK (donGiaNiemYet >= 0),
    trangThai      nvarchar(15)  NOT NULL DEFAULT N'DANG_PHUC_VU' CHECK (trangThai IN (N'DANG_PHUC_VU', N'NGUNG_PHUC_VU'))
);
GO
PRINT N'003_dichvu OK';
GO
