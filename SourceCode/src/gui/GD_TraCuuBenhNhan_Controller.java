package gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.App;
import bus.BenhNhan_BUS;
import entity.BenhNhan;
import entity.PhieuKham;
import util.AlertUtil;
import util.FormatUtil;
import util.MaThongBao;

/** UC006 - Tra cứu hồ sơ bệnh nhân + lịch sử phiếu khám (chủ file: Thanh). */
public class GD_TraCuuBenhNhan_Controller {

    @FXML private ComboBox<String> cbb_tieuChi;
    @FXML private TextField txt_tuKhoa;
    @FXML private TableView<BenhNhan> table_ketQua;
    @FXML private TableColumn<BenhNhan, String> cl_stt;
    @FXML private TableColumn<BenhNhan, String> cl_ma;
    @FXML private TableColumn<BenhNhan, String> cl_hoTen;
    @FXML private TableColumn<BenhNhan, String> cl_sdt;
    @FXML private TableColumn<BenhNhan, String> cl_cccd;
    @FXML private TableColumn<BenhNhan, String> cl_diaChi;
    @FXML private TableColumn<BenhNhan, String> cl_tienSu;

    @FXML
    private void initialize() {
        cbb_tieuChi.getItems().addAll("Mã", "SĐT", "Họ tên");
        cbb_tieuChi.setValue("Mã");
        cl_stt.setCellValueFactory(cd -> new SimpleStringProperty(
                String.valueOf(table_ketQua.getItems().indexOf(cd.getValue()) + 1)));
        cl_ma.setCellValueFactory(new PropertyValueFactory<>("maBenhNhan"));
        cl_hoTen.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        cl_sdt.setCellValueFactory(new PropertyValueFactory<>("soDienThoai"));
        cl_cccd.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getCccd() == null ? "" : cd.getValue().getCccd()));
        cl_diaChi.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getDiaChi() == null ? "" : cd.getValue().getDiaChi()));
        cl_tienSu.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getTienSuDiUng() == null ? "" : cd.getValue().getTienSuDiUng()));
        table_ketQua.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                xemChiTiet();
            }
        });
        // (Góp ý GV - cách 1) Mở màn: bảng TRỐNG cho tới khi bấm Tìm.
        nap(java.util.List.of());
    }

    /** Không tải toàn bộ: chỉ hiện 50 bệnh nhân mới nhất. */
    @FXML
    private void hienTatCa() {
        try {
            nap(BenhNhan_BUS.danhSachMoiNhat(App.session, 50));
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }

    private void nap(java.util.List<BenhNhan> ds) {
        table_ketQua.getItems().clear();
        table_ketQua.setItems(FXCollections.observableArrayList(ds));
    }

    @FXML
    private void tim() {
        try {
            var ds = BenhNhan_BUS.traCuu(App.session, cbb_tieuChi.getValue(), txt_tuKhoa.getText());
            nap(ds);
            if (ds.isEmpty()) {
                AlertUtil.baoLoi(MaThongBao.TB05);
            }
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }

    /** Chi tiết hồ sơ + lịch sử phiếu khám (double-click). */
    private void xemChiTiet() {
        BenhNhan bn = table_ketQua.getSelectionModel().getSelectedItem();
        if (bn == null) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Mã BN: ").append(bn.getMaBenhNhan()).append("\n");
        sb.append("Họ tên: ").append(bn.getHoTen()).append("\n");
        sb.append("SĐT: ").append(bn.getSoDienThoai()).append("\n");
        sb.append("CCCD: ").append(bn.getCccd() == null ? "(không)" : bn.getCccd()).append("\n");
        sb.append("Ngày sinh: ").append(bn.getNgaySinh() == null ? "(không)"
                : FormatUtil.ngay(bn.getNgaySinh())).append("\n");
        sb.append("Địa chỉ: ").append(bn.getDiaChi() == null ? "(không)" : bn.getDiaChi()).append("\n");
        sb.append("Tiền sử dị ứng: ").append(bn.getTienSuDiUng() == null ? "(không)" : bn.getTienSuDiUng()).append("\n");
        sb.append("\n----- LỊCH SỬ PHIẾU KHÁM -----\n");
        try {
            var ls = BenhNhan_BUS.layLichSuPhieuKham(App.session, bn.getMaBenhNhan());
            if (ls.isEmpty()) {
                sb.append("(chưa có lượt khám nào)\n");
            }
            int lan = 0;
            for (PhieuKham pk : ls) {
                lan++;
                sb.append("Lần ").append(lan).append(": ").append(pk.getMaPhieuKham())
                        .append(" — ").append(FormatUtil.ngay(pk.getNgayKham()))
                        .append(" — ").append(pk.getTrangThai())
                        .append(" — ").append(pk.getChanDoan() == null ? pk.getLyDoKham() : pk.getChanDoan());
                if (pk.getLanThu() > 0) {
                    sb.append(" — lần điều trị thứ ").append(pk.getLanThu());
                }
                if (pk.getPhacDieuTri() != null) {
                    sb.append(" — phác đồ: ").append(pk.getPhacDieuTri());
                }
                sb.append("\n");
            }
        } catch (RuntimeException ex) {
            sb.append("(không đọc được lịch sử: ").append(ex.getMessage()).append(")\n");
        }
        javafx.scene.control.TextArea ta = new javafx.scene.control.TextArea(sb.toString());
        ta.setEditable(false);
        ta.setPrefSize(560, 420);
        Stage stage = new Stage();
        stage.initOwner(table_ketQua.getScene().getWindow());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle("Chi tiết hồ sơ " + bn.getMaBenhNhan() + " — " + bn.getHoTen());
        javafx.scene.Scene sc = new javafx.scene.Scene(ta);
        App.ganTheme(sc);
        stage.setScene(sc);
        stage.showAndWait();
    }
}