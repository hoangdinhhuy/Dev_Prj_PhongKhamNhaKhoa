# HƯỚNG DẪN CÀI ĐẶT & CHẠY DỰ ÁN (bắt buộc đọc trước khi chạy)

> Danh sách thứ cần **tải về trước**, sau đó cài theo từng bước. Máy đã có sẵn SQL Server + SSMS + VS Code + Git thì bỏ qua bước tương ứng.

---

## 1. Những thứ cần tải về

| # | Phần mềm | Link tải | Bắt buộc? |
| --- | --- | --- | --- |
| 1 | **JDK 21** — Eclipse Temurin 21 LTS (Windows x64 **MSI**) | https://adoptium.net/temurin/releases/?version=21 | ✅ |
| 2 | **Apache Maven 3.9.x** (Binary zip) | https://maven.apache.org/download.cgi | ✅ |
| 3 | **SQL Server Express 2022** + **SSMS 22** | https://www.microsoft.com/vi-vn/sql-server/sql-server-downloads (bản Express miễn phí) | ✅ (nếu máy chưa có) |
| 4 | **VS Code** + Extension Pack for Java + Maven for Java | https://code.visualstudio.com | ✅ |
| 5 | **Gluon Scene Builder** (mở file FXML) | https://gluonhq.com/products/scene-builder/ | Tùy chọn |

## 2. Cài JDK 21

1. Chạy file MSI → màn **Custom Setup** tick cả 2 ô: *Add to PATH* và *Set JAVA_HOME variable*.
2. Đóng và mở lại terminal, kiểm tra: `java -version` phải báo **21**.

## 3. Cài Maven

1. Giải nén zip vào `C:\apache-maven` (thư mục KHÔNG có khoảng trắng) → có `C:\apache-maven\bin\mvn.cmd`.
2. Win → gõ "environment variables" → *Edit the system environment variables* → **Environment Variables...** → trong *User variables* chọn **Path → Edit... → New** → dán `C:\apache-maven\bin` → OK cả 3 hộp thoại.
   (Cách nhanh bằng PowerShell: `[Environment]::SetEnvironmentVariable("Path", [Environment]::GetEnvironmentVariable("Path","User") + ";C:\apache-maven\bin", "User")`)
3. **Đóng terminal mở lại**, kiểm tra: `mvn -v` → *Java version* phải là **21** (nếu không, `JAVA_HOME` còn trỏ JDK cũ — sửa lại biến JAVA_HOME).

## 4. Chuẩn bị SQL Server (máy đã cài thì chỉ cấu hình)

1. **Bật TCP/IP**: Start → *SQL Server Configuration Manager* → SQL Server Network Configuration → Protocols for `SQLEXPRESS` (hoặc MSSQLSERVER) → **TCP/IP = Enabled** → double-click TCP/IP → tab IP Addresses → IPAll → **TCP Port = 1433** → OK.
2. **Restart dịch vụ**: Configuration Manager → SQL Server Services → chuột phải SQL Server (SQLEXPRESS) → Restart. Chỉ giữ **1 instance** dùng port 1433 (instance còn lại → Stop).
3. **Bật tài khoản sa**: SSMS → kết nối bằng Windows Authentication → chuột phải server → Properties → Security → chọn **SQL Server and Windows Authentication mode** → OK → Security → Logins → `sa` → Properties → đặt mật khẩu (vd `NhaKhoa2026!`) + Status: **Enabled** → OK → chuột phải server → **Restart**.
4. Kiểm tra: `sqlcmd -S localhost -U sa -P NhaKhoa2026! -Q "SELECT 1"` → ra số 1.

## 5. Lấy code + tạo CSDL

1. Mở **Git Bash**:
   ```bash
   cd /e/Nam4_IUH/PTUD/Project_Nhom15/PTUD_N15_   # hoặc git clone nếu máy mới
   git pull origin prj15/main
   ```
2. Tạo CSDL (dán cả khối, thay mật khẩu sa):
   ```bash
   cd SourceCode/DATA
   for f in schema/*.sql seed/*.sql; do echo "== $f"; sqlcmd -S localhost -U sa -P 'NhaKhoa2026!' -i "$f" -f 65001; done
   ```
   → 23 file chạy hết, mỗi file in `... OK`, không có dòng `Msg ...`.
3. Copy `SourceCode/src/db.properties.example` thành `db.properties` (cùng thư mục) → sửa `db.password=` thành mật khẩu sa của bạn. **File này không commit.**

## 6. Chạy ứng dụng

```bash
cd /e/Nam4_IUH/PTUD/Project_Nhom15/PTUD_N15_/SourceCode
mvn clean compile     # lần đầu tải ~40MB thư viện
mvn javafx:run
```

- App mở **tự động ở chế độ fullscreen (maximized)**.
- Đăng nhập (mật khẩu chung `123456`): `letan` (Lễ tân - Hoàng Đình Huy), `bacsi1`/`bacsi2` (Bác sĩ), `phuta` (Phụ tá - Phan Chí Thanh), `quanly` (Quản lý - Nguyễn Tuấn Duy), `lockacc` (tài khoản bị khóa để test).
- **VS Code**: File → Open Folder → chọn `SourceCode` → đợi "Building Java projects" xong → terminal gõ `mvn javafx:run` (hoặc panel Maven → Plugins → javafx → javafx:run).

## 7. Lỗi hay gặp

| Triệu chứng | Cách xử lý |
| --- | --- |
| `mvn` không được nhận diện | Chưa tải lại PATH — đóng/mở terminal; hoặc dùng cách PowerShell ở bước 3 |
| Maven báo Java version 20 | `JAVA_HOME` trỏ JDK 20 — sửa sang JDK 21, mở terminal mới |
| App báo "Không thể kết nối SQL Server" | TCP/IP 1433 chưa bật/restart; mật khẩu sa sai; 2 instance giành 1433 |
| `Login failed for user 'sa'` | Chưa bật Mixed Mode hoặc sa chưa Enabled |
| SQL script lỗi font tiếng Việt | Thiếu `-f 65001` trong lệnh sqlcmd |
| Màn hình nào cũng "Không mở được..." | Đang chạy bản build cũ — `mvn clean compile` rồi `mvn javafx:run` lại |

Sau khi chạy được → mở `TaiLieu/KICHBAN_THUC_NGHIEM.docx` và test từ S0.
