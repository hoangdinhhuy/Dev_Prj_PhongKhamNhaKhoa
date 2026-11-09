package gui;

import java.util.ArrayList;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import main.App;
import bus.HangHoa_BUS;
import entity.HangHoa;
import util.AlertUtil;
import util.MaThongBao;

/**
 * Controller Quản lý hàng hóa / Tồn kho.
 * Tuân thủ quy chuẩn:
 * - Ẩn / để trống danh sách ban đầu cho đến khi người dùng tìm kiếm.
 * - Tra cứu theo từ khóa và tiêu chí tình trạng tồn kho.
 * - Xử lý trực tiếp trên đối tượng Entity HangHoa, chuẩn OOP.
 */
public class GD_QLHangHoa_Controller {

    @FXML private TableView<HangHoa> table_hangHoa;
    @FXML private TableColumn<HangHoa, String> cl_ma;
    @FXML private TableColumn<HangHoa, String> cl_ten;
    @FXML private TableColumn<HangHoa, String> cl_donVi;
    @FXML private TableColumn<HangHoa, String> cl_soLuong;
    @FXML private TableColumn<HangHoa, String> cl_mucToiThieu;
    @FXML private TableColumn<HangHoa, String> cl_canhBao;

    @FXML private TextField txt_timKiem;
    @FXML private ComboBox<String> cbb_locTinhTrang;
    @FXML private Label lb_thongKeHangHoa;

    @FXML private TextField txt_maHang;
    @FXML private TextField txt_ten;
    @FXML private TextField txt_donVi;
    @FXML private TextField txt_soLuong;
    @FXML private TextField txt_mucToiThieu;
    @FXML private TextField txt_tinhTrangHienTai;

    @FXML
    private void initialize() {
        cl_ma.setCellValueFactory(new PropertyValueFactory<>("maHangHoa"));
        cl_ten.setCellValueFactory(new PropertyValueFactory<>("tenHangHoa"));
        cl_donVi.setCellValueFactory(new PropertyValueFactory<>("donViTinh"));
        cl_soLuong.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getSoLuongTon())));
        cl_mucToiThieu.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getMucTonToiThieu())));

        // Thuộc tính dẫn xuất tính toán dựa trên đối tượng OOP
        cl_canhBao.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTinhTrangTonKho()));
        cl_canhBao.setCellFactory(column -> new TableCell<HangHoa, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.contains("Cần bổ sung")) {
                        setStyle("-fx-text-fill: #C0392B; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #1E7A46; -fx-font-weight: bold;");
                    }
                }
            }
        });

        // Bộ lọc tình trạng
        cbb_locTinhTrang.getItems().addAll("Tất cả hàng hóa", "Cần bổ sung", "Đủ hàng (An toàn)");
        cbb_locTinhTrang.setValue("Tất cả hàng hóa");

        // Bắt sự kiện chọn dòng trên bảng
        table_hangHoa.getSelectionModel().selectedItemProperty().addListener((obs, cu, moi) -> {
            if (moi != null) {
                doForm(moi);
            }
        });

        // QUY CHUẨN: Ẩn/để trống dữ liệu ban đầu cho đến khi người dùng tìm kiếm
        table_hangHoa.getItems().clear();
        lb_thongKeHangHoa.setText("Chưa tìm kiếm (Danh sách đang để trống)");
    }

    private void doForm(HangHoa h) {
        txt_maHang.setText(h.getMaHangHoa());
        txt_ten.setText(h.getTenHangHoa());
        txt_donVi.setText(h.getDonViTinh());
        txt_soLuong.setText(String.valueOf(h.getSoLuongTon()));
        txt_mucToiThieu.setText(String.valueOf(h.getMucTonToiThieu()));
        txt_tinhTrangHienTai.setText(h.getTinhTrangTonKho());
    }

    /**
     * Tra cứu theo từ khóa và tình trạng tồn kho.
     */
    @FXML
    public void timKiem() {
        String tuKhoa = txt_timKiem.getText();
        String locChon = cbb_locTinhTrang.getValue();
        String tinhTrang = "TAT_CA";
        if ("Cần bổ sung".equals(locChon)) {
            tinhTrang = "CAN_BO_SUNG";
        } else if ("Đủ hàng (An toàn)".equals(locChon)) {
            tinhTrang = "DU_HANG";
        }

        try {
            ArrayList<HangHoa> ketQua = HangHoa_BUS.timKiemHangHoa(App.session, tuKhoa, tinhTrang);
            capNhatBang(ketQua);
            lb_thongKeHangHoa.setText("Tìm thấy: " + ketQua.size() + " mặt hàng phù hợp");
            if (ketQua.isEmpty()) {
                AlertUtil.baoThongTin("Không tìm thấy hàng hóa nào phù hợp với điều kiện tìm kiếm.");
            }
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi("Lỗi tra cứu: " + ex.getMessage());
        }
    }

    private void capNhatBang(ArrayList<HangHoa> ds) {
        table_hangHoa.getItems().clear();
        table_hangHoa.setItems(FXCollections.observableArrayList(ds));
    }

    @FXML
    private void them() {
        try {
            HangHoa h = HangHoa_BUS.them(App.session, txt_ten.getText(), txt_donVi.getText(),
                    txt_soLuong.getText(), txt_mucToiThieu.getText());
            AlertUtil.baoThongTin("Đã thêm thành công hàng hóa: " + h.getMaHangHoa() + " - " + h.getTenHangHoa());
            timKiem();
            lamMoi();
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }

    @FXML
    private void capNhat() {
        HangHoa chon = table_hangHoa.getSelectionModel().getSelectedItem();
        if (chon == null) {
            AlertUtil.baoLoi(MaThongBao.TB19);
            return;
        }
        try {
            HangHoa_BUS.capNhat(App.session, chon.getMaHangHoa(), txt_ten.getText(), txt_donVi.getText(),
                    txt_soLuong.getText(), txt_mucToiThieu.getText());
            AlertUtil.baoThongTin("Đã cập nhật hàng hóa " + chon.getMaHangHoa());
            timKiem();
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }

    @FXML
    private void lamMoi() {
        txt_maHang.clear();
        txt_ten.clear();
        txt_donVi.clear();
        txt_soLuong.clear();
        txt_mucToiThieu.clear();
        txt_tinhTrangHienTai.clear();
        table_hangHoa.getSelectionModel().clearSelection();
    }
}
