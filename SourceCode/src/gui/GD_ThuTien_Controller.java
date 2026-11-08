package gui;

import java.math.BigDecimal;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import main.App;
import bus.BenhNhan_BUS;
import bus.DichVu_BUS;
import bus.HoaDon_BUS;
import bus.PhieuKham_BUS;
import entity.BenhNhan;
import entity.ChiDinhDichVu;
import entity.ChiTietHoaDon;
import entity.DichVu;
import entity.HoaDon;
import entity.PhieuKham;
import util.AlertUtil;
import util.FormatUtil;
import util.MaThongBao;

/**
 * (Tối ưu UX lễ tân kiêm thu ngân — gộp thu phí khám đầu lượt + thanh toán dịch vụ cuối lượt
 * vào MỘT màn theo góp ý user "cùng là thu lệ phí thì tích hợp chung màn hình")
 * Tab 1: thu phí khám đầu lượt (UC005) — xong có nút "Lập phiếu khám ngay →" nối tiếp.
 * Tab 2: thanh toán dịch vụ cuối lượt (LT-06) — chốt lượt.
 */
public class GD_ThuTien_Controller {

    // ===== TAB 1: PHI KHAM DAU LUOT =====
    @FXML private TextField txt_timBN;
    @FXML private Label lb_thongTinBN;
    @FXML private Label lb_mucPhi;
    @FXML private ComboBox<String> cbb_hinhThucPhi;
    @FXML private TextField txt_tienKhachDua;
    @FXML private Label lb_tienThuaPhi;
    @FXML private Button btn_xemHoaDon;
    @FXML private Button btn_lapPhieuNgay;
    @FXML private Label lb_hoaDonPhi;
    @FXML private VBox khungHoaDonPhi;
    @FXML private VBox noiDungHoaDonPhi;
    @FXML private ScrollPane scrollHoaDonPhi;

    // ===== TAB 2: DICH VU CUOI LUOT =====
    @FXML private TableView<PhieuKham> table_phieu;
    @FXML private TableColumn<PhieuKham, String> cl_ma;
    @FXML private TableColumn<PhieuKham, String> cl_bn;
    @FXML private TableColumn<PhieuKham, String> cl_ngay;
    @FXML private TableView<ChiDinhDichVu> table_dichVu;
    @FXML private TableColumn<ChiDinhDichVu, String> cl_dv;
    @FXML private TableColumn<ChiDinhDichVu, String> cl_gia;
    @FXML private Label lb_thongTin;
    @FXML private Label lb_tong;
    @FXML private ComboBox<String> cbb_hinhThucDV;
    @FXML private TextField txt_tienDuaDV;
    @FXML private Label lb_tienThuaDV;
    @FXML private VBox khungNoiDung;

    @FXML private TabPane tabPane;

    private BenhNhan benhNhan;
    private DichVu phiKham;
    private HoaDon hoaDonPhiDaThu;      // hóa đơn phí vừa thu (tab 1)
    private PhieuKham phieuChon;        // lượt chờ thanh toán (tab 2)
    private HoaDon hoaDonTam = new HoaDon();
    private java.util.List<Node> noiDungThanhToan;

    @FXML
    private void initialize() {
        // Tab 1
        cbb_hinhThucPhi.getItems().addAll("TIEN_MAT", "CHUYEN_KHOAN");
        cbb_hinhThucPhi.setValue("TIEN_MAT");
        phiKham = DichVu_BUS.layDichVuTheoMa(App.session, HoaDon_BUS.MA_DICH_VU_PHI_KHAM);
        lb_mucPhi.setText("(chọn bệnh nhân để xem phí)");
        txt_tienKhachDua.textProperty().addListener((o, cu, moi) -> capNhatTienThuaPhi());
        // Tab 2
        cbb_hinhThucDV.getItems().addAll("TIEN_MAT", "CHUYEN_KHOAN");
        cbb_hinhThucDV.setValue("TIEN_MAT");
        cl_ma.setCellValueFactory(new PropertyValueFactory<>("maPhieuKham"));
        cl_bn.setCellValueFactory(new PropertyValueFactory<>("tenBenhNhan"));
        cl_ngay.setCellValueFactory(cd -> new SimpleStringProperty(FormatUtil.ngay(cd.getValue().getNgayKham())));
        cl_dv.setCellValueFactory(new PropertyValueFactory<>("tenDichVu"));
        cl_gia.setCellValueFactory(cd -> new SimpleStringProperty(FormatUtil.tien(cd.getValue().getDonGia())));
        txt_tienDuaDV.textProperty().addListener((o, cu, moi) -> capNhatTienThuaDV());
        table_phieu.getSelectionModel().selectedItemProperty().addListener((o, cu, chon) -> napChiTiet());
        noiDungThanhToan = java.util.List.copyOf(khungNoiDung.getChildren());
        // Payload: BN từ Hồ sơ bệnh nhân (tab 1) hoặc PhieuKham từ dialog lập phiếu khác (tab 2)
        Object yc = App.layYeuCauChuyenManHinh();
        if (yc instanceof BenhNhan bn) {
            chonBenhNhan(bn);
            chonTab(0);
        } else if (yc instanceof PhieuKham pk) {
            chonTab(1);
            taiLaiPhieu();
            for (PhieuKham p : table_phieu.getItems()) {
                if (p.getMaPhieuKham().equals(pk.getMaPhieuKham())) {
                    table_phieu.getSelectionModel().select(p);
                    break;
                }
            }
        }
    }

    private void chonTab(int i) {
        tabPane.getSelectionModel().select(i);
    }

    // ================= TAB 1: PHI KHAM =================
    @FXML
    private void tim() {
        try {
            String tu = txt_timBN.getText().trim();
            benhNhan = null;
            hoaDonPhiDaThu = null;
            btn_xemHoaDon.setDisable(true);
            btn_lapPhieuNgay.setVisible(false);
            btn_lapPhieuNgay.setManaged(false);
            khungHoaDonPhi.setVisible(false);
            khungHoaDonPhi.setManaged(false);
            lb_thongTinBN.setText("(chưa chọn)");
            lb_hoaDonPhi.setText("");
            lb_mucPhi.setText("(chọn bệnh nhân để xem phí)");
            lb_tienThuaPhi.setText("");
            txt_tienKhachDua.clear();
            if (tu.matches("BN\\d+")) {
                benhNhan = BenhNhan_BUS.layBenhNhanTheoMa(tu);
            } else if (tu.matches("\\d{12}")) {
                // (Mẫu GD_DatPhong N11) 12 số = CCCD — tự nhận diện khách cũ
                benhNhan = BenhNhan_BUS.layBenhNhanTheoCCCD(tu);
            } else {
                var ds = BenhNhan_BUS.layBenhNhanTheoSDT(tu);
                if (ds.size() == 1) {
                    benhNhan = ds.get(0);
                }
            }
            if (benhNhan == null) {
                lb_thongTinBN.setText("(không tìm thấy)");
                AlertUtil.baoLoi(MaThongBao.TB05);
                return;
            }
            chonBenhNhan(benhNhan);
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }

    private void chonBenhNhan(BenhNhan bn) {
        benhNhan = bn;
        hoaDonPhiDaThu = null;
        btn_xemHoaDon.setDisable(true);
        btn_lapPhieuNgay.setVisible(false);
        btn_lapPhieuNgay.setManaged(false);
        khungHoaDonPhi.setVisible(false);
        khungHoaDonPhi.setManaged(false);
        lb_hoaDonPhi.setText("");
        txt_tienKhachDua.clear();
        lb_tienThuaPhi.setText("");
        lb_thongTinBN.setText(benhNhan.getMaBenhNhan() + " - " + benhNhan.getHoTen()
                + (benhNhan.getCccd() != null ? " · CCCD " + benhNhan.getCccd() : ""));
        lb_mucPhi.setText(phiKham == null ? "Thiếu DV0001 - chạy 003_seed_dichvu.sql"
                : FormatUtil.tien(phiKham.getDonGiaNiemYet()));
        HoaDon phiChuaDung = HoaDon_BUS.layHoaDonPhiChuaDung(App.session, bn.getMaBenhNhan());
        if (phiChuaDung != null) {
            hoaDonPhiDaThu = phiChuaDung;
            lb_hoaDonPhi.setText("Phí khám đã được thu — có thể lập phiếu khám.");
            btn_lapPhieuNgay.setVisible(true);
            btn_lapPhieuNgay.setManaged(true);
        }
    }

    private void capNhatTienThuaPhi() {
        if (phiKham == null) {
            return;
        }
        try {
            var dua = new BigDecimal(txt_tienKhachDua.getText().trim());
            lb_tienThuaPhi.setText(FormatUtil.tien(dua.subtract(phiKham.getDonGiaNiemYet())));
        } catch (Exception ex) {
            lb_tienThuaPhi.setText("");
        }
    }

    @FXML
    private void thuPhi() {
        try {
            if (benhNhan == null) {
                AlertUtil.baoLoi(MaThongBao.VL05);
                return;
            }
            HoaDon hd = HoaDon_BUS.thuPhiKham(App.session, benhNhan.getMaBenhNhan(),
                    cbb_hinhThucPhi.getValue(), txt_tienKhachDua.getText());
            hoaDonPhiDaThu = hd;
            hd.setBenhNhan(benhNhan);
            lb_hoaDonPhi.setText("Đã thu: " + hd.getMaHoaDon() + " - " + FormatUtil.tien(hd.getTongTien()));
            btn_xemHoaDon.setDisable(false);
            // (Luồng liền mạch — góp ý user) Nút nối tiếp HIỆN RÕ sau khi thu
            btn_lapPhieuNgay.setVisible(true);
            btn_lapPhieuNgay.setManaged(true);
            AlertUtil.baoThongTin("Thu phí thành công " + hd.getMaHoaDon()
                    + ". Bấm 'Lập phiếu khám ngay' để xếp hàng chờ.");
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }

    /** (Góp ý user — link rõ ràng) Thu phí xong bấm nút này là sang màn lập phiếu. */
    @FXML
    private void sangLapPhieu() {
        if (benhNhan == null) {
            return;
        }
        App.moManHinh("GD_LapPhieuKham", benhNhan);
    }

    @FXML
    private void xemHoaDon() {
        try {
            if (hoaDonPhiDaThu == null) {
                lb_hoaDonPhi.setText("Hãy thu phí khám thành công trước khi xem hóa đơn.");
                return;
            }
            var chiTiet = HoaDon_BUS.layChiTietHoaDon(App.session, hoaDonPhiDaThu.getMaHoaDon());
            noiDungHoaDonPhi.getChildren().setAll(HoaDonPhiKhamView.tao(hoaDonPhiDaThu, chiTiet, App.session));
            khungHoaDonPhi.setManaged(true);
            khungHoaDonPhi.setVisible(true);
            scrollHoaDonPhi.setVvalue(0);
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }

    @FXML
    private void dongHoaDonPhi() {
        khungHoaDonPhi.setVisible(false);
        khungHoaDonPhi.setManaged(false);
    }

    @FXML
    private void luuHoaDonPhi() {
        if (noiDungHoaDonPhi.getChildren().isEmpty()) return;
        javafx.stage.FileChooser chooser = new javafx.stage.FileChooser();
        chooser.setTitle("Lưu hóa đơn phí khám");
        chooser.setInitialFileName("hoa_don_phi_kham_" + hoaDonPhiDaThu.getMaHoaDon() + ".pdf");
        chooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("PDF", "*.pdf"));
        java.io.File tep = chooser.showSaveDialog(khungHoaDonPhi.getScene().getWindow());
        if (tep != null) {
            try {
                util.PdfImageUtil.luuTrangA4(noiDungHoaDonPhi.getChildren().get(0), tep.toPath());
                AlertUtil.baoThongTin("Đã lưu hóa đơn PDF: " + tep.getAbsolutePath());
            } catch (java.io.IOException ex) {
                AlertUtil.baoLoi("Không lưu được PDF: " + ex.getMessage());
            }
        }
    }

    @FXML
    private void inHoaDonPhi() {
        if (noiDungHoaDonPhi.getChildren().isEmpty()) return;
        javafx.scene.Node trang = noiDungHoaDonPhi.getChildren().get(0);
        javafx.print.PrinterJob job = javafx.print.PrinterJob.createPrinterJob();
        if (job == null) {
            AlertUtil.baoLoi("Không tìm thấy máy in trên máy này.");
            return;
        }
        javafx.stage.Window owner = khungHoaDonPhi.getScene().getWindow();
        if (!job.showPrintDialog(owner)) return;
        var layout = job.getPrinter().createPageLayout(javafx.print.Paper.A4,
                javafx.print.PageOrientation.PORTRAIT, javafx.print.Printer.MarginType.DEFAULT);
        double tyLe = Math.min(layout.getPrintableWidth() / trang.getBoundsInLocal().getWidth(),
                layout.getPrintableHeight() / trang.getBoundsInLocal().getHeight());
        javafx.scene.transform.Scale thuNho = new javafx.scene.transform.Scale(tyLe, tyLe);
        trang.getTransforms().add(thuNho);
        try {
            if (job.printPage(layout, trang)) {
                job.endJob();
                AlertUtil.baoThongTin("Đã gửi hóa đơn đến máy in.");
            } else {
                job.cancelJob();
            }
        } finally {
            trang.getTransforms().remove(thuNho);
        }
    }

    // ================= TAB 2: DICH VU CUOI LUOT =================
    private void taiLaiPhieu() {
        try {
            var ds = PhieuKham_BUS.layPhieuHoanTatChuaThanhToan(App.session);
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

    private void napChiTiet() {
        phieuChon = table_phieu.getSelectionModel().getSelectedItem();
        table_dichVu.getItems().clear();
        hoaDonTam = new HoaDon();
        lb_tong.setText(FormatUtil.tien(hoaDonTam.getTongTien()));
        lb_tienThuaDV.setText("");
        txt_tienDuaDV.clear();
        if (phieuChon == null) {
            lb_thongTin.setText("(chưa chọn lượt khám)");
            return;
        }
        lb_thongTin.setText(phieuChon.getMaPhieuKham() + " - " + phieuChon.getTenBenhNhan()
                + " - " + FormatUtil.ngay(phieuChon.getNgayKham()));
        var cds = HoaDon_BUS.layChiDinhChoThanhToan(App.session, phieuChon.getMaPhieuKham());
        table_dichVu.setItems(FXCollections.observableArrayList(cds));
        for (ChiDinhDichVu cd : cds) {
            hoaDonTam.themChiTiet(new ChiTietHoaDon(null, cd.getMaDichVu(), 1, cd.getDonGia()));
        }
        lb_tong.setText(FormatUtil.tien(hoaDonTam.getTongTien()));
    }

    private void capNhatTienThuaDV() {
        try {
            var dua = new BigDecimal(txt_tienDuaDV.getText().trim());
            hoaDonTam.setTienKhachDua(dua);
            lb_tienThuaDV.setText(FormatUtil.tien(hoaDonTam.getTienThua()));
        } catch (Exception ex) {
            lb_tienThuaDV.setText("");
        }
    }

    @FXML
    private void thanhToan() {
        if (phieuChon == null) {
            AlertUtil.baoLoi(MaThongBao.VL05);
            return;
        }
        try {
            if (!AlertUtil.xacNhan("Thanh toán " + FormatUtil.tien(hoaDonTam.getTongTien())
                    + " cho lượt khám " + phieuChon.getMaPhieuKham() + "?")) {
                return;
            }
            HoaDon hd = HoaDon_BUS.thanhToanDichVu(App.session, phieuChon.getMaPhieuKham(),
                    cbb_hinhThucDV.getValue(), txt_tienDuaDV.getText());
            var chiTiet = HoaDon_BUS.layChiTietHoaDon(App.session, hd.getMaHoaDon());
            hienHoaDonCa(hd, phieuChon, chiTiet);
            taiLaiPhieu();
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }

    private void hienHoaDonCa(HoaDon hd, PhieuKham phieu, java.util.List<ChiTietHoaDon> chiTiet) {
        VBox trang = HoaDonThanhToanView.tao(hd, phieu, chiTiet, App.session);
        javafx.scene.control.ScrollPane xemTruoc = new javafx.scene.control.ScrollPane(trang);
        xemTruoc.setFitToWidth(false);
        xemTruoc.setPannable(true);
        VBox.setVgrow(xemTruoc, Priority.ALWAYS);

        javafx.scene.control.Button btnPdf = new javafx.scene.control.Button("Lưu PDF");
        javafx.scene.control.Button btnIn = new javafx.scene.control.Button("In hóa đơn");
        javafx.scene.control.Button btnDong = new javafx.scene.control.Button("Quay lại thu tiền");
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
                    AlertUtil.baoThongTin("Đã gửi hóa đơn đến máy in.");
                } else {
                    job.cancelJob();
                }
            } finally {
                trang.getTransforms().remove(thuNho);
            }
        });
        btnDong.setOnAction(e -> {
            khungNoiDung.getChildren().setAll(noiDungThanhToan);
            taiLaiPhieu();
        });
    }

}
