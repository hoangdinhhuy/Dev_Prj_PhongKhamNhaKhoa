package gui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import main.App;
import bus.BaoCao_BUS;
import entity.HangHoa;
import entity.SessionContext;
import util.AlertUtil;
import util.FormatUtil;

/**
 * Controller Tổng quan (Dashboard) cho Quản lý.
 * Trực quan hóa số liệu KPI để Quản lý nắm bắt ngay lập tức tình hình:
 * - 4 Thẻ KPI: Nhân viên đang làm, Dịch vụ hoạt động, Cảnh báo tồn kho, Lượt khám 7 ngày.
 * - Biểu đồ BarChart: Diễn biến lượt khám tuần hiện tại.
 * - Biểu đồ PieChart: Cơ cấu nhân sự phòng khám theo vai trò.
 * - Bảng cảnh báo khẩn cấp: Các mặt hàng sắp hết cần nhập gấp.
 */
public class GD_TongQuan_Controller {

    @FXML private Label lb_homNay;
    @FXML private Label lb_chaoMung;

    @FXML private Label lb_kpiNhanVien;
    @FXML private Label lb_subNhanVien;
    @FXML private Label lb_kpiDichVu;
    @FXML private Label lb_kpiHangHoa;
    @FXML private Label lb_kpiLuotKham;

    @FXML private BarChart<String, Number> chart_luotKhamTuan;
    @FXML private PieChart chart_coCauNhanSu;

    @FXML private Label lb_soMatHangCanNhap;
    @FXML private TableView<HangHoa> table_canhBaoTonKho;
    @FXML private TableColumn<HangHoa, String> cl_cbMa;
    @FXML private TableColumn<HangHoa, String> cl_cbTen;
    @FXML private TableColumn<HangHoa, String> cl_cbDonVi;
    @FXML private TableColumn<HangHoa, Integer> cl_cbTon;
    @FXML private TableColumn<HangHoa, Integer> cl_cbToiThieu;
    @FXML private TableColumn<HangHoa, String> cl_cbThieu;

    @FXML
    private void initialize() {
        SessionContext s = App.session;
        String ten = s == null ? "Quản lý" : s.getHoTen();
        String vaiTro = s == null || s.getChucVu() == null ? "Quản lý" : s.getChucVu().toString();

        lb_homNay.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        lb_chaoMung.setText("Xin chào " + ten + " (" + vaiTro + "), chúc bạn một ngày làm việc hiệu quả!");

        // Khởi tạo bảng cảnh báo nhanh
        cl_cbMa.setCellValueFactory(new PropertyValueFactory<>("maHangHoa"));
        cl_cbTen.setCellValueFactory(new PropertyValueFactory<>("tenHangHoa"));
        cl_cbDonVi.setCellValueFactory(new PropertyValueFactory<>("donViTinh"));
        cl_cbTon.setCellValueFactory(new PropertyValueFactory<>("soLuongTon"));
        cl_cbToiThieu.setCellValueFactory(new PropertyValueFactory<>("mucTonToiThieu"));
        // Thuộc tính dẫn xuất tính toán số lượng thiếu
        cl_cbThieu.setCellValueFactory(c -> new SimpleStringProperty(
                "-" + c.getValue().getSoLuongThieu() + " " + c.getValue().getDonViTinh()));

        // Tải số liệu trực quan hóa
        taiDuLieuTongQuan();
    }

    private void taiDuLieuTongQuan() {
        try {
            BaoCao_BUS.ThongKeTongQuan kpi = BaoCao_BUS.layTongQuanKpi(App.session);

            // 1. Cập nhật thẻ KPI
            lb_kpiNhanVien.setText(String.valueOf(kpi.tongNhanVienDangLamViec));
            Map<String, Integer> cc = kpi.coCauNhanSu;
            lb_subNhanVien.setText(String.format("BS: %d • LT: %d • PT: %d • QL: %d",
                    cc.getOrDefault("Bác sĩ", 0),
                    cc.getOrDefault("Lễ tân", 0),
                    cc.getOrDefault("Phụ tá", 0),
                    cc.getOrDefault("Quản lý", 0)));

            lb_kpiDichVu.setText(String.valueOf(kpi.tongDichVuDangPhucVu));
            lb_kpiHangHoa.setText(String.valueOf(kpi.soHangHoaCanBoSung));
            lb_kpiLuotKham.setText(String.valueOf(kpi.tongLuotKham7Ngay));

            // 2. Biểu đồ lượt khám 7 ngày (BarChart)
            XYChart.Series<String, Number> seriesLK = new XYChart.Series<>();
            seriesLK.setName("Lượt khám");
            for (Object[] row : kpi.luotKham7Ngay) {
                String nhan = FormatUtil.ngay((LocalDate) row[0]);
                seriesLK.getData().add(new XYChart.Data<>(nhan, (Number) row[1]));
            }
            chart_luotKhamTuan.getData().clear();
            chart_luotKhamTuan.getData().add(seriesLK);

            // 3. Biểu đồ cơ cấu nhân sự (PieChart)
            ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
            cc.forEach((tenCV, sl) -> {
                if (sl > 0) {
                    pieData.add(new PieChart.Data(tenCV + " (" + sl + ")", sl));
                }
            });
            chart_coCauNhanSu.setData(pieData);

            // 4. Bảng cảnh báo tồn kho
            table_canhBaoTonKho.setItems(FXCollections.observableArrayList(kpi.danhSachCanBoSung));
            lb_soMatHangCanNhap.setText(kpi.soHangHoaCanBoSung + " mặt hàng cần nhập gấp");

        } catch (RuntimeException ex) {
            AlertUtil.baoLoi("Lỗi tải thông tin tổng quan: " + ex.getMessage());
        }
    }
}
