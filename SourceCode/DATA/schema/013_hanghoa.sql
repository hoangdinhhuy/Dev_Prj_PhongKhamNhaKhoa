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
