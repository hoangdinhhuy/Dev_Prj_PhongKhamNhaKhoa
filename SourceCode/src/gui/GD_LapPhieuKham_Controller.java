package gui;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import main.App;
import bus.BenhNhan_BUS;
import bus.NhanVien_BUS;
import bus.PhieuKham_BUS;
import bus.PhongKham_BUS;
import entity.BenhNhan;
import entity.HoaDon;
import entity.NhanVien;
import entity.PhieuKham;
import entity.PhongKham;
import util.AlertUtil;
import util.FormatUtil;
import util.MaThongBao;

/** UC003 - Lap phieu kham + cap STT (chu file: Huy). */
public class GD_LapPhieuKham_Controller {

    @FXML private BorderPane khungLapPhieu;
    @FXML private TextField txt_timBN;
    @FXML private Label lb_bn;
    @FXML private Label lb_hoaDon;
    @FXML private ComboBox<NhanVien> cbb_bacSi;
    @FXML private ComboBox<PhongKham> cbb_phong;
    @FXML private TextField txt_lyDoKham;
    @FXML private Label lb_sttDuKien;

    private BenhNhan benhNhan;
    private HoaDon hoaDonPhi;
    private PhieuKham phieuVuaLap;

    @FXML
    private void initialize() {
        cbb_bacSi.setItems(javafx.collections.FXCollections
                .observableArrayList(NhanVien_BUS.danhSachBacSiDangLamViec(App.session)));
        cbb_bacSi.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
            @Override protected void updateItem(NhanVien nv, boolean empty) {
                super.updateItem(nv, empty);
                setText(empty || nv == null ? null : nv.getMaNhanVien() + " - " + nv.getHoTen());
            }
        });
        cbb_bacSi.setButtonCell(cbb_bacSi.getCellFactory().call(null));
        cbb_phong.setItems(javafx.collections.FXCollections
                .observableArrayList(PhongKham_BUS.danhSachPhongDangNhanBenh(App.session)));
        cbb_phong.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
            @Override protected void updateItem(PhongKham p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p == null ? null : p.getMaPhong() + " - " + p.getTenPhong());
            }
        });
        cbb_phong.setButtonCell(cbb_phong.getCellFactory().call(null));
        // mac dinh chon phan tu dau tien de khong trong ruong
        if (!cbb_bacSi.getItems().isEmpty()) {
            cbb_bacSi.setValue(cbb_bacSi.getItems().get(0));
        }
        if (!cbb_phong.getItems().isEmpty()) {
            cbb_phong.setValue(cbb_phong.getItems().get(0));
        }
    }

    @FXML
    private void tim() {
        try {
            String tu = txt_timBN.getText().trim();
            benhNhan = null;
            hoaDonPhi = null;
            phieuVuaLap = null;
            khungLapPhieu.setRight(null);
            lb_sttDuKien.setText("");
            lb_hoaDon.setText("(chưa có - hãy dùng màn hình Thu phí)");
            if (tu.matches("BN\\d+")) {
                benhNhan = BenhNhan_BUS.layBenhNhanTheoMa(tu);
            } else {
                var ds = BenhNhan_BUS.layBenhNhanTheoSDT(tu);
                if (ds.size() == 1) {
                    benhNhan = ds.get(0);
                }
            }
            if (benhNhan == null) {
                lb_bn.setText("(không tìm thấy)");
                AlertUtil.baoLoi(MaThongBao.TB05);
                return;
            }
            lb_bn.setText(benhNhan.getMaBenhNhan() + " - " + benhNhan.getHoTen());
            hoaDonPhi = bus.HoaDon_BUS.layHoaDonPhiChuaDung(App.session,
                    benhNhan.getMaBenhNhan());
            lb_hoaDon.setText(hoaDonPhi == null ? "(chưa có - hãy dùng màn hình Thu phí)"
                    : hoaDonPhi.getMaHoaDon() + " - " + FormatUtil.tien(hoaDonPhi.getTongTien()));
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }

    @FXML
    private void lapPhieu() {
        try {
            NhanVien bs = cbb_bacSi.getValue();
            PhongKham pk = cbb_phong.getValue();
            PhieuKham phieu = PhieuKham_BUS.lapPhieuKham(App.session, benhNhan,
                    bs == null ? null : bs.getMaNhanVien(),
                    pk == null ? null : pk.getMaPhong(),
                    txt_lyDoKham.getText(), hoaDonPhi);
            phieuVuaLap = phieu;
            khungLapPhieu.setRight(null);
            lb_sttDuKien.setText(String.valueOf(phieu.getSoThuTu()));
            AlertUtil.baoThongTin("Đã lập phiếu " + phieu.getMaPhieuKham() + " - STT " + phieu.getSoThuTu()
                    + " - phòng " + phieu.getMaPhong() + " (chờ khám)");
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }

    @FXML
    private void inPhieu() {
        if (phieuVuaLap == null) {
            AlertUtil.baoLoi("Chưa có phiếu vừa lập để xem.");
            return;
        }
        javafx.scene.layout.VBox trang = PhieuTiepNhanView.tao(phieuVuaLap, benhNhan, App.session);
        javafx.scene.control.ScrollPane xemTruoc = new javafx.scene.control.ScrollPane(trang);
        xemTruoc.setFitToWidth(false);
        xemTruoc.setPannable(true);
        javafx.scene.control.Button btnPdf = new javafx.scene.control.Button("Lưu PDF");
        javafx.scene.control.Button btnIn = new javafx.scene.control.Button("In phiếu");
        btnPdf.getStyleClass().add("print-action-btn");
        btnIn.getStyleClass().add("print-action-btn");
        javafx.scene.control.Button btnDong = new javafx.scene.control.Button("Đóng");
        javafx.scene.layout.HBox thanhNut = new javafx.scene.layout.HBox(10, btnPdf, btnIn, btnDong);
        thanhNut.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        thanhNut.setPadding(new javafx.geometry.Insets(12));
        javafx.scene.layout.BorderPane khung = new javafx.scene.layout.BorderPane();
        khung.setCenter(xemTruoc);
        khung.setBottom(thanhNut);
        khung.setStyle("-fx-background-color: white; -fx-border-color: #CFD9E5; -fx-border-width: 1;");
        khung.prefWidthProperty().bind(khungLapPhieu.widthProperty().multiply(0.52));
        khung.setMaxWidth(840);
        khungLapPhieu.setRight(khung);
        javafx.stage.Window owner = txt_timBN.getScene().getWindow();
        btnPdf.setOnAction(e -> {
            javafx.stage.FileChooser fc = new javafx.stage.FileChooser();
            fc.setTitle("Lưu phiếu tiếp nhận khám");
            fc.setInitialFileName("phieu_tiep_nhan_" + phieuVuaLap.getMaPhieuKham() + ".pdf");
            fc.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("PDF", "*.pdf"));
            java.io.File tep = fc.showSaveDialog(owner);
            if (tep != null) {
                try {
                    util.PdfImageUtil.luuTrangA4(trang, tep.toPath());
                    AlertUtil.baoThongTin("Đã lưu PDF: " + tep.getAbsolutePath());
                } catch (java.io.IOException ex) {
                    AlertUtil.baoLoi("Không lưu được PDF: " + ex.getMessage());
                }
            }
        });
        btnIn.setOnAction(e -> {
            javafx.print.PrinterJob job = javafx.print.PrinterJob.createPrinterJob();
            if (job == null) {
                AlertUtil.baoLoi("Không tìm thấy máy in trên máy này.");
                return;
            }
            if (!job.showPrintDialog(owner)) return;
            var layout = job.getPrinter().createPageLayout(javafx.print.Paper.A4,
                    javafx.print.PageOrientation.PORTRAIT, javafx.print.Printer.MarginType.DEFAULT);
            double tyLe = Math.min(layout.getPrintableWidth() / trang.getWidth(),
                    layout.getPrintableHeight() / trang.getHeight());
            javafx.scene.transform.Scale thuNho = new javafx.scene.transform.Scale(tyLe, tyLe);
            trang.getTransforms().add(thuNho);
            try {
                if (job.printPage(layout, trang)) {
                    job.endJob();
                    AlertUtil.baoThongTin("Đã gửi phiếu khám đến máy in.");
                } else {
                    job.cancelJob();
                    AlertUtil.baoLoi("Không in được phiếu khám.");
                }
            } finally {
                trang.getTransforms().remove(thuNho);
            }
        });
        btnDong.setOnAction(e -> khungLapPhieu.setRight(null));
    }
}
