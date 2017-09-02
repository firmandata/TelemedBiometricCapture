package helpers;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.apache.commons.codec.binary.Base64;

public class ImageHelper {
    
    public static String jpegToBase64(final Image image) {
        return jpegToBase64(imageToBufferedImage(image), "JPEG");
    }
    
    public static String jpegToBase64(final Image image, final String format) {
        return jpegToBase64(imageToBufferedImage(image), format);
    }
    
    public static String jpegToBase64(final BufferedImage bufferedImage) {
        return jpegToBase64(bufferedImage, "JPEG");
    }
    
    public static String jpegToBase64(final BufferedImage bufferedImage, final String format) {
        String base64Encoded = null; 
        
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        
        try {
            ImageIO.write(bufferedImage, format, byteArrayOutputStream);
            base64Encoded = Base64.encodeBase64String(byteArrayOutputStream.toByteArray());
            
            byteArrayOutputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(ImageHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ImageHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return base64Encoded;
    }
    
    public static BufferedImage imageToBufferedImage(final Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }

        // Create a buffered image with transparency
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.drawImage(image, 0, 0, null);
        graphics2D.dispose();

        // Return the buffered image
        return bufferedImage;
    }
    
    public static BufferedImage base64ToImage(String imageString) {
        BufferedImage bufferedImage = null;
        
        try {
            byte[] imageByte = Base64.decodeBase64(imageString);
            
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageByte);
            bufferedImage = ImageIO.read(byteArrayInputStream);
            byteArrayInputStream.close();
        } catch (Exception ex) {
            Logger.getLogger(ImageHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return bufferedImage;
    }
    
    public static byte[] base64ToBytes(String imageString) {
        return Base64.decodeBase64(imageString);
    }
    
    public static Image bufferedImageToImage(final BufferedImage bufferedImage) {
        return (Image) bufferedImage;
    }
}
