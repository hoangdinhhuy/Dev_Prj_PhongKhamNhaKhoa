-- 005_seed_benhnhan.sql (chủ file: Huy)
-- 10 benh nhan demo, thong tin DAY DU (cccd + dia chi + tien su) de kiem tra hien thi chi tiet.
USE N15_PTUD_DB;
GO
INSERT INTO BenhNhan (maBenhNhan, hoTen, soDienThoai, cccd, ngaySinh, diaChi, tienSuDiUng) VALUES
('BN2611010001', N'Nguyễn Văn An',   '0901111001', '079199001001', '1990-05-04', N'25 Nguyễn Xí, Q. Thủ Đức, TP.HCM',    N'Dị ứng Penicillin - từng sốt phát ban khi uống'),
('BN2611010002', N'Trần Thị Bình',   '0901111002', '079198502002', '1985-08-19', N'143 Xô Viết Nghệ Tĩnh, Q. Bình Thạnh', N'Dị ứng thuốc tê (Lidocaine)'),
('BN2611010003', N'Lê Hoàng Cương',  '0901111003', '079200103003', '2001-12-01', N'78 Võ Văn Ngân, TP. Thủ Đức',          N'Không ghi nhận dị ứng'),
('BN2611010004', N'Phạm Mỹ Duyên',   '0901111004', '079199804004', '1998-02-14', N'221 Phan Văn Trị, Q. Gò Vấp',          N'Dị ứng Penicillin'),
('BN2611010005', N'Hoàng Gia Đức',   '0901111005', '079200105005', '2001-04-27', N'56 Kha Vạn Cân, Dĩ An, Bình Dương',    N'Không ghi nhận dị ứng'),
('BN2611010006', N'Đỗ Khánh Giang',  '0901111006', '079197906006', '1979-10-09', N'90 Cộng Hòa, Q. Tân Bình',             N'Dị ứng Aspirin - đau dạ dày'),
('BN2611010007', N'Bùi Thanh Hà',    '0901111007', '079199207007', '1992-06-30', N'35 Nguyễn Văn Đậu, Q. Phú Nhuận',      N'Không ghi nhận dị ứng'),
('BN2611010008', N'Ngô Trí Hùng',    '0901111008', '079198808008', '1988-01-11', N'12 Song Hành, TP. Thủ Đức',            N'Dị ứng kháng sinh nhóm Cephalosporin'),
('BN2611010009', N'Lý Tú Kiên',      '0901111009', '079200509009', '2005-03-22', N'340 Đỗ Xuân Hợp, Q. 9 (TP. Thủ Đức)',  N'Không ghi nhận dị ứng'),
('BN2611010010', N'Trương Ngọc Lam', '0901111010', '079199410010', '1994-11-17', N'777 Hoàng Hoa Thám, Q. Bình Tân',      N'Dị ứng tê Lidocaine - đã khai trong hồ sơ cũ');
GO
PRINT N'005_seed_benhnhan OK';
GO
