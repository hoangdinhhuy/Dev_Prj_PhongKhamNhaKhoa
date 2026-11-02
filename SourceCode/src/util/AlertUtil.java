package util;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/** Khuon Alert chung - chi GUI duoc goi. */
public final class AlertUtil {
    private AlertUtil() { }

    public static void baoLoi(String message) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Lỗi");
        a.setHeaderText(null);
        a.setContentText(message == null || message.isBlank() ? MaThongBao.TB09 : message);
        a.showAndWait();
    }

    public static void baoThongTin(String message) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Thành công");
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }

    public static boolean xacNhan(String message) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Xác nhận");
        a.setHeaderText(null);
        a.setContentText(message);
        Optional<ButtonType> kq = a.showAndWait();
        return kq.isPresent() && kq.get() == ButtonType.OK;
    }
}
