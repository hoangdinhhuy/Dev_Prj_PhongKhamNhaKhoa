package gui;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.scene.control.cell.PropertyValueFactory;
import main.App;
import bus.CaThuNgan_BUS;
import bus.HoaDon_BUS;
import entity.CaThuNgan;
import entity.HoaDon;
import util.AlertUtil;
import util.FormatUtil;

/**
 * (Mẫu quản lý ca POS — góp ý user 09/11: "chốt ca bằng xác nhận để nhân viên sau nhận ca")
 * Màn riêng quản lý CA THU NGÂN: xem ca hiện tại + bảng kê thu trong ca +
 * CHỐT CA (xác nhận, snapshot doanh thu) + lịch sử các ca đã chốt.
 */
public class GD_ChotCa_Controller {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter HF = DateTimeFormatter.ofPattern("HH:mm");

    @FXML private Label lb_maCa;
    @FXML private Label lb_thuNgan;
    @FXML private Label lb_moLuc;
    @FXML private Label lb_trangThaiCa;
    @FXML private TableView<HoaDon> tableBangKe;
    @FXML private TableColumn<HoaDon, String> cl_bkHD;
    @FXML private TableColumn<HoaDon, String> cl_bkGio;
    @FXML private TableColumn<HoaDon, String> cl_bkBN;
    @FXML private TableColumn<HoaDon, String> cl_bkLoai;
    @FXML private TableColumn<HoaDon, String> cl_bkHT;
    @FXML private TableColumn<HoaDon, String> cl_bkTien;
    @FXML private Label lb_soHD;
    @FXML private Label lb_tongCa;
    @FXML private Label lb_phiHo;
    @FXML private Button btn_moCa;
    @FXML private Button btn_chotCa;
    @FXML private Button btn_luuBangKe;
    @FXML private TableView<CaThuNgan> table_lichSu;
    @FXML private TableColumn<CaThuNgan, String> cl_lsCa;
    @FXML private TableColumn<CaThuNgan, String> cl_lsNV;
    @FXML private TableColumn<CaThuNgan, String> cl_lsMo;
    @FXML private TableColumn<CaThuNgan, String> cl_lsChot;
    @FXML private TableColumn<CaThuNgan, String> cl_lsDV;
    @FXML private TableColumn<CaThuNgan, String> cl_lsHo;
    @FXML private TableColumn<CaThuNgan, String> cl_lsTT;

    private CaThuNgan caHienTai;

    @FXML
    private void initialize() {
        cl_bkHD.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getMaHoaDon()));
        cl_bkGio.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getNgayThu() == null
                ? "" : cd.getValue().getNgayThu().format(HF)));
        cl_bkBN.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getTenBenhNhan()));
        cl_bkLoai.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getMaPhieuKham() == null ? "Phí khám (thu hộ)" : "Dịch vụ cuối lượt"));
        cl_bkHT.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getHinhThucString()));
        cl_bkTien.setCellValueFactory(cd -> new SimpleStringProperty(
                FormatUtil.tien(cd.getValue().getTongTien())));
        cl_lsCa.setCellValueFactory(new PropertyValueFactory<>("maCa"));
        cl_lsNV.setCellValueFactory(new PropertyValueFactory<>("nhanVienString"));
        cl_lsMo.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getThoiGianMo() == null
                ? "" : cd.getValue().getThoiGianMo().format(DTF)));
        cl_lsChot.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getThoiGianChot() == null
                ? "(đang mở)" : cd.getValue().getThoiGianChot().format(DTF)));
        cl_lsDV.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getDoanhThuDichVu() == null ? "—" : FormatUtil.tien(cd.getValue().getDoanhThuDichVu())));
        cl_lsHo.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getPhiThuHo() == null ? "—" : FormatUtil.tien(cd.getValue().getPhiThuHo())));
        cl_lsTT.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().isDangMo() ? "ĐANG MỞ" : "ĐÃ CHỐT"));
        napDuLieu();
    }

    private void napDuLieu() {
        try {
            caHienTai = CaThuNgan_BUS.layCaDangMo(App.session);
            if (caHienTai == null) {
                lb_maCa.setText("(chưa có)");
                lb_thuNgan.setText("(chưa có)");
                lb_moLuc.setText("(chưa có)");
                datTrangThaiCa("CHƯA MỞ CA — bấm NHẬN CA để bắt đầu", false);
                tableBangKe.getItems().clear();
                lb_soHD.setText("Số hóa đơn: 0");
                lb_tongCa.setText("DOANH THU CA (dịch vụ): 0 ₫");
                lb_phiHo.setText("Phí khám thu hộ: 0 ₫");
                btn_moCa.setDisable(false);
                btn_chotCa.setDisable(true);
                btn_luuBangKe.setDisable(true);
            } else {
                lb_maCa.setText(caHienTai.getMaCa());
                lb_thuNgan.setText(caHienTai.getNhanVienString());
                lb_moLuc.setText(caHienTai.getThoiGianMo().format(DTF));
                datTrangThaiCa("ĐANG MỞ", true);
                napBangKeCa();
                btn_moCa.setDisable(true);
                btn_chotCa.setDisable(false);
                btn_luuBangKe.setDisable(false);
            }
            table_lichSu.setItems(FXCollections.observableArrayList(
                    CaThuNgan_BUS.lichSuCa(App.session, 50)));
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }

    private void datTrangThaiCa(String noiDung, boolean dangMo) {
        lb_trangThaiCa.setText(noiDung);
        lb_trangThaiCa.getStyleClass().setAll("shift-status",
                dangMo ? "shift-status-open" : "shift-status-none");
    }

    /** Bảng kê các hóa đơn thu TỪ LÚC MỞ CA (không gộp lệch với ca trước). */
    private void napBangKeCa() {
        try {
            var trongCa = HoaDon_BUS.layHoaDonTrongCa(App.session, caHienTai);
            tableBangKe.setItems(FXCollections.observableArrayList(trongCa));
            BigDecimal ca = BigDecimal.ZERO;
            BigDecimal ho = BigDecimal.ZERO;
            for (HoaDon hd : trongCa) {
                if (hd.getTongTien() == null) continue;
                if (hd.getMaPhieuKham() != null) ca = ca.add(hd.getTongTien());
                else ho = ho.add(hd.getTongTien());
            }
            lb_soHD.setText("Số hóa đơn: " + trongCa.size());
            lb_tongCa.setText("DOANH THU CA (dịch vụ): " + FormatUtil.tien(ca));
            lb_phiHo.setText("Phí khám thu hộ: " + FormatUtil.tien(ho));
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }

    @FXML
    private void moCa() {
        try {
            CaThuNgan ca = CaThuNgan_BUS.moCa(App.session);
            AlertUtil.baoThongTin("Đã nhận ca " + ca.getMaCa() + " lúc "
                    + ca.getThoiGianMo().format(DTF) + ". Bắt đầu ca làm việc.");
            napDuLieu();
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }

    @FXML
    private void chotCa() {
        try {
            if (caHienTai == null) {
                AlertUtil.baoLoi("Không có ca đang mở.");
                return;
            }
            if (!AlertUtil.xacNhan("Chốt ca " + caHienTai.getMaCa() + "?\n"
                    + "Bảng kê sẽ được ghi nhận và nhân viên sau phải NHẬN CA mới thu được.")) {
                return;
            }
            CaThuNgan daChot = CaThuNgan_BUS.chotCa(App.session, caHienTai);
            AlertUtil.baoThongTin("Đã chốt ca " + daChot.getMaCa() + " lúc "
                    + daChot.getThoiGianChot().format(DTF)
                    + "\nDoanh thu ca (dịch vụ): " + FormatUtil.tien(daChot.getDoanhThuDichVu())
                    + "\nPhí khám thu hộ: " + FormatUtil.tien(daChot.getPhiThuHo())
                    + "\nNhân viên tiếp theo bấm NHẬN CA để bắt đầu ca mới.");
            napDuLieu();
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }

    /** Lưu PDF bảng kê ca hiện tại (bàn giao). */
    @FXML
    private void luuBangKe() {
        if (caHienTai == null) {
            AlertUtil.baoLoi("Không có ca đang mở.");
            return;
        }
        VBox trang = new VBox(8);
        trang.setStyle("-fx-background-color: white; -fx-padding: 24;");
        Label tieuDe = new Label("BẢNG KÊ CHỐT CA " + caHienTai.getMaCa()
                + " — " + caHienTai.getThoiGianMo().format(DTF));
        tieuDe.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Label nguoi = new Label("Thu ngân: " + caHienTai.getNhanVienString());
        trang.getChildren().addAll(tieuDe, nguoi);
        TableView<HoaDon> banIn = new TableView<>(tableBangKe.getItems());
        TableColumn<HoaDon, String> c1 = new TableColumn<>("Hóa đơn");
        c1.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getMaHoaDon()));
        TableColumn<HoaDon, String> c2 = new TableColumn<>("Bệnh nhân");
        c2.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getTenBenhNhan()));
        TableColumn<HoaDon, String> c3 = new TableColumn<>("Loại");
        c3.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getMaPhieuKham() == null ? "Phí khám (thu hộ)" : "Dịch vụ cuối lượt"));
        TableColumn<HoaDon, String> c4 = new TableColumn<>("Hình thức");
        c4.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getHinhThucString()));
        TableColumn<HoaDon, String> c5 = new TableColumn<>("Tiền");
        c5.setCellValueFactory(cd -> new SimpleStringProperty(FormatUtil.tien(cd.getValue().getTongTien())));
        banIn.getColumns().setAll(c1, c2, c3, c4, c5);
        banIn.setPrefHeight(400);
        trang.getChildren().add(banIn);
        Label tong = new Label("DOANH THU CA (dịch vụ): " + lb_tongCa.getText()
                .replace("DOANH THU CA (dịch vụ): ", "")
                + "   —   Phí khám thu hộ: " + lb_phiHo.getText().replace("Phí khám thu hộ: ", ""));
        tong.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        trang.getChildren().add(tong);

        javafx.stage.FileChooser fc = new javafx.stage.FileChooser();
        fc.setTitle("Lưu PDF bảng kê ca");
        fc.setInitialFileName("bang_ke_" + caHienTai.getMaCa() + ".pdf");
        fc.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("PDF", "*.pdf"));
        java.io.File tep = fc.showSaveDialog(tableBangKe.getScene().getWindow());
        if (tep != null) {
            try {
                util.PdfImageUtil.luuTrangA4(trang, tep.toPath());
                AlertUtil.baoThongTin("Đã lưu bảng kê PDF: " + tep.getAbsolutePath());
            } catch (java.io.IOException ex) {
                AlertUtil.baoLoi("Không lưu được PDF: " + ex.getMessage());
            }
        }
    }
}
