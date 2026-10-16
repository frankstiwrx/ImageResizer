package com.frankstiwrx.imageresizer.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

public class BatchProcessor {

    public record BatchResult(
            int processed,
            int skipped,
            int failed
    ) {}

    public static BatchResult resizeFolder(
            File folder,
            double percentage) throws IOException {

        if (!Double.isFinite(percentage) || percentage <= 0) {
            throw new IllegalArgumentException(
                    "A porcentagem deve ser maior que zero."
            );
        }

        if (!folder.isDirectory()) {
            throw new IOException("Selecione uma pasta válida.");
        }

        File[] files = folder.listFiles((dir, name) ->
                name.toLowerCase(Locale.ROOT).endsWith(".png")
        );

        if (files == null) {
            throw new IOException("Não foi possível acessar a pasta.");
        }

        Path outputFolder = folder.toPath()
                .resolve("redimensionadas");

        Files.createDirectories(outputFolder);

        int processed = 0;
        int skipped = 0;
        int failed = 0;

        double factor = percentage / 100.0;

        for (File file : files) {

            try {

                BufferedImage original =
                        ImageProcessor.loadImage(file);

                double calculatedWidth =
                        original.getWidth() * factor;

                double calculatedHeight =
                        original.getHeight() * factor;

                if (calculatedWidth < 1 ||
                    calculatedHeight < 1 ||
                    calculatedWidth > 8192 ||
                    calculatedHeight > 8192) {

                    failed++;
                    continue;
                }

                int width = (int) Math.round(calculatedWidth);
                int height = (int) Math.round(calculatedHeight);

                Path destination = outputFolder.resolve(
                        file.getName()
                );

                // Não sobrescreve arquivos existentes
                if (Files.exists(destination)) {
                    skipped++;
                    continue;
                }

                BufferedImage resized =
                        ImageProcessor.resizeImage(
                                original,
                                width,
                                height
                        );

                ImageProcessor.saveImage(
                        resized,
                        destination.toFile()
                );

                processed++;

            } catch (IOException | IllegalArgumentException ex) {

                System.err.println(
                        "Erro ao processar "
                        + file.getName()
                        + ": "
                        + ex.getMessage()
                );

                failed++;
            }
        }

        return new BatchResult(processed, skipped, failed);
    }
}