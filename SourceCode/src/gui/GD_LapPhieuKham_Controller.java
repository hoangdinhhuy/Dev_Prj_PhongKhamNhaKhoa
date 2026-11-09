package gui;

import java.time.LocalDate;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Modality;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import main.App;
import bus.HoaDon_BUS;
import bus.NhanVien_BUS;
import bus.PhieuKham_BUS;
import bus.PhongKham_BUS;
import entity.BenhNhan;
import entity.HoaDon;
import entity.NhanVien;
import entity.PhieuKham;
import entity.PhongKham;
import entity.TrangThaiPhieuKham;
import util.AlertUtil;

/**
 * (Mẫu GD_SoDoPhong + GD_DatPhong của N11 — khách sạn chọn phòng trống rồi đặt; bên mình
 * chọn phòng nhận bệnh rồi lập phiếu) Màn lập phiếu của lễ tân:
 * tile phòng và danh sách lượt tại chỗ; form lập phiếu theo phòng đã chọn.
 */
public class GD_LapPhieuKham_Controller {


    @FXML private FlowPane paneTiles;
    @FXML private ComboBox<String> cbb_loc;
    @FXML private TextField txt_timPhong;
    @FXML private Label lb_phongDaChon;
    @FXML private Label lb_nhanSuPhong;
    @FXML private Label lb_soLuot;
    @FXML private TableView<PhieuKham> tableLuot;
    @FXML private TableColumn<PhieuKham, String> cl_stt;
    @FXML private TableColumn<PhieuKham, String> cl_benhNhan;
    @FXML private TableColumn<PhieuKham, String> cl_trangThai;
    @FXML private Button btn_lapPhieu;

    private java.util.List<PhongKham> dsPhong = new java.util.ArrayList<>();
    private LocalDate homNay = LocalDate.now();
    private BenhNhan bnChoLapPhieu;   // bệnh nhân được chuyển tới từ Thu lệ phí (nếu có)
    private PhongKham phongDaChon;

    @FXML
    private void initialize() {
        cl_stt.setCellValueFactory(cd -> new SimpleStringProperty(
                String.valueOf(cd.getValue().getSoThuTu())));
        cl_benhNhan.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getBenhNhanString()));
        cl_trangThai.setCellValueFactory(cd -> new SimpleStringProperty(switch (cd.getValue().getTrangThai()) {
            case CHO_KHAM -> "Chờ khám";
            case DANG_KHAM -> "Đang khám";
            case HOAN_TAT -> "Hoàn tất";
        }));
        cbb_loc.getItems().addAll("Tất cả phòng", "Phòng đang khám",
                "Phòng có hàng chờ", "Phòng trống", "Phòng tắt nhận bệnh");
        cbb_loc.setValue("Tất cả phòng");
        cbb_loc.setOnAction(e -> taiLai());
        taiLai();
        // từ Thu lệ phí chuyển sang — giữ sẵn bệnh nhân để dialog lập phiếu tự điền
        Object yc = App.layYeuCauChuyenManHinh();
        if (yc instanceof BenhNhan bn) {
            bnChoLapPhieu = bn;
        }
    }


    @FXML
    private void taiLai() {
        try {
            homNay = LocalDate.now();
            dsPhong = PhongKham_BUS.danhSachTatCaPhong(App.session);
            String loc = cbb_loc.getValue();
            String tuKhoa = txt_timPhong.getText() == null ? ""
                    : txt_timPhong.getText().trim().toLowerCase(java.util.Locale.ROOT);
            paneTiles.getChildren().clear();
            for (PhongKham p : dsPhong) {
                boolean dangNhan = p.isDangNhanBenh();
                ThongTinPhong t = thongTin(p);
                if (loc != null && !"Tất cả phòng".equals(loc)
                        && !phongDungLoc(p, t, loc, dangNhan)) {
                    continue;
                }
                String ten = p.getTenPhong() == null ? "" : p.getTenPhong();
                if (!p.getMaPhong().toLowerCase(java.util.Locale.ROOT).contains(tuKhoa)
                        && !ten.toLowerCase(java.util.Locale.ROOT).contains(tuKhoa)) {
                    continue;
                }
                paneTiles.getChildren().add(taoTile(p, t, dangNhan));
            }
            if (phongDaChon != null) {
                dsPhong.stream().filter(p -> p.equals(phongDaChon)
                                && paneTiles.getChildren().stream().anyMatch(node -> p.getMaPhong()
                                        .equals(node.getUserData()))).findFirst()
                        .ifPresentOrElse(p -> chonPhong(p, thongTin(p)), this::boChonPhong);
            }
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }

    private boolean phongDungLoc(PhongKham p, ThongTinPhong t, String loc, boolean dangNhan) {
        return switch (loc) {
            case "Phòng đang khám" -> dangNhan && t.dangKham > 0;
            case "Phòng có hàng chờ" -> dangNhan && t.choKham > 0;
            case "Phòng trống" -> dangNhan && t.choKham == 0 && t.dangKham == 0;
            case "Phòng tắt nhận bệnh" -> !dangNhan;
            default -> true;
        };
    }

    private ThongTinPhong thongTin(PhongKham p) {
        ThongTinPhong t = new ThongTinPhong();
        var cho = PhieuKham_BUS.layDanhSachTheoNgayVaPhong(App.session, homNay,
                    p.getMaPhong(), TrangThaiPhieuKham.CHO_KHAM);
        var dang = PhieuKham_BUS.layDanhSachTheoNgayVaPhong(App.session, homNay,
                    p.getMaPhong(), TrangThaiPhieuKham.DANG_KHAM);
        t.choKham = cho.size();
        t.dangKham = dang.size();
        t.dsLuot = new java.util.ArrayList<>();
        t.dsLuot.addAll(cho);
        t.dsLuot.addAll(dang);
        t.dsLuot.sort(java.util.Comparator.comparingInt(PhieuKham::getSoThuTu));
        return t;
    }

    private static class ThongTinPhong {
        int choKham, dangKham;
        java.util.List<PhieuKham> dsLuot = new java.util.ArrayList<>();
    }

    private VBox taoTile(PhongKham p, ThongTinPhong t, boolean dangNhan) {
        String tenPhong = p.getTenPhong() == null ? p.getMaPhong() : p.getTenPhong();
        boolean daPhanCong = p.getMaBacSi() != null && p.getMaPhuTa() != null;
        Color mau;
        if (!dangNhan || !daPhanCong) {
            mau = Color.web("#8a8f98");          // xám — không tiếp nhận hoặc chưa phân công
        } else if (t.dangKham > 0) {
            mau = Color.web("#e05252");          // đỏ — đang khám
        } else if (t.choKham > 0) {
            mau = Color.web("#e5a13d");          // vàng — có hàng chờ
        } else {
            mau = Color.web("#31c57e");          // xanh lá — trống, tiếp nhận được ngay
        }

        VBox tile = new VBox(6);
        tile.setStyle("-fx-background-color: #" + mau.toString().substring(2, 8)
                + "; -fx-background-radius: 12; -fx-padding: 14; -fx-cursor: hand; -fx-opacity: "
                + (dangNhan && daPhanCong ? "1.0" : "0.78") + ";");
        tile.setPrefSize(230, 130);
        tile.setUserData(p.getMaPhong());
        Label lbPhong = new Label(p.getMaPhong());
        lbPhong.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");
        Label lbTen = new Label(tenPhong + (dangNhan ? "" : " — TẮT NHẬN BỆNH NHÂN"));
        lbTen.setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-wrap-text: true;");
        Label lbSo = new Label("Chờ " + t.choKham + " · Đang " + t.dangKham);
        lbSo.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;");
        Label lbTrangThai = new Label(!dangNhan ? "KHÔNG TIẾP NHẬN" : !daPhanCong
                ? "CHƯA PHÂN CÔNG" : t.dangKham > 0
                ? "ĐANG KHÁM" : t.choKham > 0 ? "CÓ HÀNG CHỜ" : "TRỐNG — LẬP PHIẾU ĐƯỢC");
        lbTrangThai.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
        tile.getChildren().addAll(lbPhong, lbTen, lbSo, lbTrangThai);
        tile.setOnMouseClicked(e -> chonPhong(p, t));
        return tile;
    }

    private void chonPhong(PhongKham phong, ThongTinPhong thongTin) {
        phongDaChon = phong;
        lb_phongDaChon.setText(phong.getMaPhong() + " · " + phong.getTenPhong());
        lb_soLuot.setText("Chờ " + thongTin.choKham + " · Đang khám " + thongTin.dangKham);
        tableLuot.setItems(FXCollections.observableArrayList(thongTin.dsLuot));
        NhanVien bacSi = NhanVien_BUS.layNhanVienTheoMa(App.session, phong.getMaBacSi());
        NhanVien phuTa = NhanVien_BUS.layNhanVienTheoMa(App.session, phong.getMaPhuTa());
        lb_nhanSuPhong.setText("Bác sĩ: " + (bacSi == null ? "chưa phân công" : bacSi.getHoTen())
                + " · Phụ tá: " + (phuTa == null ? "chưa phân công" : phuTa.getHoTen()));
        btn_lapPhieu.setDisable(!phong.isDangNhanBenh() || bacSi == null || phuTa == null
                || !bacSi.isConLamViec() || !phuTa.isConLamViec());
    }

    private void boChonPhong() {
        phongDaChon = null;
        lb_phongDaChon.setText("Chọn phòng");
        lb_nhanSuPhong.setText("");
        lb_soLuot.setText("");
        tableLuot.getItems().clear();
        btn_lapPhieu.setDisable(true);
    }

    @FXML
    private void moFormPhongDaChon() {
        if (phongDaChon != null && !btn_lapPhieu.isDisable()) {
            moFormLapPhieu(phongDaChon, phongDaChon.getTenPhong());
        }
    }

    /** Form lập phiếu dùng bác sĩ đã phân công cho phòng. */
    private void moFormLapPhieu(PhongKham phong, String tenPhong) {
        Dialog<Void> dialog = new Dialog<>();
        chuanBiDialog(dialog);
        dialog.setTitle("Lập phiếu khám");
        dialog.setHeaderText("Phòng đã chọn: " + phong.getMaPhong() + " · " + tenPhong
                + "\nBệnh nhân cũ: nhập SĐT / 12 số CCCD / mã BN — khách mới tạo hồ sơ ở màn Hồ sơ bệnh nhân.");

        TimKiemBenhNhanBox boxBN = new TimKiemBenhNhanBox();
        if (bnChoLapPhieu != null) {
            boxBN.datBenhNhan(bnChoLapPhieu);
            bnChoLapPhieu = null;
        }
        Label lbBN = new Label("(chưa chọn bệnh nhân)");

        NhanVien bacSi = NhanVien_BUS.layNhanVienTheoMa(App.session, phong.getMaBacSi());
        NhanVien phuTa = NhanVien_BUS.layNhanVienTheoMa(App.session, phong.getMaPhuTa());
        Label lbNhanSu = new Label("Bác sĩ: " + (bacSi == null ? "chưa phân công" : bacSi.getHoTen())
                + " · Phụ tá: " + (phuTa == null ? "chưa phân công" : phuTa.getHoTen()));

        TextField txtLyDo = new TextField();
        txtLyDo.setPromptText("Lý do khám (vd: Đau răng 36)");
        txtLyDo.setMaxWidth(420);
        Label lbKetQua = new Label("");

        boxBN.setNguoiChon(bn -> lbBN.setText("Đã chọn: " + bn.getMaBenhNhan() + " - " + bn.getHoTen()));

        Button btnLap = new Button("Lập phiếu & hiển thị phiếu in");
        btnLap.getStyleClass().add("btn-primary");
        btnLap.setOnAction(e -> {
            BenhNhan bn = boxBN.getBenhNhanDaChon();
            if (bn == null) {
                AlertUtil.baoLoi("Chưa chọn bệnh nhân — nhập SĐT/CCCD/mã BN để tìm khách cũ,"
                        + " hoặc tạo hồ sơ mới ở màn Hồ sơ bệnh nhân.");
                return;
            }
            try {
                HoaDon hoaDonPhi = HoaDon_BUS.layHoaDonPhiChuaDung(App.session, bn.getMaBenhNhan());
                PhieuKham phieu = PhieuKham_BUS.lapPhieuKham(App.session, bn,
                        phong.getMaBacSi(),
                        phong.getMaPhong(), txtLyDo.getText(), hoaDonPhi);
                lbKetQua.setText("Đã lập " + phieu.getMaPhieuKham() + " — STT " + phieu.getSoThuTu()
                        + " — phòng " + phieu.getMaPhong());
                lbKetQua.setStyle("-fx-text-fill: #1B7A3D; -fx-font-weight: bold;");
                hienPhieuTrongDialog(dialog, phieu, bn);
                btnLap.setDisable(true);
            } catch (RuntimeException ex) {
                AlertUtil.baoLoi(ex.getMessage());
            }
        });

        VBox content = new VBox(10,
                new Label("Bệnh nhân:"), boxBN, lbBN,
                lbNhanSu,
                new Label("Lý do khám*:"), txtLyDo,
                btnLap, lbKetQua);
        content.setStyle("-fx-padding: 8;");
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
        taiLai();
    }

    private void chuanBiDialog(Dialog<?> dialog) {
        dialog.initOwner(paneTiles.getScene().getWindow());
        dialog.initModality(Modality.WINDOW_MODAL);
        java.net.URL css = App.class.getResource("/gui/ungdung.css");
        if (css != null) {
            dialog.getDialogPane().getStylesheets().add(css.toExternalForm());
        }
    }

    private void hienPhieuTrongDialog(Dialog<Void> dialog, PhieuKham phieu, BenhNhan bn) {
        VBox trang = PhieuTiepNhanView.tao(phieu, bn, App.session);
        javafx.scene.control.ScrollPane xemTruoc = new javafx.scene.control.ScrollPane(trang);
        xemTruoc.setPrefHeight(260);
        Button btnPdf = new Button("Lưu PDF phiếu tiếp nhận");
        btnPdf.getStyleClass().add("print-action-btn");
        btnPdf.setOnAction(e -> {
            javafx.stage.FileChooser fc = new javafx.stage.FileChooser();
            fc.setTitle("Lưu phiếu tiếp nhận khám");
            fc.setInitialFileName("phieu_tiep_nhan_" + phieu.getMaPhieuKham() + ".pdf");
            fc.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("PDF", "*.pdf"));
            java.io.File tep = fc.showSaveDialog(dialog.getDialogPane().getScene().getWindow());
            if (tep != null) {
                try {
                    util.PdfImageUtil.luuTrangA4(trang, tep.toPath());
                    AlertUtil.baoThongTin("Đã lưu PDF: " + tep.getAbsolutePath());
                } catch (java.io.IOException ex) {
                    AlertUtil.baoLoi("Không lưu được PDF: " + ex.getMessage());
                }
            }
        });
        VBox khungPhieu = (VBox) dialog.getDialogPane().getContent();
        khungPhieu.getChildren().addAll(new Label("PHIẾU TIẾP NHẬN"), xemTruoc, btnPdf);
    }


    @FXML
    private void timPhong() {
        taiLai();
        if (paneTiles.getChildren().isEmpty()) {
            AlertUtil.baoThongTin("Không có phòng khớp điều kiện tìm kiếm.");
        }
    }

    @FXML
    private void tatCa() {
        txt_timPhong.clear();
        if ("Tất cả phòng".equals(cbb_loc.getValue())) taiLai();
        else cbb_loc.setValue("Tất cả phòng");
    }
}
