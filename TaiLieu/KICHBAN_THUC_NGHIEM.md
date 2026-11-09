# KỊCH BẢN THỰC NGHIỆM — HỆ THỐNG QUẢN LÝ PHÒNG KHÁM NHA KHOA SÀI GÒN (Nhóm 15)

> **Cách dùng:** chạy đúng thứ tự các kịch bản S0 → S10 trên **một máy có CSDL local** (trừ S10 cần 2 máy). Mỗi bước ghi "Kết quả thực tế" + đánh dấu Đạt/Chưa để làm minh chứng kiểm thử (CLO4). Thông báo chuẩn TBxx/VLxx tra ở bảng cuối file.

---

## 1. Chuẩn bị môi trường (làm 1 lần)

| # | Việc | Kết quả mong đợi |
| --- | --- | --- |
| P1 | Cài JDK 21 + Maven (hoặc VS Code + Extension Pack for Java); cài SQL Server Express + SSMS | `java -version` báo 21; `mvn -v` chạy được |
| P2 | Bật TCP/IP 1433 trong SQL Server Configuration Manager, restart dịch vụ; bật SQL auth + user `sa` | `sqlcmd -S localhost -U sa -P <mật khẩu> -Q "SELECT 1"` trả về 1 |
| P3 | Trong SSMS chạy `SourceCode/DATA/schema/000_hatang.sql` → `013_hanghoa.sql` đúng thứ tự | Mỗi file in ra "… OK"; có thêm bảng `HangHoa` trong `N15_PTUD_DB` |
| P4 | Chạy `SourceCode/DATA/seed/001_…` → `013_seed_hanghoa.sql` đúng thứ tự, sau đó chạy `014_seed_demo_8.sql` | Có đúng 8 bệnh nhân mẫu: 2 chưa thu phí, 2 đang khám, 4 hoàn tất (3 chờ thanh toán dịch vụ, 1 không phát sinh tiền cuối lượt); hàng hóa vẫn có 6 mặt hàng |
| P5 | Copy `src/db.properties.example` thành `db.properties`, sửa `db.password` | File tồn tại, KHÔNG hiện trong `git status` |
| P6 | Tại thư mục gốc chạy `mvn clean compile` rồi `mvn javafx:run` | Mở màn hình **Đăng nhập** (nếu lỗi kết nối → xem lại P2–P5) |

## 2. Tài khoản seed (mật khẩu chung `123456`)

| Tên đăng nhập | Vai trò | Dùng cho kịch bản |
| --- | --- | --- |
| `NV0001` | Lễ tân | S2, S3, S4 |
| `NV0002` / `NV0003` | Bác sĩ | S6 |
| `NV0005` | Phụ tá | S5 |
| `NV0007` | Quản lý | S7, S8, S9 |
| `NV0004` | Lễ tân (BI_KHOA) | S1 |

---

## S0 — Khởi động ứng dụng

| Bước | Hành động | Kết quả mong đợi | KQ thực tế | Đạt? |
| --- | --- | --- | --- | --- |
| 0.1 | Chạy app khi SQL Server **đang tắt** | Alert "Lỗi kết nối CSDL…", app không mở màn hình đăng nhập | | |
| 0.2 | Bật SQL Server, chạy lại app | Mở màn hình **Đăng nhập** | | |

## S1 — UC001: Đăng nhập & phân quyền

| Bước | Hành động | Kết quả mong đợi | KQ thực tế | Đạt? |
| --- | --- | --- | --- | --- |
| 1.1 | Để trống cả hai ô, bấm Đăng nhập | Alert VL13 | | |
| 1.2 | `NV0001` / mật khẩu sai | Alert TB01, ở lại màn đăng nhập | | |
| 1.3 | `NV0004` / `123456` | Alert TB02 "Tài khoản của bạn đã bị khóa." | | |
| 1.4 | `NV0001` / `123456` | Vào GD_Chinh; menu theo thứ tự: Tổng quan, Hồ sơ bệnh nhân, Tra cứu hồ sơ bệnh nhân, Thu tiền, Lập phiếu khám, Quản lý ca thu ngân. Trong Lập phiếu khám, danh sách phòng hiện ngay; chọn phòng trống → bấm Lập phiếu khám tại phòng này | | |
| 1.5 | Đăng xuất → `NV0002` / `123456` | Menu có: Tổng quan, Khám và chẩn đoán, Lịch sử khám | | |
| 1.6 | Đăng xuất → `NV0005` / `123456` | Menu có: Tổng quan, Hàng chờ theo phòng | | |
| 1.7 | Đăng xuất → `NV0007` / `123456` | Menu có: Tổng quan, Quản lý hàng hóa, Quản lý nhân viên, Quản lý dịch vụ, Báo cáo lượt khám; không có Tra cứu hồ sơ bệnh nhân, Hàng chờ theo phòng, Lịch sử khám. Phân tích AI nằm trong màn Quản lý hàng hóa | | |
| 1.8 | DB: `SELECT matKhau FROM TaiKhoan WHERE tenDangNhap='NV0001'` | Giá trị là hash 64 ký tự, không phải "123456" | | |

## S1b — Lập phiếu khám theo tile phòng (HOME lễ tân, `NV0001`) — mẫu SoDoPhong N11

| Bước | Hành động | Kết quả mong đợi | KQ thực tế | Đạt? |
| --- | --- | --- | --- | --- |
| 1b.1 | Menu "Quầy hôm nay" (mục đầu) | Tile từng phòng, TOÀN MÀU: đỏ = đang khám, xanh dương = còn lượt chưa thu phí, vàng = có hàng chờ, xanh lá = trống | | |
| 1b.2 | Lọc "Phòng còn lượt chưa thu phí" | Chỉ còn các phòng lễ tân cần thu | | |
| 1b.3 | Bấm một tile | Dialog lượt khám hôm nay của phòng (STT, BN, trạng thái, hóa đơn) | | |
| 1b.4 | Chọn lượt HOÀN TẤT → "Thu phí cho lượt đã chọn" | Sang màn Thanh toán, lượt đó được chọn sẵn | | |

## S2 — UC002: Quản lý hồ sơ bệnh nhân (đăng nhập `NV0001`) UC002: Quản lý hồ sơ bệnh nhân (đăng nhập `NV0001`)

| Bước | Hành động | Kết quả mong đợi | KQ thực tế | Đạt? |
| --- | --- | --- | --- | --- |
| 2.0 | Mở màn Hồ sơ bệnh nhân (chưa bấm gì) → nhập SĐT → Tìm; để trống rồi bấm Tìm; bấm "Hiện tất cả" | Mở màn = **bảng TRỐNG**; Tìm theo SĐT ra đúng bản ghi; để trống → báo gợi ý "Nhập số điện thoại..." và bảng giữ trống; "Hiện tất cả" là thao tác tường minh mới tải toàn bộ (Góp ý GV 2/5 cách 1) | | |
| 2.0a | Tra cứu theo họ tên không dấu; gõ tên bệnh nhân cũ ở ô gợi ý tiếp nhận/lập phiếu | Tìm đúng hồ sơ theo từ khóa, gợi ý trả về hồ sơ phù hợp dù không nằm trong nhóm bệnh nhân mới nhất | | |
| 2.1 | Mở QL hồ sơ, bấm Thêm khi bỏ trống họ tên | Alert VL01 | | |
| 2.2 | Nhập đủ, SĐT `0901111` (7 số) | Alert VL02 | | |
| 2.3 | SĐT hợp lệ, CCCD `12345678901` (11 số) | Alert VL03 | | |
| 2.4 | Ngày sinh = ngày mai | Alert VL04 | | |
| 2.5 | Nhập hợp lệ, CCCD = `079199001001` (của BN seed 0001) | Alert TB13 | | |
| 2.6 | CCCD để trống hoặc nhập CCCD mới, SĐT trùng `0901111001` | Bấm Thêm | Alert `TB20`, hồ sơ không được tạo | | |
| 2.6a | Nhập cả SĐT và CCCD đã thuộc hồ sơ khác | Bấm Thêm | Alert `TB13`, hồ sơ không được tạo | | |
| 2.9 | Dữ liệu đã có mã BN đầu ngày nhưng bộ đếm chưa đồng bộ | Tạo hồ sơ hợp lệ | Tự bỏ qua mã đã tồn tại; mã mới theo mẫu `BNyyMMddNNNN`, không hiện lỗi PRIMARY KEY | | |
| 2.7 | Click dòng vừa tạo → sửa họ tên → Sửa | Cập nhật thành công, bảng refresh | | |
| 2.8 | Ngắt DB (tắt dịch vụ SQL) rồi bấm Thêm | Alert TB09, mở lại DB app vẫn chạy bình thường | | |

## S2b — Tiếp nhận nhanh 1 chạm (đăng nhập `NV0001`) — luồng tối ưu dùng trong thực tế

| Bước | Hành động | Kết quả mong đợi | KQ thực tế | Đạt? |
| --- | --- | --- | --- | --- |
| 2b.1 | Menu "Tiếp nhận nhanh" (đầu tiên) | Wizard bước 1; ô tìm có gợi ý type-ahead | | |
| 2b.2 | Gõ "ngo" | Gợi ý BN Ngô ngay khi gõ, chọn bằng chuột hoặc Enter | | |
| 2b.3 | Chọn BN cũ → Tiếp tục | Đã có phí chưa dùng → nhảy thẳng bước 3; chưa có → bước 2 | | |
| 2b.4 | Bước 2 thu phí (tiền thừa tự tính) → Thu phí | Tạo hóa đơn, tự sang bước 3 | | |
| 2b.5 | Bước 3: bác sĩ gợi ý "ít chờ nhất", chip lý do nhanh → Lập phiếu | Phiếu + STT hiện, xem trước phiếu in tại chỗ | | |
| 2b.6 | Nhập BN mới hoàn toàn (không chọn gợi ý) | BUS.taoHoSo vẫn chặn VL01–VL04/TB13/TB20 như UC002 | | |

## S3 — UC005: Thu phí khám — hiện nằm ở **BƯỚC 2 của màn Tiếp nhận nhanh** (màn rời cũ đã gộp) UC005: Thu tiền phí khám (`NV0001`)

| Bước | Hành động | Kết quả mong đợi | KQ thực tế | Đạt? |
| --- | --- | --- | --- | --- |
| 3.1 | Bấm Thu phí khi chưa tìm BN | Alert VL05 | | |
| 3.1a | Mở màn Thu phí khi chưa chọn bệnh nhân | Mức phí hiện lời nhắc chọn bệnh nhân; ô tiền khách đưa và tiền thừa trống | | |
| 3.2 | Tìm `0901111004` (BN0004) — mức phí hiện 50.000 ₫ — nhập tiền khách đưa `30000` | Alert "Tiền khách đưa chưa đủ mức phí khám." | | |
| 3.3 | Nhập `100000`, hình thức TIEN_MAT → Thu phí | Báo thành công, hiện "Đã thu: HDyyMMdd000x — 50.000 ₫" | | |
| 3.4 | Bấm **Thu phí lần nữa** cho cùng BN | Alert TB04 "Bệnh nhân đã hoàn tất phí khám." | | |
| 3.5 | Sau khi thu phí, bấm "Xem/In hóa đơn" | Hóa đơn phí khám dạng gọn, hẹp; xem trọn trang trong panel mà không cuộn dọc, không che form; lưu PDF/in/đóng được và không tạo hóa đơn mới | | |
| 3.6 | Bộ đếm hóa đơn thấp hơn mã đã tồn tại trong ngày, sau đó thu phí bệnh nhân khác | Thu phí thành công với mã hóa đơn chưa tồn tại; không báo lỗi PRIMARY KEY | | |

## S4 — UC003: Lập phiếu khám — CHỈ QUA TILE PHÒNG (màn cột chọn phòng đã bỏ; `NV0001`) UC003: Lập phiếu khám (`NV0001`)

| Bước | Hành động | Kết quả mong đợi | KQ thực tế | Đạt? |
| --- | --- | --- | --- | --- |
| 4.1 | Mở Lập phiếu khám, chọn một phòng | Danh sách lượt của phòng hiện bên dưới tile; có bác sĩ và phụ tá được phân công | | |
| 4.2 | Chọn phòng tắt nhận bệnh hoặc chưa phân công nhân sự | Nút Lập phiếu bị khóa | | |
| 4.3 | Chọn phòng đang nhận bệnh, để trống lý do rồi lập phiếu | Alert VL08 | | |
| 4.4 | BN đã thu phí + phòng đang nhận bệnh có bác sĩ, phụ tá phân công + lý do → Lập phiếu | Thành công: STT tiếp theo của phòng, trạng thái CHO_KHAM; bác sĩ trên phiếu đúng phân công | | |
| 4.5 | Lập tiếp phiếu cho BN khác đã thu phí cùng phòng | STT tăng; hai mã phiếu khác nhau | | |
| 4.6 | Tắt nhận bệnh ở phòng đang chọn | Nút Lập phiếu bị khóa khi tải lại; BUS trả TB03 nếu gọi trực tiếp | | |
| 4.7 | Khóa tài khoản bác sĩ hoặc phụ tá được phân công cho phòng, rồi thử lập phiếu | BUS từ chối; không tạo phiếu | | |
| 4.8 | Nhấn nút **Lập phiếu 2 lần liên tục nhanh** (cùng BN đã dùng phí → lần 2 sẽ chặn) | Lần 1 thành công; lần 2 Alert TB04 — không có phiếu trùng mã/STT | | |
| 4.9 | Lập phiếu thành công rồi bấm "Lưu PDF phiếu tiếp nhận" | Phiếu xem trước trong form có mã, STT, tên BN, bác sĩ, lý do; tệp PDF lưu được | | |
| 4.10 | Gọi BUS lập phiếu P001 với bác sĩ không được phân công cho P001 | Alert TB21; không tạo phiếu hoặc cấp STT | | |
| 4.11 | Đổi bộ lọc hoặc tìm phòng khác sau khi đã chọn phòng | Bảng chi tiết và nút lập phiếu luôn khớp phòng đang hiển thị | | |

## S4b — Quản lý ca thu ngân (cuối ngày)

Ca kéo dài qua nửa đêm vẫn tính đủ hóa đơn từ thời điểm mở đến thời điểm chốt, kể cả hóa đơn do lễ tân khác thu trong cùng ca.

| Bước | Hành động | Kết quả mong đợi | KQ thực tế | Đạt? |
| --- | --- | --- | --- | --- |
| 4b.1 | Cuối ngày mở "Lập phiếu khám", nhìn bảng kê dưới màn | Mọi hóa đơn ĐÃ THU hôm nay: giờ, BN, loại (phí khám/dịch vụ), hình thức, tiền | | |
| 4b.2 | So tổng "TỔNG THU HÔM NAY" với tiền mặt + chuyển khoản trong két | Khớp — nghiệp vụ chốt sổ bàn giao ca của thu ngân | | |
| 4b.3 | Bấm "Lưu PDF bảng kê (bàn giao ca)" | Ra file PDF bảng kê đầy đủ để nộp | | |

## S5 — UC009: Hàng chờ (`NV0005`)

| Bước | Hành động | Kết quả mong đợi | KQ thực tế | Đạt? |
| --- | --- | --- | --- | --- |
| 5.1 | Mở Hàng chờ, chọn P001 | Bảng "HÀNG CHỜ" có 4 dòng (3 seed + S4.4), đúng thứ tự STT | | |
| 5.2 | Chọn phòng P004 (không có phiếu) | Không lỗi; khi Tải lại → thông báo TB07 | | |
| 5.3 | Letan vừa lập phiếu mới (màn khác) → chờ 10 giây không bấm gì | Hàng chờ tự hiện phiếu mới (Timeline refresh) | | |
| 5.4 | Chọn STT 4 → **Gọi số** | Bảng số hiển thị "4", "Phòng P001 - <tên BN>"; DB kiểm tra phiếu **vẫn** `CHO_KHAM` | | |
| 5.5 | Gọi lại đúng phiếu đó lần nữa | Alert TB10 | | |
| 5.6 | Chọn STT 4 → **Xác nhận vào khám** | Báo "đã đưa vào khám"; phiếu rời hàng chờ, xuất hiện ở bảng ĐANG KHÁM; DB: `trangThai='DANG_KHAM'` | | |
| 5.7 | Bấm Gọi số trên phiếu đã vào khám | Alert TB10 | | |
| 5.8 | *(2 máy)* Hai phụ tá cùng chọn 1 phiếu rồi cùng bấm Xác nhận vào khám | 1 máy thành công, máy kia Alert TB10 | | |
| 5.9 | PT-05: chọn P004 → Tắt rồi Bật nhận bệnh | Thông báo thành công; NV0001 lập phiếu lại thấy/không thấy P003/P004 theo trạng thái | | |

## S6 — UC004: Khám và chẩn đoán (`NV0002`)

| Bước | Hành động | Kết quả mong đợi | KQ thực tế | Đạt? |
| --- | --- | --- | --- | --- |
| 6.1 | Mở Khám bệnh | Danh sách = phiếu `CHO_KHAM`/`DANG_KHAM` của `NV0002` hôm nay (không thấy phiếu của NV0003) | | |
| 6.2 | Chọn phiếu đang `CHO_KHAM` | Nút Lưu/Hoàn tất **bị disable** | | |
| 6.3 | Chọn phiếu `DANG_KHAM` (đã vào khám ở S5.6) | Nút enable; tiền sử hiện từ hồ sơ bệnh án | | |
| 6.4 | Bấm Lưu khi chẩn đoán trống | Alert VL09 | | |
| 6.5 | Nhập chẩn đoán, **không** có chỉ định → Lưu | Alert TB06 | | |
| 6.6 | Thêm chỉ định DV0010 (Tráng men — NGUNG_PHUC_VU) → Lưu | Alert TB15, không lưu gì | | |
| 6.6a | Phiếu khám đã liên kết hóa đơn phí khám đã thu, chỉ định DV0001 (Phí khám ban đầu) | Đơn giá hiện 0đ; DV0001 không xuất hiện trong danh sách cần thu cuối lượt | | |
| 6.7 | Chỉ định DV0003 + DV0006, thuốc TH0001 SL 12 + ghi chú → Lưu | Báo thành công; phiếu vẫn `DANG_KHAM`; DB có đúng 2 dòng ChiDinhDichVu, 1 dòng ChiTietCapThuoc | | |
| 6.8 | Sửa chỉ định (xóa DV0006, thêm DV0002) → Lưu lại | DB đúng 2 dòng mới (DV0003, DV0002) — không nhân đôi | | |
| 6.9 | Bấm **Hoàn tất** | Báo "HOAN_TAT"; phiếu rời danh sách; DB `trangThai='HOAN_TAT'`, có `maHoSo` | | |
| 6.10 | Hoàn tất lại phiếu đó (tìm qua DB/đăng nhập lại) | Alert TB10 | | |
| 6.11 | Đăng nhập `NV0003`, mở thủ công mã phiếu của `NV0002` (Sửa SQL tạm `trangThai='DANG_KHAM'`) rồi Lưu/Hoàn tất | Alert TB10/TB08 — chặn sửa phiếu của bác sĩ khác | | |
| 6.12 | Thuốc số lượng nhập `0` hoặc `abc` | Alert VL12 | | |

## S7 — UC007: Quản lý dịch vụ (`NV0007`)

| Bước | Hành động | Kết quả mong đợi | KQ thực tế | Đạt? |
| --- | --- | --- | --- | --- |
| 7.1 | Thêm khi tên trống hoặc giá `0`/`abc` | Alert VL01 / VL06 | | |
| 7.2 | Thêm "Cạo vôi răng" (trùng tên) | Alert TB12 | | |
| 7.3 | Thêm dịch vụ mới giá hợp lệ | Mã `DV0012`, trạng thái Đang phục vụ | | |
| 7.4 | Sửa tên + giá dịch vụ vừa thêm | Thành công; bảng refresh | | |
| 7.5 | Đổi trạng thái DV0012 → NGUNG_PHUC_VU | Thành công; bac sĩ **không** chỉ định được DV này (S6.6) | | |
| 7.6 | Đổi trạng thái DV0001 (Phí khám ban đầu) → NGUNG | ⚠️ Cho phép nhưng sẽ làm thu phí lỗi — khôi phục lại DANG_PHUC_VU ngay sau test | | |

## QT-07 — Quản lý hàng hóa/tồn kho (`NV0007`)

| Bước | Hành động | Kết quả mong đợi | KQ thực tế | Đạt? |
| --- | --- | --- | --- | --- |
| QT07.1 | Mở “Quản lý hàng hóa” (trên “Quản lý nhân viên”) | Bảng hàng hóa hiển thị mã, tên, đơn vị, tồn kho, mức tối thiểu, tình trạng | | |
| QT07.2 | Thêm “Găng tay y tế”, đơn vị “Hộp”, tồn 10, tối thiểu 5 | Tạo mã HH tự động, xuất hiện trong bảng, tình trạng “Đủ hàng” | | |
| QT07.3 | Thêm lại cùng tên | Báo TB18, không tạo bản ghi trùng | | |
| QT07.4 | Chọn hàng vừa thêm, sửa tồn thành 3 rồi cập nhật | Bảng hiển thị 3, tình trạng “Cần bổ sung” | | |
| QT07.5 | Nhập tồn âm hoặc chữ | Báo VL18, dữ liệu cũ giữ nguyên | | |

## S8 — UC010: Quản lý nhân viên & tài khoản (`NV0007`)

| Bước | Hành động | Kết quả mong đợi | KQ thực tế | Đạt? |
| --- | --- | --- | --- | --- |
| 8.1 | Thêm NV thiếu họ tên / SĐT sai | Alert VL01 / VL02 | | |
| 8.1a | Thêm hoặc sửa NV với ngày sinh chưa đủ 18 tuổi (mọi chức vụ) | Alert VL14; dữ liệu nhân viên không bị thay đổi | | |
| 8.1b | Thêm hoặc sửa NV đúng ngày tròn 18 tuổi | Cho phép lưu nếu các dữ liệu khác hợp lệ | | |
| 8.2 | Thêm NV hợp lệ (chức vụ PHU_TA) | Mã `NVyyMMdd000x`, hiện trong bảng | | |
| 8.3 | Chọn NV mới, tạo tài khoản `phuta2` / `123456` | Báo thành công; đăng nhập được `phuta2` | | |
| 8.4 | Tạo lại tên `phuta2` | Alert TB14 | | |
| 8.5 | Khóa/mở tài khoản `NV0003` | Đổi trạng thái thành công (dùng ở S4.7); **nhớ mở lại sau test** | | |
| 8.6 | Xóa NV đã có phiếu khám (NV0002) | Alert TB11 | | |
| 8.7 | Xóa NV mới tạo (chưa có tài khoản/phiếu) — phải xóa tài khoản trước nếu đã tạo | Xóa thành công | | |

## S9 — UC008: Báo cáo lượt khám (`NV0007`)

| Bước | Hành động | Kết quả mong đợi | KQ thực tế | Đạt? |
| --- | --- | --- | --- | --- |
| 9.1 | Từ = hôm qua, Đến = hôm nay, bấm Xem | Biểu đồ + bảng: hôm qua 2 lượt, hôm nay = số phiếu thực tế (seed 4 + S4 đã lập) | | |
| 9.2 | Từ > Đến | Alert VL10 | | |
| 9.3 | Tab tần suất dịch vụ | DV0003 (hàn răng) ≥ 2 lượt, DV0002 ≥ 2, DV0006 ≥ 2 (theo seed + S6) | | |
| 9.4 | Xuất CSV → mở bằng Excel | Mở đúng font tiếng Việt (UTF-8 BOM), cột phân cách `;` | | |
| 9.5 | Đăng nhập `NV0001` rồi mở Báo cáo (qua menu không thấy; kiểm tra bằng BUS khi gọi API) | Menu ẩn; nếu gọi trực tiếp → TB08 | | |

## S10 — Tình huống đồng thời (2 máy, cùng DB)

| Bước | Hành động | Kết quả mong đợi | KQ thực tế | Đạt? |
| --- | --- | --- | --- | --- |
| 10.1 | 2 NV0001 cùng bấm Thêm hồ sơ ngay lúc nhau | Hai mã BN khác nhau (BoDem nguyên tử), không trùng | | |
| 10.2 | 2 NV0001 cùng lập phiếu P001 | STT khác nhau, mã khác nhau | | |
| 10.3 | 2 phụ tá cùng Xác nhận vào khám 1 phiếu | 1 thành công, 1 → TB10 | | |

---


## S11 — Thanh toán dịch vụ, Lịch sử khám, In phiếu, Gợi ý AI (bổ sung theo sơ đồ luồng Word)

| Mã | Tình huống | Kết quả mong đợi | KQ thực tế | Đạt? |
| --- | --- | --- | --- | --- |
| TC-S11-01 | `NV0001` → "Thanh toán dịch vụ" → chọn lượt khám đã HOAN_TAT (từ S6) | Panel phải hiện đúng các dịch vụ đã chỉ định + tổng tiền (đơn giá niêm yết) | | |
| TC-S11-01a | Chọn lượt HOAN_TAT không có dịch vụ chỉ định | Lượt khám không xuất hiện trong danh sách chờ thanh toán dịch vụ; phí khám ban đầu đã thu ở UC005 không bị thu lại | | |
| TC-S11-02 | Tiền khách đưa thiếu tổng | Báo "Tiền khách đưa chưa đủ để thanh toán." | | |
| TC-S11-03 | Xác nhận thanh toán | Hóa đơn HDyyMMdd… tạo kèm chi tiết từng dịch vụ; lượt khám rời danh sách chờ thanh toán | | |
| TC-S11-04 | Thanh toán lại cùng lượt khám | Báo TB17 "Lượt khám này đã được thanh toán." | | |
| TC-S11-05 | `NV0002` → "Lịch sử khám" | Bảng mọi lượt khám (badge màu theo trạng thái); tìm "Nguyễn" → lọc đúng | | |
| TC-S11-06 | Double-click một dòng lịch sử | Hộp chi tiết: chẩn đoán, kết quả, thuốc tự mua, trạng thái | | |
| TC-S11-07 | `NV0001` → "Lập phiếu khám" → Xem / in phiếu PDF | Bản xem trước ở cột phải có STT, phòng khám, thông tin bệnh nhân, bác sĩ, lý do, hướng dẫn và QR chứa mã phiếu; Lưu PDF tạo tệp mở được, In phiếu mở hộp chọn máy in | | |

---


## S12 — Giao diện toàn màn hình và hiển thị dữ liệu

| Mã | Tình huống | Kết quả mong đợi | KQ thực tế | Đạt? |
| --- | --- | --- | --- | --- |
| TC-S12-01 | Mở màn đăng nhập và màn chính | Nút nguồn nền xanh, biểu tượng trắng ở góc trên phải; đóng được ứng dụng | | |
| TC-S12-02 | Quan sát panel ảnh đăng nhập | Nền panel xanh liền với mép ảnh, không có dải màu lệch rõ | | |
| TC-S12-03 | Đăng nhập rồi chọn các chức năng trong menu | Chuyển màn hình bằng menu, không có nút Quay lại dư thừa | | |
| TC-S12-04 | Bấm Đăng xuất ở sidebar | Nút có biểu tượng đăng xuất cạnh chữ; trở về màn đăng nhập | | |
| TC-S12-05 | Mở app toàn màn hình | Menu và dòng bảng dễ đọc; không có nút/chữ quá nhỏ | | |
| TC-S12-06 | Fullscreen → mở Tra cứu / Hồ sơ / Lịch sử khám | Bảng dãn theo khung; bảng Hồ sơ quản lý hiển thị STT, Mã BN, Họ tên, SĐT; bảng Tra cứu hiển thị thêm CCCD, Địa chỉ và Tiền sử dị ứng; không tràn ngang | | |
| TC-S12-07 | Mở Khám bệnh trên màn hình 15.6 inch | Vùng nhập bên phải đủ rộng, nội dung nút không hiện `...`; khi thiếu chiều cao có thanh cuộn dọc | | |
| TC-S12-08 | Kiểm tra data: Hồ sơ bệnh nhân → bấm "Hiện tất cả" (màn mở là bảng trống) | Đúng 10 bệnh nhân, đủ CCCD + địa chỉ + tiền sử hiển thị đầy đủ, không trùng lặp | | |

---

## 3. Bảng tra thông báo chuẩn

| Mã | Nội dung | Mã | Nội dung |
| --- | --- | --- | --- |
| TB01 | Vui lòng kiểm tra lại tên đăng nhập hoặc mật khẩu. | VL01 | Họ tên không được để trống. |
| TB02 | Tài khoản của bạn đã bị khóa. | VL02 | Số điện thoại phải gồm 10 chữ số và bắt đầu bằng 0. |
| TB03 | Bác sĩ/Phòng khám hiện không trong ca. Vui lòng chọn khác. | VL03 | CCCD phải gồm 12 chữ số. |
| TB04 | Bệnh nhân đã hoàn tất phí khám. | VL04 | Ngày sinh không được là ngày trong tương lai. |
| TB05 | Không tìm thấy hồ sơ bệnh nhân. | VL05 | Vui lòng chọn bệnh nhân. |
| TB06 | Vui lòng chọn ít nhất một dịch vụ hợp lệ trong danh mục. | VL06 | Đơn giá phải là số dương lớn hơn 0. |
| TB07 | Hiện không có bệnh nhân đang chờ khám. | VL07 | Vui lòng chọn bác sĩ và phòng khám. |
| TB08 | Bạn không có quyền thực hiện chức năng này. | VL08 | Lý do khám không được để trống. |
| TB09 | Lưu dữ liệu không thành công. Vui lòng thử lại. | VL09 | Vui lòng nhập chẩn đoán trước khi lưu. |
| TB10 | Trạng thái phiếu khám đã thay đổi. Vui lòng tải lại danh sách. | VL10 | Từ ngày không được sau Đến ngày. |
| TB11 | Không thể xóa vì dữ liệu đang được sử dụng. | VL11 | Vui lòng chọn dịch vụ cần chỉ định. |
| TB12 | Tên dịch vụ đã tồn tại. | VL12 | Số lượng phải là số nguyên dương. |
| TB13 | CCCD đã tồn tại trong hồ sơ bệnh nhân khác. | VL13 | Vui lòng nhập tên đăng nhập và mật khẩu. |
| TB14 | Tên đăng nhập đã tồn tại. | | |
| TB15 | Dịch vụ đã ngừng phục vụ, không thể chỉ định. | | |

## 4. Ghi nhận kết quả

- Người test: ______________ Ngày: ______ Môi trường: Windows ______ / SQL Server ______ / JDK ______
- Kết luận từng kịch bản: S0 ☐ S1 ☐ S2 ☐ S3 ☐ S4 ☐ S5 ☐ S6 ☐ S7 ☐ S8 ☐ S9 ☐ S10 ☐
- Bằng chứng kèm theo (ảnh chụp/alert, câu lệnh SQL kiểm tra DB, file CSV) lưu vào `TaiLieu/docs/evidence/<tên thành viên>/`.

> **Lưu ý sau test:** khôi phục trạng thái đã sửa tạm (bật lại `NV0003`, DV0001) hoặc chạy lại toàn bộ seed để có dữ liệu demo chuẩn.

## S13 — Hồi quy sau rà soát hệ thống

| Mã | Thao tác | Kết quả mong đợi |
| --- | --- | --- |
| TC-S13-01 | Thanh toán một phiếu rồi tải lại danh sách | Chi tiết dịch vụ, tổng tiền, tiền khách đưa và tiền thừa của phiếu cũ được xóa; không hiện dữ liệu cũ khi danh sách trống. |
| TC-S13-02 | Vào Hàng chờ, chuyển màn hình rồi quay lại nhiều lần | Chỉ màn đang mở tự làm mới mỗi 10 giây; không có nhiều thông báo hàng chờ trống lặp lại. |
| TC-S13-03 | Chuyển phòng trong Hàng chờ | Số vừa gọi và nút Gọi lại được đặt lại theo phòng mới. |
| TC-S13-04 | Bác sĩ tải lại danh sách khi không còn phiếu khám | Trường nhập, bảng chỉ định/thuốc được xóa; nút Lưu và Hoàn tất bị khóa. |
| TC-S13-05 | Đăng nhập sai vai trò và gọi BUS đọc chỉ định/thuốc/thanh toán | Bị từ chối với TB08; GUI không truy vấn DAO trực tiếp. |
| TC-S13-06 | Hai máy đồng thời tạo mã mới đầu ngày | Bộ đếm khóa trong transaction; mã không trùng và không lỗi INSERT bộ đếm. |
| TC-S13-07 | Tạo mã dịch vụ ở hai ngày khác nhau | Mã dịch vụ tiếp tục tăng, không reset theo ngày. |

## TC-S14 — Xuất hóa đơn cuối lượt

| Mã | Thao tác | Kết quả mong đợi |
| --- | --- | --- |
| TC-S14-01 | Lễ tân chọn lượt HOAN_TAT, nhập tiền hợp lệ và xác nhận thanh toán | Giao dịch được lưu; khu vực bên phải chuyển sang xem trước hóa đơn, không che menu hay mở đè lên form. |
| TC-S14-02 | Đối chiếu nội dung hóa đơn | Có mã hóa đơn, lượt khám, bệnh nhân, từng dịch vụ, số lượng, đơn giá, thành tiền, tổng thanh toán, phương thức, tiền khách trả/thừa và nhân viên thu. |
| TC-S14-03 | Chọn Lưu PDF | Tạo PDF mở được, hiển thị đầy đủ chi tiết và số tiền đã lưu; bản xem trước gọn, hẹp, không cần cuộn dọc. |
| TC-S14-04 | Chọn In hóa đơn | Mở hộp thoại máy in; hủy thì không in, xác nhận thì gửi hóa đơn đến máy in. |
| TC-S14-05 | Chọn Quay lại thanh toán sau khi phát hành | Trở về màn thanh toán; lượt vừa thu không còn trong danh sách chờ. |
