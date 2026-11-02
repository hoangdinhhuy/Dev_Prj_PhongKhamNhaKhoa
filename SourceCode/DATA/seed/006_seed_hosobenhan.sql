-- 006_seed_hosobenhan.sql (chủ file: Thắng)
USE N15_PTUD_DB;
GO
INSERT INTO HoSoBenhAn (maHoSo, tienSuBenhLy, ghiChuChuyenMon, maBenhNhan) VALUES
('HS2611010001', N'Sâu răng hàm lớn hai bên, từng hàn năm 2023', N'Bệnh nhân hợp tác, giữ vệ sinh tốt', 'BN2611010001'),
('HS2611010002', N'Viêm nướu nhẹ, cạo vôi định kỳ',              NULL,                                  'BN2611010002'),
('HS2611010003', N'Răng khôn mọc lệch, theo dõi',                N'Cân nhắc nhổ răng khôn trong 6 tháng', 'BN2611010003');
GO
PRINT N'006_seed_hosobenhan OK';
GO
