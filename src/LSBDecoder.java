import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class LSBDecoder {
    static final String STEG_IMAGE_FILE = "C:\\Users\\Evgeniy\\steg.png";
    static final String DECODED_MESSAGE_FILE = "C:\\Users\\Evgeniy\\message_dec.txt";

    public static int length = 0;

    public static void main(String[] args) throws Exception {

        BufferedImage image = readImageFile(STEG_IMAGE_FILE);

        StringBuilder message = new StringBuilder();
        System.out.println("len is " + length * 8);
        String decodedMessageBinary = decodeTheMessageToBinary(image);
        for (int i = 0; i < length * 8; i = i + 8) {

            String sub = decodedMessageBinary.substring(i, i + 8);
            int m = Integer.parseInt(sub, 2);
            char ch = (char) m;
            message.append(ch);
        }
        System.out.println(decodedMessageBinary);
        System.out.println(message);
        PrintWriter out = new PrintWriter(new FileWriter(DECODED_MESSAGE_FILE, true), true);
        out.write(message.toString());
        out.close();
    }

    public static BufferedImage readImageFile(String COVERIMAGEFILE) {
        BufferedImage theImage = null;
        File p = new File(COVERIMAGEFILE);
        try {
            theImage = ImageIO.read(p);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return theImage;
    }


    public static String decodeTheMessageToBinary(BufferedImage image) {

        int messageIndex = 0;
        StringBuilder decodedMessage = new StringBuilder();
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                if (x == 0 && y < 32) {
                    int blue = image.getRGB(x, y) & 255;
                    int codedPart = blue & 1;
                    codedPart <<= 31 - y;
                    length += codedPart;

                } else if (messageIndex < length * 8) {
                    int blue = image.getRGB(x, y);
                    int lastBit = blue & 1;
                    String lastBitString = Integer.toBinaryString(lastBit);
                    decodedMessage.append(lastBitString);

                    messageIndex++;
                }
            }
        }
        System.out.println("bin value of msg hided in img is " + decodedMessage);
        return decodedMessage.toString();
    }

}