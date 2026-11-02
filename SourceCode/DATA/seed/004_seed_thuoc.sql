-- 004_seed_thuoc.sql (chủ file: Thắng)
USE N15_PTUD_DB;
GO
INSERT INTO Thuoc (maThuoc, tenThuoc, donViTinh) VALUES
('TH0001', N'Amoxicillin 500mg',           N'Viên'),
('TH0002', N'Ibuprofen 400mg',             N'Viên'),
('TH0003', N'Paracetamol 500mg',           N'Viên'),
('TH0004', N'Chlorhexidine súc miệng',     N'Chai'),
('TH0005', N'Metronidazole 250mg',         N'Viên'),
('TH0006', N'Gel chống viêm Novol',        N'Tuýp'),
('TH0007', N'Vitamin C',                   N'Viên'),
('TH0008', N'Nước muối sinh lý 0.9%',      N'Chai'),
('TH0009', N'Xylocaine (thuốc tê)',        N'Lọ'),
('TH0010', N'Bông gạc cầm máu',            N'Hộp');
GO
PRINT N'004_seed_thuoc OK';
GO
