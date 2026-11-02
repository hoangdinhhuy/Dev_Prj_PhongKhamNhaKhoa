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
    DECLARE @bs varchar(12) = CASE WHEN @i % 2 = 0 THEN 'NV2608010002' ELSE 'NV2608010003' END;
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
