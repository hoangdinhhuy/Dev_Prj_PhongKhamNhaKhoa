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
