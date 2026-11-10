package gui;

import java.time.LocalDate;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import main.App;
import bus.HangCho_BUS;
import bus.PhongKham_BUS;
import entity.PhieuKham;
import entity.PhongKham;
import util.AlertUtil;
import util.MaThongBao;

/** UC009 + PT-05 - Hàng chờ, gọi số, vào khám, bật/tắt nhận bệnh. */
public class GD_HangCho_Controller {

    @FXML private ComboBox<PhongKham> cbb_phong;
    @FXML private ComboBox<PhongKham> cbb_allPhong;
    @FXML private TableView<PhieuKham> table_hangCho;
    @FXML private TableView<PhieuKham> table_dangKham;
    @FXML private TableColumn<PhieuKham, String> cl_stt;
    @FXML private TableColumn<PhieuKham, String> cl_ma;
    @FXML private TableColumn<PhieuKham, String> cl_bn;
    @FXML private TableColumn<PhieuKham, String> cl_lyDo;
    @FXML private TableColumn<PhieuKham, String> cl_tt;
    @FXML private TableColumn<PhieuKham, String> cl_stt2;
    @FXML private TableColumn<PhieuKham, String> cl_ma2;
    @FXML private TableColumn<PhieuKham, String> cl_bn2;
    @FXML private TableColumn<PhieuKham, String> cl_lyDo2;
    @FXML private Label lb_soDangGoi;
    @FXML private Label lb_phongDangGoi;
    @FXML private Button btn_goSo;
    @FXML private Button btn_xacNhanVaoKham;
    @FXML private Button btn_taiLai;
    @FXML private Button btn_goiLai;

    private Timeline timeline;
    private PhieuKham phieuVuaGoi;

    @FXML
    private void initialize() {
        setupTable(cl_stt, cl_ma, cl_bn, cl_lyDo, cl_tt);
        setupTable(cl_stt2, cl_ma2, cl_bn2, cl_lyDo2, null);
        setupCombo(cbb_phong, false);
        setupCombo(cbb_allPhong, true);

        // Chỉ bấm được khi đã chọn một dòng ở bảng hàng chờ
        btn_goSo.disableProperty().bind(table_hangCho.getSelectionModel().selectedItemProperty().isNull());
        btn_xacNhanVaoKham.disableProperty().bind(table_hangCho.getSelectionModel().selectedItemProperty().isNull());

        napPhong();
        cbb_phong.valueProperty().addListener((o, cu, moi) -> {
            phieuVuaGoi = null;
            btn_goiLai.setDisable(true);
            lb_soDangGoi.setText("—");
            lb_phongDangGoi.setText("");
            table_hangCho.setUserData(Boolean.FALSE);
            taiLai();
        });
        // Làm mới mỗi 10 giây (lễ tân lập phiếu -> hàng chờ tự thấy). Dòng đang chọn được giữ nguyên.
        timeline = new Timeline(new KeyFrame(Duration.seconds(10), e -> taiLai()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        cbb_phong.sceneProperty().addListener((o, cu, sc) -> {
            if (sc == null) timeline.stop();
            else timeline.play();
        });
        taiLai();
    }

    private void setupCombo(ComboBox<PhongKham> cbb, boolean hienTrangThai) {
        cbb.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(PhongKham p, boolean empty) {
                super.updateItem(p, empty);
                if (empty || p == null) {
                    setText(null);
                } else {
                    String s = p.getMaPhong() + " - " + p.getTenPhong();
                    if (hienTrangThai) s += p.isDangNhanBenh() ? " [đang nhận]" : " [đã tắt]";
                    setText(s);
                }
            }
        });
        cbb.setButtonCell(cbb.getCellFactory().call(null));
    }

    private void setupTable(TableColumn<PhieuKham, String> stt, TableColumn<PhieuKham, String> ma,
                            TableColumn<PhieuKham, String> bn, TableColumn<PhieuKham, String> lyDo,
                            TableColumn<PhieuKham, String> tt) {
        stt.setCellValueFactory(cd -> new SimpleStringProperty(String.valueOf(cd.getValue().getSoThuTu())));
        ma.setCellValueFactory(new PropertyValueFactory<>("maPhieuKham"));
        bn.setCellValueFactory(new PropertyValueFactory<>("tenBenhNhan"));
        lyDo.setCellValueFactory(new PropertyValueFactory<>("lyDoKham"));
        if (tt != null) {
            tt.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getTrangThai().toString()));
            tt.setCellFactory(col -> new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    getStyleClass().removeAll("badge-cho", "badge-dang", "badge-hoan");
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item);
                        getStyleClass().add(switch (item) {
                            case "Chờ khám" -> "badge-cho";
                            case "Đang khám" -> "badge-dang";
                            default -> "badge-hoan";
                        });
                    }
                }
            });
        }
    }

    /** Nạp lại 2 combo, giữ phòng đang chọn nếu còn. */
    private void napPhong() {
        String maXem = cbb_phong.getValue() == null ? null : cbb_phong.getValue().getMaPhong();
        List<PhongKham> dsXem = PhongKham_BUS.danhSachPhongPhuTrachDangNhan(App.session);
        cbb_phong.setItems(FXCollections.observableArrayList(dsXem));
        cbb_phong.setValue(dsXem.stream().filter(p -> p.getMaPhong().equals(maXem))
                .findFirst().orElse(dsXem.isEmpty() ? null : dsXem.get(0)));

        String maBatTat = cbb_allPhong.getValue() == null ? null : cbb_allPhong.getValue().getMaPhong();
        try {
            List<PhongKham> dsBatTat = PhongKham_BUS.danhSachPhongBatTat(App.session);
            cbb_allPhong.setItems(FXCollections.observableArrayList(dsBatTat));
            // Mặc định chọn sẵn phòng đang xem ở trên
            String uuTien = maBatTat != null ? maBatTat : maXem;
            cbb_allPhong.setValue(dsBatTat.stream().filter(p -> p.getMaPhong().equals(uuTien))
                    .findFirst().orElse(dsBatTat.isEmpty() ? null : dsBatTat.get(0)));
        } catch (RuntimeException ex) {
            cbb_allPhong.getItems().clear();
        }
    }

    @FXML
    private void taiLai() {
        PhongKham phong = cbb_phong.getValue();
        if (phong == null) {
            table_hangCho.getItems().clear();
            table_dangKham.getItems().clear();
            table_hangCho.setUserData(Boolean.FALSE);
            return;
        }
        PhieuKham dangChon = table_hangCho.getSelectionModel().getSelectedItem();
        String maDangChon = dangChon == null ? null : dangChon.getMaPhieuKham();
        try {
            var cho = HangCho_BUS.danhSachCho(App.session, LocalDate.now(), phong.getMaPhong());
            table_hangCho.setItems(FXCollections.observableArrayList(cho));
            chonLai(maDangChon, cho);
            var dang = HangCho_BUS.danhSachDangKham(App.session, LocalDate.now(), phong.getMaPhong());
            table_dangKham.setItems(FXCollections.observableArrayList(dang));
            if (cho.isEmpty() && Boolean.TRUE.equals(table_hangCho.getUserData())) {
                AlertUtil.baoThongTin(MaThongBao.TB07);
            }
            table_hangCho.setUserData(!cho.isEmpty());
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }

    /** Giữ đúng dòng đang chọn sau khi làm mới; nếu dòng đó đã biến mất thì chọn dòng đầu. */
    private void chonLai(String maPhieu, List<PhieuKham> ds) {
        if (ds.isEmpty()) return;
        if (maPhieu != null) {
            for (int i = 0; i < ds.size(); i++) {
                if (maPhieu.equals(ds.get(i).getMaPhieuKham())) {
                    table_hangCho.getSelectionModel().select(i);
                    return;
                }
            }
        }
        table_hangCho.getSelectionModel().selectFirst();
    }

    @FXML
    private void goiSo() {
        PhieuKham chon = table_hangCho.getSelectionModel().getSelectedItem();
        if (chon == null) {
            AlertUtil.baoLoi(MaThongBao.VL05);
            return;
        }
        try {
            PhieuKham pk = HangCho_BUS.goiSo(App.session, chon.getMaPhieuKham());
            phieuVuaGoi = pk;
            btn_goiLai.setDisable(false);
            lb_soDangGoi.setText(String.valueOf(pk.getSoThuTu()));
            lb_phongDangGoi.setText("Phòng " + pk.getMaPhong() + " - " + pk.getTenBenhNhan());
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }

    @FXML
    private void goiLai() {
        if (phieuVuaGoi == null) return;
        try {
            PhieuKham pk = HangCho_BUS.goiSo(App.session, phieuVuaGoi.getMaPhieuKham());
            lb_soDangGoi.setText(String.valueOf(pk.getSoThuTu()));
            lb_phongDangGoi.setText("Phòng " + pk.getMaPhong() + " - " + pk.getTenBenhNhan());
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
            phieuVuaGoi = null;
            btn_goiLai.setDisable(true);
        }
    }

    @FXML
    private void xacNhanVaoKham() {
        PhieuKham chon = table_hangCho.getSelectionModel().getSelectedItem();
        if (chon == null) {
            AlertUtil.baoLoi(MaThongBao.VL05);
            return;
        }
        try {
            HangCho_BUS.tiepNhanVaoKham(App.session, chon.getMaPhieuKham());
            AlertUtil.baoThongTin("Đã đưa " + chon.getTenBenhNhan() + " vào khám.");
            taiLai();
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
            taiLai();
        }
    }

    @FXML
    private void batNhanBenh() {
        doiTrangThaiPhong(true);
    }

    @FXML
    private void tatNhanBenh() {
        doiTrangThaiPhong(false);
    }

    private void doiTrangThaiPhong(boolean bat) {
        PhongKham p = cbb_allPhong.getValue();
        if (p == null) {
            AlertUtil.baoLoi("Chọn phòng ở danh sách bên dưới.");
            return;
        }
        try {
            if (!bat) {
                int dangCho = PhongKham_BUS.demPhieuDangCho(App.session, p.getMaPhong());
                if (dangCho > 0) {
                    Alert hoi = new Alert(Alert.AlertType.CONFIRMATION,
                            "Phòng " + p.getMaPhong() + " còn " + dangCho
                                    + " bệnh nhân đang chờ khám. Vẫn tắt nhận bệnh?",
                            ButtonType.YES, ButtonType.NO);
                    hoi.setHeaderText(null);
                    if (hoi.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) {
                        return;
                    }
                }
            }
            PhongKham_BUS.capNhatNhanBenh(App.session, p.getMaPhong(), bat);
            AlertUtil.baoThongTin("Phòng " + p.getMaPhong() + (bat ? " đã bật nhận bệnh." : " đã tắt nhận bệnh."));
            napPhong();
            taiLai();
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }
}