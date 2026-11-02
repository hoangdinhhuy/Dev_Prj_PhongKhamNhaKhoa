-- 005_benhnhan.sql (chủ file: Huy)
-- LUU Y: cccd KHONG dat UNIQUE o DB vi SQL Server khong cho phep unique index
-- chap nhieu gia tri NULL (cau hinh filtered index bat buoc SET option tren moi
-- phien ket noi — rui ro voi JDBC). Tinh duy nhat cccd do BenhNhan_BUS kiem tra
-- truoc khi luu (TB13), so dien thoai KHONG la ma dinh danh duy nhat (chot cua nhom).
USE N15_PTUD_DB;
GO
CREATE TABLE BenhNhan (
    maBenhNhan   varchar(12)   NOT NULL PRIMARY KEY,  -- BNyyMMddNNNN
    hoTen        nvarchar(100) NOT NULL,
    soDienThoai  varchar(10)   NOT NULL CHECK (soDienThoai LIKE '0%' AND LEN(soDienThoai) = 10),
    cccd         varchar(12)   NULL CHECK (cccd IS NULL OR LEN(cccd) = 12),
    ngaySinh     date          NOT NULL,
    diaChi       nvarchar(200) NULL,
    tienSuDiUng  nvarchar(500) NULL
);
GO
PRINT N'005_benhnhan OK';
GO
