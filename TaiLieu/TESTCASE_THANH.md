# BỘ TEST CASE — PHAN CHÍ THANH (24738801) — LUỒNG PHỤ TÁ + HẠ TẦNG

> Phạm vi: UC009 Hàng chờ/gọi số/vào khám · AI dự đoán thời gian chờ · Quản lý phòng · Hạ tầng CSDL/ConnectDB/BoDem.
> Backend liên quan: TestRunner T13–T16, T32 (34/34 PASS).

## TC-T01 — Theo dõi hàng chờ (UC009)

| Mã | Bước thực hiện | Kết quả mong đợi | KQ thực tế | Đạt? |
| --- | --- | --- | --- | --- |
| T01-1 | Mở "Hàng chờ" chọn phòng P001 | Hàng chờ hiện các phiếu CHO_KHAM hôm nay theo STT; tự refresh 10 giây | | |
| T01-2 | Chọn phòng không có phiếu chờ | Không lỗi; thông báo `TB07` khi tải lại | | |
| T01-3 | Chọn 1 bệnh nhân | Banner **"AI dự đoán thời gian chờ: ~X phút"** hiện đúng theo vị trí (người đầu ≈ 5 phút + lượt đang khám) | | |
| T01-4 | **Gọi số** | Bảng số nhảy STT; trạng thái phiếu **vẫn** CHO_KHAM trong DB | | |
| T01-5 | **Xác nhận vào khám** | Phiếu chuyển DANG_KHAM, rời hàng chờ sang bảng đang khám | | |
| T01-6 | Xác nhận vào khám lại phiếu đó | Alert `TB10` | | |
| T01-7 | *(2 máy)* Cùng xác nhận vào khám 1 phiếu | 1 thành công — máy còn lại `TB10` | | |

## TC-T02 — Quản lý phòng (PT-05)

| Mã | Bước thực hiện | Kết quả mong đợi | KQ thực tế | Đạt? |
| --- | --- | --- | --- | --- |
| T02-1 | Chọn phòng → Tắt nhận bệnh | Phòng mất khỏi ComboBox "chọn phòng" của lễ tân | | |
| T02-2 | Bật lại nhận bệnh | Phòng trở lại danh sách | | |

## TC-T03 — Hạ tầng CSDL & kết nối (hạng mục riêng của Thanh)

| Mã | Bước thực hiện | Kết quả mong đợi | KQ thực tế | Đạt? |
| --- | --- | --- | --- | --- |
| T03-1 | Chạy lại toàn bộ `SourceCode/DATA` trên máy mới | 23 file OK, DB đủ 13 bảng | | |
| T03-2 | `ConnectDB` main test | "Kết nối N15_PTUD_DB thành công" | | |
| T03-3 | `BoDem_DAO` gọi 2 lần liên tiếp | Mã/STT tăng đúng, 2 máy đồng thời không trùng | | |
| T03-4 | Tắt SQL Server → chạy app | Alert lỗi kết nối rõ ràng, không crash | | |

## Ghi nhận

- Người test: ______ Ngày: ______ Build: ______
- Minh chứng lưu `TaiLieu/docs/evidence/thanh/`.
