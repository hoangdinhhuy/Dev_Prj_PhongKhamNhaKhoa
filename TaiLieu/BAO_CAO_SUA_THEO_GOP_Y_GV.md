# BÁO CÁO ĐÃ SỬA THEO GÓP Ý GIẢNG VIÊN (05/11/2026)

**Nhóm 15 — Hệ thống Quản lý Phòng khám Nha khoa Sài Gòn** · Cập nhật 08/11/2026
Bảng đối chiếu: từng góp ý → vị trí sửa trong mã nguồn → bằng chứng kiểm thử.
Chạy kiểm chứng: `java -cp target/classes main.main.TestRunner` → **46/46 PASS** · `main.LoadCheck` → **17/17 FXML PASS**.

## 1. Kiến trúc: Business Logic, DTO và Entity

| Góp ý | Đã sửa ở đâu | Bằng chứng |
| --- | --- | --- |
| Xử lý dựa trên đối tượng, dùng lại đối tượng đã tạo | BUS giữ đối tượng `PhieuKham pk` nạp 1 lần rồi dùng `pk.getMaBenhNhan()/getTrangThai()` xuyên suốt `luuKham`, `hoanTat`, `lapPhieuKham` (`bus/KhamBenh_BUS.java`, `bus/PhieuKham_BUS.java`) | T15–T23 PASS |
| Entity đúng bản chất, không dồn chi tiết vào 1 Entity | `HoaDon` chỉ giữ thông tin hóa đơn; chi tiết nằm ở `ChiTietHoaDon`; chỉ định nằm ở `ChiDinhDichVu` | T21 (transaction 4 bảng) PASS |
| Thuộc tính hiển thị tách khỏi dữ liệu thật | `PhieuKham.tenBenhNhan/tenBacSi/tenPhong/lanThu` đánh dấu "Cột hiển thị (JOIN, không lưu DB)" | LoadCheck PASS |

## 2. Thuộc tính dẫn xuất và logic tính tiền

| Góp ý | Đã sửa ở đâu | Bằng chứng |
| --- | --- | --- |
| Không cho set tùy ý thuộc tính dẫn xuất | **Xóa `setTongTien`** khỏi `entity/HoaDon.java`, **xóa `setThanhTien`** khỏi `entity/ChiTietHoaDon.java`; tổng tiền chỉ được tính qua `themChiTiet()` → `tinhTongTien()` | T39: reflection xác nhận **không tồn tại** 2 setter |
| Giá trị xác lập qua công thức | `getTienThua()` tính sẵn `tienKhachDua − tongTien` trong entity, không có field/setter | T40: 350k + 200k×2 → tong 750.000, thừa 250.000 PASS |
| Kiểm tra vị trí sử dụng tránh sửa số tiền đã tính | `GD_ThanhToan_Controller` bỏ biến tổng tự cộng — dựng `HoaDon hoaDonTam`, nạp chi tiết, hiển thị `getTongTien()/getTienThua()`; DB có lớp 2: `CHECK (thanhTien = soLuong * donGia)`, `CHECK (soLuong > 0)` (schema 008) | T30: thanh toán tổng 550.000 = Σ chỉ định PASS |

## 3. Tài khoản cá nhân và vai trò

| Góp ý | Đã sửa ở đâu | Bằng chứng |
| --- | --- | --- |
| Tài khoản gắn người dùng cụ thể, không dùng tên vai trò | Migration `DATA/schema/016_tai_khoan_ca_nhan.sql` + seed `002`: `letan→huyhd`, `bacsi1→thanghq`, `bacsi2→chaulm`, `phuta→thanhtpc`, `quanly→duynt`, `lockacc→haph` | GUI: đăng nhập `huyhd` → "Chào mừng Hoàng Đình Huy", `thanghq` → "Chào mừng Huỳnh Quốc Thắng" |
| Tách tài khoản và vai trò; quyền xác định sau đăng nhập | Vai trò nằm ở `NhanVien.chucVu`; `SessionContext` mang vai trò; mọi BUS kiểm qua `QuyenUtil.kiemTraQuyen` | T04, T42: vai trái → TB08 PASS |
| Đổi toàn bộ dữ liệu minh họa | TestRunner, LoadCheck, 8 file tài liệu đổi hết username cũ (còn 0 chỗ) | T01 nhãn "huyhd -> LE_TAN" PASS |

## 4. Tra cứu và hiển thị danh sách

| Góp ý | Đã sửa ở đâu | Bằng chứng |
| --- | --- | --- |
| Không mặc định tải toàn bộ; mặc định có tiêu chí thật | `GD_QLBenhNhan`: 50 hồ sơ mới nhất; `GD_TraCuuBenhNhan`: đã khám trong 30 ngày (TOP 20); `GD_LichSuKham`: 30 ngày + FETCH 200; `GD_ThanhToan`: FETCH 100 | LoadCheck + GUI |
| Tìm kiếm theo từ khóa, hỗ trợ chọn chính xác | `util/GoiTuKhoa.chuanHoa` (bỏ dấu) dùng ở Tra cứu, Lịch sử khám, tìm dịch vụ màn khám | T37 PASS; GUI: gõ "nguyen van" theo "Họ tên" → lọc đúng 5 dòng NGUYEN VAN TEST |
| Gợi ý đúng ngữ cảnh | Màn khám: ô tìm dịch vụ lọc tức thì (FilteredList) | GUI: gõ "hàn" → chỉ còn "DV0003 - Hàn răng composite" |
| Dữ liệu 5–10 năm + sao lưu | `TaiLieu/HUONG_DAN_CAI_DAT.md` mục 8: BACKUP DATABASE hàng tuần + khôi phục | — |

## 5. Dữ liệu kiểm thử cho chức năng thanh toán

| Góp ý | Đã sửa ở đâu | Bằng chứng |
| --- | --- | --- |
| Sẵn bệnh nhân hoàn tất khám + chi phí phát sinh | DB có sẵn 2 phiếu `HOAN_TAT` có dịch vụ chưa thanh toán; script tái tạo `DATA/maintenance/reset_patient_demo.sql` | T29: phiếu nằm trong danh sách chờ thanh toán PASS |
| Dùng ngay phiếu đã có dịch vụ để demo | `GD_ThanhToan` nạp `layPhieuHoanTatChuaThanhToan` → chọn là có đủ dịch vụ + tiền | GUI: màn Thanh toán hiển thị sẵn danh sách chờ |

## 6. Chỉ định dịch vụ trong màn khám bệnh

| Góp ý | Đã sửa ở đâu | Bằng chứng |
| --- | --- | --- |
| Ít nhất 1 chỉ định điều trị | `KhamBenh_BUS.luuKham` chặn `TB06`, `hoanTat` chặn khi chưa lưu | T19 PASS |
| Danh sách nhiều lựa chọn phải tìm kiếm chính xác | `GD_KhamBenh.fxml` + Controller: TextField tìm + FilteredList, so khớp không dấu | GUI xác nhận (mục 4) |

## 7. Chi tiết điều trị, phác đồ và lịch tái khám

| Góp ý | Đã sửa ở đâu | Bằng chứng |
| --- | --- | --- |
| Ghi rõ răng số bao nhiêu | `ChiDinhDichVu.soRang` (migration 014) + khung răng FDI 32 nút bấm chọn; bắt buộc chọn răng cho dịch vụ hàn/trám/trát/nhổ/tủy/mão (VL19) | T36: lưu "36,37" PASS; GUI: khung 18–11, 21–28, 48–41, 31–38 hiện khi chọn "Hàn răng composite"; bấm Thêm không chọn răng → alert "Lỗi" chặn, bảng không thêm |
| Theo dõi số lần điều trị, nội dung từng lần | `PhieuKham.phacDieuTri` + `ngayHenTiep` (migration 015); "Lần điều trị thứ N" tại `GD_KhamBenh`; cột "Lần thứ" `GD_LichSuKham` bằng `ROW_NUMBER() OVER (PARTITION BY maBenhNhan)`; popup chi tiết in phác đồ + ngày hẹn + răng theo từng lượt; màn khám hiển thị "Lần trước (ngày, mã phiếu): phác đồ" | T43: max lanThu của BN == số phiếu thật PASS; GUI: "Lần điều trị thứ 3… Lần trước (08/11/2026, PK2611080003): 1. Tay trang; 2. Han composite rang 36,37" |
| Dựa trên phác đồ lên lịch hẹn theo buổi | DatePicker "Ngày hẹn tái khám" lưu `ngayHenTiep` | T35: lưu đúng phác đồ + ngày hẹn PASS |
| Quản lý toàn bộ quá trình nhiều lần | Lịch sử khám theo bệnh nhân + popup chi tiết từng lượt (`PhieuKham_BUS.layChiDinhTheoPhieu`) | LoadCheck GD_LichSuKham PASS |

## Phần 2 góp ý (bổ sung)

| Góp ý | Xử lý |
| --- | --- |
| P2-2 Entity đúng bản chất: Hóa đơn ↔ Nhân viên/Khách hàng; Chi tiết hóa đơn ↔ Sản phẩm | Thêm quan hệ đối tượng: `HoaDon.nhanVien` (NhanVien), `HoaDon.benhNhan` (BenhNhan), `ChiTietHoaDon.dichVu` (DichVu) — DAO nạp thật khi đọc |
| P2-3 Dùng quan hệ đối tượng thay vì truy vấn từng thuộc tính | T44: nạp hóa đơn → `getNhanVien()/getBenhNhan()/chiTiet.getDichVu()` đều đầy đủ PASS |
| P2-4 Role + quyền theo nhóm | `QuyenUtil` kiểm theo `ChucVu`; test quyền âm T04/T42/T42b PASS |
| P2-5 Thiết kế cho dữ liệu tích lũy + sao lưu | Mọi danh sách mặc định có tiêu chí + FETCH; backup tuần (mục 4) |

## Màn hình mới tách theo phân công (đề xuất nhóm)

- `GD_QuanLyPhong` — bật/tắt nhận bệnh theo phòng (PHU_TA, QUAN_LY) — LoadCheck PASS
- `GD_ThongKeHangCho` — đếm CHỜ/ĐANG/HOÀN TẤT theo phòng trong khoảng ngày (T41/T42 PASS)

## Cách chạy kiểm chứng

```bash
cd SourceCode
mvn -q clean compile
# Backend 46 test (headless, dữ liệu test giữ lại làm demo)
java -cp "target/classes;<javafx-libs>/*" main.main.TestRunner
# Kiểm tra 17 FXML nạp được
java -cp "target/classes;<javafx-libs>/*" --module-path "<javafx-libs>" \
     --add-modules javafx.controls,javafx.fxml main.main.LoadCheck
```

Tài khoản test (mật khẩu chung `123456`): `huyhd` (Lễ tân), `thanghq`/`chaulm` (Bác sĩ), `thanhtpc` (Phụ tá), `duynt` (Quản lý), `haph` (bị khóa — TB02).
