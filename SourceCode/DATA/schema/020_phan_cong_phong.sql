USE N15_PTUD_DB;
GO
IF COL_LENGTH('dbo.PhongKham', 'maBacSi') IS NULL
    ALTER TABLE dbo.PhongKham ADD maBacSi varchar(12) NULL;
IF COL_LENGTH('dbo.PhongKham', 'maPhuTa') IS NULL
    ALTER TABLE dbo.PhongKham ADD maPhuTa varchar(12) NULL;
GO
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_PhongKham_BacSi')
    ALTER TABLE dbo.PhongKham ADD CONSTRAINT FK_PhongKham_BacSi
        FOREIGN KEY (maBacSi) REFERENCES dbo.NhanVien(maNhanVien);
IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_PhongKham_PhuTa')
    ALTER TABLE dbo.PhongKham ADD CONSTRAINT FK_PhongKham_PhuTa
        FOREIGN KEY (maPhuTa) REFERENCES dbo.NhanVien(maNhanVien);
GO
PRINT N'020_phan_cong_phong OK';
GO
