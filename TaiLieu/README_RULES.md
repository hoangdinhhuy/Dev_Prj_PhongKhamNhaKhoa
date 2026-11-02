# README_RULES — QUY TẮC CODE CHUNG NHÓM 15

> **Bắt buộc đọc trước khi viết dòng code đầu tiên.** Mọi thứ cần thống nhất khi code được chốt trong file này. Mục tiêu: **không phát sinh bất kỳ lỗi dữ liệu (data) hay lỗi định dạng (format) nào khi ghép code của 4 người.** Ai muốn thay đổi quy tắc → mở PR sửa file này và được cả nhóm đồng ý, KHÔNG tự đi riêng.

---

## 1. Môi trường & phiên bản chốt (cấm tự đổi)

| Hạng mục | Chốt | Lý do bắt buộc |
| --- | --- | --- |
| JDK | **21** (Temurin 21) | khớp báo cáo + `pom.xml` |
| Build | **Maven** — một `pom.xml` duy nhất | build đồng nhất mọi máy, chạy tốt trên VS Code |
| UI | **JavaFX 21 + FXML** (Gluon Scene Builder) | khớp báo cáo; cấm nhét Swing/AWT vào |
| Driver JDBC | `com.microsoft.sqlserver:mssql-jdbc:12.8.1.jre11` | khai báo trong `pom.xml`, cấm copy jar vào `Library/` |
| CSDL | SQL Server Express 2019/2022 + SSMS, DB tên **`N15_PTUD_DB`** | thống nhất script + seed |
| IDE | VS Code + Extension Pack for Java | Eclipse/IntelliJ vẫn chạy được vì có Maven |
| Encoding | UTF-8 cho mọi file (java/fxml/sql/md) | tiếng Việt không lỗi font |
| Font UI | Segoe UI | hiển thị tiếng Việt đúng |

Cấm: thêm dependency mới vào `pom.xml` mà không qua PR của Duy; đổi version bất kỳ thư viện; dùng JDK khác 21.

---

## 2. Kiến trúc 3 lớp — quyền hạn và cấm kỵ từng tầng

```
GUI (gui/) ──gọi──► BUS (bus/) ──gọi──► DAO (dao/) ──JDBC──► SQL Server
                         │
Model (model/) ◄─────────┴── dùng chung (POJO + enum, không phụ thuộc tầng nào)
connect/ConnectDB: nơi DUY NHẤT cấp Connection
util/: hàm format, validate, phân quyền, alert dùng chung
```

| Tầng | ĐƯỢC làm | CẤM làm |
| --- | --- | --- |
| **GUI** (FXML + Controller) | nhận/kiểm tra hình thức input (rỗng, regex); gọi BUS; hiển thị; điều hướng qua `App.setRoot/openModal`; Alert theo message chuẩn | viết SQL/JDBC; gọi thẳng DAO; tự chế câu thông báo lỗi nghiệp vụ; lưu dữ liệu vào biến static riêng |
| **BUS** | validate nghiệp vụ; kiểm tra quyền (`QuyenUtil`); điều phối transaction nhiều bước; sinh mã qua `BoDem_DAO`; quyết định message chuẩn trả về | viết SQL; trả về ResultSet; mở Alert; giữ trạng thái trong biến static |
| **DAO** | PreparedStatement; đọc/ghi 1 vùng bảng của mình; ánh xạ `ResultSet → Model`; trả `boolean`/entity/`List` | mở Alert/JOptionPane; kiểm tra quyền; tự commit trong transaction do BUS mở; SELECT `*` khi entity có nhiều cột |
| **Model** | POJO: field private + getter/setter + constructor đủ/rút gọn/no-arg; enum; `equals/hashCode` theo khóa chính; helper `getXxxString()` | chứa control JavaFX (`TextField`, `TableView`…); chứa `Connection`; chứa logic DB |
| **connect** | `ConnectDB` singleton + đọc `db.properties` | hardcode user/password vào code; tạo lớp kết nối thứ hai |

### 2.1. Pattern chuẩn bắt chước từ N11 (viết y hệt kiểu này)

**ConnectDB (singleton, do Thanh chủ trì — người khác chỉ dùng):**

```java
public class ConnectDB {
    private static Connection con;
    private static final ConnectDB instance = new ConnectDB();
    public static ConnectDB getInstance() { return instance; }

    public void connect() { /* đọc db.properties, DriverManager.getConnection */ }
    public Connection getConnection() { /* tự nối lại nếu rớt */ }
}
```

**DAO (chuẩn N11: `Xxx_DAO`, PreparedStatement, đóng tài nguyên trong finally):**

```java
public class BenhNhan_DAO {
    public ArrayList<BenhNhan> getAllBenhNhan() {
        ArrayList<BenhNhan> ds = new ArrayList<>();
        Connection con = ConnectDB.getInstance().getConnection();
        PreparedStatement pstm = null; ResultSet rs = null;
        try {
            String sql = "SELECT maBenhNhan, hoTen, soDienThoai, ... FROM BenhNhan";
            pstm = con.prepareStatement(sql);
            rs = pstm.executeQuery();
            while (rs.next()) { ds.add(new BenhNhan(rs.getString("maBenhNhan"), ...)); }
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi truy vấn BenhNhan", ex);   // GUI sẽ bắt và Alert TB09
        } finally {
            try { if (rs != null) rs.close(); if (pstm != null) pstm.close(); } catch (SQLException ex) { /* ghi log */ }
        }
        return ds;
    }
}
```

**BUS (validate + quyền + transaction):**

```java
public class PhieuKham_BUS {
    public PhieuKham lapPhieuKham(SessionContext session, String maBenhNhan,
                                  String maBacSi, String maPhong, String lyDoKham) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.LE_TAN);           // 1. quyền
        ValidateUtil.khongRong(lyDoKham, MaThongBao.VL09);        // 2. dữ liệu
        // 3. nghiệp vụ: bác sĩ/phòng đang nhận bệnh? → ném TB03 nếu không
        // 4. transaction: sinh mã PK + cấp STT + INSERT phiếu (nhiều DAO, 1 commit)
        ...
    }
}
```

**Transaction nhiều bước (BUS mở, DAO nhận `Connection` qua overload):**

```java
Connection con = ConnectDB.getInstance().getConnection();
try {
    con.setAutoCommit(false);
    String maPK = BoDem_DAO.layMaMoi(con, "PK", LocalDate.now());  // overload nhận Connection
    PhieuKham_DAO.themPhieuKham(con, phieuKham);                    // overload nhận Connection
    con.commit();
} catch (Exception ex) {
    con.rollback();
    throw ex;                                                       // BUS ném message chuẩn, GUI Alert
} finally {
    con.setAutoCommit(true);
}
```

---

## 3. Quy tắc đặt tên (viết sai tên = PR bị reject)

| Đối tượng | Quy tắc | Ví dụ đúng | Ví dụ sai |
| --- | --- | --- | --- |
| Package | lowercase, không dấu | `dao` | `vn.nhom15.DAO` |
| Class/Entity/DAO/BUS | PascalCase, **tiếng Việt không dấu**; DAO đuôi `_DAO`, BUS đuôi `_BUS` | `BenhNhan`, `PhieuKham_DAO`, `HoaDon_BUS` | `benhnhan`, `Customer`, `PhieuKhamDAO` |
| Hàm/biến | camelCase, không dấu | `lapPhieuKham`, `dsPhong`, `donGia` | `LapPhieuKham`, `don_gia` |
| Hằng số | UPPER_SNAKE_CASE | `MAX_SO_THU_TU` | `maxSoThuTu` |
| Enum | tên PascalCase, giá trị UPPER | `TrangThaiPhieuKham.CHO_KHAM` | `choKham` |
| Controller | `GD_<ChucNang>_Controller` (đúng cả chính tả "Controller") | `GD_LapPhieuKham_Controller` | `GD_LapPhieuKham_Cotroller` ⚠️ |
| FXML | cùng tên controller | `GD_LapPhieuKham.fxml` | `lapPhieu.fxml` |
| CSS | `<mucDich>.css` | `dangnhap.css` | `style1.css` |
| Bảng/Cột SQL | PascalCase không dấu, PK dạng `<TiềnTố>Mã` | `PhieuKham(maPhieuKham)` | `phieu_kham` |
| Hàm DAO | bộ cố định: `them`, `sua`, `xoa`, `getAll`, `layTheoMa`, `layTheo<DieuKien>`, `dem`, `thongKeTheo…` | `layPhieuKhamTheoNgay` | `capNhat` lẫn `sua` ⚠️ |

**Biến `@FXML` trong controller (chuẩn N11):** `txt_` (TextField), `cbb` (ComboBox), `btn` (Button), `lb_` (Label), `cl_` (TableColumn), `table…` (TableView), `dp_` (DatePicker), `pn_` (Pane). Ví dụ: `txt_hoTen`, `cbb_bacSi`, `table_phieuKham`, `cl_stt`.

**Nhãn hiển thị trên GUI: tiếng Việt CÓ dấu; tên class/biến/cột DB: tiếng Việt KHÔNG dấu.** (N11: `TrangThaiPhong.DANGTHUE` → hiển thị "Đang thuê".)

---

## 4. Chuẩn hóa dữ liệu — chống lỗi data khi ghép code

### 4.1. Kiểu dữ liệu bắt buộc

| Dữ liệu | Kiểu Java | Kiểu SQL Server | Cấm |
| --- | --- | --- | --- |
| Mã (BN/PK/HD/NV…), SĐT, CCCD | `String` | `varchar`/`nvarchar` | `int`/`long` (mất số 0 đầu, tràn số) |
| Ngày (sinh, khám) | `java.time.LocalDate` | `date` | `java.util.Date`, `String` |
| Thời điểm (thu tiền, gọi số) | `java.time.LocalDateTime` | `datetime2(0)` | `java.util.Date` |
| Tiền (đơn giá, thành tiền) | `java.math.BigDecimal` | `decimal(18,2)` | `double`/`float` (sai số) |
| Số lượng, STT | `int` | `int` | — |
| Đúng/sai (đã thanh toán…) | `boolean` | `bit` | `int` 0/1 tự chế |
| Trạng thái/loại | **enum Java** | `nvarchar` lưu đúng tên enum | số tự chế không có trong báo cáo |

### 4.2. Định dạng hiển thị (dùng `FormatUtil` — cấm tự `String.format` riêng)

| Loại | Chuẩn hiển thị | Ví dụ |
| --- | --- | --- |
| Ngày | `dd/MM/yyyy` (Locale `vi`) | 01/11/2026 |
| Ngày giờ | `dd/MM/yyyy HH:mm` | 01/11/2026 14:05 |
| Tiền VND | `#,##0` + " ₫" | 1.250.000 ₫ |
| Mã định danh | hiển thị nguyên, không format | `BN2611010001` |
| Trạng thái | text tiếng Việt có dấu từ `getXxxString()` | "Chờ khám" |

### 4.3. Kiểm tra đầu vào (dùng `ValidateUtil` — regex chốt một chỗ)

| Dữ liệu | Quy tắc | Message khi lỗi |
| --- | --- | --- |
| Bắt buộc nhập | `trim()` rồi kiểm rỗng → VL01/VL09/… | theo bảng mục 7 |
| SĐT | regex `^0\d{9}$` (10 số, đầu 0) | VL02 |
| CCCD | regex `^\d{12}$` (có nhập mới kiểm) | VL03 |
| Ngày sinh | không sau ngày hiện tại | VL04 |
| Ngày sinh nhân viên | đủ 18 tuổi khi thêm và sửa, áp dụng mọi chức vụ | VL14 |
| Tiền/giá | `> 0` | VL06 |
| Số lượng | số nguyên `>= 1` | VL12 |
| Khoảng thời gian báo cáo | từNgay ≤ đếnNgay | VL10 |

Quy ước chuỗi: **mọi input `trim()` trước khi dùng**; rỗng → chuẩn hóa thành `null` khi ghi DB; so sánh chuỗi mã luôn `equals`, không dùng `==`.

### 4.4. Giá trị enum (khớp báo cáo — cấm đặt thêm giá trị riêng)

| Enum | Giá trị đúng | Bảng lưu |
| --- | --- | --- |
| `ChucVu` | `LE_TAN`, `BAC_SI`, `PHU_TA`, `QUAN_LY` | `NhanVien.chucVu` |
| `TrangThaiTaiKhoan` | `HOAT_DONG`, `BI_KHOA` | `TaiKhoan.trangThai` |
| `TrangThaiPhieuKham` | `CHO_KHAM`, `DANG_KHAM`, `HOAN_TAT` | `PhieuKham.trangThai` |
| `TrangThaiDichVu` | `DANG_PHUC_VU`, `NGUNG_PHUC_VU` | `DichVu.trangThai` |
| Hình thức thanh toán | `TIEN_MAT`, `CHUYEN_KHOAN` | `HoaDon.hinhThucThanhToan` |

### 4.5. Chuyển trạng thái phiếu khám — luồng lõi không được phá vỡ

- `CHO_KHAM`: chỉ Huy tạo (khi lập phiếu). `DANG_KHAM`: chỉ Thanh chuyển (gọi số + xác nhận vào khám). `HOAN_TAT`: chỉ Thắng chuyển (đã có chẩn đoán; nếu có dịch vụ chỉ định thì phải lưu xong chi tiết).
- **UPDATE trạng thái luôn kèm điều kiện trạng thái cũ** và kiểm tra số dòng ảnh hưởng:

```java
String sql = "UPDATE PhieuKham SET trangThai = ? WHERE maPhieuKham = ? AND trangThai = ?";
int n = pstm.executeUpdate();           // n == 0 → người khác đã đổi trạng thái → báo TB09/lỗi nghiệp vụ
```

- Cấm UPDATE toàn bộ phiếu từ object cũ (ghi đè trường luồng khác vừa lưu). Chỉ UPDATE đúng cột nghiệp vụ mình.

---

## 5. Cấp mã tự động & số thứ tự (chống trùng mã/STT khi chạy đồng thời)

1. Mọi mã sinh theo ngày qua **bảng `BoDem(loaiMa, ngay, giaTri)`** — cấp mã trong transaction bằng `UPDATE ... SET giaTri = giaTri + 1 OUTPUT` (khóa dòng, nguyên tử). Cấm `SELECT MAX(ma)+1`, cấm `COUNT+1` — hai máy chạy cùng lúc sẽ trùng mã.
2. Format mã (báo cáo Chương 4): `NVyyMMddNNNN`, `BNyyMMddNNNN`, `HSyyMMddNNNN`, `PKyyMMddNNNN`, `HDyyMMddNNNN`, `DVNNNN`, `THNNNN`, `PNNN`. Hết dải 9999 → ném lỗi TB09 kèm mô tả.
3. `PhieuKham.soThuTu`: đếm riêng theo `(ngayKham, maPhong)`, ràng buộc `UNIQUE` trong DB; reset tự nhiên theo ngày. BUS cấp STT trong cùng transaction với INSERT phiếu.
4. Hàm duy nhất được gọi: `BoDem_DAO.layMaMoi(...)` / `BoDem_DAO.laySoThuTuTiepTheo(...)` (Thanh chủ trì) — cấm viết bộ sinh mã riêng.

---

## 6. Quy tắc SQL Server & script trong `SourceCode/DATA/`

- `SourceCode/DATA/schema/`: `000_hatang.sql` (CREATE DATABASE `N15_PTUD_DB`, collation, bảng `BoDem`) → `001_phongkham.sql` (Thanh) → `002_nhanvien_taikhoan.sql` (Duy) → `003_dichvu.sql` (Duy) → `004_thuoc.sql` (Thắng) → `005_benhnhan.sql` (Huy) → `006_hosobenhan.sql` (Thắng) → `007_phieukham.sql` (Huy) → `008_hoadon_chitiethoadon.sql` (Huy) → `009_chidinhdichvu.sql` (Thắng) → `010_chitietcapthuoc.sql` (Thắng). `011_banggoiso.sql` (Thanh — chốt ở M0 nếu cần lưu "số đang gọi").
- `SourceCode/DATA/seed/`: cùng số với schema (`002_seed_nhanvien_taikhoan.sql`…). Seed đủ FK, ≥10 bệnh nhân, ≥3 bác sĩ, ≥8 dịch vụ, dữ liệu trải 2 tuần để demo báo cáo.
- Chuỗi tiếng Việt trong DB: `nvarchar` (hoặc `varchar` + collation hỗ trợ Unicode — theo file 000); ngày `date`; thời điểm `datetime2(0)`; tiền `decimal(18,2)`; đúng/sai `bit`.
- Ràng buộc: PK + FK đầy đủ; `CHECK` cho giá > 0, số lượng > 0, STT > 0; `UNIQUE(soDienThoai)` cho `TaiKhoan`… theo đặc tả báo cáo; `UNIQUE(cccd)` cho phép NULL.
- **File schema đã chạy chung chỉ thêm file mới, KHÔNG sửa nội dung cũ** (người khác đã chạy rồi).
- Cấm commit: backup `.bak`, dữ liệu bệnh nhân thật, mật khẩu thật.

---

## 7. BẢNG THÔNG BÁO CHUẨN (mỗi lỗi một message duy nhất — cấm tự chế chữ)

GUI hiển thị đúng chuỗi dưới đây; chuỗi nằm trong `util/MaThongBao.java` (hằng số + nội dung). Bus ném message theo mã; GUI chỉ Alert.

### 7.1. Thông báo nghiệp vụ (trích theo đặc tả use case trong báo cáo)

| Mã | Nội dung chính xác | Nơi dùng |
| --- | --- | --- |
| TB01 | `Vui lòng kiểm tra lại tên đăng nhập hoặc mật khẩu.` | UC001 sai thông tin |
| TB02 | `Tài khoản của bạn đã bị khóa.` | UC001 tài khoản BI_KHOA |
| TB03 | `Bác sĩ/Phòng khám hiện không trong ca. Vui lòng chọn khác.` | UC003 |
| TB04 | `Bệnh nhân đã hoàn tất phí khám.` | UC005 thu trùng |
| TB05 | `Không tìm thấy hồ sơ bệnh nhân.` | UC006 |
| TB06 | `Vui lòng chọn ít nhất một dịch vụ hợp lệ trong danh mục.` | UC004 |
| TB07 | `Hiện không có bệnh nhân đang chờ khám.` | UC009 danh sách rỗng |
| TB08 | `Bạn không có quyền thực hiện chức năng này.` | mọi BUS khi sai vai trò |
| TB09 | `Lưu dữ liệu không thành công. Vui lòng thử lại.` | lỗi DB chung (GUI bắt RuntimeException từ DAO/BUS) |
| TB10 | `Trạng thái phiếu khám đã thay đổi. Vui lòng tải lại danh sách.` | UPDATE có điều kiện ảnh hưởng 0 dòng |
| TB11 | `Không thể xóa vì dữ liệu đang được sử dụng.` | xóa bị chặn FK/nghiệp vụ |
| TB12 | `Tên dịch vụ đã tồn tại.` | UC007 trùng tên |
| TB13 | `CCCD đã tồn tại trong hồ sơ bệnh nhân khác.` | UC002 trùng CCCD |
| TB14 | `Tên đăng nhập đã tồn tại.` | UC010 tạo tài khoản |
| TB15 | `Dịch vụ đã ngừng phục vụ, không thể chỉ định.` | UC004 chỉ định |
| TB16 | `Bệnh nhân này đã có hồ sơ bệnh án.` | UC004 tạo hồ sơ (1 BN — 1 hồ sơ) |
| TB17 | `Lượt khám này đã được thanh toán.` | Thanh toán dịch vụ cuối lượt (chống thu 2 lần) |

### 7.2. Thông báo kiểm tra dữ liệu

| Mã | Nội dung chính xác |
| --- | --- |
| VL01 | `Họ tên không được để trống.` |
| VL02 | `Số điện thoại phải gồm 10 chữ số và bắt đầu bằng 0.` |
| VL03 | `CCCD phải gồm 12 chữ số.` |
| VL04 | `Ngày sinh không được là ngày trong tương lai.` |
| VL05 | `Vui lòng chọn bệnh nhân.` |
| VL06 | `Đơn giá phải là số dương lớn hơn 0.` |
| VL07 | `Vui lòng chọn bác sĩ và phòng khám.` |
| VL08 | `Lý do khám không được để trống.` |
| VL09 | `Vui lòng nhập chẩn đoán trước khi lưu.` |
| VL10 | `Từ ngày không được sau Đến ngày.` |
| VL11 | `Vui lòng chọn dịch vụ cần chỉ định.` |
| VL12 | `Số lượng phải là số nguyên dương.` |
| VL13 | `Vui lòng nhập tên đăng nhập và mật khẩu.` |
| VL14 | `Nhân viên phải đủ 18 tuổi.` |

**Khuôn Alert chung (dùng `AlertUtil`):** lỗi validate/nghiệp vụ → `ERROR` với message theo mã; thao tác thành công → `INFORMATION` "Thực hiện thành công." + tên thao tác; xóa/hủy → `CONFIRMATION` "Bạn có chắc muốn xóa …?" + `showAndWait()` kiểm `ButtonType.OK` trước khi gọi BUS. Chỉ GUI được mở Alert.

---

## 8. Quy tắc GUI/JavaFX chung

1. **1 màn hình = 1 cặp file**: `gui/GD_X.fxml` + `gui/GD_X_Controller implements Initializable`. FXML mở bằng Scene Builder, controller khai trong `fx:controller="gui.GD_X_Controller"`.
2. **Điều hướng chỉ qua `App`** (Duy chủ trì): `App.setRoot("GD_X")` đổi màn chính; `App.openModal("GD_X")` mở hộp thoại modal. Session người dùng lưu static tại `App` (`App.session: SessionContext`).
3. **Controller `initialize()`**: nạp ComboBox, gán `setCellValueFactory` cho từng `TableColumn`, bắt sự kiện click dòng, gọi `loadTableData()`.
4. **Nạp bảng**: DAO trả `ArrayList<Entity>` → `FXCollections.observableArrayList(ds)` → `table.getItems().clear(); table.setItems(list);`. STT hiển thị tự sinh bằng `setCellFactory` (`getIndex()+1`), KHÔNG lưu STT hiển thị vào DB (STT khám là nghiệp vụ riêng, xem mục 5).
5. **Click dòng → đổ form**: `table.setOnMouseClicked` → `getSelectionModel().getSelectedItem()` → set vào control; nút Sửa lấy từ selection, không lấy từ TextField mồ côi.
6. **Trình tự nút ghi dữ liệu**: validate form (`kiemTraDuLieu()`) → gọi BUS → thành công: Alert + `loadTableData()` + `clearFields()`; thất bại: Alert đúng message nhận từ BUS, không refresh.
7. Query nặng (báo cáo) phải chạy không chặn UI: dùng `Task`/`Service` nếu vượt ~1 giây (mục tiêu phản hồi ≤ 2 giây theo báo cáo).
8. Tài nguyên: icon ảnh `resources/pic/`, load bằng `getClass().getResource(...)`; CSS đặt `resources/css/`.

---

## 9. Phân quyền (theo vai trò — chặn ở BUS, không chỉ ẩn menu)

- GUI ẩn/hiện menu theo `session.getChucVu()`: Lễ tân → nhóm màn UC002/003/005/006; Bác sĩ → UC004; Phụ tá → UC009; Quản lý → UC007/008/010.
- **Mọi hàm BUS gọi đầu tiên `QuyenUtil.kiemTraQuyen(session, ChucVu…)`** — sai vai trò ném TB08. Session lấy từ `App.session` (do đăng nhập tạo), **cấm** nhận vai trò từ input người dùng trên form.
- Mật khẩu: chỉ lưu hash trong DB (đề xuất báo cáo: BCrypt/Argon2id); cấm log/in mật khẩu ra console; cấm trả entity `TaiKhoan` kèm mật khẩu lên GUI cho người khác xem.

---

## 10. Quy tắc Git/GitHub

| Hạng mục | Chốt |
| --- | --- |
| Nhánh ổn định | `master` — cấm push trực tiếp |
| Nhánh chức năng | `feat/<ban>-<mã>-<tên>` ví dụ `feat/huy-lt-02-thu-phi` |
| Nhánh tài liệu | `prj15/main` |
| Commit | conventional không dấu: `feat(uc003): lap phieu kham va cap stt`, `fix(uc005): sua truong hop thu phi trung`, `docs(rules): ...` |
| Issue | 1 chức năng = 1 Issue, prefix `LT-`/`QT-`/`BS-`/`PT-` + số, gán đúng chủ nhiệm — prefix theo luồng chức năng (LT=lễ tân, QT=quản trị, BS=bác sĩ, PT=phụ tá) |
| PR | 1 chức năng = 1 PR; tiêu đề `[LT-02] Thu phí khám`; mô tả: làm gì, cách test, ảnh chụp |
| Review xoay vòng | Thanh → Duy → Thắng → Huy → Thanh; PR đụng file dùng chung thêm chủ file review |
| Merge | merge commit (giữ lịch sử + tác giả); xóa nhánh sau merge |
| Cấm | force push; commit file người khác; commit `db.properties` thật, `bin/`, `.class`, jar, dữ liệu thật; nhờ đứng tên commit hộ |
| Git config | email = email GitHub thật để ghi nhận contribution |

**Quyền sở hữu file (quy tắc "một lớp một chủ"):** mỗi file có đúng một chủ (bảng trong từng README nhiệm vụ); người khác được **dùng lại** nhưng không được sửa; cần thêm chữ ký hàm → PR đề xuất cho chủ file. Entity dùng chéo (ví dụ `PhieuKham` do Huy chủ, Thắng/Thanh dùng) chỉ đọc/ghi qua DAO của chủ.

---

## 11. Kiểm thử tối thiểu & Definition of Done

Mỗi chức năng hoàn thành phải đạt **toàn bộ**:

- [ ] GUI gọi BUS, BUS gọi DAO, dữ liệu lưu DB và đọc lại đúng (không có "lưu xong không thấy").
- [ ] Đủ 4 loại tình huống: luồng đúng; dữ liệu sai (báo VLxx); thiếu dữ liệu (bỏ trống); sai quyền (đăng nhập vai trò khác → TB08).
- [ ] Nhiều bảng trong 1 thao tác cùng commit/rollback — thử ngắt giữa chừng không rớt dữ liệu một phần (báo cáo yêu cầu "dữ liệu lưu không được một phần").
- [ ] Nhấn lại nút Lưu/Thu 2 lần không tạo bản ghi trùng (mã/STT qua `BoDem` + ràng buộc UNIQUE).
- [ ] Trạng thái phiếu khám chỉ đi đúng chiều `CHO_KHAM → DANG_KHAM → HOAN_TAT` và chỉ người được giao chuyển.
- [ ] Test case ghi trong `TaiLieu/docs/evidence/<tên>/test-cases.md`: mã, đầu vào, bước, mong đợi, thực tế, đạt/chưa.
- [ ] Hiển thị đúng format: ngày `dd/MM/yyyy`, tiền `#,##0 ₫`, tiếng Việt không lỗi font (UTF-8 + Segoe UI).
- [ ] PR được review theo vòng, merge sạch, tự giải thích được từng dòng khi bảo vệ.

## 12. CẤM LÀM (bài học rút từ source mẫu N11 — không lặp lại)

1. ❌ Hardcode `sa/password` trong code → dùng `db.properties` (không commit).
2. ❌ Hai bộ enum trùng nhau (N11 có `dao.Enum_*` và `entity.*`) → chỉ một bộ trong `model/`.
3. ❌ Lọc tìm kiếm bằng Java khi SQL `WHERE` làm được với lượng lớn — tra cứu đơn giản lọc client-side được (theo mẫu N11), báo cáo/thống kê bắt buộc SQL `GROUP BY`.
4. ❌ `SELECT *` rồi bỏ giá trị — liệt kê đúng cột.
5. ❌ Sai chính tả tên class/controller (`Cotroller`) — PR bị reject.
6. ❌ Mở connection mới trong DAO khi đang trong transaction — dùng overload nhận `Connection`.
7. ❌ GUI gọi thẳng DAO (bỏ qua BUS) — phá kiến trúc 3 lớp, mất kiểm tra quyền.
8. ❌ Tự đổi version JDK/library trên máy riêng rồi báo "máy tôi chạy được".
9. ❌ Nhặt dữ liệu enum từ DB dạng int rồi tự map — DB lưu chuỗi enum đúng giá trị mục 4.4.
10. ❌ Thêm cột/bảng vào SQL chung mà không báo chủ bảng + không tạo file migration mới.

---
*File này là hợp đồng kỹ thuật của nhóm. Khi báo cáo/Word thay đổi → cập nhật ngay file này ở cùng mốc, không để code và tài liệu lệch nhau.*
