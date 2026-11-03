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
                SessionContext letan = TaiKhoan_BUS.dangNhap("letan", "123456");
                SessionContext phuta = TaiKhoan_BUS.dangNhap("phuta", "123456");
                SessionContext bacsi = TaiKhoan_BUS.dangNhap("bacsi1", "123456");
                SessionContext quanly = TaiKhoan_BUS.dangNhap("quanly", "123456");
                String[] screens = {"GD_DangNhap", "GD_Chinh", "GD_TongQuan", "GD_QLBenhNhan",
                        "GD_TraCuuBenhNhan", "GD_ThuPhi", "GD_LapPhieuKham", "GD_ThanhToan",
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
                        Parent view = FXMLLoader.load(LoadCheck.class.getResource("/gui/" + name + ".fxml"));
                        System.out.println("[PASS] " + name + " -> " + view.getClass().getSimpleName());
                    } catch (Throwable ex) {
                        failed.incrementAndGet();
                        Throwable cause = ex;
                        while (cause.getCause() != null) cause = cause.getCause();
                        System.out.println("[FAIL] " + name + " -> " + cause);
                    }
                }
                try {
                    App.session = letan;
                    kiemTraHaiManVuaSua();
                    System.out.println("[PASS] Phiếu bên phải và phí trống trước khi chọn BN");
                } catch (Throwable ex) {
                    failed.incrementAndGet();
                    Throwable cause = ex;
                    while (cause.getCause() != null) cause = cause.getCause();
                    System.out.println("[FAIL] Phiếu/Thu phí -> " + cause);
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

    private static void kiemTraHaiManVuaSua() throws Exception {
        FXMLLoader phiLoader = new FXMLLoader(LoadCheck.class.getResource("/gui/GD_LapPhieuKham.fxml"));
        BorderPane root = phiLoader.load();
        new Scene(root, 1600, 900);
        root.resize(1600, 900);
        PhieuKham phieu = new PhieuKham("PKTEST000001");
        phieu.setNgayKham(LocalDate.now());
        phieu.setSoThuTu(1);
        phieu.setMaPhong("P001");
        phieu.setMaBenhNhan("BNTEST000001");
        phieu.setTenBenhNhan("Bệnh nhân kiểm tra");
        phieu.setTenBacSi("Bác sĩ kiểm tra");
        phieu.setLyDoKham("Kiểm tra bố cục");
        Field field = GD_LapPhieuKham_Controller.class.getDeclaredField("phieuVuaLap");
        field.setAccessible(true);
        field.set(phiLoader.getController(), phieu);
        root.applyCss();
        ((Button) root.lookup("#btn_in")).fire();
        root.layout();
        if (root.getRight() == null || root.getRight().getBoundsInParent().getMinX() < 600) {
            throw new AssertionError("Bản xem trước chưa nằm ở cột phải");
        }
        FXMLLoader feeLoader = new FXMLLoader(LoadCheck.class.getResource("/gui/GD_ThuPhi.fxml"));
        BorderPane feeRoot = feeLoader.load();
        Label fee = (Label) feeRoot.lookup("#lb_mucPhi");
        if (fee == null || !fee.getText().contains("chọn bệnh nhân")) {
            throw new AssertionError("Mức phí đang hiện trước khi chọn bệnh nhân");
        }
    }
}
