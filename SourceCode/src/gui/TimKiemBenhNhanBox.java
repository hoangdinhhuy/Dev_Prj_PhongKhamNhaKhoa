package gui;

import javafx.collections.FXCollections;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import main.App;
import bus.BenhNhan_BUS;
import entity.BenhNhan;

/**
 * (Tối ưu UX lễ tân) Ô tìm bệnh nhân có GỢI Ý gõ-tới-đâu-hiện-tới-đó:
 * gõ tên (kể cả không dấu), SĐT hoặc mã BN → danh sách tối đa 8 gợi ý → bấm chọn.
 * Dùng chung cho Lập phiếu khám / Thu tiền — lễ tân không phải copy SĐT giữa các màn.
 */
public class TimKiemBenhNhanBox extends VBox {

    /** Ai dùng box thì nhận bệnh nhân qua callback này. */
    public interface NguoiChon {
        void khiChon(BenhNhan bn);
    }

    private final TextField txt = new TextField();
    private final ListView<BenhNhan> lvGoiY = new ListView<>();
    private BenhNhan daChon;
    private NguoiChon nguoiChon;

    public TimKiemBenhNhanBox() {
        setSpacing(2);
        txt.setPromptText("Gõ tên / SĐT / mã BN — gợi ý hiện ngay khi gõ");
        lvGoiY.setPrefHeight(150);
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
        txt.textProperty().addListener((o, cu, moi) -> capNhatGoiY(moi));
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
        if (tu.isEmpty()) {
            anGoiY();
            return;
        }
        try {
            lvGoiY.setItems(FXCollections.observableArrayList(
                    BenhNhan_BUS.goiYBenhNhan(App.session, tu, 8)));
            lvGoiY.setVisible(true);
            lvGoiY.setManaged(true);
        } catch (RuntimeException ex) {
            anGoiY();
        }
    }

    private void anGoiY() {
        lvGoiY.getItems().clear();
        lvGoiY.setVisible(false);
        lvGoiY.setManaged(false);
    }

    private void chon(BenhNhan bn) {
        if (bn == null) {
            return;
        }
        daChon = bn;
        txt.setText(bn.getHoTen() + " — " + bn.getSoDienThoai());
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
        txt.setText(bn.getHoTen() + " — " + bn.getSoDienThoai());
        anGoiY();
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
