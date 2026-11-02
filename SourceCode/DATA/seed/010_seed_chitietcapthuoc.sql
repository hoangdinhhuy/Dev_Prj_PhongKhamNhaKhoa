-- 010_seed_chitietcapthuoc.sql (chủ file: Thắng)
USE N15_PTUD_DB;
GO
DECLARE @y date = DATEADD(day, -1, CAST(GETDATE() AS date));
DECLARE @h varchar(6) = FORMAT(@y, 'yyMMdd');

INSERT INTO ChiTietCapThuoc (maPhieuKham, maThuoc, soLuong, ghiChuCapThuoc) VALUES
('PK' + @h + '0001', 'TH0001', 12, N'Uống 1 viên x 2 lần/ngày sau ăn, trong 6 ngày'),
('PK' + @h + '0001', 'TH0003', 6,  N'Uống khi đau, tối đa 3 viên/ngày');
GO
PRINT N'010_seed_chitietcapthuoc OK';
GO
