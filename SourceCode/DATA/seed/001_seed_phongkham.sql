-- 001_seed_phongkham.sql (chủ file: Thanh)
USE N15_PTUD_DB;
GO
INSERT INTO PhongKham (maPhong, tenPhong, dangNhanBenh) VALUES
(N'P001', N'Phòng khám 1', 1),
(N'P002', N'Phòng khám 2', 1),
(N'P003', N'Phòng khám 3', 0),
(N'P004', N'Phòng khám 4', 0);
GO
PRINT N'001_seed_phongkham OK';
GO
