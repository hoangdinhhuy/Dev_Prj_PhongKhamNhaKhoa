package bus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import dao.BaoCao_DAO;
import dao.ChiDinhDichVu_DAO;
import dao.PhieuKham_DAO;
import entity.PhieuKham;
import entity.ChucVu;
import entity.SessionContext;
import entity.TrangThaiPhieuKham;
import util.AIHoTro;
import util.MaThongBao;
import util.QuyenUtil;

/** AI ho tro van hanh: du doan thoi gian cho (phu ta) + du bao vat tu (quan ly). */
public class AIHoTro_BUS {

    /** (4) Du doan phut cho doi cua mot phieu trong hang cho phong (CHO_KHAM/DANG_KHAM). */
    public static int duDoanThoiGianCho(SessionContext session, String maPhieuKham) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.PHU_TA);
        PhieuKham pk = PhieuKham_DAO.layPhieuKhamTheoMa(maPhieuKham);
        if (pk == null) {
            return 0;
        }
        List<PhieuKham> cho = PhieuKham_DAO.layDanhSachTheoNgayVaPhong(
                LocalDate.now(), pk.getMaPhong(), TrangThaiPhieuKham.CHO_KHAM);
        List<PhieuKham> dangKham = PhieuKham_DAO.layDanhSachTheoNgayVaPhong(
                LocalDate.now(), pk.getMaPhong(), TrangThaiPhieuKham.DANG_KHAM);
        int viTri = 0;
        for (int i = 0; i < cho.size(); i++) {
            if (cho.get(i).getMaPhieuKham().equals(maPhieuKham)) {
                viTri = i;
                break;
            }
            viTri = cho.size();   // khong co trong hang cho -> xep cuoi
        }
        double dvTB = ChiDinhDichVu_DAO.trungBinhSoDichVuMoiPhieu();
        return AIHoTro.duDoanThoiGianCho(viTri, dangKham.size(), dvTB);
    }

    /** (5) Du bao vat tu 2 tuan toi — QUAN_LY. */
    public static List<Object[]> duBaoVatTu(SessionContext session) {
        QuyenUtil.kiemTraQuyen(session, ChucVu.QUAN_LY);
        LocalDate den = LocalDate.now();
        LocalDate tu = den.minusDays(29);
        var tanSuat = BaoCao_DAO.thongKeTanSuatDichVu(tu, den);
        int luotKham = 0;
        for (Object[] row : BaoCao_DAO.thongKeLuotKham(tu, den)) {
            luotKham += (Integer) row[1];
        }
        return AIHoTro.duBaoVatTu(tanSuat, luotKham);
    }

    private AIHoTro_BUS() { }
}
