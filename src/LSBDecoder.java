import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class LSBDecoder {
    static final String IMAGE_WITH_LSB_INJECTION = "catLSB.png";
    static final String MESSAGE_FILE = "extractedMessage.txt";

    static final int INT_SIZE_IN_BITS = Integer.BYTES * 8;

    public static void main(String[] args) {
        try (PrintWriter out = new PrintWriter(new FileWriter(MESSAGE_FILE, false), true))
        {
            BufferedImage image = readImageFile(IMAGE_WITH_LSB_INJECTION);
            String message = extractMessage(image);
            System.out.println(message);
            out.write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static BufferedImage readImageFile(String imageFilename) throws IOException {
        BufferedImage image;
        File imageFile = new File(imageFilename);

        image = ImageIO.read(imageFile);

        return image;
    }


    private static String extractMessage(BufferedImage image) {
        int messageIndex = 0;
        int decodedSymbol = 0;
        int messageLength = extractLength(image);

        StringBuilder decodedMessage = new StringBuilder();
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                if (messageIndex <= messageLength * INT_SIZE_IN_BITS  && (x != 0 || y >= INT_SIZE_IN_BITS)) {
                    int blue = image.getRGB(x, y);
                    int lastBit = blue & 1;
                    decodedSymbol += (lastBit << (INT_SIZE_IN_BITS - 1) - (messageIndex % (INT_SIZE_IN_BITS)));

                    if (messageIndex % INT_SIZE_IN_BITS == 0 && messageIndex != 0) {
                        decodedMessage.append((char) decodedSymbol);
                        decodedSymbol = 0;
                    }
                    messageIndex++;
                }
            }
        }
        return decodedMessage.toString();
    }

    private static int extractLength(BufferedImage image) {
        int messageLength = 0;
        for(int y = 0; y < image.getHeight(); y++) {
            if (y < INT_SIZE_IN_BITS) {
                int blue = image.getRGB(0, y) & 255;
                int codedPart = blue & 1;
                codedPart <<= (INT_SIZE_IN_BITS - 1) - y;
                messageLength += codedPart;
            }
        }
        return messageLength;
    }
}