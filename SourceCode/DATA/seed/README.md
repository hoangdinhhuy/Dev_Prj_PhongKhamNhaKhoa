# Seed database

Chạy các file seed theo thứ tự sau khi đã tạo schema:

1. Chạy `001_...` đến `013_seed_hanghoa.sql` theo số thứ tự.
2. Chạy `014_seed_demo_8.sql` cuối cùng để thay dữ liệu bệnh nhân/lượt khám/hóa đơn cũ bằng bộ 8 hồ sơ thử nghiệm.

`014_seed_demo_8.sql` giữ nguyên nhân viên, tài khoản, phòng khám, dịch vụ, thuốc và hàng hóa. Script xóa dữ liệu trong các bảng hồ sơ bệnh nhân, hồ sơ bệnh án, phiếu khám, chỉ định, cấp thuốc, hóa đơn và chi tiết hóa đơn; sau đó tạo:

- 2 bệnh nhân chưa thu phí khám.
- 2 bệnh nhân đã thu phí và đang khám.
- 4 lượt đã hoàn tất: 3 lượt có dịch vụ chờ thanh toán cuối lượt, 1 lượt chỉ có phí khám ban đầu nên không phát sinh tiền cuối lượt.

Script dùng ngày hiện tại để sinh mã và có transaction. Có thể chạy lại để đưa bộ dữ liệu về trạng thái ban đầu như trên; không cần chạy lại các seed khác.
