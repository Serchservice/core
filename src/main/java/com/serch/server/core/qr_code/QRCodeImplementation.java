package com.serch.server.core.qr_code;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.serch.server.enums.auth.Role;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class QRCodeImplementation implements QRCodeService {
    @Override
    public String platformCertificate(String secret) {
        return generate(
                String.format("https://www.serchservice.com/platform/certificates?verify=%s", secret),
                300,
                300,
                false,
                0xFF050404
        );
    }

    @SneakyThrows
    private String generate(String url, Integer height, Integer width, boolean isTransparent, int color) {
        BufferedImage qrImage = getBufferedImage(url, height, width, isTransparent, color);
        addLogo(height, width, qrImage, isTransparent);

        // Convert the final image to a Base64 encoded string
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "PNG", outputStream);
        outputStream.flush();

        return Base64.encodeBase64String(outputStream.toByteArray());
    }

    @SneakyThrows
    private BufferedImage getBufferedImage(String url, Integer height, Integer width, boolean isTransparent, int color) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, width, height);

        // Convert the BitMatrix to a BufferedImage with the specified color
        return createScaledQRCode(trimWhitePadding(bitMatrix), width, height, isTransparent, color);
    }

    private BitMatrix trimWhitePadding(BitMatrix matrix) {
        int[] enclosingRectangle = matrix.getEnclosingRectangle();
        int newWidth = enclosingRectangle[2];
        int newHeight = enclosingRectangle[3];

        BitMatrix trimmedMatrix = new BitMatrix(newWidth, newHeight);
        for (int x = 0; x < newWidth; x++) {
            for (int y = 0; y < newHeight; y++) {
                if (matrix.get(x + enclosingRectangle[0], y + enclosingRectangle[1])) {
                    trimmedMatrix.set(x, y);
                }
            }
        }
        return trimmedMatrix;
    }

    private BufferedImage createScaledQRCode(BitMatrix bitMatrix, int width, int height, boolean isTransparent, int color) {
        // Determine padding when not transparent
        int padding = isTransparent ? 0 : Math.min(width, height) / 20; // 5% of the smaller dimension

        // Adjust dimensions for padding
        int imageWidth = width + 2 * padding;
        int imageHeight = height + 2 * padding;

        // Create the BufferedImage with adjusted dimensions
        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        int backgroundColor = isTransparent ? 0x00FFFFFF : 0xFFFFFFFF; // Transparent or white background

        int matrixWidth = bitMatrix.getWidth();
        int matrixHeight = bitMatrix.getHeight();

        // Calculate the scale factors for width and height based on the original width and height
        double scaleX = (double) width / matrixWidth;
        double scaleY = (double) height / matrixHeight;

        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setComposite(AlphaComposite.Src);

        // Fill the entire image with the background color
        graphics.setColor(new Color(backgroundColor, true));
        graphics.fillRect(0, 0, imageWidth, imageHeight);

        // Draw the scaled QR code onto the image with the padding offset
        graphics.setColor(new Color(color, true));
        for (int x = 0; x < matrixWidth; x++) {
            for (int y = 0; y < matrixHeight; y++) {
                if (bitMatrix.get(x, y)) {
                    int scaledX = (int) (x * scaleX) + padding;
                    int scaledY = (int) (y * scaleY) + padding;
                    int scaledWidth = (int) ((x + 1) * scaleX) - (int) (x * scaleX);
                    int scaledHeight = (int) ((y + 1) * scaleY) - (int) (y * scaleY);
                    graphics.fillRect(scaledX, scaledY, scaledWidth, scaledHeight);
                }
            }
        }
        graphics.dispose();
        return image;
    }

    private void addLogo(Integer height, Integer width, BufferedImage qrImage, boolean isTransparent) throws Exception {
        // Add a logo to the QR code from the URL
        BufferedImage logo = ImageIO.read(toUrl());
        int logoWidth = width / 5;
        int logoHeight = height / 5;

        addLogoToQRCode(qrImage, logo, logoWidth, logoHeight, isTransparent);
    }

    private void addLogoToQRCode(BufferedImage qrImage, BufferedImage logo, int logoWidth, int logoHeight, boolean isTransparent) {
        Graphics2D graphics = qrImage.createGraphics();
        // Reduce the logo size when isTransparent is false
        if (!isTransparent) {
            logoWidth = logoWidth * 2 / 3; // Reduce the logo width
            logoHeight = logoHeight * 2 / 3; // Reduce the logo height
        }

        int centerX = (qrImage.getWidth() - logoWidth) / 2;
        int centerY = (qrImage.getHeight() - logoHeight) / 2;

        // If isTransparent is false, add a square background with border radius
        if (!isTransparent) {
            int backgroundSize = Math.max(logoWidth, logoHeight) + 10; // Slightly larger than the reduced logo
            int backgroundX = (qrImage.getWidth() - backgroundSize) / 2;
            int backgroundY = (qrImage.getHeight() - backgroundSize) / 2;
            int borderRadius = 15; // Border radius for rounded corners

            // Draw the square background with rounded corners
            graphics.setColor(new Color(0x0E1218));
            graphics.fillRoundRect(backgroundX, backgroundY, backgroundSize, backgroundSize, borderRadius, borderRadius);
        }

        // Draw the logo on top
        graphics.setComposite(AlphaComposite.SrcOver);
        graphics.drawImage(logo, centerX, centerY, logoWidth, logoHeight, null);
        graphics.dispose();
    }

    private URL toUrl() throws Exception {
        return new URI("https://chxpalpeslofqzeulcjr.supabase.co/storage/v1/object/public/serch/logo/logo.png").toURL();
    }

    @Override
    public String generateAdminDetails(String secret) {
        return generate(
                String.format("https://card.serchservice.com/%s", secret),
                350,
                350,
                true,
                0xFF151515
        );
    }

    @Override
    public String generateMFA(String secretKey, String emailAddress, Role role) {
        return generate(
                getBarCode(secretKey, emailAddress, String.format("Serch %s", role.getType())),
                300,
                300,
                true,
                0xFF050404
        );
    }

    /**
     * Constructs a URI for generating a QR code to assist the user in setting up their MFA authenticator app.
     * <p>
     * The URI is formatted according to the Key URI format as specified by Google Authenticator,
     * and it includes the secret key, account name, and issuer information.
     * </p>
     *
     * @param secretKey Base32 encoded secret key (may have optional whitespace).
     * @param account   The user's account name, such as an email address or username.
     * @param issuer    The organization managing this account (e.g., "MyApp").
     *
     * @return A {@link String} representing the URI for the QR code.
     *
     * @see <a href="https://github.com/google/google-authenticator/wiki/Key-Uri-Format">Key URI Format</a>
     */
    private String getBarCode(String secretKey, String account, String issuer) {
        String normalizedBase32Key = secretKey.replace(" ", "").toUpperCase();
        return "otpauth://totp/"
                + URLEncoder.encode(issuer + ":" + account, StandardCharsets.UTF_8).replace("+", "%20")
                + "?secret=" + URLEncoder.encode(normalizedBase32Key, StandardCharsets.UTF_8).replace("+", "%20")
                + "&issuer=" + URLEncoder.encode(issuer, StandardCharsets.UTF_8).replace("+", "%20");
    }
}