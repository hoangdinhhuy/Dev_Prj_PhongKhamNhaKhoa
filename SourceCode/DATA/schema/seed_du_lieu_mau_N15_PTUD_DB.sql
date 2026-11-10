-- DU LIEU MAU NHA KHOA - PHAN CHI THANH
-- Dung cho database N15_PTUD_DB da tao du 15 bang bang sqlchung.sql.
-- KHONG xoa database, KHONG xoa du lieu cu. Co the chay lai trong ngay.
-- Tat ca benh nhan, so dien thoai va thong tin kham benh deu la DU LIEU GIA LAP.
-- Tai khoan demo: NV0001..NV0007 / 123456 (SHA-256 hex chu thuong).
-- Khuyen nghi: sao luu database truoc khi chay; KHONG dung mat khau mau trong production.

USE N15_PTUD_DB;
GO

SET NOCOUNT ON;
SET XACT_ABORT ON;

-- Kiem tra schema de tranh chen mot nua du lieu vao database sai phien ban.
IF OBJECT_ID(N'dbo.NhanVien', N'U') IS NULL
   OR OBJECT_ID(N'dbo.TaiKhoan', N'U') IS NULL
   OR OBJECT_ID(N'dbo.PhieuKham', N'U') IS NULL
   OR OBJECT_ID(N'dbo.CaThuNgan', N'U') IS NULL
   OR OBJECT_ID(N'dbo.BoDem', N'U') IS NULL
BEGIN
    THROW 51001, N'Thieu bang trong N15_PTUD_DB. Hay chay du schema sqlchung.sql truoc.', 1;
END;

IF COL_LENGTH(N'dbo.PhongKham', N'maBacSi') IS NULL
   OR COL_LENGTH(N'dbo.PhongKham', N'maPhuTa') IS NULL
   OR COL_LENGTH(N'dbo.HoaDon', N'maPhieuKham') IS NULL
   OR COL_LENGTH(N'dbo.ChiDinhDichVu', N'soRang') IS NULL
BEGIN
    THROW 51002, N'Thieu cot tu migration 012-020. Hay kiem tra schema truoc khi seed.', 1;
END;

DECLARE @ngay date = CAST(GETDATE() AS date);
DECLARE @maNgay char(6) = CONVERT(char(6), @ngay, 12); -- yyMMdd
DECLARE @pk5 varchar(12) = 'PK' + @maNgay + '0005';
DECLARE @pk6 varchar(12) = 'PK' + @maNgay + '0006';
DECLARE @hd1 varchar(12) = 'HD' + @maNgay + '0001';
DECLARE @hd2 varchar(12) = 'HD' + @maNgay + '0002';
DECLARE @matKhauHash varchar(64) = LOWER(CONVERT(varchar(64), HASHBYTES('SHA2_256', '123456'), 2));

BEGIN TRY
    BEGIN TRANSACTION;

    -- 1. NHAN VIEN: 1 le tan, 2 bac si, 2 phu ta, 1 le tan thu hai, 1 quan ly.
    INSERT INTO dbo.NhanVien
        (maNhanVien, hoTen, soDienThoai, ngaySinh, gioiTinh, cccd, chucVu, conLamViec)
    SELECT v.ma, v.hoTen, v.sdt, v.ngaySinh, v.gioiTinh, NULL, v.chucVu, 1
    FROM (VALUES
        ('NV0001', N'Lễ tân mẫu 01',     '0123000001', CAST('1999-02-14' AS date), 1, N'LE_TAN'),
        ('NV0002', N'Bác sĩ mẫu 01',     '0123000002', CAST('1988-07-09' AS date), 1, N'BAC_SI'),
        ('NV0003', N'Bác sĩ mẫu 02',     '0123000003', CAST('1990-10-21' AS date), 0, N'BAC_SI'),
        ('NV0004', N'Lễ tân mẫu 02',     '0123000004', CAST('2000-04-18' AS date), 0, N'LE_TAN'),
        ('NV0005', N'Phan Chí Thanh',    '0123000005', CAST('2004-11-15' AS date), 1, N'PHU_TA'),
        ('NV0006', N'Phụ tá mẫu 02',     '0123000006', CAST('2001-08-08' AS date), 0, N'PHU_TA'),
        ('NV0007', N'Quản lý mẫu',       '0123000007', CAST('1985-05-05' AS date), 1, N'QUAN_LY')
    ) AS v(ma, hoTen, sdt, ngaySinh, gioiTinh, chucVu)
    WHERE NOT EXISTS (SELECT 1 FROM dbo.NhanVien n WHERE n.maNhanVien = v.ma);

    -- 2. TAI KHOAN: mat khau duoc bam SHA-256, khong luu 123456 dang ro.
    INSERT INTO dbo.TaiKhoan (tenDangNhap, matKhau, trangThai, maNhanVien)
    SELECT n.maNhanVien, @matKhauHash, N'HOAT_DONG', n.maNhanVien
    FROM dbo.NhanVien n
    WHERE n.maNhanVien IN ('NV0001','NV0002','NV0003','NV0004','NV0005','NV0006','NV0007')
      AND NOT EXISTS (SELECT 1 FROM dbo.TaiKhoan tk WHERE tk.maNhanVien = n.maNhanVien)
      AND NOT EXISTS (SELECT 1 FROM dbo.TaiKhoan tk WHERE tk.tenDangNhap = n.maNhanVien);

    -- 3. PHONG KHAM: 2 phong dang nhan benh, 2 phong tam tat.
    INSERT INTO dbo.PhongKham (maPhong, tenPhong, dangNhanBenh, maBacSi, maPhuTa)
    SELECT v.ma, v.ten, v.dangNhan, v.bacSi, v.phuTa
    FROM (VALUES
        ('P001', N'Phòng khám tổng quát 1', CAST(1 AS bit), 'NV0002', 'NV0005'),
        ('P002', N'Phòng khám tổng quát 2', CAST(1 AS bit), 'NV0003', 'NV0006'),
        ('P003', N'Phòng điều trị 1',       CAST(0 AS bit), 'NV0002', 'NV0005'),
        ('P004', N'Phòng điều trị 2',       CAST(0 AS bit), 'NV0003', 'NV0006')
    ) AS v(ma, ten, dangNhan, bacSi, phuTa)
    WHERE NOT EXISTS (SELECT 1 FROM dbo.PhongKham p WHERE p.maPhong = v.ma)
      AND NOT EXISTS (SELECT 1 FROM dbo.PhongKham p WHERE p.tenPhong = v.ten);

    -- 4. DICH VU.
    INSERT INTO dbo.DichVu (maDichVu, tenDichVu, donGiaNiemYet, trangThai)
    SELECT v.ma, v.ten, v.gia, N'DANG_PHUC_VU'
    FROM (VALUES
        ('DV0001', N'Khám và tư vấn nha khoa',       CAST(100000 AS decimal(18,2))),
        ('DV0002', N'Cạo vôi răng',                  CAST(250000 AS decimal(18,2))),
        ('DV0003', N'Trám răng thẩm mỹ',             CAST(350000 AS decimal(18,2))),
        ('DV0004', N'Nhổ răng thường',              CAST(300000 AS decimal(18,2))),
        ('DV0005', N'Nhổ răng khôn',                 CAST(1500000 AS decimal(18,2))),
        ('DV0006', N'Tẩy trắng răng',               CAST(1800000 AS decimal(18,2))),
        ('DV0007', N'Chụp X-quang răng',            CAST(150000 AS decimal(18,2))),
        ('DV0008', N'Điều trị tủy răng',            CAST(900000 AS decimal(18,2)))
    ) AS v(ma, ten, gia)
    WHERE NOT EXISTS (SELECT 1 FROM dbo.DichVu d WHERE d.maDichVu = v.ma)
      AND NOT EXISTS (SELECT 1 FROM dbo.DichVu d WHERE d.tenDichVu = v.ten);

    -- 5. THUOC.
    INSERT INTO dbo.Thuoc (maThuoc, tenThuoc, donViTinh)
    SELECT v.ma, v.ten, v.dvt
    FROM (VALUES
        ('TH0001', N'Paracetamol 500 mg', N'Viên'),
        ('TH0002', N'Amoxicillin 500 mg', N'Viên'),
        ('TH0003', N'Ibuprofen 200 mg',   N'Viên'),
        ('TH0004', N'Chlorhexidine',      N'Chai'),
        ('TH0005', N'Nước muối sinh lý',  N'Chai')
    ) AS v(ma, ten, dvt)
    WHERE NOT EXISTS (SELECT 1 FROM dbo.Thuoc t WHERE t.maThuoc = v.ma);

    -- 6. BENH NHAN: ma co dinh de chay lai khong trung; SĐT gia lap, duy nhat.
    INSERT INTO dbo.BenhNhan
        (maBenhNhan, hoTen, soDienThoai, cccd, ngaySinh, diaChi, tienSuDiUng)
    SELECT v.ma, v.ten, v.sdt, NULL, v.ngaySinh, N'Dữ liệu giả lập - TP.HCM', v.diUng
    FROM (VALUES
        ('BN2611010001', N'Bệnh nhân mẫu 01', '0123456701', CAST('1995-01-10' AS date), N'Không ghi nhận'),
        ('BN2611010002', N'Bệnh nhân mẫu 02', '0123456702', CAST('2002-02-11' AS date), N'Không ghi nhận'),
        ('BN2611010003', N'Bệnh nhân mẫu 03', '0123456703', CAST('1988-03-12' AS date), N'Không ghi nhận'),
        ('BN2611010004', N'Bệnh nhân mẫu 04', '0123456704', CAST('1999-04-13' AS date), N'Dị ứng thuốc (giả lập)'),
        ('BN2611010005', N'Bệnh nhân mẫu 05', '0123456705', CAST('2000-05-14' AS date), N'Không ghi nhận'),
        ('BN2611010006', N'Bệnh nhân mẫu 06', '0123456706', CAST('1991-06-15' AS date), N'Không ghi nhận'),
        ('BN2611010007', N'Bệnh nhân mẫu 07', '0123456707', CAST('2003-07-16' AS date), N'Không ghi nhận'),
        ('BN2611010008', N'Bệnh nhân mẫu 08', '0123456708', CAST('1997-08-17' AS date), N'Không ghi nhận'),
        ('BN2611010009', N'Bệnh nhân mẫu 09', '0123456709', CAST('1985-09-18' AS date), N'Không ghi nhận'),
        ('BN2611010010', N'Bệnh nhân mẫu 10', '0123456710', CAST('1996-10-19' AS date), N'Không ghi nhận')
    ) AS v(ma, ten, sdt, ngaySinh, diUng)
    WHERE NOT EXISTS (SELECT 1 FROM dbo.BenhNhan b WHERE b.maBenhNhan = v.ma)
      AND NOT EXISTS (SELECT 1 FROM dbo.BenhNhan b WHERE b.soDienThoai = v.sdt);

    -- 7. HO SO BENH AN: moi benh nhan mot ho so.
    INSERT INTO dbo.HoSoBenhAn (maHoSo, tienSuBenhLy, ghiChuChuyenMon, maBenhNhan)
    SELECT 'HS' + RIGHT(b.maBenhNhan, 10),
           N'Hồ sơ bệnh án giả lập', N'Dữ liệu dùng để kiểm thử giao diện', b.maBenhNhan
    FROM dbo.BenhNhan b
    WHERE b.maBenhNhan BETWEEN 'BN2611010001' AND 'BN2611010010'
      AND NOT EXISTS (SELECT 1 FROM dbo.HoSoBenhAn h WHERE h.maBenhNhan = b.maBenhNhan);

    -- 8. PHIEU KHAM HOM NAY: co CHO_KHAM, DANG_KHAM, HOAN_TAT de demo UC009.
    INSERT INTO dbo.PhieuKham
      (maPhieuKham, ngayKham, soThuTu, lyDoKham, chanDoan, ketQuaDieuTri,
       ghiChuThuocSuDung, ghiChuThuocTuMua, trangThai, maBenhNhan,
       maNhanVien, maPhong, maHoSo, maHoaDonPhiKham, phacDieuTri, ngayHenTiep)
    SELECT 'PK' + @maNgay + v.suffix, @ngay, v.stt, v.lyDo, v.chanDoan,
           v.ketQua, NULL, NULL, v.trangThai, v.benhNhan,
           v.bacSi, v.phong, 'HS' + RIGHT(v.benhNhan, 10), NULL,
           v.phacDo, CASE WHEN v.trangThai = N'HOAN_TAT' THEN DATEADD(day, 14, @ngay) END
    FROM (VALUES
        ('0001', 1, N'Đau răng hàm',       CAST(NULL AS nvarchar(500)), CAST(NULL AS nvarchar(500)), N'CHO_KHAM',  'BN2611010001', 'NV0002', 'P001', CAST(NULL AS nvarchar(500))),
        ('0002', 2, N'Kiểm tra định kỳ',   NULL, NULL, N'CHO_KHAM',  'BN2611010002', 'NV0002', 'P001', NULL),
        ('0003', 1, N'Ê buốt răng',        NULL, NULL, N'CHO_KHAM',  'BN2611010003', 'NV0003', 'P002', NULL),
        ('0004', 2, N'Đau nướu',          N'Viêm nướu nhẹ', NULL, N'DANG_KHAM', 'BN2611010004', 'NV0003', 'P002', NULL),
        ('0005', 3, N'Lấy cao răng',       N'Cao răng', N'Đã cạo vôi', N'HOAN_TAT', 'BN2611010005', 'NV0002', 'P001', N'Tái khám định kỳ'),
        ('0006', 3, N'Răng bị sâu',        N'Sâu răng nhẹ', N'Đã trám răng', N'HOAN_TAT', 'BN2611010006', 'NV0003', 'P002', N'Tái kiểm tra miếng trám'),
        ('0007', 1, N'Tư vấn niềng răng',  NULL, NULL, N'CHO_KHAM',  'BN2611010007', 'NV0002', 'P003', NULL),
        ('0008', 1, N'Khám đau răng',      NULL, NULL, N'CHO_KHAM',  'BN2611010008', 'NV0003', 'P004', NULL)
    ) AS v(suffix, stt, lyDo, chanDoan, ketQua, trangThai, benhNhan, bacSi, phong, phacDo)
    WHERE NOT EXISTS (SELECT 1 FROM dbo.PhieuKham p WHERE p.maPhieuKham = 'PK' + @maNgay + v.suffix)
      AND NOT EXISTS (SELECT 1 FROM dbo.PhieuKham p WHERE p.ngayKham = @ngay AND p.maPhong = v.phong AND p.soThuTu = v.stt);

    -- 9. HOA DON DICH VU CHO 2 PHIEU DA HOAN TAT.
    INSERT INTO dbo.HoaDon
      (maHoaDon, ngayThu, hinhThucThanhToan, daThanhToan, tienKhachDua,
       maBenhNhan, maNhanVien, maPhieuKham)
    SELECT v.maHD, DATEADD(hour, v.gio, CAST(@ngay AS datetime2(0))),
           v.hinhThuc, 1, v.tienDua, v.benhNhan, 'NV0001', v.maPK
    FROM (VALUES
        (@hd1, 10, N'TIEN_MAT',      CAST(300000 AS decimal(18,2)), 'BN2611010005', @pk5),
        (@hd2, 11, N'CHUYEN_KHOAN', CAST(350000 AS decimal(18,2)), 'BN2611010006', @pk6)
    ) AS v(maHD, gio, hinhThuc, tienDua, benhNhan, maPK)
    WHERE NOT EXISTS (SELECT 1 FROM dbo.HoaDon h WHERE h.maHoaDon = v.maHD)
      AND EXISTS (SELECT 1 FROM dbo.PhieuKham p WHERE p.maPhieuKham = v.maPK);

    -- 10. CHI TIET HOA DON (thanhTien = soLuong * donGia).
    INSERT INTO dbo.ChiTietHoaDon (maHoaDon, maDichVu, soLuong, donGia, thanhTien)
    SELECT v.hd, v.dv, 1, v.gia, v.gia
    FROM (VALUES
        (@hd1, 'DV0002', CAST(250000 AS decimal(18,2))),
        (@hd2, 'DV0003', CAST(350000 AS decimal(18,2)))
    ) AS v(hd, dv, gia)
    WHERE EXISTS (SELECT 1 FROM dbo.HoaDon h WHERE h.maHoaDon = v.hd)
      AND NOT EXISTS (SELECT 1 FROM dbo.ChiTietHoaDon c WHERE c.maHoaDon = v.hd AND c.maDichVu = v.dv);

    -- 11. CHI DINH DICH VU + SO RANG.
    INSERT INTO dbo.ChiDinhDichVu (maPhieuKham, maDichVu, soRang)
    SELECT v.pk, v.dv, v.soRang
    FROM (VALUES
        (@pk5, 'DV0002', N'16, 26'),
        (@pk6, 'DV0003', N'36')
    ) AS v(pk, dv, soRang)
    WHERE EXISTS (SELECT 1 FROM dbo.PhieuKham p WHERE p.maPhieuKham = v.pk)
      AND NOT EXISTS (SELECT 1 FROM dbo.ChiDinhDichVu c WHERE c.maPhieuKham = v.pk AND c.maDichVu = v.dv);

    -- 12. CHI TIET CAP THUOC - chi du lieu giao dien, khong phai chi dinh dieu tri.
    INSERT INTO dbo.ChiTietCapThuoc (maPhieuKham, maThuoc, soLuong, ghiChuCapThuoc)
    SELECT v.pk, v.thuoc, v.sl, N'Dữ liệu mẫu để kiểm thử'
    FROM (VALUES
        (@pk5, 'TH0005', 1),
        (@pk6, 'TH0001', 2)
    ) AS v(pk, thuoc, sl)
    WHERE EXISTS (SELECT 1 FROM dbo.PhieuKham p WHERE p.maPhieuKham = v.pk)
      AND NOT EXISTS (SELECT 1 FROM dbo.ChiTietCapThuoc c WHERE c.maPhieuKham = v.pk AND c.maThuoc = v.thuoc);

    -- 13. HANG HOA / TON KHO.
    INSERT INTO dbo.HangHoa (maHangHoa, tenHangHoa, donViTinh, soLuongTon, mucTonToiThieu)
    SELECT v.ma, v.ten, v.dvt, v.ton, v.minTon
    FROM (VALUES
        ('HH0001', N'Găng tay y tế',        N'Hộp',  40, 10),
        ('HH0002', N'Khẩu trang y tế',      N'Hộp',  25,  5),
        ('HH0003', N'Bông gòn',            N'Gói',  30,  8),
        ('HH0004', N'Kim tiêm',            N'Cái', 100, 20),
        ('HH0005', N'Ống hút nước bọt',    N'Cái',  80, 15),
        ('HH0006', N'Cốc súc miệng',       N'Cái', 120, 20)
    ) AS v(ma, ten, dvt, ton, minTon)
    WHERE NOT EXISTS (SELECT 1 FROM dbo.HangHoa h WHERE h.maHangHoa = v.ma);

    -- 14. CA THU NGAN (1 ca dang mo de demo).
    IF NOT EXISTS (SELECT 1 FROM dbo.CaThuNgan WHERE maCa = 'CT0001')
    BEGIN
        INSERT INTO dbo.CaThuNgan
          (maCa, maNhanVien, thoiGianMo, thoiGianChot, doanhThuDichVu, phiThuHo, trangThai)
        VALUES ('CT0001', 'NV0001', DATEADD(hour, 8, CAST(@ngay AS datetime2(0))),
                NULL, NULL, NULL, N'DANG_MO');
    END;

    -- 15. BO DEM: can bang so thu tu va ma hien co de han che trung ma khi tao moi.
    DECLARE @canDem TABLE (loaiMa varchar(10), ngay date, giaTri int);
    INSERT INTO @canDem (loaiMa, ngay, giaTri)
    VALUES
        ('NV',       '19000101', 7),
        ('DV',       '19000101', 8),
        ('TH',       '19000101', 5),
        ('BN',       '20261101',10),
        ('HS',       '20261101',10),
        ('PK',       @ngay,       8),
        ('HD',       @ngay,       2),
        ('STT-P001', @ngay,       3),
        ('STT-P002', @ngay,       3),
        ('STT-P003', @ngay,       1),
        ('STT-P004', @ngay,       1);

    UPDATE b
       SET b.giaTri = CASE WHEN b.giaTri < c.giaTri THEN c.giaTri ELSE b.giaTri END
    FROM dbo.BoDem b
    JOIN @canDem c ON c.loaiMa = b.loaiMa AND c.ngay = b.ngay;

    INSERT INTO dbo.BoDem (loaiMa, ngay, giaTri)
    SELECT c.loaiMa, c.ngay, c.giaTri
    FROM @canDem c
    WHERE NOT EXISTS (SELECT 1 FROM dbo.BoDem b WHERE b.loaiMa = c.loaiMa AND b.ngay = c.ngay);

    COMMIT TRANSACTION;
    PRINT N'THEM DU LIEU MAU THANH CONG - Khong xoa du lieu cu.';
END TRY
BEGIN CATCH
    IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
    THROW;
END CATCH;

-- KIEM TRA KET QUA (khong hien thi matKhau/hash).
SELECT t.name AS TenBang, SUM(p.rows) AS SoDong
FROM sys.tables t
JOIN sys.partitions p ON p.object_id = t.object_id AND p.index_id IN (0,1)
GROUP BY t.name
ORDER BY t.name;

SELECT tenDangNhap, maNhanVien, trangThai
FROM dbo.TaiKhoan
WHERE tenDangNhap IN ('NV0001','NV0005','NV0007')
ORDER BY tenDangNhap;

SELECT maPhieuKham, ngayKham, soThuTu, trangThai, maPhong
FROM dbo.PhieuKham
WHERE ngayKham = CAST(GETDATE() AS date)
ORDER BY maPhong, soThuTu;
