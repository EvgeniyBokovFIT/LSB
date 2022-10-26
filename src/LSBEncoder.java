import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class LSBEncoder {
    static final String MESSAGE_FILENAME = "C:\\Users\\Evgeniy\\message.txt";
    static final String ORIG_IMAGE_FILENAME = "C:\\Users\\Evgeniy\\cover.png";
    static final String CIPHERED_IMAGE_FILENAME = "C:\\Users\\Evgeniy\\steg.png";

    public static void main(String[] args) throws Exception {

        String contentOfMessageFile = readMessageFile();
        int[] bits = messageToBinary(contentOfMessageFile);
        System.out.println("msg in file " + contentOfMessageFile);
        System.out.println(Arrays.toString(bits));
        System.out.println();
        BufferedImage theImage = readImageFile(ORIG_IMAGE_FILENAME);
        hideTheMessage(bits, theImage);

    }

    private static String readMessageFile() throws FileNotFoundException {
        StringBuilder contentOfMessageFile = new StringBuilder();
        File messageFile = new File(MESSAGE_FILENAME);
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
        int binaryMessageIndex = 0;
        int[] binaryMessage = new int[msg.length() * 8];
        for (int i = 0; i < msg.length(); i++) {
            int curCharAsInt = msg.charAt(i);
            StringBuilder curCharBinary = new StringBuilder(Integer.toBinaryString(curCharAsInt));
            while (curCharBinary.length() != 8) {
                curCharBinary.insert(0, '0');
            }
            System.out.println("dec value for " + curCharAsInt + " is " + curCharBinary);

            for (int j = 0; j < 8; j++) {
                binaryMessage[binaryMessageIndex] = Integer.parseInt(String.valueOf(curCharBinary.charAt(j)));
                binaryMessageIndex++;
            }
        }
        return binaryMessage;
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

    public static void hideTheMessage(int[] messageBinary, BufferedImage image) throws Exception {
        final int maxMessageLengthInBits = Integer.BYTES * 8;
        File cipheredImageFile = new File(CIPHERED_IMAGE_FILENAME);
        int messageLength = messageBinary.length / 8;
        int[] messageLengthBinaryAsIntArray = getMessageLengthBinary(messageLength);

        int messageBinaryIndex = 0;
        int messageLengthBinaryIndex = 0;

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                if (x == 0 && y < maxMessageLengthInBits) {
                    int currentPixelRGB = image.getRGB(x, y);
                    int rgb = getNewRGB(currentPixelRGB, messageLengthBinaryAsIntArray, messageLengthBinaryIndex);
                    image.setRGB(x, y, rgb);
                    messageLengthBinaryIndex++;

                } else if (messageBinaryIndex < messageBinary.length) {
                    int currentPixelRGB = image.getRGB(x, y);
                    int rgb = getNewRGB(currentPixelRGB, messageBinary, messageBinaryIndex);
                    image.setRGB(x, y, rgb);
                    messageBinaryIndex++;
                }
            }
        }
        ImageIO.write(image, "png", cipheredImageFile);
    }

    private static int getNewRGB(int pixelRGB, int[] messageBinary, int messageIndex) {
        int red = pixelRGB >> 16 & 255;
        int green = pixelRGB >> 8 & 255;
        int blue = pixelRGB & 255;

        int blueWithChangedBit = (blue & 254) + messageBinary[messageIndex];

        return (255 << 24) | (red << 16) | (green << 8) | blueWithChangedBit;
    }

    private static int[] getMessageLengthBinary(int messageLength) {
        StringBuilder messageLengthBinary = new StringBuilder(Integer.toBinaryString(messageLength));
        int[] messageLengthBinaryAsIntArray = new int[Integer.BYTES * 8];

        while (messageLengthBinary.length() < Integer.BYTES * 8) {
            messageLengthBinary.insert(0, '0');
        }
        for (int i = 0; i < Integer.BYTES * 8; i++) {
            messageLengthBinaryAsIntArray[i] = Integer.parseInt(String.valueOf(messageLengthBinary.charAt(i)));
        }

        return  messageLengthBinaryAsIntArray;
    }
}