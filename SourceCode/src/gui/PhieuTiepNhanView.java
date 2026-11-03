package gui;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;
import entity.BenhNhan;
import entity.PhieuKham;
import entity.SessionContext;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import util.FormatUtil;

/** Mau phieu tiep nhan A4 dung chung cho ban xem truoc, in va luu PDF. */
public final class PhieuTiepNhanView {
    private static final String XANH = "#075AA4";
    private static final String DEN = "#202733";

    private PhieuTiepNhanView() { }

    public static VBox tao(PhieuKham phieu, BenhNhan benhNhan, SessionContext nhanVien) {
        VBox trang = new VBox(20);
        trang.setPrefSize(760, 1075);
        trang.setMinSize(760, 1075);
        trang.setMaxSize(760, 1075);
        trang.setPadding(new Insets(34));
        trang.setStyle("-fx-background-color: white; -fx-border-color: #C8D5E5; -fx-border-width: 1;");

        ImageView logo = new ImageView(new Image("/pic/logo_glow.png"));
        logo.setFitWidth(74);
        logo.setFitHeight(74);
        logo.setPreserveRatio(true);
        VBox tenPhongKham = new VBox(4,
                nhan("NHA KHOA SÀI GÒN", 28, XANH, true),
                nhan("397 Nguyễn Kiệm, Phường Đức Nhuận, TP.HCM", 14, "#424C5B", false));
        tenPhongKham.setAlignment(Pos.CENTER_LEFT);
        HBox dauTrang = new HBox(18, logo, tenPhongKham);
        dauTrang.setAlignment(Pos.CENTER_LEFT);
        trang.getChildren().addAll(dauTrang, vach(XANH));

        Label tieuDe = nhan("PHIẾU TIẾP NHẬN KHÁM", 34, DEN, true);
        tieuDe.setMaxWidth(Double.MAX_VALUE);
        tieuDe.setAlignment(Pos.CENTER);
        Label ngay = nhan("Ngày " + FormatUtil.ngay(phieu.getNgayKham()), 18, "#424C5B", false);
        ngay.setMaxWidth(Double.MAX_VALUE);
        ngay.setAlignment(Pos.CENTER);
        VBox tieuDeBox = new VBox(8, tieuDe, ngay);
        trang.getChildren().add(tieuDeBox);

        VBox stt = new VBox(6, nhan("SỐ THỨ TỰ", 18, "#424C5B", false),
                nhan(String.format("%03d", phieu.getSoThuTu()), 50, XANH, true));
        VBox phong = new VBox(6, nhan("PHÒNG KHÁM", 18, "#424C5B", false),
                nhan(phieu.getMaPhong(), 42, XANH, true));
        phong.setAlignment(Pos.CENTER_RIGHT);
        Region can = new Region();
        HBox.setHgrow(can, Priority.ALWAYS);
        trang.getChildren().addAll(new HBox(stt, can, phong), vach("#B9C8D8"));

        String ngaySinh = benhNhan == null || benhNhan.getNgaySinh() == null ? "—"
                : FormatUtil.ngay(benhNhan.getNgaySinh());
        VBox thongTin = new VBox(16,
                dong("Mã phiếu", phieu.getMaPhieuKham()),
                dong("Mã bệnh nhân", phieu.getMaBenhNhan()),
                dong("Họ tên bệnh nhân", phieu.getTenBenhNhan()),
                dong("Ngày sinh", ngaySinh),
                dong("Bác sĩ phụ trách", phieu.getTenBacSi()));
        trang.getChildren().addAll(thongTin, vach("#B9C8D8"));

        Label lyDo = nhan(phieu.getLyDoKham(), 20, DEN, true);
        lyDo.setWrapText(true);
        trang.getChildren().add(new VBox(10, nhan("Lý do khám", 17, "#424C5B", false), lyDo));

        Label chiDan = nhan("Vui lòng đến phòng " + phieu.getMaPhong()
                + ", chờ gọi theo số thứ tự và xuất trình phiếu cho nhân viên phụ trách.",
                17, DEN, false);
        chiDan.setWrapText(true);
        VBox huongDan = new VBox(10, nhan("HƯỚNG DẪN", 22, XANH, true), chiDan);
        huongDan.setPadding(new Insets(18));
        huongDan.setStyle("-fx-background-color: #E4F2FF; -fx-background-radius: 12;");
        trang.getChildren().add(huongDan);

        Region khoangTrong = new Region();
        VBox.setVgrow(khoangTrong, Priority.ALWAYS);
        trang.getChildren().add(khoangTrong);

        VBox tiepNhan = new VBox(8, nhan("Nhân viên tiếp nhận", 17, "#424C5B", false),
                nhan(nhanVien == null ? "—" : nhanVien.getHoTen(), 21, DEN, true));
        VBox maTraCuu = new VBox(5, maQr(phieu.getMaPhieuKham()),
                nhan("Mã phiếu (QR)", 13, "#424C5B", false));
        maTraCuu.setAlignment(Pos.CENTER);
        HBox cuoiPhieu = new HBox(tiepNhan, new Region(), maTraCuu);
        HBox.setHgrow(cuoiPhieu.getChildren().get(1), Priority.ALWAYS);
        trang.getChildren().addAll(cuoiPhieu, vach("#B9C8D8"));
        Label luuY = nhan("Phiếu dùng để tiếp nhận và hướng dẫn khám bệnh, không thay thế hóa đơn thanh toán.",
                14, "#424C5B", false);
        luuY.setMaxWidth(Double.MAX_VALUE);
        luuY.setWrapText(true);
        luuY.setTextAlignment(TextAlignment.CENTER);
        luuY.setAlignment(Pos.CENTER);
        trang.getChildren().add(luuY);
        return trang;
    }

    private static HBox dong(String ten, String giaTri) {
        Label trai = nhan(ten, 17, "#424C5B", false);
        Label phai = nhan(giaTri == null ? "—" : giaTri, 18, DEN, true);
        phai.setWrapText(true);
        phai.setMaxWidth(420);
        phai.setAlignment(Pos.CENTER_RIGHT);
        phai.setTextAlignment(TextAlignment.RIGHT);
        Region cach = new Region();
        HBox.setHgrow(cach, Priority.ALWAYS);
        HBox hang = new HBox(10, trai, cach, phai);
        hang.setAlignment(Pos.CENTER_LEFT);
        return hang;
    }

    private static Region vach(String mau) {
        Region vach = new Region();
        vach.setPrefHeight(2);
        vach.setMinHeight(2);
        vach.setStyle("-fx-background-color: " + mau + ";");
        return vach;
    }

    private static Label nhan(String chu, int co, String mau, boolean dam) {
        Label label = new Label(chu == null ? "" : chu);
        label.setFont(Font.font("Segoe UI", dam ? FontWeight.BOLD : FontWeight.NORMAL, co));
        label.setTextFill(Color.web(mau));
        return label;
    }

    private static ImageView maQr(String maPhieu) {
        try {
            var maTran = new QRCodeWriter().encode(maPhieu, BarcodeFormat.QR_CODE, 90, 90);
            WritableImage anh = new WritableImage(90, 90);
            for (int y = 0; y < 90; y++) {
                for (int x = 0; x < 90; x++) {
                    anh.getPixelWriter().setColor(x, y, maTran.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return new ImageView(anh);
        } catch (Exception ex) {
            return new ImageView();
        }
    }
}
