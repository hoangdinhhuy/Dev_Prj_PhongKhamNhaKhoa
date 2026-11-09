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
 * Màn hình chính điều hướng (1 cửa sổ duy nhất).
 * Đã lược bỏ toàn bộ các màn hình UC không liên quan,
 * CHỈ GIỮ LẠI các phân hệ Quản lý theo yêu cầu:
 * - Tổng quan (Dashboard trực quan hóa)
 * - Quản lý nhân viên & tài khoản (UC010)
 * - Quản lý danh mục dịch vụ (UC007)
 * - Quản lý hàng hóa / tồn kho
 * - Báo cáo lượt khám & xuất CSV (UC008)
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

    private void doiIcon(String fxml, boolean dangChon) {
        Button b = nutMap.get(fxml);
        if (b == null) {
            return;
        }
        String iconFile = iconMap.get(fxml);
        if (iconFile == null) {
            return;
        }
        String ten = dangChon ? iconFile.replace(".png", "_w.png") : iconFile;
        java.net.URL url = App.class.getResource("/pic/" + ten);
        if (url == null) {
            return;
        }
        b.setGraphic(new ImageView(new Image(url.toString(), 28, 28, true, true)));
    }

    @FXML
    private void initialize() {
        App.ganManHinhChinh(this);
        SessionContext s = App.session;
        String ten = s == null ? "Quản lý" : s.getHoTen();
        String[] tu = ten.trim().split("\\s+");
        lb_avatar.setText(tu[0].isEmpty() ? "QL" :
                (tu[0].substring(0, 1) + (tu.length > 1 ? tu[tu.length - 1].substring(0, 1) : "")).toUpperCase());
        lb_chipTen.setText(ten);
        lb_chipVaiTro.setText(s == null || s.getChucVu() == null ? "Quản lý" : s.getChucVu().toString());

        // CHỈ GIỮ CÁC MÀN HÌNH THEO ĐÚNG YÊU CẦU:
        them("Tổng quan (Dashboard)", "icon_house.png", "GD_TongQuan", ChucVu.QUAN_LY, ChucVu.BAC_SI, ChucVu.LE_TAN, ChucVu.PHU_TA);
        them("Quản lý nhân viên & tài khoản", "icon_users.png", "GD_QLNhanVien", ChucVu.QUAN_LY);
        them("Danh mục dịch vụ", "icon_tooth.png", "GD_QLDichVu", ChucVu.QUAN_LY);
        them("Hàng hóa / Tồn kho", "icon_folder.png", "GD_QLHangHoa", ChucVu.QUAN_LY);
        them("Báo cáo lượt khám & Xuất CSV", "icon_chart.png", "GD_BaoCao", ChucVu.QUAN_LY);

        // Mở màn hình Tổng quan đầu tiên
        moFxml("GD_TongQuan");
    }

    public void moTongQuan() {
        moFxml("GD_TongQuan");
    }

    @FXML
    private void dangXuat() {
        App.dangXuat();
    }

    /** Nạp FXML vào khung giữa + tô sáng mục menu tương ứng. */
    public void moFxml(String tenFxml) {
        try {
            Node node = FXMLLoader.load(App.class.getResource("/gui/" + tenFxml + ".fxml"));
            khungNoiDung.getChildren().setAll(node);
            nutMap.forEach((k, b) -> {
                b.getStyleClass().remove("active");
                doiIcon(k, false);
            });
            Button b = nutMap.get(tenFxml);
            if (b != null) {
                b.getStyleClass().add("active");
                doiIcon(tenFxml, true);
            }
        } catch (IOException ex) {
            AlertUtil.baoLoi("Không mở được màn hình " + tenFxml + ": " + ex.getMessage());
        }
    }

    private void them(String tieuDe, String iconFile, String fxml, ChucVu... choPhep) {
        SessionContext s = App.session;
        // Nếu s == null hoặc vai trò khớp trong danh sách cho phép thì hiển thị
        boolean hopLe = (s == null);
        if (s != null) {
            for (ChucVu cv : choPhep) {
                if (cv == s.getChucVu()) {
                    hopLe = true;
                    break;
                }
            }
        }

        if (hopLe) {
            Button b = new Button(tieuDe);
            b.setWrapText(true);
            java.net.URL url = App.class.getResource("/pic/" + iconFile);
            if (url != null) {
                b.setGraphic(new ImageView(new Image(url.toString(), 26, 26, true, true)));
            }
            b.getStyleClass().add("menu-btn");
            b.setMaxWidth(Double.MAX_VALUE);
            b.setOnAction(e -> moFxml(fxml));
            vb_menu.getChildren().add(b);
            nutMap.put(fxml, b);
            iconMap.put(fxml, iconFile);
        }
    }

    @FXML
    private void dongUngDung() {
        App.dongUngDung();
    }
}
