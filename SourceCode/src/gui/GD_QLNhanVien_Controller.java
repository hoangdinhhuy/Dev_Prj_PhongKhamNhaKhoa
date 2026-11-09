package gui;

import java.time.LocalDate;
import java.util.ArrayList;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import main.App;
import bus.NhanVien_BUS;
import entity.ChucVu;
import entity.NhanVien;
import util.AlertUtil;

/**
 * Controller Quản lý nhân viên & tài khoản (UC010).
 * Tuân thủ quy chuẩn:
 * - Ẩn / để trống danh sách ban đầu cho đến khi người dùng tìm kiếm.
 * - Tra cứu dựa trên tiêu chí hoặc từ khóa.
 * - Xử lý trực tiếp trên đối tượng Entity NhanVien (kèm tài khoản liên kết), chuẩn OOP.
 */
public class GD_QLNhanVien_Controller {

    @FXML private TableView<NhanVien> table_nhanVien;
    @FXML private TableColumn<NhanVien, String> cl_stt;
    @FXML private TableColumn<NhanVien, String> cl_ma;
    @FXML private TableColumn<NhanVien, String> cl_hoTen;
    @FXML private TableColumn<NhanVien, String> cl_sdt;
    @FXML private TableColumn<NhanVien, String> cl_gioiTinh;
    @FXML private TableColumn<NhanVien, String> cl_chucVu;
    @FXML private TableColumn<NhanVien, String> cl_lamViec;
    @FXML private TableColumn<NhanVien, String> cl_tenDangNhap;
    @FXML private TableColumn<NhanVien, String> cl_trangThaiTK;

    @FXML private TextField txt_timKiem;
    @FXML private ComboBox<String> cbb_locChucVu;
    @FXML private ComboBox<String> cbb_locTrangThai;
    @FXML private Label lb_thongKeNhanVien;

    @FXML private TextField txt_maNV;
    @FXML private TextField txt_hoTen;
    @FXML private TextField txt_sdt;
    @FXML private DatePicker dp_ngaySinh;
    @FXML private ComboBox<String> cbb_gioiTinh;
    @FXML private TextField txt_cccd;
    @FXML private ComboBox<ChucVu> cbb_chucVu;
    @FXML private ComboBox<String> cbb_conLamViec;

    @FXML private TextField txt_tenDangNhap;
    @FXML private PasswordField txt_matKhau;
    @FXML private TextField txt_trangThaiTK;

    @FXML
    private void initialize() {
        cl_stt.setCellValueFactory(cd -> new SimpleStringProperty(
                String.valueOf(table_nhanVien.getItems().indexOf(cd.getValue()) + 1)));
        cl_ma.setCellValueFactory(new PropertyValueFactory<>("maNhanVien"));
        cl_hoTen.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        cl_sdt.setCellValueFactory(new PropertyValueFactory<>("soDienThoai"));
        cl_gioiTinh.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getGioiTinhString()));
        cl_chucVu.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getChucVu().toString()));
        cl_lamViec.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getTrangThaiLamViecString()));
        cl_tenDangNhap.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getTenDangNhap() == null ? "—" : cd.getValue().getTenDangNhap()));
        cl_trangThaiTK.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getTrangThaiTaiKhoanString()));

        // Khởi tạo các ComboBox
        cbb_gioiTinh.getItems().addAll("Nam", "Nữ");
        cbb_chucVu.getItems().addAll(ChucVu.values());
        cbb_conLamViec.getItems().addAll("Đang làm việc", "Đã nghỉ việc");
        cbb_conLamViec.setValue("Đang làm việc");

        // Bộ lọc chức vụ
        cbb_locChucVu.getItems().add("Tất cả");
        for (ChucVu cv : ChucVu.values()) {
            cbb_locChucVu.getItems().add(cv.toString());
        }
        cbb_locChucVu.setValue("Tất cả");

        // Bộ lọc tình trạng
        cbb_locTrangThai.getItems().addAll("Tất cả", "Đang làm việc", "Đã nghỉ việc");
        cbb_locTrangThai.setValue("Tất cả");

        // Bắt sự kiện chọn dòng trên bảng (thao tác trực tiếp trên đối tượng OOP)
        table_nhanVien.getSelectionModel().selectedItemProperty().addListener((obs, cu, moi) -> {
            if (moi != null) {
                doForm(moi);
            }
        });

        // QUY CHUẨN: Ẩn/để trống dữ liệu ban đầu cho đến khi người dùng tìm kiếm
        table_nhanVien.getItems().clear();
        lb_thongKeNhanVien.setText("Chưa tìm kiếm (Danh sách đang để trống)");
    }

    private void doForm(NhanVien nv) {
        txt_maNV.setText(nv.getMaNhanVien());
        txt_hoTen.setText(nv.getHoTen());
        txt_sdt.setText(nv.getSoDienThoai());
        dp_ngaySinh.setValue(nv.getNgaySinh());
        cbb_gioiTinh.setValue(nv.getGioiTinh() == null ? null : (nv.getGioiTinh() ? "Nam" : "Nữ"));
        txt_cccd.setText(nv.getCccd() == null ? "" : nv.getCccd());
        cbb_chucVu.setValue(nv.getChucVu());
        cbb_conLamViec.setValue(nv.isConLamViec() ? "Đang làm việc" : "Đã nghỉ việc");

        txt_tenDangNhap.setText(nv.getTenDangNhap() == null ? nv.getMaNhanVien() : nv.getTenDangNhap());
        txt_trangThaiTK.setText(nv.getTrangThaiTaiKhoanString());
        txt_matKhau.clear();
    }

    /**
     * Tra cứu theo tiêu chí: từ khóa + chức vụ + trạng thái làm việc.
     */
    @FXML
    public void timKiem() {
        String tuKhoa = txt_timKiem.getText();

        // Xác định chức vụ lọc
        ChucVu cvLoc = null;
        String cvStr = cbb_locChucVu.getValue();
        if (cvStr != null && !cvStr.equals("Tất cả")) {
            for (ChucVu cv : ChucVu.values()) {
                if (cv.toString().equals(cvStr)) {
                    cvLoc = cv;
                    break;
                }
            }
        }

        // Xác định trạng thái làm việc
        Boolean conLamLoc = null;
        String ttStr = cbb_locTrangThai.getValue();
        if ("Đang làm việc".equals(ttStr)) {
            conLamLoc = Boolean.TRUE;
        } else if ("Đã nghỉ việc".equals(ttStr)) {
            conLamLoc = Boolean.FALSE;
        }

        try {
            ArrayList<NhanVien> ketQua = NhanVien_BUS.timKiemNhanVien(
                    App.session, tuKhoa, cvLoc, conLamLoc);
            capNhatBang(ketQua);
            lb_thongKeNhanVien.setText("Tìm thấy: " + ketQua.size() + " nhân viên phù hợp");
            if (ketQua.isEmpty()) {
                AlertUtil.baoThongTin("Không tìm thấy nhân viên nào khớp với tiêu chí tìm kiếm.");
            }
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi("Lỗi tra cứu: " + ex.getMessage());
        }
    }

    private void capNhatBang(ArrayList<NhanVien> ds) {
        table_nhanVien.getItems().clear();
        table_nhanVien.setItems(FXCollections.observableArrayList(ds));
    }

    private Boolean gioiTinhChon() {
        String g = cbb_gioiTinh.getValue();
        return g == null ? null : (g.equals("Nam") ? Boolean.TRUE : Boolean.FALSE);
    }

    @FXML
    private void them() {
        try {
            NhanVien nv = NhanVien_BUS.themNhanVien(App.session, txt_hoTen.getText(), txt_sdt.getText(),
                    dp_ngaySinh.getValue(), gioiTinhChon(), txt_cccd.getText(), cbb_chucVu.getValue());
            AlertUtil.baoThongTin("Đã thêm thành công nhân viên: " + nv.getMaNhanVien() + " - " + nv.getHoTen());
            timKiem();
            lamMoi();
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }

    @FXML
    private void sua() {
        NhanVien chon = table_nhanVien.getSelectionModel().getSelectedItem();
        if (chon == null) {
            AlertUtil.baoLoi("Vui lòng chọn nhân viên trong bảng cần cập nhật.");
            return;
        }
        try {
            boolean conLam = "Đang làm việc".equals(cbb_conLamViec.getValue());
            NhanVien banSua = new NhanVien(chon.getMaNhanVien(), txt_hoTen.getText(), txt_sdt.getText(),
                    dp_ngaySinh.getValue(), gioiTinhChon(), txt_cccd.getText(),
                    cbb_chucVu.getValue(), conLam);
            NhanVien_BUS.suaNhanVien(App.session, banSua);
            AlertUtil.baoThongTin("Đã cập nhật thông tin nhân viên " + chon.getMaNhanVien());
            timKiem();
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }

    @FXML
    private void xoa() {
        NhanVien chon = table_nhanVien.getSelectionModel().getSelectedItem();
        if (chon == null) {
            AlertUtil.baoLoi("Vui lòng chọn nhân viên trong bảng cần xóa.");
            return;
        }
        try {
            if (!AlertUtil.xacNhan("Xác nhận xóa nhân viên " + chon.getMaNhanVien() + " - " + chon.getHoTen() + "?")) {
                return;
            }
            NhanVien_BUS.xoaNhanVien(App.session, chon.getMaNhanVien());
            AlertUtil.baoThongTin("Đã xóa nhân viên " + chon.getMaNhanVien());
            timKiem();
            lamMoi();
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }

    @FXML
    private void taoTaiKhoan() {
        NhanVien chon = table_nhanVien.getSelectionModel().getSelectedItem();
        if (chon == null) {
            AlertUtil.baoLoi("Vui lòng chọn nhân viên cần tạo tài khoản.");
            return;
        }
        if (chon.hasTaiKhoan()) {
            AlertUtil.baoLoi("Nhân viên này đã có tài khoản (" + chon.getTenDangNhap() + ").");
            return;
        }
        String tenDN = txt_tenDangNhap.getText().trim();
        String mk = txt_matKhau.getText();
        if (tenDN.isEmpty() || mk.isEmpty()) {
            AlertUtil.baoLoi("Vui lòng nhập tên đăng nhập và mật khẩu khởi tạo.");
            return;
        }
        try {
            NhanVien_BUS.taoTaiKhoan(App.session, chon.getMaNhanVien(), tenDN, mk);
            AlertUtil.baoThongTin("Đã tạo thành công tài khoản: " + tenDN + " cho nhân viên " + chon.getHoTen());
            timKiem();
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }

    @FXML
    private void khoaMo() {
        NhanVien chon = table_nhanVien.getSelectionModel().getSelectedItem();
        String tenDN = txt_tenDangNhap.getText().trim();
        if (chon != null && chon.hasTaiKhoan()) {
            tenDN = chon.getTenDangNhap();
        }
        if (tenDN.isEmpty()) {
            AlertUtil.baoLoi("Vui lòng chọn nhân viên có tài khoản hoặc nhập tên đăng nhập cần khóa/mở.");
            return;
        }
        try {
            NhanVien_BUS.khoaMoTaiKhoan(App.session, tenDN);
            AlertUtil.baoThongTin("Đã chuyển đổi trạng thái tài khoản " + tenDN);
            timKiem();
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }

    @FXML
    private void lamMoi() {
        txt_maNV.clear();
        txt_hoTen.clear();
        txt_sdt.clear();
        txt_cccd.clear();
        dp_ngaySinh.setValue(null);
        cbb_gioiTinh.setValue(null);
        cbb_chucVu.setValue(null);
        cbb_conLamViec.setValue("Đang làm việc");
        txt_tenDangNhap.clear();
        txt_matKhau.clear();
        txt_trangThaiTK.clear();
        table_nhanVien.getSelectionModel().clearSelection();
    }
}
