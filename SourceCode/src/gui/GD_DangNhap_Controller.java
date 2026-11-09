package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import main.App;
import bus.TaiKhoan_BUS;
import entity.SessionContext;

/** UC001 - Dang nhap (chu file: Duy). Dang nhap dung -> man hinh chinh. */
public class GD_DangNhap_Controller {

    @FXML private TextField txt_taiKhoan;
    @FXML private PasswordField txt_matKhau;
    @FXML private TextField txt_matKhauHien;
    @FXML private Button btn_eye;
    @FXML private ImageView img_eye;
    @FXML private Button btn_dangNhap;
    @FXML private Label lb_thongBao;

    private boolean hienMatKhau = false;

    @FXML
    private void initialize() {
        // Mac dinh tai khoan Quan ly (NV0007 - Nguyen Tuan Duy)
        txt_taiKhoan.setText("NV0007");
        txt_matKhau.setText("123456");
    }

    @FXML
    private void anHienMatKhau() {
        hienMatKhau = !hienMatKhau;
        if (hienMatKhau) {
            txt_matKhauHien.setText(txt_matKhau.getText());
            txt_matKhauHien.setVisible(true);
            txt_matKhauHien.setManaged(true);
            txt_matKhau.setVisible(false);
            txt_matKhau.setManaged(false);
        } else {
            txt_matKhau.setText(txt_matKhauHien.getText());
            txt_matKhau.setVisible(true);
            txt_matKhau.setManaged(true);
            txt_matKhauHien.setVisible(false);
            txt_matKhauHien.setManaged(false);
        }
    }

    @FXML
    private void dangNhap() {
        try {
            SessionContext s = TaiKhoan_BUS.dangNhap(txt_taiKhoan.getText().trim(),
                    hienMatKhau ? txt_matKhauHien.getText() : txt_matKhau.getText());
            App.session = s;
            App.setRoot("GD_Chinh");
            App.maximizeChinh();
        } catch (RuntimeException ex) {
            lb_thongBao.setText(ex.getMessage());
        }
    }

    @FXML
    private void dongUngDung() {
        Stage stage = (Stage) btn_dangNhap.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void quenMatKhau() {
        lb_thongBao.setText("Vui lòng liên hệ quản lý để được cấp lại mật khẩu.");
    }
}
