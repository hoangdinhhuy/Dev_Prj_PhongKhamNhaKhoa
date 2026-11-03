package gui;

import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.PhieuKham;
import entity.SessionContext;
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

/** Hóa đơn chi tiết thanh toán cuối lượt, dùng chung cho xem trước, in và lưu PDF. */
public final class HoaDonThanhToanView {
    private static final String XANH = "#075AA4";
    private static final String XAM = "#465363";

    private HoaDonThanhToanView() { }

    public static VBox tao(HoaDon hoaDon, PhieuKham phieu, List<ChiTietHoaDon> chiTiet,
                           SessionContext thuNgan) {
        VBox trang = new VBox(16);
        trang.setPrefSize(760, 1075);
        trang.setMinSize(760, 1075);
        trang.setMaxSize(760, 1075);
        trang.setPadding(new Insets(34));
        trang.setStyle("-fx-background-color:white; -fx-border-color:#C8D5E5; -fx-border-width:1;");

        ImageView logo = new ImageView(new Image("/pic/logo_glow.png"));
        logo.setFitWidth(62);
        logo.setFitHeight(62);
        logo.setPreserveRatio(true);
        Label ten = nhan("NHA KHOA SÀI GÒN", 26, XANH, true);
        Label diaChi = nhan("397 Nguyễn Kiệm, Phường Đức Nhuận, TP.HCM", 14, XAM, false);
        HBox tieuDePhongKham = new HBox(16, logo, new VBox(4, ten, diaChi));
        tieuDePhongKham.setAlignment(Pos.CENTER_LEFT);
        trang.getChildren().addAll(tieuDePhongKham, vach(XANH));
        Label tieuDe = nhan("HÓA ĐƠN THANH TOÁN DỊCH VỤ", 27, "#202733", true);
        tieuDe.setMaxWidth(Double.MAX_VALUE);
        tieuDe.setAlignment(Pos.CENTER);
        String ngayGio = hoaDon.getNgayThu() == null ? "—"
                : hoaDon.getNgayThu().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        Label ngay = nhan("Ngày thu: " + ngayGio, 16, XAM, false);
        ngay.setMaxWidth(Double.MAX_VALUE);
        ngay.setAlignment(Pos.CENTER);
        Label daThanhToan = nhan("ĐÃ THANH TOÁN", 14, "#16803C", true);
        daThanhToan.setMaxWidth(Double.MAX_VALUE);
        daThanhToan.setAlignment(Pos.CENTER);
        trang.getChildren().addAll(new VBox(5, tieuDe, ngay, daThanhToan), vach("#B9C8D8"));
        trang.getChildren().addAll(
                dong("Mã hóa đơn", hoaDon.getMaHoaDon()),
                dong("Mã lượt khám", phieu.getMaPhieuKham()),
                dong("Mã bệnh nhân", hoaDon.getMaBenhNhan()),
                dong("Họ tên bệnh nhân", phieu.getTenBenhNhan()));

        HBox tieuDeBang = new HBox(8, cot("Dịch vụ", 300), cot("SL", 40),
                cot("Đơn giá", 145), cot("Thành tiền", 165));
        tieuDeBang.setPadding(new Insets(9, 8, 9, 8));
        tieuDeBang.setStyle("-fx-background-color:#E7EEF6; -fx-font-weight:bold;");
        VBox bang = new VBox(0, tieuDeBang);
        for (ChiTietHoaDon ct : chiTiet) {
            HBox dong = new HBox(8, cot(ct.getTenDichVu(), 300), cot(String.valueOf(ct.getSoLuong()), 40),
                    cot(FormatUtil.tien(ct.getDonGia()), 145), cot(FormatUtil.tien(ct.getThanhTien()), 165));
            dong.setPadding(new Insets(9, 8, 9, 8));
            dong.setStyle("-fx-border-color:transparent transparent #E1E7EF transparent;");
            bang.getChildren().add(dong);
        }
        trang.getChildren().add(bang);

        java.math.BigDecimal tong = chiTiet.stream().map(ChiTietHoaDon::getThanhTien)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        String tienKhachDua = hoaDon.getTienKhachDua() == null ? FormatUtil.tien(tong)
                : FormatUtil.tien(hoaDon.getTienKhachDua());
        java.math.BigDecimal tienThua = (hoaDon.getTienKhachDua() == null ? tong : hoaDon.getTienKhachDua())
                .subtract(tong).max(java.math.BigDecimal.ZERO);
        trang.getChildren().addAll(vach("#B9C8D8"),
                dong("Tổng thanh toán", FormatUtil.tien(tong)),
                dong("Hình thức", hoaDon.getHinhThucString()),
                dong("Khách đã trả", tienKhachDua),
                dong("Tiền thừa", FormatUtil.tien(tienThua)),
                dong("Nhân viên thu", thuNgan == null ? hoaDon.getMaNhanVien() : thuNgan.getHoTen()));
        Region day = new Region();
        VBox.setVgrow(day, Priority.ALWAYS);
        Label camOn = nhan("Cảm ơn Quý khách đã sử dụng dịch vụ tại Nha khoa Sài Gòn.", 15, XAM, false);
        camOn.setWrapText(true);
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
        label.setAlignment(Pos.CENTER_LEFT);
        return label;
    }

    private static HBox dong(String ten, String giaTri) {
        Label trai = nhan(ten, 15, XAM, false);
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
