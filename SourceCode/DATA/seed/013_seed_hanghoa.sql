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
