package gui;

import bus.HangHoa_BUS;
import entity.HangHoa;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import main.App;
import util.AlertUtil;
import util.MaThongBao;

public class GD_QLHangHoa_Controller {
    @FXML private TableView<HangHoa> table_hangHoa;
    @FXML private TableColumn<HangHoa, String> cl_ma, cl_ten, cl_donVi, cl_soLuong, cl_mucToiThieu, cl_canhBao;
    @FXML private TextField txt_ten, txt_donVi, txt_soLuong, txt_mucToiThieu;

    @FXML private void initialize() {
        cl_ma.setCellValueFactory(new PropertyValueFactory<>("maHangHoa"));
        cl_ten.setCellValueFactory(new PropertyValueFactory<>("tenHangHoa"));
        cl_donVi.setCellValueFactory(new PropertyValueFactory<>("donViTinh"));
        cl_soLuong.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getSoLuongTon())));
        cl_mucToiThieu.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getMucTonToiThieu())));
        cl_canhBao.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getSoLuongTon() <= c.getValue().getMucTonToiThieu() ? "Cần bổ sung" : "Đủ hàng"));
        table_hangHoa.getSelectionModel().selectedItemProperty().addListener((obs, cu, moi) -> {
            if (moi != null) {
                txt_ten.setText(moi.getTenHangHoa());
                txt_donVi.setText(moi.getDonViTinh());
                txt_soLuong.setText(String.valueOf(moi.getSoLuongTon()));
                txt_mucToiThieu.setText(String.valueOf(moi.getMucTonToiThieu()));
            }
        });
        taiDanhSach();
    }

    private void taiDanhSach() {
        try { table_hangHoa.setItems(FXCollections.observableArrayList(HangHoa_BUS.danhSach(App.session))); }
        catch (RuntimeException ex) { AlertUtil.baoLoi(ex.getMessage()); }
    }

    @FXML private void them() {
        try {
            HangHoa h = HangHoa_BUS.them(App.session, txt_ten.getText(), txt_donVi.getText(),
                    txt_soLuong.getText(), txt_mucToiThieu.getText());
            AlertUtil.baoThongTin("Đã thêm hàng hóa " + h.getMaHangHoa());
            taiDanhSach(); lamMoi();
        } catch (RuntimeException ex) { AlertUtil.baoLoi(ex.getMessage()); }
    }

    @FXML private void capNhat() {
        HangHoa h = table_hangHoa.getSelectionModel().getSelectedItem();
        if (h == null) { AlertUtil.baoLoi(MaThongBao.TB19); return; }
        try {
            HangHoa_BUS.capNhat(App.session, h.getMaHangHoa(), txt_ten.getText(), txt_donVi.getText(),
                    txt_soLuong.getText(), txt_mucToiThieu.getText());
            AlertUtil.baoThongTin("Đã cập nhật hàng hóa " + h.getMaHangHoa());
            taiDanhSach(); lamMoi();
        } catch (RuntimeException ex) { AlertUtil.baoLoi(ex.getMessage()); }
    }

    @FXML private void lamMoi() {
        table_hangHoa.getSelectionModel().clearSelection();
        txt_ten.clear(); txt_donVi.clear(); txt_soLuong.clear(); txt_mucToiThieu.clear();
    }

}
