package gui;

import java.time.format.DateTimeFormatter;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import main.App;
import entity.SessionContext;

/** Tong quan (dashboard) — duoc nap vao khung noi dung cua man hinh chinh. */
public class GD_TongQuan_Controller {

    @FXML private Label lb_homNay;
    @FXML private Label lb_vaiTro;
    @FXML private Label lb_chaoMung;

    @FXML
    private void initialize() {
        SessionContext s = App.session;
        String ten = s == null ? "" : s.getHoTen();
        String vaiTro = s == null || s.getChucVu() == null ? "—" : s.getChucVu().toString();
        lb_homNay.setText(java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        lb_vaiTro.setText(vaiTro);
        lb_chaoMung.setText("Chào mừng " + ten + " đến với hệ thống quản lý phòng khám!");
    }
}
