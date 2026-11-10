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
import entity.Quyen;
import util.QuyenUtil;
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
        b.setGraphic(new ImageView(new Image(url.toString(), 30, 30, true, true)));
    }

    @FXML
    private void initialize() {
        App.ganManHinhChinh(this);
        SessionContext s = App.session;
        String ten = s == null ? "" : s.getHoTen();
        String[] tu = ten.trim().split("\s+");
        lb_avatar.setText(tu[0].isEmpty() ? "?" :
                (tu[0].substring(0, 1) + (tu.length > 1 ? tu[tu.length - 1].substring(0, 1) : "")).toUpperCase());
        lb_chipTen.setText(ten);
        lb_chipVaiTro.setText(s == null || s.getChucVu() == null ? "—" : s.getChucVu().toString());
        them("Tổng quan", "icon_house.png", "GD_TongQuan", Quyen.XEM_TONG_QUAN);
        them("Hồ sơ bệnh nhân", "icon_folder.png", "GD_QLBenhNhan", Quyen.QL_HO_SO_BENH_NHAN);
        them("Tra cứu hồ sơ bệnh nhân", "icon_search.png", "GD_TraCuuBenhNhan", Quyen.TRA_CUU_BENH_NHAN);
        them("Thu tiền (phí + dịch vụ)", "icon_dollar.png", "GD_ThuTien", Quyen.THU_TIEN);
        them("Lập phiếu khám", "icon_clipboard.png", "GD_LapPhieuKham", Quyen.LAP_PHIEU_KHAM);
        them("Quản lý ca thu ngân", "icon_receipt.png", "GD_ChotCa", Quyen.QL_CA_THU_NGAN);
        them("Hàng chờ theo phòng", "icon_clock.png", "GD_HangCho", Quyen.XEM_HANG_CHO);
        them("Khám và chẩn đoán", "icon_stethoscope.png", "GD_KhamBenh", Quyen.KHAM_BENH);
        them("Lịch sử khám", "icon_history.png", "GD_LichSuKham", Quyen.XEM_LICH_SU_KHAM);
        them("Quản lý hàng hóa", "icon_folder.png", "GD_QLHangHoa", Quyen.QL_HANG_HOA);
        them("Quản lý nhân viên", "icon_users.png", "GD_QLNhanVien", Quyen.QL_NHAN_VIEN);
        them("Quản lý dịch vụ", "icon_tooth.png", "GD_QLDichVu", Quyen.QL_DICH_VU);
        them("Báo cáo lượt khám", "icon_chart.png", "GD_BaoCao", Quyen.XEM_BAO_CAO);
    }

    /** (App.quayVeTrangChu) Ve man Tong quan. */
    public void moTongQuan() {
        moFxml("GD_TongQuan");
    }

    /** Dang xuat: ve man dang nhap. */
    @FXML
    private void dangXuat() {
        App.dangXuat();
    }

    /** Nap FXML vao khung giua + to sang muc menu tuong ung. */
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

        private void them(String tieuDe, String iconFile, String fxml, Quyen quyen) {
        if (!QuyenUtil.coQuyen(App.session, quyen)) {
            return;
        }
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

    @FXML
    private void dongUngDung() {
        App.dongUngDung();
    }
}
