package gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import main.App;
import bus.DichVu_BUS;
import entity.DichVu;
import entity.TrangThaiDichVu;
import util.AlertUtil;
import util.FormatUtil;

/** UC007 - Quan ly danh muc dich vu (chu file: Duy). */
public class GD_QLDichVu_Controller {

    @FXML private TableView<DichVu> table_dichVu;
    @FXML private TableColumn<DichVu, String> cl_stt;
    @FXML private TableColumn<DichVu, String> cl_ma;
    @FXML private TableColumn<DichVu, String> cl_ten;
    @FXML private TableColumn<DichVu, String> cl_gia;
    @FXML private TableColumn<DichVu, String> cl_tt;
    @FXML private TextField txt_tenDichVu;
    @FXML private TextField txt_donGia;
    @FXML private ComboBox<TrangThaiDichVu> cbb_trangThai;

    @FXML
    private void initialize() {
        cl_stt.setCellValueFactory(cd -> new SimpleStringProperty(
                String.valueOf(table_dichVu.getItems().indexOf(cd.getValue()) + 1)));
        cl_ma.setCellValueFactory(new PropertyValueFactory<>("maDichVu"));
        cl_ten.setCellValueFactory(new PropertyValueFactory<>("tenDichVu"));
        cl_gia.setCellValueFactory(cd -> new SimpleStringProperty(FormatUtil.tien(cd.getValue().getDonGiaNiemYet())));
        cl_tt.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getTrangThai().toString()));
        cbb_trangThai.getItems().addAll(TrangThaiDichVu.values());
        table_dichVu.setOnMouseClicked(e -> {
            if (e.getClickCount() == 1) {
                doForm();
            }
        });
        loadTableData();
    }

    private void doForm() {
        DichVu dv = table_dichVu.getSelectionModel().getSelectedItem();
        if (dv == null) {
            return;
        }
        txt_tenDichVu.setText(dv.getTenDichVu());
        txt_donGia.setText(dv.getDonGiaNiemYet() == null ? "" : dv.getDonGiaNiemYet().toPlainString());
        cbb_trangThai.setValue(dv.getTrangThai());
    }

    private void loadTableData() {
        var ds = DichVu_BUS.danhSachDichVu(App.session);
        table_dichVu.getItems().clear();
        table_dichVu.setItems(FXCollections.observableArrayList(ds));
    }

    @FXML
    private void them() {
        try {
            DichVu dv = DichVu_BUS.themDichVu(App.session, txt_tenDichVu.getText(), txt_donGia.getText());
            AlertUtil.baoThongTin("Đã thêm dịch vụ " + dv.getMaDichVu());
            loadTableData();
            lamMoi();
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }

    @FXML
    private void sua() {
        DichVu dv = table_dichVu.getSelectionModel().getSelectedItem();
        if (dv == null) {
            AlertUtil.baoLoi("Vui lòng chọn dịch vụ trong bảng.");
            return;
        }
        try {
            DichVu_BUS.suaDichVu(App.session, dv, txt_tenDichVu.getText(), txt_donGia.getText());
            AlertUtil.baoThongTin("Đã cập nhật dịch vụ " + dv.getMaDichVu()
                    + " (hóa đơn cũ giữ nguyên giá snapshot).");
            loadTableData();
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }

    @FXML
    private void doiTrangThai() {
        DichVu dv = table_dichVu.getSelectionModel().getSelectedItem();
        if (dv == null) {
            AlertUtil.baoLoi("Vui lòng chọn dịch vụ trong bảng.");
            return;
        }
        TrangThaiDichVu moi = cbb_trangThai.getValue();
        if (moi == null) {
            AlertUtil.baoLoi("Chọn trạng thái mới trong ComboBox.");
            return;
        }
        try {
            DichVu_BUS.doiTrangThai(App.session, dv, moi);
            AlertUtil.baoThongTin("Đã đổi trạng thái " + dv.getMaDichVu() + " → " + moi);
            loadTableData();
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }

    @FXML
    private void lamMoi() {
        txt_tenDichVu.clear();
        txt_donGia.clear();
        cbb_trangThai.setValue(null);
        table_dichVu.getSelectionModel().clearSelection();
    }
}
