package gui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
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
import util.FormatUtil;

/**
 * (Mẫu GD_SoDoPhong + GD_DatPhong của N11 — khách sạn chọn phòng trống rồi đặt; bên mình
 * chọn phòng nhận bệnh rồi lập phiếu) Màn lập phiếu của lễ tân:
 * tile phòng tô màu + dialog "Lập phiếu khám tại phòng này" (phòng cố định theo tile,
 * bác sĩ gợi ý ít hàng chờ nhất).
 */
public class GD_LapPhieuKham_Controller {


    @FXML private FlowPane paneTiles;
    @FXML private ComboBox<String> cbb_loc;
    @FXML private TextField txt_timPhong;

    private java.util.List<PhongKham> dsPhong = new java.util.ArrayList<>();
    private LocalDate homNay = LocalDate.now();
    private BenhNhan bnChoLapPhieu;   // bệnh nhân được chuyển tới từ Thu lệ phí (nếu có)

    @FXML
    private void initialize() {
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
            dsPhong = PhongKham_BUS.danhSachTatCaPhong(App.session);
            String loc = cbb_loc.getValue();
            paneTiles.getChildren().clear();
            for (PhongKham p : dsPhong) {
                boolean dangNhan = p.isDangNhanBenh();
                ThongTinPhong t = thongTin(p);
                if (loc != null && !"Tất cả phòng".equals(loc)
                        && !phongDungLoc(p, t, loc, dangNhan)) {
                    continue;
                }
                paneTiles.getChildren().add(taoTile(p, t, dangNhan));
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
        try {
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
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
        return t;
    }

    private static class ThongTinPhong {
        int choKham, dangKham;
        java.util.List<PhieuKham> dsLuot = new java.util.ArrayList<>();
    }

    private VBox taoTile(PhongKham p, ThongTinPhong t, boolean dangNhan) {
        String tenPhong = p.getTenPhong() == null ? p.getMaPhong() : p.getTenPhong();
        Color mau;
        if (!dangNhan) {
            mau = Color.web("#8a8f98");          // xám — phụ tá đã tắt nhận bệnh nhân
        } else if (t.dangKham > 0) {
            mau = Color.web("#e05252");          // đỏ — đang khám
        } else if (t.choKham > 0) {
            mau = Color.web("#e5a13d");          // vàng — có hàng chờ
        } else {
            mau = Color.web("#31c57e");          // xanh lá — trống, tiếp nhận được ngay
        }

        VBox tile = new VBox(6);
        tile.setStyle("-fx-background-color: #" + mau.toString().substring(2, 8)
                + "; -fx-background-radius: 12; -fx-padding: 14; -fx-cursor: "
                + (dangNhan ? "hand" : "default") + "; -fx-opacity: "
                + (dangNhan ? "1.0" : "0.78") + ";");
        tile.setPrefSize(230, 130);
        Label lbPhong = new Label(p.getMaPhong());
        lbPhong.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");
        Label lbTen = new Label(tenPhong + (dangNhan ? "" : " — TẮT NHẬN BỆNH NHÂN"));
        lbTen.setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-wrap-text: true;");
        Label lbSo = new Label("Chờ " + t.choKham + " · Đang " + t.dangKham);
        lbSo.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;");
        Label lbTrangThai = new Label(!dangNhan ? "KHÔNG TIẾP NHẬN" : t.dangKham > 0
                ? "ĐANG KHÁM" : t.choKham > 0 ? "CÓ HÀNG CHỜ" : "TRỐNG — LẬP PHIẾU ĐƯỢC");
        lbTrangThai.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
        tile.getChildren().addAll(lbPhong, lbTen, lbSo, lbTrangThai);
        if (dangNhan) {
            tile.setOnMouseClicked(e -> moDialogLapPhieu(p, t, tenPhong));
        } else {
            tile.setOnMouseClicked(e -> AlertUtil.baoThongTin("Phòng "
                    + p.getMaPhong() + " đang tắt nhận bệnh nhân — liên hệ Phụ tá bật lại."));
        }
        return tile;
    }

    /** (N11: click phòng → dialog → hành động) Dialog: lượt hôm nay + nút mở form lập phiếu. */
    private void moDialogLapPhieu(PhongKham p, ThongTinPhong t, String tenPhong) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Phòng " + p.getMaPhong());
        dialog.setHeaderText("Phòng " + p.getMaPhong() + " · " + tenPhong
                + " — Chờ " + t.choKham + " · Đang khám " + t.dangKham);

        TableView<PhieuKham> table = new TableView<>();
        TableColumn<PhieuKham, String> cStt = new TableColumn<>("STT");
        cStt.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(
                String.valueOf(cd.getValue().getSoThuTu())));
        TableColumn<PhieuKham, String> cBn = new TableColumn<>("Bệnh nhân");
        cBn.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(
                cd.getValue().getBenhNhanString()));
        TableColumn<PhieuKham, String> cTt = new TableColumn<>("Trạng thái");
        cTt.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(
                cd.getValue().getTrangThai().toString()));
        table.getColumns().setAll(cStt, cBn, cTt);
        table.setItems(FXCollections.observableArrayList(t.dsLuot));
        table.setPrefSize(520, 220);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        Button btnLap = new Button("Lập phiếu khám tại phòng này →");
        btnLap.getStyleClass().add("btn-primary");
        btnLap.setOnAction(e -> {
            dialog.close();
            moFormLapPhieu(p, tenPhong);
        });

        VBox content = new VBox(10, table, new HBox(10, btnLap));
        content.setStyle("-fx-padding: 8;");
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    /** (N11 GD_DatPhong) Form lập phiếu: phòng CỐ ĐỊNH theo tile, bác sĩ gợi ý ít chờ nhất. */
    private void moFormLapPhieu(PhongKham phong, String tenPhong) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Lập phiếu khám");
        dialog.setHeaderText("Phòng đã chọn: " + phong.getMaPhong() + " · " + tenPhong
                + "\nBệnh nhân cũ: nhập SĐT / 12 số CCCD / mã BN — khách mới tạo hồ sơ ở màn Hồ sơ bệnh nhân.");

        TimKiemBenhNhanBox boxBN = new TimKiemBenhNhanBox();
        if (bnChoLapPhieu != null) {
            boxBN.datBenhNhan(bnChoLapPhieu);
            bnChoLapPhieu = null;
        }
        Label lbBN = new Label("(chưa chọn bệnh nhân)");

        ComboBox<NhanVien> cbbBS = new ComboBox<>();
        var dsBs = NhanVien_BUS.danhSachBacSiDangLamViec(App.session);
        NhanVien totNhat = null;
        int itNhat = Integer.MAX_VALUE;
        for (NhanVien nv : dsBs) {
            int cho = PhieuKham_BUS.soPhieuDangChoCuaBacSi(App.session, nv.getMaNhanVien());
            if (cho < itNhat) {
                itNhat = cho;
                totNhat = nv;
            }
        }
        final NhanVien bsGoiY = totNhat;
        final int soChoGoiY = itNhat;
        cbbBS.setItems(FXCollections.observableArrayList(dsBs));
        cbbBS.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(NhanVien nv, boolean empty) {
                super.updateItem(nv, empty);
                setText(empty || nv == null ? null : nv.getMaNhanVien() + " - " + nv.getHoTen()
                        + (nv == bsGoiY ? "  (ít chờ nhất — " + soChoGoiY + " phiếu hôm nay)" : ""));
            }
        });
        cbbBS.setButtonCell(cbbBS.getCellFactory().call(null));
        cbbBS.setValue(bsGoiY);
        cbbBS.setMaxWidth(420);

        TextField txtLyDo = new TextField();
        txtLyDo.setPromptText("Lý do khám (vd: Đau răng 36)");
        txtLyDo.setMaxWidth(420);
        Label lbKetQua = new Label("");

        boxBN.setNguoiChon(bn -> lbBN.setText("Đã chọn: " + bn.getMaBenhNhan() + " - " + bn.getHoTen()));

        Button btnLap = new Button("Lập phiếu & hiển thị phiếu in");
        btnLap.getStyleClass().add("btn-primary");
        btnLap.setOnAction(e -> {
            BenhNhan bn = boxBN.getBenhNhanDaChon();
            NhanVien bs = cbbBS.getValue();
            if (bn == null) {
                AlertUtil.baoLoi("Chưa chọn bệnh nhân — nhập SĐT/CCCD/mã BN để tìm khách cũ,"
                        + " hoặc tạo hồ sơ mới ở màn Hồ sơ bệnh nhân.");
                return;
            }
            try {
                HoaDon hoaDonPhi = HoaDon_BUS.layHoaDonPhiChuaDung(App.session, bn.getMaBenhNhan());
                PhieuKham phieu = PhieuKham_BUS.lapPhieuKham(App.session, bn,
                        bs == null ? null : bs.getMaNhanVien(),
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
                new Label("Bác sĩ:"), cbbBS,
                new Label("Lý do khám*:"), txtLyDo,
                btnLap, lbKetQua);
        content.setStyle("-fx-padding: 8;");
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
        taiLai();
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
        String tu = txt_timPhong.getText() == null ? "" : txt_timPhong.getText().trim().toLowerCase();
        paneTiles.getChildren().clear();
        try {
            if (dsPhong.isEmpty()) {
                dsPhong = PhongKham_BUS.danhSachTatCaPhong(App.session);
            }
            for (PhongKham p : dsPhong) {
                String ten = (p.getTenPhong() == null ? "" : p.getTenPhong()).toLowerCase();
                if (tu.isEmpty() || p.getMaPhong().toLowerCase().contains(tu) || ten.contains(tu)) {
                    ThongTinPhong t = thongTin(p);
                    paneTiles.getChildren().add(taoTile(p, t, p.isDangNhanBenh()));
                }
            }
            if (paneTiles.getChildren().isEmpty()) {
                AlertUtil.baoThongTin("Không có phòng khớp từ khóa.");
            }
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }

    @FXML
    private void tatCa() {
        txt_timPhong.clear();
        cbb_loc.setValue("Tất cả phòng");
        taiLai();
    }
}
