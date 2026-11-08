# BỘ TEST CASE — HUỲNH QUỐC THẮNG (22693891) — LUỒNG BÁC SĨ

> Phạm vi: UC004 Khám & chẩn đoán + chỉ định + cấp thuốc + hoàn tất · Lịch sử khám.
> Backend liên quan: TestRunner T17–T23 (34/34 PASS).

## TC-BS01 — Ghi nhận khám và chẩn đoán (UC004)

| Mã | Bước thực hiện | Kết quả mong đợi | KQ thực tế | Đạt? |
| --- | --- | --- | --- | --- |
| BS01-1 | Mở màn Khám (chưa bấm gì) | Danh sách phiếu của bác sĩ hôm nay; **tự chọn dòng đầu tiên**, panel phải hiện thông tin BN + tiền sử | | |
| BS01-2 | Chỉ định rỗng → Lưu | Alert `TB06` | | |
| BS01-3 | Chẩn đoán trống → Lưu | Alert `VL09` | | |
| BS01-4 | Chỉ định dịch vụ NGUNG_PHUC_VU → Lưu | Alert `TB15`, không lưu gì | | |
| BS01-5 | Chọn dịch vụ hợp lệ + thuốc → Lưu | Transaction lưu đủ: phiếu + hồ sơ (tự tạo lần đầu) + chỉ định + thuốc | | |
| BS01-6 | Sửa chỉ định rồi Lưu lại | DB đúng số dòng mới, không nhân đôi | | |
| BS01-7 | Số lượng thuốc `0` hoặc chữ | Alert `VL12` | | |
| BS01-8 | Hoàn tất khi chưa có chẩn đoán | Alert `VL09` | | |
| BS01-9 | Hoàn tất hợp lệ | Phiếu HOAN_TAT, rời danh sách | | |
| BS01-10 | Hoàn tất lần nữa | Alert `TB10` | | |
| BS01-11 | Đăng nhập bác sĩ khác, thử sửa phiếu của bác sĩ này | Chặn `TB08/TB10` | | |

## TC-BS02 — Lịch sử khám

| Mã | Bước thực hiện | Kết quả mong đợi | KQ thực tế | Đạt? |
| --- | --- | --- | --- | --- |
| BS02-1 | Mở "Lịch sử khám" | Bảng mọi lượt khám, badge màu theo trạng thái, sắp xếp mới nhất lên đầu | | |
| BS02-2 | Tìm "Nguyễn" → Tìm kiếm | Lọc đúng theo tên/mã/SDT | | |
| BS02-3 | Double-click một dòng | Chi tiết: chẩn đoán, kết quả điều trị, thuốc tự mua, trạng thái | | |

## Ghi nhận

- Người test: ______ Ngày: ______ Build: ______
- Minh chứng lưu `TaiLieu/docs/evidence/thang/`.
