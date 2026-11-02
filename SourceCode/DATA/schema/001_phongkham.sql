-- 001_phongkham.sql (chủ file: Thanh)
USE N15_PTUD_DB;
GO
CREATE TABLE PhongKham (
    maPhong      varchar(4)   NOT NULL PRIMARY KEY,
    tenPhong     nvarchar(50) NOT NULL UNIQUE,
    dangNhanBenh bit          NOT NULL DEFAULT 0
);
GO
PRINT N'001_phongkham OK';
GO
