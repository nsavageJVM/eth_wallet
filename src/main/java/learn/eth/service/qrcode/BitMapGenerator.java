package learn.eth.service.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
@Component
public class BitMapGenerator {


    BufferedImage createQRCode(String data) throws WriterException {
        int size = 256;
        int white = 255 << 16 | 255 << 8 | 255;
        int black = 0;
        MultiFormatWriter barcodeWriter = new MultiFormatWriter();

        BitMatrix barcodeBitMatrix = barcodeWriter.encode(data, BarcodeFormat.AZTEC, size, size);
        BufferedImage barcodeBitmap = new BufferedImage (size, size, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                barcodeBitmap.setRGB(x, y, barcodeBitMatrix.get(x, y) ? black: white);
            }
        }
        return barcodeBitmap;
    }


    public static void main(String[] args) throws WriterException, IOException {
        BitMapGenerator gen = new BitMapGenerator();
        BufferedImage image =  gen.createQRCode("this is a test");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageOutputStream ios = new MemoryCacheImageOutputStream(out);
        ImageIO.write(image, "png", ios);


    }
}











