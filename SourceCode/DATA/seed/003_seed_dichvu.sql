-- 003_seed_dichvu.sql (chủ file: Duy)
USE N15_PTUD_DB;
GO
INSERT INTO DichVu (maDichVu, tenDichVu, donGiaNiemYet, trangThai) VALUES
('DV0001', N'Phí khám ban đầu',   50000.00,           N'DANG_PHUC_VU'),
('DV0002', N'Cạo vôi răng',       200000.00,          N'DANG_PHUC_VU'),
('DV0003', N'Hàn răng composite', 350000.00,          N'DANG_PHUC_VU'),
('DV0004', N'Nhổ răng thường',    400000.00,          N'DANG_PHUC_VU'),
('DV0005', N'Tẩy trắng răng',     1500000.00,         N'DANG_PHUC_VU'),
('DV0006', N'Chụp phim Panorama', 100000.00,          N'DANG_PHUC_VU'),
('DV0007', N'Trát răng sâu',      250000.00,          N'DANG_PHUC_VU'),
('DV0008', N'Lấy tủy răng',       800000.00,          N'DANG_PHUC_VU'),
('DV0009', N'Đội mão răng sứ',    2000000.00,         N'DANG_PHUC_VU'),
('DV0010', N'Tráng men răng',     300000.00,          N'NGUNG_PHUC_VU'),
('DV0011', N'Khám lưu động',      100000.00,          N'NGUNG_PHUC_VU');
GO
-- DV0001 la "Phi kham ban dau" - BUS thu phi bat buoc dung dich vu nay, KHONG xoa/doi ma.
PRINT N'003_seed_dichvu OK';
GO
