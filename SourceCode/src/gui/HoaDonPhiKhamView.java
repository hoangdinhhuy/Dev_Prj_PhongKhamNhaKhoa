package gui;

import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.SessionContext;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import util.FormatUtil;

/** Bản in hóa đơn thu phí khám ban đầu. */
public final class HoaDonPhiKhamView {
    private HoaDonPhiKhamView() { }

    public static VBox tao(HoaDon hoaDon, List<ChiTietHoaDon> chiTiet, SessionContext thuNgan) {
        VBox trang = new VBox(14);
        trang.setPrefSize(760, 1075);
        trang.setMinSize(760, 1075);
        trang.setMaxSize(760, 1075);
        trang.setPadding(new Insets(34));
        trang.setStyle("-fx-background-color:white; -fx-border-color:#C8D5E5; -fx-border-width:1;");

        ImageView logo = new ImageView(new Image("/pic/logo_glow.png"));
        logo.setFitWidth(62);
        logo.setFitHeight(62);
        logo.setPreserveRatio(true);
        Label tenPhongKham = nhan("NHA KHOA SÀI GÒN", 26, "#075AA4", true);
        Label diaChi = nhan("397 Nguyễn Kiệm, Phường Đức Nhuận, TP.HCM", 14, "#465363", false);
        HBox dauTrang = new HBox(16, logo, new VBox(4, tenPhongKham, diaChi));
        dauTrang.setAlignment(Pos.CENTER_LEFT);
        trang.getChildren().addAll(dauTrang, vach("#075AA4"));

        Label tieuDe = nhan("HÓA ĐƠN PHÍ KHÁM BAN ĐẦU", 27, "#202733", true);
        tieuDe.setMaxWidth(Double.MAX_VALUE);
        tieuDe.setAlignment(Pos.CENTER);
        String ngayThu = hoaDon.getNgayThu() == null ? "—"
                : hoaDon.getNgayThu().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        Label thoiGian = nhan("Ngày thu: " + ngayThu, 16, "#465363", false);
        thoiGian.setMaxWidth(Double.MAX_VALUE);
        thoiGian.setAlignment(Pos.CENTER);
        Label trangThai = nhan("ĐÃ THANH TOÁN", 14, "#16803C", true);
        trangThai.setMaxWidth(Double.MAX_VALUE);
        trangThai.setAlignment(Pos.CENTER);
        trang.getChildren().addAll(new VBox(5, tieuDe, thoiGian, trangThai), vach("#B9C8D8"));

        trang.getChildren().addAll(
                dong("Mã hóa đơn", hoaDon.getMaHoaDon()),
                dong("Mã bệnh nhân", hoaDon.getMaBenhNhan()),
                dong("Họ tên bệnh nhân", hoaDon.getTenBenhNhan()));

        HBox tieuDeBang = new HBox(8, cot("Khoản thu", 350), cot("SL", 45),
                cot("Đơn giá", 145), cot("Thành tiền", 165));
        tieuDeBang.setPadding(new Insets(9, 8, 9, 8));
        tieuDeBang.setStyle("-fx-background-color:#E7EEF6; -fx-font-weight:bold;");
        VBox bang = new VBox(0, tieuDeBang);
        for (ChiTietHoaDon ct : chiTiet) {
            HBox dong = new HBox(8, cot(ct.getTenDichVu(), 350), cot(String.valueOf(ct.getSoLuong()), 45),
                    cot(FormatUtil.tien(ct.getDonGia()), 145), cot(FormatUtil.tien(ct.getThanhTien()), 165));
            dong.setPadding(new Insets(9, 8, 9, 8));
            dong.setStyle("-fx-border-color:transparent transparent #E1E7EF transparent;");
            bang.getChildren().add(dong);
        }
        trang.getChildren().add(bang);

        BigDecimal tong = chiTiet.stream().map(ChiTietHoaDon::getThanhTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal daDua = hoaDon.getTienKhachDua() == null ? tong : hoaDon.getTienKhachDua();
        trang.getChildren().addAll(vach("#B9C8D8"),
                dong("Tổng thanh toán", FormatUtil.tien(tong)),
                dong("Hình thức", hoaDon.getHinhThucString()),
                dong("Khách đã trả", FormatUtil.tien(daDua)),
                dong("Tiền thừa", FormatUtil.tien(daDua.subtract(tong).max(BigDecimal.ZERO))),
                dong("Nhân viên thu", thuNgan == null ? hoaDon.getMaNhanVien() : thuNgan.getHoTen()));
        Region day = new Region();
        VBox.setVgrow(day, Priority.ALWAYS);
        Label camOn = nhan("Cảm ơn Quý khách đã đến Nha khoa Sài Gòn.", 15, "#465363", false);
        camOn.setMaxWidth(Double.MAX_VALUE);
        camOn.setAlignment(Pos.CENTER);
        trang.getChildren().addAll(day, vach("#B9C8D8"), camOn);
        return trang;
    }

    private static Label cot(String text, double width) {
        Label label = nhan(text == null ? "—" : text, 13, "#202733", false);
        label.setWrapText(true);
        label.setMinWidth(width);
        label.setPrefWidth(width);
        label.setMaxWidth(width);
        return label;
    }

    private static HBox dong(String ten, String giaTri) {
        Label trai = nhan(ten, 15, "#465363", false);
        Label phai = nhan(giaTri == null ? "—" : giaTri, 15, "#202733", true);
        phai.setWrapText(true);
        phai.setAlignment(Pos.CENTER_RIGHT);
        Region cach = new Region();
        HBox.setHgrow(cach, Priority.ALWAYS);
        return new HBox(10, trai, cach, phai);
    }

    private static Region vach(String mau) {
        Region line = new Region();
        line.setPrefHeight(2);
        line.setStyle("-fx-background-color:" + mau + ";");
        return line;
    }

    private static Label nhan(String text, int size, String mau, boolean dam) {
        Label label = new Label(text);
        label.setStyle("-fx-font-family:'Segoe UI'; -fx-font-size:" + size + "px; -fx-text-fill:" + mau
                + "; -fx-font-weight:" + (dam ? "bold" : "normal") + ";");
        return label;
    }
}
