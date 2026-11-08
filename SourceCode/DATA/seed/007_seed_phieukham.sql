-- 007_seed_phieukham.sql (chủ file: Huy)
-- Dung ngay hien tai cua may de man hinh Hang cho co du lieu demo.
USE N15_PTUD_DB;
GO
DECLARE @d date = CAST(GETDATE() AS date);
DECLARE @y date = DATEADD(day, -1, @d);
DECLARE @t varchar(6) = FORMAT(@d, 'yyMMdd');
DECLARE @h varchar(6) = FORMAT(@y, 'yyMMdd');

-- Hom nay: 3 CHO_KHAM (P001, bacsi1), 1 DANG_KHAM (P002, bacsi2, da thu phi)
INSERT INTO PhieuKham (maPhieuKham, ngayKham, soThuTu, lyDoKham, trangThai, maBenhNhan, maNhanVien, maPhong) VALUES
('PK' + @t + '0001', @d, 1, N'Đau răng hàm bên phải',        N'CHO_KHAM', 'BN2611010004', 'NV0002', 'P001'),
('PK' + @t + '0002', @d, 2, N'Cạo vôi răng định kỳ',         N'CHO_KHAM', 'BN2611010005', 'NV0002', 'P001'),
('PK' + @t + '0003', @d, 3, N'Nhức răng khôn hàm dưới',      N'CHO_KHAM', 'BN2611010006', 'NV0002', 'P001'),
('PK' + @t + '0004', @d, 1, N'Đã vào khám - sâu răng cửa',   N'DANG_KHAM', 'BN2611010007', 'NV0003', 'P002');

-- Hom qua: 2 HOAN_TAT de Bao cao co du lieu
INSERT INTO PhieuKham (maPhieuKham, ngayKham, soThuTu, lyDoKham, chanDoan, ketQuaDieuTri, trangThai, maBenhNhan, maNhanVien, maPhong, maHoSo) VALUES
('PK' + @h + '0001', @y, 1, N'Đau khi nhai',       N'Sâu răng răng số 36', N'Đã hàn răng composite', N'HOAN_TAT', 'BN2611010001', 'NV0002', 'P001', 'HS2611010001'),
('PK' + @h + '0002', @y, 2, N'Cạo vôi + kiểm tra', N'Viêm nướu nhẹ',       N'Đã cạo vôi, hướng dẫn vệ sinh', N'HOAN_TAT', 'BN2611010002', 'NV0003', 'P001', 'HS2611010002');

-- Dong bo BoDem (idempotent: chay lai khong bao loi trung khoa)
UPDATE BoDem SET giaTri = 4 WHERE loaiMa = 'PK' AND ngay = @d;
IF @@ROWCOUNT = 0 INSERT INTO BoDem (loaiMa, ngay, giaTri) VALUES ('PK', @d, 4);
UPDATE BoDem SET giaTri = 3 WHERE loaiMa = 'STT-P001' AND ngay = @d;
IF @@ROWCOUNT = 0 INSERT INTO BoDem (loaiMa, ngay, giaTri) VALUES ('STT-P001', @d, 3);
UPDATE BoDem SET giaTri = 1 WHERE loaiMa = 'STT-P002' AND ngay = @d;
IF @@ROWCOUNT = 0 INSERT INTO BoDem (loaiMa, ngay, giaTri) VALUES ('STT-P002', @d, 1);
UPDATE BoDem SET giaTri = 1 WHERE loaiMa = 'HD' AND ngay = @d;
IF @@ROWCOUNT = 0 INSERT INTO BoDem (loaiMa, ngay, giaTri) VALUES ('HD', @d, 1);
UPDATE BoDem SET giaTri = 2 WHERE loaiMa = 'PK' AND ngay = @y;
IF @@ROWCOUNT = 0 INSERT INTO BoDem (loaiMa, ngay, giaTri) VALUES ('PK', @y, 2);
UPDATE BoDem SET giaTri = 2 WHERE loaiMa = 'STT-P001' AND ngay = @y;
IF @@ROWCOUNT = 0 INSERT INTO BoDem (loaiMa, ngay, giaTri) VALUES ('STT-P001', @y, 2);
GO
PRINT N'007_seed_phieukham OK';
GO
