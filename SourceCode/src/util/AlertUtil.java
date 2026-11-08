package util;

import javax.swing.JOptionPane;

/** Khuôn thông báo chung (Swing) — chỉ GUI được gọi. */
public final class AlertUtil {
    private AlertUtil() { }

    public static void baoLoi(String message) {
        JOptionPane.showMessageDialog(null,
                message == null || message.isBlank() ? MaThongBao.TB09 : message,
                "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    public static void baoThongTin(String message) {
        JOptionPane.showMessageDialog(null, message, "Thành công",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean xacNhan(String message) {
        return JOptionPane.showConfirmDialog(null, message, "Xác nhận",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION;
    }
}
