-- 016_tai_khoan_ca_nhan.sql — doi tai khoan vai tro (letan/bacsi1/...) thanh TAI KHOAN CA NHAN
-- (Gop y GV: tai khoan phai gan nguoi dung cu the, khong dung ten vai tro lam chung)
USE N15_PTUD_DB;
GO
UPDATE TaiKhoan SET tenDangNhap = N'huyhd'     WHERE tenDangNhap = N'letan';
UPDATE TaiKhoan SET tenDangNhap = N'thanghq'   WHERE tenDangNhap = N'bacsi1';
UPDATE TaiKhoan SET tenDangNhap = N'chaulm'    WHERE tenDangNhap = N'bacsi2';
UPDATE TaiKhoan SET tenDangNhap = N'thanhtpc'  WHERE tenDangNhap = N'phuta';
UPDATE TaiKhoan SET tenDangNhap = N'duynt'     WHERE tenDangNhap = N'quanly';
UPDATE TaiKhoan SET tenDangNhap = N'haph'      WHERE tenDangNhap = N'lockacc';
GO
PRINT N'016_tai_khoan_ca_nhan OK';
GO
