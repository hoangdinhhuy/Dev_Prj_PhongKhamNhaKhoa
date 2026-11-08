# Cấu hình AI, gateway và bảng sảnh

## Chạy MOCK trong LAN

1. Từ thư mục `SourceCode`, mở PowerShell trên máy chủ gateway và đặt hai token thử nghiệm dài, ngẫu nhiên. Dùng cùng `GATEWAY_CALL_TOKEN` trên máy phụ tá; chỉ đặt `GATEWAY_INVENTORY_TOKEN` trên máy quản lý.
   Các tên biến có trong [`.env.example`](../../../SourceCode/.env.example); file ví dụ chỉ để tham khảo, ứng dụng không tự đọc `.env`.

```powershell
$env:AI_MODE = 'MOCK'
$env:GATEWAY_PORT = '8088'
$env:GATEWAY_CALL_TOKEN = '<token gọi số>'
$env:GATEWAY_INVENTORY_TOKEN = '<token phân tích kho>'
mvn exec:java '-Dexec.mainClass=gateway.GatewayServer'
```

2. Cho phép TCP 8088 trong Windows Firewall chỉ trên mạng Private/LAN tin cậy. Trên máy sảnh mở `http://<IP-máy-gateway>:8088/display`, bấm **Bật âm thanh** một lần để cấp quyền autoplay.
3. Trên máy phụ tá đặt `GATEWAY_URL=http://<IP-máy-gateway>:8088` và cùng `GATEWAY_CALL_TOKEN`, rồi khởi động ứng dụng. Trên máy quản lý đặt cùng `GATEWAY_URL` và `GATEWAY_INVENTORY_TOKEN`.
4. Bấm Gọi số/Gọi lại. Sảnh hiển thị STT + phòng, xếp tiếng đọc theo thứ tự và không nhận tên/mã bệnh nhân. Nút Xác nhận vào khám vẫn là hành động riêng.

Token là bí mật dạng capability: cấp đúng token cho đúng máy, không ghi vào source, db.properties hay Git. Môi trường hiện chưa có xác thực tài khoản phiên trên gateway; quyền QUAN_LY vẫn được kiểm tra trong BUS trước khi thống kê kho. Nếu token quản lý bị lộ, gateway không thể tự xác minh lại danh tính phiên JavaFX.

## Google Cloud Text-to-Speech LIVE

- Tạo/chọn project Google Cloud, bật Cloud Text-to-Speech API và billing; áp quota/budget alerts và hạn mức request theo chính sách vận hành của project.
- Cấp cho identity của gateway quyền sử dụng Cloud Text-to-Speech tối thiểu; dùng service account riêng khi chạy server lâu dài. Local demo có thể đăng nhập ADC bằng `gcloud auth application-default login`.
- Thiết lập credential **trên máy gateway** (`GOOGLE_APPLICATION_CREDENTIALS` trỏ đến JSON service account ngoài repo, hoặc ADC đã đăng nhập). Không đặt credential ở JavaFX client.
- Khởi chạy gateway với `AI_MODE=LIVE`. Voice mặc định `vi-VN-Neural2-A`; chỉnh bằng `TTS_VOICE`, tốc độ bằng `TTS_RATE` (0.25–4), âm lượng dB bằng `TTS_VOLUME_GAIN_DB` (-96–16).
- MP3 lưu trong `SourceCode/data/tts-cache/`, cache theo voice/nội dung/tham số. Gateway phục vụ cache khi cloud không cần gọi lại; khi chưa có audio, kiosk báo trạng thái và dùng Web Speech API nếu máy có voice tiếng Việt.

## Gemini Flash LIVE

- Tạo Gemini API key trong Google AI Studio, cấu hình billing/quota/budget phù hợp. Chỉ đặt `GEMINI_API_KEY` trên máy gateway.
- `GEMINI_MODEL` mặc định là `gemini-flash-latest`; đổi bằng env khi model Flash được bật cho project/API key.
- Gateway gọi Gemini qua Google GenAI Java SDK, có timeout và tối đa 2 attempts cho 429/5xx. Không log prompt hoặc response.
- Màn kho gửi whitelist số lượng tổng hợp từ BUS. Chưa có lịch sử nhập/xuất: không có dự báo tiêu thụ/ngày đủ dùng. Gemini không được cập nhật dữ liệu.

## Bảo mật và vận hành

- Gateway bind `0.0.0.0:8088`, HTTP và token capability phù hợp demo trên LAN riêng với dữ liệu giả. Không mở cổng ra Internet. Triển khai thật cần HTTPS, firewall allowlist, quản lý/luân chuyển secret và xác thực người dùng ở server.
- Màn sảnh chỉ đọc snapshot 5 lượt gọi gần nhất và SSE; không nối SQL Server. Kiosk không có endpoint chỉnh phiếu.
- Mất gateway: gọi số trong DB và xác nhận vào khám vẫn chạy; GUI cảnh báo không đẩy được số lên sảnh. Gemini lỗi/timeout/429: số liệu tồn kho và chức năng cập nhật kho vẫn dùng được.
- Tắt gateway bằng Ctrl+C. Health check: `http://localhost:8088/health`.
