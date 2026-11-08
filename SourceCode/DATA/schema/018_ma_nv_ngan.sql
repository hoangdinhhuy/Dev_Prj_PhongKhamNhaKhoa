-- 018_ma_nv_ngan.sql (user chot 08/11: ma nhan vien ngan NV0001 — thuc te dinh, khong gan ngay)
-- NV la thuc the TINH (nhan su) -> ma NV0001..; BN/HS/PK/HD la giao dich/don -> giu yyMMddNNNN de ghi ngay phat sinh.
-- Truong maNhanVien varchar(12) giu nguyen (NV0001 < 12 ky tu).
USE N15_PTUD_DB;
GO
PRINT N'018: chay qua JDBC (NOCHECK FK -> doi ma -> CHECK lai) — xem script java kem theo';
GO
