package entity;

public enum ChucVu {
    LE_TAN, BAC_SI, PHU_TA, QUAN_LY;

    @Override
    public String toString() {
        return switch (this) {
            case LE_TAN -> "Lễ tân";
            case BAC_SI -> "Bác sĩ";
            case PHU_TA -> "Phụ tá";
            case QUAN_LY -> "Quản lý";
        };
    }
}
