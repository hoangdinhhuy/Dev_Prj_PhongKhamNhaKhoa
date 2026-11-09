package gui;

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
import bus.DichVu_BUS;
import entity.DichVu;
import entity.TrangThaiDichVu;
import util.AlertUtil;

/**
 * Controller Quản lý danh mục dịch vụ (UC007).
 * Tuân thủ quy chuẩn:
 * - Ẩn / để trống danh sách ban đầu cho đến khi người dùng tìm kiếm.
 * - Tra cứu theo từ khóa và trạng thái phục vụ.
 * - Xử lý trực tiếp trên đối tượng Entity DichVu, chuẩn OOP.
 */
public class GD_QLDichVu_Controller {

    @FXML private TableView<DichVu> table_dichVu;
    @FXML private TableColumn<DichVu, String> cl_stt;
    @FXML private TableColumn<DichVu, String> cl_ma;
    @FXML private TableColumn<DichVu, String> cl_ten;
    @FXML private TableColumn<DichVu, String> cl_gia;
    @FXML private TableColumn<DichVu, String> cl_tt;

    @FXML private TextField txt_timKiem;
    @FXML private ComboBox<String> cbb_locTrangThai;
    @FXML private Label lb_thongKeDichVu;

    @FXML private TextField txt_maDichVu;
    @FXML private TextField txt_tenDichVu;
    @FXML private TextField txt_donGia;
    @FXML private ComboBox<TrangThaiDichVu> cbb_trangThai;

    @FXML
    private void initialize() {
        cl_stt.setCellValueFactory(cd -> new SimpleStringProperty(
                String.valueOf(table_dichVu.getItems().indexOf(cd.getValue()) + 1)));
        cl_ma.setCellValueFactory(new PropertyValueFactory<>("maDichVu"));
        cl_ten.setCellValueFactory(new PropertyValueFactory<>("tenDichVu"));
        cl_gia.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getDonGiaDinhDang()));
        cl_tt.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getTrangThaiString()));

        cbb_trangThai.getItems().addAll(TrangThaiDichVu.values());
        cbb_trangThai.setValue(TrangThaiDichVu.DANG_PHUC_VU);

        // Bộ lọc trạng thái tra cứu
        cbb_locTrangThai.getItems().addAll("Tất cả", "Đang phục vụ", "Ngừng phục vụ");
        cbb_locTrangThai.setValue("Tất cả");

        // Bắt sự kiện chọn dòng trên bảng
        table_dichVu.getSelectionModel().selectedItemProperty().addListener((obs, cu, moi) -> {
            if (moi != null) {
                doForm(moi);
            }
        });

        // QUY CHUẨN: Ẩn/để trống dữ liệu ban đầu cho đến khi người dùng tìm kiếm
        table_dichVu.getItems().clear();
        lb_thongKeDichVu.setText("Chưa tìm kiếm (Danh sách đang để trống)");
    }

    private void doForm(DichVu dv) {
        txt_maDichVu.setText(dv.getMaDichVu());
        txt_tenDichVu.setText(dv.getTenDichVu());
        txt_donGia.setText(dv.getDonGiaNiemYet() == null ? "" : dv.getDonGiaNiemYet().toPlainString());
        cbb_trangThai.setValue(dv.getTrangThai());
    }

    /**
     * Tra cứu theo từ khóa (Tên/Mã) và trạng thái.
     */
    @FXML
    public void timKiem() {
        String tuKhoa = txt_timKiem.getText();
        TrangThaiDichVu ttLoc = null;
        String chonTT = cbb_locTrangThai.getValue();
        if ("Đang phục vụ".equals(chonTT)) {
            ttLoc = TrangThaiDichVu.DANG_PHUC_VU;
        } else if ("Ngừng phục vụ".equals(chonTT)) {
            ttLoc = TrangThaiDichVu.NGUNG_PHUC_VU;
        }

        try {
            ArrayList<DichVu> ketQua = DichVu_BUS.timKiemDichVu(App.session, tuKhoa, ttLoc);
            capNhatBang(ketQua);
            lb_thongKeDichVu.setText("Tìm thấy: " + ketQua.size() + " dịch vụ phù hợp");
            if (ketQua.isEmpty()) {
                AlertUtil.baoThongTin("Không tìm thấy dịch vụ nào phù hợp với từ khóa.");
            }
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi("Lỗi tìm kiếm: " + ex.getMessage());
        }
    }

    private void capNhatBang(ArrayList<DichVu> ds) {
        table_dichVu.getItems().clear();
        table_dichVu.setItems(FXCollections.observableArrayList(ds));
    }

    @FXML
    private void them() {
        try {
            DichVu dv = DichVu_BUS.themDichVu(App.session, txt_tenDichVu.getText(), txt_donGia.getText());
            AlertUtil.baoThongTin("Đã thêm thành công dịch vụ: " + dv.getMaDichVu() + " - " + dv.getTenDichVu());
            timKiem();
            lamMoi();
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }

    @FXML
    private void sua() {
        DichVu chon = table_dichVu.getSelectionModel().getSelectedItem();
        if (chon == null) {
            AlertUtil.baoLoi("Vui lòng chọn dịch vụ trong bảng để cập nhật.");
            return;
        }
        try {
            DichVu_BUS.suaDichVu(App.session, chon, txt_tenDichVu.getText(), txt_donGia.getText());
            AlertUtil.baoThongTin("Đã cập nhật dịch vụ " + chon.getMaDichVu() + " (các hóa đơn cũ vẫn bảo toàn giá snapshot).");
            timKiem();
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }

    @FXML
    private void doiTrangThai() {
        DichVu chon = table_dichVu.getSelectionModel().getSelectedItem();
        if (chon == null) {
            AlertUtil.baoLoi("Vui lòng chọn dịch vụ trong bảng để đổi trạng thái.");
            return;
        }
        TrangThaiDichVu moi = cbb_trangThai.getValue();
        if (moi == null) {
            AlertUtil.baoLoi("Vui lòng chọn trạng thái mới.");
            return;
        }
        try {
            DichVu_BUS.doiTrangThai(App.session, chon, moi);
            AlertUtil.baoThongTin("Đã đổi trạng thái dịch vụ " + chon.getMaDichVu() + " sang: " + moi);
            timKiem();
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }

    @FXML
    private void lamMoi() {
        txt_maDichVu.clear();
        txt_tenDichVu.clear();
        txt_donGia.clear();
        cbb_trangThai.setValue(TrangThaiDichVu.DANG_PHUC_VU);
        table_dichVu.getSelectionModel().clearSelection();
    }
}
