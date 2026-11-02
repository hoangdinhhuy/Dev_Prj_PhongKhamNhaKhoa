-- 002_seed_nhanvien_taikhoan.sql (chủ file: Duy)
-- Ten nhan vien = ten thanh vien code luong do (theo TaiLieu/docs/tasks):
-- LE_TAN = Huy, BAC_SI = Thang, PHU_TA = Thanh, QUAN_LY = Duy (bacsi2 + lockacc la tai khoan demo).
-- Mat khau chung: 123456  ->  SHA-256 = 8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92
USE N15_PTUD_DB;
GO
INSERT INTO NhanVien (maNhanVien, hoTen, soDienThoai, ngaySinh, gioiTinh, cccd, chucVu, conLamViec) VALUES
(N'NV2608010001', N'Hoàng Đình Huy',   N'0901234561', '1999-03-12', 0, '079199000001', N'LE_TAN',  1),
(N'NV2608010002', N'Huỳnh Quốc Thắng', N'0901234562', '1995-07-08', 1, '079199500002', N'BAC_SI',  1),
(N'NV2608010003', N'Lê Minh Châu',      N'0901234563', '1994-11-25', 1, '079199400003', N'BAC_SI',  1),
(N'NV2608010004', N'Phạm Thu Hà',       N'0901234564', '1997-01-30', 0, '079199700004', N'LE_TAN',  1),
(N'NV2608010005', N'Phan Chí Thanh',    N'0901234565', '1993-05-19', 1, '079199300005', N'PHU_TA',  1),
(N'NV2608010006', N'Bùi Ngọc Diệp',     N'0901234566', '1998-09-02', 0, '079199800006', N'PHU_TA',  1),
(N'NV2608010007', N'Nguyễn Tuấn Duy',   N'0901234567', '1990-12-15', 1, '079199000007', N'QUAN_LY', 1),
(N'NV2608010008', N'Đặng Hải Yến',      N'0901234568', '1996-06-21', 0, NULL,           N'BAC_SI',  0);
GO
INSERT INTO TaiKhoan (tenDangNhap, matKhau, trangThai, maNhanVien) VALUES
(N'letan',  '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'HOAT_DONG', 'NV2608010001'),
(N'bacsi1', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'HOAT_DONG', 'NV2608010002'),
(N'bacsi2', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'HOAT_DONG', 'NV2608010003'),
(N'phuta',  '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'HOAT_DONG', 'NV2608010005'),
(N'quanly', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'HOAT_DONG', 'NV2608010007'),
(N'lockacc','8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', N'BI_KHOA',   'NV2608010004');
GO
-- Tai khoan BI_KHOA dung de test UC001 (TB02). NV2608010008 (bac si nghi viec) khong co tai khoan.
PRINT N'002_seed_nhanvien_taikhoan OK';
GO
