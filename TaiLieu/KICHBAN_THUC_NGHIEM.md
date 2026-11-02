# KỊCH BẢN THỰC NGHIỆM — HỆ THỐNG QUẢN LÝ PHÒNG KHÁM NHA KHOA SÀI GÒN (Nhóm 15)

> **Cách dùng:** chạy đúng thứ tự các kịch bản S0 → S10 trên **một máy có CSDL local** (trừ S10 cần 2 máy). Mỗi bước ghi "Kết quả thực tế" + đánh dấu Đạt/Chưa để làm minh chứng kiểm thử (CLO4). Thông báo chuẩn TBxx/VLxx tra ở bảng cuối file.

---

## 1. Chuẩn bị môi trường (làm 1 lần)

| # | Việc | Kết quả mong đợi |
| --- | --- | --- |
| P1 | Cài JDK 21 + Maven (hoặc VS Code + Extension Pack for Java); cài SQL Server Express + SSMS | `java -version` báo 21; `mvn -v` chạy được |
| P2 | Bật TCP/IP 1433 trong SQL Server Configuration Manager, restart dịch vụ; bật SQL auth + user `sa` | `sqlcmd -S localhost -U sa -P <mật khẩu> -Q "SELECT 1"` trả về 1 |
| P3 | Trong SSMS chạy `SourceCode/DATA/schema/000_hatang.sql` → `010_*.sql` đúng thứ tự | Mỗi file in ra "… OK"; có 13 bảng trong `N15_PTUD_DB` (12 bảng nghiệp vụ + `BoDem`) |
| P4 | Chạy `SourceCode/DATA/seed/001_…` → `010_…` đúng thứ tự | Mỗi file in ra "… OK"; `PhieuKham` có 6 phiếu (4 hôm nay, 2 hôm qua) |
| P5 | Copy `src/db.properties.example` thành `db.properties`, sửa `db.password` | File tồn tại, KHÔNG hiện trong `git status` |
| P6 | Tại thư mục gốc chạy `mvn clean compile` rồi `mvn javafx:run` | Mở màn hình **Đăng nhập** (nếu lỗi kết nối → xem lại P2–P5) |

## 2. Tài khoản seed (mật khẩu chung `123456`)

| Tên đăng nhập | Vai trò | Dùng cho kịch bản |
| --- | --- | --- |
| `letan` | Lễ tân | S2, S3, S4 |
| `bacsi1` / `bacsi2` | Bác sĩ | S6 |
| `phuta` | Phụ tá | S5 |
| `quanly` | Quản lý | S7, S8, S9 |
| `lockacc` | Lễ tân (BI_KHOA) | S1 |

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
| 1.2 | `letan` / mật khẩu sai | Alert TB01, ở lại màn đăng nhập | | |
| 1.3 | `lockacc` / `123456` | Alert TB02 "Tài khoản của bạn đã bị khóa." | | |
| 1.4 | `letan` / `123456` | Vào GD_Chinh; menu có: Tổng quan, Hồ sơ bệnh nhân, Tra cứu hồ sơ bệnh nhân, Thu lệ phí khám, Thanh toán dịch vụ, Lập phiếu khám | | |
| 1.5 | Đăng xuất → `bacsi1` / `123456` | Menu có: Tổng quan, Khám và chẩn đoán, Lịch sử khám | | |
| 1.6 | Đăng xuất → `phuta` / `123456` | Menu có: Tổng quan, Hàng chờ theo phòng | | |
| 1.7 | Đăng xuất → `quanly` / `123456` | Menu có: Tổng quan, Quản lý nhân viên, Quản lý dịch vụ, Báo cáo lượt khám, AI dự báo vật tư (chức năng bổ sung); không có Tra cứu hồ sơ bệnh nhân, Hàng chờ theo phòng, Lịch sử khám | | |
| 1.8 | DB: `SELECT matKhau FROM TaiKhoan WHERE tenDangNhap='letan'` | Giá trị là hash 64 ký tự, không phải "123456" | | |

## S2 — UC002: Quản lý hồ sơ bệnh nhân (đăng nhập `letan`)

| Bước | Hành động | Kết quả mong đợi | KQ thực tế | Đạt? |
| --- | --- | --- | --- | --- |
| 2.1 | Mở QL hồ sơ, bấm Thêm khi bỏ trống họ tên | Alert VL01 | | |
| 2.2 | Nhập đủ, SĐT `0901111` (7 số) | Alert VL02 | | |
| 2.3 | SĐT hợp lệ, CCCD `12345678901` (11 số) | Alert VL03 | | |
| 2.4 | Ngày sinh = ngày mai | Alert VL04 | | |
| 2.5 | Nhập hợp lệ, CCCD = `079199001001` (của BN seed 0001) | Alert TB13 | | |
| 2.6 | CCCD để trống, SĐT trùng `0901111001` | Hỏi "Vẫn tạo hồ sơ mới?" → Chọn **Có** | Tạo thành công, mã `BNyyMMdd0001`, hiện đầu bảng | | |
| 2.7 | Click dòng vừa tạo → sửa họ tên → Sửa | Cập nhật thành công, bảng refresh | | |
| 2.8 | Ngắt DB (tắt dịch vụ SQL) rồi bấm Thêm | Alert TB09, mở lại DB app vẫn chạy bình thường | | |

## S3 — UC005: Thu tiền phí khám (`letan`)

| Bước | Hành động | Kết quả mong đợi | KQ thực tế | Đạt? |
| --- | --- | --- | --- | --- |
| 3.1 | Bấm Thu phí khi chưa tìm BN | Alert VL05 | | |
| 3.2 | Tìm `0901111004` (BN0004) — mức phí hiện 50.000 ₫ — nhập tiền khách đưa `30000` | Alert "Tiền khách đưa chưa đủ mức phí khám." | | |
| 3.3 | Nhập `100000`, hình thức TIEN_MAT → Thu phí | Báo thành công, hiện "Đã thu: HDyyMMdd000x — 50.000 ₫" | | |
| 3.4 | Bấm **Thu phí lần nữa** cho cùng BN | Alert TB04 "Bệnh nhân đã hoàn tất phí khám." | | |
| 3.5 | Bấm "Dùng hóa đơn đã thu" | Hiện "Dùng hóa đơn đã thu: HD…", không phát sinh hóa đơn mới | | |

## S4 — UC003: Lập phiếu khám (`letan`)

| Bước | Hành động | Kết quả mong đợi | KQ thực tế | Đạt? |
| --- | --- | --- | --- | --- |
| 4.1 | Mở Lập phiếu với BN chưa thu phí | Chỉ báo "(chưa có - hãy dùng màn hình Thu phí)" | | |
| 4.2 | Lập phiếu chưa chọn bác sĩ/phòng | Alert VL07 | | |
| 4.3 | Chọn bác sĩ/phòng, lý do để trống | Alert VL08 | | |
| 4.4 | BN0004 (đã thu phí S3.3) + `bacsi1` + `P001` + lý do → Lập phiếu | Thành công: **STT = 4** (sau 3 phiếu seed của P001), mã `PKyyMMdd0005`, "chờ khám" | | |
| 4.5 | Lập tiếp phiếu thứ 2 cho BN0005 cùng phòng | STT = 5; hai mã khác nhau | | |
| 4.6 | Chọn phòng P003 (tắt nhận bệnh) → Lập phiếu | Alert TB03 | | |
| 4.7 | Gọi SP: khóa tài khoản `bacsi2` (S8.4), rồi lập phiếu chọn `bacsi2` | Alert TB03 | | |
| 4.8 | Nhấn nút **Lập phiếu 2 lần liên tục nhanh** (cùng BN đã dùng phí → lần 2 sẽ chặn) | Lần 1 thành công; lần 2 Alert TB04 — không có phiếu trùng mã/STT | | |
| 4.9 | Bấm "Xem phiếu" | Modal hiện đúng mã, STT, tên BN, bác sĩ, lý do | | |

## S5 — UC009: Hàng chờ (`phuta`)

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
| 5.9 | PT-05: chọn P004 → Tắt rồi Bật nhận bệnh | Thông báo thành công; letan lập phiếu lại thấy/không thấy P003/P004 theo trạng thái | | |

## S6 — UC004: Khám và chẩn đoán (`bacsi1`)

| Bước | Hành động | Kết quả mong đợi | KQ thực tế | Đạt? |
| --- | --- | --- | --- | --- |
| 6.1 | Mở Khám bệnh | Danh sách = phiếu `CHO_KHAM`/`DANG_KHAM` của `bacsi1` hôm nay (không thấy phiếu của bacsi2) | | |
| 6.2 | Chọn phiếu đang `CHO_KHAM` | Nút Lưu/Hoàn tất **bị disable** | | |
| 6.3 | Chọn phiếu `DANG_KHAM` (đã vào khám ở S5.6) | Nút enable; tiền sử hiện từ hồ sơ bệnh án | | |
| 6.4 | Bấm Lưu khi chẩn đoán trống | Alert VL09 | | |
| 6.5 | Nhập chẩn đoán, **không** có chỉ định → Lưu | Alert TB06 | | |
| 6.6 | Thêm chỉ định DV0010 (Tráng men — NGUNG_PHUC_VU) → Lưu | Alert TB15, không lưu gì | | |
| 6.7 | Chỉ định DV0003 + DV0006, thuốc TH0001 SL 12 + ghi chú → Lưu | Báo thành công; phiếu vẫn `DANG_KHAM`; DB có đúng 2 dòng ChiDinhDichVu, 1 dòng ChiTietCapThuoc | | |
| 6.8 | Sửa chỉ định (xóa DV0006, thêm DV0002) → Lưu lại | DB đúng 2 dòng mới (DV0003, DV0002) — không nhân đôi | | |
| 6.9 | Bấm **Hoàn tất** | Báo "HOAN_TAT"; phiếu rời danh sách; DB `trangThai='HOAN_TAT'`, có `maHoSo` | | |
| 6.10 | Hoàn tất lại phiếu đó (tìm qua DB/đăng nhập lại) | Alert TB10 | | |
| 6.11 | Đăng nhập `bacsi2`, mở thủ công mã phiếu của `bacsi1` (Sửa SQL tạm `trangThai='DANG_KHAM'`) rồi Lưu/Hoàn tất | Alert TB10/TB08 — chặn sửa phiếu của bác sĩ khác | | |
| 6.12 | Thuốc số lượng nhập `0` hoặc `abc` | Alert VL12 | | |

## S7 — UC007: Quản lý dịch vụ (`quanly`)

| Bước | Hành động | Kết quả mong đợi | KQ thực tế | Đạt? |
| --- | --- | --- | --- | --- |
| 7.1 | Thêm khi tên trống hoặc giá `0`/`abc` | Alert VL01 / VL06 | | |
| 7.2 | Thêm "Cạo vôi răng" (trùng tên) | Alert TB12 | | |
| 7.3 | Thêm dịch vụ mới giá hợp lệ | Mã `DV0012`, trạng thái Đang phục vụ | | |
| 7.4 | Sửa tên + giá dịch vụ vừa thêm | Thành công; bảng refresh | | |
| 7.5 | Đổi trạng thái DV0012 → NGUNG_PHUC_VU | Thành công; bac sĩ **không** chỉ định được DV này (S6.6) | | |
| 7.6 | Đổi trạng thái DV0001 (Phí khám ban đầu) → NGUNG | ⚠️ Cho phép nhưng sẽ làm thu phí lỗi — khôi phục lại DANG_PHUC_VU ngay sau test | | |

## S8 — UC010: Quản lý nhân viên & tài khoản (`quanly`)

| Bước | Hành động | Kết quả mong đợi | KQ thực tế | Đạt? |
| --- | --- | --- | --- | --- |
| 8.1 | Thêm NV thiếu họ tên / SĐT sai | Alert VL01 / VL02 | | |
| 8.1a | Thêm hoặc sửa NV với ngày sinh chưa đủ 18 tuổi (mọi chức vụ) | Alert VL14; dữ liệu nhân viên không bị thay đổi | | |
| 8.1b | Thêm hoặc sửa NV đúng ngày tròn 18 tuổi | Cho phép lưu nếu các dữ liệu khác hợp lệ | | |
| 8.2 | Thêm NV hợp lệ (chức vụ PHU_TA) | Mã `NVyyMMdd000x`, hiện trong bảng | | |
| 8.3 | Chọn NV mới, tạo tài khoản `phuta2` / `123456` | Báo thành công; đăng nhập được `phuta2` | | |
| 8.4 | Tạo lại tên `phuta2` | Alert TB14 | | |
| 8.5 | Khóa/mở tài khoản `bacsi2` | Đổi trạng thái thành công (dùng ở S4.7); **nhớ mở lại sau test** | | |
| 8.6 | Xóa NV đã có phiếu khám (NV2608010002) | Alert TB11 | | |
| 8.7 | Xóa NV mới tạo (chưa có tài khoản/phiếu) — phải xóa tài khoản trước nếu đã tạo | Xóa thành công | | |

## S9 — UC008: Báo cáo lượt khám (`quanly`)

| Bước | Hành động | Kết quả mong đợi | KQ thực tế | Đạt? |
| --- | --- | --- | --- | --- |
| 9.1 | Từ = hôm qua, Đến = hôm nay, bấm Xem | Biểu đồ + bảng: hôm qua 2 lượt, hôm nay = số phiếu thực tế (seed 4 + S4 đã lập) | | |
| 9.2 | Từ > Đến | Alert VL10 | | |
| 9.3 | Tab tần suất dịch vụ | DV0003 (hàn răng) ≥ 2 lượt, DV0002 ≥ 2, DV0006 ≥ 2 (theo seed + S6) | | |
| 9.4 | Xuất CSV → mở bằng Excel | Mở đúng font tiếng Việt (UTF-8 BOM), cột phân cách `;` | | |
| 9.5 | Đăng nhập `letan` rồi mở Báo cáo (qua menu không thấy; kiểm tra bằng BUS khi gọi API) | Menu ẩn; nếu gọi trực tiếp → TB08 | | |

## S10 — Tình huống đồng thời (2 máy, cùng DB)

| Bước | Hành động | Kết quả mong đợi | KQ thực tế | Đạt? |
| --- | --- | --- | --- | --- |
| 10.1 | 2 letan cùng bấm Thêm hồ sơ ngay lúc nhau | Hai mã BN khác nhau (BoDem nguyên tử), không trùng | | |
| 10.2 | 2 letan cùng lập phiếu P001 | STT khác nhau, mã khác nhau | | |
| 10.3 | 2 phụ tá cùng Xác nhận vào khám 1 phiếu | 1 thành công, 1 → TB10 | | |

---


## S11 — Thanh toán dịch vụ, Lịch sử khám, In phiếu, Gợi ý AI (bổ sung theo sơ đồ luồng Word)

| Mã | Tình huống | Kết quả mong đợi | KQ thực tế | Đạt? |
| --- | --- | --- | --- | --- |
| TC-S11-01 | `letan` → "Thanh toán dịch vụ" → chọn lượt khám đã HOAN_TAT (từ S6) | Panel phải hiện đúng các dịch vụ đã chỉ định + tổng tiền (đơn giá niêm yết) | | |
| TC-S11-02 | Tiền khách đưa thiếu tổng | Báo "Tiền khách đưa chưa đủ để thanh toán." | | |
| TC-S11-03 | Xác nhận thanh toán | Hóa đơn HDyyMMdd… tạo kèm chi tiết từng dịch vụ; lượt khám rời danh sách chờ thanh toán | | |
| TC-S11-04 | Thanh toán lại cùng lượt khám | Báo TB17 "Lượt khám này đã được thanh toán." | | |
| TC-S11-05 | `bacsi1` → "Lịch sử khám" | Bảng mọi lượt khám (badge màu theo trạng thái); tìm "Nguyễn" → lọc đúng | | |
| TC-S11-06 | Double-click một dòng lịch sử | Hộp chi tiết: chẩn đoán, kết quả, thuốc tự mua, trạng thái | | |
| TC-S11-07 | `letan` → "Lập phiếu khám" → Xem / in phiếu PDF | Bản xem trước phiếu tiếp nhận A4 có STT, phòng khám, thông tin bệnh nhân, bác sĩ, lý do, hướng dẫn và mã QR tra cứu; Lưu PDF tạo tệp mở được, In phiếu mở hộp chọn máy in | | |
| TC-S11-08 | `phuta` → "Hàng chờ theo phòng" → chọn 1 bệnh nhân trong hàng chờ | Hiện banner "AI dự đoán thời gian chờ: ~X phút" (tính theo vị trí hàng chờ + lượt đang khám + thời lượng trung bình) | | |
| TC-S11-10 | `quanly` → "AI dự báo vật tư" → Chạy dự báo | Bảng vật tư (Găng tay, Khẩu trang, Vật liệu trám, Thuốc tê...) với "Đã dùng 30 ngày" + "Dự báo 2 tuần tới" + ghi chú nhu cầu | | |

---


## S12 — Giao diện toàn màn hình và hiển thị dữ liệu

| Mã | Tình huống | Kết quả mong đợi | KQ thực tế | Đạt? |
| --- | --- | --- | --- | --- |
| TC-S12-01 | Mở màn đăng nhập và màn chính | Nút nguồn nền xanh, biểu tượng trắng ở góc trên phải; đóng được ứng dụng | | |
| TC-S12-02 | Quan sát panel ảnh đăng nhập | Nền panel xanh liền với mép ảnh, không có dải màu lệch rõ | | |
| TC-S12-03 | Đăng nhập rồi chọn các chức năng trong menu | Chuyển màn hình bằng menu, không có nút Quay lại dư thừa | | |
| TC-S12-04 | Bấm Đăng xuất ở sidebar | Nút có biểu tượng đăng xuất cạnh chữ; trở về màn đăng nhập | | |
| TC-S12-05 | Mở app toàn màn hình | Menu và dòng bảng dễ đọc; không có nút/chữ quá nhỏ | | |
| TC-S12-06 | Fullscreen → mở Tra cứu / Hồ sơ / Lịch sử khám | Bảng dãn theo khung, đủ cột, không tràn ngang, không trống rỗng | | |
| TC-S12-07 | Fullscreen → Thu phí / Khám bệnh | Form nằm trong card cân đối, ô nhập dãn theo khung, không bị hẹp cắt chữ | | |
| TC-S12-08 | Kiểm tra data: Hồ sơ bệnh nhân | Đúng 10 bệnh nhân, đủ CCCD + địa chỉ + tiền sử hiển thị đầy đủ, không trùng lặp | | |

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

> **Lưu ý sau test:** khôi phục trạng thái đã sửa tạm (bật lại `bacsi2`, DV0001) hoặc chạy lại toàn bộ seed để có dữ liệu demo chuẩn.
