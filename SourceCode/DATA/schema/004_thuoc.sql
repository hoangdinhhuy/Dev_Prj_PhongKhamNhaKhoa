-- 004_thuoc.sql (chủ file: Thắng)
USE N15_PTUD_DB;
GO
CREATE TABLE Thuoc (
    maThuoc   varchar(6)    NOT NULL PRIMARY KEY,   -- THNNNN
    tenThuoc  nvarchar(100) NOT NULL,
    donViTinh nvarchar(20)  NOT NULL
);
GO
PRINT N'004_thuoc OK';
GO
