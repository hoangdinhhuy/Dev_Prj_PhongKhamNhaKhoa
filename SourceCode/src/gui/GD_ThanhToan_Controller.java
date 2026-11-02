package gui;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import main.App;
import bus.HoaDon_BUS;
import dao.ChiDinhDichVu_DAO;
import entity.ChiDinhDichVu;
import entity.HoaDon;
import entity.PhieuKham;
import util.AlertUtil;
import util.FormatUtil;
import util.MaThongBao;

/** LT-06 — Thanh toan dich vu cuoi luot (chu file: Thanh). */
public class GD_ThanhToan_Controller {

    @FXML private TableView<PhieuKham> table_phieu;
    @FXML private TableColumn<PhieuKham, String> cl_ma;
    @FXML private TableColumn<PhieuKham, String> cl_bn;
    @FXML private TableColumn<PhieuKham, String> cl_ngay;
    @FXML private TableView<ChiDinhDichVu> table_dichVu;
    @FXML private TableColumn<ChiDinhDichVu, String> cl_dv;
    @FXML private TableColumn<ChiDinhDichVu, String> cl_gia;
    @FXML private Label lb_thongTin;
    @FXML private Label lb_tong;
    @FXML private ComboBox<String> cbb_hinhThuc;
    @FXML private TextField txt_tienDua;
    @FXML private Label lb_tienThua;

    private PhieuKham phieuChon;
    private BigDecimal tongTien = BigDecimal.ZERO;

    @FXML
    private void initialize() {
        cl_ma.setCellValueFactory(new PropertyValueFactory<>("maPhieuKham"));
        cl_bn.setCellValueFactory(new PropertyValueFactory<>("tenBenhNhan"));
        cl_ngay.setCellValueFactory(cd -> new SimpleStringProperty(FormatUtil.ngay(cd.getValue().getNgayKham())));
        cl_dv.setCellValueFactory(new PropertyValueFactory<>("tenDichVu"));
        cl_gia.setCellValueFactory(cd -> new SimpleStringProperty(FormatUtil.tien(cd.getValue().getDonGia())));
        cbb_hinhThuc.getItems().addAll("TIEN_MAT", "CHUYEN_KHOAN");
        cbb_hinhThuc.setValue("TIEN_MAT");
        txt_tienDua.textProperty().addListener((o, cu, moi) -> capNhatTienThua());
        table_phieu.getSelectionModel().selectedItemProperty().addListener((o, cu, chon) -> napChiTiet());
        taiLai();
    }

    @FXML
    private void taiLai() {
        try {
            var ds = danhSachChoThanhToan();
            table_phieu.getItems().clear();
            table_phieu.setItems(FXCollections.observableArrayList(ds));
            if (!ds.isEmpty()) {
                table_phieu.getSelectionModel().selectFirst();
            } else {
                lb_thongTin.setText("(không có lượt khám nào chờ thanh toán)");
            }
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }

    private java.util.List<PhieuKham> danhSachChoThanhToan() {
        return bus.PhieuKham_BUS.layPhieuHoanTatChuaThanhToan(App.session);
    }

    private void napChiTiet() {
        phieuChon = table_phieu.getSelectionModel().getSelectedItem();
        if (phieuChon == null) {
            return;
        }
        lb_thongTin.setText(phieuChon.getMaPhieuKham() + " - " + phieuChon.getTenBenhNhan()
                + " - " + FormatUtil.ngay(phieuChon.getNgayKham()));
        var cds = ChiDinhDichVu_DAO.layTheoPhieu(phieuChon.getMaPhieuKham());
        table_dichVu.getItems().clear();
        table_dichVu.setItems(FXCollections.observableArrayList(cds));
        tongTien = cds.stream().map(ChiDinhDichVu::getDonGia)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        lb_tong.setText(FormatUtil.tien(tongTien));
    }

    private void capNhatTienThua() {
        try {
            var dua = new BigDecimal(txt_tienDua.getText().trim());
            lb_tienThua.setText(FormatUtil.tien(dua.subtract(tongTien)));
        } catch (Exception ex) {
            lb_tienThua.setText("");
        }
    }

    @FXML
    private void thanhToan() {
        if (phieuChon == null) {
            AlertUtil.baoLoi(MaThongBao.VL05);
            return;
        }
        try {
            if (!AlertUtil.xacNhan("Thanh toán " + FormatUtil.tien(tongTien)
                    + " cho lượt khám " + phieuChon.getMaPhieuKham() + "?")) {
                return;
            }
            HoaDon hd = HoaDon_BUS.thanhToanDichVu(App.session, phieuChon.getMaPhieuKham(),
                    cbb_hinhThuc.getValue(), txt_tienDua.getText());
            AlertUtil.baoThongTin("Thanh toán thành công. Hóa đơn " + hd.getMaHoaDon()
                    + " - " + FormatUtil.tien(hd.getTongTien()));
            txt_tienDua.clear();
            taiLai();
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }
}
