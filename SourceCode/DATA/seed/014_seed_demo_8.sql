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
