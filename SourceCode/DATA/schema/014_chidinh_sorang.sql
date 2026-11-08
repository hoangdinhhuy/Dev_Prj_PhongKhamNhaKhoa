-- 014_chidinh_sorang.sql (Gop y GV 2/7: ghi ro rang so bao nhieu khi chi dinh dieu tri)
USE N15_PTUD_DB;
GO
ALTER TABLE ChiDinhDichVu ADD soRang nvarchar(100) NULL;   -- FDI, vd: "36,37"
GO
PRINT N'014_chidinh_sorang OK';
GO
