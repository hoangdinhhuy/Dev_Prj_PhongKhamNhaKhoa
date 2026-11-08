# BỘ TEST CASE — NGUYỄN TUẤN DUY (24726131) — QUẢN LÝ + KHUNG ỨNG DỤNG

> Phạm vi: UC001 Đăng nhập · UC010 Nhân viên/tài khoản · UC007 Dịch vụ · UC008 Báo cáo + CSV · Chế độ sáng/tối · AI dự báo vật tư.
> Backend liên quan: TestRunner T01–T04, T24–T28, T32, T34 (34/34 PASS).

## TC-D01 — Đăng nhập & phân quyền (UC001)

| Mã | Bước thực hiện | Kết quả mong đợi | KQ thực tế | Đạt? |
| --- | --- | --- | --- | --- |
| D01-1 | Bỏ trống cả 2 ô → Đăng nhập | Alert `VL13` | | |
| D01-2 | `letan` / sai mật khẩu | Alert `TB01` | | |
| D01-3 | `lockacc / 123456` | Alert `TB02: Tài khoản của bạn đã bị khóa.` | | |
| D01-4 | Đăng nhập đủ 4 vai trò | Mỗi vai trò thấy đúng menu của mình | | |
| D01-5 | DB: `SELECT matKhau FROM TaiKhoan WHERE tenDangNhap='letan'` | Hash 64 ký tự, không phải "123456" | | |
| D01-6 | Bấm "Quên mật khẩu?" | Hướng dẫn liên hệ Quản trị viên | | |

## TC-D02 — Quản lý nhân viên & tài khoản (UC010)

| Mã | Bước thực hiện | Kết quả mong đợi | KQ thực tế | Đạt? |
| --- | --- | --- | --- | --- |
| D02-1 | Thêm NV thiếu tên / SĐT sai | Alert `VL01` / `VL02` | | |
| D02-2 | Thêm NV hợp lệ | Mã `NVyyMMddNNNN`, hiện trong bảng | | |
| D02-3 | Tạo tài khoản cho NV → đăng nhập được | OK | | |
| D02-4 | Tạo lại tên đăng nhập trùng | Alert `TB14` | | |
| D02-5 | Khóa/Mở tài khoản | Trạng thái đổi; TK khóa không đăng nhập được | | |
| D02-6 | Xóa NV đang có tài khoản/phiếu | Alert `TB11` | | |

## TC-D03 — Quản lý dịch vụ (UC007)

| Mã | Bước thực hiện | Kết quả mong đợi | KQ thực tế | Đạt? |
| --- | --- | --- | --- | --- |
| D03-1 | Thêm dịch vụ trùng tên | Alert `TB12` | | |
| D03-2 | Giá 0/âm/chữ | Alert `VL06` | | |
| D03-3 | Thêm hợp lệ | Mã `DVNNNN`, trạng thái Đang phục vụ | | |
| D03-4 | Đổi trạng thái → NGUNG_PHUC_VU | Bác sĩ không chỉ định được dịch vụ này nữa | | |

## TC-D04 — Báo cáo lượt khám + CSV (UC008)

| Mã | Bước thực hiện | Kết quả mong đợi | KQ thực tế | Đạt? |
| --- | --- | --- | --- | --- |
| D04-1 | Chọn 7 ngày gần nhất → Xem | Biểu đồ cột + bảng khớp số phiếu trong DB | | |
| D04-2 | Từ ngày > Đến ngày | Alert `VL10` | | |
| D04-3 | Xuất CSV → mở bằng Excel | Đúng font tiếng Việt (UTF-8 BOM), đủ cột | | |
| D04-4 | Đăng nhập `letan` thử mở Báo cáo | Không có trong menu; gọi trực tiếp → `TB08` | | |

## TC-D05 — Chế độ sáng/tối + AI dự báo vật tư

| Mã | Bước thực hiện | Kết quả mong đợi | KQ thực tế | Đạt? |
| --- | --- | --- | --- | --- |
| D05-1 | Bấm "Chế độ tối" ở sidebar | Toàn bộ nền tối, chữ sáng, nút "Chế độ sáng" | | |
| D05-2 | Chế độ tối → mở từng chức năng | Mọi màn hiển thị đúng nền tối, không chữ chìm | | |
| D05-3 | Bấm "Chế độ sáng" | Trở lại giao diện sáng | | |
| D05-4 | "AI dự báo vật tư" → Chạy dự báo | Bảng vật tư + dự báo 2 tuần + ghi chú nhu cầu | | |

## Ghi nhận

- Người test: ______ Ngày: ______ Build: ______
- Minh chứng lưu `TaiLieu/docs/evidence/duy/`.
