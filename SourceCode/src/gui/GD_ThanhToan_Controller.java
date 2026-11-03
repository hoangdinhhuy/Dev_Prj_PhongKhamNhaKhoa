package gui;

import java.math.BigDecimal;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import main.App;
import bus.HoaDon_BUS;
import entity.ChiDinhDichVu;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.PhieuKham;
import util.AlertUtil;
import util.FormatUtil;
import util.MaThongBao;

/** LT-06 — Thanh toan dich vu cuoi luot (chu file: Thanh). */
public class GD_ThanhToan_Controller {

    @FXML private TableView<PhieuKham> table_phieu;
    @FXML private TableColumn<PhieuKham, String> cl_ma;
    @FXML private TableColumn<PhieuKham, String> cl_bn;
    @FXML private TableColumn<PhieuKham, String> cl_ngay;
    @FXML private TableView<ChiDinhDichVu> table_dichVu;
    @FXML private TableColumn<ChiDinhDichVu, String> cl_dv;
    @FXML private TableColumn<ChiDinhDichVu, String> cl_gia;
    @FXML private Label lb_thongTin;
    @FXML private Label lb_tong;
    @FXML private ComboBox<String> cbb_hinhThuc;
    @FXML private TextField txt_tienDua;
    @FXML private Label lb_tienThua;
    @FXML private VBox khungNoiDung;

    private PhieuKham phieuChon;
    private BigDecimal tongTien = BigDecimal.ZERO;
    private java.util.List<Node> noiDungThanhToan;

    @FXML
    private void initialize() {
        cl_ma.setCellValueFactory(new PropertyValueFactory<>("maPhieuKham"));
        cl_bn.setCellValueFactory(new PropertyValueFactory<>("tenBenhNhan"));
        cl_ngay.setCellValueFactory(cd -> new SimpleStringProperty(FormatUtil.ngay(cd.getValue().getNgayKham())));
        cl_dv.setCellValueFactory(new PropertyValueFactory<>("tenDichVu"));
        cl_gia.setCellValueFactory(cd -> new SimpleStringProperty(FormatUtil.tien(cd.getValue().getDonGia())));
        cbb_hinhThuc.getItems().addAll("TIEN_MAT", "CHUYEN_KHOAN");
        cbb_hinhThuc.setValue("TIEN_MAT");
        noiDungThanhToan = java.util.List.copyOf(khungNoiDung.getChildren());
        txt_tienDua.textProperty().addListener((o, cu, moi) -> capNhatTienThua());
        table_phieu.getSelectionModel().selectedItemProperty().addListener((o, cu, chon) -> napChiTiet());
        taiLai();
    }

    @FXML
    private void taiLai() {
        try {
            var ds = danhSachChoThanhToan();
            table_phieu.getItems().clear();
            table_phieu.setItems(FXCollections.observableArrayList(ds));
            if (!ds.isEmpty()) {
                table_phieu.getSelectionModel().selectFirst();
            } else {
                napChiTiet();
                lb_thongTin.setText("(không có lượt khám nào chờ thanh toán)");
            }
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }

    private java.util.List<PhieuKham> danhSachChoThanhToan() {
        return bus.PhieuKham_BUS.layPhieuHoanTatChuaThanhToan(App.session);
    }

    private void napChiTiet() {
        phieuChon = table_phieu.getSelectionModel().getSelectedItem();
        table_dichVu.getItems().clear();
        tongTien = BigDecimal.ZERO;
        lb_tong.setText(FormatUtil.tien(tongTien));
        lb_tienThua.setText("");
        txt_tienDua.clear();
        if (phieuChon == null) {
            lb_thongTin.setText("(chưa chọn lượt khám)");
            return;
        }
        lb_thongTin.setText(phieuChon.getMaPhieuKham() + " - " + phieuChon.getTenBenhNhan()
                + " - " + FormatUtil.ngay(phieuChon.getNgayKham()));
        var cds = HoaDon_BUS.layChiDinhChoThanhToan(App.session, phieuChon.getMaPhieuKham());
        table_dichVu.setItems(FXCollections.observableArrayList(cds));
        tongTien = cds.stream().map(ChiDinhDichVu::getDonGia)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        lb_tong.setText(FormatUtil.tien(tongTien));
    }

    private void capNhatTienThua() {
        try {
            var dua = new BigDecimal(txt_tienDua.getText().trim());
            lb_tienThua.setText(FormatUtil.tien(dua.subtract(tongTien)));
        } catch (Exception ex) {
            lb_tienThua.setText("");
        }
    }

    @FXML
    private void thanhToan() {
        if (phieuChon == null) {
            AlertUtil.baoLoi(MaThongBao.VL05);
            return;
        }
        try {
            if (!AlertUtil.xacNhan("Thanh toán " + FormatUtil.tien(tongTien)
                    + " cho lượt khám " + phieuChon.getMaPhieuKham() + "?")) {
                return;
            }
            HoaDon hd = HoaDon_BUS.thanhToanDichVu(App.session, phieuChon.getMaPhieuKham(),
                    cbb_hinhThuc.getValue(), txt_tienDua.getText());
            var chiTiet = HoaDon_BUS.layChiTietHoaDon(App.session, hd.getMaHoaDon());
            hienHoaDon(hd, phieuChon, chiTiet);
            taiLai();
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }

    private void hienHoaDon(HoaDon hd, PhieuKham phieu, java.util.List<ChiTietHoaDon> chiTiet) {
        VBox trang = HoaDonThanhToanView.tao(hd, phieu, chiTiet, App.session);
        javafx.scene.control.ScrollPane xemTruoc = new javafx.scene.control.ScrollPane(trang);
        xemTruoc.setFitToWidth(false);
        xemTruoc.setPannable(true);
        VBox.setVgrow(xemTruoc, Priority.ALWAYS);

        javafx.scene.control.Button btnPdf = new javafx.scene.control.Button("Lưu PDF");
        javafx.scene.control.Button btnIn = new javafx.scene.control.Button("In hóa đơn");
        javafx.scene.control.Button btnDong = new javafx.scene.control.Button("Quay lại thanh toán");
        btnPdf.getStyleClass().add("print-action-btn");
        btnIn.getStyleClass().add("print-action-btn");
        HBox nut = new HBox(10, btnPdf, btnIn, btnDong);
        nut.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        khungNoiDung.getChildren().setAll(xemTruoc, nut);

        javafx.stage.Window owner = khungNoiDung.getScene().getWindow();
        btnPdf.setOnAction(e -> {
            javafx.stage.FileChooser fc = new javafx.stage.FileChooser();
            fc.setTitle("Lưu hóa đơn thanh toán");
            fc.setInitialFileName("hoa_don_" + hd.getMaHoaDon() + ".pdf");
            fc.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("PDF", "*.pdf"));
            java.io.File tep = fc.showSaveDialog(owner);
            if (tep != null) {
                try {
                    util.PdfImageUtil.luuTrangA4(trang, tep.toPath());
                    AlertUtil.baoThongTin("Đã lưu hóa đơn PDF: " + tep.getAbsolutePath());
                } catch (java.io.IOException ex) {
                    AlertUtil.baoLoi("Không lưu được PDF: " + ex.getMessage());
                }
            }
        });
        btnIn.setOnAction(e -> inHoaDon(trang, owner));
        btnDong.setOnAction(e -> {
            khungNoiDung.getChildren().setAll(noiDungThanhToan);
            taiLai();
        });
    }

    private void inHoaDon(VBox trang, javafx.stage.Window owner) {
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
                AlertUtil.baoThongTin("Đã gửi hóa đơn đến máy in.");
            } else {
                job.cancelJob();
                AlertUtil.baoLoi("Không in được hóa đơn.");
            }
        } finally {
            trang.getTransforms().remove(thuNho);
        }
    }
}
