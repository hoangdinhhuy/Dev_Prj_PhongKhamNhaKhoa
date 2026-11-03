package gui;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import main.App;
import bus.BenhNhan_BUS;
import bus.DichVu_BUS;
import bus.HoaDon_BUS;
import entity.BenhNhan;
import entity.DichVu;
import entity.HoaDon;
import util.AlertUtil;
import util.FormatUtil;
import util.MaThongBao;

/** UC005 - Thu tien phi kham (chu file: Huy). */
public class GD_ThuPhi_Controller {

    @FXML private TextField txt_timBN;
    @FXML private Label lb_thongTinBN;
    @FXML private Label lb_mucPhi;
    @FXML private ComboBox<String> cbb_hinhThuc;
    @FXML private TextField txt_tienKhachDua;
    @FXML private Label lb_tienThua;
    @FXML private Label lb_hoaDon;
    @FXML private Button btn_xemHoaDon;
    @FXML private VBox khungHoaDon;
    @FXML private VBox noiDungHoaDon;
    @FXML private ScrollPane scrollHoaDon;

    private BenhNhan benhNhan;
    private DichVu phiKham;
    private HoaDon hoaDonDaThu;

    @FXML
    private void initialize() {
        cbb_hinhThuc.getItems().addAll("TIEN_MAT", "CHUYEN_KHOAN");
        cbb_hinhThuc.setValue("TIEN_MAT");
        phiKham = DichVu_BUS.layDichVuTheoMa(App.session, HoaDon_BUS.MA_DICH_VU_PHI_KHAM);
        lb_mucPhi.setText("(chọn bệnh nhân để xem phí)");
        txt_tienKhachDua.textProperty().addListener((o, cu, moi) -> capNhatTienThua());
    }

    @FXML
    private void tim() {
        try {
            String tu = txt_timBN.getText().trim();
            benhNhan = null;
            hoaDonDaThu = null;
            btn_xemHoaDon.setDisable(true);
            khungHoaDon.setVisible(false);
            khungHoaDon.setManaged(false);
            lb_thongTinBN.setText("(chưa chọn)");
            lb_hoaDon.setText("");
            lb_mucPhi.setText("(chọn bệnh nhân để xem phí)");
            lb_tienThua.setText("");
            txt_tienKhachDua.clear();
            if (tu.matches("BN\\d+")) {
                benhNhan = BenhNhan_BUS.layBenhNhanTheoMa(tu);
            } else {
                var ds = BenhNhan_BUS.layBenhNhanTheoSDT(tu);
                if (ds.size() == 1) {
                    benhNhan = ds.get(0);
                }
            }
            if (benhNhan == null) {
                lb_thongTinBN.setText("(không tìm thấy)");
                lb_hoaDon.setText("");
                AlertUtil.baoLoi(MaThongBao.TB05);
                return;
            }
            lb_thongTinBN.setText(benhNhan.getMaBenhNhan() + " - " + benhNhan.getHoTen());
            lb_mucPhi.setText(phiKham == null ? "Thiếu DV0001 - chạy 003_seed_dichvu.sql"
                    : FormatUtil.tien(phiKham.getDonGiaNiemYet()));
            lb_hoaDon.setText("");
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }

    private void capNhatTienThua() {
        if (phiKham == null) {
            return;
        }
        try {
            var dua = new java.math.BigDecimal(txt_tienKhachDua.getText().trim());
            lb_tienThua.setText(FormatUtil.tien(dua.subtract(phiKham.getDonGiaNiemYet())));
        } catch (Exception ex) {
            lb_tienThua.setText("");
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
                    cbb_hinhThuc.getValue(), txt_tienKhachDua.getText());
            hoaDonDaThu = hd;
            hd.setTenBenhNhan(benhNhan.getHoTen());
            lb_hoaDon.setText("Đã thu: " + hd.getMaHoaDon() + " - " + FormatUtil.tien(hd.getTongTien()));
            btn_xemHoaDon.setDisable(false);
            AlertUtil.baoThongTin("Thu phí thành công. Hóa đơn " + hd.getMaHoaDon()
                    + " sẽ được gắn khi lập phiếu khám.");
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }

    @FXML
    private void xemHoaDon() {
        try {
            if (hoaDonDaThu == null) {
                lb_hoaDon.setText("Hãy thu phí khám thành công trước khi xem hóa đơn.");
                return;
            }
            var chiTiet = HoaDon_BUS.layChiTietHoaDon(App.session, hoaDonDaThu.getMaHoaDon());
            noiDungHoaDon.getChildren().setAll(HoaDonPhiKhamView.tao(hoaDonDaThu, chiTiet, App.session));
            khungHoaDon.setManaged(true);
            khungHoaDon.setVisible(true);
            scrollHoaDon.setVvalue(0);
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }

    @FXML
    private void dongHoaDon() {
        khungHoaDon.setVisible(false);
        khungHoaDon.setManaged(false);
    }

    @FXML
    private void luuHoaDonPdf() {
        if (noiDungHoaDon.getChildren().isEmpty()) return;
        javafx.stage.FileChooser chooser = new javafx.stage.FileChooser();
        chooser.setTitle("Lưu hóa đơn phí khám");
        chooser.setInitialFileName("hoa_don_phi_kham_" + hoaDonDaThu.getMaHoaDon() + ".pdf");
        chooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("PDF", "*.pdf"));
        java.io.File tep = chooser.showSaveDialog(khungHoaDon.getScene().getWindow());
        if (tep != null) {
            try {
                util.PdfImageUtil.luuTrangA4(noiDungHoaDon.getChildren().get(0), tep.toPath());
                AlertUtil.baoThongTin("Đã lưu hóa đơn PDF: " + tep.getAbsolutePath());
            } catch (java.io.IOException ex) {
                AlertUtil.baoLoi("Không lưu được PDF: " + ex.getMessage());
            }
        }
    }

    @FXML
    private void inHoaDon() {
        if (noiDungHoaDon.getChildren().isEmpty()) return;
        javafx.scene.Node trang = noiDungHoaDon.getChildren().get(0);
        javafx.print.PrinterJob job = javafx.print.PrinterJob.createPrinterJob();
        if (job == null) {
            AlertUtil.baoLoi("Không tìm thấy máy in trên máy này.");
            return;
        }
        javafx.stage.Window owner = khungHoaDon.getScene().getWindow();
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
                AlertUtil.baoLoi("Không in được hóa đơn.");
            }
        } finally {
            trang.getTransforms().remove(thuNho);
        }
    }

    public HoaDon getHoaDonDaThu() {
        return hoaDonDaThu;
    }

    public BenhNhan getBenhNhan() {
        return benhNhan;
    }
}
