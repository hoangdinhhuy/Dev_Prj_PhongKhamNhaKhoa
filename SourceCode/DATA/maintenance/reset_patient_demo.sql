-- DANGER: Xoa toan bo benh nhan, ho so benh an, phieu kham, chi dinh, cap thuoc va hoa don.
-- Giu nguyen nhan vien/tai khoan, phong kham, dich vu, thuoc va hang hoa.
-- Mac dinh khong chay. Chi doi @xacNhan thanh 1 sau khi da sao luu database.
USE N15_PTUD_DB;
SET NOCOUNT ON;
SET XACT_ABORT ON;

DECLARE @xacNhan bit = 0;
IF @xacNhan <> 1
    THROW 51000, N'Dung an toan: hay xac nhan reset va kiem tra backup truoc.', 1;

IF NOT EXISTS (
    SELECT 1 FROM msdb.dbo.backupset
    WHERE database_name = N'N15_PTUD_DB' AND type = 'D'
      AND backup_finish_date >= DATEADD(hour, -2, GETDATE())
)
    THROW 51001, N'Khong tim thay full backup N15_PTUD_DB trong 2 gio gan nhat.', 1;

BEGIN TRY
    BEGIN TRANSACTION;

    DELETE FROM ChiTietHoaDon;
    DELETE FROM ChiDinhDichVu;
    DELETE FROM ChiTietCapThuoc;
    -- Gỡ liên kết hóa đơn phí (PhieuKham → HoaDon) trước khi dọn hóa đơn.
    UPDATE PhieuKham SET maHoaDonPhiKham = NULL WHERE maHoaDonPhiKham IS NOT NULL;
    DELETE FROM HoaDon;
    DELETE FROM PhieuKham;
    DELETE FROM HoSoBenhAn;
    DELETE FROM BenhNhan;

    DECLARE @ngay date = CONVERT(date, GETDATE());
    DECLARE @boDem int = 0;
    SELECT @boDem = giaTri FROM BoDem WITH (UPDLOCK, HOLDLOCK)
    WHERE loaiMa = 'BN' AND ngay = @ngay;
    IF @boDem > 9994
        THROW 51002, N'Bo dem ma benh nhan da vuot gioi han ma trong ngay.', 1;

    DECLARE @prefix varchar(8) = 'BN' + CONVERT(varchar(6), @ngay, 12);
    DECLARE @ma1 varchar(12) = @prefix + RIGHT('0000' + CAST(@boDem + 1 AS varchar(4)), 4);
    DECLARE @ma2 varchar(12) = @prefix + RIGHT('0000' + CAST(@boDem + 2 AS varchar(4)), 4);
    DECLARE @ma3 varchar(12) = @prefix + RIGHT('0000' + CAST(@boDem + 3 AS varchar(4)), 4);
    DECLARE @ma4 varchar(12) = @prefix + RIGHT('0000' + CAST(@boDem + 4 AS varchar(4)), 4);
    DECLARE @ma5 varchar(12) = @prefix + RIGHT('0000' + CAST(@boDem + 5 AS varchar(4)), 4);

    INSERT INTO BenhNhan (maBenhNhan, hoTen, soDienThoai, cccd, ngaySinh, diaChi, tienSuDiUng) VALUES
    (@ma1, N'Nguyễn Minh Khang', '0909000101', '079090000101', '1990-01-15', N'Địa chỉ demo 01, TP.HCM', N'Dị ứng Penicillin'),
    (@ma2, N'Trần Ngọc Bảo',    '0909000102', '079090000102', '1988-04-22', N'Địa chỉ demo 02, TP.HCM', N'Không ghi nhận dị ứng'),
    (@ma3, N'Lê Gia Hân',        '0909000103', '079090000103', '1995-07-09', N'Địa chỉ demo 03, TP.HCM', N'Dị ứng thuốc tê Lidocaine'),
    (@ma4, N'Phạm Quốc An',      '0909000104', '079090000104', '2000-11-03', N'Địa chỉ demo 04, TP.HCM', N'Không ghi nhận dị ứng'),
    (@ma5, N'Võ Thanh Mai',      '0909000105', '079090000105', '1987-12-18', N'Địa chỉ demo 05, TP.HCM', N'Dị ứng Aspirin');

    IF EXISTS (SELECT 1 FROM BoDem WHERE loaiMa = 'BN' AND ngay = @ngay)
        UPDATE BoDem SET giaTri = @boDem + 5 WHERE loaiMa = 'BN' AND ngay = @ngay;
    ELSE
        INSERT INTO BoDem (loaiMa, ngay, giaTri) VALUES ('BN', @ngay, 5);

    COMMIT TRANSACTION;
    SELECT maBenhNhan, hoTen, soDienThoai FROM BenhNhan ORDER BY maBenhNhan;
END TRY
BEGIN CATCH
    IF XACT_STATE() <> 0 ROLLBACK TRANSACTION;
    THROW;
END CATCH;
