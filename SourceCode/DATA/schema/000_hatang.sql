-- 000_hatang.sql — Hạ tầng CSDL (chủ file: Thanh)
IF DB_ID('N15_PTUD_DB') IS NULL
BEGIN
    CREATE DATABASE N15_PTUD_DB;
END
GO
USE N15_PTUD_DB;
GO
-- Bo dem ma tu dong: 1 dong cho moi (loaiMa, ngay). Cap ma nguyen tu bang UPDATE ... OUTPUT
CREATE TABLE BoDem (
    loaiMa varchar(10)  NOT NULL,
    ngay   date         NOT NULL,
    giaTri int          NOT NULL DEFAULT 0,
    CONSTRAINT PK_BoDem PRIMARY KEY (loaiMa, ngay),
    CONSTRAINT CK_BoDem_GiaTri CHECK (giaTri >= 0)
);
GO
-- loaiMa: NV, BN, HS, PK, HD, DV, TH (kiem so trong ma), va 'STT-<maPhong>' cho so thu tu
PRINT N'000_hatang OK';
GO
