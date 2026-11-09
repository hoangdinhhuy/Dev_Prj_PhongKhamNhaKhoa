package gui;

import javafx.collections.FXCollections;
import javafx.application.Platform;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import main.App;
import bus.BenhNhan_BUS;
import entity.BenhNhan;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * (Tối ưu UX lễ tân) Ô tìm bệnh nhân có GỢI Ý gõ-tới-đâu-hiện-tới-đó:
 * gõ tên (kể cả không dấu), SĐT hoặc mã BN → danh sách tối đa 8 gợi ý → bấm chọn.
 * Dùng chung cho Lập phiếu khám / Thu tiền — lễ tân không phải copy SĐT giữa các màn.
 */
public class TimKiemBenhNhanBox extends VBox {

    private static final ExecutorService TIM_KIEM = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "tim-kiem-goi-y-benh-nhan");
        thread.setDaemon(true);
        return thread;
    });

    /** Ai dùng box thì nhận bệnh nhân qua callback này. */
    public interface NguoiChon {
        void khiChon(BenhNhan bn);
    }

    private final TextField txt = new TextField();
    private final ListView<BenhNhan> lvGoiY = new ListView<>();
    private BenhNhan daChon;
    private NguoiChon nguoiChon;
    private boolean dangGanBenhNhan;
    private long lanTimHienTai;
    private final PauseTransition treTimKiem = new PauseTransition(Duration.millis(300));

    public TimKiemBenhNhanBox() {
        setSpacing(2);
        txt.setPromptText("Gõ tên / SĐT / mã BN — gợi ý hiện ngay khi gõ");
        lvGoiY.setPrefHeight(150);
        lvGoiY.setPlaceholder(new javafx.scene.control.Label("Đang tìm bệnh nhân…"));
        lvGoiY.setVisible(false);
        lvGoiY.setManaged(false);
        lvGoiY.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(BenhNhan bn, boolean empty) {
                super.updateItem(bn, empty);
                setText(empty || bn == null ? null
                        : bn.getMaBenhNhan() + " · " + bn.getHoTen() + " · " + bn.getSoDienThoai());
            }
        });
        getChildren().addAll(txt, lvGoiY);
        txt.textProperty().addListener((o, cu, moi) -> {
            if (!dangGanBenhNhan) {
                capNhatGoiY(moi);
            }
        });
        lvGoiY.setOnMouseClicked(e -> {
            if (e.getClickCount() == 1) {
                chon(lvGoiY.getSelectionModel().getSelectedItem());
            }
        });
        txt.setOnAction(e -> {
            if (!lvGoiY.getItems().isEmpty()) {
                chon(lvGoiY.getItems().get(0));
            }
        });
    }

    private void capNhatGoiY(String tuMoi) {
        daChon = null;
        String tu = tuMoi == null ? "" : tuMoi.trim();
        long lanTim = ++lanTimHienTai;
        treTimKiem.stop();
        if (tu.isEmpty()) {
            anGoiY();
            return;
        }
        lvGoiY.setItems(FXCollections.observableArrayList());
        lvGoiY.setPlaceholder(new javafx.scene.control.Label("Đang tìm bệnh nhân…"));
        lvGoiY.setVisible(true);
        lvGoiY.setManaged(true);
        treTimKiem.setOnFinished(event -> CompletableFuture
                .supplyAsync(() -> BenhNhan_BUS.goiYBenhNhan(App.session, tu, 8), TIM_KIEM)
                .whenComplete((ketQua, loi) -> Platform.runLater(() -> {
                    if (lanTim != lanTimHienTai) {
                        return;
                    }
                    if (loi != null) {
                        anGoiY();
                        return;
                    }
                    lvGoiY.setItems(FXCollections.observableArrayList(ketQua));
                    lvGoiY.setPlaceholder(new javafx.scene.control.Label("Không tìm thấy bệnh nhân phù hợp."));
                })));
        treTimKiem.playFromStart();
    }

    private void anGoiY() {
        treTimKiem.stop();
        lanTimHienTai++;
        lvGoiY.getItems().clear();
        lvGoiY.setVisible(false);
        lvGoiY.setManaged(false);
    }

    private void chon(BenhNhan bn) {
        if (bn == null) {
            return;
        }
        daChon = bn;
        ganTextBenhNhan(bn);
        anGoiY();
        if (nguoiChon != null) {
            nguoiChon.khiChon(bn);
        }
    }

    /** Đặt sẵn bệnh nhân (khi luồng khác chuyển bệnh nhân sang). */
    public void datBenhNhan(BenhNhan bn) {
        if (bn == null) {
            return;
        }
        daChon = bn;
        ganTextBenhNhan(bn);
        anGoiY();
    }

    private void ganTextBenhNhan(BenhNhan bn) {
        dangGanBenhNhan = true;
        try {
            txt.setText(bn.getHoTen() + " — " + bn.getSoDienThoai());
        } finally {
            dangGanBenhNhan = false;
        }
    }

    public void xoa() {
        txt.clear();
        daChon = null;
        anGoiY();
    }

    public BenhNhan getBenhNhanDaChon() {
        return daChon;
    }

    public void setNguoiChon(NguoiChon nguoiChon) {
        this.nguoiChon = nguoiChon;
    }

    public TextField oNhap() {
        return txt;
    }
}
