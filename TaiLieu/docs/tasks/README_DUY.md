# NHIỆM VỤ CODE — NGUYỄN TUẤN DUY (24726131) — LUỒNG QUẢN LÝ + KHUNG ỨNG DỤNG

> **Tóm tắt:** Duy sở hữu khung ứng dụng dùng chung (build, App, ConnectDB *chốt hợp đồng với Thanh*, util, session, điều hướng) và luồng quản trị: đăng nhập/phân quyền (UC001) → quản lý nhân viên & tài khoản (UC010) → quản lý danh mục dịch vụ (UC007) → báo cáo lượt khám + xuất CSV (UC008).
> **Bắt buộc đọc trước:** [README_RULES.md](../../README_RULES.md). Duy là người giữ `pom.xml` và các file dùng chung — chính Duy phải gương mẫu theo RULES trước.

---

## 1. Phạm vi công việc

| Mã | Use case / đầu việc | Màn hình | Điểm nội bộ | Thứ tự làm |
| --- | --- | --- | --- | --- |
| QT-01 | **Khung app M0**: `pom.xml`, `App`, hợp đồng `ConnectDB`, `db.properties.example`, `util/*`, `GD_Chinh`, CSS nền | — | 3 | 1 (cả nhóm chờ) |
| QT-02 | UC001 — Đăng nhập & phân quyền | `GD_DangNhap` | 4 | 2 |
| QT-03 | UC010 — Quản lý nhân viên & tài khoản | `GD_QLNhanVien` | 4 | 3 |
| QT-04 | UC007 — Quản lý danh mục dịch vụ | `GD_QLDichVu` | 4 | 3 (song song QT-03) |
| QT-05 | UC008 — Báo cáo lượt khám + xuất CSV | `GD_BaoCao` | 6 | 4 |
| QT-06 | Kiểm thử, tài liệu, demo | — | 4 | song song |

Tổng 25. Backlog có chủ nhiệm (chốt M0): **QT-07 hàng hóa/tồn kho**, **QT-08 lịch ca/quên mật khẩu/trạng thái nghỉ việc**, **QT-09 doanh thu** (UC008 hiện loại doanh thu — phải cập nhật phạm vi trước khi làm).

## 2. File sở hữu (quy tắc một-chủ)

| Loại | File | Ghi chú |
| --- | --- | --- |
| Build | `pom.xml` — **Duy là người duy nhất merge thay đổi** | JDK 21, `javafx-controls/fxml 21`, `javafx-maven-plugin`, `mssql-jdbc:12.8.1.jre11` |
| Khung | `App.java` (entry + `setRoot`/`openModal` + `App.session`), `model/SessionContext.java`, `util/FormatUtil.java`, `util/ValidateUtil.java`, `util/QuyenUtil.java`, `util/AlertUtil.java`, `util/MaThongBao.java` | cả nhóm dùng — đổi chữ ký phải qua PR |
| GUI khung | `gui/GD_Chinh_Controller.java` + `fxml/GD_Chinh.fxml`, `css/` nền | menu ẩn/hiện theo vai trò |
| Model | `model/NhanVien.java`, `model/TaiKhoan.java`, `model/DichVu.java`, `model/ChucVu.java`, `model/TrangThaiTaiKhoan.java`, `model/TrangThaiDichVu.java` | |
| DAO | `dao/TaiKhoan_DAO.java`, `dao/NhanVien_DAO.java`, `dao/DichVu_DAO.java`, `dao/BaoCao_DAO.java` | |
| BUS | `bus/TaiKhoan_BUS.java`, `bus/NhanVien_BUS.java`, `bus/DichVu_BUS.java`, `bus/BaoCao_BUS.java` | `NhanVien_BUS`/`DichVu_BUS` là phụ thuộc của Huy/Thắng — chốt chữ ký sớm |
| GUI | cặp FXML+Controller: `GD_DangNhap`, `GD_QLNhanVien`, `GD_QLDichVu`, `GD_BaoCao` | |
| SQL | `SourceCode/DATA/schema/002_nhanvien_taikhoan.sql`, `003_dichvu.sql` + seed | |
| Minh chứng | `TaiLieu/docs/evidence/duy/` | |

**Hợp đồng với Thanh (M0):** Duy chốt yêu cầu của `ConnectDB` (constructor đọc `db.properties`, `connect()`, `getConnection()` tự nối lại) — Thanh viết code; Duy review API. Cấm tạo lớp kết nối thứ hai.

## 3. Chi tiết chức năng

### 3.0. QT-01 — Khung ứng dụng (làm đầu tiên, M0)

- `pom.xml`: `<maven.compiler.release>21</maven.compiler.release>`, dependency `org.openjfx:javafx-controls:21` + `javafx-fxml:21`, `com.microsoft.sqlserver:mssql-jdbc:12.8.1.jre11`, plugin `org.openjfx:javafx-maven-plugin` với `<mainClass>main.App</mainClass>` → chạy được bằng `mvn javafx:run` trên mọi máy.
- `App extends Application`: `App.setRoot(String fxmlTen)`, `App.openModal(String fxmlTen)` / `openModal(String, w, h)` (theo mẫu N11), `App.session` static; `start()` kiểm tra `ConnectDB.getInstance().connect()` trước khi mở `GD_DangNhap`.
- `SessionContext`: `String maNhanVien`, `String hoTen`, `ChucVu chucVu` — tạo **chỉ** trong `TaiKhoan_BUS.dangNhap`, cấm dựng từ form.
- `util/*` đúng theo RULES mục 4/7/9: `MaThongBao` chứa toàn bộ hằng TB/VL; `FormatUtil` (ngày `dd/MM/yyyy`, tiền `#,##0 ₫`); `ValidateUtil` (regex SĐT `^0\d{9}$`, CCCD 12 số, không rỗng, số dương); `QuyenUtil.kiemTraQuyen(session, ChucVu...)` ném TB08; `AlertUtil` (ERROR/INFO/CONFIRMATION khuôn chung).
- `GD_Chinh`: menu/điều hướng theo `session.getChucVu()` — Lễ tân: UC002/003/005/006; Bác sĩ: UC004; Phụ tá: UC009; Quản lý: UC007/008/010. Header hiển thị "Xin chào, [họ tên] ([vai trò])" + nút Đăng xuất (về `GD_DangNhap`, dọn `App.session`).
- Chạy thử thật `mvn javafx:run` + test kết nối DB bằng main() trước khi chuyển sang QT-02 (quy trình bắt buộc của môn học).

### 3.1. QT-02 — UC001: Đăng nhập (`GD_DangNhap`)

**Mục tiêu (đặc tả):** xác thực theo tài khoản; kiểm tra trạng thái (HOAT_DONG/BI_KHOA); thành công → điều hướng theo vai trò.

**GUI:** `txt_taiKhoan`, `txt_matKhau` (PasswordField), `btn_dangNhap`. Rỗng → VL13. Sai → **TB01**. Bị khóa → **TB02** và dừng.

**BUS — `TaiKhoan_BUS`:**

```java
public SessionContext dangNhap(String tenDangNhap, String matKhau)
```

Quy tắc: `trim()` cả hai; đối chiếu DB (`TaiKhoan_DAO.getTaiKhoanTheoTenDangNhap`); so mật khẩu qua **hash** (BCrypt/Argon2id theo đề xuất báo cáo — chốt thư viện tại M0, không lưu/in mật khẩu gốc); `BI_KHOA` → TB02; tạo `SessionContext` (lấy `ChucVu` từ `NhanVien`, **không** lấy từ input); ghi `App.session`. Phân quyền ở tầng BUS + ẩn menu GUI — cả hai lớp (đúng ghi chú báo cáo).

**DAO — `TaiKhoan_DAO`:** `getTaiKhoanTheoTenDangNhap`, `capNhatLanDangNhap`, `themTaiKhoan`, `capNhatTaiKhoan`, `khoaMoTaiKhoan`.

**SQL:** `002_nhanvien_taikhoan.sql` — `NhanVien(maNhanVien PK NVyyMMddNNNN, hoTen, soDienThoai, CCCD, chucVu nvarchar CHECK IN (LE_TAN,BAC_SI,PHU_TA,QUAN_LY), ...)`; `TaiKhoan(tenDangNhap PK, matKhauHash, trangThai CHECK IN (HOAT_DONG,BI_KHOA), maNhanVien FK UNIQUE)`; seed 4 tài khoản chuẩn: `NV0001/123456`, `bacsi/123456`, `NV0005/123456`, `NV0007/123456` (ghi rõ trong seed + demo.md).

**Test case:** đăng nhập đúng từng vai trò → đúng menu (TC-QT02-01..04); sai mật khẩu → TB01 (05); tài khoản khóa → TB02 và không vào được (06); rỗng → VL13 (07); mật khẩu trong DB là hash không phải gốc (08); Đăng xuất → session dọn sạch, Back không vào lại màn cũ (09).

### 3.2. QT-03 — UC010: Quản lý nhân viên & tài khoản (`GD_QLNhanVien`)

**Mục tiêu:** đặc tả chi tiết chưa có trong báo cáo (chỉ nêu trong bảng tác nhân) — nhóm chốt bản lõi: CRUD `NhanVien` + tạo/khóa tài khoản gắn nhân viên; không có lịch ca, không có quên mật khẩu (backlog QT-08).

**GUI:** `table_nhanVien` (mã, họ tên, SĐT, CCCD, chức vụ); `cbb_chucVu` (4 enum); các `txt_` tương tự màn bệnh nhân; `btn_them/sua/xoa/lamMoi`; phần tài khoản: `txt_tenDangNhap`, `cbb_trangThai`, `btn_taoTaiKhoan`, `btn_khoaMo`.

**BUS — `NhanVien_BUS`:** `danhSachBacSiDangLamViec(session)` (dùng cho Huy), `danhSachNhanVien(session)`, `themNhanVien(session, ...)` (quyền QUAN_LY; validate VL01/VL02/VL03; mã `NVyyMMddNNNN` qua `BoDem_DAO`), `suaNhanVien`, `xoaNhanVien` (đang có tài khoản/đã lập phiếu → **TB11**, chỉ ngừng làm việc), `taoTaiKhoan(session, maNhanVien, tenDangNhap, matKhau)` (trùng tên đăng nhập → **TB14**; mật khẩu hash), `khoaMoTaiKhoan`.

**DAO:** `NhanVien_DAO` (them/sua/xoa/getAll/layTheoMa/layDanhSachBacSiHoatDong), `TaiKhoan_DAO` (them/capNhat/layTheoMaNV/kiemTraTenDangNhapTonTai).

**Test case:** thêm NV 4 chức vụ (TC-QT03-01); SĐT/CCCD sai → VL02/VL03 (02,03); tạo TK trùng → TB14 (04); khóa TK bác sĩ → Huy lập phiếu cho bác sĩ đó báo TB03 (05); xóa NV đang gắn TK → TB11 (06).

### 3.3. QT-04 — UC007: Quản lý danh mục dịch vụ (`GD_QLDichVu`)

**Mục tiêu (đặc tả):** thêm/điều chỉnh tên và mức giá niêm yết; kiểm trùng tên; giá > 0.

**GUI:** `table_dichVu` (mã, tên, đơn giá `#,##0 ₫`, trạng thái); `txt_tenDichVu`, `txt_donGia`, `cbb_trangThai` (`DANG_PHUC_VU`/`NGUNG_PHUC_VU`); `btn_them/sua/lamMoi`. Không có nút Xóa cứng — dịch vụ hết dùng chỉ chuyển `NGUNG_PHUC_VU`.

**BUS — `DichVu_BUS`:** `danhSachDichVuDangPhucVu(session)` (dùng cho Thắng), `layDichVuTheoMa` (dùng cho Huy — `DV0001` phí khám), `themDichVu` (trùng tên → **TB12**; VL06), `suaDichVu` (đổi giá chỉ ảnh hưởng sau này — chi tiết hóa đơn giữ snapshot), `doiTrangThai`. Quyền QUAN_LY.

**SQL:** `003_dichvu.sql` — `DichVu(maDichVu PK DVNNNN, tenDichVu UNIQUE, donGiaNiemYet decimal(18,2) CHECK >=0, trangThai)`; seed ≥ 8 dịch vụ nha khoa + `DV0001 Phí khám ban đầu` + 2 dịch vụ `NGUNG_PHUC_VU` để test.

**Test case:** thêm dịch vụ (TC-QT04-01); trùng tên → TB12 (02); giá 0/âm/chữ → VL06 (03); ngừng phục vụ → Thắng không chỉ định được TB15 (04); đổi giá không sửa hóa đơn cũ (05).

### 3.4. QT-05 — UC008: Báo cáo lượt khám + xuất CSV (`GD_BaoCao`)

**Mục tiêu (đặc tả):** chọn Từ ngày–Đến ngày; tổng hợp số lượt khám và tần suất dịch vụ (**loại doanh thu**); hiển thị biểu đồ + bảng; màn hình có nút xuất CSV.

**GUI:** `dp_tuNgay`, `dp_denNgay` + `btn_xem` (VL10 khi ngược); `BarChart<String,Number>` lượt khám theo ngày; `BarChart` tần suất dịch vụ (hoặc 1 chart + 1 bảng); `table_luotKham`, `table_tanSuatDichVu`; `btn_xuatCsv`. Query nặng → chạy trong `javafx.concurrent.Task`, hiện "Đang tổng hợp...".

**BUS — `BaoCao_BUS`:** `thongKeLuotKham(session, tuNgay, denNgay)`, `thongKeTanSuatDichVu(session, tuNgay, denNgay)` — quyền QUAN_LY; validate VL10; **phải dùng SQL `GROUP BY`** (cấm nạp toàn bộ phiếu về lọc Java — RULES mục 12.3).

**DAO — `BaoCao_DAO`:** đếm phiếu nhóm theo `ngayKham` (chỉ `HOAN_TAT` + `DANG_KHAM`? — chốt: đếm phiếu đã lập theo ngày, có bộ lọc trạng thái rõ trong query và ghi chú trong design.md); đếm `ChiDinhDichVu` nhóm theo dịch vụ. SQL đặt thêm bản tham khảo tại `DATA/queries/baocao_luotkham.sql`.

**Xuất CSV (chuẩn format chốt — không ai làm khác):** mã hóa **UTF-8 có BOM**; phân cách `;`; ngày `dd/MM/yyyy`; tiền/số không dấu phẩy hàng nghìn; tên file `baocao_luotkham_yyyyMMdd_HHmm.csv`; mở bằng Excel VN hiển thị đúng tiếng Việt.

**Test case:** khoảng hợp lệ → số liệu khớp đếm tay từ seed (TC-QT05-01); từ > đến → VL10 (02); không có dữ liệu → bảng rỗng + thông báo, không lỗi (03); CSV mở Excel đúng font + cột (04); chạy vai trò Lễ tân → TB08 (05).

## 4. Thứ tự làm & phụ thuộc

```
QT-01 khung ──► QT-02 Đăng nhập ──► QT-03/QT-04 (song song) ──► QT-05 Báo cáo
Cả nhóm chờ QT-01 + chữ ký `NhanVien_BUS.danhSachBacSiDangLamViec` và `DichVu_BUS.danhSachDichVuDangPhucVu/layDichVuTheoMa` — chốt chữ ký ngay M0 kể cả chưa hiện thực.
```

## 5. Git workflow cá nhân

- Issue prefix `QT-xx`; nhánh `feat/duy-qt-0x-<ten>`; commit `feat(uc001): dang nhap va phan quyen...`.
- **Reviewer của Duy: Thanh.** PR đụng `pom.xml`, `App`, `util/*`, `SessionContext` = file toàn nhóm → **cần 2 người review** (Thanh + Huy) trước merge.

## 6. Mốc cá nhân

| Mốc | Duy phải có |
| --- | --- |
| M0 | `pom.xml` + khung chạy được trên 4 máy; ConnectDB API chốt với Thanh; chữ ký BUS chia sẻ; schema 002/003 + seed |
| M1 | 5 màn hình (Đăng nhập/Chính/NV/DV/Báo cáo) chạy FAKE |
| M2 | Đủ 4 chức năng với DB local + hash mật khẩu + CSV đúng chuẩn |
| M3 | Đăng nhập phân quyền thật phục vụ demo cả nhóm |
| M4 | design.md/test-cases.md/demo.md + slide phần mình |

## 7. Chuẩn bị bảo vệ

Lần theo: **nút Đăng nhập → `TaiKhoan_BUS.dangNhap` (trim → TB01/TB02 → hash so khớp → tạo `SessionContext` từ DB, không từ form) → `App.session` → `GD_Chinh` ẩn menu theo vai trò; BUS vẫn kiểm tra quyền lần nữa (`QuyenUtil`) khi gọi hàm**. Sẵn sàng giải thích: vì sao phân quyền phải chặn ở BUS; vì sao mật khẩu hash; vì sao `pom.xml` khóa version; cách xuất CSV UTF-8 BOM để Excel không lỗi font; một test case khó nhất (TC-QT05-04 font CSV).
