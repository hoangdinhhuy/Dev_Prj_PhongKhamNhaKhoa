# NHIỆM VỤ CODE — HOÀNG ĐÌNH HUY (22669751) — LUỒNG LỄ TÂN

> **Tóm tắt:** Huy sở hữu toàn bộ luồng tiếp nhận bên lễ tân: quản lý hồ sơ bệnh nhân (UC002) → thu phí khám (UC005) → lập phiếu khám & cấp STT (UC003) → tra cứu hồ sơ (UC006). Làm đủ **GUI → BUS → DAO → SQL → kiểm thử → minh chứng** cho từng chức năng.
> **Bắt buộc đọc trước:** [README_RULES.md](../../README_RULES.md) — mọi quy tắc đặt tên, kiểu dữ liệu, thông báo chuẩn, transaction, Git trong file này là ràng buộc. Vi phạm quy tắc đặt tên/kiểu dữ liệu → PR bị reject ngay không cần fix nhỏ.

---

## 1. Phạm vi công việc

| Mã | Use case | Màn hình | Điểm nội bộ | Thứ tự làm |
| --- | --- | --- | --- | --- |
| LT-01 | UC002 — Quản lý hồ sơ bệnh nhân | `GD_QLBenhNhan` | 5 | 1 (làm đầu, mọi người phụ thuộc) |
| LT-02 | UC005 — Thu tiền phí khám | `GD_ThuPhi` | 6 | 2 |
| LT-03 | UC003 — Tiếp nhận & lập phiếu khám, cấp STT | `GD_LapPhieuKham` | 6 | 3 |
| LT-04 | UC006 — Tra cứu hồ sơ bệnh nhân | `GD_TraCuuBenhNhan` | 3 | 4 |
| LT-05 | Kiểm thử, tài liệu, demo cho cả 4 chức năng | — | 5 | song song |

Tổng 25 — ước lượng khối lượng, không phải điểm môn học. Backlog có chủ nhiệm (không bắt buộc bản lõi, chốt ở M0): **LT-06 thanh toán dịch vụ cuối lượt** (cần mô hình liên kết hóa đơn–lượt khám; phối hợp Thắng cung cấp dịch vụ đã thực hiện).

## 2. File sở hữu (quy tắc một-chủ — người khác dùng lại, không sửa)

| Loại | File | Ghi chú |
| --- | --- | --- |
| Model | `model/BenhNhan.java`, `model/PhieuKham.java`, `model/HoaDon.java`, `model/ChiTietHoaDon.java`, `model/TrangThaiPhieuKham.java` | Huy là chủ file nhóm này |
| DAO | `dao/BenhNhan_DAO.java`, `dao/PhieuKham_DAO.java`, `dao/HoaDon_DAO.java`, `dao/ChiTietHoaDon_DAO.java` | Thắng, Thanh gọi qua đây — không viết DAO riêng cho phiếu khám |
| BUS | `bus/BenhNhan_BUS.java`, `bus/PhieuKham_BUS.java`, `bus/HoaDon_BUS.java` | |
| GUI | `gui/GD_QLBenhNhan_Controller.java` + `fxml/GD_QLBenhNhan.fxml`, `gui/GD_ThuPhi_Controller.java` + `fxml/GD_ThuPhi.fxml`, `gui/GD_LapPhieuKham_Controller.java` + `fxml/GD_LapPhieuKham.fxml`, `gui/GD_TraCuuBenhNhan_Controller.java` + `fxml/GD_TraCuuBenhNhan.fxml` | |
| SQL | `SourceCode/DATA/schema/005_benhnhan.sql`, `007_phieukham.sql`, `008_hoadon_chitiethoadon.sql` + seed tương ứng trong `SourceCode/DATA/seed/` | 007 phải khai báo `UNIQUE(soThuTu, ngayKham, maPhong)` |
| Minh chứng | `TaiLieu/docs/evidence/huy/` — `design.md`, `implementation.md`, `test-cases.md`, `demo.md`, ảnh/video | |

**File dùng chung của người khác mà Huy cần:** `BoDem_DAO` (Thanh — cấp mã/STT), `NhanVien_BUS` (Duy — danh sách bác sĩ), `PhongKham_BUS` (Thanh — phòng đang nhận bệnh), `util/*` (Duy). Thiếu hàm → mở Issue nhờ chủ file, không tự sửa.

## 3. Chi tiết chức năng

### 3.1. LT-01 — UC002: Quản lý hồ sơ bệnh nhân (`GD_QLBenhNhan`)

**Mục tiêu (đặc tả):** tra cứu, khởi tạo hồ sơ mới hoặc cập nhật lý lịch hành chính, tiền sử dị ứng/bệnh lý; kiểm tra trùng CCCD/SĐT trước khi lưu; sinh mã bệnh nhân tự động.

**GUI — `GD_QLBenhNhan.fxml` + `GD_QLBenhNhan_Controller`:**

| Control | @FXML tên | Ghi chú |
| --- | --- | --- |
| TableView danh sách BN | `table_benhNhan` | cột `cl_stt` (tự sinh), `cl_ma`, `cl_hoTen`, `cl_sdt`, `cl_ngaySinh` (`FormatUtil` dd/MM/yyyy) |
| TextField | `txt_hoTen`, `txt_sdt`, `txt_cccd`, `txt_diaChi`, `txt_tienSuDiUng` | trim trước khi validate |
| DatePicker | `dp_ngaySinh` | |
| Button | `btn_them`, `btn_sua`, `btn_lamMoi` | Sửa lấy từ selection của bảng |

Trình tự nút: `kiemTraDuLieu()` (VL01, VL02, VL03, VL04) → gọi BUS → thành công: Alert + `loadTableData()` + `clearFields()`; thất bại: Alert đúng message nhận về.

**BUS — `BenhNhan_BUS`:**

```java
public BenhNhan taoHoSo(SessionContext session, String hoTen, LocalDate ngaySinh,
                        String sdt, String cccd, String diaChi, String tienSuDiUng)
public BenhNhan capNhatHoSo(SessionContext session, BenhNhan benhNhan)
```

Quy tắc nghiệp vụ: (1) `QuyenUtil.kiemTraQuyen(session, ChucVu.LE_TAN)`; (2) validate theo bảng RULES; (3) trùng CCCD (nếu nhập) → ném **TB13**; (4) trùng SĐT → ném **TB20**, không cho tạo/cập nhật; (5) mã `BNyyMMddNNNN` cấp qua `BoDem_DAO` trong transaction.

**DAO — `BenhNhan_DAO`:** `themBenhNhan`, `suaBenhNhan`, `getAllBenhNhan`, `layBenhNhanTheoMa`, `layBenhNhanTheoSDT`, `layBenhNhanTheoCCCD`, `demBenhNhanHomNay`. Liệt kê đúng cột, không `SELECT *`.

**SQL:** `005_benhnhan.sql` — bảng `BenhNhan(maBenhNhan PK, hoTen, ngaySinh, soDienThoai, cccd UNIQUE NULL, diaChi, tienSuDiUng)`; CHECK `soDienThoai` 10 số; seed ≥ 10 bệnh nhân.

**Test case bắt buộc (ghi `TaiLieu/docs/evidence/huy/test-cases.md`):**

| Mã | Tình huống | Kết quả mong đợi |
| --- | --- | --- |
| TC-LT01-01 | Tạo hồ sơ đủ dữ liệu hợp lệ | Lưu thành công, mã `BNyyMMdd0001`, đọc lại đúng |
| TC-LT01-02 | Bỏ trống họ tên | Alert VL01, không lưu |
| TC-LT01-03 | SĐT 9 số | Alert VL02 |
| TC-LT01-04 | CCCD 11 số | Alert VL03 |
| TC-LT01-05 | CCCD trùng BN khác | Alert TB13 |
| TC-LT01-06 | SĐT trùng | Alert TB20, không cho tạo hồ sơ |
| TC-LT01-07 | Sửa hồ sơ từ dòng chọn | Cập nhật đúng dòng, bảng refresh |
| TC-LT01-08 | Ngắt mạng DB giữa lúc lưu | TB09, DB không có bản ghi một phần |

### 3.2. LT-02 — UC005: Thu tiền phí khám (`GD_ThuPhi`)

**Mục tiêu (đặc tả):** thu phí khám ban đầu **trước khi lập phiếu**; hiển thị mức phí; chống thu trùng ("Bệnh nhân đã hoàn tất phí khám").

**Chốt nghiệp vụ (thống nhất M0 — áp dụng cho bản lõi):** phí khám ban đầu là 1 dòng dịch vụ đặc biệt seed sẵn: `DichVu` mã `DV0001`, tên "Phí khám ban đầu" (giá do chủ phòng khám chỉnh trên UC007). Thu phí = tạo `HoaDon` (`daThanhToan = 1`) + `ChiTietHoaDon(DV0001)`. Hóa đơn phí này chưa gắn phiếu → khi lập phiếu (LT-03) gắn vào `PhieuKham.maHoaDonPhiKham` (cột UNIQUE do Huy thêm vào 007 — đã được nhóm chốt).

**GUI — `GD_ThuPhi.fxml`:** `txt_timBN` (mã/SĐT) + `btn_tim`; panel thông tin BN; `lb_mucPhi` (đọc `DichVu` DV0001 qua `DichVu_BUS` của Duy — hàm `layDichVuTheoMa`, hiển thị `#,##0 ₫`); `cbb_hinhThuc` (`TIEN_MAT`/`CHUYEN_KHOAN`); `txt_tienKhachDua`; `lb_tienThua` tự tính; `btn_thuPhi`. Validate: chọn BN (VL05), tiền khách đưa ≥ mức phí.

**BUS — `HoaDon_BUS`:**

```java
public HoaDon thuPhiKham(SessionContext session, String maBenhNhan,
                         String hinhThucThanhToan, BigDecimal tienKhachDua)
public List<HoaDon> layHoaDonPhiChuaDung(SessionContext session, String maBenhNhan) // hỗ trợ "dùng hóa đơn đã thu"
```

Quy tắc: quyền LE_TAN; nếu tồn tại hóa đơn phí khám đã thanh toán **chưa gắn phiếu nào** của BN → ném **TB04** (GUI đồng thời hiện nút "Dùng hóa đơn đã thu" để tiếp tục, không thu lại); `ngayThu = LocalDateTime.now()`; mã `HDyyMMddNNNN` qua `BoDem_DAO`; snapshot `donGia` vào `ChiTietHoaDon` tại thời điểm thu (giá đổi sau không ảnh hưởng hóa đơn cũ).

**DAO — `HoaDon_DAO` + `ChiTietHoaDon_DAO`:** `themHoaDon` (overload nhận `Connection` cho transaction), `layHoaDonTheoMa`, `layHoaDonPhiChuaDungTheoBN`, `themChiTiet`, `layChiTietTheoHoaDon`.

**SQL:** `008_hoadon_chitiethoadon.sql` — `HoaDon(maHoaDon PK, ngayThu datetime2(0), hinhThucThanhToan CHECK IN (TIEN_MAT, CHUYEN_KHOAN), daThanhToan bit, tienKhachDua decimal(18,2), FK BN/NV)`, `ChiTietHoaDon(PK ghép maHoaDon+maDichVu, soLuong CHECK >0, donGia, thanhTien PERSISTED = soLuong*donGia)`; seed `DV0001`.

**Test case bắt buộc:** thu thành công (TC-LT02-01); thu trùng → TB04 (02); tiền khách đưa thiếu → VL06 (03); tiền thừa đúng (04); hóa đơn + chi tiết cùng commit, ngắt giữa chừng rollback hết (05); hóa đơn đọc lại đúng format tiền (06); dùng hóa đơn đã thu để lập phiếu không phát sinh thêm (07).

### 3.3. LT-03 — UC003: Lập phiếu khám & cấp STT (`GD_LapPhieuKham`)

**Mục tiêu (đặc tả):** chọn bệnh nhân đã thu phí, chọn Bác sĩ/Phòng, nhập lý do khám; kiểm tra trạng thái ca làm việc; tạo mã phiếu + STT tự động, đưa vào hàng chờ; trạng thái `CHO_KHAM`.

**GUI — `GD_LapPhieuKham.fxml`:** `txt_timBN`/`btn_tim` (chỉ BN đã có hóa đơn phí chưa dùng); panel thông tin BN + hóa đơn phí; `cbb_bacSi` ← `NhanVien_BUS.danhSachBacSiDangLamViec(session)` (Duy); `cbb_phong` ← `PhongKham_BUS.danhSachPhongDangNhanBenh(session)` (Thanh); `txt_lyDoKham`; `lb_soThuTuDuKien` (preview); `btn_lapPhieu`, `btn_in` (mở modal xem phiếu kiểu Bill N11).

**BUS — `PhieuKham_BUS`:**

```java
public PhieuKham lapPhieuKham(SessionContext session, String maBenhNhan,
                              String maBacSi, String maPhong, String lyDoKham,
                              String maHoaDonPhiKham)
```

Quy tắc (đúng thứ tự): quyền LE_TAN → VL05/VL07/VL08 → kiểm "ca làm việc" bản lõi: bác sĩ có `TaiKhoan.trangThai = HOAT_DONG` **và** phòng `dangNhanBenh = 1`, vi phạm → **TB03** → transaction (1 commit/rollback): `BoDem_DAO.layMaMoi(con,"PK",ngay)` + `laySoThuTuTiepTheo(con, ngay, maPhong)` + `PhieuKham_DAO.themPhieuKham(con, ...)` với `trangThai = CHO_KHAM`, gắn `maHoaDonPhiKham` (UNIQUE — hóa đơn đã gắn sẽ bị DB chặn thu lại).

**DAO — `PhieuKham_DAO`:** `themPhieuKham(con, phieuKham)`, `layPhieuKhamTheoMa`, `layDanhSachTheoNgayVaPhong`, `layDanhSachTheoBacSi`, `capNhatTrangThai(maPhieuKham, trangThaiMoi, trangThaiCu)` (UPDATE có điều kiện + trả số dòng ảnh hưởng — Thanh/Thắng dùng), `layTheoBN`. Cập nhật **chỉ đúng cột**, cấm UPDATE cả object cũ.

**SQL:** `007_phieukham.sql` — `PhieuKham(maPhieuKham PK, ngayKham date, soThuTu int CHECK>0, lyDoKham nvarchar, chanDoan NULL, ketQuaDieuTri NULL, ghiChuThuocSuDung NULL, ghiChuThuocTuMua NULL, trangThai nvarchar(40) DEFAULT 'CHO_KHAM', FK BN/NV(bác sĩ)/PhongKham/HoSoBenhAn NULL, maHoaDonPhiKham UNIQUE FK HoaDon)` + `UNIQUE(ngayKham, maPhong, soThuTu)`.

**Test case bắt buộc:** lập phiếu đủ điều kiện → STT = 1 (TC-LT03-01); phiếu thứ 2 cùng phòng/ngày STT = 2 (02); STT khác phòng độc lập (03); bác sĩ không nhận bệnh → TB03 (04); phòng tắt nhận bệnh → TB03 (05); gắn hóa đơn phí đã dùng → DB chặn UNIQUE → TB09/TB10 (06); ngắt giữa transaction → không có mã phiếu chết, bộ đếm không nhảy (07); nhấn Lập phiếu 2 lần nhanh → 2 phiếu khác mã khác STT, không trùng (08); in xem phiếu đúng thông tin (09).

### 3.4. LT-04 — UC006: Tra cứu hồ sơ bệnh nhân (`GD_TraCuuBenhNhan`)

**Mục tiêu (đặc tả):** tìm theo mã BN hoặc SĐT (mở rộng: họ tên), hiển thị hồ sơ; không thấy → "Không tìm thấy hồ sơ bệnh nhân".

**GUI — `GD_TraCuuBenhNhan.fxml`:** `cbb_tieuChi` (Mã/SĐT/Họ tên) + `txt_tuKhoa` + `btn_tim`; `table_ketQua`; double-click dòng → modal chi tiết: thông tin hành chính + tiền sử + danh sách phiếu khám của BN (đọc `PhieuKham_DAO.layTheoBN`, chỉ đọc).

**BUS — `BenhNhan_BUS.traCuu(session, tieuChi, tuKhoa)`:** quyền LE_TAN (Quản lý cũng được xem); không thấy → trả `List` rỗng, GUI Alert **TB05**. Mã/SĐT khớp chính xác qua SQL `WHERE`; họ tên lọc client-side theo mẫu N11 (nạp danh sách rồi `contains`).

**Test case bắt buộc:** tìm theo mã đúng (TC-LT04-01); tìm SĐT (02); tên không dấu vẫn ra (03); không thấy → TB05 (04); tra cứu rỗng khóa mở chi tiết (05); hiển thị lịch sử phiếu đúng thứ tự ngày (06).

## 4. Thứ tự làm & phụ thuộc

```
M0 (nhóm): pom/khung App/ConnectDB/BoDem_Dao/util/schema 000-003 ──► LT-01 Hồ sơ ──► LT-02 Thu phí ──► LT-03 Lập phiếu ──► LT-04 Tra cứu
LT-03 chờ Duy có `danhSachBacSiDangLamViec` + Thanh có `danhSachPhongDangNhanBenh` (M1) — trước đó dùng fake ghi rõ FAKE trong controller.
```

## 5. Git workflow cá nhân

- Issue prefix `LT-xx`; nhánh `feat/huy-lt-0x-<ten>`; commit `feat(uc002): tao ho so benh nhan...`.
- **Reviewer của Huy: Thanh.** PR đụng `PhieuKham_DAO`/schema 007 (file dùng chéo) phải mời thêm Thắng + Thanh.
- Cấm sửa file của người khác; cần hàm mới ở `BoDem_DAO`/`NhanVien_BUS`/`PhongKham_BUS` → Issue cho chủ file.

## 6. Mốc cá nhân

| Mốc | Huy phải có |
| --- | --- |
| M0 | Hoàn thiện schema 005/007/008; entity nhóm mình chắc `String`/`LocalDate`/`BigDecimal` đúng RULES |
| M1 | 4 màn hình chạy với FAKE data |
| M2 | 4 chức năng chạy DB local + test-cases.md hoàn chỉnh |
| M3 | Luồng demo chặng lễ tân chạy trơn với Thanh/Thắng/Duy |
| M4 | design.md + demo.md + ảnh/video; tập trả lời câu hỏi bảo vệ |

## 7. Chuẩn bị bảo vệ (thanh điểm CLO6/CLO2/CLO7)

Lần theo được 1 thao tác: **nút "Lập phiếu" → `PhieuKham_BUS.lapPhieuKham` (quyền → validate → TB03 → transaction: mã + STT + INSERT) → `PhieuKham_DAO` → bảng `PhieuKham`**. Sẵn sàng giải thích: vì sao mã/SĐT là `String`; vì sao tiền là `BigDecimal`; vì sao STT UNIQUE theo (ngày, phòng) và cấp qua `BoDem` thay vì `MAX+1`; vì sao UPDATE trạng thái có điều kiện; một test case khó nhất (TC-LT03-08 nhấn 2 lần).
