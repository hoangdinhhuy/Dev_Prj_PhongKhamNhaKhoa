package util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.transform.Scale;

/** Ghi mot trang JavaFX da dan trang thanh PDF A4, khong can thu vien ngoai. */
public final class PdfImageUtil {
    private PdfImageUtil() { }

    public static void luuTrangA4(Node trang, Path tep) throws IOException {
        SnapshotParameters params = new SnapshotParameters();
        params.setTransform(new Scale(2, 2));
        WritableImage anh = trang.snapshot(params, null);
        int rong = (int) anh.getWidth();
        int cao = (int) anh.getHeight();
        BufferedImage rgb = new BufferedImage(rong, cao, BufferedImage.TYPE_INT_RGB);
        PixelReader pixel = anh.getPixelReader();
        for (int y = 0; y < cao; y++) {
            for (int x = 0; x < rong; x++) {
                rgb.setRGB(x, y, pixel.getArgb(x, y) & 0xFFFFFF);
            }
        }
        ByteArrayOutputStream jpeg = new ByteArrayOutputStream();
        ImageIO.write(rgb, "jpg", jpeg);
        byte[] anhJpeg = jpeg.toByteArray();
        byte[] lenhVe = "q\n595 0 0 842 0 0 cm\n/Im0 Do\nQ\n".getBytes(StandardCharsets.US_ASCII);

        ByteArrayOutputStream pdf = new ByteArrayOutputStream();
        int[] offset = new int[6];
        ghi(pdf, "%PDF-1.4\n%\u00e2\u00e3\u00cf\u00d3\n");
        doiTuong(pdf, offset, 1, "<< /Type /Catalog /Pages 2 0 R >>");
        doiTuong(pdf, offset, 2, "<< /Type /Pages /Kids [3 0 R] /Count 1 >>");
        doiTuong(pdf, offset, 3, "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] "
                + "/Resources << /XObject << /Im0 4 0 R >> >> /Contents 5 0 R >>");
        offset[4] = pdf.size();
        ghi(pdf, "4 0 obj\n<< /Type /XObject /Subtype /Image /Width " + rong + " /Height " + cao
                + " /ColorSpace /DeviceRGB /BitsPerComponent 8 /Filter /DCTDecode /Length "
                + anhJpeg.length + " >>\nstream\n");
        pdf.write(anhJpeg);
        ghi(pdf, "\nendstream\nendobj\n");
        offset[5] = pdf.size();
        ghi(pdf, "5 0 obj\n<< /Length " + lenhVe.length + " >>\nstream\n");
        pdf.write(lenhVe);
        ghi(pdf, "endstream\nendobj\n");
        int xref = pdf.size();
        ghi(pdf, "xref\n0 6\n0000000000 65535 f \n");
        for (int i = 1; i <= 5; i++) {
            ghi(pdf, String.format(java.util.Locale.ROOT, "%010d 00000 n \n", offset[i]));
        }
        ghi(pdf, "trailer\n<< /Size 6 /Root 1 0 R >>\nstartxref\n" + xref + "\n%%EOF\n");
        Files.write(tep, pdf.toByteArray());
    }

    private static void doiTuong(ByteArrayOutputStream out, int[] offset, int so, String noiDung)
            throws IOException {
        offset[so] = out.size();
        ghi(out, so + " 0 obj\n" + noiDung + "\nendobj\n");
    }

    private static void ghi(ByteArrayOutputStream out, String chuoi) throws IOException {
        out.write(chuoi.getBytes(StandardCharsets.ISO_8859_1));
    }
}
