package gui;

import java.util.ArrayList;
import java.util.Map;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import main.App;
import bus.BenhNhan_BUS;
import bus.DichVu_BUS;
import bus.HoaDon_BUS;
import bus.HoSoBenhAn_BUS;
import bus.KhamBenh_BUS;
import entity.ChiDinhDichVu;
import entity.ChiTietCapThuoc;
import entity.DichVu;
import entity.HoSoBenhAn;
import entity.PhieuKham;
import entity.Thuoc;
import util.AlertUtil;
import util.FormatUtil;
import util.MaThongBao;
import util.ValidateUtil;

/** UC004 - Ghi nhan kham va chan doan (chu file: Thang). */
public class GD_KhamBenh_Controller {

    @FXML private TableView<PhieuKham> table_danhSach;
    @FXML private TableColumn<PhieuKham, String> cl_stt;
    @FXML private TableColumn<PhieuKham, String> cl_ma;
    @FXML private TableColumn<PhieuKham, String> cl_bn;
    @FXML private TableColumn<PhieuKham, String> cl_tt;
    @FXML private Label lb_bn;
    @FXML private Label lb_tienSu;
    @FXML private TextArea ta_chanDoan;
    @FXML private TextField txt_ketQua;
    @FXML private TextField txt_thuocTuMua;
    @FXML private TextField txt_timDichVu;
    @FXML private ComboBox<DichVu> cbb_dichVu;
    @FXML private javafx.scene.layout.VBox pn_khungRang;
    @FXML private javafx.scene.layout.HBox hangTren;
    @FXML private javafx.scene.layout.HBox hangDuoi;
    @FXML private TextArea ta_phacDo;
    @FXML private javafx.scene.control.DatePicker dp_ngayHen;
    @FXML private ComboBox<Thuoc> cbb_thuoc;
    @FXML private TextField txt_soLuong;
    @FXML private TextField txt_ghiChuCap;
    @FXML private TableView<ChiDinhDichVu> table_chiDinh;
    @FXML private TableColumn<ChiDinhDichVu, String> cl_dv;
    @FXML private TableColumn<ChiDinhDichVu, String> cl_soRang;
    @FXML private TableColumn<ChiDinhDichVu, String> cl_gia;
    @FXML private TableView<ChiTietCapThuoc> table_thuoc;
    @FXML private TableColumn<ChiTietCapThuoc, String> cl_tenThuoc;
    @FXML private TableColumn<ChiTietCapThuoc, String> cl_soLuongThuoc;
    @FXML private TableColumn<ChiTietCapThuoc, String> cl_ghiChuThuoc;
    @FXML private Button btn_taiLai;
    @FXML private Button btn_luu;
    @FXML private Button btn_hoanTat;

    private ArrayList<ChiDinhDichVu> chiDinhTam = new ArrayList<>();
    private ArrayList<ChiTietCapThuoc> thuocTam = new ArrayList<>();
    private final java.util.LinkedHashSet<String> soRangChon = new java.util.LinkedHashSet<>();
    private final Map<String, javafx.scene.control.ToggleButton> nutRang = new java.util.LinkedHashMap<>();

    /** (Gop y GV 2/7) Khung rang FDI 32 o — bấm chọn/bỏ chọn răng cần điều trị. */
    private void taoKhungRang() {
        String[] hang1 = {"18","17","16","15","14","13","12","11","21","22","23","24","25","26","27","28"};
        String[] hang2 = {"48","47","46","45","44","43","42","41","31","32","33","34","35","36","37","38"};
        for (String so : hang1) {
            hangTren.getChildren().add(taoNutRang(so));
        }
        for (String so : hang2) {
            hangDuoi.getChildren().add(taoNutRang(so));
        }
        anKhungRang();
    }

    private javafx.scene.control.ToggleButton taoNutRang(String so) {
        javafx.scene.control.ToggleButton b = new javafx.scene.control.ToggleButton(so);
        b.setPrefSize(46, 40);
        b.getStyleClass().add("nut-rang");
        b.setOnAction(e -> {
            if (soRangChon.contains(so)) {
                soRangChon.remove(so);
                b.setSelected(false);
            } else {
                soRangChon.add(so);
                b.setSelected(true);
            }
        });
        nutRang.put(so, b);
        return b;
    }

    /** Hien khung rang khi dich vu thuoc nhom dieu tri rang cu the (han/tram/trat/nho/tuy/mao). */
    private void anKhungRang() {
        boolean dieuTriRang = cbb_dichVu.getValue() != null
                && util.GoiTuKhoa.laDieuTriRang(cbb_dichVu.getValue().getTenDichVu());
        pn_khungRang.setVisible(dieuTriRang);
        pn_khungRang.setManaged(dieuTriRang);
    }

    private void resetKhungRang() {
        soRangChon.clear();
        nutRang.values().forEach(b -> b.setSelected(false));
    }

    @FXML
    private void initialize() {
        cl_stt.setCellValueFactory(cd -> new SimpleStringProperty(String.valueOf(cd.getValue().getSoThuTu())));
        cl_ma.setCellValueFactory(new PropertyValueFactory<>("maPhieuKham"));
        cl_bn.setCellValueFactory(new PropertyValueFactory<>("tenBenhNhan"));
        cl_tt.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getTrangThai().toString()));
        cl_tt.setCellFactory(col -> new javafx.scene.control.TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    getStyleClass().removeAll("badge-cho", "badge-dang", "badge-hoan");
                } else {
                    setText(item);
                    getStyleClass().removeAll("badge-cho", "badge-dang", "badge-hoan");
                    getStyleClass().add(switch (item) {
                        case "Chờ khám" -> "badge-cho";
                        case "Đang khám" -> "badge-dang";
                        default -> "badge-hoan";
                    });
                }
            }
        });
        table_danhSach.setOnMouseClicked(e -> {
            if (e.getClickCount() == 1) {
                chonPhieu();
            }
        });
        // (Gop y GV 2/6) Tim dich vu theo tu khoa — khong de bac si do danh sach dai
        javafx.collections.ObservableList<DichVu> dsDV = FXCollections
                .observableArrayList(DichVu_BUS.danhSachDichVuDangPhucVu(App.session));
        javafx.collections.transformation.FilteredList<DichVu> dvLoc = new javafx.collections.transformation.FilteredList<>(dsDV, d -> true);
        cbb_dichVu.setItems(dvLoc);
        txt_timDichVu.textProperty().addListener((o, cu, moi) -> {
            String k = util.GoiTuKhoa.chuanHoa(moi);
            dvLoc.setPredicate(dv -> k.isEmpty() || util.GoiTuKhoa.chuanHoa(dv.getTenDichVu()).contains(k));
        });
        if (!dvLoc.isEmpty()) {
            cbb_dichVu.setValue(dvLoc.get(0));
        }
        if (!cbb_thuoc.getItems().isEmpty()) {
            cbb_thuoc.setValue(cbb_thuoc.getItems().get(0));
        }
        taoKhungRang();
        cbb_dichVu.valueProperty().addListener((o, cu, moi) -> {
            anKhungRang();
            resetKhungRang(); // doi dich vu thi chon lai rang — tranh nham rang cua dich vu truoc
        });
        cl_dv.setCellValueFactory(new PropertyValueFactory<>("tenDichVu"));
        cl_soRang.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getSoRang() == null ? "" : cd.getValue().getSoRang()));
        cl_gia.setCellValueFactory(cd -> new SimpleStringProperty(FormatUtil.tien(cd.getValue().getDonGia())));
        cl_tenThuoc.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getTenThuoc() == null ? "" : cd.getValue().getTenThuoc()));
        cl_soLuongThuoc.setCellValueFactory(cd -> new SimpleStringProperty(String.valueOf(cd.getValue().getSoLuong())));
        cl_ghiChuThuoc.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getGhiChuCapThuoc() == null ? "" : cd.getValue().getGhiChuCapThuoc()));
        cbb_thuoc.setItems(FXCollections.observableArrayList(KhamBenh_BUS.danhSachThuoc(App.session)));
        if (!cbb_thuoc.getItems().isEmpty()) {
            cbb_thuoc.setValue(cbb_thuoc.getItems().get(0));
        }
        taiLai();
    }

    @FXML
    private void taiLai() {
        try {
            var ds = KhamBenh_BUS.danhSachKhamCuaToi(App.session);
            table_danhSach.getItems().clear();
            table_danhSach.setItems(FXCollections.observableArrayList(ds));
            if (!ds.isEmpty()) {
                table_danhSach.getSelectionModel().selectFirst();
                chonPhieu();
            } else {
                lb_bn.setText("(hôm nay chưa có phiếu khám của bạn)");
                lb_tienSu.setText("");
                ta_chanDoan.clear();
                txt_ketQua.clear();
                txt_thuocTuMua.clear();
                chiDinhTam.clear();
                thuocTam.clear();
                napBangTam();
                btn_luu.setDisable(true);
                btn_hoanTat.setDisable(true);
            }
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }

    private void chonPhieu() {
        PhieuKham pk = table_danhSach.getSelectionModel().getSelectedItem();
        if (pk == null) {
            return;
        }
        lb_bn.setText(pk.getMaPhieuKham() + " - " + pk.getTenBenhNhan() + " [" + pk.getTrangThai() + "]");
        HoSoBenhAn hs = HoSoBenhAn_BUS.layTheoBenhNhan(App.session, pk.getMaBenhNhan());
        lb_tienSu.setText(hs == null ? "Chưa có hồ sơ bệnh án (sẽ tự tạo khi lưu lần đầu)."
                : "Hồ sơ " + hs.getMaHoSo() + " — tiền sử: "
                + (hs.getTienSuBenhLy() == null ? "" : hs.getTienSuBenhLy()));
        ta_chanDoan.setText(pk.getChanDoan() == null ? "" : pk.getChanDoan());
        txt_ketQua.setText(pk.getKetQuaDieuTri() == null ? "" : pk.getKetQuaDieuTri());
        txt_thuocTuMua.setText(pk.getGhiChuThuocTuMua() == null ? "" : pk.getGhiChuThuocTuMua());
        ta_phacDo.setText(pk.getPhacDieuTri() == null ? "" : pk.getPhacDieuTri());
        dp_ngayHen.setValue(pk.getNgayHenTiep());
        int lanThu = KhamBenh_BUS.lanDieuTriThuHienTai(App.session, pk.getMaBenhNhan());
        lb_tienSu.setText(lb_tienSu.getText() + "\nLần điều trị thứ " + lanThu
                + " (đếm trên toàn bộ lượt khám của bệnh nhân).");
        // (Gop y GV P1-7) Bac si phai thay lan truoc lam gi de tiep tuc qua trinh dieu tri
        var lichSu = BenhNhan_BUS.layLichSuPhieuKham(App.session, pk.getMaBenhNhan());
        var lanTruoc = lichSu.stream()
                .filter(p -> !p.getMaPhieuKham().equals(pk.getMaPhieuKham()) && p.getPhacDieuTri() != null)
                .findFirst().orElse(null);
        if (lanTruoc != null) {
            lb_tienSu.setText(lb_tienSu.getText() + "\nLần trước (" + FormatUtil.ngay(lanTruoc.getNgayKham())
                    + ", " + lanTruoc.getMaPhieuKham() + "): " + lanTruoc.getPhacDieuTri());
        }
        resetKhungRang();
        chiDinhTam = new ArrayList<>(KhamBenh_BUS.layChiDinhCuaToi(App.session, pk.getMaPhieuKham()));
        thuocTam = new ArrayList<>(KhamBenh_BUS.layThuocCapCuaToi(App.session, pk.getMaPhieuKham()));
        napBangTam();
        boolean choSua = pk.getTrangThai() == entity.TrangThaiPhieuKham.DANG_KHAM;
        btn_luu.setDisable(!choSua);
        btn_hoanTat.setDisable(!choSua);
    }

    private void napBangTam() {
        table_chiDinh.getItems().clear();
        table_chiDinh.setItems(FXCollections.observableArrayList(chiDinhTam));
        table_thuoc.getItems().clear();
        table_thuoc.setItems(FXCollections.observableArrayList(thuocTam));
    }

    @FXML
    private void themChiDinh() {
        DichVu dv = cbb_dichVu.getValue();
        PhieuKham pk = table_danhSach.getSelectionModel().getSelectedItem();
        if (pk == null) {
            AlertUtil.baoLoi(MaThongBao.VL05);
            return;
        }
        if (dv == null) {
            AlertUtil.baoLoi(MaThongBao.VL11);
            return;
        }
        boolean trung = chiDinhTam.stream().anyMatch(c -> c.getMaDichVu().equals(dv.getMaDichVu()));
        if (trung) {
            AlertUtil.baoLoi("Dịch vụ đã có trong danh sách chỉ định.");
            return;
        }
        // (Gop y GV 2/7) Dich vu dieu tri rang cu the phai ghi ro so rang
        boolean dieuTriRang = util.GoiTuKhoa.laDieuTriRang(dv.getTenDichVu());
        if (dieuTriRang && soRangChon.isEmpty()) {
            AlertUtil.baoLoi(MaThongBao.VL19);
            return;
        }
        ChiDinhDichVu cd = new ChiDinhDichVu(pk.getMaPhieuKham(), dv.getMaDichVu());
        cd.setDichVu(dv);
        if (dieuTriRang) {
            cd.setSoRang(String.join(",", soRangChon));
        }
        cd.setDonGia(HoaDon_BUS.MA_DICH_VU_PHI_KHAM.equals(dv.getMaDichVu())
                && pk.getMaHoaDonPhiKham() != null
                ? java.math.BigDecimal.ZERO : dv.getDonGiaNiemYet());
        chiDinhTam.add(cd);
        napBangTam();
        if (dieuTriRang) {
            resetKhungRang();
        }
    }

    @FXML
    private void xoaChiDinh() {
        ChiDinhDichVu cd = table_chiDinh.getSelectionModel().getSelectedItem();
        if (cd != null) {
            chiDinhTam.remove(cd);
            napBangTam();
        }
    }


    @FXML
    private void themThuoc() {
        Thuoc th = cbb_thuoc.getValue();
        PhieuKham pk = table_danhSach.getSelectionModel().getSelectedItem();
        if (pk == null) {
            AlertUtil.baoLoi(MaThongBao.VL05);
            return;
        }
        if (th == null) {
            AlertUtil.baoLoi("Vui lòng chọn thuốc.");
            return;
        }
        int sl = ValidateUtil.soLuong(txt_soLuong.getText());
        ChiTietCapThuoc ct = new ChiTietCapThuoc(pk.getMaPhieuKham(), th.getMaThuoc(), sl,
                txt_ghiChuCap.getText() == null ? null : txt_ghiChuCap.getText().trim());
        ChiTietCapThuoc daCo = thuocTam.stream().filter(t -> t.getMaThuoc().equals(th.getMaThuoc()))
                .findFirst().orElse(null);
        if (daCo != null) {
            daCo.setSoLuong(sl);
            daCo.setGhiChuCapThuoc(ct.getGhiChuCapThuoc());
        } else {
            ct.setThuoc(th);
            thuocTam.add(ct);
        }
        napBangTam();
    }

    @FXML
    private void xoaThuoc() {
        ChiTietCapThuoc ct = table_thuoc.getSelectionModel().getSelectedItem();
        if (ct != null) {
            thuocTam.remove(ct);
            napBangTam();
        }
    }

    @FXML
    private void luu() {
        PhieuKham pk = table_danhSach.getSelectionModel().getSelectedItem();
        if (pk == null) {
            AlertUtil.baoLoi(MaThongBao.VL05);
            return;
        }
        try {
            KhamBenh_BUS.luuKham(App.session, pk.getMaPhieuKham(), ta_chanDoan.getText(),
                    txt_ketQua.getText(), txt_thuocTuMua.getText(), chiDinhTam, thuocTam,
                    ta_phacDo.getText(), dp_ngayHen.getValue());
            AlertUtil.baoThongTin("Đã lưu thông tin khám (phiếu vẫn đang khám).");
            taiLai();
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }

    @FXML
    private void hoanTat() {
        PhieuKham pk = table_danhSach.getSelectionModel().getSelectedItem();
        if (pk == null) {
            AlertUtil.baoLoi(MaThongBao.VL05);
            return;
        }
        try {
            if (!AlertUtil.xacNhan("Hoàn tất lượt khám " + pk.getMaPhieuKham() + "?")) {
                return;
            }
            KhamBenh_BUS.hoanTat(App.session, pk.getMaPhieuKham());
            AlertUtil.baoThongTin("Lượt khám đã HOAN_TAT.");
            taiLai();
        } catch (RuntimeException ex) {
            AlertUtil.baoLoi(ex.getMessage());
        }
    }
}
