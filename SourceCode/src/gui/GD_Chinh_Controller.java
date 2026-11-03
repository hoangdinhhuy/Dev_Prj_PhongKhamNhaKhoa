package gui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import main.App;
import entity.ChucVu;
import entity.SessionContext;
import util.AlertUtil;

/**
 * Man hinh chinh: sidebar + khung noi dung. Bam menu thi FXML tuong ung duoc nap
 * vao khung giua (1 cua so duy nhat), muc menu dang chon duoc to sang.
 */
public class GD_Chinh_Controller {

    @FXML private StackPane khungNoiDung;
    @FXML private VBox vb_menu;
    @FXML private Button btn_dangXuat;
    @FXML private Label lb_avatar;
    @FXML private Label lb_chipTen;
    @FXML private Label lb_chipVaiTro;

    private final Map<String, Button> nutMap = new HashMap<>();
    private final Map<String, String> iconMap = new HashMap<>();
    private String fxmlHienTai;

    private void doiIcon(String fxml, boolean dangChon) {
        Button b = nutMap.get(fxml);
        if (b == null) {
            return;
        }
        String f = iconMap.get(fxml);
        String ten = dangChon ? f.replace(".png", "_w.png") : f;
        b.setGraphic(new ImageView(new Image("/pic/" + ten, 30, 30, true, true)));
    }

    @FXML
    private void initialize() {
        SessionContext s = App.session;
        String ten = s == null ? "" : s.getHoTen();
        String[] tu = ten.trim().split("\\s+");
        lb_avatar.setText(tu[0].isEmpty() ? "?" :
                (tu[0].substring(0, 1) + (tu.length > 1 ? tu[tu.length - 1].substring(0, 1) : "")).toUpperCase());
        lb_chipTen.setText(ten);
        lb_chipVaiTro.setText(s == null || s.getChucVu() == null ? "—" : s.getChucVu().toString());
        them("Tổng quan", "icon_house.png", "GD_TongQuan", ChucVu.LE_TAN, ChucVu.BAC_SI, ChucVu.PHU_TA, ChucVu.QUAN_LY);
        them("Hồ sơ bệnh nhân", "icon_folder.png", "GD_QLBenhNhan", ChucVu.LE_TAN);
        them("Tra cứu hồ sơ bệnh nhân", "icon_search.png", "GD_TraCuuBenhNhan", ChucVu.LE_TAN);
        them("Thu lệ phí khám", "icon_dollar.png", "GD_ThuPhi", ChucVu.LE_TAN);
        them("Lập phiếu khám", "icon_clipboard.png", "GD_LapPhieuKham", ChucVu.LE_TAN);
        them("Thanh toán dịch vụ", "icon_receipt.png", "GD_ThanhToan", ChucVu.LE_TAN);
        them("Hàng chờ theo phòng", "icon_clock.png", "GD_HangCho", ChucVu.PHU_TA);
        them("Khám và chẩn đoán", "icon_stethoscope.png", "GD_KhamBenh", ChucVu.BAC_SI);
        them("Lịch sử khám", "icon_history.png", "GD_LichSuKham", ChucVu.BAC_SI);
        them("Quản lý hàng hóa", "icon_folder.png", "GD_QLHangHoa", ChucVu.QUAN_LY);
        them("Quản lý nhân viên", "icon_users.png", "GD_QLNhanVien", ChucVu.QUAN_LY);
        them("Quản lý dịch vụ", "icon_tooth.png", "GD_QLDichVu", ChucVu.QUAN_LY);
        them("Báo cáo lượt khám", "icon_chart.png", "GD_BaoCao", ChucVu.QUAN_LY);
        moFxml("GD_TongQuan");
    }

    /** Nap FXML vao khung giua + to sang muc menu tuong ung. */
    public void moFxml(String tenFxml) {
        try {
            Node node = FXMLLoader.load(App.class.getResource("/gui/" + tenFxml + ".fxml"));
            khungNoiDung.getChildren().setAll(node);
            fxmlHienTai = tenFxml;
            nutMap.forEach((k, b) -> {
                b.getStyleClass().remove("active");
                doiIcon(k, false);
            });
            Button nut = nutMap.get(tenFxml);
            if (nut != null) {
                nut.getStyleClass().add("active");
                doiIcon(tenFxml, true);
            }
        } catch (IOException ex) {
            AlertUtil.baoLoi("Không mở được màn hình " + tenFxml + ": " + ex.getMessage());
        }
    }

    private void them(String tieuDe, String iconFile, String fxml, ChucVu... choPhep) {
        SessionContext s = App.session;
        for (ChucVu cv : choPhep) {
            if (s != null && cv == s.getChucVu()) {
                Button b = new Button(tieuDe);
                b.setWrapText(true);
                ImageView iv = new ImageView(new Image("/pic/" + iconFile, 200, 200, true, true));
                b.setGraphic(iv);
                b.getStyleClass().add("menu-btn");
                b.setMaxWidth(Double.MAX_VALUE);
                b.setOnAction(e -> moFxml(fxml));
                vb_menu.getChildren().add(b);
                nutMap.put(fxml, b);
                iconMap.put(fxml, iconFile);
                return;
            }
        }
    }

    @FXML
    private void dongUngDung() {
        App.dongUngDung();
    }

    @FXML
    private void dangXuat() {
        App.session = null;
        App.setRoot("GD_DangNhap");
    }
}
