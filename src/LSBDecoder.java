import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class LSBDecoder {
    static final String STEG_IMAGE_FILE = "C:\\Users\\Evgeniy\\steg.png";
    static final String DECODED_MESSAGE_FILE = "C:\\Users\\Evgeniy\\message_dec.txt";

    public static void main(String[] args) throws Exception {

        BufferedImage image = readImageFile(STEG_IMAGE_FILE);

        StringBuilder message = new StringBuilder();
        String decodedMessageBinary = decodeTheMessageToBinary(image);
        for (int i = 0; i < decodedMessageBinary.length(); i += 8) {

            String bit = decodedMessageBinary.substring(i, i + 8);
            int bitInt = Integer.parseInt(bit, 2);
            message.append((char)bitInt);
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
        int messageLength = 0;
        StringBuilder decodedMessage = new StringBuilder();
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                if (x == 0 && y < 32) {
                    int blue = image.getRGB(x, y) & 255;
                    int codedPart = blue & 1;
                    codedPart <<= 31 - y;
                    messageLength += codedPart;

                } else if (messageIndex < messageLength * 8) {
                    int blue = image.getRGB(x, y);
                    int lastBit = blue & 1;
                    String lastBitString = Integer.toBinaryString(lastBit);
                    decodedMessage.append(lastBitString);

                    messageIndex++;
                }
            }
        }
        return decodedMessage.toString();
    }

}