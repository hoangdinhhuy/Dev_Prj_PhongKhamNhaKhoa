-- 017_tai_khoan_ma_nv.sql (Gop y GV P2-4: "moi nhan vien dung MA NHAN SU/tai khoan dinh danh rieng")
-- Username = chinh MA NHAN VIEN (NVyyMMddNNNN) — khong dung biet danh ca nhan (huyhd...), khong dung ten vai tro.
USE N15_PTUD_DB;
GO
UPDATE TaiKhoan SET tenDangNhap = maNhanVien;   -- 1-1: TaiKhoan.maNhanVien la UNIQUE FK
GO
PRINT N'017_tai_khoan_ma_nv OK';
GO
