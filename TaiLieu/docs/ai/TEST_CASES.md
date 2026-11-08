# Kiểm thử TTS + phân tích kho

Trạng thái tại checkpoint cuối. LIVE Google và hai máy vật lý chưa xác minh nếu chưa có credential/thiết bị.

| ID | Kiểm tra | Trạng thái | Bằng chứng |
|---|---|---|---|
| TTS-01 | Gateway MOCK `/health`, `/display` | PASS | trả HTTP 200, health `ok=true` |
| TTS-02 | Gọi số qua POST và nhận SSE `call` | PASS | local gateway nhận STT `025`, phòng `P002`; SSE trả text, không có identity bệnh nhân |
| TTS-03 | Cách đọc STT `001`, `025`, `100` | PASS | `không không một`, `không hai mươi lăm`, `một trăm` |
| TTS-04 | Token gọi số sai | PASS | endpoint từ chối 403 |
| TTS-05 | Gửi lặp cùng eventId | PASS | snapshot không thêm lượt gọi thứ hai |
| TTS-06 | Máy sảnh reconnect nhận snapshot, không phát lại | PASS (code + endpoint) | `snapshot` chỉ render, không vào hàng phát audio |
| TTS-07 | 2 phòng gọi đồng thời, xếp loa | NOT_RUN | cần chạy hai app client/máy trong LAN; server xếp event, kiosk xếp hàng phát |
| TTS-08 | Google TTS Neural2 + ADC, cache MP3, mất Google | NOT_RUN | cần Cloud project, billing và ADC/service account |
| TTS-09 | Mất gateway không ảnh hưởng chuyển CHO_KHAM → DANG_KHAM | NOT_RUN | code giữ hai thao tác độc lập; cần regression GUI/DB |
| AI-01 | Gateway MOCK trả đúng JSON, số liệu whitelist | PASS | local POST trả `soMatHang=6`, `tongMucTonToiThieu=29`, `soMatHangCanNhap=2`, risk tính `MEDIUM` |
| AI-02 | Inventory endpoint token sai | PASS | endpoint từ chối 403 |
| AI-03 | Chỉ quản lý được lấy thống kê kho | PASS | `TestRunner`: lễ tân bị TB08; whitelist thống kê quản lý đạt trước khi runner lỗi ở T05 |
| AI-04 | Danh mục rỗng / mọi hàng đủ / dưới ngưỡng | PARTIAL | code xử lý UNKNOWN/LOW/MEDIUM/HIGH; chỉ MOCK 6 hàng đã chạy |
| AI-05 | Gemini LIVE JSON, timeout/429, response sai schema | NOT_RUN | cần `GEMINI_API_KEY`/quota. SDK có timeout, retry giới hạn và JSON validation |
| AI-06 | Không sửa DB, không gửi trường ngoài whitelist | PASS (code review) | gateway dựng payload mới từ 6 trường numeric/boolean, bỏ mọi field khác |
| UI-01 | JavaFX Gọi số/Gọi lại và trang inventory AI | NOT_RUN | compile đạt; cần chạy GUI để click thật |
| REG-01 | `mvn clean compile` | PASS | kết quả ở checkpoint cuối |
| REG-02 | `main.TestRunner` regression với DB thật | FAIL | T01–T04 và check kho PASS; T05 va chạm PK `BN26110001` đã có trong DB. Các test phụ thuộc bị lỗi dây chuyền; xem `PROGRESS.md` |

## Lệnh kiểm tra gateway MOCK

Tham khảo lệnh PowerShell chạy gateway và các endpoint ở `SETUP.md`. API `/api/call` nhận form fields `eventId`, `stt`, `room`, `type`, `token`; `/api/inventory-analysis` nhận `metrics` và `token`.
