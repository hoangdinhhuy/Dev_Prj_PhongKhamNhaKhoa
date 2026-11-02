---
name: nhom15-dentistclinic
description: Chuẩn phát triển ứng dụng Nhóm 15 — Hệ thống quản lý Phòng khám Nha khoa Sài Gòn (JavaFX 21 + Maven + SQL Server, kiến trúc GUI→BUS→DAO). Kích hoạt khi làm bất kỳ việc gì trên repo PTUD_N15_/DentistClinic: thêm màn hình, sửa BUS/DAO/SQL, viết seed, kiểm thử, hay review code — dù người dùng chỉ nói "thêm chức năng", "sửa màn hình", "làm app nha khoa" mà không nhắc tên repo.
---

# Nhóm 15 — DentistClinic: chuẩn làm việc bắt buộc

Trước khi code, ĐỌC 2 file hợp lệ của dự án (không hỏi lại):
1. `TaiLieu/README_RULES.md` — quy tắc đặt tên, kiểu dữ liệu, bảng thông báo TB01–TB16/VL01–VL13, transaction, Git.
2. `TaiLieu/KICHBAN_THUC_NGHIEM.md` — kịch bản nghiệm thu; mọi thay đổi chức năng phải cập nhật kịch bản tương ứng.

## Kiến trúc (không đổi vì lý do tiện)

```
GUI (gui/GD_X_Controller + gui/GD_X.fxml)
  → chỉ gọi BUS, không SQL, không gọi DAO
BUS (bus/X_BUS)  → validate (ValidateUtil) + quyền (QuyenUtil) + transaction + sinh mã (BoDem_DAO)
DAO (dao/X_DAO)  → PreparedStatement, trả entity/List/boolean, không Alert
Model (model/)   → POJO + enum, không chứa JavaFX/Connection
connect/ConnectDB → singleton duy nhất cấp Connection (đọc /db.properties, KHÔNG commit file thật)
```

## Quy tắc hay quên (vi phạm = reject)

- Mã tự động: `BoDem_DAO.layMaMoi(con, loaiMa, ngay)` — cấm `MAX+1`/`COUNT+1`. STT dùng `laySoThuTuTiepTheo`.
- Chuyển trạng thái phiếu: UPDATE phải kèm `AND trangThai = '<trạng thái cũ>'` và kiểm tra số dòng ảnh hưởng (0 dòng → TB10).
- Kiểu dữ liệu: mã/SĐT/CCCD = String; ngày = LocalDate/LocalDateTime; tiền = BigDecimal; trạng thái = enum lưu NVARCHAR đúng tên (`CHO_KHAM`...).
- Thông báo: chỉ dùng chuỗi trong `util/MaThongBao.java`; cần message mới → thêm vào catalog trước rồi mới dùng.
- Cột SQL NULL-able KHÔNG được đặt `UNIQUE` (SQL Server không cho 2 NULL trong unique — đã gây lỗi thật ở `BenhNhan.cccd`, `PhieuKham.maHoaDonPhiKham`); tính duy nhất kiểm ở BUS.
- Cột computed `PERSISTED`/filtered index cấm dùng (yêu cầu SET option từng phiên — lỗi với JDBC/sqlcmd); dùng cột thường + `CHECK`.
- SQL mới: thêm file đánh số mới trong `SourceCode/DATA/schema|seed/` (file đã chạy cấm sửa); seed phải idempotent (`IF NOT EXISTS` guard).

## Thêm 1 màn hình (quy trình 6 bước)

1. FXML `src/gui/GD_<ChucNang>.fxml` — controller `gui.GD_<ChucNang>_Controller`, thanh tiêu đề là HBox `styleClass="header"` chứa nút "← Quay lại" (onAction `#quayLai` → gọi `App.quayVeTrangChu()`), KHÔNG ghi mã UC vào text hiển thị.
2. Thêm nút menu trong `GD_Chinh_Controller` (đúng vai trò) — điều hướng là **1 cửa sổ duy nhất**: `GD_Chinh_Controller.moFxml(ten)` nạp FXML vào `khungNoiDung`, cấm mở Stage/window mới cho các màn chức năng.
3. BUS method: `QuyenUtil.kiemTraQuyen(...)` là dòng đầu tiên; validate trước khi gọi DAO.
4. DAO: chỉ thêm vào DAO của chủ bảng (quyền sở hữu file trong `docs/tasks/`).
5. Gắn theme: mọi Scene phải gọi `App.ganTheme(scene)` (scene chính tự gắn qua `App.setRoot`).
6. Bổ sung test case vào `TaiLieu/KICHBAN_THUC_NGHIEM.md` + (nếu backend) 1 check trong `SourceCode/src/main/TestRunner.java`.

## Kiểm thử

- Backend: chạy `main.TestRunner` (headless, DB thật) — phải 33/33 PASS trước khi báo xong bất kỳ thay đổi nào đụng BUS/DAO/SQL.
- GUI: người thật chạy kịch bản S0–S10 trong `TaiLieu/KICHBAN_THUC_NGHIEM.md`.

## UI theme

`resources/css/ungdung.css` là theme duy nhất: header gradient xanh `#154360→#1F618D`, card trắng bo góc, badge trạng thái (`badge-cho/dang/hoan`), nút `.danger-btn/.success-btn`. Font Segoe UI. Icon dùng emoji chủ đề nha khoa (🦷 ⏳ 💵 📊 🧑‍💼) — không chèn ảnh ngoài không có giấy phép.
