package gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import main.App;
import bus.BenhNhan_BUS;
import entity.BenhNhan;
import util.AlertUtil;

/** UC002 - Quan ly ho so benh nhan (chu file: Huy). */
public class GD_QLBenhNhan_Controller {

    @FXML private TableView<BenhNhan> table_benhNhan;
    @FXML private TableColumn<BenhNhan, String> cl_stt;
    @FXML private TableColumn<BenhNhan, String> cl_ma;
    @FXML private TableColumn<BenhNhan, String> cl_hoTen;
    @FXML private TableColumn<BenhNhan, String> cl_sdt;
    @FXML private TextField txt_hoTen;
    @FXML private TextField txt_sdt;
    @FXML private TextField txt_cccd;
    @FXML private TextField txt_diaChi;
    @FXML private TextArea txt_tienSuDiUng;
    @FXML private TextField txt_timSDT;
    @FXML private DatePicker dp_ngaySinh;
    @FXML private Button btn_them;
    @FXML private Button btn_sua;
    @FXML private Button btn_thuTien;

    @FXML
    private void initialize() {
        cl_stt.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(
                String.valueOf(table_benhNhan.getItems().indexOf(cd.getValue()) + 1)));
        cl_ma.setCellValueFactory(new PropertyValueFactory<>("maBenhNhan"));
        cl_hoTen.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        cl_sdt.setCellValueFactory(new PropertyValueFactory<>("soDienThoai"));
        table_benhNhan.setOnMouseClicked(e -> {
            if (e.getClickCount() == 1) {
                doForm();
            }
        });
        table_benhNhan.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, selected) ->
                btn_thuTien.setDisable(selected == null));
        // (Góp ý GV 2/5 — cách 1) Mở màn: bảng TRỐNG cho tới khi tìm/hiện tất cả
        lamBangRong();
    }

    /** Bảng TRỐNG khi mới mở màn — không nạp gì trước khi có yêu cầu tìm kiếm. */
    private void lamBangRong() {
        table_benhNhan.getItems().clear();
    }

    /** Nút tường minh: người dùng yêu cầu rõ ràng mới tải toàn bộ danh sách. */
    @FXML
    private void hienTatCa() {
        try {
            loadTableData();
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }

    private void loadTableData() {   // chỉ gọi từ nút "Hiện tất cả" (thao tác tường minh)
        var ds = BenhNhan_BUS.danhSachToanBo(App.session);
        table_benhNhan.getItems().clear();
        table_benhNhan.setItems(FXCollections.observableArrayList(ds));
    }

    @FXML
    private void timTheoSDT() {
        String sdt = txt_timSDT.getText() == null ? "" : txt_timSDT.getText().trim();
        if (sdt.isEmpty()) {
            lamBangRong();
            AlertUtil.baoThongTin("Nhập số điện thoại rồi bấm Tìm; hoặc bấm Hiện tất cả để xem toàn bộ.");
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

    @FXML
    private void them() {
        try {
            if (!AlertUtil.xacNhan("Tạo hồ sơ bệnh nhân với thông tin đã nhập?")) {
                return;
            }
            BenhNhan bn = BenhNhan_BUS.taoHoSo(App.session, txt_hoTen.getText(), dp_ngaySinh.getValue(),
                    txt_sdt.getText(), txt_cccd.getText(), txt_diaChi.getText(), txt_tienSuDiUng.getText());
            AlertUtil.baoThongTin("Đã tạo hồ sơ bệnh nhân " + bn.getMaBenhNhan());
            txt_timSDT.setText(bn.getSoDienThoai());
            timTheoSDT();   // tìm theo SĐT vừa tạo — danh sách chỉ hiện bản ghi đang làm việc
            chonBenhNhan(bn.getMaBenhNhan());
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
            txt_timSDT.setText(chon.getSoDienThoai());
            timTheoSDT();
            chonBenhNhan(chon.getMaBenhNhan());
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

    private void chonBenhNhan(String maBenhNhan) {
        for (BenhNhan bn : table_benhNhan.getItems()) {
            if (bn.getMaBenhNhan().equals(maBenhNhan)) {
                table_benhNhan.getSelectionModel().select(bn);
                table_benhNhan.scrollTo(bn);
                doForm();
                return;
            }
        }
    }

    @FXML
    private void thuTien() {
        BenhNhan bn = table_benhNhan.getSelectionModel().getSelectedItem();
        if (bn == null) {
            AlertUtil.baoLoi("Chọn hoặc thêm bệnh nhân trước khi thu tiền.");
            return;
        }
        App.moManHinh("GD_ThuTien", bn);
    }

    /** Quet CCCD bang dien thoai: bat server nho, hien QR de dien thoai mo trang gui thong tin.
     *  TINH NANG AN — bat lai bang cach bo comment nut trong GD_QLBenhNhan.fxml. */
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
                    "Điện thoại kết nối CÙNG Wi-Fi (hoặc hotspot của laptop) rồi quét mã:\n"
                            + "http://" + server.QuetCCCDServer.ipLan() + ":" + port
                            + "\nĐang chờ thông tin từ điện thoại...");
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
}
