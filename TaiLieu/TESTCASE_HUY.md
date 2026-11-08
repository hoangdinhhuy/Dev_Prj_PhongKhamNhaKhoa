# BỘ TEST CASE — HOÀNG ĐÌNH HUY (24738801) — LUỒNG LỄ TÂN

> Phạm vi: UC002 Hồ sơ bệnh nhân · UC005 Thu phí · UC003 Lập phiếu + in phiếu · UC006 Tra cứu · Thanh toán dịch vụ.
> Backend liên quan: TestRunner T05–T12, T29–T31 (phải 34/34 PASS).
> Kết quả thực tế ghi trực tiếp vào cột **KQ thực tế** + đánh dấu Đạt/Chưa — lưu lại làm minh chứng CLO4.

## TC-H01 — Quản lý hồ sơ bệnh nhân (UC002)

| Mã | Bước thực hiện | Kết quả mong đợi | KQ thực tế | Đạt? |
| --- | --- | --- | --- | --- |
| H01-1 | Mở "Hồ sơ bệnh nhân" (không bấm gì) | Bảng hiện **full danh sách 10 bệnh nhân** với đủ CCCD/địa chỉ/tiền sử | | |
| H01-2 | Bỏ trống họ tên → Thêm | Alert `VL01: Họ tên không được để trống.` | | |
| H01-3 | SĐT nhập `090123456` (9 số) | Alert `VL02` | | |
| H01-4 | CCCD nhập 11 số | Alert `VL03` | | |
| H01-5 | CCCD trùng `079199001001` | Alert `TB13: CCCD đã tồn tại...` | | |
| H01-6 | SĐT trùng BN khác → xác nhận Có | Vẫn tạo được hồ sơ mới (SĐT không là mã định danh duy nhất) | | |
| H01-7 | Nhập hợp lệ → Thêm | Mã mới `BNyyMMddNNNN`, xuất hiện đầu bảng, đọc lại đủ thông tin | | |
| H01-8 | Click dòng → sửa SĐT → Sửa | Cập nhật + bảng refresh | | |
| H01-9 | Double-click dòng | Modal chi tiết hồ sơ + lịch sử phiếu khám của BN | | |

## TC-H02 — Thu tiền phí khám (UC005)

| Mã | Bước thực hiện | Kết quả mong đợi | KQ thực tế | Đạt? |
| --- | --- | --- | --- | --- |
| H02-1 | Bấm "Thu phí" khi chưa tìm BN | Alert `VL05` | | |
| H02-2 | Tìm `0901111001` | Hiện tên BN + mức phí 50.000 ₫ | | |
| H02-3 | Tiền khách đưa `30000` → Thu phí | Báo "Tiền khách đưa chưa đủ mức phí khám." | | |
| H02-4 | Tiền `100000` → Thu phí | Hóa đơn `HDyyMMdd...` + thông báo thành công | | |
| H02-5 | Thu phí lần nữa cùng BN | Alert `TB04: Bệnh nhân đã hoàn tất phí khám.` | | |
| H02-6 | Bấm "Dùng hóa đơn đã thu" | Dùng lại hóa đơn chưa gắn phiếu, không phát sinh hóa đơn mới | | |

## TC-H03 — Lập phiếu khám + in phiếu (UC003)

| Mã | Bước thực hiện | Kết quả mong đợi | KQ thực tế | Đạt? |
| --- | --- | --- | --- | --- |
| H03-1 | Lập phiếu chưa chọn BN | Alert `VL05` | | |
| H03-2 | Lý do khám để trống | Alert `VL08` | | |
| H03-3 | Chọn phòng P003 (tắt nhận bệnh) | Alert `TB03` | | |
| H03-4 | BN đã thu phí + bác sĩ + P001 + lý do → Lập phiếu | STT tự cấp, trạng thái "chờ khám", hóa đơn phí được gắn | | |
| H03-5 | Lập phiếu thứ 2 cùng phòng | STT tăng +1, mã khác | | |
| H03-6 | Nhấn Lập phiếu 2 lần nhanh | Lần 2 chặn `TB04` — không trùng mã/STT | | |
| H03-7 | "Xem phiếu" → "In phiếu" | Hộp chọn máy in Windows; in được (hoặc báo "Không tìm thấy máy in") | | |

## TC-H04 — Tra cứu hồ sơ (UC006)

| Mã | Bước thực hiện | Kết quả mong đợi | KQ thực tế | Đạt? |
| --- | --- | --- | --- | --- |
| H04-1 | Mở màn Tra cứu (chưa bấm gì) | Hiện **full danh sách** bệnh nhân ngay | | |
| H04-2 | Chọn "Họ tên" + gõ "Nguyễn" → Tìm | Lọc đúng các BN chứa từ khóa | | |
| H04-3 | Tìm mã không tồn tại | Alert `TB05`, bảng rỗng | | |
| H04-4 | Double-click dòng | Modal chi tiết + lịch sử phiếu khám | | |

## TC-H05 — Thanh toán dịch vụ cuối lượt

| Mã | Bước thực hiện | Kết quả mong đợi | KQ thực tế | Đạt? |
| --- | --- | --- | --- | --- |
| H05-1 | Mở "Thanh toán dịch vụ" → chọn lượt HOAN_TAT | Panel phải hiện đúng dịch vụ đã chỉ định + tổng tiền | | |
| H05-2 | Tiền khách đưa thiếu tổng | Báo "Tiền khách đưa chưa đủ để thanh toán." | | |
| H05-3 | Xác nhận thanh toán | Hóa đơn tạo kèm chi tiết từng dịch vụ; lượt rời danh sách | | |
| H05-4 | Thanh toán lại lượt đó | Alert `TB17` | | |

## Ghi nhận

- Người test: ______ Ngày: ______ Build: ______
- Minh chứng (ảnh chụp) lưu `TaiLieu/docs/evidence/huy/`.
