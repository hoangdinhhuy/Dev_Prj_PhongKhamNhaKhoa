# DATA — Cơ sở dữ liệu `N15_PTUD_DB`

## Thứ tự chạy (bắt buộc, trong SSMS)

Chạy schema trước, seed sau, đúng số thứ tự. File đã chạy chung **cấm sửa nội dung** — cần đổi thì tạo file số mới.

| Bước | Schema (`DATA/schema/`) | Seed (`DATA/seed/`) | Chủ file |
| --- | --- | --- | --- |
| 1 | `000_hatang.sql` (tạo DB + bảng `BoDem`) | — | Thanh |
| 2 | `001_phongkham.sql` | `001_seed_phongkham.sql` | Thanh |
| 3 | `002_nhanvien_taikhoan.sql` | `002_seed_nhanvien_taikhoan.sql` | Duy |
| 4 | `003_dichvu.sql` | `003_seed_dichvu.sql` | Duy |
| 5 | `004_thuoc.sql` | `004_seed_thuoc.sql` | Thắng |
| 6 | `005_benhnhan.sql` | `005_seed_benhnhan.sql` | Huy |
| 7 | `006_hosobenhan.sql` | `006_seed_hosobenhan.sql` | Thắng |
| 8 | `007_phieukham.sql` | `007_seed_phieukham.sql` | Huy |
| 9 | `008_hoadon_chitiethoadon.sql` | `008_seed_hoadon.sql` | Huy |
| 10 | `009_chidinhdichvu.sql` | `009_seed_chidinhdichvu.sql` | Thắng |
| 11 | `010_chitietcapthuoc.sql` | `010_seed_chitietcapthuoc.sql` | Thắng |
| 12 | — | `011_seed_morong.sql` (mở rộng dữ liệu demo: 20 BN, phiếu 7 ngày, 14 dịch vụ) | cả nhóm |
| 13 | `012_hoadon_phieukham.sql` (FK HoaDon↔PhieuKham cho thanh toán cuối lượt) | — | Thanh |

> Seed phiếu khám (007) dùng ngày hiện tại của máy (`GETDATE()`) để màn hình Hàng chờ và Báo cáo có dữ liệu demo ngay.

## Chuẩn bị SQL Server trước khi chạy script

1. Mở **SQL Server Configuration Manager** → SQL Server Network Configuration → Protocols → **TCP/IP = Enabled** → restart dịch vụ SQL Server.
2. SSMS → Server Properties → Security → bật **SQL Server and Windows Authentication mode**; enable user `sa`, đặt mật khẩu.
3. Cổng 1433: kiểm tra bằng `sqlcmd -S localhost -U sa -P <mật khẩu> -Q "SELECT 1"`.
4. Copy `src/main/resources/db.properties.example` thành `db.properties` (cùng thư mục) và sửa `db.password`.

## Tài khoản seed (mật khẩu chung: `123456`, lưu dạng hash SHA-256)

| Tên đăng nhập | Vai trò | Ghi chú |
| --- | --- | --- |
| `letan` | LE_TAN | chạy màn hình lễ tân (hồ sơ/thu phí/lập phiếu/tra cứu) |
| `bacsi1`, `bacsi2` | BAC_SI | màn hình khám bệnh |
| `phuta` | PHU_TA | màn hình hàng chờ |
| `quanly` | QUAN_LY | nhân viên/dịch vụ/báo cáo |
