package gui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import main.App;
import bus.BaoCao_BUS;
import util.AlertUtil;
import util.FormatUtil;

/**
 * Controller Báo cáo lượt khám & xuất CSV (UC008).
 * Tuân thủ quy chuẩn:
 * - Tra cứu theo khoảng thời gian thực tế.
 * - Xuất CSV UTF-8 kèm BOM hỗ trợ tiếng Việt trọn vẹn trong Microsoft Excel.
 */
public class GD_BaoCao_Controller {

    @FXML private DatePicker dp_tuNgay;
    @FXML private DatePicker dp_denNgay;
    @FXML private Label lb_thongKeTong;

    @FXML private BarChart<String, Number> chart_luotKham;
    @FXML private TableView<Object[]> table_luotKham;
    @FXML private TableColumn<Object[], String> cl_sttLK;
    @FXML private TableColumn<Object[], String> cl_ngay;
    @FXML private TableColumn<Object[], String> cl_soLuot;

    @FXML private BarChart<String, Number> chart_dichVu;
    @FXML private TableView<Object[]> table_tanSuat;
    @FXML private TableColumn<Object[], String> cl_sttTS;
    @FXML private TableColumn<Object[], String> cl_dv;
    @FXML private TableColumn<Object[], String> cl_soLan;

    private ArrayList<Object[]> duLieuLuotKham = new ArrayList<>();
    private ArrayList<Object[]> duLieuTanSuat = new ArrayList<>();

    @FXML
    private void initialize() {
        cl_sttLK.setCellValueFactory(cd -> new SimpleStringProperty(
                String.valueOf(table_luotKham.getItems().indexOf(cd.getValue()) + 1)));
        cl_ngay.setCellValueFactory(cd -> new SimpleStringProperty(FormatUtil.ngay((LocalDate) cd.getValue()[0])));
        cl_soLuot.setCellValueFactory(cd -> new SimpleStringProperty(String.valueOf(cd.getValue()[1])));

        cl_sttTS.setCellValueFactory(cd -> new SimpleStringProperty(
                String.valueOf(table_tanSuat.getItems().indexOf(cd.getValue()) + 1)));
        cl_dv.setCellValueFactory(cd -> new SimpleStringProperty(String.valueOf(cd.getValue()[0])));
        cl_soLan.setCellValueFactory(cd -> new SimpleStringProperty(String.valueOf(cd.getValue()[1])));

        // Mặc định chọn 7 ngày gần nhất
        xemMacDinh7Ngay();
    }

    /**
     * Tải dữ liệu mặc định 7 ngày gần nhất.
     */
    @FXML
    public void xemMacDinh7Ngay() {
        LocalDate den = LocalDate.now();
        LocalDate tu = den.minusDays(6);
        dp_tuNgay.setValue(tu);
        dp_denNgay.setValue(den);
        thucHienThongKe();
    }

    /**
     * Thống kê theo tháng hiện tại.
     */
    @FXML
    public void xemThangNay() {
        LocalDate den = LocalDate.now();
        LocalDate tu = den.withDayOfMonth(1);
        dp_tuNgay.setValue(tu);
        dp_denNgay.setValue(den);
        thucHienThongKe();
    }

    @FXML
    private void xem() {
        thucHienThongKe();
    }

    private void thucHienThongKe() {
        try {
            LocalDate tu = dp_tuNgay.getValue();
            LocalDate den = dp_denNgay.getValue();

            duLieuLuotKham = BaoCao_BUS.thongKeLuotKham(App.session, tu, den);
            duLieuTanSuat = BaoCao_BUS.thongKeTanSuatDichVu(App.session, tu, den);

            // Cập nhật Table
            table_luotKham.setItems(FXCollections.observableArrayList(duLieuLuotKham));
            table_tanSuat.setItems(FXCollections.observableArrayList(duLieuTanSuat));

            // Tính tổng lượt khám
            int tongLK = 0;
            for (Object[] row : duLieuLuotKham) {
                tongLK += ((Number) row[1]).intValue();
            }
            lb_thongKeTong.setText("Tổng lượt khám trong kỳ: " + tongLK + " lượt");

            // Cập nhật Chart lượt khám
            napChartLuotKham(chart_luotKham, duLieuLuotKham);

            // Cập nhật Chart tần suất dịch vụ
            napChartDichVu(chart_dichVu, duLieuTanSuat);

            if (duLieuLuotKham.isEmpty() && duLieuTanSuat.isEmpty()) {
                AlertUtil.baoThongTin("Không có dữ liệu trong khoảng thời gian đã chọn.");
            }
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }

    private void napChartLuotKham(BarChart<String, Number> chart, ArrayList<Object[]> ds) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Lượt khám");
        for (Object[] row : ds) {
            String nhan = FormatUtil.ngay((LocalDate) row[0]);
            series.getData().add(new XYChart.Data<>(nhan, (Number) row[1]));
        }
        chart.getData().clear();
        chart.getData().add(series);
    }

    private void napChartDichVu(BarChart<String, Number> chart, ArrayList<Object[]> ds) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Lượt chỉ định");
        for (Object[] row : ds) {
            String ten = String.valueOf(row[0]);
            series.getData().add(new XYChart.Data<>(ten, (Number) row[1]));
        }
        chart.getData().clear();
        chart.getData().add(series);
    }

    @FXML
    private void xuatCsv() {
        if (duLieuLuotKham.isEmpty() && duLieuTanSuat.isEmpty()) {
            AlertUtil.baoLoi("Chưa có dữ liệu thống kê — vui lòng bấm Xem thống kê trước khi xuất CSV.");
            return;
        }

        FileChooser fc = new FileChooser();
        fc.setTitle("Xuất báo cáo lượt khám & tần suất dịch vụ (CSV)");
        String timestamp = java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));
        fc.setInitialFileName("BaoCao_NhaKhoa_" + timestamp + ".csv");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV (*.csv)", "*.csv"));

        File file = fc.showSaveDialog(chart_luotKham.getScene() == null ? null : chart_luotKham.getScene().getWindow());
        if (file == null) {
            return;
        }

        try (BufferedWriter w = java.nio.file.Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            // Ghi Byte Order Mark (BOM) để Microsoft Excel hiển thị tiếng Việt UTF-8 chuẩn xác
            w.write('\ufeff');

            w.write("PHÒNG KHÁM NHA KHOA SÀI GÒN - BÁO CÁO HOẠT ĐỘNG KHÁM CHỮA BỆNH");
            w.newLine();
            w.write("Khoảng thời gian:;" + dp_tuNgay.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    + " đến " + dp_denNgay.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            w.newLine();
            w.newLine();

            // Phần 1: Lượt khám
            w.write("I. THỐNG KÊ LƯỢT KHÁM THEO NGÀY");
            w.newLine();
            w.write("STT;Ngày khám;Số lượt khám");
            w.newLine();
            int stt = 1;
            int tongLK = 0;
            for (Object[] row : duLieuLuotKham) {
                w.write(stt++ + ";" + FormatUtil.ngay((LocalDate) row[0]) + ";" + row[1]);
                w.newLine();
                tongLK += ((Number) row[1]).intValue();
            }
            w.write("TỔNG CỘNG;;" + tongLK);
            w.newLine();
            w.newLine();

            // Phần 2: Tần suất dịch vụ
            w.write("II. TẦN SUẤT SỬ DỤNG DỊCH VỤ ĐIỀU TRỊ");
            w.newLine();
            w.write("STT;Tên dịch vụ;Số lượt chỉ định");
            w.newLine();
            stt = 1;
            int tongCD = 0;
            for (Object[] row : duLieuTanSuat) {
                w.write(stt++ + ";" + String.valueOf(row[0]).replace(";", ",") + ";" + row[1]);
                w.newLine();
                tongCD += ((Number) row[1]).intValue();
            }
            w.write("TỔNG LƯỢT CHỈ ĐỊNH;;" + tongCD);
            w.newLine();

            AlertUtil.baoThongTin("Xuất file CSV thành công:\n" + file.getAbsolutePath());
        } catch (IOException ex) {
            AlertUtil.baoLoi("Lỗi khi ghi file CSV: " + ex.getMessage());
        }
    }
}
