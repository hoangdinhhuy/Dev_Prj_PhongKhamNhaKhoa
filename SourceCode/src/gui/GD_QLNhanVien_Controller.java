package gui;

import java.time.LocalDate;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import main.App;
import bus.NhanVien_BUS;
import entity.ChucVu;
import entity.NhanVien;
import util.AlertUtil;
import util.FormatUtil;

/** UC010 - Quan ly nhan vien + tai khoan (chu file: Duy). */
public class GD_QLNhanVien_Controller {

    @FXML private TableView<NhanVien> table_nhanVien;
    @FXML private TableColumn<NhanVien, String> cl_stt;
    @FXML private TableColumn<NhanVien, String> cl_ma;
    @FXML private TableColumn<NhanVien, String> cl_hoTen;
    @FXML private TableColumn<NhanVien, String> cl_sdt;
    @FXML private TableColumn<NhanVien, String> cl_chucVu;
    @FXML private TableColumn<NhanVien, String> cl_lamViec;
    @FXML private TextField txt_hoTen;
    @FXML private TextField txt_sdt;
    @FXML private TextField txt_cccd;
    @FXML private DatePicker dp_ngaySinh;
    @FXML private ComboBox<String> cbb_gioiTinh;
    @FXML private ComboBox<ChucVu> cbb_chucVu;
    @FXML private TextField txt_tenDangNhap;
    @FXML private TextField txt_matKhau;

    @FXML
    private void initialize() {
        cl_stt.setCellValueFactory(cd -> new SimpleStringProperty(
                String.valueOf(table_nhanVien.getItems().indexOf(cd.getValue()) + 1)));
        cl_ma.setCellValueFactory(new PropertyValueFactory<>("maNhanVien"));
        cl_hoTen.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        cl_sdt.setCellValueFactory(new PropertyValueFactory<>("soDienThoai"));
        cl_chucVu.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getChucVu().toString()));
        cl_lamViec.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().isConLamViec() ? "Có" : "Không"));
        cbb_gioiTinh.getItems().addAll("Nam", "Nữ");
        cbb_chucVu.getItems().addAll(ChucVu.values());
        table_nhanVien.setOnMouseClicked(e -> {
            if (e.getClickCount() == 1) {
                doForm();
            }
        });
        loadTableData();
    }

    private void doForm() {
        NhanVien nv = table_nhanVien.getSelectionModel().getSelectedItem();
        if (nv == null) {
            return;
        }
        txt_hoTen.setText(nv.getHoTen());
        txt_sdt.setText(nv.getSoDienThoai());
        txt_cccd.setText(nv.getCccd() == null ? "" : nv.getCccd());
        dp_ngaySinh.setValue(nv.getNgaySinh());
        cbb_gioiTinh.setValue(nv.getGioiTinh() == null ? null : (nv.getGioiTinh() ? "Nam" : "Nữ"));
        cbb_chucVu.setValue(nv.getChucVu());
    }

    private void loadTableData() {
        var ds = NhanVien_BUS.danhSachNhanVien(App.session);
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
            AlertUtil.baoThongTin("Đã thêm nhân viên " + nv.getMaNhanVien());
            loadTableData();
            lamMoi();
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }

    @FXML
    private void sua() {
        NhanVien nv = table_nhanVien.getSelectionModel().getSelectedItem();
        if (nv == null) {
            AlertUtil.baoLoi("Vui lòng chọn nhân viên trong bảng.");
            return;
        }
        try {
            NhanVien banSua = new NhanVien(nv.getMaNhanVien(), txt_hoTen.getText(), txt_sdt.getText(),
                    dp_ngaySinh.getValue(), gioiTinhChon(), txt_cccd.getText(),
                    cbb_chucVu.getValue(), nv.isConLamViec());
            NhanVien_BUS.suaNhanVien(App.session, banSua);
            AlertUtil.baoThongTin("Đã cập nhật nhân viên " + nv.getMaNhanVien());
            loadTableData();
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }

    @FXML
    private void xoa() {
        NhanVien nv = table_nhanVien.getSelectionModel().getSelectedItem();
        if (nv == null) {
            AlertUtil.baoLoi("Vui lòng chọn nhân viên trong bảng.");
            return;
        }
        try {
            if (!AlertUtil.xacNhan("Xóa nhân viên " + nv.getMaNhanVien() + " - " + nv.getHoTen() + "?")) {
                return;
            }
            NhanVien_BUS.xoaNhanVien(App.session, nv.getMaNhanVien());
            AlertUtil.baoThongTin("Đã xóa.");
            loadTableData();
            lamMoi();
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }

    @FXML
    private void taoTaiKhoan() {
        NhanVien nv = table_nhanVien.getSelectionModel().getSelectedItem();
        if (nv == null) {
            AlertUtil.baoLoi("Chọn nhân viên cần tạo tài khoản.");
            return;
        }
        try {
            NhanVien_BUS.taoTaiKhoan(App.session, nv.getMaNhanVien(), txt_tenDangNhap.getText(),
                    txt_matKhau.getText());
            AlertUtil.baoThongTin("Đã tạo tài khoản " + txt_tenDangNhap.getText().trim());
            txt_tenDangNhap.clear();
            txt_matKhau.clear();
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }

    @FXML
    private void khoaMo() {
        try {
            String ten = txt_tenDangNhap.getText().trim();
            if (ten.isEmpty()) {
                AlertUtil.baoLoi("Nhập tên đăng nhập cần khóa/mở.");
                return;
            }
            NhanVien_BUS.khoaMoTaiKhoan(App.session, ten);
            AlertUtil.baoThongTin("Đã đổi trạng thái tài khoản " + ten);
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }

    @FXML
    private void lamMoi() {
        txt_hoTen.clear();
        txt_sdt.clear();
        txt_cccd.clear();
        dp_ngaySinh.setValue(null);
        cbb_gioiTinh.setValue(null);
        cbb_chucVu.setValue(null);
        table_nhanVien.getSelectionModel().clearSelection();
    }
}
