package main;

import java.util.HashMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import entity.SessionContext;
import bus.TaiKhoan_BUS;
import javafx.application.Platform;

/** Cong cu tam: load thu tung FXML de bat loi that (xoa sau khi debug). */
public class LoadCheck {
    public static void main(String[] args) throws Exception {
        Platform.startup(() -> {});
        SessionContext s = TaiKhoan_BUS.dangNhap("letan", "123456");
        App.session = s;
        String[] screens = {"GD_DangNhap", "GD_Chinh", "GD_TongQuan", "GD_QLBenhNhan", "GD_TraCuuBenhNhan", "GD_ThuPhi",
                "GD_LapPhieuKham", "GD_ThanhToan", "GD_HangCho", "GD_KhamBenh",
                "GD_LichSuKham", "GD_QLNhanVien", "GD_QLDichVu", "GD_BaoCao"};
        for (String name : screens) {
            System.out.println("=== " + name);
            try {
                Parent p = FXMLLoader.load(LoadCheck.class.getResource("/gui/" + name + ".fxml"));
                System.out.println("    OK " + p.getClass().getSimpleName());
            } catch (Throwable ex) {
                System.out.println("    LOI: " + ex);
                Throwable c = ex.getCause();
                while (c != null) {
                    System.out.println("    caused by: " + c);
                    c = c.getCause();
                }
            }
        }
        // vai tro khac
        App.session = TaiKhoan_BUS.dangNhap("bacsi1", "123456");
        for (String name : new String[]{"GD_KhamBenh", "GD_LichSuKham"}) {
            System.out.println("=== " + name + " (bacsi)");
            try {
                FXMLLoader.load(LoadCheck.class.getResource("/gui/" + name + ".fxml"));
                System.out.println("    OK");
            } catch (Throwable ex) {
                System.out.println("    LOI: " + ex);
                Throwable c = ex.getCause();
                while (c != null) {
                    System.out.println("    caused by: " + c);
                    c = c.getCause();
                }
            }
        }
        Platform.exit();
        System.out.flush();
    }
}
