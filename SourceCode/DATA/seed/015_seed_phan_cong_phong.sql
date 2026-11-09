USE N15_PTUD_DB;
GO
IF NOT EXISTS (SELECT 1 FROM dbo.TaiKhoan WHERE maNhanVien = 'NV0006')
    INSERT INTO dbo.TaiKhoan (tenDangNhap, matKhau, trangThai, maNhanVien)
    VALUES ('NV0006',
            '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92',
            N'HOAT_DONG', 'NV0006');

-- Hai cap bac si - phu ta, moi cap phu trach hai ghe/phong.
-- Chi dien phan cong con thieu de khong ghi de thay doi tai co so.
UPDATE dbo.PhongKham SET maBacSi = 'NV0002', maPhuTa = 'NV0005'
WHERE maPhong IN ('P001', 'P003') AND maBacSi IS NULL AND maPhuTa IS NULL;
UPDATE dbo.PhongKham SET maBacSi = 'NV0003', maPhuTa = 'NV0006'
WHERE maPhong IN ('P002', 'P004') AND maBacSi IS NULL AND maPhuTa IS NULL;
GO
PRINT N'015_seed_phan_cong_phong OK';
GO
