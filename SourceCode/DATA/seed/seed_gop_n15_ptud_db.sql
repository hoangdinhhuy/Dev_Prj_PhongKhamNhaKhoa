-- ============================================================================
-- N15_PTUD_DB - GOM 14 FILE SEED CUA NHOM 15
-- Nguon: 001..011, 013, 014_seed_demo_8, 015_seed_phan_cong_phong.
-- CHI CHAY TREN DATABASE DA TAO DU SCHEMA VA CHUA CO DU LIEU.
-- KHONG CHAY LAI TREN DATABASE CO DU LIEU: 014 se XOA ho so, phieu, hoa don.
-- Backup database neu can giu bat ky du lieu nao.
-- Sau 014, du lieu benh nhan con 8 ho so demo (theo README cua nhom).
-- Tai khoan mau: NV0005 / 123456, hash SHA-256.
-- ============================================================================
USE N15_PTUD_DB;
GO
SET NOCOUNT ON;
GO

-- Dung neu thieu schema/ migration. File nay chi them du lieu, khong tao bang.
IF OBJECT_ID(N'dbo.BoDem', N'U') IS NULL
   OR OBJECT_ID(N'dbo.PhongKham', N'U') IS NULL
   OR OBJECT_ID(N'dbo.NhanVien', N'U') IS NULL
   OR OBJECT_ID(N'dbo.TaiKhoan', N'U') IS NULL
   OR OBJECT_ID(N'dbo.DichVu', N'U') IS NULL
   OR OBJECT_ID(N'dbo.Thuoc', N'U') IS NULL
   OR OBJECT_ID(N'dbo.BenhNhan', N'U') IS NULL
   OR OBJECT_ID(N'dbo.HoSoBenhAn', N'U') IS NULL
   OR OBJECT_ID(N'dbo.PhieuKham', N'U') IS NULL
   OR OBJECT_ID(N'dbo.HoaDon', N'U') IS NULL
   OR OBJECT_ID(N'dbo.ChiTietHoaDon', N'U') IS NULL
   OR OBJECT_ID(N'dbo.ChiDinhDichVu', N'U') IS NULL
   OR OBJECT_ID(N'dbo.ChiTietCapThuoc', N'U') IS NULL
   OR OBJECT_ID(N'dbo.HangHoa', N'U') IS NULL
   OR COL_LENGTH(N'dbo.PhongKham', N'maBacSi') IS NULL
   OR COL_LENGTH(N'dbo.PhongKham', N'maPhuTa') IS NULL
   OR COL_LENGTH(N'dbo.HoaDon', N'maPhieuKham') IS NULL
BEGIN
    PRINT N'DUNG: Thieu bang/cot trong schema. Hay chay du migration truoc.';
    SET NOEXEC ON;
END;
GO

-- CHAN CHAY NHAM TREN DATABASE DA CO DU LIEU.
-- SET NOEXEC ON bo qua cac batch phia sau. Cuoi file se reset NOEXEC OFF.
IF EXISTS (
    SELECT 1
    FROM sys.tables AS t
    JOIN sys.partitions AS p ON p.object_id = t.object_id AND p.index_id IN (0, 1)
    WHERE t.name IN (
        'BoDem','PhongKham','NhanVien','TaiKhoan','DichVu','Thuoc','BenhNhan',
        'HoSoBenhAn','PhieuKham','HoaDon','ChiTietHoaDon','ChiDinhDichVu',
        'ChiTietCapThuoc','HangHoa','CaThuNgan'
    ) AND p.rows > 0
)
BEGIN
    PRINT N'DUNG: Database da co du lieu. Khong chay file gop de tranh ghi de/xoa du lieu.';
    SET NOEXEC ON;
END;
GO

-- >>> BAT DAU: 001_seed_phongkham.sql
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
GO
-- <<< KET THUC: 001_seed_phongkham.sql

-- >>> BAT DAU: 002_seed_nhanvien_taikhoan.sql
-- 002_seed_nhanvien_taikhoan.sql (chủ file: Duy)
-- TAI KHOAN CA NHAN (Gop y GV: khong dung ten vai tro lam tai khoan):
-- NV0001 = Le tan (Huy) · NV0002/NV0003 = Bac si (Thang + demo) · NV0005 = Phu ta (Thanh) · NV0007 = Quan ly (Duy) · NV0004 = demo BI_KHOA
-- Ten nhan vien = ten thanh vien code luong do (theo TaiLieu/docs/tasks):
-- Username = MA NHAN VIEN (Gop y GV P2-4). NV0004 BI_KHOA; NV0008 bac si nghi viec KHONG co tai khoan.
-- Mat khau chung: 123456  ->  SHA-256 = 8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92
USE N15_PTUD_DB;
GO
INSERT INTO NhanVien (maNhanVien, hoTen, soDienThoai, ngaySinh, gioiTinh, cccd, chucVu, conLamViec) VALUES
(N'NV0001', N'Hoàng Đình Huy',   N'0901234561', '1999-03-12', 0, '079199000001', N'LE_TAN',  1),
(N'NV0002', N'Huỳnh Quốc Thắng', N'0901234562', '1995-07-08', 1, '079199500002', N'BAC_SI',  1),
(N'NV0003', N'Lê Minh Châu',      N'0901234563', '1994-11-25', 1, '079199400003', N'BAC_SI',  1),
(N'NV0004', N'Phạm Thu Hà',       N'0901234564', '1997-01-30', 0, '079199700004', N'LE_TAN',  1),
(N'NV0005', N'Phan Chí Thanh',    N'0901234565', '1993-05-19', 1, '079199300005', N'PHU_TA',  1),
(N'NV0006', N'Bùi Ngọc Diệp',     N'0901234566', '1998-09-02', 0, '079199800006', N'PHU_TA',  1),
(N'NV0007', N'Nguyễn Tuấn Duy',   N'0901234567', '1990-12-15', 1, '079199000007', N'QUAN_LY', 1),
(N'NV0008', N'Đặng Hải Yến',      N'0901234568', '1996-06-21', 0, NULL,           N'BAC_SI',  0);
GO
INSERT INTO TaiKhoan (tenDangNhap, matKhau, trangThai, maNhanVien) VALUES
(N'NV0001',  '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'HOAT_DONG', 'NV0001'),
(N'NV0002', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'HOAT_DONG', 'NV0002'),
(N'NV0003', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'HOAT_DONG', 'NV0003'),
(N'NV0005',  '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'HOAT_DONG', 'NV0005'),
(N'NV0007', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'HOAT_DONG', 'NV0007'),
(N'NV0004','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'BI_KHOA',   'NV0004');
GO
-- Tai khoan BI_KHOA dung de test UC001 (TB02). NV0008 (bac si nghi viec) khong co tai khoan.
PRINT N'002_seed_nhanvien_taikhoan OK';
GO
GO
-- <<< KET THUC: 002_seed_nhanvien_taikhoan.sql

-- >>> BAT DAU: 003_seed_dichvu.sql
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
GO
-- <<< KET THUC: 003_seed_dichvu.sql

-- >>> BAT DAU: 004_seed_thuoc.sql
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
GO
-- <<< KET THUC: 004_seed_thuoc.sql

-- >>> BAT DAU: 005_seed_benhnhan.sql
-- 005_seed_benhnhan.sql (chủ file: Huy)
-- 10 benh nhan demo, thong tin DAY DU (cccd + dia chi + tien su) de kiem tra hien thi chi tiet.
USE N15_PTUD_DB;
GO
INSERT INTO BenhNhan (maBenhNhan, hoTen, soDienThoai, cccd, ngaySinh, diaChi, tienSuDiUng) VALUES
('BN2611010001', N'Nguyễn Văn An',   '0901111001', '079199001001', '1990-05-04', N'25 Nguyễn Xí, Q. Thủ Đức, TP.HCM',    N'Dị ứng Penicillin - từng sốt phát ban khi uống'),
('BN2611010002', N'Trần Thị Bình',   '0901111002', '079198502002', '1985-08-19', N'143 Xô Viết Nghệ Tĩnh, Q. Bình Thạnh', N'Dị ứng thuốc tê (Lidocaine)'),
('BN2611010003', N'Lê Hoàng Cương',  '0901111003', '079200103003', '2001-12-01', N'78 Võ Văn Ngân, TP. Thủ Đức',          N'Không ghi nhận dị ứng'),
('BN2611010004', N'Phạm Mỹ Duyên',   '0901111004', '079199804004', '1998-02-14', N'221 Phan Văn Trị, Q. Gò Vấp',          N'Dị ứng Penicillin'),
('BN2611010005', N'Hoàng Gia Đức',   '0901111005', '079200105005', '2001-04-27', N'56 Kha Vạn Cân, Dĩ An, Bình Dương',    N'Không ghi nhận dị ứng'),
('BN2611010006', N'Đỗ Khánh Giang',  '0901111006', '079197906006', '1979-10-09', N'90 Cộng Hòa, Q. Tân Bình',             N'Dị ứng Aspirin - đau dạ dày'),
('BN2611010007', N'Bùi Thanh Hà',    '0901111007', '079199207007', '1992-06-30', N'35 Nguyễn Văn Đậu, Q. Phú Nhuận',      N'Không ghi nhận dị ứng'),
('BN2611010008', N'Ngô Trí Hùng',    '0901111008', '079198808008', '1988-01-11', N'12 Song Hành, TP. Thủ Đức',            N'Dị ứng kháng sinh nhóm Cephalosporin'),
('BN2611010009', N'Lý Tú Kiên',      '0901111009', '079200509009', '2005-03-22', N'340 Đỗ Xuân Hợp, Q. 9 (TP. Thủ Đức)',  N'Không ghi nhận dị ứng'),
('BN2611010010', N'Trương Ngọc Lam', '0901111010', '079199410010', '1994-11-17', N'777 Hoàng Hoa Thám, Q. Bình Tân',      N'Dị ứng tê Lidocaine - đã khai trong hồ sơ cũ');
GO
PRINT N'005_seed_benhnhan OK';
GO
GO
-- <<< KET THUC: 005_seed_benhnhan.sql

-- >>> BAT DAU: 006_seed_hosobenhan.sql
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
GO
-- <<< KET THUC: 006_seed_hosobenhan.sql

-- >>> BAT DAU: 007_seed_phieukham.sql
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
GO
-- <<< KET THUC: 007_seed_phieukham.sql

-- >>> BAT DAU: 008_seed_hoadon.sql
-- 008_seed_hoadon.sql (chủ file: Huy)
USE N15_PTUD_DB;
GO
DECLARE @d date = CAST(GETDATE() AS date);
DECLARE @t varchar(6) = FORMAT(@d, 'yyMMdd');

-- Hoa don phi kham da thu cho phieu DANG_KHAM hom nay (idempotent)
IF NOT EXISTS (SELECT 1 FROM HoaDon WHERE maHoaDon = 'HD' + @t + '0001')
BEGIN
    INSERT INTO HoaDon (maHoaDon, ngayThu, hinhThucThanhToan, daThanhToan, tienKhachDua, maBenhNhan, maNhanVien) VALUES
    ('HD' + @t + '0001', CAST(GETDATE() AS datetime2(0)), N'TIEN_MAT', 1, 50000.00, 'BN2611010007', 'NV0001');
    INSERT INTO ChiTietHoaDon (maHoaDon, maDichVu, soLuong, donGia, thanhTien) VALUES
    ('HD' + @t + '0001', 'DV0001', 1, 50000.00, 50000.00);
END
UPDATE PhieuKham SET maHoaDonPhiKham = 'HD' + @t + '0001'
WHERE maPhieuKham = 'PK' + @t + '0004' AND maHoaDonPhiKham IS NULL;
GO
PRINT N'008_seed_hoadon OK';
GO
GO
-- <<< KET THUC: 008_seed_hoadon.sql

-- >>> BAT DAU: 009_seed_chidinhdichvu.sql
-- 009_seed_chidinhdichvu.sql (chủ file: Thắng)
USE N15_PTUD_DB;
GO
DECLARE @y date = DATEADD(day, -1, CAST(GETDATE() AS date));
DECLARE @d date = CAST(GETDATE() AS date);
DECLARE @t varchar(6) = FORMAT(@d, 'yyMMdd');
DECLARE @h varchar(6) = FORMAT(@y, 'yyMMdd');

INSERT INTO ChiDinhDichVu (maPhieuKham, maDichVu) VALUES
('PK' + @h + '0001', 'DV0003'),   -- Han rang composite
('PK' + @h + '0002', 'DV0002'),   -- Cao voi
('PK' + @t + '0004', 'DV0006');   -- Chup phim (phieu DANG_KHAM dang kham)
GO
PRINT N'009_seed_chidinhdichvu OK';
GO
GO
-- <<< KET THUC: 009_seed_chidinhdichvu.sql

-- >>> BAT DAU: 010_seed_chitietcapthuoc(1).sql
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
GO
-- <<< KET THUC: 010_seed_chitietcapthuoc(1).sql

-- >>> BAT DAU: 011_seed_morong.sql
-- 011_seed_morong.sql — Mo rong du lieu demo cho bao cao + hang cho (them sau, khong sua file cu)
-- 10 benh nhan moi, 3 dich vu moi, 2 phieu kham HOAN_TAT cho moi ngay trong 6 ngay qua.
USE N15_PTUD_DB;
GO
IF (SELECT COUNT(*) FROM DichVu WHERE maDichVu IN ('DV0012', 'DV0013', 'DV0014')) = 0
BEGIN
INSERT INTO DichVu (maDichVu, tenDichVu, donGiaNiemYet, trangThai) VALUES
('DV0012', N'Tẩy trắng tại nhà (kèm máng)', 500000.00, N'DANG_PHUC_VU'),
('DV0013', N'Chỉnh nha - tái khám niềng',    200000.00, N'DANG_PHUC_VU'),
('DV0014', N'Chụp phim răng vùng',            80000.00, N'DANG_PHUC_VU');
END
GO
-- 2 phieu HOAN_TAT moi ngay cho 6 ngay gan nhat (bo qua hom qua - da co trong 007)
DECLARE @i int = 7;
WHILE @i >= 2
BEGIN
    DECLARE @d date = DATEADD(day, -@i, CAST(GETDATE() AS date));
    DECLARE @t varchar(6) = FORMAT(@d, 'yyMMdd');
    DECLARE @bs varchar(12) = CASE WHEN @i % 2 = 0 THEN 'NV0002' ELSE 'NV0003' END;
    DECLARE @bnma varchar(12) = 'BN26110100' + RIGHT('0' + CAST((1 + (@i % 10)) AS varchar(2)), 2);

    IF NOT EXISTS (SELECT 1 FROM PhieuKham WHERE maPhieuKham = 'PK' + @t + '0001')
    INSERT INTO PhieuKham (maPhieuKham, ngayKham, soThuTu, lyDoKham, chanDoan, ketQuaDieuTri, trangThai,
                           maBenhNhan, maNhanVien, maPhong, maHoSo) VALUES
    ('PK' + @t + '0001', @d, 1, N'Khám định kỳ / điều trị',
     CASE WHEN @i % 2 = 0 THEN N'Sâu răng men' ELSE N'Cao răng, viêm nướu nhẹ' END,
     CASE WHEN @i % 2 = 0 THEN N'Đã trát răng' ELSE N'Đã cạo vôi' END,
     N'HOAN_TAT', @bnma, @bs, 'P001',
     CASE WHEN @bnma IN ('BN2611010001','BN2611010002','BN2611010003') THEN
          (SELECT maHoSo FROM HoSoBenhAn WHERE maBenhNhan = @bnma) ELSE NULL END);
    IF NOT EXISTS (SELECT 1 FROM PhieuKham WHERE maPhieuKham = 'PK' + @t + '0002')
    INSERT INTO PhieuKham (maPhieuKham, ngayKham, soThuTu, lyDoKham, chanDoan, ketQuaDieuTri, trangThai,
                           maBenhNhan, maNhanVien, maPhong) VALUES
    ('PK' + @t + '0002', @d, 2, N'Tái khám theo hẹn', N'Trạng thái răng miệng ổn định',
     N'Hẹn tái khám sau 6 tháng', N'HOAN_TAT', @bnma, @bs, 'P001');

    -- Dong bo bo dem (hom qua da co dong tu 007 -> UPDATE, ngay khac INSERT)
    UPDATE BoDem SET giaTri = 2 WHERE loaiMa = 'PK' AND ngay = @d;
    IF @@ROWCOUNT = 0 INSERT INTO BoDem (loaiMa, ngay, giaTri) VALUES ('PK', @d, 2);
    UPDATE BoDem SET giaTri = 2 WHERE loaiMa = 'STT-P001' AND ngay = @d;
    IF @@ROWCOUNT = 0 INSERT INTO BoDem (loaiMa, ngay, giaTri) VALUES ('STT-P001', @d, 2);

    SET @i = @i - 1;
END
GO
-- Chi dinh cho mot so phieu moi (de tan suat dich vu trong bao cao dep)
DECLARE @j int = 1;
WHILE @j <= 6
BEGIN
    DECLARE @d2 date = DATEADD(day, -@j, CAST(GETDATE() AS date));
    DECLARE @t2 varchar(6) = FORMAT(@d2, 'yyMMdd');
    INSERT INTO ChiDinhDichVu (maPhieuKham, maDichVu)
    SELECT 'PK' + @t2 + '0001',
           CASE WHEN @j % 3 = 1 THEN 'DV0002' WHEN @j % 3 = 2 THEN 'DV0003' ELSE 'DV0007' END
    WHERE NOT EXISTS (SELECT 1 FROM ChiDinhDichVu WHERE maPhieuKham = 'PK' + @t2 + '0001'
                      AND maDichVu = CASE WHEN @j % 3 = 1 THEN 'DV0002'
                                          WHEN @j % 3 = 2 THEN 'DV0003' ELSE 'DV0007' END);
    SET @j = @j + 1;
END
GO
PRINT N'011_seed_morong OK';
GO
GO
-- <<< KET THUC: 011_seed_morong.sql

-- >>> BAT DAU: 013_seed_hanghoa.sql
USE N15_PTUD_DB;
GO
-- Chạy sau schema/013_hanghoa.sql. Có thể chạy lại mà không tạo bản ghi trùng.
IF OBJECT_ID(N'dbo.HangHoa', N'U') IS NULL
    THROW 50013, N'Chưa có bảng HangHoa. Hãy chạy schema/013_hanghoa.sql trước.', 1;
GO
IF NOT EXISTS (SELECT 1 FROM dbo.HangHoa WHERE maHangHoa = 'HH0001')
    INSERT INTO dbo.HangHoa VALUES ('HH0001', N'Găng tay y tế', N'Hộp', 24, 10);
IF NOT EXISTS (SELECT 1 FROM dbo.HangHoa WHERE maHangHoa = 'HH0002')
    INSERT INTO dbo.HangHoa VALUES ('HH0002', N'Khẩu trang y tế', N'Hộp', 15, 8);
IF NOT EXISTS (SELECT 1 FROM dbo.HangHoa WHERE maHangHoa = 'HH0003')
    INSERT INTO dbo.HangHoa VALUES ('HH0003', N'Bông gạc nha khoa', N'Gói', 7, 10);
IF NOT EXISTS (SELECT 1 FROM dbo.HangHoa WHERE maHangHoa = 'HH0004')
    INSERT INTO dbo.HangHoa VALUES ('HH0004', N'Kim tiêm nha khoa', N'Hộp', 12, 5);
IF NOT EXISTS (SELECT 1 FROM dbo.HangHoa WHERE maHangHoa = 'HH0005')
    INSERT INTO dbo.HangHoa VALUES ('HH0005', N'Vật liệu trám composite', N'Tuýp', 4, 6);
IF NOT EXISTS (SELECT 1 FROM dbo.HangHoa WHERE maHangHoa = 'HH0006')
    INSERT INTO dbo.HangHoa VALUES ('HH0006', N'Thuốc tê Lidocaine', N'Lọ', 18, 8);
GO
PRINT N'013_seed_hanghoa OK';
GO
GO
-- <<< KET THUC: 013_seed_hanghoa.sql

-- >>> BAT DAU: 014_seed_demo_8.sql
-- 014_seed_demo_8.sql
-- Thay toàn bộ dữ liệu bệnh nhân/lượt khám/hóa đơn mẫu bằng 8 hồ sơ demo.
-- Chạy sau tất cả schema và seed 001..013. Không xóa nhân viên, tài khoản,
-- phòng khám, dịch vụ, thuốc hoặc hàng hóa.
USE N15_PTUD_DB;
GO
SET XACT_ABORT ON;
BEGIN TRY
    BEGIN TRANSACTION;

    DECLARE @d date = CAST(GETDATE() AS date);
    DECLARE @t varchar(6) = CONVERT(varchar(6), @d, 12);

    -- Dọn dữ liệu cũ theo thứ tự khóa ngoại, gồm cả mẫu mở rộng nhiều ngày.
    DELETE FROM ChiTietCapThuoc;
    DELETE FROM ChiDinhDichVu;
    DELETE FROM ChiTietHoaDon;
    UPDATE PhieuKham SET maHoaDonPhiKham = NULL;
    UPDATE HoaDon SET maPhieuKham = NULL;
    DELETE FROM PhieuKham;
    DELETE FROM HoSoBenhAn;
    DELETE FROM HoaDon;
    DELETE FROM BenhNhan;

    -- 8 bệnh nhân mẫu, đầy đủ thông tin để thử hồ sơ và tra cứu.
    INSERT INTO BenhNhan (maBenhNhan, hoTen, soDienThoai, cccd, ngaySinh, diaChi, tienSuDiUng) VALUES
    ('BN' + @t + '0001', N'Nguyễn Minh Anh', '0901112001', '079201001001', '1990-05-04', N'25 Nguyễn Xí, TP.HCM', N'Dị ứng Penicillin'),
    ('BN' + @t + '0002', N'Trần Thị Bình',   '0901112002', '079198502002', '1985-08-19', N'143 Xô Viết Nghệ Tĩnh, TP.HCM', N'Không ghi nhận dị ứng'),
    ('BN' + @t + '0003', N'Lê Hoàng Cương',  '0901112003', '079200103003', '2001-12-01', N'78 Võ Văn Ngân, TP.HCM', N'Dị ứng Lidocaine'),
    ('BN' + @t + '0004', N'Phạm Mỹ Duyên',   '0901112004', '079199804004', '1998-02-14', N'221 Phan Văn Trị, TP.HCM', N'Không ghi nhận dị ứng'),
    ('BN' + @t + '0005', N'Hoàng Gia Đức',   '0901112005', '079200105005', '2001-04-27', N'56 Kha Vạn Cân, TP.HCM', N'Không ghi nhận dị ứng'),
    ('BN' + @t + '0006', N'Đỗ Khánh Giang',  '0901112006', '079197906006', '1979-10-09', N'90 Cộng Hòa, TP.HCM', N'Dị ứng Aspirin'),
    ('BN' + @t + '0007', N'Bùi Thanh Hà',    '0901112007', '079199207007', '1992-06-30', N'35 Nguyễn Văn Đậu, TP.HCM', N'Không ghi nhận dị ứng'),
    ('BN' + @t + '0008', N'Ngô Trí Hùng',    '0901112008', '079198808008', '1988-01-11', N'12 Song Hành, TP.HCM', N'Dị ứng Cephalosporin');

    -- Có hồ sơ bệnh án cho 4 lượt đã hoàn tất.
    INSERT INTO HoSoBenhAn (maHoSo, tienSuBenhLy, ghiChuChuyenMon, maBenhNhan) VALUES
    ('HS' + @t + '0001', N'Khám tổng quát lần đầu', N'Tái khám định kỳ', 'BN' + @t + '0005'),
    ('HS' + @t + '0002', N'Răng hàm có dấu hiệu sâu', N'Tư vấn vệ sinh răng miệng', 'BN' + @t + '0006'),
    ('HS' + @t + '0003', N'Đau răng khi ăn đồ nóng', N'Theo dõi sau điều trị', 'BN' + @t + '0007'),
    ('HS' + @t + '0004', N'Khám định kỳ', N'Không phát hiện vấn đề cần điều trị', 'BN' + @t + '0008');

    -- Thu phí khám ban đầu cho 6 bệnh nhân đã có phiếu (2 đang khám + 4 hoàn tất).
    INSERT INTO HoaDon (maHoaDon, ngayThu, hinhThucThanhToan, daThanhToan, tienKhachDua, maBenhNhan, maNhanVien)
    SELECT 'HD' + @t + RIGHT('0000' + CAST(n.n AS varchar(4)), 4),
           DATEADD(minute, n.n, CAST(@d AS datetime2(0))), N'TIEN_MAT', 1, 50000,
           'BN' + @t + RIGHT('0000' + CAST(n.n + 2 AS varchar(4)), 4), 'NV0001'
    FROM (VALUES (1),(2),(3),(4),(5),(6)) n(n);

    INSERT INTO ChiTietHoaDon (maHoaDon, maDichVu, soLuong, donGia, thanhTien)
    SELECT 'HD' + @t + RIGHT('0000' + CAST(n.n AS varchar(4)), 4), 'DV0001', 1, 50000, 50000
    FROM (VALUES (1),(2),(3),(4),(5),(6)) n(n);

    INSERT INTO PhieuKham (maPhieuKham, ngayKham, soThuTu, lyDoKham, chanDoan, ketQuaDieuTri,
                           trangThai, maBenhNhan, maNhanVien, maPhong, maHoSo, maHoaDonPhiKham)
    SELECT 'PK' + @t + RIGHT('0000' + CAST(n.n AS varchar(4)), 4), @d, n.n,
           CASE n.n WHEN 1 THEN N'Đau răng hàm' WHEN 2 THEN N'Khám tổng quát'
                    WHEN 3 THEN N'Ê buốt răng' WHEN 4 THEN N'Đau răng khi nhai'
                    WHEN 5 THEN N'Kiểm tra răng định kỳ' ELSE N'Khám tổng quát' END,
           CASE WHEN n.n >= 3 THEN CASE n.n WHEN 3 THEN N'Sâu răng hàm'
                    WHEN 4 THEN N'Viêm nướu nhẹ' WHEN 5 THEN N'Mòn men răng' ELSE N'Răng miệng ổn định' END ELSE NULL END,
           CASE WHEN n.n >= 3 THEN N'Đã tư vấn và ghi nhận kết quả khám' ELSE NULL END,
           CASE WHEN n.n <= 2 THEN N'DANG_KHAM' ELSE N'HOAN_TAT' END,
           'BN' + @t + RIGHT('0000' + CAST(n.n + 2 AS varchar(4)), 4),
           CASE WHEN n.n % 2 = 1 THEN 'NV0002' ELSE 'NV0003' END,
           CASE WHEN n.n % 2 = 1 THEN 'P001' ELSE 'P002' END,
           CASE WHEN n.n >= 3 THEN 'HS' + @t + RIGHT('0000' + CAST(n.n - 2 AS varchar(4)), 4) ELSE NULL END,
           'HD' + @t + RIGHT('0000' + CAST(n.n AS varchar(4)), 4)
    FROM (VALUES (1),(2),(3),(4),(5),(6)) n(n);

    -- 3 lượt hoàn tất có dịch vụ phát sinh cần thu cuối lượt.
    INSERT INTO ChiDinhDichVu (maPhieuKham, maDichVu) VALUES
    ('PK' + @t + '0003', 'DV0002'),
    ('PK' + @t + '0004', 'DV0003'),
    ('PK' + @t + '0005', 'DV0004');

    -- Lượt thứ 4 chỉ có DV0001 (phí khám đã thu), không phát sinh tiền cuối lượt.
    -- Vẫn có chỉ định để thỏa quy tắc hoàn tất lượt khám của ứng dụng.
    INSERT INTO ChiDinhDichVu (maPhieuKham, maDichVu) VALUES ('PK' + @t + '0006', 'DV0001');

    -- Đồng bộ bộ đếm để thao tác tạo mới tiếp theo không đụng mã seed.
    MERGE BoDem AS target
    USING (VALUES ('BN', @d, 8), ('HS', @d, 4), ('PK', @d, 6), ('HD', @d, 6),
                  ('STT-P001', @d, 3), ('STT-P002', @d, 3)) AS source(loaiMa, ngay, giaTri)
       ON target.loaiMa = source.loaiMa AND target.ngay = source.ngay
    WHEN MATCHED THEN UPDATE SET giaTri = source.giaTri
    WHEN NOT MATCHED THEN INSERT (loaiMa, ngay, giaTri)
         VALUES (source.loaiMa, source.ngay, source.giaTri);

    COMMIT TRANSACTION;
    PRINT N'014_seed_demo_8 OK: 8 bệnh nhân; 2 chưa thu phí, 2 đang khám, 4 hoàn tất (3 chờ thu dịch vụ, 1 không phát sinh).';
END TRY
BEGIN CATCH
    IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
    THROW;
END CATCH;
GO
GO
-- <<< KET THUC: 014_seed_demo_8.sql

-- >>> BAT DAU: 015_seed_phan_cong_phong.sql
USE N15_PTUD_DB;
GO
IF NOT EXISTS (SELECT 1 FROM dbo.TaiKhoan WHERE maNhanVien = 'NV0006')
    INSERT INTO dbo.TaiKhoan (tenDangNhap, matKhau, trangThai, maNhanVien)
    VALUES ('NV0006',
            '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92',
            N'HOAT_DONG', 'NV0006');

-- Hai cap bac si - phu ta, moi cap phu trach hai ghe/phong.
-- Chi dien phan cong con thieu de khong ghi de thay doi tai co so.
UPDATE dbo.PhongKham SET maBacSi = 'NV0002', maPhuTa = 'NV0005'
WHERE maPhong IN ('P001', 'P003') AND maBacSi IS NULL AND maPhuTa IS NULL;
UPDATE dbo.PhongKham SET maBacSi = 'NV0003', maPhuTa = 'NV0006'
WHERE maPhong IN ('P002', 'P004') AND maBacSi IS NULL AND maPhuTa IS NULL;
GO
PRINT N'015_seed_phan_cong_phong OK';
GO
GO
-- <<< KET THUC: 015_seed_phan_cong_phong.sql

-- ============================================================================
-- DA CHAY HET 14 FILE SEED. Kiem tra ket qua va tai khoan.
-- ============================================================================
SELECT t.name AS TenBang, SUM(p.rows) AS SoDong
FROM sys.tables AS t
JOIN sys.partitions AS p ON p.object_id = t.object_id AND p.index_id IN (0, 1)
GROUP BY t.name
ORDER BY t.name;
GO
SELECT tenDangNhap, maNhanVien, trangThai
FROM dbo.TaiKhoan
WHERE tenDangNhap IN (N'NV0001', N'NV0005', N'NV0007')
ORDER BY tenDangNhap;
GO
PRINT N'HOAN TAT FILE SEED GOM. Benh nhan demo cuoi cung theo 014_seed_demo_8.';
GO
-- Dam bao reset session khi batch guard da bat NOEXEC.
SET NOEXEC OFF;
GO
