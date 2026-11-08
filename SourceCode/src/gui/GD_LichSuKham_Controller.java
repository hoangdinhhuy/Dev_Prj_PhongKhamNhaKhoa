package gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.App;
import bus.PhieuKham_BUS;
import entity.PhieuKham;
import util.AlertUtil;
import util.FormatUtil;
import util.MaThongBao;

/** Lich su kham (theo Figma man 5) — tra cuu moi luot kham, xem chi tiet (chu file: Thang). */
public class GD_LichSuKham_Controller {

    @FXML private TextField txt_tim;
    @FXML private javafx.scene.control.DatePicker dp_tuNgay;
    @FXML private javafx.scene.control.DatePicker dp_denNgay;
    @FXML private TableView<PhieuKham> table_lichSu;
    @FXML private TableColumn<PhieuKham, String> cl_ma;
    @FXML private TableColumn<PhieuKham, String> cl_lanThu;
    @FXML private TableColumn<PhieuKham, String> cl_bn;
    @FXML private TableColumn<PhieuKham, String> cl_ngay;
    @FXML private TableColumn<PhieuKham, String> cl_bs;
    @FXML private TableColumn<PhieuKham, String> cl_noiDung;
    @FXML private TableColumn<PhieuKham, String> cl_tt;

    @FXML
    private void initialize() {
        java.time.LocalDate homNay = java.time.LocalDate.now();
        dp_tuNgay.setValue(homNay.minusDays(30));
        dp_denNgay.setValue(homNay);
        cl_ma.setCellValueFactory(new PropertyValueFactory<>("maPhieuKham"));
        cl_lanThu.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getLanThu() > 0 ? "Lần " + cd.getValue().getLanThu() : "—"));
        cl_bn.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getTenBenhNhan() + " (" + cd.getValue().getMaBenhNhan() + ")"));
        cl_ngay.setCellValueFactory(cd -> new SimpleStringProperty(FormatUtil.ngay(cd.getValue().getNgayKham())));
        cl_bs.setCellValueFactory(new PropertyValueFactory<>("tenBacSi"));
        cl_noiDung.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getChanDoan() == null || cd.getValue().getChanDoan().isBlank()
                        ? cd.getValue().getLyDoKham() : cd.getValue().getChanDoan()));
        cl_tt.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getTrangThai().toString()));
        cl_tt.setCellFactory(col -> new javafx.scene.control.TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    getStyleClass().removeAll("badge-cho", "badge-dang", "badge-hoan");
                } else {
                    setText(item);
                    getStyleClass().removeAll("badge-cho", "badge-dang", "badge-hoan");
                    getStyleClass().add(switch (item) {
                        case "Chờ khám" -> "badge-cho";
                        case "Đang khám" -> "badge-dang";
                        default -> "badge-hoan";
                    });
                }
            }
        });
        table_lichSu.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                xemChiTiet();
            }
        });
        // (Góp ý GV 2/5 — cách 1) Mở màn: bảng TRỐNG cho tới khi bấm Tìm/Lọc
        nap(java.util.List.of());
    }

    private void nap(java.util.List<PhieuKham> ds) {
        table_lichSu.getItems().clear();
        table_lichSu.setItems(FXCollections.observableArrayList(ds));
    }

    @FXML
    private void tim() {
        try {
            // (Góp ý GV 2/6) Tìm không phụ thuộc dấu tiếng Việt
            String k = util.GoiTuKhoa.chuanHoa(txt_tim.getText());
            java.time.LocalDate tu = dp_tuNgay.getValue() == null
                    ? java.time.LocalDate.now().minusDays(30) : dp_tuNgay.getValue();
            java.time.LocalDate den = dp_denNgay.getValue() == null
                    ? java.time.LocalDate.now() : dp_denNgay.getValue();
            var ds = PhieuKham_BUS.layLichSuKhoangLanThu(App.session, tu, den, 200).stream()
                    .filter(pk -> k.isEmpty()
                            || util.GoiTuKhoa.chuanHoa(pk.getTenBenhNhan()).contains(k)
                            || (pk.getMaBenhNhan() != null && util.GoiTuKhoa.chuanHoa(pk.getMaBenhNhan()).contains(k))
                            || util.GoiTuKhoa.chuanHoa(pk.getMaPhieuKham()).contains(k))
                    .toList();
            nap(ds);
            if (ds.isEmpty()) {
                AlertUtil.baoLoi(MaThongBao.TB05);
            }
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }

    @FXML
    private void lamMoi() {
        txt_tim.clear();
        nap(java.util.List.of());
    }

    private void xemChiTiet() {
        PhieuKham pk = table_lichSu.getSelectionModel().getSelectedItem();
        if (pk == null) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Mã lượt khám: ").append(pk.getMaPhieuKham()).append("\n");
        sb.append("Bệnh nhân: ").append(pk.getTenBenhNhan()).append(" (").append(pk.getMaBenhNhan()).append(")\n");
        sb.append("Ngày khám: ").append(FormatUtil.ngay(pk.getNgayKham()))
                .append("  |  STT: ").append(pk.getSoThuTu())
                .append("  |  Phòng: ").append(pk.getTenPhong() == null ? pk.getMaPhong() : pk.getTenPhong()).append("\n");
        sb.append("Bác sĩ: ").append(pk.getTenBacSi()).append("\n");
        sb.append("Lý do khám: ").append(pk.getLyDoKham()).append("\n");
        sb.append("Chẩn đoán: ").append(pk.getChanDoan() == null ? "(chưa có)" : pk.getChanDoan()).append("\n");
        sb.append("Kết quả điều trị: ").append(pk.getKetQuaDieuTri() == null ? "(chưa có)" : pk.getKetQuaDieuTri()).append("\n");
        sb.append("Lần điều trị: ").append(pk.getLanThu() > 0 ? "thứ " + pk.getLanThu() : "—").append("\n");
        sb.append("Phác đồ điều trị: ").append(pk.getPhacDieuTri() == null ? "(không)" : pk.getPhacDieuTri()).append("\n");
        sb.append("Ngày hẹn tái khám: ").append(pk.getNgayHenTiep() == null ? "(không)"
                : FormatUtil.ngay(pk.getNgayHenTiep())).append("\n");
        try {
            var cds = PhieuKham_BUS.layChiDinhTheoPhieu(App.session, pk.getMaPhieuKham());
            if (!cds.isEmpty()) {
                sb.append("Dịch vụ đã chỉ định: ");
                for (entity.ChiDinhDichVu c : cds) {
                    sb.append(c.getTenDichVu());
                    if (c.getSoRang() != null && !c.getSoRang().isBlank()) {
                        sb.append(" (răng ").append(c.getSoRang()).append(")");
                    }
                    sb.append(";  ");
                }
                sb.append("\n");
            }
        } catch (RuntimeException ignored) { }
        sb.append("Ghi chú thuốc tự mua: ").append(pk.getGhiChuThuocTuMua() == null ? "(không)" : pk.getGhiChuThuocTuMua()).append("\n");
        sb.append("Trạng thái: ").append(pk.getTrangThai()).append("\n");
        javafx.scene.control.TextArea ta = new javafx.scene.control.TextArea(sb.toString());
        ta.setEditable(false);
        ta.setPrefSize(560, 420);
        Stage stage = new Stage();
        stage.initOwner(table_lichSu.getScene().getWindow());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle("Chi tiết lượt khám " + pk.getMaPhieuKham());
        javafx.scene.Scene sc = new javafx.scene.Scene(ta);
        App.ganTheme(sc);
        stage.setScene(sc);
        stage.showAndWait();
    }
}
