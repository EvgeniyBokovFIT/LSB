import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class LSBEncoder {
    static final String MESSAGE = "message.txt";
    static final String ORIG_IMAGE = "cat.png";
    static final String IMAGE_WITH_LSB_INJECTION = "catLSB.png";

    static final int INT_SIZE_IN_BITS = Integer.BYTES * 8;

    public static void main(String[] args) throws Exception {

        BufferedImage imageWithInjection = getImageWithInjectedMessage(ORIG_IMAGE, MESSAGE);
        File cipheredImageFile = new File(IMAGE_WITH_LSB_INJECTION);
        ImageIO.write(imageWithInjection, "png", cipheredImageFile);
    }

    private static String readMessageFile(String filename) throws FileNotFoundException {
        StringBuilder contentOfMessageFile = new StringBuilder();
        File messageFile = new File(filename);
        Scanner scanner = new Scanner(messageFile);
        while (scanner.hasNextLine()) {
            String next = scanner.nextLine();
            contentOfMessageFile.append(next);
            if (scanner.hasNextLine()) {
                contentOfMessageFile.append("\n");
            }
        }
        scanner.close();
        return contentOfMessageFile.toString();
    }

    private static int[] messageToBinary(String msg) {
        List<Integer> binaryMessage = new ArrayList<>();

        for (int i = 0; i < msg.length(); i++) {
            binaryMessage.add((int) msg.charAt(i));
        }
        return binaryMessage.stream().mapToInt(elem -> elem).toArray();
    }

    private static BufferedImage readImageFile(String imageFilename) {
        File imageFile = new File(imageFilename);

        BufferedImage image;
        try {
            image = ImageIO.read(imageFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return image;

    }


    private static BufferedImage getImageWithInjectedMessage(String imageFilename, String messageFilename)
            throws FileNotFoundException {
        BufferedImage image = readImageFile(imageFilename);
        String contentOfMessageFile = readMessageFile(messageFilename);
        int[] messageBinary = messageToBinary(contentOfMessageFile);
        int messageLength = messageBinary.length;
        int messageBitIndex = 0;
        int lengthValueBitIndex = 0;

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                if (x == 0 && y < INT_SIZE_IN_BITS) {
                    int rgb = getRGBWithChangedBit(image.getRGB(x, y), messageLength, lengthValueBitIndex);
                    image.setRGB(x, y, rgb);
                    lengthValueBitIndex++;
                } else if (messageBitIndex < messageBinary.length * INT_SIZE_IN_BITS) {
                    int rgb = getRGBWithChangedBit(image.getRGB(x, y), messageBinary[messageBitIndex / INT_SIZE_IN_BITS],
                            messageBitIndex % INT_SIZE_IN_BITS);
                    image.setRGB(x, y, rgb);
                    messageBitIndex++;
                } else {
                    return image;
                }
            }
        }
        return image;
    }

    private static int getRGBWithChangedBit(int pixelRGB, int messageByte, int index) {
        int red = pixelRGB >> 16 & 255;
        int green = pixelRGB >> 8 & 255;
        int blue = pixelRGB & 255;

        int curLengthBit = messageByte >> (31 - index);
        curLengthBit &= 1;

        int blueWithChangedBit = (blue & 254) + curLengthBit;

        return (255 << 24) | (red << 16) | (green << 8) | blueWithChangedBit;
    }
}