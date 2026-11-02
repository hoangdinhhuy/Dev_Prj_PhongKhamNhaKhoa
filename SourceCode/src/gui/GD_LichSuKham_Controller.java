package gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
    @FXML private TableView<PhieuKham> table_lichSu;
    @FXML private TableColumn<PhieuKham, String> cl_ma;
    @FXML private TableColumn<PhieuKham, String> cl_bn;
    @FXML private TableColumn<PhieuKham, String> cl_ngay;
    @FXML private TableColumn<PhieuKham, String> cl_bs;
    @FXML private TableColumn<PhieuKham, String> cl_noiDung;
    @FXML private TableColumn<PhieuKham, String> cl_tt;

    @FXML
    private void initialize() {
        cl_ma.setCellValueFactory(new PropertyValueFactory<>("maPhieuKham"));
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
        tatCa();
    }

    @FXML
    private void tim() {
        try {
            String k = txt_tim.getText().trim().toLowerCase();
            var ds = PhieuKham_BUS.layToanBoLichSu(App.session).stream()
                    .filter(pk -> k.isEmpty()
                            || (pk.getTenBenhNhan() != null && pk.getTenBenhNhan().toLowerCase().contains(k))
                            || (pk.getMaBenhNhan() != null && pk.getMaBenhNhan().toLowerCase().contains(k))
                            || pk.getMaPhieuKham().toLowerCase().contains(k))
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
    private void tatCa() {
        try {
            nap(PhieuKham_BUS.layToanBoLichSu(App.session));
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }

    private void nap(java.util.List<PhieuKham> ds) {
        table_lichSu.getItems().clear();
        table_lichSu.setItems(FXCollections.observableArrayList(ds));
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
                .append("  |  Phòng: ").append(pk.getMaPhong()).append("\n");
        sb.append("Bác sĩ: ").append(pk.getTenBacSi()).append("\n");
        sb.append("Lý do khám: ").append(pk.getLyDoKham()).append("\n");
        sb.append("Chẩn đoán: ").append(pk.getChanDoan() == null ? "(chưa có)" : pk.getChanDoan()).append("\n");
        sb.append("Kết quả điều trị: ").append(pk.getKetQuaDieuTri() == null ? "(chưa có)" : pk.getKetQuaDieuTri()).append("\n");
        sb.append("Ghi chú thuốc tự mua: ").append(pk.getGhiChuThuocTuMua() == null ? "(không)" : pk.getGhiChuThuocTuMua()).append("\n");
        sb.append("Trạng thái: ").append(pk.getTrangThai()).append("\n");
        javafx.scene.control.TextArea ta = new javafx.scene.control.TextArea(sb.toString());
        ta.setEditable(false);
        ta.setPrefSize(560, 380);
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
