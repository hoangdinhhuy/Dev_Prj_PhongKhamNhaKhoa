package entity;

public enum TrangThaiPhieuKham {
    CHO_KHAM, DANG_KHAM, HOAN_TAT;

    @Override
    public String toString() {
        return switch (this) {
            case CHO_KHAM -> "Chờ khám";
            case DANG_KHAM -> "Đang khám";
            case HOAN_TAT -> "Hoàn tất";
        };
    }
}
