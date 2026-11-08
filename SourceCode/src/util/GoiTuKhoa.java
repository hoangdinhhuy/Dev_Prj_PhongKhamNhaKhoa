package util;

import java.text.Normalizer;

/** Thu vien xu ly tu khoa tim kiem (Gop y GV 2/6 — tim kiem dung ngu canh, khong can dau). */
public final class GoiTuKhoa {

    /** Chuan hoa: bo dau tieng Viet + lowercase — "Trám Răng" == "tram rang". */
    public static String chuanHoa(String s) {
        if (s == null) return "";
        String t = Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .replace('đ', 'd').replace('Đ', 'D')
                .toLowerCase().trim();
        return t;
    }

    /** Dich vu thuoc nhom dieu tri RANG CU THE — phai ghi ro so rang (Gop y GV 2/7).
     *  Luu y: chuoi so sanh phai KHONG DAU vi chuanHoa() da bo dau (bug da sua: "hàn" khong bao gio
     *  khop "han rang composite" sau khi bo dau). */
    public static boolean laDieuTriRang(String tenDichVu) {
        String t = chuanHoa(tenDichVu);
        return t.contains("han") || t.contains("tram") || t.contains("trat")
                || t.contains("nho") || t.contains("tuy") || t.contains("mao");
    }

    private GoiTuKhoa() { }
}
