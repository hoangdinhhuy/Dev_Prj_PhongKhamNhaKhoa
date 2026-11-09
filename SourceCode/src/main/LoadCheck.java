package main;

import bus.TaiKhoan_BUS;
import entity.SessionContext;
import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;

/** Smoke check FXML trên JavaFX thread với các màn hình Quản lý được yêu cầu. */
public class LoadCheck {
    public static void main(String[] args) throws Exception {
        CountDownLatch done = new CountDownLatch(1);
        AtomicInteger failed = new AtomicInteger();
        Platform.startup(() -> Platform.runLater(() -> {
            try {
                SessionContext quanly = TaiKhoan_BUS.dangNhap("NV0007", "123456");
                App.session = quanly;
                String[] screens = {"GD_DangNhap", "GD_Chinh", "GD_TongQuan", "GD_QLHangHoa",
                        "GD_QLNhanVien", "GD_QLDichVu", "GD_BaoCao"};
                for (String name : screens) {
                    try {
                        FXMLLoader loader = new FXMLLoader(LoadCheck.class.getResource("/gui/" + name + ".fxml"));
                        Parent view = loader.load();
                        if (name.equals("GD_Chinh")) {
                            Field menuField = loader.getController().getClass().getDeclaredField("vb_menu");
                            menuField.setAccessible(true);
                            javafx.scene.layout.VBox menu = (javafx.scene.layout.VBox) menuField.get(loader.getController());
                            java.util.List<String> actual = menu.getChildren().stream()
                                    .map(node -> ((Button) node).getText()).toList();
                            java.util.List<String> expected = java.util.List.of(
                                    "Tổng quan (Dashboard)",
                                    "Quản lý nhân viên & tài khoản",
                                    "Danh mục dịch vụ",
                                    "Hàng hóa / Tồn kho",
                                    "Báo cáo lượt khám & Xuất CSV");
                            if (!actual.equals(expected)) {
                                throw new AssertionError("Menu quản lý sai thứ tự: " + actual);
                            }
                        }
                        System.out.println("[PASS] " + name + " -> " + view.getClass().getSimpleName());
                    } catch (Throwable ex) {
                        failed.incrementAndGet();
                        Throwable cause = ex;
                        while (cause.getCause() != null) cause = cause.getCause();
                        System.out.println("[FAIL] " + name + " -> " + cause);
                    }
                }
            } catch (Throwable ex) {
                failed.incrementAndGet();
                System.out.println("[FAIL] setup -> " + ex);
            } finally {
                Platform.exit();
                done.countDown();
            }
        }));
        done.await();
        if (failed.get() != 0) System.exit(1);
    }
}
