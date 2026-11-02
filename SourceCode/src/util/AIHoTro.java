package util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Bo AI ho tro van hanh (DentCare AI): 
 * 1) Du doan thoi gian cho kham (cho phu ta) — tu vi tri hang cho + so luot dang kham + thoi luong trung binh.
 * 2) Du bao vat tu nha khoa (cho quan ly) — tu so dich vu da chi dinh 30 ngay qua x dinh muc vat tu chuan.
 * Chi la UOC TIN/DU BAO ho tro — phu ta van dieu phoi, nhan vien kho van quyet dinh nhap hang.
 */
public final class AIHoTro {

    /** Du doan phut cho doi: 5 phut thu tuc + (cac luot dang kham + cac luot dung truoc) x thoi luong 1 luot. */
    public static int duDoanThoiGianCho(int viTriHangCho, int soLuotDangKham, double dichVuTrungBinhMoiLuot) {
        int phutMoiLuot = 15 + 8 * Math.max(0, (int) Math.round(dichVuTrungBinhMoiLuot) - 1);
        int soLuotPhaiDoi = Math.max(0, viTriHangCho) + Math.max(0, soLuotDangKham);
        return 5 + phutMoiLuot * soLuotPhaiDoi;
    }

    /**
     * Du bao vat tu 2 tuan toi.
     * Dau vao: tanSuat = ket qua thong ke chi dinh 30 ngay (Object[]{tenDichVu, soLuot});
     *          luotKham30Ngay = tong so luot kham 30 ngay qua.
     * Tra ve: List Object[]{vatTu, daDung30Ngay(Integer), duBao2Tuan(Integer), ghiChu(String)}.
     */
    public static List<Object[]> duBaoVatTu(List<Object[]> tanSuatDichVu, int luotKham30Ngay) {
        Map<String, Integer> dung = new LinkedHashMap<>();

        // Dinh muc moi LUOT KHAM (ai cung dung)
        dung.put("Găng tay khám", dung.getOrDefault("Găng tay khám", 0) + luotKham30Ngay * 2);
        dung.put("Khẩu trang y tế", dung.getOrDefault("Khẩu trang y tế", 0) + luotKham30Ngay * 1);
        dung.put("Cốc nhựa súc miệng", dung.getOrDefault("Cốc nhựa súc miệng", 0) + luotKham30Ngay * 1);

        // Dinh muc theo DICH VU da chi dinh
        Map<String, Integer> heSo = new LinkedHashMap<>();
        heSo.put("cạo vôi", 0);       // -> Bộ đầu mút cạo vôi
        heSo.put("hàn", 0);           // -> Vật liệu trám răng
        heSo.put("trát", 0);          // -> Vật liệu trám răng
        heSo.put("nhổ", 0);           // -> Thuốc tê x1 + Gạc x2
        heSo.put("tủy", 0);           // -> Thuốc tê x2
        heSo.put("tẩy trắng", 0);     // -> Bộ kem tẩy trắng x1

        for (Object[] row : tanSuatDichVu) {
            String tenDV = String.valueOf(row[0]).toLowerCase();
            int soLuot = (Integer) row[1];
            for (String k : heSo.keySet()) {
                if (tenDV.contains(k)) {
                    if (k.equals("cạo vôi")) {
                        dung.put("Bộ đầu mút cạo vôi", dung.getOrDefault("Bộ đầu mút cạo vôi", 0) + soLuot);
                    } else if (k.equals("hàn") || k.equals("trát")) {
                        dung.put("Vật liệu trám răng", dung.getOrDefault("Vật liệu trám răng", 0) + soLuot);
                    } else if (k.equals("nhổ")) {
                        dung.put("Thuốc tê", dung.getOrDefault("Thuốc tê", 0) + soLuot);
                        dung.put("Gạc cầm máu", dung.getOrDefault("Gạc cầm máu", 0) + soLuot * 2);
                    } else if (k.equals("tủy")) {
                        dung.put("Thuốc tê", dung.getOrDefault("Thuốc tê", 0) + soLuot * 2);
                    } else if (k.equals("tẩy trắng")) {
                        dung.put("Bộ kem tẩy trắng", dung.getOrDefault("Bộ kem tẩy trắng", 0) + soLuot);
                    }
                }
            }
        }

        // Du bao 2 tuan = trung binh/ngay x 14 x he so du phong 10%
        List<Object[]> kq = new ArrayList<>();
        int duBaoCaoNhat = -1;
        String vatTuCaoNhat = null;
        List<Object[]> tam = new ArrayList<>();
        for (Map.Entry<String, Integer> e : dung.entrySet()) {
            int duBao = (int) Math.ceil(e.getValue() / 30.0 * 14 * 1.1);
            tam.add(new Object[]{e.getKey(), e.getValue(), duBao, ""});
            if (duBao > duBaoCaoNhat) {
                duBaoCaoNhat = duBao;
                vatTuCaoNhat = e.getKey();
            }
        }
        for (Object[] row : tam) {
            String ghiChu = row[1].equals(vatTuCaoNhat)
                    ? "Nhu cầu cao nhất — nên nhập sớm"
                    : "Bình thường";
            kq.add(new Object[]{row[0], row[1], row[2], ghiChu});
        }
        return kq;
    }

    private AIHoTro() { }
}
