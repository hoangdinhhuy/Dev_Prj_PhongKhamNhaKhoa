package main;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import bus.BaoCao_BUS;
import bus.BenhNhan_BUS;
import bus.CaThuNgan_BUS;
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
import entity.ChiTietHoaDon;
import entity.ChucVu;
import entity.HoaDon;
import entity.NhanVien;
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
            return "(khong co loi — mong doi " + maLoi + ")";
        } catch (RuntimeException ex) {
            return ex.getMessage() == null ? "(null)" : ex.getMessage();
        }
    }

    public static void main(String[] args) {
        System.out.println("===== KIEM THU BACKEND — N15_PTUD_DB =====");
        // ---- UC001 Dang nhap ----
        SessionContext[] ctx = new SessionContext[4];
        try {
            ctx[0] = TaiKhoan_BUS.dangNhap("NV0001", "123456");
            ctx[1] = TaiKhoan_BUS.dangNhap("NV0002", "123456");
            ctx[2] = TaiKhoan_BUS.dangNhap("NV0005", "123456");
            ctx[3] = TaiKhoan_BUS.dangNhap("NV0007", "123456");
            check("T01 Dang nhap dung (NV0001 -> LE_TAN)", ctx[0] != null && ctx[0].getChucVu() == ChucVu.LE_TAN, "null/sai vai tro");
            check("T01b Login NV0002/NV0005/NV0007", ctx[1] != null && ctx[2] != null && ctx[3] != null,
                "ctx[1]=" + (ctx[1] == null ? "NULL" : "OK") + " ctx[2]=" + (ctx[2] == null ? "NULL" : "OK") + " ctx[3]=" + (ctx[3] == null ? "NULL" : "OK"));
        } catch (RuntimeException ex) {
            check("T01 Dang nhap dung", false, ex.getMessage());
        }
        check("T02 Sai mat khau -> TB01", expectMessage(() -> TaiKhoan_BUS.dangNhap("NV0001", "sai"), "TB01").equals("Vui lòng kiểm tra lại tên đăng nhập hoặc mật khẩu."), "");
        check("T03 Tai khoan khoa -> TB02", expectMessage(() -> TaiKhoan_BUS.dangNhap("NV0004", "123456"), "TB02").equals("Tài khoản của bạn đã bị khóa."), "");
        check("T04 Letan goi themNhanVien -> TB08", expectMessage(() -> NhanVien_BUS.themNhanVien(ctx[0], "Test", "0900000001", LocalDate.of(1995, 1, 1), Boolean.TRUE, null, ChucVu.PHU_TA), "TB08").equals("Bạn không có quyền thực hiện chức năng này."), "");
        check("QT07 Le tan khong duoc xem hang hoa", expectMessage(() -> HangHoa_BUS.danhSach(ctx[0]), "TB08").equals("Bạn không có quyền thực hiện chức năng này."), "");

        // ---- UC002 Ho so benh nhan ----
        String maBNTheoNgay = "BN" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String sdtMoi = "09" + String.format("%08d", System.nanoTime() % 100_000_000L);
        final BenhNhan[] bnMoi = new BenhNhan[1];
        try {
            bnMoi[0] = BenhNhan_BUS.taoHoSo(ctx[0], "NGUYEN VAN TEST", LocalDate.of(1994, 5, 5), sdtMoi, null, "Q.Thu Duc", null);
            check("T05 Tao ho so hop le + ma BN theo yyMMdd", bnMoi[0].getMaBenhNhan().startsWith(maBNTheoNgay)
                    && BenhNhan_BUS.layBenhNhanTheoMa(bnMoi[0].getMaBenhNhan()) != null, bnMoi[0].getMaBenhNhan());
        } catch (RuntimeException ex) {
            check("T05 Tao ho so hop le", false, ex.getMessage());
        }
        check("T06 SDT 9 so -> VL02", expectMessage(() -> BenhNhan_BUS.taoHoSo(ctx[0], "Test", LocalDate.of(1994, 5, 5), "090123456", null, null, null), "VL02").equals("Số điện thoại phải gồm 10 chữ số và bắt đầu bằng 0."), "");
        // T07: tu tao BN co CCCD moi, roi thu tao trung CCCD -> khong phu thuoc seed
        String cccdMoi = "079" + String.format("%09d", System.nanoTime() % 1_000_000_000L);
        String sdtMoi2 = "09" + String.format("%08d", System.nanoTime() % 100_000_000L);
        BenhNhan bnCccd = BenhNhan_BUS.taoHoSo(ctx[0], "Test CCCD", LocalDate.of(1994, 5, 5), sdtMoi2, cccdMoi, null, null);
        check("T07 CCCD trung -> TB13", expectMessage(() -> BenhNhan_BUS.taoHoSo(ctx[0], "Test", LocalDate.of(1994, 5, 5),
                "09" + String.format("%08d", System.nanoTime() % 100_000_000L), cccdMoi, null, null),
                "TB13").equals("CCCD đã tồn tại trong hồ sơ bệnh nhân khác."), "");

        // ---- UC005 Thu phi ----
        final HoaDon[] hd = new HoaDon[1];
        try {
            hd[0] = HoaDon_BUS.thuPhiKham(ctx[0], bnMoi[0].getMaBenhNhan(), "TIEN_MAT", "100000");
            String maHDTheoNgay = "HD" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
            check("T08 Thu phi thanh cong + ma HD theo yyMMdd", hd[0].getMaHoaDon().startsWith(maHDTheoNgay) && hd[0].isDaThanhToan()
                    && hd[0].getTongTien() != null && hd[0].getTongTien().compareTo(new BigDecimal("50000")) == 0, String.valueOf(hd[0].getTongTien()));
        } catch (RuntimeException ex) {
            check("T08 Thu phi thanh cong", false, ex.getMessage());
        }
        check("T09 Thu phi trung -> TB04", expectMessage(() -> HoaDon_BUS.thuPhiKham(ctx[0], bnMoi[0].getMaBenhNhan(), "TIEN_MAT", "100000"), "TB04").equals("Bệnh nhân đã hoàn tất phí khám."), "");

        // ---- UC003 Lap phieu ----
        String phongTest = "P001";
        final PhieuKham[] pk = new PhieuKham[1];
        try {
            pk[0] = PhieuKham_BUS.lapPhieuKham(ctx[0], bnMoi[0], ctx[1].getMaNhanVien(), phongTest, "Test luong kham", hd[0]);
            check("T10 Lap phieu -> CHO_KHAM, STT cap tu BoDem", pk[0] != null
                    && pk[0].getTrangThai() == TrangThaiPhieuKham.CHO_KHAM && pk[0].getSoThuTu() > 0,
                    pk[0] == null ? "null" : "STT=" + pk[0].getSoThuTu());
        } catch (RuntimeException ex) {
            check("T10 Lap phieu", false, ex.getMessage());
        }
        // T11: chu dong tat P003 truoc khi test, xong bat lai
        PhongKham_DAO.capNhatNhanBenh("P003", false);
        check("T11 Phong tat nhan benh (P003) -> TB03", expectMessage(() -> PhieuKham_BUS.lapPhieuKham(ctx[0], bnMoi[0], ctx[1].getMaNhanVien(), "P003", "Test", hd[0]), "TB03").equals("Bác sĩ/Phòng khám hiện không trong ca. Vui lòng chọn khác."), "");
        PhongKham_DAO.capNhatNhanBenh("P003", true);
        check("T12 Gan hoa don da dung -> TB04", expectMessage(() -> PhieuKham_BUS.lapPhieuKham(ctx[0], bnMoi[0], ctx[1].getMaNhanVien(), phongTest, "Test", hd[0]), "TB04").equals("Bệnh nhân đã hoàn tất phí khám."), "");

        // ---- UC009 Hang cho ----
        try {
            HangCho_BUS.goiSo(ctx[2], pk[0].getMaPhieuKham());
            check("T13 Hang cho co phieu vua lap", true, "");
        } catch (RuntimeException ex) {
            check("T13 Hang cho co phieu vua lap", false, ex.getMessage());
        }
        try {
            HangCho_BUS.goiSo(ctx[2], pk[0].getMaPhieuKham());
            check("T14 Goi so giu CHO_KHAM", PhieuKham_DAO.layPhieuKhamTheoMa(pk[0].getMaPhieuKham())
                    .getTrangThai() == TrangThaiPhieuKham.CHO_KHAM, "");
        } catch (RuntimeException ex) {
            check("T14 Goi so giu CHO_KHAM", false, ex.getMessage());
        }
        try {
            HangCho_BUS.tiepNhanVaoKham(ctx[2], pk[0].getMaPhieuKham());
            check("T15 Vao kham -> DANG_KHAM", PhieuKham_DAO.layPhieuKhamTheoMa(pk[0].getMaPhieuKham())
                    .getTrangThai() == TrangThaiPhieuKham.DANG_KHAM, "");
        } catch (RuntimeException ex) {
            check("T15 Vao kham", false, ex.getMessage());
        }
        check("T16 Vao kham lan 2 -> TB10", expectMessage(() -> HangCho_BUS.tiepNhanVaoKham(ctx[2], pk[0].getMaPhieuKham()), "TB10").equals("Trạng thái phiếu khám đã thay đổi. Vui lòng tải lại danh sách."), "");

        // ---- UC004 Kham benh ----
        List<PhieuKham> cuaToi = KhamBenh_BUS.danhSachKhamCuaToi(ctx[1]);
        check("T17 Danh sach kham cua bac si chua phieu vua vao kham", cuaToi.stream().anyMatch(p -> p.getMaPhieuKham().equals(pk[0].getMaPhieuKham())), "size=" + cuaToi.size());
        check("T18 Luu kham thieu chan doan -> VL09", expectMessage(() -> KhamBenh_BUS.luuKham(ctx[1], pk[0].getMaPhieuKham(), "  ", null, null, new ArrayList<>(), new ArrayList<>(), null, null), "VL09").equals("Vui lòng nhập chẩn đoán trước khi lưu."), "");
        check("T19 Chi dinh rong -> TB06", expectMessage(() -> KhamBenh_BUS.luuKham(ctx[1], pk[0].getMaPhieuKham(), "Sau rang", null, null, new ArrayList<>(), new ArrayList<>(), null, null), "TB06").equals("Vui lòng chọn ít nhất một dịch vụ hợp lệ trong danh mục."), "");
        List<ChiDinhDichVu> chiDinh = new ArrayList<>();
        chiDinh.add(new ChiDinhDichVu(pk[0].getMaPhieuKham(), "DV0010")); // NGUNG_PHUC_VU
        check("T20 Chi dinh dich vu ngung -> TB15", expectMessage(() -> KhamBenh_BUS.luuKham(ctx[1], pk[0].getMaPhieuKham(), "Sau rang", null, null, chiDinh, new ArrayList<>(), null, null), "TB15").equals("Dịch vụ đã ngừng phục vụ, không thể chỉ định."), "");
        try {
            List<ChiDinhDichVu> cd = new ArrayList<>();
            cd.add(new ChiDinhDichVu(pk[0].getMaPhieuKham(), "DV0002"));
            cd.add(new ChiDinhDichVu(pk[0].getMaPhieuKham(), "DV0003"));
            cd.get(1).setSoRang("36,37"); // (Gop y GV 2/7) han rang composite ghi ro so rang FDI
            List<ChiTietCapThuoc> thuoc = new ArrayList<>();
            thuoc.add(new ChiTietCapThuoc(pk[0].getMaPhieuKham(), "TH0001", 12, "Uong 2 vien/ngay"));
            java.time.LocalDate hen = LocalDate.now().plusDays(7);
            KhamBenh_BUS.luuKham(ctx[1], pk[0].getMaPhieuKham(), "Sau rang men rang so 36", "Da trat tam",
                    "Mua thuoc giam đau o nha", cd, thuoc, "1. Tay trang; 2. Han composite rang 36,37", hen);
            PhieuKham daLuu = PhieuKham_DAO.layPhieuKhamTheoMa(pk[0].getMaPhieuKham());
            check("T21 Luu kham hop le (transaction 4 bang)", ChiDinhDichVu_DAO.layTheoPhieu(pk[0].getMaPhieuKham()).size() == 2
                    && ChiTietCapThuoc_DAO.layTheoPhieu(pk[0].getMaPhieuKham()).size() == 1
                    && daLuu.getMaHoSo() != null, "chi dinh/thuoc/ho so");
            check("T35 Phac do + ngay hen luu dung", "1. Tay trang; 2. Han composite rang 36,37".equals(daLuu.getPhacDieuTri())
                    && hen.equals(daLuu.getNgayHenTiep()), "phacDo=" + daLuu.getPhacDieuTri() + ", hen=" + daLuu.getNgayHenTiep());
            check("T36 So rang FDI luu theo chi dinh", "36,37".equals(
                    ChiDinhDichVu_DAO.layTheoPhieu(pk[0].getMaPhieuKham()).stream()
                            .filter(c -> c.getMaDichVu().equals("DV0003")).findFirst().orElseThrow().getSoRang()), "");
        } catch (RuntimeException ex) {
            check("T21 Luu kham hop le", false, ex.getMessage());
            check("T35 Phac do + ngay hen luu dung", false, ex.getMessage());
            check("T36 So rang FDI luu theo chi dinh", false, ex.getMessage());
        }
        try {
            KhamBenh_BUS.hoanTat(ctx[1], pk[0].getMaPhieuKham());
            check("T22 Hoan tat -> HOAN_TAT", PhieuKham_DAO.layPhieuKhamTheoMa(pk[0].getMaPhieuKham())
                    .getTrangThai() == TrangThaiPhieuKham.HOAN_TAT, "");
        } catch (RuntimeException ex) {
            check("T22 Hoan tat", false, ex.getMessage());
        }
        check("T23 Hoan tat lan 2 -> TB10", expectMessage(() -> KhamBenh_BUS.hoanTat(ctx[1], pk[0].getMaPhieuKham()), "TB10").equals("Trạng thái phiếu khám đã thay đổi. Vui lòng tải lại danh sách."), "");

        // ---- UC007 Dich vu ----
        check("T24 Them dich vu trung ten -> TB12", expectMessage(() -> DichVu_BUS.themDichVu(ctx[3], "Cạo vôi răng", "100000"), "TB12").equals("Tên dịch vụ đã tồn tại."), "");
        check("T25 Gia 0 -> VL06", expectMessage(() -> DichVu_BUS.themDichVu(ctx[3], "Dich vu test " + System.nanoTime(), "0"), "VL06").equals("Đơn giá phải là số dương lớn hơn 0."), "");

        // ---- UC008 Bao cao ----
        try {
            var bc = BaoCao_BUS.thongKeLuotKham(ctx[3], LocalDate.now().minusDays(7), LocalDate.now());
            int tong = bc.stream().mapToInt(r -> (Integer) r[1]).sum();
            check("T26 Bao cao 7 ngay co du lieu (>=1 luot hom nay)", tong >= 1, "tong=" + tong);
        } catch (RuntimeException ex) {
            check("T26 Bao cao", false, ex.getMessage());
        }
        check("T27 Tu ngay > Den ngay -> VL10", expectMessage(() -> BaoCao_BUS.thongKeLuotKham(ctx[3], LocalDate.now(), LocalDate.now().minusDays(7)), "VL10").equals("Từ ngày không được sau Đến ngày."), "");

        // ---- UC010 Nhan vien ----
        check("T28 Xoa NV da co phieu -> TB11", expectMessage(() -> NhanVien_BUS.xoaNhanVien(ctx[3], "NV0002"), "TB11").equals("Không thể xóa vì dữ liệu đang được sử dụng."), "");

        // ---- Thanh toan dich vu cuoi luot (LT-06) + Lich su ----
        check("T29 Phieu HOAN_TAT chua thanh toan co trong danh sach", PhieuKham_DAO
                .layPhieuHoanTatChuaThanhToan().stream()
                .anyMatch(p -> p.getMaPhieuKham().equals(pk[0].getMaPhieuKham())), "");
        try {
            HoaDon hdDV = HoaDon_BUS.thanhToanDichVu(ctx[0], pk[0].getMaPhieuKham(), "TIEN_MAT", "1000000");
            check("T30 Thanh toan dich vu (tong = 550.000 = 200k + 350k, gan phieu)",
                    hdDV.isDaThanhToan() && hdDV.getTongTien().compareTo(new BigDecimal("550000")) == 0
                            && pk[0].getMaPhieuKham().equals(hdDV.getMaPhieuKham())
                            && dao.HoaDon_DAO.layHoaDonTheoPhieu(pk[0].getMaPhieuKham()) != null,
                    String.valueOf(hdDV.getTongTien()));
        } catch (RuntimeException ex) {
            check("T30 Thanh toan dich vu", false, ex.getMessage());
        }
        check("T31 Thanh toan lan 2 -> TB17", expectMessage(() -> HoaDon_BUS.thanhToanDichVu(ctx[0],
                pk[0].getMaPhieuKham(), "TIEN_MAT", "1000000"), "TB17").equals("Lượt khám này đã được thanh toán."), "");
        check("T33 Lich su kham toan he thong co du lieu", !PhieuKham_DAO
                .layToanBoLichSu().isEmpty(), "");

        // ==== (Góp ý GV 05/11) Kiểm thử sau khâu hoá chuyên sâu: từ khoá, entity dẫn xuất, quan hệ đối tượng ====
        check("T37 chuanHoa bo dau tieng Viet", util.GoiTuKhoa.chuanHoa("Trám Răng").equals("tram rang")
                && util.GoiTuKhoa.chuanHoa("NGUYỄN VĂN").equals("nguyen van")
                && util.GoiTuKhoa.chuanHoa("Đội Mão").equals("doi mao"), "");
        check("T38 Nhan dien dich vu dieu tri rang (khong dau)",
                util.GoiTuKhoa.laDieuTriRang("Hàn răng composite")
                        && util.GoiTuKhoa.laDieuTriRang("Nhổ răng thường")
                        && util.GoiTuKhoa.laDieuTriRang("Lấy tủy răng")
                        && util.GoiTuKhoa.laDieuTriRang("Trát răng sâu")
                        && util.GoiTuKhoa.laDieuTriRang("Đội mão răng sứ")
                        && !util.GoiTuKhoa.laDieuTriRang("Cạo vôi răng")
                        && !util.GoiTuKhoa.laDieuTriRang("Tẩy trắng răng")
                        && !util.GoiTuKhoa.laDieuTriRang("Chụp phim Panorama")
                        && !util.GoiTuKhoa.laDieuTriRang("Khám lưu động"), "");
        boolean khongSetTuyY;
        try {
            HoaDon.class.getMethod("setTongTien", BigDecimal.class);
            khongSetTuyY = false;
        } catch (NoSuchMethodException e) {
            khongSetTuyY = true;
        }
        boolean khongSetThanhTien;
        try {
            ChiTietHoaDon.class.getMethod("setThanhTien", BigDecimal.class);
            khongSetThanhTien = false;
        } catch (NoSuchMethodException e) {
            khongSetThanhTien = true;
        }
        check("T39 Reflection: khong ton tai setTongTien/setThanhTien (Gop y GV 2/2)",
                khongSetTuyY && khongSetThanhTien, "");
        try {
            HoaDon hdT = new HoaDon("HD_TEST_T40");
            hdT.themChiTiet(new ChiTietHoaDon("HD_TEST_T40", "DV0003", 1, new BigDecimal("350000")));
            hdT.themChiTiet(new ChiTietHoaDon("HD_TEST_T40", "DV0002", 2, new BigDecimal("200000")));
            hdT.setTienKhachDua(new BigDecimal("1000000"));
            check("T40 Entity tu tinh tongTien + tienThua",
                    hdT.getTongTien().compareTo(new BigDecimal("750000")) == 0
                            && hdT.getTienThua().compareTo(new BigDecimal("250000")) == 0,
                    "tong=" + hdT.getTongTien() + ", thua=" + hdT.getTienThua());
        } catch (RuntimeException ex) {
            check("T40 Entity tu tinh tongTien + tienThua", false, ex.getMessage());
        }
        try {
            var tk = PhieuKham_BUS.thongKeTheoPhong(ctx[3], LocalDate.now().minusDays(7), LocalDate.now());
            boolean hopLe = !tk.isEmpty() && tk.stream().allMatch(r ->
                    ((Integer) r[1]) + ((Integer) r[2]) + ((Integer) r[3]) == ((Integer) r[4]));
            check("T41 Thong ke theo phong: tong = cho + dang + hoan tat", hopLe, "rows=" + tk.size());
            check("T42 Le tan khong duoc xem thong ke hang cho -> TB08",
                    expectMessage(() -> PhieuKham_BUS.thongKeTheoPhong(ctx[0],
                            LocalDate.now().minusDays(1), LocalDate.now()), "TB08")
                            .equals("Bạn không có quyền thực hiện chức năng này."), "");
        } catch (RuntimeException ex) {
            check("T41 Thong ke theo phong", false, ex.getMessage());
            check("T42 Quyen thong ke hang cho", false, ex.getMessage());
        }
        try {
            int demBN = PhieuKham_DAO.demTheoBenhNhan(bnMoi[0].getMaBenhNhan());
            var lichSu = PhieuKham_BUS.layLichSuKhoangLanThu(ctx[3],
                    LocalDate.now().minusDays(30), LocalDate.now(), 500);
            boolean lanThuDung = !lichSu.isEmpty() && lichSu.stream().allMatch(p -> p.getLanThu() >= 1)
                    && lichSu.stream().filter(p -> p.getMaBenhNhan().equals(bnMoi[0].getMaBenhNhan()))
                            .mapToInt(PhieuKham::getLanThu).max().orElse(0) == demBN;
            check("T43 Lan thu (ROW_NUMBER theo BN) dung so voi so phieu", lanThuDung,
                    "dem=" + demBN + ", rows=" + lichSu.size());
            check("T44 Quan he doi tuong nap that: HoaDon-NV/BN, ChiTiet-DichVu",
                    dao.HoaDon_DAO.layHoaDonTheoPhieu(pk[0].getMaPhieuKham()) != null
                            && dao.HoaDon_DAO.layHoaDonTheoPhieu(pk[0].getMaPhieuKham()).getNhanVien() != null
                            && dao.HoaDon_DAO.layHoaDonTheoPhieu(pk[0].getMaPhieuKham()).getBenhNhan() != null
                            && dao.HoaDon_DAO.layHoaDonTheoPhieu(pk[0].getMaPhieuKham()).getChiTiets().stream()
                                    .allMatch(ct -> ct.getDichVu() != null && ct.getDichVu().getTenDichVu() != null),
                    "nhanVien/benhNhan/dichVu");
        } catch (RuntimeException ex) {
            check("T43 Lan thu theo benh nhan", false, ex.getMessage());
            check("T44 Quan he doi tuong", false, ex.getMessage());
        }

        // ==== (Tối ưu UX lễ tân) Gợi ý type-ahead + bác sĩ ít chờ nhất ====
        String tuGoiY = bnCccd.getHoTen().substring(0, 4); // "Test" — tu khoa tap vua tao
        var dsGoiY = BenhNhan_BUS.goiYBenhNhan(ctx[0], tuGoiY, 8);
        check("T45 Goi y type-ahead: tim dung BN vua tao",
                dsGoiY.stream().anyMatch(b -> b.getMaBenhNhan().equals(bnCccd.getMaBenhNhan())), "");
        check("T46 Goi y theo SDT bat dau bang SDT vua tao",
                !BenhNhan_BUS.goiYBenhNhan(ctx[0], sdtMoi2, 8).isEmpty()
                        && BenhNhan_BUS.goiYBenhNhan(ctx[0], "0999", 8).isEmpty(), "");
        int itNhat = Integer.MAX_VALUE;
        var dsBs = NhanVien_BUS.danhSachBacSiDangLamViec(ctx[3]);
        for (NhanVien nv : dsBs) {
            itNhat = Math.min(itNhat, PhieuKham_BUS.soPhieuDangChoCuaBacSi(ctx[3], nv.getMaNhanVien()));
        }
        check("T47 Goi y bac si it cho nhat: dem duoc so phieu (>=0)", itNhat >= 0, "itNhat=" + itNhat);

        // ==== (Chốt ca thu ngân — mẫu POS) T48: nhan ca -> chot ca ====
        try {
            var caMo = CaThuNgan_BUS.layCaDangMo(ctx[0]);
            if (caMo == null) {
                caMo = CaThuNgan_BUS.moCa(ctx[0]);
            }
            check("T48a Nhan ca: ca DANG_MO, gan dung thu ngan",
                    caMo.isDangMo() && caMo.getMaNhanVien() != null, "maCa=" + caMo.getMaCa());
            var daChot = CaThuNgan_BUS.chotCa(ctx[3], caMo);
            check("T48b Chot ca: snapshot doanh thu + trang thai DA_CHOT",
                    daChot.getThoiGianChot() != null && daChot.getDoanhThuDichVu() != null
                            && daChot.getPhiThuHo() != null
                            && entity.CaThuNgan.DA_CHOT.equals(daChot.getTrangThai()),
                    "DV=" + daChot.getDoanhThuDichVu() + ", ho=" + daChot.getPhiThuHo());
            check("T48c Sau chot: khong con ca DANG_MO (nhan vien sau phai Nhan ca)",
                    CaThuNgan_BUS.layCaDangMo(ctx[0]) == null, "");
        } catch (RuntimeException ex) {
            check("T48 Chot ca thu ngan", false, ex.getMessage());
        }

        System.out.println("===== KET QUA: " + pass + " PASS, " + fail + " FAIL =====");
        if (fail > 0) {
            System.exit(1);
        }
    }
}
