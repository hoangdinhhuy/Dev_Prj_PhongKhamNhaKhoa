package gui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import main.App;
import bus.BaoCao_BUS;
import util.AlertUtil;
import util.FormatUtil;

/** UC008 - Bao cao luot kham + xuat CSV (chu file: Duy). CSV: UTF-8 BOM, phan cach ';'. */
public class GD_BaoCao_Controller {

    @FXML private DatePicker dp_tuNgay;
    @FXML private DatePicker dp_denNgay;
    @FXML private BarChart<String, Number> chart_luotKham;
    @FXML private BarChart<String, Number> chart_dichVu;
    @FXML private TableView<Object[]> table_luotKham;
    @FXML private TableView<Object[]> table_tanSuat;
    @FXML private TableColumn<Object[], String> cl_ngay;
    @FXML private TableColumn<Object[], String> cl_soLuot;
    @FXML private TableColumn<Object[], String> cl_dv;
    @FXML private TableColumn<Object[], String> cl_soLan;

    private ArrayList<Object[]> duLieuLuotKham = new ArrayList<>();
    private ArrayList<Object[]> duLieuTanSuat = new ArrayList<>();

    @FXML
    private void initialize() {
        cl_ngay.setCellValueFactory(cd -> new SimpleStringProperty(FormatUtil.ngay((LocalDate) cd.getValue()[0])));
        cl_soLuot.setCellValueFactory(cd -> new SimpleStringProperty(String.valueOf(cd.getValue()[1])));
        cl_dv.setCellValueFactory(cd -> new SimpleStringProperty(String.valueOf(cd.getValue()[0])));
        cl_soLan.setCellValueFactory(cd -> new SimpleStringProperty(String.valueOf(cd.getValue()[1])));
        dp_tuNgay.setValue(LocalDate.now().minusDays(7));
        dp_denNgay.setValue(LocalDate.now());
    }

    @FXML
    private void xem() {
        try {
            LocalDate tu = dp_tuNgay.getValue();
            LocalDate den = dp_denNgay.getValue();
            duLieuLuotKham = BaoCao_BUS.thongKeLuotKham(App.session, tu, den);
            duLieuTanSuat = BaoCao_BUS.thongKeTanSuatDichVu(App.session, tu, den);
            nap(table_luotKham, duLieuLuotKham);
            nap(table_tanSuat, duLieuTanSuat);
            napChart(chart_luotKham, duLieuLuotKham, "Lượt khám");
            napChart(chart_dichVu, duLieuTanSuat, "Lượt chỉ định");
            if (duLieuLuotKham.isEmpty() && duLieuTanSuat.isEmpty()) {
                AlertUtil.baoThongTin("Không có dữ liệu trong khoảng đã chọn.");
            }
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void nap(TableView<Object[]> table, ArrayList<Object[]> ds) {
        table.getItems().clear();
        table.setItems(FXCollections.observableArrayList(ds));
    }

    @SuppressWarnings("unchecked")
    private void napChart(BarChart<String, Number> chart, ArrayList<Object[]> ds, String tenChuoi) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(tenChuoi);
        for (Object[] row : ds) {
            String nhan = row[0] instanceof LocalDate d ? FormatUtil.ngay(d) : String.valueOf(row[0]);
            series.getData().add(new XYChart.Data<>(nhan, (Number) row[1]));
        }
        chart.getData().clear();
        chart.getData().add(series);
    }

    @FXML
    private void xuatCsv() {
        if (duLieuLuotKham.isEmpty() && duLieuTanSuat.isEmpty()) {
            AlertUtil.baoLoi("Chưa có dữ liệu - bấm Xem trước khi xuất CSV.");
            return;
        }
        FileChooser fc = new FileChooser();
        fc.setTitle("Xuất CSV");
        fc.setInitialFileName("baocao_luotkham_"
                + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter
                        .ofPattern("yyyyMMdd_HHmm")) + ".csv");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        File file = fc.showSaveDialog(chart_luotKham.getScene() == null ? null
                : chart_luotKham.getScene().getWindow());
        if (file == null) {
            return;
        }
        try (BufferedWriter w = java.nio.file.Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            w.write('\ufeff');   // BOM de Excel hien dung tieng Viet
            w.write("BAO CAO LUOT KHAM THEO NGAY");
            w.newLine();
            w.write("Ngay;So luot kham");
            w.newLine();
            for (Object[] row : duLieuLuotKham) {
                w.write(FormatUtil.ngay((LocalDate) row[0]) + ";" + row[1]);
                w.newLine();
            }
            w.newLine();
            w.write("TAN SUAT DICH VU");
            w.newLine();
            w.write("Dich vu;So luot chi dinh");
            w.newLine();
            for (Object[] row : duLieuTanSuat) {
                w.write(String.valueOf(row[0]).replace(";", ",") + ";" + row[1]);
                w.newLine();
            }
            AlertUtil.baoThongTin("Đã xuất CSV: " + file.getAbsolutePath());
        } catch (IOException ex) {
            AlertUtil.baoLoi("Lỗi ghi file CSV: " + ex.getMessage());
        }
    }
}
