-- 019_ca_thu_ngan.sql (09/11 — chốt ca thu ngân như app quản lý POS)
-- Ca lam viec: mo ca -> thu trong ca -> CHOT CA (bang ke + xac nhan) -> nhan vien sau NHAN CA.
USE N15_PTUD_DB;
GO
CREATE TABLE CaThuNgan (
    maCa           varchar(12)   NOT NULL PRIMARY KEY,     -- CTnnnn
    maNhanVien     varchar(12)   NOT NULL REFERENCES NhanVien(maNhanVien),  -- nhan vien MO ca
    thoiGianMo     datetime2(0)  NOT NULL,
    thoiGianChot   datetime2(0)  NULL,                      -- NULL = DANG MO
    doanhThuDichVu decimal(18,2) NULL,                      -- snapshot luc chot (dich vu cuoi luot)
    phiThuHo       decimal(18,2) NULL,                      -- snapshot luc chot (phi kham dau vao)
    trangThai      nvarchar(10)  NOT NULL DEFAULT N'DANG_MO'
                   CHECK (trangThai IN (N'DANG_MO', N'DA_CHOT'))
);
GO
PRINT N'019_ca_thu_ngan OK';
GO
