package util;

import java.io.IOException;
import java.nio.file.Path;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;

/** Xuất node JavaFX ra ảnh PNG nét (dùng cho hóa đơn/phiếu/bảng kê). */
public class PdfImageUtil {

    /** Lưu node JavaFX thành file PNG tỷ lệ 2x cho nét. */
    public static void luuTrangA4(Node trang, Path tep) throws IOException {
        double w = Math.max(1, trang.getBoundsInParent().getWidth());
        double h = Math.max(1, trang.getBoundsInParent().getHeight());
        SnapshotParameters sp = new SnapshotParameters();
        sp.setTransform(javafx.scene.transform.Transform.scale(2, 2));
        WritableImage anh = trang.snapshot(sp, new WritableImage((int) (w * 2), (int) (h * 2)));
        java.io.File f = tep.toFile();
        // đổi extension thành .png nếu người dùng chọn .pdf
        String name = f.getName();;
        java.nio.file.Path pngPath = f.toPath().getParent().resolve(name);
        java.awt.image.BufferedImage bimg = new java.awt.image.BufferedImage(
                (int) anh.getWidth(), (int) anh.getHeight(), java.awt.image.BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < (int) anh.getHeight(); y++)
            for (int x = 0; x < (int) anh.getWidth(); x++)
                bimg.setRGB(x, y, anh.getPixelReader().getArgb(x, y));
        javax.imageio.ImageIO.write(bimg, "png", pngPath.toFile());
    }
}
