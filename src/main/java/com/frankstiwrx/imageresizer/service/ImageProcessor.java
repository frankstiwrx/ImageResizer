package com.frankstiwrx.imageresizer.service;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageProcessor {

    public static BufferedImage loadImage(File file) throws IOException {

        BufferedImage image = ImageIO.read(file);

        if (image == null) {
            throw new IOException("Formato de imagem inválido ou não suportado.");
        }

        return image;
    }

    public static BufferedImage resizeImage(
            BufferedImage original,
            int width,
            int height) {

        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException(
                    "As dimensões devem ser maiores que zero."
            );
        }

        BufferedImage resized = new BufferedImage(
                width,
                height,
                BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D graphics = resized.createGraphics();

        try {

            graphics.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC
            );

            graphics.setRenderingHint(
                    RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY
            );

            graphics.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            graphics.drawImage(
                    original,
                    0,
                    0,
                    width,
                    height,
                    null
            );

        } finally {
            graphics.dispose();
        }

        return resized;
    }

    public static void saveImage(
            BufferedImage image,
            File destination) throws IOException {

        boolean saved = ImageIO.write(
                image,
                "png",
                destination
        );

        if (!saved) {
            throw new IOException("Não foi possível salvar a imagem.");
        }
    }
}