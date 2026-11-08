-- Enforce one patient record per phone number, including concurrent requests.
USE N15_PTUD_DB;
GO

IF EXISTS (
    SELECT soDienThoai
    FROM dbo.BenhNhan
    GROUP BY soDienThoai
    HAVING COUNT(*) > 1
)
BEGIN
    SELECT soDienThoai, COUNT(*) AS soHoSo
    FROM dbo.BenhNhan
    GROUP BY soDienThoai
    HAVING COUNT(*) > 1;
    ;THROW 51020, N'Duplicate patient phone numbers exist. Resolve them before applying this constraint.', 1;
END;
GO

IF NOT EXISTS (
    SELECT 1 FROM sys.key_constraints WHERE name = N'UQ_BenhNhan_SoDienThoai'
)
BEGIN
    ALTER TABLE dbo.BenhNhan
        ADD CONSTRAINT UQ_BenhNhan_SoDienThoai UNIQUE (soDienThoai);
END;
GO

PRINT N'014_unique_benhnhan_sdt OK';
GO
