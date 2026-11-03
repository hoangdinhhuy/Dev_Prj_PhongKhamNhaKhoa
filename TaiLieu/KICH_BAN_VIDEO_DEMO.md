# KỊCH BẢN QUAY VIDEO DEMO — 1 LUỒNG BỆNH NHÂN LIỀN MẠCH (~6 phút)

> **Mục tiêu:** quay 1 lần liền mạch theo hành trình của MỘT bệnh nhân: tạo hồ sơ → thu phí → lập phiếu → vào khám → khám & hoàn tất → thanh toán → báo cáo.
> **Chuẩn bị trước khi quay:** app mở sẵn ở màn Đăng nhập (maximized); DB đã có 10 bệnh nhân mẫu; đóng hết cửa sổ thừa; quay màn hình (Win+G hoặc OBS).

---

## CẢNH 0 — Mở đầu (10 giây)

Nói trước camera (hoặc thuyết minh): *"Đây là Hệ thống quản lý Phòng khám Nha khoa Sài Gòn — đồ án môn Phát triển Ứng dụng, nhóm 15. Tôi sẽ đi qua trọn vẹn một lượt khám: từ tiếp nhận, thu phí, lập phiếu, vào khám, đến thanh toán và báo cáo."*

## CẢNH 1 — Lễ tân: tạo hồ sơ bệnh nhân (45 giây) — `letan / 123456`

| Bước | Thao tác | Nói khi demo |
| --- | --- | --- |
| 1 | Đăng nhập `letan / 123456` | "Đăng nhập phân quyền — mỗi vai trò thấy đúng chức năng của mình" |
| 2 | Menu **Hồ sơ bệnh nhân** | "Trước tiên là tạo hồ sơ bệnh nhân điện tử" |
| 3 | Nhập: `Trần Nhật Minh` · SĐT `0905552001` · CCCD `079200599001` · ngày sinh `12/05/2001` · địa chỉ · tiền sử `Không dị ứng` → **Thêm** → OK | "Hệ thống tự sinh mã bệnh nhân theo ngày, kiểm tra trùng CCCD trước khi lưu" |
| 4 | Click dòng vừa tạo | "Thông tin đầy đủ lưu vào SQL Server" |

## CẢNH 2 — Lễ tân: thu lệ phí (40 giây) — đang ở vai trò letan

| Bước | Thao tác | Nói khi demo |
| --- | --- | --- |
| 1 | Menu **Thu lệ phí khám** → tìm SĐT `0905552001` | "Tra bệnh nhân theo SĐT" |
| 2 | Tiền khách đưa `100000` → **Thu phí** → OK | "Thu phí khám ban đầu 50.000 — hệ thống tự tạo hóa đơn, tự tính tiền thừa" |
| 3 | Bấm **Thu phí** lần nữa | "Chống thu trùng — mỗi bệnh nhân chỉ thu phí một lần trước khi khám" |

## CẢNH 3 — Lễ tân: lập phiếu khám + in (45 giây) — đang ở vai trò letan

| Bước | Thao tác | Nói khi demo |
| --- | --- | --- |
| 1 | Menu **Lập phiếu khám** → tìm SĐT `0905552001` | "Bệnh nhân đã thu phí — hợp lệ để lập phiếu" |
| 2 | Chọn bác sĩ `bacsi1`, phòng `P001`, lý do `Đau răng hàm bên phải` → **Lập phiếu** → OK | "Hệ thống tự cấp mã phiếu và số thứ tự theo phòng/ngày" |
| 3 | **Xem phiếu** | "Phiếu khám có đầy đủ thông tin để in đưa bệnh nhân" |

## CẢNH 4 — Phụ tá: hàng chờ, gọi số, vào khám (45 giây) — Đăng xuất → `phuta / 123456`

| Bước | Thao tác | Nói khi demo |
| --- | --- | --- |
| 1 | Đăng xuất → đăng nhập `phuta / 123456` → **Hàng chờ theo phòng** → P001 | "Chuyển vai trò phụ tá — theo dõi hàng chờ theo phòng" |
| 2 | Chọn bệnh nhân vừa lập phiếu → **Gọi số** | "Gọi số — bệnh nhân chuyển sang trạng thái đang được gọi" |
| 3 | **Xác nhận vào khám** → OK | "Xác nhận vào khám — phiếu chuyển DANG_KHAM" |
| 4 | Thử xác nhận lại lần nữa → `TB10` | "Chặn thao tác trùng — nếu hai máy bấm cùng lúc chỉ một máy thành công" |

## CẢNH 5 — Bác sĩ: khám, chỉ định, hoàn tất (60 giây) — Đăng xuất → `bacsi1 / 123456`

| Bước | Thao tác | Nói khi demo |
| --- | --- | --- |
| 1 | Đăng xuất → `bacsi1 / 123456` → **Khám và chẩn đoán** → chọn phiếu | "Chuyển vai trò bác sĩ — danh sách phiếu được phân công" |
| 2 | Nhập chẩn đoán `Sâu răng men, hàn răng số 36` · kết quả `Đã hàn xong` | "Bác sĩ ghi chẩn đoán và kết quả điều trị" |
| 3 | Chỉ định: `Cạo vôi răng` + `Hàn răng composite` · thuốc `Amoxicillin` SL 12 → **Lưu** → OK | "Chỉ định dịch vụ và cấp thuốc — lưu một lần vào 4 bảng cùng lúc" |
| 4 | **Hoàn tất lượt khám** → OK | "Hoàn tất lượt khám — chỉ bác sĩ mới được chuyển trạng thái này" |
| 5 | Menu **Lịch sử khám** → double-click lượt vừa xong | "Toàn bộ lịch sử lưu vết — tra cứu lại bất kỳ lúc nào" |

## CẢNH 6 — Lễ tân: thanh toán dịch vụ (40 giây) — Đăng xuất → `letan / 123456`

| Bước | Thao tác | Nói khi demo |
| --- | --- | --- |
| 1 | Đăng xuất → `letan` → **Thanh toán dịch vụ** → chọn lượt vừa hoàn tất | "Sau khi khám xong, quay lại thu tiền dịch vụ" |
| 2 | Xem tổng `550.000 ₫` · Tiền khách đưa `600000` → **Xác nhận thanh toán** → OK | "Tổng tiền tự tính theo đơn giá đã chỉ định, tạo hóa đơn khám" |
| 3 | Thử thanh toán lại → `TB17` | "Chặn thanh toán trùng" |

## CẢNH 7 — Quản lý: báo cáo, CSV, kết (45 giây) — Đăng xuất → `quanly / 123456`

| Bước | Thao tác | Nói khi demo |
| --- | --- | --- |
| 1 | Đăng xuất → `quanly / 123456` → **Báo cáo lượt khám** (7 ngày gần nhất) → Xem | "Quản lý xem báo cáo lượt khám theo ngày — biểu đồ tự cập nhật từ CSDL" |
| 2 | **Xuất CSV** → lưu → mở nhanh file | "Xuất CSV mở được bằng Excel" |
| 3 | **Quản lý dịch vụ** / **Quản lý nhân viên** (lướt nhanh) | "Danh mục dịch vụ và nhân viên quản lý tập trung" |
| 4 | Kết: *"Toàn bộ dữ liệu lưu tập trung trên SQL Server, kiến trúc 3 lớp GUI – BUS – DAO. Cảm ơn cô và các bạn đã xem!"* | |

## CẢNH 8 (TÙY CHỌN — nếu còn thời gian) — Nổi bật điểm cộng

| Bước | Thao tác | Nói |
| --- | --- | --- |
| 8.1 | Sidebar → **Chế độ tối** | "App có thêm chế độ tối" |
| 8.2 | Maximize cửa sổ | "Giao diện co giãn tốt ở chế độ fullscreen" |

---

## LƯU Ý KỸ THUẬT KHI QUAY

1. **Trật tự bắt buộc:** hồ sơ → thu phí → lập phiếu → vào khám → khám/hoàn tất → thanh toán → báo cáo (sai thứ tự sẽ bị chặn bởi nghiệp vụ — đó là điểm cộng, nhưng lúc quay phải đi đúng thứ tự).
2. Mỗi lần **Đăng xuất** nói to "chuyển vai trò" để cô hiểu đang đổi tài khoản.
3. Nếu gõ chậm, cắt cảnh (cut) giữa các bước — kịch bản viết theo đoạn ngắn để dễ cắt.
4. Không bấm **"Quét CCCD bằng điện thoại"** (nút đang ẩn — tính năng phát triển tiếp).
5. Quay xong lưu file: `Demo_Nhom15_[ngày].mp4`.
