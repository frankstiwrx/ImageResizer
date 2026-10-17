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
        if (width > 8192 || height > 8192) {

    throw new IllegalArgumentException(
            "A dimensão máxima permitida é 8192 pixels."
    );
}

if ((long) width * height > 25_000_000L) {

    throw new IllegalArgumentException(
            "A imagem excede o limite de 25 milhões de pixels."
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
    public static BufferedImage resizeToFit(
        BufferedImage original,
        int targetWidth,
        int targetHeight) {

    if (targetWidth <= 0 || targetHeight <= 0) {
        throw new IllegalArgumentException(
                "As dimensões devem ser maiores que zero."
        );
    }
    if (targetWidth > 8192 || targetHeight > 8192) {

    throw new IllegalArgumentException(
            "A dimensão máxima permitida é 8192 pixels."
    );
}

if ((long) targetWidth * targetHeight > 25_000_000L) {

    throw new IllegalArgumentException(
            "A imagem excede o limite de 25 milhões de pixels."
    );
}
    

    // Calcula a escala proporcional
    double scaleX = (double) targetWidth / original.getWidth();
    double scaleY = (double) targetHeight / original.getHeight();

    double scale = Math.min(scaleX, scaleY);

    int newWidth = Math.max(
            1,
            (int) Math.round(original.getWidth() * scale)
    );

    int newHeight = Math.max(
            1,
            (int) Math.round(original.getHeight() * scale)
    );

    // Cria uma imagem com fundo transparente
    BufferedImage canvas = new BufferedImage(
            targetWidth,
            targetHeight,
            BufferedImage.TYPE_INT_ARGB
    );

    Graphics2D graphics = canvas.createGraphics();

    try {

        graphics.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC
        );

        graphics.setRenderingHint(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY
        );

        // Centraliza a imagem
        int x = (targetWidth - newWidth) / 2;
        int y = (targetHeight - newHeight) / 2;

        graphics.drawImage(
                original,
                x,
                y,
                newWidth,
                newHeight,
                null
        );

    } finally {
        graphics.dispose();
    }

    return canvas;
}
}