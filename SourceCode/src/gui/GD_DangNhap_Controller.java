package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import main.App;
import bus.TaiKhoan_BUS;
import entity.SessionContext;
import util.AlertUtil;

/** Dang nhap — layout theo thiet ke Figma (panel thuong hieu ben trai). */
public class GD_DangNhap_Controller {

    @FXML private TextField txt_taiKhoan;
    @FXML private PasswordField txt_matKhau;
    @FXML private TextField txt_matKhauHien;
    @FXML private Button btn_eye;
    @FXML private ImageView img_eye;
    @FXML private Label lb_thongBao;

    private boolean dangHien = false;

    @FXML
    private void dongUngDung() {
        App.dongUngDung();
    }

    @FXML
    private void anHienMatKhau(ActionEvent event) {
        if (dangHien) {
            txt_matKhau.setText(txt_matKhauHien.getText());
        } else {
            txt_matKhauHien.setText(txt_matKhau.getText());
        }
        dangHien = !dangHien;
        txt_matKhau.setVisible(!dangHien);
        txt_matKhau.setManaged(!dangHien);
        txt_matKhauHien.setVisible(dangHien);
        txt_matKhauHien.setManaged(dangHien);
        String icon = dangHien ? "/pic/icon_eye.png" : "/pic/icon_eye.png";
        img_eye.setImage(new Image(icon));
    }

    @FXML
    private void quenMatKhau() {
        AlertUtil.baoThongTin("Vui lòng liên hệ Quản trị viên để được cấp lại mật khẩu.");
    }

    @FXML
    private void dangNhap(ActionEvent event) {
        try {
            String matKhau = dangHien ? txt_matKhauHien.getText() : txt_matKhau.getText();
            SessionContext ctx = TaiKhoan_BUS.dangNhap(txt_taiKhoan.getText(), matKhau);
            App.session = ctx;
            App.setRoot("GD_Chinh");
        } catch (RuntimeException ex) {
            lb_thongBao.setText(ex.getMessage());
            AlertUtil.baoLoi(ex.getMessage());
        }
    }
}
