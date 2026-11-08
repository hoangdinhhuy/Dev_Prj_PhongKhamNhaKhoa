# Ôn bảo vệ phần Huy — hiểu luồng từ nút đến CSDL

> Tài liệu này giúp trình bày đúng phần Huy phụ trách và trả lời câu hỏi “nút này chạy tới đâu, kiểm tra gì, lưu ở đâu, dữ liệu quay lên giao diện thế nào”. Không cần thuộc từng dòng; hãy nắm vai trò từng lớp và lần theo các điểm code được dẫn bên dưới.

## 1. Câu giới thiệu ngắn

> “Ứng dụng dùng kiến trúc 3 tầng: GUI nhận thao tác, BUS kiểm tra quyền và nghiệp vụ, DAO dùng JDBC để truy vấn hoặc cập nhật SQL Server. Entity là đối tượng mang dữ liệu giữa các lớp. Giao diện JavaFX/FXML gọi Controller theo sự kiện; mẫu phiếu và hóa đơn xem/in được dựng bằng JavaFX rồi nhận dữ liệu từ Controller.”

Phần Huy tập trung vào hồ sơ bệnh nhân, thu phí khám ban đầu, lập phiếu khám và hóa đơn; một số màn thanh toán cuối lượt dùng chung BUS/DAO hóa đơn. Phạm vi giao việc ghi ở [README_HUY.md](docs/tasks/README_HUY.md).

## Ràng buộc trong phần Huy phụ trách

Các rule dưới đây được kiểm tra chủ yếu ở BUS; những hàm định dạng/kiểm tra chung nằm trong `ValidateUtil`. Đây là các điều kiện cụ thể trong luồng hồ sơ bệnh nhân, thu phí, lập phiếu và khám.

| Chức năng | Điều kiện | Vị trí kiểm tra / lỗi |
|---|---|---|
| Tạo/sửa hồ sơ bệnh nhân | Chỉ lễ tân; họ tên không rỗng; SĐT đúng 10 số và bắt đầu bằng 0; ngày sinh hợp lệ, không tương lai; CCCD tùy chọn nhưng nếu nhập phải đủ 12 số | `BenhNhan_BUS.taoHoSo/capNhatHoSo`; `ValidateUtil`; quyền sai `TB08`, dữ liệu sai `VL01/VL02/VL03/VL04` |
| Tạo/sửa hồ sơ bệnh nhân | CCCD không được trùng hồ sơ khác; SĐT không được trùng hồ sơ khác (khi sửa thì loại chính hồ sơ đang sửa khỏi kiểm tra) | `BenhNhan_BUS`; trùng CCCD `TB13`, trùng SĐT `TB20` |
| Thu phí khám ban đầu | Chỉ lễ tân; phải có mã bệnh nhân, cấu hình dịch vụ phí khám `DV0001`, chưa có hóa đơn phí khám chưa dùng; tiền khách đưa phải là số dương và không thấp hơn phí | `HoaDon_BUS.thuPhiKham`; mã/đầu vào `VL05`/`VL06`, đã thu `TB04`, thiếu tiền bị từ chối |
| Lập phiếu khám | Chỉ lễ tân; phải chọn bệnh nhân, bác sĩ và phòng hợp lệ; lý do khám không rỗng; phải dùng hóa đơn phí khám đã thanh toán và chưa gắn phiếu nào | `PhieuKham_BUS.lapPhieuKham`; lỗi thiếu dữ liệu `VL05/VL07/VL08`, bác sĩ/phòng không hợp lệ `TB03`, hóa đơn phí không hợp lệ `TB04` |
| Khám và lưu kết quả | Chỉ bác sĩ phụ trách phiếu; phiếu phải đang `DANG_KHAM`; chẩn đoán bắt buộc; phải có ít nhất một dịch vụ chỉ định; số lượng thuốc cấp ít nhất 1 | `KhamBenh_BUS.luuKham/hoanTat`; `VL09/VL12`, trạng thái hoặc quyền không hợp lệ `TB08/TB10`, thiếu dịch vụ `TB06` |
| Dịch vụ được chỉ định | Chỉ dùng dịch vụ đang `DANG_PHUC_VU` | `KhamBenh_BUS.luuKham`; dịch vụ ngừng phục vụ `TB15` |
| Thanh toán dịch vụ cuối lượt | Chỉ lễ tân; phiếu phải `HOAN_TAT`, có dịch vụ tính tiền, chưa có hóa đơn thanh toán cho phiếu; tiền khách đưa phải đủ tổng | `HoaDon_BUS.thanhToanDichVu`; trạng thái sai `TB10`, không có dịch vụ `TB06`, đã thanh toán `TB17`, thiếu tiền bị từ chối |

**Ví dụ trả lời về SĐT:** “Trong `BenhNhan_BUS.taoHoSo`, BUS gọi `ValidateUtil.sdt(soDienThoai)`. Hàm ở `ValidateUtil` kiểm tra biểu thức `^0\\d{9}$`, tức đúng 10 chữ số và bắt đầu bằng 0; sau đó BUS còn kiểm tra SĐT đã tồn tại chưa, nếu trùng thì báo `TB20`.”

## 2. Mô hình nào đang dùng?

### Kiến trúc chính: 3 tầng

```text
FXML / Controller (GUI) → BUS (nghiệp vụ) → DAO (JDBC/SQL) → SQL Server
                              ↕
                         Entity / enum
```

- **GUI:** FXML khai báo bố cục và `onAction`; Controller lấy giá trị ô nhập, gọi BUS, rồi cập nhật bảng/nhãn hoặc mở mẫu in. GUI không viết SQL và không gọi DAO trực tiếp.
- **BUS:** kiểm tra quyền, xác thực dữ liệu, áp dụng quy tắc nghiệp vụ, phối hợp transaction và gọi DAO.
- **DAO:** chứa truy vấn SQL/`PreparedStatement`, ánh xạ kết quả `ResultSet` thành Entity và trả kết quả về BUS.
- **Entity:** các lớp như `BenhNhan`, `PhieuKham`, `HoaDon`, `ChiTietHoaDon` mang dữ liệu giữa GUI/BUS/DAO. Entity không phải bảng database và không tự gọi SQL.
- **SQL Server:** nơi lưu dữ liệu. JDBC là cách Java kết nối/gửi SQL đến SQL Server; trong dự án, `ConnectDB` cấp `Connection` dùng chung.

`DATA/schema/*.sql` là **script tạo/chỉnh cấu trúc database**; `DATA/seed/*.sql` thêm dữ liệu mẫu. Chúng thường chạy riêng trong SSMS. Khi app hoạt động, DAO gửi SQL runtime qua JDBC. Schema không phải tầng Java.

### Các mô hình/kỹ thuật phụ cô có thể hỏi

- **Lập trình hướng sự kiện:** nút FXML có `onAction="#them"`, `onAction="#thuPhi"`… JavaFX gọi hàm Controller khi người dùng bấm. Ô tiền dùng listener để cập nhật tiền thừa khi nội dung thay đổi.
- **DAO pattern:** gom thao tác đọc/ghi từng nhóm bảng trong `*_DAO`, tách SQL khỏi giao diện/nghiệp vụ.
- **Singleton kết nối:** `ConnectDB.getInstance()` cung cấp một đầu mối lấy `Connection`, cấu hình đọc từ `db.properties`.
- **Entity/POJO:** đối tượng dữ liệu như `HoaDon` hoặc `ChiTietHoaDon` được truyền giữa lớp.
- **Transaction:** gom nhiều câu ghi liên quan thành một lần commit; nếu lỗi thì rollback để tránh lưu dở dang.

Có thể nói GUI tách FXML và Controller theo cách tổ chức gần với MVC, nhưng tên kiến trúc chính của phần ứng dụng là **3 tầng**. Tránh khẳng định dự án áp dụng một framework MVC hoàn chỉnh.

## 3. Ví dụ đầy đủ: bấm “Thêm bệnh nhân”

Luồng đáng nhớ:

```text
GD_QLBenhNhan.fxml
  → onAction="#them"
GD_QLBenhNhan_Controller.them()
  → BenhNhan_BUS.taoHoSo(...)
  → ValidateUtil / QuyenUtil / transaction
  → BenhNhan_DAO.themBenhNhan(con, bn)
  → PreparedStatement INSERT → SQL Server
  ← commit, trả Entity BenhNhan
Controller báo thành công → loadTableData() → BUS/DAO đọc lại danh sách → TableView cập nhật
```

1. Nút được khai báo ở [GD_QLBenhNhan.fxml](../SourceCode/src/gui/GD_QLBenhNhan.fxml) với `onAction="#them"`.
2. [`GD_QLBenhNhan_Controller.them()`](../SourceCode/src/gui/GD_QLBenhNhan_Controller.java) lấy `getText()`/ngày trên form rồi gọi `BenhNhan_BUS.taoHoSo(...)`.
3. [`BenhNhan_BUS.taoHoSo(...)`](../SourceCode/src/bus/BenhNhan_BUS.java) kiểm tra quyền lễ tân; kiểm tra họ tên, SĐT, ngày sinh, CCCD và trùng CCCD/SĐT; xin mã bệnh nhân qua `BoDem_DAO`.
4. BUS tạo Entity `BenhNhan`, mở transaction bằng `setAutoCommit(false)`, rồi gọi DAO.
5. [`BenhNhan_DAO.themBenhNhan(...)`](../SourceCode/src/dao/BenhNhan_DAO.java) chạy `INSERT INTO BenhNhan ... VALUES (?, ?, ...)`. Dấu `?` là tham số; DAO gán giá trị bằng `PreparedStatement.setString/setDate...`, giúp truyền dữ liệu đúng kiểu và tránh nối chuỗi người dùng thành SQL.
6. Thành công thì `commit()`; lỗi SQL thì `rollback()`. Controller nhận Entity hoặc lỗi, hiện thông báo; thành công thì nạp lại dữ liệu để dòng mới xuất hiện trong bảng.

**Trả lời nếu cô hỏi “dữ liệu trả lên bảng bằng cách nào?”**: “Sau khi BUS trả về thành công, Controller gọi hàm nạp danh sách. Hàm đó gọi BUS/DAO đọc lại các bệnh nhân; DAO ánh xạ từng dòng `ResultSet` thành `BenhNhan`; Controller đặt danh sách Entity vào `TableView`.”

## 4. Hóa đơn gồm đầu hóa đơn và các dòng chi tiết

Trong database, hai phần này tách riêng:

| Bảng | Ý nghĩa | Ví dụ dữ liệu |
|---|---|---|
| `HoaDon` | Phần đầu: mã hóa đơn, thời điểm thu, hình thức, đã thanh toán chưa, tiền khách đưa, bệnh nhân, nhân viên thu, có thể gắn phiếu khám | Một hóa đơn cho một lần thanh toán |
| `ChiTietHoaDon` | Các khoản nằm trong hóa đơn: dịch vụ, số lượng, đơn giá tại lúc thu, thành tiền | Một hóa đơn có một hoặc nhiều dòng dịch vụ |

Quan hệ: `HoaDon` **1–n** `ChiTietHoaDon`; mỗi dòng chi tiết tham chiếu `maHoaDon`. Thành tiền dòng được tính theo `soLuong × donGia`. `donGia` được lưu như giá tại thời điểm thu để thay đổi bảng giá về sau không làm đổi hóa đơn cũ.

### A. Thu phí khám ban đầu

- Controller [`GD_ThuPhi_Controller.thuPhi()`](../SourceCode/src/gui/GD_ThuPhi_Controller.java) gọi `HoaDon_BUS.thuPhiKham(...)`.
- BUS kiểm tra quyền, bệnh nhân, mức phí DV0001, chưa thu trùng và tiền khách đưa đủ.
- BUS mở transaction, tạo mã HD, tạo Entity `HoaDon`, gọi `HoaDon_DAO.themHoaDon(...)` để ghi phần đầu và `ChiTietHoaDon_DAO.themChiTiet(...)` để ghi dòng DV0001. Chỉ commit khi cả hai ghi thành công.
- Hóa đơn phí khám ban đầu ban đầu chưa gắn phiếu; khi lập phiếu, mã hóa đơn phí được gắn với `PhieuKham.maHoaDonPhiKham`.

### B. Thanh toán dịch vụ cuối lượt

- Màn hình nạp các phiếu `HOAN_TAT` có dịch vụ tính tiền và chưa có hóa đơn cuối lượt. Truy vấn nằm ở [`PhieuKham_DAO.layPhieuHoanTatChuaThanhToan()`](../SourceCode/src/dao/PhieuKham_DAO.java).
- Khi chọn phiếu, Controller lấy các dịch vụ đã chỉ định, cộng giá để hiện tổng tiền.
- Bấm “Xác nhận thanh toán” gọi [`HoaDon_BUS.thanhToanDichVu(...)`](../SourceCode/src/bus/HoaDon_BUS.java). BUS kiểm tra phiếu đã hoàn tất, có dịch vụ tính tiền, chưa có hóa đơn dịch vụ, và tiền khách đưa đủ tổng.
- BUS tạo phần đầu `HoaDon` có `maPhieuKham`, rồi tạo một dòng `ChiTietHoaDon` cho từng dịch vụ. Cả hóa đơn và các dòng chi tiết cùng transaction.

### C. Đọc dữ liệu để xem/in hóa đơn

Đây là luồng **đọc để hiển thị**, khác với luồng **tạo/lưu** ở BUS:

1. [`HoaDon_DAO.layHoaDonTheoMa(...)`](../SourceCode/src/dao/HoaDon_DAO.java) đọc phần đầu hóa đơn và `JOIN BenhNhan` để lấy tên bệnh nhân.
2. [`ChiTietHoaDon_DAO.layChiTietTheoHoaDon(...)`](../SourceCode/src/dao/ChiTietHoaDon_DAO.java) đọc các dòng và `JOIN DichVu` để lấy tên dịch vụ.
3. `HoaDon_BUS.layChiTietHoaDon(...)` kiểm tra quyền/trạng thái rồi trả danh sách chi tiết cho Controller.
4. Controller truyền Entity hóa đơn, phiếu khám (nếu là hóa đơn cuối lượt), danh sách chi tiết và session vào `HoaDonPhiKhamView.tao(...)` hoặc `HoaDonThanhToanView.tao(...)`.

Lưu ý khi nói cho chính xác: lúc vừa thu tiền, Controller còn giữ Entity `HoaDon` BUS vừa trả về; phần chi tiết được đọc lại qua BUS/DAO để dựng View. View không tự truy vấn database. Ở các truy vấn đọc, `JOIN` ghép dữ liệu liên quan; `JOIN` không phải thao tác tạo hóa đơn.

## 5. Tính tiền thừa nằm ở đâu?

Ở màn cuối lượt, [`GD_ThanhToan_Controller.initialize()`](../SourceCode/src/gui/GD_ThanhToan_Controller.java) gắn listener vào ô tiền khách đưa:

```java
txt_tienDua.textProperty().addListener((o, cu, moi) -> capNhatTienThua());
```

Mỗi lần người dùng sửa ô, `capNhatTienThua()` lấy chuỗi thành `BigDecimal` rồi gọi:

```java
dua.subtract(tongTien)
```

`BigDecimal` là kiểu biểu diễn tiền chính xác; `subtract` mới là phép trừ. `tongTien` được cộng từ giá dịch vụ của phiếu đang chọn. Kết quả được `FormatUtil.tien(...)` định dạng rồi đưa vào label tiền thừa. Ô trống/nhập chưa thành số thì label để trống. Màn thu phí ban đầu tính tương tự nhưng trừ giá phí khám.

**Phép tính trên giao diện chỉ để xem trước.** Khi bấm xác nhận, BUS tự kiểm tra `tienDua.compareTo(tong) < 0`; nếu thiếu tiền thì từ chối, không lưu hóa đơn. Như vậy quy tắc nghiệp vụ không chỉ dựa vào label của GUI.

## 6. Mẫu phiếu/hóa đơn và PDF

FXML dùng để mô tả bố cục màn hình chức năng và nối sự kiện tới Controller. Có thể dùng Scene Builder chỉnh FXML nếu phù hợp. Tuy nhiên, các mẫu in hiện tại là JavaFX View được dựng bằng code trong:

- [`PhieuTiepNhanView`](../SourceCode/src/gui/PhieuTiepNhanView.java)
- [`HoaDonPhiKhamView`](../SourceCode/src/gui/HoaDonPhiKhamView.java)
- [`HoaDonThanhToanView`](../SourceCode/src/gui/HoaDonThanhToanView.java)

Controller đưa dữ liệu vào các hàm `tao(...)`; các View tạo `VBox`, `Label`, bảng dòng chi tiết và định dạng. Logo, địa chỉ, tiêu đề, câu cảm ơn/hướng dẫn là nội dung cố định hoặc tài nguyên ảnh; dữ liệu bệnh nhân, mã hóa đơn/phiếu, dịch vụ, giá, ngày thu lấy từ Entity/danh sách đã có. Nút Lưu PDF/In xuất nội dung View hiện tại; thao tác xuất không tự tạo thêm hóa đơn hay ghi một khoản thu mới.

Nếu cô hỏi “em thiết kế bằng kéo thả hết à?”, trả lời trung thực: “Bố cục màn hình chức năng nằm trong FXML và có thể chỉnh bằng Scene Builder. Mẫu phiếu/hóa đơn xem/in trong bản này được dựng bằng JavaFX code trong các lớp View; Controller truyền dữ liệu vào mẫu.”

## 7. Câu trả lời ngắn cho các câu cô hay hỏi

**“Nút này đi đâu?”**  
“`onAction` trong FXML gọi hàm `@FXML` cùng tên trong Controller. Controller lấy input, gọi BUS; BUS kiểm tra quyền/nghiệp vụ rồi gọi DAO; DAO dùng JDBC chạy SQL. Kết quả trả ngược qua BUS về Controller để cập nhật giao diện.”

**“BUS khác DAO ở đâu?”**  
“BUS quyết định dữ liệu có hợp lệ và thao tác có được phép không; DAO chỉ biết cách đọc/ghi database và ánh xạ kết quả.”

**“Entity có lưu xuống database không?”**  
“Entity chỉ là đối tượng Java mang giá trị. DAO đọc field của Entity để gán vào PreparedStatement; SQL Server mới là nơi lưu bền vững.”

**“Vì sao hóa đơn có hai bảng?”**  
“Một hóa đơn có thông tin chung và nhiều khoản thu. `HoaDon` lưu phần đầu; `ChiTietHoaDon` lưu từng khoản để xem/in được tên dịch vụ, số lượng và giá từng dòng.”

**“`JOIN` tạo hóa đơn phải không?”**  
“Không. INSERT trong DAO lưu hóa đơn; JOIN dùng ở truy vấn SELECT để ghép thông tin hóa đơn với bệnh nhân hoặc dòng chi tiết với dịch vụ khi đọc lên.”

**“Nếu lưu đầu hóa đơn xong nhưng thêm chi tiết lỗi?”**  
“BUS đang mở transaction. Lỗi trước commit thì rollback cả phần đầu và chi tiết, tránh hóa đơn bị lưu thiếu dòng.”

**“Schema có phải tầng thứ tư?”**  
“Không. Schema là script định nghĩa cấu trúc SQL Server. Phần ứng dụng có ba tầng GUI, BUS, DAO; SQL Server là hệ quản trị dữ liệu mà DAO kết nối qua JDBC.”

**“Đây là hướng đối tượng hay hướng sự kiện?”**  
“Có cả hai khía cạnh: các lớp Controller/BUS/DAO/Entity tổ chức chương trình theo hướng đối tượng; JavaFX dùng sự kiện như nút `onAction` và listener để kích hoạt luồng xử lý.”

## 8. Mấy ý cần tránh nói nhầm

- Không nói `BigDecimal` tự biết trừ tiền; code gọi `subtract`.
- Không nói `JOIN` ghi hoặc tạo hóa đơn; `INSERT` mới ghi.
- Không nói toàn bộ nội dung mẫu in đều truy vấn từ database; logo, địa chỉ, nhãn và câu hướng dẫn có thể là dữ liệu cố định.
- Không nói mọi mẫu in được kéo thả bằng Scene Builder; ba mẫu trên dựng bằng JavaFX code.
- Không nói SQL schema được Java chạy mỗi lần bấm nút; schema thường chạy riêng khi cài/đổi cấu trúc database.
- Không gọi dự án là “4 tầng ứng dụng” chỉ vì có SQL Server. Cách nói rõ nhất: **3 tầng ứng dụng GUI–BUS–DAO, DAO truy cập SQL Server bằng JDBC**.

## 9. Bản đồ file để mở nhanh khi cô hỏi

| Câu hỏi | Mở file |
|---|---|
| Nút gắn Controller ở đâu? | `SourceCode/src/gui/GD_QLBenhNhan.fxml`, `GD_ThuPhi.fxml`, `GD_LapPhieuKham.fxml`, `GD_ThanhToan.fxml` |
| Nút tạo hồ sơ gọi lớp nào? | `GD_QLBenhNhan_Controller.java` → `BenhNhan_BUS.java` → `BenhNhan_DAO.java` |
| Phí khám ban đầu được lưu thế nào? | `GD_ThuPhi_Controller.java` → `HoaDon_BUS.java` → `HoaDon_DAO.java` + `ChiTietHoaDon_DAO.java` |
| Thanh toán dịch vụ cuối lượt? | `GD_ThanhToan_Controller.java` → `HoaDon_BUS.java` → các DAO hóa đơn/chi tiết |
| Dữ liệu phiếu khám JOIN từ đâu? | `PhieuKham_DAO.java` — `JOINS` với `BenhNhan`, `NhanVien`, `PhongKham` |
| Mẫu in được dựng ở đâu? | `PhieuTiepNhanView.java`, `HoaDonPhiKhamView.java`, `HoaDonThanhToanView.java` |
| Bảng/khóa SQL ở đâu? | `SourceCode/DATA/schema/005_benhnhan.sql`, `007_phieukham.sql`, `008_hoadon_chitiethoadon.sql` |

### Câu chốt nếu cần trình bày trong 20 giây

> “Người dùng bấm nút trên FXML thì JavaFX gọi Controller. Controller lấy dữ liệu form và gọi BUS. BUS kiểm tra quyền, dữ liệu và quy tắc; sau đó DAO dùng JDBC/PreparedStatement để làm việc với SQL Server. Dữ liệu đọc được ánh xạ thành Entity, trả ngược về Controller để lên bảng hoặc truyền vào View phiếu/hóa đơn. Các thao tác ghi nhiều bảng dùng transaction để commit đồng bộ hoặc rollback khi lỗi.”
