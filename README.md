# HỆ THỐNG QUẢN LÝ PHÒNG KHÁM NHA KHOA SÀI GÒN

Đồ án môn học **Phát triển Ứng dụng (Application Development)** — Nhóm 15
Trường Đại học Công nghiệp TP. Hồ Chí Minh (IUH) — Khoa Công nghệ Thông tin — Học kỳ I, năm học 2026–2027.

> Cấu trúc repo theo chuẩn bộ nộp của Nhóm 11: **SourceCode** (mã nguồn Maven), **TaiLieu** (tài liệu), **FileDongGoi** (bản đóng gói — để trống, thêm sau), **FileTrinhChieu** (slide — để trống, thêm sau), **video** (demo — để trống, thêm sau).

> 📚 **Trang tài liệu nhóm (trong `TaiLieu/`):** [README_RULES.md](TaiLieu/README_RULES.md) (quy tắc code chung) · [README_THANH.md](TaiLieu/docs/tasks/README_THANH.md) · [README_DUY.md](TaiLieu/docs/tasks/README_DUY.md) · [README_THANG.md](TaiLieu/docs/tasks/README_THANG.md) · [README_HUY.md](TaiLieu/docs/tasks/README_HUY.md) (nhiệm vụ từng thành viên) · [README_KHOI_NGHIEP.md](TaiLieu/README_KHOI_NGHIEP.md) (đề xuất khởi nghiệp — Chương 5 báo cáo) · [KICHBAN_THUC_NGHIEM.md](TaiLieu/KICHBAN_THUC_NGHIEM.md) (kịch bản kiểm thử S0–S11, bản .docx cùng thư mục).

---

## 1. Thành viên nhóm

| STT | Họ tên | MSSV | Vai trò nhóm | Luồng phụ trách | Tài liệu nhiệm vụ |
| --- | --- | --- | --- | --- | --- |
| 1 | Phan Chí Thanh | 24738801 | Facilitator | **Phụ tá + hạ tầng CSDL**: hàng chờ/gọi số, xác nhận vào khám + ConnectDB/BoDem/schema dùng chung | [README_THANH.md](TaiLieu/docs/tasks/README_THANH.md) |
| 2 | Nguyễn Tuấn Duy | 24726131 | Reporter | **Quản lý**: đăng nhập/phân quyền, nhân viên, dịch vụ, báo cáo + khung ứng dụng | [README_DUY.md](TaiLieu/docs/tasks/README_DUY.md) |
| 3 | Huỳnh Quốc Thắng | 22693891 | Recorder | **Bác sĩ**: khám & chẩn đoán, chỉ định dịch vụ, cấp thuốc, hoàn tất, lịch sử khám | [README_THANG.md](TaiLieu/docs/tasks/README_THANG.md) |
| 4 | Hoàng Đình Huy | 22669751 | Time Keeper | **Lễ tân**: hồ sơ bệnh nhân, thu phí, lập phiếu khám, tra cứu | [README_HUY.md](TaiLieu/docs/tasks/README_HUY.md) |

## 2. Giới thiệu đề tài

Phòng khám Nha khoa Sài Gòn hiện quản lý bệnh nhân, phiếu khám và thanh toán bằng sổ tay và bảng tính, dẫn đến tra cứu chậm, dữ liệu trùng lặp, cấp số thứ tự dễ trùng và không có báo cáo tổng hợp. Đồ án xây dựng ứng dụng desktop bằng **Java + JavaFX kết nối SQL Server qua JDBC** để số hóa toàn bộ quy trình: tiếp nhận → thu phí khám → lập phiếu & cấp số thứ tự → gọi số vào khám → khám & chỉ định → hoàn tất → thanh toán → thống kê.

**Phạm vi (trích nguyên văn báo cáo):**

> "Đề tài tập trung xây dựng ứng dụng Java phục vụ công tác quản lý nội bộ tại Phòng khám Nha khoa Sài Gòn. Hệ thống hỗ trợ hình thức đăng ký khám trực tiếp, sử dụng hồ sơ điện tử và phiếu khám để quản lý thông tin bệnh nhân, từng lượt khám và số thứ tự theo phòng…"

Việc đặt lịch hẹn qua điện thoại/quét QR và tích hợp AI **không thuộc phạm vi đồ án**, được phát triển riêng trong phần khởi nghiệp: [TaiLieu/README_KHOI_NGHIEP.md](TaiLieu/README_KHOI_NGHIEP.md) (tính năng gợi ý chỉ định bằng AI đã có bản mô phỏng tại chỗ trong app).

### 2.1. Tác nhân và danh sách use case (theo báo cáo Chương 3)

| Mã | Tên use case | Tác nhân | Người hiện thực |
| --- | --- | --- | --- |
| UC001 | Đăng nhập hệ thống | Cả 4 tác nhân | Duy |
| UC002 | Quản lý hồ sơ bệnh nhân | Lễ tân | Huy |
| UC003 | Tiếp nhận bệnh nhân — lập phiếu khám bệnh (kèm in phiếu) | Lễ tân | Huy |
| UC004 | Ghi nhận khám và chẩn đoán | Bác sĩ | Thắng |
| UC005 | Thu tiền phí khám | Lễ tân | Huy |
| UC006 | Tra cứu hồ sơ bệnh nhân | Lễ tân | Huy |
| UC007 | Quản lý danh mục dịch vụ | Quản lý | Duy |
| UC008 | Xem báo cáo lượt khám (kèm xuất CSV, không có doanh thu) | Quản lý | Duy |
| UC009 | Theo dõi hàng chờ (gọi số, xác nhận vào khám) | Phụ tá | Thanh |
| UC010 | Quản lý nhân viên & tài khoản (nêu trong bảng tác nhân, đặc tả do nhóm chốt tối thiểu) | Quản lý | Duy |

Bổ sung ngoài đặc tả UC nhưng có trong sơ đồ luồng Word: **Thanh toán dịch vụ cuối lượt** (Lễ tân) và **Lịch sử khám** (Bác sĩ/Quản lý).

### 2.2. Luồng nghiệp vụ lõi (trạng thái phiếu khám)

```
CHO_KHAM ──(Phụ tá gọi số + xác nhận vào khám)──► DANG_KHAM ──(Bác sĩ hoàn tất)──► HOAN_TAT
```

- Huy tạo phiếu với trạng thái `CHO_KHAM`; **chỉ** Thanh chuyển sang `DANG_KHAM`; **chỉ** Thắng chuyển sang `HOAN_TAT` (đã kiểm tra chẩn đoán/kết quả dịch vụ).
- Thanh toán không làm đổi trạng thái khám. Không chuyển ngược trạng thái.
- Chi tiết chuyển trạng thái và ràng buộc UPDATE: xem [TaiLieu/README_RULES.md](TaiLieu/README_RULES.md).

## 3. Công nghệ chốt (bắt buộc cả nhóm cùng dùng)

| Thành phần | Chốt | Ghi chú |
| --- | --- | --- |
| JDK | **21** | khóa trong `pom.xml` (`maven.compiler.release=21`) |
| Build | **Maven** — một `pom.xml` duy nhất tại `SourceCode/` | build đồng nhất mọi máy |
| UI | **JavaFX 21 + FXML (Gluon Scene Builder)** | khớp báo cáo |
| CSDL | **Microsoft SQL Server** (Express 2019/2022) + **SSMS** | tên DB: `N15_PTUD_DB` |
| Driver JDBC | **mssql-jdbc 12.x.jre11** | khai báo trong `pom.xml` |
| IDE | **VS Code** (Extension Pack for Java) + Scene Builder | |
| Encoding | **UTF-8** | font UI: Segoe UI |

## 4. Kiến trúc ứng dụng (CHỐT — mọi người code theo đúng mô hình này)

Kiến trúc 3 lớp theo sơ đồ nhóm, hiện thực theo chuẩn mẫu của nhóm 11:

```
┌────────────────────────────────────────────────────────┐
│  LỚP 1 — GUI (vn.nhom15.nhakhoa.gui)                    │
│  JavaFX FXML + Controller: nhận dữ liệu, validate nhẹ,  │
│  bắt sự kiện, hiển thị. KHÔNG có SQL.                   │
└───────────────▲────────────────────────────────────────┘
                │ gọi hàm BUS (trả entity/List/boolean, ném lỗi message chuẩn)
┌───────────────┴────────────────────────────────────────┐
│  LỚP 2 — BUS (vn.nhom15.nhakhoa.bus)                    │
│  Kiểm tra dữ liệu nghiệp vụ, phân quyền, điều phối      │
│  transaction nhiều bước. KHÔNG có câu lệnh SQL.         │
└───────────────▲────────────────────────────────────────┘
                │ gọi hàm DAO (PreparedStatement)
┌───────────────┴────────────────────────────────────────┐
│  LỚP 3 — DAO (vn.nhom15.nhakhoa.dao)                    │
│  JDBC — đọc/ghi SQL Server, ánh xạ ResultSet → Model.   │
└───────────────▲────────────────────────────────────────┘
                │ jdbc:sqlserver://localhost:1433;databaseName=N15_PTUD_DB
        ┌───────┴────────┐
        │  SQL SERVER    │   N15_PTUD_DB (SourceCode/DATA/schema/*.sql)
        └────────────────┘
  🔷 Model (model): POJO + enum dùng chung mọi lớp.
  🔷 connect (connect): ConnectDB singleton — nơi DUY NHẤT cấp Connection.
  🔷 util (util): FormatUtil, ValidateUtil, QuyenUtil, AlertUtil, MaThongBao, GoiYAI.
```

**Quy tắc vàng:** GUI chỉ gọi BUS; BUS chỉ gọi DAO; DAO chỉ nói chuyện với SQL Server. Chi tiết từng tầng tại [TaiLieu/README_RULES.md](TaiLieu/README_RULES.md).

### 4.1. Cấu trúc thư mục repo (chuẩn bộ nộp Nhóm 11)

```
PTUD_N15_/
├── SourceCode/                     ← toàn bộ mã nguồn (Maven)
│   ├── pom.xml                     ← JDK 21, JavaFX 21, mssql-jdbc; Duy giữ quyền sửa
│   ├── DATA/
│   │   ├── schema/                 ← 000_hatang.sql … 012_*.sql (mỗi file một chủ)
│   │   ├── seed/                   ← dữ liệu mẫu đi kèm từng schema
│   │   └── README.md               ← hướng dẫn chạy SQL + cấu hình máy
│   └── src/
│       ├── main/java/vn/nhom15/nhakhoa/
│       │   ├── App.java            ← entry point + điều hướng 1 cửa sổ (Duy)
│       │   ├── connect/ConnectDB.java  ← singleton kết nối (Thanh)
│       │   ├── model/              ← 16 entity + enum + SessionContext
│       │   ├── dao/                ← Xxx_DAO + BoDem_DAO (cấp mã tự động)
│       │   ├── bus/                ← Xxx_BUS
│       │   ├── gui/                ← GD_*_Controller (cùng tên FXML)
│       │   └── util/               ← FormatUtil, ValidateUtil, QuyenUtil, AlertUtil, GoiYAI
│       ├── main/resources/
│       │   ├── fxml/GD_*.fxml      ← Scene Builder
│       │   ├── css/ungdung.css     ← theme duy nhất của app
│       │   ├── pic/                ← logo + bộ icon FontAwesome
│       │   ├── db.properties.example
│       │   └── db.properties       ← CẤU HÌNH THẬT — KHÔNG commit
│       └── test/java/…/TestRunner.java  ← 33 kiểm thử backend tự động
├── TaiLieu/                        ← README_RULES, README_KHOI_NGHIEP, KICHBAN (md+docx),
│   │                                  docs/tasks (nhiệm vụ 4 thành viên), docs/evidence (minh chứng)
├── FileDongGoi/                    ← (để trống) bản đóng gói .jar/.exe khi nộp
├── FileTrinhChieu/                 ← (để trống) slide thuyết trình khi nộp
├── video/                          ← (để trống) video demo khi nộp
└── README.md                       ← file này
```

### 4.2. Màn hình ứng dụng (đặt tên chuẩn — cấm đặt lại)

| FXML (`SourceCode/src/main/resources/fxml/`) | Use case | Người |
| --- | --- | --- |
| `GD_DangNhap.fxml` | UC001 | Duy |
| `GD_Chinh.fxml` | khung điều hướng 1 cửa sổ | Duy |
| `GD_TongQuan.fxml` | dashboard sau đăng nhập | Duy |
| `GD_QLBenhNhan.fxml` | UC002 | Huy |
| `GD_TraCuuBenhNhan.fxml` | UC006 | Huy |
| `GD_ThuPhi.fxml` | UC005 | Huy |
| `GD_LapPhieuKham.fxml` | UC003 | Huy |
| `GD_HangCho.fxml` | UC009 | Thanh |
| `GD_KhamBenh.fxml` | UC004 | Thắng |
| `GD_LichSuKham.fxml` | lịch sử khám (sơ đồ luồng Word) | Thắng |
| `GD_QLNhanVien.fxml` | UC010 | Duy |
| `GD_QLDichVu.fxml` | UC007 | Duy |
| `GD_ThanhToan.fxml` | thanh toán cuối lượt (sơ đồ luồng Word) | Huy |
| `GD_BaoCao.fxml` | UC008 | Duy |

## 5. Cơ sở dữ liệu (theo báo cáo Chương 4 — 12 bảng)

`NhanVien`, `TaiKhoan`, `PhongKham`, `BenhNhan`, `HoSoBenhAn`, `DichVu`, `Thuoc`, `PhieuKham`, `HoaDon`, `ChiTietHoaDon`, `ChiDinhDichVu`, `ChiTietCapThuoc` — cộng bảng hạ tầng `BoDem` (bộ đếm mã) do Thanh chủ trì.

Quy ước chính (chi tiết đầy đủ trong [TaiLieu/README_RULES.md](TaiLieu/README_RULES.md)):

- Mã tự động theo ngày, cấp nguyên tử qua bảng `BoDem` trong transaction: `NVyyMMddNNNN`, `BNyyMMddNNNN`, `HSyyMMddNNNN`, `PKyyMMddNNNN`, `HDyyMMddNNNN`, `DVNNNN`, `THNNNN`, `PNNN`.
- `PhieuKham.soThuTu`: UNIQUE theo cặp `(ngayKham, maPhong)`.
- Tiền: `decimal(18,2)` ↔ `BigDecimal`; chuỗi tiếng Việt: `nvarchar`; trạng thái: `nvarchar` lưu đúng tên enum.

## 6. Hướng dẫn cài đặt & chạy (làm đúng thứ tự)

1. **JDK 21** — cài Temurin JDK 21 (tick *Set JAVA_HOME*), `java -version` phải báo 21.
2. **SQL Server + SSMS** — bật **TCP/IP 1433** trong SQL Server Configuration Manager và restart dịch vụ; bật **SQL Server + Windows Authentication mode**; enable user `sa`.
3. **VS Code** — cài Extension Pack for Java + Maven for Java; **Scene Builder** (Gluon) để mở FXML.
4. **Tạo DB** — trong SSMS (hoặc Git Bash `cd SourceCode/DATA` rồi vòng lặp sqlcmd theo `DATA/README.md`): chạy `schema/000_hatang.sql` → `012_*.sql` rồi `seed/*.sql`.
5. **Cấu hình kết nối** — copy `SourceCode/src/main/resources/db.properties.example` thành `db.properties`, sửa mật khẩu `sa` (file thật không commit).
6. **Chạy app** — mở thư mục `SourceCode` trong VS Code (hoặc terminal `cd SourceCode`): `mvn clean compile` rồi `mvn javafx:run`.
7. **Đăng nhập thử** — `letan / 123456` (các tài khoản seed xem `TaiLieu/../SourceCode/DATA/seed/002_seed_nhanvien_taikhoan.sql`).

> ⚠️ Lỗi hay gặp (chưa kết nối được DB): port 1433 chưa bật, `encrypt=true` thiếu `trustServerCertificate=true`, sai driver — bẫy chi tiết ở [TaiLieu/README_RULES.md](TaiLieu/README_RULES.md).

## 7. Quy trình làm việc nhóm (Git/GitHub)

- Nhánh ổn định: `master`. Nhánh làm việc cá nhân: `feat/<ban>-<mã>-<tên>`. Nhánh tổng hợp: `prj15/main`.
- Mỗi chức năng = 1 Issue (prefix `LT-`/`QT-`/`BS-`/`PT-` theo luồng) = 1 PR. Commit conventional: `feat(uc003): lap phieu kham va cap so thu tu`.
- Review xoay vòng: **Thanh review Duy → Duy review Thắng → Thắng review Huy → Huy review Thanh**.
- Cấm: force push, commit trực tiếp lên `master`, commit `db.properties` thật, dữ liệu bệnh nhân thật.

### Mốc tiến độ

| Mốc | Kết quả |
| --- | --- |
| **M0 — Chốt nền** | pom + khung package + ConnectDB + schema 000–003 + seed |
| **M1 — GUI từng luồng** | màn hình chạy với dữ liệu mẫu |
| **M2 — BUS/DAO với DB** | chức năng chạy DB local + test case |
| **M3 — Ghép luồng** | Lễ tân → Phụ tá → Bác sĩ → Quản lý chạy trơn |
| **M4 — Nghiệm thu** | kiểm tra chéo, cập nhật Word/PPT, tập demo |

## 8. Trình bày & bảo vệ

Luồng demo chuẩn sau đăng nhập (mỗi người trình bày chặng của mình):

1. **Duy** — giới thiệu, đăng nhập phân quyền, khung ứng dụng.
2. **Huy** — tìm/tạo hồ sơ bệnh nhân → thu phí khám → lập phiếu, cấp STT.
3. **Thanh** — theo dõi hàng chờ theo phòng → gọi số → xác nhận vào khám.
4. **Thắng** — nhận danh sách khám → xem tiền sử → chẩn đoán, chỉ định, cấp thuốc → hoàn tất (+ lịch sử khám).
5. **Huy** — thanh toán dịch vụ cuối lượt.
6. **Duy** — quản lý nhân viên/dịch vụ → báo cáo lượt khám → xuất CSV.

Mỗi thành viên phải lần theo được một thao tác từ GUI xuống DB (GUI → BUS → DAO → SQL). Minh chứng lưu `TaiLieu/docs/evidence/<tên>/`.

---

## 9. Tham khảo

- Báo cáo môn học `Nhom15_REPORT_ApplicationDevelopment_v3.docx` (nguồn nghiệp vụ chính thức — không sửa trực tiếp).
- Source mẫu khóa trước: `PTUD_2025_Nhom11_DHKHMT18A/SourceCode/…/PTUD_N11_2025fx` (chuẩn GUI→DAO, ConnectDB, DAO, entity, controller/FXML).
- [OpenJFX — JavaFX + Maven](https://openjfx.io/openjfx-docs/#maven) · [Microsoft JDBC for SQL Server](https://learn.microsoft.com/sql/connect/jdbc/microsoft-jdbc-driver-for-sql-server) · [GitHub Flow](https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/proposing-changes-to-your-work-with-pull-requests/github-flow).
