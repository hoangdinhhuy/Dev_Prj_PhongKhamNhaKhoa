package entity;

public enum TrangThaiDichVu {
    DANG_PHUC_VU, NGUNG_PHUC_VU;

    @Override
    public String toString() {
        return this == DANG_PHUC_VU ? "Đang phục vụ" : "Ngừng phục vụ";
    }
}
