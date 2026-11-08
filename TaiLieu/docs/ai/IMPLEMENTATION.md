# Thiết kế và hiện thực

## Kiến trúc

- `GD_HangCho_Controller` gọi `HangCho_BUS.goiSo` (kiểm tra PHU_TA và phiếu CHO_KHAM), sau đó gửi một payload nhỏ qua `GatewayClient`; gọi lại làm xác thực nghiệp vụ tương tự. `tiepNhanVaoKham` giữ nguyên thao tác riêng.
- `GatewayServer` dùng JDK `HttpServer`: `/display` static kiosk, `/api/snapshot`, SSE `/api/events`, POST `/api/call`, `/audio/{hash}.mp3`, POST `/api/inventory-analysis`, `/health`.
- Gateway giữ 5 sự kiện gọi gần nhất trong RAM. SSE snapshot chỉ render; event mới được xếp thứ tự và phát một lần theo `eventId`. Mỗi kiosk có hàng đợi audio riêng. MP3 cache trên đĩa theo voice + text + rate + volume.
- TTS LIVE dùng Cloud Text-to-Speech REST endpoint và ADC/service account ở gateway. MOCK không gọi Google; kiosk dùng Web Speech API khi có voice `vi-VN`, nếu không thì báo audio unavailable.
- Inventory BUS checks QUAN_LY, then aggregates item count, total stock, total minimum stock, below-threshold count, and risk. It sends no item names/codes, prices, patient history, or staff data. The server accepts only six whitelisted fields, recalculates risk, then calls Google GenAI Java SDK or returns MOCK. JSON output is bounded and validated; risk and numeric metrics come from Java.
- Không có lịch sử nhập/xuất trong schema hiện tại; xu hướng, tiêu thụ, lead time và ngày đủ dùng không thể tính. Không thêm bảng tồn kho mới.

## Thay đổi chính

- `SourceCode/src/gateway/GatewayServer.java`, `GatewayClient.java`, `display.html`
- `GD_HangCho_Controller.java`, `GD_HangCho.fxml`
- `HangHoa_BUS.java`, `GD_QLHangHoa_Controller.java`, `GD_QLHangHoa.fxml`, `TestRunner.java`
- `SourceCode/pom.xml`, `.gitignore`
- `TaiLieu/docs/ai/{PROGRESS,SETUP,TEST_CASES,DEMO,IMPLEMENTATION}.md`

## Dependency và cấu hình

- Giữ Java 21/JavaFX 21/JDBC. Thêm Google GenAI Java SDK, Google Auth Library (ADC), Gson; thêm Maven exec plugin để chạy gateway độc lập.
- Secrets chỉ đọc từ environment gateway/client. Không có secret thật trong repo. TTS credential và Gemini key không nằm trong JavaFX app.

## Giới hạn / review cần thiết

- Gọi số snapshot chỉ tồn tại trong RAM gateway; gateway restart thì không còn bảng sảnh cũ. Hệ thống chưa có bảng `BangGoiSo` và không cần sửa trạng thái khám để chạy event.
- Token tách call/inventory là capability token. BUS kiểm tra QUAN_LY, nhưng gateway chưa thể xác thực danh tính tài khoản JavaFX bằng phiên server-side; chỉ cấp inventory token cho workstation quản lý. Review kiến trúc auth trước triển khai thật.
- HTTP, firewall LAN, trình duyệt/loa/autoplay và cài voice offline phụ thuộc máy đích. Môi trường thật cần HTTPS, allowlist, identity server-side, quota/budget alerts, xác nhận privacy/retention.
- Gemini là giải thích/gợi ý; nội dung tự do vẫn cần nhân viên kiểm tra. Server không cung cấp tool hoặc endpoint sửa DB cho model.
