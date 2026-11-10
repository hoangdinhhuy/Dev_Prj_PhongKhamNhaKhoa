--use master 
--drop database N15_PTUD_DB
-- 000_hatang.sql — Hạ tầng CSDL (chủ file: Thanh)
IF DB_ID('N15_PTUD_DB') IS NULL
BEGIN
    CREATE DATABASE N15_PTUD_DB;
END
GO
USE N15_PTUD_DB;
GO
-- Bo dem ma tu dong: 1 dong cho moi (loaiMa, ngay). Cap ma nguyen tu bang UPDATE ... OUTPUT
CREATE TABLE BoDem (
    loaiMa varchar(10)  NOT NULL,
    ngay   date         NOT NULL,
    giaTri int          NOT NULL DEFAULT 0,
    CONSTRAINT PK_BoDem PRIMARY KEY (loaiMa, ngay),
    CONSTRAINT CK_BoDem_GiaTri CHECK (giaTri >= 0)
);
GO
-- loaiMa: NV, BN, HS, PK, HD, DV, TH (kiem so trong ma), va 'STT-<maPhong>' cho so thu tu
PRINT N'000_hatang OK';
GO



-- 001_phongkham.sql (chủ file: Thanh)
USE N15_PTUD_DB;
GO
CREATE TABLE PhongKham (
    maPhong      varchar(4)   NOT NULL PRIMARY KEY,
    tenPhong     nvarchar(50) NOT NULL UNIQUE,
    dangNhanBenh bit          NOT NULL DEFAULT 0
);
GO
PRINT N'001_phongkham OK';
GO



-- 002_nhanvien_taikhoan.sql (chủ file: Duy)
USE N15_PTUD_DB;
GO
CREATE TABLE NhanVien (
    maNhanVien  varchar(12)   NOT NULL PRIMARY KEY,   -- NVNNNN (ma nhan su TINH, khong gan ngay)
    hoTen       nvarchar(100) NOT NULL,
    soDienThoai varchar(10)   NULL,
    ngaySinh    date          NULL,
    gioiTinh    bit           NULL,
    cccd        varchar(12)   NULL,   -- khong UNIQUE o DB (unique index khong chap nhan nhieu NULL); kiem tra o BUS khi can
    chucVu      nvarchar(10)  NOT NULL CHECK (chucVu IN (N'LE_TAN', N'BAC_SI', N'PHU_TA', N'QUAN_LY')),
    conLamViec  bit           NOT NULL DEFAULT 1
);
GO
CREATE TABLE TaiKhoan (
    tenDangNhap nvarchar(50) NOT NULL PRIMARY KEY,
    matKhau     varchar(64)  NOT NULL,                 -- hash SHA-256 (hex 64 ky tu), KHONG luu mat khau goc
    trangThai   nvarchar(10) NOT NULL DEFAULT N'HOAT_DONG' CHECK (trangThai IN (N'HOAT_DONG', N'BI_KHOA')),
    maNhanVien  varchar(12)  NOT NULL UNIQUE REFERENCES NhanVien(maNhanVien)
);
GO
PRINT N'002_nhanvien_taikhoan OK';
GO


-- 003_dichvu.sql (chủ file: Duy)
USE N15_PTUD_DB;
GO
CREATE TABLE DichVu (
    maDichVu       varchar(6)    NOT NULL PRIMARY KEY,  -- DVNNNN
    tenDichVu      nvarchar(100) NOT NULL UNIQUE,
    donGiaNiemYet  decimal(18,2) NOT NULL CHECK (donGiaNiemYet >= 0),
    trangThai      nvarchar(15)  NOT NULL DEFAULT N'DANG_PHUC_VU' CHECK (trangThai IN (N'DANG_PHUC_VU', N'NGUNG_PHUC_VU'))
);
GO
PRINT N'003_dichvu OK';
GO

-- 004_thuoc.sql (chủ file: Thắng)
USE N15_PTUD_DB;
GO
CREATE TABLE Thuoc (
    maThuoc   varchar(6)    NOT NULL PRIMARY KEY,   -- THNNNN
    tenThuoc  nvarchar(100) NOT NULL,
    donViTinh nvarchar(20)  NOT NULL
);
GO
PRINT N'004_thuoc OK';
GO


-- 005_benhnhan.sql (chủ file: Huy)
-- LUU Y: cccd KHONG dat UNIQUE o DB vi SQL Server khong cho phep unique index
-- chap nhieu gia tri NULL (cau hinh filtered index bat buoc SET option tren moi
-- phien ket noi — rui ro voi JDBC). Tinh duy nhat cccd do BenhNhan_BUS kiem tra
-- truoc khi luu (TB13), so dien thoai KHONG la ma dinh danh duy nhat (chot cua nhom).
USE N15_PTUD_DB;
GO
CREATE TABLE BenhNhan (
    maBenhNhan   varchar(12)   NOT NULL PRIMARY KEY,  -- BNyyMMddNNNN
    hoTen        nvarchar(100) NOT NULL,
    soDienThoai  varchar(10)   NOT NULL CHECK (soDienThoai LIKE '0%' AND LEN(soDienThoai) = 10),
    cccd         varchar(12)   NULL CHECK (cccd IS NULL OR LEN(cccd) = 12),
    ngaySinh     date          NOT NULL,
    diaChi       nvarchar(200) NULL,
    tienSuDiUng  nvarchar(500) NULL
);
GO
PRINT N'005_benhnhan OK';
GO



-- 006_hosobenhan.sql (chủ file: Thắng) — 1 benh nhan chi co 1 ho so
USE N15_PTUD_DB;
GO
CREATE TABLE HoSoBenhAn (
    maHoSo           varchar(12)   NOT NULL PRIMARY KEY,   -- HSyyMMddNNNN
    tienSuBenhLy     nvarchar(500) NULL,
    ghiChuChuyenMon  nvarchar(500) NULL,
    maBenhNhan       varchar(12)   NOT NULL UNIQUE REFERENCES BenhNhan(maBenhNhan)
);
GO
PRINT N'006_hosobenhan OK';
GO


-- 007_phieukham.sql (chủ file: Huy)
USE N15_PTUD_DB;
GO
CREATE TABLE PhieuKham (
    maPhieuKham        varchar(12)   NOT NULL PRIMARY KEY,  -- PKyyMMddNNNN
    ngayKham           date          NOT NULL,
    soThuTu            int           NOT NULL CHECK (soThuTu > 0),
    lyDoKham           nvarchar(500) NOT NULL,
    chanDoan           nvarchar(500) NULL,
    ketQuaDieuTri      nvarchar(500) NULL,
    ghiChuThuocSuDung  nvarchar(500) NULL,
    ghiChuThuocTuMua   nvarchar(500) NULL,
    trangThai          nvarchar(10)  NOT NULL DEFAULT N'CHO_KHAM'
                       CHECK (trangThai IN (N'CHO_KHAM', N'DANG_KHAM', N'HOAN_TAT')),
    maBenhNhan         varchar(12)   NOT NULL REFERENCES BenhNhan(maBenhNhan),
    maNhanVien         varchar(12)   NOT NULL REFERENCES NhanVien(maNhanVien),  -- bac si
    maPhong            varchar(4)    NOT NULL REFERENCES PhongKham(maPhong),
    maHoSo             varchar(12)   NULL REFERENCES HoSoBenhAn(maHoSo),
    -- KHONG UNIQUE o DB: SQL Server khong cho unique index chap nhieu nhieu NULL;
    -- rang buoc "1 hoa don phi chi gan 1 phieu" do BUS kiem tra truoc khi gan.
    maHoaDonPhiKham    varchar(12)   NULL,  -- FK them o 008 (sau khi co bang HoaDon)
    CONSTRAINT UQ_PhieuKham_STT UNIQUE (ngayKham, maPhong, soThuTu)
);
GO
PRINT N'007_phieukham OK';
GO

-- 008_hoadon_chitiethoadon.sql (chủ file: Huy)
USE N15_PTUD_DB;
GO
CREATE TABLE HoaDon (
    maHoaDon            varchar(12)   NOT NULL PRIMARY KEY,  -- HDyyMMddNNNN
    ngayThu             datetime2(0)  NOT NULL,
    hinhThucThanhToan   nvarchar(15)  NOT NULL CHECK (hinhThucThanhToan IN (N'TIEN_MAT', N'CHUYEN_KHOAN')),
    daThanhToan         bit           NOT NULL DEFAULT 0,
    tienKhachDua        decimal(18,2) NULL,
    maBenhNhan          varchar(12)   NOT NULL REFERENCES BenhNhan(maBenhNhan),
    maNhanVien          varchar(12)   NOT NULL REFERENCES NhanVien(maNhanVien)  -- le tan thu tien
);
GO
-- thanhTien la cot thuong + CHECK nhat quan (thay cho computed PERSISTED:
-- cot computed bat buoc SET option tren moi phien ket noi — rui ro voi JDBC).
CREATE TABLE ChiTietHoaDon (
    maHoaDon  varchar(12)   NOT NULL REFERENCES HoaDon(maHoaDon),
    maDichVu  varchar(6)    NOT NULL REFERENCES DichVu(maDichVu),
    soLuong   int           NOT NULL CHECK (soLuong > 0),
    donGia    decimal(18,2) NOT NULL,                 -- snapshot gia tai thoi diem thu
    thanhTien decimal(18,2) NOT NULL,
    CONSTRAINT CK_CTHD_ThanhTien CHECK (thanhTien = soLuong * donGia),
    CONSTRAINT PK_ChiTietHoaDon PRIMARY KEY (maHoaDon, maDichVu)
);
GO
-- FK hoa don phi kham them sau vi PhieuKham tao o 007 truoc HoaDon
ALTER TABLE PhieuKham ADD CONSTRAINT FK_PhieuKham_HoaDon
    FOREIGN KEY (maHoaDonPhiKham) REFERENCES HoaDon(maHoaDon);
GO
PRINT N'008_hoadon_chitiethoadon OK';
GO



-- 009_chidinhdichvu.sql (chủ file: Thắng)
USE N15_PTUD_DB;
GO
CREATE TABLE ChiDinhDichVu (
    maPhieuKham varchar(12) NOT NULL REFERENCES PhieuKham(maPhieuKham),
    maDichVu    varchar(6)  NOT NULL REFERENCES DichVu(maDichVu),
    CONSTRAINT PK_ChiDinhDichVu PRIMARY KEY (maPhieuKham, maDichVu)
);
GO
PRINT N'009_chidinhdichvu OK';
GO


-- 010_chitietcapthuoc.sql (chủ file: Thắng)
USE N15_PTUD_DB;
GO
CREATE TABLE ChiTietCapThuoc (
    maPhieuKham     varchar(12)   NOT NULL REFERENCES PhieuKham(maPhieuKham),
    maThuoc         varchar(6)    NOT NULL REFERENCES Thuoc(maThuoc),
    soLuong         int           NOT NULL CHECK (soLuong > 0),
    ghiChuCapThuoc  nvarchar(300) NULL,
    CONSTRAINT PK_ChiTietCapThuoc PRIMARY KEY (maPhieuKham, maThuoc)
);
GO
PRINT N'010_chitietcapthuoc OK';
GO


-- 012_hoadon_phieukham.sql — lien ket hoa don dich vu cuoi luot voi phieu kham
-- (chuc nang "Thanh toan" + "HOA DON KHAM" trong so do luong Word — LT-06)
USE N15_PTUD_DB;
GO
ALTER TABLE HoaDon ADD maPhieuKham varchar(12) NULL REFERENCES PhieuKham(maPhieuKham);
GO
PRINT N'012_hoadon_phieukham OK';
GO


USE N15_PTUD_DB;
GO
IF OBJECT_ID(N'dbo.HangHoa', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.HangHoa (
        maHangHoa varchar(10) NOT NULL PRIMARY KEY,
        tenHangHoa nvarchar(100) NOT NULL,
        donViTinh nvarchar(30) NOT NULL,
        soLuongTon int NOT NULL CONSTRAINT CK_HangHoa_SoLuong CHECK (soLuongTon >= 0),
        mucTonToiThieu int NOT NULL CONSTRAINT CK_HangHoa_MucTon CHECK (mucTonToiThieu >= 0)
    );
END
GO
PRINT N'013_hanghoa OK';
GO



-- 014_chidinh_sorang.sql (Gop y GV 2/7: ghi ro rang so bao nhieu khi chi dinh dieu tri)
USE N15_PTUD_DB;
GO
ALTER TABLE ChiDinhDichVu ADD soRang nvarchar(100) NULL;   -- FDI, vd: "36,37"
GO
PRINT N'014_chidinh_sorang OK';
GO


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



-- 015_phacdo_hen.sql (Gop y GV 2/7: phac do dieu tri + lich hen tai kham theo tung buoi)
USE N15_PTUD_DB;
GO
ALTER TABLE PhieuKham ADD phacDieuTri nvarchar(500) NULL;   -- phac do bac si lap
ALTER TABLE PhieuKham ADD ngayHenTiep date NULL;            -- lich hen tai kham buoi tiep
GO
PRINT N'015_phacdo_hen OK';
GO



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


-- 017_tai_khoan_ma_nv.sql (Gop y GV P2-4: "moi nhan vien dung MA NHAN SU/tai khoan dinh danh rieng")
-- Username = chinh MA NHAN VIEN (NVyyMMddNNNN) — khong dung biet danh ca nhan (huyhd...), khong dung ten vai tro.
USE N15_PTUD_DB;
GO
UPDATE TaiKhoan SET tenDangNhap = maNhanVien;   -- 1-1: TaiKhoan.maNhanVien la UNIQUE FK
GO
PRINT N'017_tai_khoan_ma_nv OK';
GO


-- 018_ma_nv_ngan.sql (user chot 08/11: ma nhan vien ngan NV0001 — thuc te dinh, khong gan ngay)
-- NV la thuc the TINH (nhan su) -> ma NV0001..; BN/HS/PK/HD la giao dich/don -> giu yyMMddNNNN de ghi ngay phat sinh.
-- Truong maNhanVien varchar(12) giu nguyen (NV0001 < 12 ky tu).
USE N15_PTUD_DB;
GO
PRINT N'018: chay qua JDBC (NOCHECK FK -> doi ma -> CHECK lai) — xem script java kem theo';
GO


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
