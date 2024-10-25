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
        return generate(String.format("https://www.serchservice.com/platform/certificates?verify=%s", secret), 300, 300);
    }

//    @SneakyThrows
//    private String generate(String url, Integer height, Integer width) {
//        QRCodeWriter qrCodeWriter = new QRCodeWriter();
//
//        BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, width, height);
//        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        ImageIO.write(bufferedImage, "PNG", outputStream);
//        outputStream.flush();
//
//        return Base64.encodeBase64String(outputStream.toByteArray());
//    }

    @SneakyThrows
    private String generate(String url, Integer height, Integer width) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, width, height);

        // Convert the BitMatrix to a BufferedImage with a transparent background
        BufferedImage qrImage = createTransparentQRCode(bitMatrix, width, height);

        // Add a logo to the QR code from the URL
        BufferedImage logo = ImageIO.read(toUrl());
        int logoWidth = width / 5;
        int logoHeight = height / 5;
        addLogoToQRCode(qrImage, logo, logoWidth, logoHeight);

        // Convert the final image to a Base64 encoded string
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "PNG", outputStream);
        outputStream.flush();

        return Base64.encodeBase64String(outputStream.toByteArray());
    }

    private BufferedImage createTransparentQRCode(BitMatrix bitMatrix, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0x00FFFFFF); // Black or transparent
            }
        }
        return image;
    }

    private void addLogoToQRCode(BufferedImage qrImage, BufferedImage logo, int logoWidth, int logoHeight) {
        Graphics2D graphics = qrImage.createGraphics();
        int centerX = (qrImage.getWidth() - logoWidth) / 2;
        int centerY = (qrImage.getHeight() - logoHeight) / 2;

        // Draw the logo with transparency enabled
        graphics.setComposite(AlphaComposite.SrcOver);
        graphics.drawImage(logo, centerX, centerY, logoWidth, logoHeight, null);
        graphics.dispose();
    }

    private URL toUrl() throws Exception {
        return new URI("https://chxpalpeslofqzeulcjr.supabase.co/storage/v1/object/public/serch/logo/logo.png").toURL();
    }

    @Override
    public String generateAdminDetails(String secret) {
        return generate(String.format("https://card.serchservice.com?verify=%s", secret), 350, 350);
    }

    @Override
    public String generateMFA(String secretKey, String emailAddress, Role role) {
        return generate(getBarCode(secretKey, emailAddress, String.format("Serch %s", role.getType())), 300, 300);
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