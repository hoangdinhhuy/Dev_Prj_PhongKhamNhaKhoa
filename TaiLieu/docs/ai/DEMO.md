# Kịch bản demo

## Gọi số sảnh

1. Máy server: khởi động `GatewayServer` ở chế độ MOCK theo `SETUP.md`.
2. Máy sảnh cùng Wi-Fi/LAN: mở `http://<IP-server>:8088/display` ở Edge fullscreen/kiosk, bật âm thanh một lần.
3. Máy phụ tá: chọn phiếu chờ phòng P002, bấm **Gọi số**. Bảng sảnh hiển thị STT lớn, phòng P002 và phát câu mời; không hiện tên bệnh nhân.
4. Bấm **Gọi lại** để phát lại lời mời. Bảng hiển thị nhãn “Gọi lại”.
5. Chọn riêng **Xác nhận vào khám** để đổi trạng thái. Gọi số/gọi lại không đổi trạng thái.
6. Tắt/mở lại tab sảnh: snapshot hiện số gần nhất nhưng không tự phát lại âm thanh.

## Phân tích kho

1. Đăng nhập `NV0007`, mở **Quản lý hàng hóa**; kiểm tra 6 mặt hàng mẫu và mặt hàng ở dưới mức tồn tối thiểu.
2. Bấm **Phân tích bằng AI**. MOCK diễn giải số liệu hiện tại, mức rủi ro tính tại Java, khuyến nghị kiểm tra kho; ghi rõ không có lịch sử nhập/xuất và MOCK.
3. Chạy LIVE chỉ khi đã cấu hình Gemini key trên gateway. Bấm phân tích và xác nhận màn hình hiện `LIVE`; DB phải giữ nguyên.
4. Dừng gateway hoặc cấu hình key/quota lỗi; màn kho vẫn xem/thêm/cập nhật được và hiện trạng thái gateway/AI không sẵn sàng.

## Giới hạn demo

HTTP chỉ dùng trên LAN tin cậy với dữ liệu giả. Không tuyên bố demo hai thiết bị, loa thật hoặc Cloud LIVE nếu chưa chạy các bước đó trên phần cứng và credentials tương ứng.
