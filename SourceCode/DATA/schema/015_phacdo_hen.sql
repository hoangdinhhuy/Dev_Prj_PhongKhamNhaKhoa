-- 015_phacdo_hen.sql (Gop y GV 2/7: phac do dieu tri + lich hen tai kham theo tung buoi)
USE N15_PTUD_DB;
GO
ALTER TABLE PhieuKham ADD phacDieuTri nvarchar(500) NULL;   -- phac do bac si lap
ALTER TABLE PhieuKham ADD ngayHenTiep date NULL;            -- lich hen tai kham buoi tiep
GO
PRINT N'015_phacdo_hen OK';
GO
