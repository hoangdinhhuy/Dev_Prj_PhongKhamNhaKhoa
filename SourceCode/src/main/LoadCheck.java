package main;

import bus.TaiKhoan_BUS;
import entity.SessionContext;
import entity.PhieuKham;
import gui.GD_LapPhieuKham_Controller;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

/** Smoke check FXML trên JavaFX thread với đúng vai trò của từng màn. */
public class LoadCheck {
    public static void main(String[] args) throws Exception {
        CountDownLatch done = new CountDownLatch(1);
        AtomicInteger failed = new AtomicInteger();
        Platform.startup(() -> Platform.runLater(() -> {
            try {
                SessionContext letan = TaiKhoan_BUS.dangNhap("NV0001", "123456");
                SessionContext phuta = TaiKhoan_BUS.dangNhap("NV0005", "123456");
                SessionContext bacsi = TaiKhoan_BUS.dangNhap("NV0002", "123456");
                SessionContext quanly = TaiKhoan_BUS.dangNhap("NV0007", "123456");
                String[] screens = {"GD_DangNhap", "GD_Chinh", "GD_TongQuan", "GD_QLBenhNhan",
                        "GD_TraCuuBenhNhan", "GD_ThuTien", "GD_ChotCa", "GD_LapPhieuKham",
                        "GD_HangCho", "GD_KhamBenh", "GD_LichSuKham", "GD_QLHangHoa",
                        "GD_QLNhanVien", "GD_QLDichVu", "GD_BaoCao"};
                for (String name : screens) {
                    App.session = switch (name) {
                        case "GD_HangCho" -> phuta;
                        case "GD_KhamBenh", "GD_LichSuKham" -> bacsi;
                        case "GD_QLHangHoa", "GD_QLNhanVien", "GD_QLDichVu", "GD_BaoCao" -> quanly;
                        default -> letan;
                    };
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
                                    "Tổng quan", "Hồ sơ bệnh nhân", "Tra cứu hồ sơ bệnh nhân",
                                    "Thu tiền (phí + dịch vụ)", "Lập phiếu khám", "Chốt ca (bàn giao)");
                            if (!actual.equals(expected)) {
                                throw new AssertionError("Menu lễ tân sai thứ tự: " + actual);
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
