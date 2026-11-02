package main;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import connectDB.ConnectDB;
import entity.SessionContext;

/** Entry point + dieu huong 1 cua so (setRoot/openModal) + phien dang nhap. Chu file: Duy. */
public class App extends Application {

    public static SessionContext session;
    private static Stage primaryStage;
    private static Scene scene;

    /** Tieu de hien thi cua cac man hinh (tieng Viet, khong dung ma FXML). */
    private static final java.util.Map<String, String> TIEU_DE = java.util.Map.ofEntries(
            java.util.Map.entry("GD_QLBenhNhan", "Quản lý hồ sơ bệnh nhân"),
            java.util.Map.entry("GD_TraCuuBenhNhan", "Tra cứu hồ sơ bệnh nhân"),
            java.util.Map.entry("GD_ThuPhi", "Thu tiền phí khám"),
            java.util.Map.entry("GD_LapPhieuKham", "Lập phiếu khám"),
            java.util.Map.entry("GD_HangCho", "Theo dõi hàng chờ"),
            java.util.Map.entry("GD_KhamBenh", "Khám và chẩn đoán"),
            java.util.Map.entry("GD_QLNhanVien", "Quản lý nhân viên & tài khoản"),
            java.util.Map.entry("GD_QLDichVu", "Quản lý dịch vụ"),
            java.util.Map.entry("GD_BaoCao", "Báo cáo lượt khám"));

    /** Gắn theme chung cho mọi Scene (gui/ungdung.css). */
    public static void ganTheme(Scene sc) {
        var css = App.class.getResource("/gui/ungdung.css");
        if (css != null && !sc.getStylesheets().contains(css.toExternalForm())) {
            sc.getStylesheets().add(css.toExternalForm());
        }
    }

    /** Dong ung dung tu nut trong giao dien khong co thanh tieu de Windows. */
    public static void dongUngDung() {
        javafx.application.Platform.exit();
    }

    /** Doi man hinh chinh cua cua so chinh. */
    public static void setRoot(String tenFxml) {
        try {
            Parent root = FXMLLoader.load(App.class.getResource("/gui/" + tenFxml + ".fxml"));
            if (scene == null) {
                scene = new Scene(root);
            } else {
                scene.setRoot(root);
            }
            ganTheme(scene);
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
        } catch (IOException ex) {
            throw new RuntimeException("Không mở được màn hình " + tenFxml + ": " + ex.getMessage(), ex);
        }
    }

    /** Mo man hinh modal (showAndWait) - kieu N11. */
    public static void openModal(String tenFxml) {
        openModal(tenFxml, 0, 0);
    }

    public static void openModal(String tenFxml, double width, double height) {
        try {
            Parent root = FXMLLoader.load(App.class.getResource("/gui/" + tenFxml + ".fxml"));
            Stage modal = new Stage();
            modal.initOwner(primaryStage);
            modal.initModality(Modality.WINDOW_MODAL);
            modal.setTitle(TIEU_DE.getOrDefault(tenFxml, "Phòng khám Nha khoa Sài Gòn"));
            Scene sc = new Scene(root);
            ganTheme(sc);
            modal.setScene(sc);
            if (width > 0) {
                modal.setWidth(width);
            }
            if (height > 0) {
                modal.setHeight(height);
            }
            modal.showAndWait();
        } catch (IOException ex) {
            throw new RuntimeException("Không mở được màn hình " + tenFxml + ": " + ex.getMessage(), ex);
        }
    }

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.initStyle(StageStyle.UNDECORATED);
        try {
            ConnectDB.getInstance().connect();
        } catch (RuntimeException ex) {
            Alert a = new Alert(Alert.AlertType.ERROR,
                    "Không thể kết nối SQL Server. Kiểm tra db.properties và DATA/README.md.\nChi tiết: "
                            + ex.getMessage(),
                    ButtonType.OK);
            a.setHeaderText("Lỗi kết nối CSDL");
            a.showAndWait();
            return;
        }
        setRoot("GD_DangNhap");
        primaryStage.setTitle("Nha khoa Sài Gòn — DentistClinic");
        primaryStage.getIcons().add(new javafx.scene.image.Image("/pic/logo_glow.png"));
        primaryStage.setMinWidth(980);
        primaryStage.setMinHeight(620);
        primaryStage.setMaximized(true);   // mo luon o che do toan man hinh
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
