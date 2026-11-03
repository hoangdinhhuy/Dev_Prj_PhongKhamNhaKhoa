package main;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import bus.BaoCao_BUS;
import bus.BenhNhan_BUS;
import bus.DichVu_BUS;
import bus.HangCho_BUS;
import bus.HangHoa_BUS;
import bus.HoaDon_BUS;
import bus.KhamBenh_BUS;
import bus.NhanVien_BUS;
import bus.PhieuKham_BUS;
import bus.TaiKhoan_BUS;
import dao.ChiDinhDichVu_DAO;
import dao.ChiTietCapThuoc_DAO;
import dao.PhieuKham_DAO;
import dao.PhongKham_DAO;
import entity.BenhNhan;
import entity.ChiDinhDichVu;
import entity.ChiTietCapThuoc;
import entity.ChucVu;
import entity.HoaDon;
import entity.PhieuKham;
import entity.SessionContext;
import entity.TrangThaiPhieuKham;
import util.MaThongBao;

/**
 * Kiem thu backend (BUS/DAO) voi DB that — chay headless, khong mo GUI.
 * Chay: java -cp "target/classes;..." main.TestRunner
 * Ket qua la du lieu mau de xem trong Hang cho / Bao cao (khong xoa sau khi test).
 */
public class TestRunner {

    private static int pass = 0, fail = 0;

    private static void check(String ten, boolean dk, String chiTiet) {
        if (dk) {
            pass++;
            System.out.println("[PASS] " + ten);
        } else {
            fail++;
            System.out.println("[FAIL] " + ten + " -> " + chiTiet);
        }
    }

    private static String expectMessage(Runnable action, String maLoi) {
        try {
            action.run();
            return "KHONG nem loi (du kien chua " + maLoi + ")";
        } catch (RuntimeException ex) {
            return ex.getMessage();
        }
    }

    public static void main(String[] args) {
        System.out.println("===== KIEM THU BACKEND — N15_PTUD_DB =====");

        // ---- UC001 Dang nhap ----
        SessionContext letan = null, bacsi = null, phuta = null, quanly = null;
        try {
            letan = TaiKhoan_BUS.dangNhap("letan", "123456");
            check("T01 Dang nhap dung (letan -> LE_TAN)", letan != null && letan.getChucVu() == ChucVu.LE_TAN, "null/sai vai tro");
        } catch (RuntimeException ex) {
            check("T01 Dang nhap dung", false, ex.getMessage());
        }
        check("T02 Sai mat khau -> TB01", expectMessage(() -> TaiKhoan_BUS.dangNhap("letan", "sai"), "TB01").equals("Vui lòng kiểm tra lại tên đăng nhập hoặc mật khẩu."), "");
        check("T03 Tai khoan khoa -> TB02", expectMessage(() -> TaiKhoan_BUS.dangNhap("lockacc", "123456"), "TB02").equals("Tài khoản của bạn đã bị khóa."), "");
        try {
            bacsi = TaiKhoan_BUS.dangNhap("bacsi1", "123456");
            phuta = TaiKhoan_BUS.dangNhap("phuta", "123456");
            quanly = TaiKhoan_BUS.dangNhap("quanly", "123456");
        } catch (RuntimeException ignored) { }
        final SessionContext fLetan = letan;
        final SessionContext fBacsi = bacsi, fPhuta = phuta, fQuanly = quanly;
        String phongTest = PhongKham_DAO.layPhongDangNhanBenh().get(0).getMaPhong();

        // ---- UC002 Ho so benh nhan ----
        check("T04 Letan goi themNhanVien -> TB08", expectMessage(() -> NhanVien_BUS.themNhanVien(fLetan, "Test", "0900000001", LocalDate.of(1995, 1, 1), Boolean.TRUE, null, ChucVu.PHU_TA), "TB08").equals("Bạn không có quyền thực hiện chức năng này."), "");
        check("QT07 Le tan khong duoc xem hang hoa -> TB08",
                expectMessage(() -> HangHoa_BUS.danhSach(fLetan), "TB08").equals(MaThongBao.TB08), "");
        check("Bac si khong duoc doc chi dinh de thanh toan -> TB08",
                expectMessage(() -> HoaDon_BUS.layChiDinhChoThanhToan(fBacsi, "PK_KHONG_TON_TAI"), "TB08")
                        .equals(MaThongBao.TB08), "");
        check("Le tan khong duoc doc thuoc cap cua bac si -> TB08",
                expectMessage(() -> KhamBenh_BUS.layThuocCapCuaToi(fLetan, "PK_KHONG_TON_TAI"), "TB08")
                        .equals(MaThongBao.TB08), "");
        check("Le tan khong duoc doc danh sach thuoc cho kham -> TB08",
                expectMessage(() -> KhamBenh_BUS.danhSachThuoc(fLetan), "TB08")
                        .equals(MaThongBao.TB08), "");
        String sdtMoi = "098" + String.valueOf(System.nanoTime()).substring(0, 7);
        String maBNTheoNgay = "BN" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String maHDTheoNgay = "HD" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        final BenhNhan[] bnMoi = new BenhNhan[1];
        try {
            bnMoi[0] = BenhNhan_BUS.taoHoSo(fLetan, "NGUYEN VAN TEST", LocalDate.of(1994, 5, 5), sdtMoi, null, "Q.Thu Duc", null);
            check("T05 Tao ho so hop le + ma BN theo yyMMdd", bnMoi[0].getMaBenhNhan().startsWith(maBNTheoNgay)
                    && BenhNhan_BUS.layBenhNhanTheoMa(bnMoi[0].getMaBenhNhan()) != null, bnMoi[0].getMaBenhNhan());
        } catch (RuntimeException ex) {
            check("T05 Tao ho so hop le", false, ex.getMessage());
        }
        check("T06 SDT 9 so -> VL02", expectMessage(() -> BenhNhan_BUS.taoHoSo(fLetan, "Test", LocalDate.of(1994, 5, 5), "090123456", null, null, null), "VL02").equals("Số điện thoại phải gồm 10 chữ số và bắt đầu bằng 0."), "");
        check("T07 CCCD trung -> TB13", expectMessage(() -> BenhNhan_BUS.taoHoSo(fLetan, "Test", LocalDate.of(1994, 5, 5), sdtMoi, "079199001001", null, null), "TB13").equals("CCCD đã tồn tại trong hồ sơ bệnh nhân khác."), "");

        // ---- UC005 Thu phi ----
        final HoaDon[] hd = new HoaDon[1];
        try {
            hd[0] = HoaDon_BUS.thuPhiKham(fLetan, bnMoi[0].getMaBenhNhan(), "TIEN_MAT", "100000");
            check("T08 Thu phi thanh cong + ma HD theo yyMMdd", hd[0].getMaHoaDon().startsWith(maHDTheoNgay) && hd[0].isDaThanhToan()
                    && hd[0].getTongTien() != null && hd[0].getTongTien().compareTo(new BigDecimal("50000")) == 0, String.valueOf(hd[0].getTongTien()));
        } catch (RuntimeException ex) {
            check("T08 Thu phi thanh cong", false, ex.getMessage());
        }
        check("T09 Thu phi trung -> TB04", expectMessage(() -> HoaDon_BUS.thuPhiKham(fLetan, bnMoi[0].getMaBenhNhan(), "TIEN_MAT", "100000"), "TB04").equals("Bệnh nhân đã hoàn tất phí khám."), "");

        // ---- UC003 Lap phieu ----
        final PhieuKham[] pk = new PhieuKham[1];
        try {
            pk[0] = PhieuKham_BUS.lapPhieuKham(fLetan, bnMoi[0], fBacsi.getMaNhanVien(), phongTest, "Test luong kham", hd[0]);
            check("T10 Lap phieu -> CHO_KHAM, STT cap tu BoDem", pk[0] != null
                    && pk[0].getTrangThai() == TrangThaiPhieuKham.CHO_KHAM && pk[0].getSoThuTu() > 0,
                    pk[0] == null ? "null" : "STT=" + pk[0].getSoThuTu());
        } catch (RuntimeException ex) {
            check("T10 Lap phieu", false, ex.getMessage());
        }
        check("T11 Phong tat nhan benh (P003) -> TB03", expectMessage(() -> PhieuKham_BUS.lapPhieuKham(fLetan, bnMoi[0], fBacsi.getMaNhanVien(), "P003", "Test", hd[0]), "TB03").equals("Bác sĩ/Phòng khám hiện không trong ca. Vui lòng chọn khác."), "");
        check("T12 Gan hoa don da dung -> TB04", expectMessage(() -> PhieuKham_BUS.lapPhieuKham(fLetan, bnMoi[0], fBacsi.getMaNhanVien(), phongTest, "Test", hd[0]), "TB04").equals("Bệnh nhân đã hoàn tất phí khám."), "");

        // ---- UC009 Hang cho ----
        try {
            List<PhieuKham> cho = HangCho_BUS.danhSachCho(fPhuta, LocalDate.now(), phongTest);
            check("T13 Hang cho co phieu vua lap", cho.stream().anyMatch(p -> p.getMaPhieuKham().equals(pk[0].getMaPhieuKham())), "size=" + cho.size());
        } catch (RuntimeException ex) {
            check("T13 Hang cho", false, ex.getMessage());
        }
        try {
            PhieuKham g = HangCho_BUS.goiSo(fPhuta, pk[0].getMaPhieuKham());
            check("T14 Goi so giu CHO_KHAM", g.getTrangThai() == TrangThaiPhieuKham.CHO_KHAM, g.getTrangThai().toString());
        } catch (RuntimeException ex) {
            check("T14 Goi so", false, ex.getMessage());
        }
        try {
            HangCho_BUS.tiepNhanVaoKham(fPhuta, pk[0].getMaPhieuKham());
            PhieuKham sau = PhieuKham_DAO.layPhieuKhamTheoMa(pk[0].getMaPhieuKham());
            check("T15 Vao kham -> DANG_KHAM", sau.getTrangThai() == TrangThaiPhieuKham.DANG_KHAM, sau.getTrangThai().toString());
        } catch (RuntimeException ex) {
            check("T15 Vao kham", false, ex.getMessage());
        }
        check("T16 Vao kham lan 2 -> TB10", expectMessage(() -> HangCho_BUS.tiepNhanVaoKham(fPhuta, pk[0].getMaPhieuKham()), "TB10").equals("Trạng thái phiếu khám đã thay đổi. Vui lòng tải lại danh sách."), "");

        // ---- UC004 Kham benh ----
        List<PhieuKham> cuaToi = KhamBenh_BUS.danhSachKhamCuaToi(fBacsi);
        check("T17 Danh sach kham cua bac si chua phieu vua vao kham", cuaToi.stream().anyMatch(p -> p.getMaPhieuKham().equals(pk[0].getMaPhieuKham())), "size=" + cuaToi.size());
        check("T18 Luu kham thieu chan doan -> VL09", expectMessage(() -> KhamBenh_BUS.luuKham(fBacsi, pk[0].getMaPhieuKham(), "  ", null, null, new ArrayList<>(), new ArrayList<>()), "VL09").equals("Vui lòng nhập chẩn đoán trước khi lưu."), "");
        check("T19 Chi dinh rong -> TB06", expectMessage(() -> KhamBenh_BUS.luuKham(fBacsi, pk[0].getMaPhieuKham(), "Sau rang", null, null, new ArrayList<>(), new ArrayList<>()), "TB06").equals("Vui lòng chọn ít nhất một dịch vụ hợp lệ trong danh mục."), "");
        List<ChiDinhDichVu> chiDinh = new ArrayList<>();
        chiDinh.add(new ChiDinhDichVu(pk[0].getMaPhieuKham(), "DV0010")); // NGUNG_PHUC_VU
        check("T20 Chi dinh dich vu ngung -> TB15", expectMessage(() -> KhamBenh_BUS.luuKham(fBacsi, pk[0].getMaPhieuKham(), "Sau rang", null, null, chiDinh, new ArrayList<>()), "TB15").equals("Dịch vụ đã ngừng phục vụ, không thể chỉ định."), "");
        try {
            List<ChiDinhDichVu> cd = new ArrayList<>();
            cd.add(new ChiDinhDichVu(pk[0].getMaPhieuKham(), "DV0002"));
            cd.add(new ChiDinhDichVu(pk[0].getMaPhieuKham(), "DV0006"));
            List<ChiTietCapThuoc> thuoc = new ArrayList<>();
            thuoc.add(new ChiTietCapThuoc(pk[0].getMaPhieuKham(), "TH0001", 12, "Uong 2 vien/ngay"));
            KhamBenh_BUS.luuKham(fBacsi, pk[0].getMaPhieuKham(), "Sau rang men rang so 36", "Da trat tam", "Mua thuoc giam đau o nha", cd, thuoc);
            check("T21 Luu kham hop le (transaction 4 bang)", ChiDinhDichVu_DAO.layTheoPhieu(pk[0].getMaPhieuKham()).size() == 2
                    && ChiTietCapThuoc_DAO.layTheoPhieu(pk[0].getMaPhieuKham()).size() == 1
                    && PhieuKham_DAO.layPhieuKhamTheoMa(pk[0].getMaPhieuKham()).getMaHoSo() != null, "chi dinh/thuoc/ho so");
        } catch (RuntimeException ex) {
            check("T21 Luu kham hop le", false, ex.getMessage());
        }
        try {
            KhamBenh_BUS.hoanTat(fBacsi, pk[0].getMaPhieuKham());
            check("T22 Hoan tat -> HOAN_TAT", PhieuKham_DAO.layPhieuKhamTheoMa(pk[0].getMaPhieuKham())
                    .getTrangThai() == TrangThaiPhieuKham.HOAN_TAT, "");
        } catch (RuntimeException ex) {
            check("T22 Hoan tat", false, ex.getMessage());
        }
        check("T23 Hoan tat lan 2 -> TB10", expectMessage(() -> KhamBenh_BUS.hoanTat(fBacsi, pk[0].getMaPhieuKham()), "TB10").equals("Trạng thái phiếu khám đã thay đổi. Vui lòng tải lại danh sách."), "");

        // ---- UC007 Dich vu ----
        check("T24 Them dich vu trung ten -> TB12", expectMessage(() -> DichVu_BUS.themDichVu(fQuanly, "Cạo vôi răng", "100000"), "TB12").equals("Tên dịch vụ đã tồn tại."), "");
        check("T25 Gia 0 -> VL06", expectMessage(() -> DichVu_BUS.themDichVu(fQuanly, "Dich vu test " + System.nanoTime(), "0"), "VL06").equals("Đơn giá phải là số dương lớn hơn 0."), "");

        // ---- UC008 Bao cao ----
        try {
            var bc = BaoCao_BUS.thongKeLuotKham(fQuanly, LocalDate.now().minusDays(7), LocalDate.now());
            int tong = bc.stream().mapToInt(r -> (Integer) r[1]).sum();
            check("T26 Bao cao 7 ngay >= 17 luot", tong >= 17, "tong=" + tong);
        } catch (RuntimeException ex) {
            check("T26 Bao cao", false, ex.getMessage());
        }
        check("T27 Tu ngay > Den ngay -> VL10", expectMessage(() -> BaoCao_BUS.thongKeLuotKham(fQuanly, LocalDate.now(), LocalDate.now().minusDays(7)), "VL10").equals("Từ ngày không được sau Đến ngày."), "");

        // ---- UC010 Nhan vien ----
        check("T28 Xoa NV da co phieu -> TB11", expectMessage(() -> NhanVien_BUS.xoaNhanVien(fQuanly, "NV2608010002"), "TB11").equals("Không thể xóa vì dữ liệu đang được sử dụng."), "");

        // ---- Thanh toan dich vu cuoi luot (LT-06) + Lich su ----
        check("T29 Phieu HOAN_TAT chua thanh toan co trong danh sach", PhieuKham_DAO
                .layPhieuHoanTatChuaThanhToan().stream()
                .anyMatch(p -> p.getMaPhieuKham().equals(pk[0].getMaPhieuKham())), "");
        try {
            HoaDon hdDV = HoaDon_BUS.thanhToanDichVu(fLetan, pk[0].getMaPhieuKham(), "TIEN_MAT", "1000000");
            check("T30 Thanh toan dich vu (tong = 300.000, gan phieu)",
                    hdDV.isDaThanhToan() && hdDV.getTongTien().compareTo(new BigDecimal("300000")) == 0
                            && pk[0].getMaPhieuKham().equals(hdDV.getMaPhieuKham())
                            && dao.HoaDon_DAO.layHoaDonTheoPhieu(pk[0].getMaPhieuKham()) != null,
                    String.valueOf(hdDV.getTongTien()));
        } catch (RuntimeException ex) {
            check("T30 Thanh toan dich vu", false, ex.getMessage());
        }
        check("T31 Thanh toan lan 2 -> TB17", expectMessage(() -> HoaDon_BUS.thanhToanDichVu(fLetan,
                pk[0].getMaPhieuKham(), "TIEN_MAT", "1000000"), "TB17").equals("Lượt khám này đã được thanh toán."), "");
        check("T33 Lich su kham toan he thong (>=19 luot)", PhieuKham_DAO
                .layToanBoLichSu().size() >= 19, "");

        System.out.println("===== KET QUA: " + pass + " PASS, " + fail + " FAIL =====");
        if (fail > 0) {
            System.exit(1);
        }
    }
}
