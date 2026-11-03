package gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import main.App;
import bus.BenhNhan_BUS;
import entity.BenhNhan;
import util.AlertUtil;
import util.FormatUtil;

/** UC002 - Quan ly ho so benh nhan (chu file: Huy). */
public class GD_QLBenhNhan_Controller {

    @FXML private TableView<BenhNhan> table_benhNhan;
    @FXML private TableColumn<BenhNhan, String> cl_stt;
    @FXML private TableColumn<BenhNhan, String> cl_ma;
    @FXML private TableColumn<BenhNhan, String> cl_hoTen;
    @FXML private TableColumn<BenhNhan, String> cl_sdt;
    @FXML private TableColumn<BenhNhan, String> cl_ngaySinh;
    @FXML private TableColumn<BenhNhan, String> cl_cccd;
    @FXML private TextField txt_hoTen;
    @FXML private TextField txt_sdt;
    @FXML private TextField txt_cccd;
    @FXML private TextField txt_diaChi;
    @FXML private TextArea txt_tienSuDiUng;
    @FXML private TextField txt_timSDT;
    @FXML private DatePicker dp_ngaySinh;
    @FXML private Button btn_them;
    @FXML private Button btn_sua;

    @FXML
    private void initialize() {
        cl_stt.setCellValueFactory(cd -> new SimpleStringProperty(
                String.valueOf(table_benhNhan.getItems().indexOf(cd.getValue()) + 1)));
        cl_ma.setCellValueFactory(new PropertyValueFactory<>("maBenhNhan"));
        cl_hoTen.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        cl_sdt.setCellValueFactory(new PropertyValueFactory<>("soDienThoai"));
        cl_ngaySinh.setCellValueFactory(cd -> new SimpleStringProperty(FormatUtil.ngay(cd.getValue().getNgaySinh())));
        cl_cccd.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getCccd() == null ? "" : cd.getValue().getCccd()));
        table_benhNhan.setOnMouseClicked(e -> {
            if (e.getClickCount() == 1) {
                doForm();
            }
        });
        loadTableData();
    }

    private void doForm() {
        BenhNhan bn = table_benhNhan.getSelectionModel().getSelectedItem();
        if (bn == null) {
            return;
        }
        txt_hoTen.setText(bn.getHoTen());
        txt_sdt.setText(bn.getSoDienThoai());
        txt_cccd.setText(bn.getCccd() == null ? "" : bn.getCccd());
        txt_diaChi.setText(bn.getDiaChi() == null ? "" : bn.getDiaChi());
        txt_tienSuDiUng.setText(bn.getTienSuDiUng() == null ? "" : bn.getTienSuDiUng());
        dp_ngaySinh.setValue(bn.getNgaySinh());
    }

    private void loadTableData() {
        var ds = BenhNhan_BUS.danhSachToanBo(App.session);
        table_benhNhan.getItems().clear();
        table_benhNhan.setItems(FXCollections.observableArrayList(ds));
    }

    @FXML
    private void timTheoSDT() {
        String sdt = txt_timSDT.getText() == null ? "" : txt_timSDT.getText().trim();
        if (sdt.isEmpty()) {
            loadTableData();
            return;
        }
        try {
            var ds = BenhNhan_BUS.traCuu(App.session, "SĐT", sdt);
            table_benhNhan.getItems().setAll(ds);
            table_benhNhan.getSelectionModel().clearSelection();
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }

    @FXML
    private void hienTatCa() {
        txt_timSDT.clear();
        loadTableData();
    }


    /** Quet CCCD bang dien thoai: bat server nho, hien QR de dien thoai mo trang gui thong tin. */
    @FXML
    private void quetCCCD() {
        try {
            int port = server.QuetCCCDServer.start();
            server.QuetCCCDServer.nguoiNhan = data -> javafx.application.Platform.runLater(() -> {
                txt_hoTen.setText(data.getOrDefault("hoTen", ""));
                txt_sdt.setText("");
                txt_cccd.setText(data.getOrDefault("cccd", ""));
                txt_diaChi.setText(data.getOrDefault("diaChi", ""));
                try {
                    dp_ngaySinh.setValue(java.time.LocalDate.parse(
                            data.getOrDefault("ngaySinh", ""),
                            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                } catch (Exception ignored) { }
                AlertUtil.baoThongTin("Đã nhận thông tin CCCD từ điện thoại — kiểm tra lại rồi bấm Thêm.");
            });
            javafx.scene.image.Image qr = server.QuetCCCDServer.taoQR(
                    "http://" + server.QuetCCCDServer.ipLan() + ":" + port + "/", 280);
            javafx.scene.control.Label lb = new javafx.scene.control.Label(
                    "Điện thoại kết nối CÙNG Wi-Fi (hoặc hotspot của laptop) rồi quét mã:\\n"
                            + "http://" + server.QuetCCCDServer.ipLan() + ":" + port
                            + "\\nĐang chờ thông tin từ điện thoại...");
            lb.setWrapText(true);
            javafx.scene.image.ImageView iv = new javafx.scene.image.ImageView(qr);
            javafx.scene.layout.VBox khung = new javafx.scene.layout.VBox(12, iv, lb);
            khung.setStyle("-fx-alignment: center; -fx-padding: 20; -fx-background-color: white;");
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.initOwner(table_benhNhan.getScene().getWindow());
            stage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            stage.setTitle("Quét CCCD bằng điện thoại");
            stage.setScene(new javafx.scene.Scene(khung, 460, 520));
            stage.setOnHidden(e -> server.QuetCCCDServer.stop());
            stage.show();
        } catch (Exception ex) {
            AlertUtil.baoLoi("Không bật được server quét CCCD: " + ex.getMessage());
        }
    }

    @FXML
    private void them() {
        try {
            if (!AlertUtil.xacNhan("Tạo hồ sơ bệnh nhân với thông tin đã nhập?")) {
                return;
            }
            // Cảnh báo trùng SĐT (SĐT không phải mã định danh duy nhất - chốt nhóm)
            if (!txt_sdt.getText().isBlank()
                    && !BenhNhan_BUS.layBenhNhanTheoSDT(txt_sdt.getText().trim()).isEmpty()
                    && !AlertUtil.xacNhan("Số điện thoại đã tồn tại. Vẫn tạo hồ sơ mới?")) {
                return;
            }
            BenhNhan bn = BenhNhan_BUS.taoHoSo(App.session, txt_hoTen.getText(), dp_ngaySinh.getValue(),
                    txt_sdt.getText(), txt_cccd.getText(), txt_diaChi.getText(), txt_tienSuDiUng.getText());
            AlertUtil.baoThongTin("Đã tạo hồ sơ bệnh nhân " + bn.getMaBenhNhan());
            loadTableData();
            lamMoi();
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }

    @FXML
    private void sua() {
        BenhNhan chon = table_benhNhan.getSelectionModel().getSelectedItem();
        if (chon == null) {
            AlertUtil.baoLoi("Vui lòng chọn bệnh nhân trong bảng.");
            return;
        }
        try {
            chon.setHoTen(txt_hoTen.getText());
            chon.setSoDienThoai(txt_sdt.getText());
            chon.setCccd(txt_cccd.getText());
            chon.setNgaySinh(dp_ngaySinh.getValue());
            chon.setDiaChi(txt_diaChi.getText());
            chon.setTienSuDiUng(txt_tienSuDiUng.getText());
            BenhNhan_BUS.capNhatHoSo(App.session, chon);
            AlertUtil.baoThongTin("Đã cập nhật hồ sơ " + chon.getMaBenhNhan());
            loadTableData();
            lamMoi();
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }

    @FXML
    private void lamMoi() {
        txt_hoTen.clear();
        txt_sdt.clear();
        txt_cccd.clear();
        txt_diaChi.clear();
        txt_tienSuDiUng.clear();
        dp_ngaySinh.setValue(null);
        table_benhNhan.getSelectionModel().clearSelection();
    }
}
