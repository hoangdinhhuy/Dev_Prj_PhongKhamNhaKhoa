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
| 14 | `013_hanghoa.sql` (danh mục hàng hóa và tồn kho) | `013_seed_hanghoa.sql` (6 mặt hàng, chạy lại an toàn) | Duy |

Khi chạy seed bằng `sqlcmd` trên Windows, thêm `-f 65001` để giữ đúng tên hàng tiếng Việt UTF-8.

## Làm mới dữ liệu bệnh nhân demo

Để giữ nguyên tài khoản, nhân viên, dịch vụ, thuốc, phòng khám và hàng hóa nhưng thay toàn bộ dữ liệu bệnh nhân/phiếu khám/hóa đơn, dùng `maintenance/reset_patient_demo.sql`. Script chỉ chạy khi được bật xác nhận bên trong file và có full backup database trong 2 giờ gần nhất. Nó tạo 5 hồ sơ bệnh nhân giả lập mới, không phải dữ liệu người thật.

Database đã được sao lưu trước lần làm mới này tại `backup/N15_PTUD_DB_before_patient_refresh_20261103_191356.bak`. File `.bak` bị Git bỏ qua để tránh đẩy dữ liệu database lên repository. Hướng dẫn khôi phục ở `backup/README.md`.

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
