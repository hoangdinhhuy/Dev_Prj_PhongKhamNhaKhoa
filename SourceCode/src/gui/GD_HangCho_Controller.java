package gui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
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
import util.FormatUtil;
import util.MaThongBao;

/** UC009 + PT-05 - Hang cho, goi so, vao kham, quan ly phong (chu file: Thanh). */
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

    private Timeline timeline;

    @FXML
    private void initialize() {
        setupTable(cl_stt, cl_ma, cl_bn, cl_lyDo, cl_tt);
        setupTable(cl_stt2, cl_ma2, cl_bn2, cl_lyDo2, null);
        napPhong();
        // Refresh tu dong moi 10 giay (le tan lap phieu -> hang cho tu thay)
        timeline = new Timeline(new KeyFrame(Duration.seconds(10), e -> taiLai()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        // Dung timeline khi cua so dong
        cbb_phong.sceneProperty().addListener((o, cu, sc) -> {
            if (sc != null && sc.getWindow() != null) {
                sc.getWindow().setOnHiding(e -> timeline.stop());
            }
        });
        taiLai();
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
            tt.setCellFactory(col -> new javafx.scene.control.TableCell<>() {
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
        }
    }

    private void napPhong() {
        var ds = PhongKham_BUS.danhSachPhongDangNhanBenh(App.session);
        cbb_phong.setItems(FXCollections.observableArrayList(ds));
        if (!ds.isEmpty() && cbb_phong.getValue() == null) {
            cbb_phong.setValue(ds.get(0));
        }
        cbb_phong.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
            @Override protected void updateItem(PhongKham p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p == null ? null : p.getMaPhong() + " - " + p.getTenPhong());
            }
        });
        cbb_phong.setButtonCell(cbb_phong.getCellFactory().call(null));
        try {
            cbb_allPhong.setItems(FXCollections.observableArrayList(PhongKham_BUS.danhSachTatCaPhong(App.session)));
            cbb_allPhong.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
                @Override protected void updateItem(PhongKham p, boolean empty) {
                    super.updateItem(p, empty);
                    setText(empty || p == null ? null : p.getMaPhong() + " - " + p.getTenPhong()
                            + (p.isDangNhanBenh() ? " [đang nhận]" : " [tắt]"));
                }
            });
            cbb_allPhong.setButtonCell(cbb_allPhong.getCellFactory().call(null));
        } catch (RuntimeException ignored) {
            // QUAN_LY mo man hinh nay khong co quyen danh sach tong - bo qua
        }
    }

    @FXML
    private void taiLai() {
        PhongKham phong = cbb_phong.getValue();
        if (phong == null) {
            return;
        }
        try {
            var cho = HangCho_BUS.danhSachCho(App.session, java.time.LocalDate.now(), phong.getMaPhong());
            table_hangCho.getItems().clear();
            table_hangCho.setItems(FXCollections.observableArrayList(cho));
            if (!cho.isEmpty()) {
                table_hangCho.getSelectionModel().selectFirst();
            }
            var dang = HangCho_BUS.danhSachDangKham(App.session, java.time.LocalDate.now(), phong.getMaPhong());
            table_dangKham.getItems().clear();
            table_dangKham.setItems(FXCollections.observableArrayList(dang));
            if (cho.isEmpty() && table_hangCho.getUserData() != null) {
                AlertUtil.baoThongTin(MaThongBao.TB07);
                table_hangCho.setUserData(null);
            }
            table_hangCho.setUserData(Boolean.TRUE);
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
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
            lb_soDangGoi.setText(String.valueOf(pk.getSoThuTu()));
            lb_phongDangGoi.setText("Phòng " + pk.getMaPhong() + " - " + pk.getTenBenhNhan());
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
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
            AlertUtil.baoThongTin("Đã đưa " + chon.getTenBenhNhan() + " vào khám (DANG_KHAM).");
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
            PhongKham_BUS.capNhatNhanBenh(App.session, p.getMaPhong(), bat);
            AlertUtil.baoThongTin("Phòng " + p.getMaPhong() + (bat ? " đã bật nhận bệnh." : " đã tắt nhận bệnh."));
            napPhong();
            taiLai();
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }
}
