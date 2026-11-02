package gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import main.App;
import bus.AIHoTro_BUS;
import javafx.beans.property.SimpleStringProperty;
import util.AlertUtil;

/** (5) AI du bao vat tu nha khoa — cho QUAN_LY (chu file: Duy). */
public class GD_DuBaoVatTu_Controller {

    @FXML private TableView<Object[]> table_duBao;
    @FXML private TableColumn<Object[], String> cl_vatTu;
    @FXML private TableColumn<Object[], String> cl_daDung;
    @FXML private TableColumn<Object[], String> cl_duBao;
    @FXML private TableColumn<Object[], String> cl_ghiChu;
    @FXML private Button btn_chayDuBao;

    @FXML
    private void initialize() {
        cl_vatTu.setCellValueFactory(cd -> new SimpleStringProperty(String.valueOf(cd.getValue()[0])));
        cl_daDung.setCellValueFactory(cd -> new SimpleStringProperty(String.valueOf(cd.getValue()[1])));
        cl_duBao.setCellValueFactory(cd -> new SimpleStringProperty(String.valueOf(cd.getValue()[2])));
        cl_ghiChu.setCellValueFactory(cd -> new SimpleStringProperty(String.valueOf(cd.getValue()[3])));
        chayDuBao();
    }

    @FXML
    private void chayDuBao() {
        try {
            var ds = AIHoTro_BUS.duBaoVatTu(App.session);
            table_duBao.getItems().clear();
            table_duBao.setItems(FXCollections.observableArrayList(ds));
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }

}
