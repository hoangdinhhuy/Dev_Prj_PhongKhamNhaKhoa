-- Tao lai bo du lieu demo 8 benh nhan va cac luot kham de thu nhieu nghiep vu.
-- DESTRUCTIVE: xoa toan bo du lieu benh nhan/ho so/phieu/hoa don hien co.
-- Chi chay sau khi da backup N15_PTUD_DB; mac dinh bi khoa an toan.
USE N15_PTUD_DB;
SET NOCOUNT ON;
SET XACT_ABORT ON;

DECLARE @xacNhan bit = 0; -- doi thanh 1 sau khi da backup va muon thay bo data
IF @xacNhan <> 1
    THROW 51000, N'Dung an toan: hay backup database, sau do doi @xacNhan = 1.', 1;

IF NOT EXISTS (
    SELECT 1 FROM msdb.dbo.backupset
    WHERE database_name = N'N15_PTUD_DB' AND type = 'D'
      AND backup_finish_date >= DATEADD(hour, -2, GETDATE())
)
    THROW 51001, N'Khong tim thay full backup N15_PTUD_DB trong 2 gio gan nhat.', 1;

DECLARE @homNay date = CONVERT(date, GETDATE());
DECLARE @homQua date = DATEADD(day, -1, @homNay);
DECLARE @ngayMa varchar(6) = CONVERT(varchar(6), @homNay, 12);
DECLARE @ngayMaCu varchar(6) = CONVERT(varchar(6), @homQua, 12);
DECLARE @bn varchar(12) = 'BN' + @ngayMa;
DECLARE @hs varchar(12) = 'HS' + @ngayMa;
DECLARE @pkN varchar(12) = 'PK' + @ngayMa;
DECLARE @pkC varchar(12) = 'PK' + @ngayMaCu;
DECLARE @hd varchar(12) = 'HD' + @ngayMa;

BEGIN TRY
    BEGIN TRANSACTION;

    DELETE FROM ChiTietHoaDon;
    DELETE FROM ChiDinhDichVu;
    DELETE FROM ChiTietCapThuoc;
    UPDATE PhieuKham SET maHoaDonPhiKham = NULL WHERE maHoaDonPhiKham IS NOT NULL;
    DELETE FROM HoaDon;
    DELETE FROM PhieuKham;
    DELETE FROM HoSoBenhAn;
    DELETE FROM BenhNhan;

    INSERT INTO BenhNhan (maBenhNhan, hoTen, soDienThoai, cccd, ngaySinh, diaChi, tienSuDiUng) VALUES
    (@bn+'9701', N'Nguyễn Minh Khang', '0909000101', '079090000101', '1990-01-15', N'25 Nguyễn Xí, TP.HCM', N'Dị ứng Penicillin'),
    (@bn+'9702', N'Trần Ngọc Bảo',    '0909000102', '079090000102', '1988-04-22', N'143 Xô Viết Nghệ Tĩnh, TP.HCM', N'Không ghi nhận dị ứng'),
    (@bn+'9703', N'Lê Gia Hân',        '0909000103', '079090000103', '1995-07-09', N'78 Võ Văn Ngân, TP.HCM', N'Dị ứng thuốc tê Lidocaine'),
    (@bn+'9704', N'Phạm Quốc An',      '0909000104', '079090000104', '2000-11-03', N'221 Phan Văn Trị, TP.HCM', N'Không ghi nhận dị ứng'),
    (@bn+'9705', N'Võ Thanh Mai',      '0909000105', '079090000105', '1987-12-18', N'56 Kha Vạn Cân, TP.HCM', N'Dị ứng Aspirin'),
    (@bn+'9706', N'Nguyễn Thảo Vy',    '0909000106', '079090000106', '1992-06-30', N'35 Nguyễn Văn Đậu, TP.HCM', N'Không ghi nhận dị ứng'),
    (@bn+'9707', N'Đỗ Khánh Long',     '0909000107', '079090000107', '1988-01-11', N'12 Song Hành, TP.HCM', N'Dị ứng Cephalosporin'),
    (@bn+'9708', N'Bùi Thanh Hà',      '0909000108', '079090000108', '1994-11-17', N'777 Hoàng Hoa Thám, TP.HCM', N'Không ghi nhận dị ứng');

    INSERT INTO HoSoBenhAn (maHoSo, tienSuBenhLy, ghiChuChuyenMon, maBenhNhan) VALUES
    (@hs+'9705', N'Từng điều trị sâu răng', N'Tái khám định kỳ', @bn+'9705'),
    (@hs+'9706', N'Viêm nướu nhẹ', N'Theo dõi vệ sinh răng miệng', @bn+'9706'),
    (@hs+'9707', N'Đã hàn răng năm trước', N'Có lịch sử điều trị', @bn+'9707'),
    (@hs+'9708', N'Chưa ghi nhận bệnh lý đặc biệt', N'Hồ sơ mẫu đầy đủ', @bn+'9708');

    -- BN9701-9702 chưa đóng phí; BN9703-9708 đã đóng phí khám ban đầu.
    INSERT INTO HoaDon (maHoaDon, ngayThu, hinhThucThanhToan, daThanhToan, tienKhachDua, maBenhNhan, maNhanVien, maPhieuKham) VALUES
    (@hd+'9701', CAST(@homNay AS datetime2(0)), N'TIEN_MAT', 1, 50000, @bn+'9703', 'NV0001', NULL),
    (@hd+'9702', CAST(@homNay AS datetime2(0)), N'CHUYEN_KHOAN', 1, 50000, @bn+'9704', 'NV0001', NULL),
    (@hd+'9703', CAST(@homNay AS datetime2(0)), N'TIEN_MAT', 1, 50000, @bn+'9705', 'NV0001', NULL),
    (@hd+'9704', CAST(@homNay AS datetime2(0)), N'TIEN_MAT', 1, 50000, @bn+'9706', 'NV0001', NULL),
    (@hd+'9705', CAST(@homNay AS datetime2(0)), N'CHUYEN_KHOAN', 1, 50000, @bn+'9707', 'NV0001', NULL),
    (@hd+'9706', CAST(@homNay AS datetime2(0)), N'TIEN_MAT', 1, 50000, @bn+'9708', 'NV0001', NULL);
    INSERT INTO ChiTietHoaDon (maHoaDon, maDichVu, soLuong, donGia, thanhTien)
    SELECT @hd + '970' + CONVERT(varchar(1), n), 'DV0001', 1, 50000, 50000
    FROM (VALUES (1),(2),(3),(4),(5),(6)) v(n);

    INSERT INTO PhieuKham (maPhieuKham, ngayKham, soThuTu, lyDoKham, chanDoan, ketQuaDieuTri, trangThai,
                           maBenhNhan, maNhanVien, maPhong, maHoSo, maHoaDonPhiKham) VALUES
    (@pkN+'9701', @homNay, 1, N'Khám tổng quát', N'Sâu răng hàm', NULL, N'DANG_KHAM', @bn+'9703', 'NV0002', 'P001', NULL, @hd+'9701'),
    (@pkN+'9702', @homNay, 1, N'Tái khám theo hẹn', N'Viêm nướu nhẹ', NULL, N'DANG_KHAM', @bn+'9704', 'NV0003', 'P002', NULL, @hd+'9702'),
    (@pkC+'9701', @homQua, 1, N'Đau răng khi nhai', N'Cao răng', N'Đã cạo vôi', N'HOAN_TAT', @bn+'9705', 'NV0002', 'P001', @hs+'9705', @hd+'9703'),
    (@pkC+'9702', @homQua, 2, N'Khám tổng quát', N'Sâu răng hàm', N'Đã hàn răng composite', N'HOAN_TAT', @bn+'9706', 'NV0003', 'P001', @hs+'9706', @hd+'9704'),
    (@pkC+'9703', @homQua, 1, N'Tái khám theo hẹn', N'Răng khôn mọc lệch', N'Đã nhổ răng thường', N'HOAN_TAT', @bn+'9707', 'NV0002', 'P002', @hs+'9707', @hd+'9705'),
    (@pkC+'9704', @homQua, 2, N'Khám tổng quát', N'Răng miệng ổn định', N'Tư vấn vệ sinh răng miệng', N'HOAN_TAT', @bn+'9708', 'NV0003', 'P002', @hs+'9708', @hd+'9706');

    -- Hai luot dang kham; ba luot hoan tat co dich vu chua thanh toan;
    -- luot hoan tat con lai khong phat sinh dich vu.
    INSERT INTO ChiDinhDichVu (maPhieuKham, maDichVu) VALUES
    (@pkN+'9701', 'DV0006'), (@pkN+'9702', 'DV0002'), (@pkC+'9701', 'DV0002'),
    (@pkC+'9702', 'DV0003'), (@pkC+'9702', 'DV0006'), (@pkC+'9703', 'DV0004');
    INSERT INTO ChiTietCapThuoc (maPhieuKham, maThuoc, soLuong, ghiChuCapThuoc) VALUES
    (@pkN+'9701', 'TH0001', 6, N'Uống sau ăn, ngày 2 lần');

    -- Dong bo bo dem ma de cac lan tao moi tiep theo khong trung voi bo demo nay.
    MERGE BoDem AS t USING (VALUES ('BN', @homNay, 9708), ('HS', @homNay, 9708),
        ('PK', @homNay, 9702), ('PK', @homQua, 9704), ('HD', @homNay, 9706),
        ('STT-P001', @homNay, 1), ('STT-P002', @homNay, 1),
        ('STT-P001', @homQua, 2), ('STT-P002', @homQua, 2)) AS s(loaiMa, ngay, giaTri)
      ON t.loaiMa = s.loaiMa AND t.ngay = s.ngay
    WHEN MATCHED THEN UPDATE SET giaTri = s.giaTri
    WHEN NOT MATCHED THEN INSERT (loaiMa, ngay, giaTri) VALUES (s.loaiMa, s.ngay, s.giaTri);

    COMMIT TRANSACTION;
    SELECT maBenhNhan, hoTen, soDienThoai FROM BenhNhan ORDER BY maBenhNhan;
END TRY
BEGIN CATCH
    IF XACT_STATE() <> 0 ROLLBACK TRANSACTION;
    THROW;
END CATCH;
GO
