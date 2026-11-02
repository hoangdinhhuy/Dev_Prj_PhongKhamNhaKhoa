-- 012_hoadon_phieukham.sql — lien ket hoa don dich vu cuoi luot voi phieu kham
-- (chuc nang "Thanh toan" + "HOA DON KHAM" trong so do luong Word — LT-06)
USE N15_PTUD_DB;
GO
ALTER TABLE HoaDon ADD maPhieuKham varchar(12) NULL REFERENCES PhieuKham(maPhieuKham);
GO
PRINT N'012_hoadon_phieukham OK';
GO
