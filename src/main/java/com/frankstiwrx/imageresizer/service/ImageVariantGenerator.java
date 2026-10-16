package com.frankstiwrx.imageresizer.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ImageVariantGenerator {

    public static List<Path> generate(
            File source,
            Path outputFolder,
            int imageId,
            boolean includeF,
            boolean includeR) throws IOException {

        if (imageId <= 0 || imageId > 999999) {
            throw new IllegalArgumentException(
                    "O identificador deve estar entre 1 e 999999."
            );
        }

        if (!includeF && !includeR) {
            throw new IllegalArgumentException(
                    "Selecione pelo menos um grupo de variações."
            );
        }

        BufferedImage original =
                ImageProcessor.loadImage(source);

        // Gera as duas resoluções necessárias
        BufferedImage small =
                ImageProcessor.resizeToFit(
                        original,
                        128,
                        128
                );

        BufferedImage large =
                ImageProcessor.resizeToFit(
                        original,
                        500,
                        500
                );

        // Formata o identificador com seis dígitos
        String id = String.format("%06d", imageId);

        String baseName = "e_" + id;

        List<String> suffixes = new ArrayList<>();

        if (includeF) {
            suffixes.add("_f");
            suffixes.add("_f_l");
            suffixes.add("_f_ll");
        }

        if (includeR) {
            suffixes.add("_r");
            suffixes.add("_r_l");
            suffixes.add("_r_ll");
        }

        Files.createDirectories(outputFolder);

        List<Path> generatedFiles = new ArrayList<>();

        // Verifica se os arquivos já existem
        for (String suffix : suffixes) {

            Path destination = outputFolder.resolve(
                    baseName + suffix + ".png"
            );

            if (Files.exists(destination)) {
                throw new IOException(
                        "O arquivo já existe: "
                        + destination.getFileName()
                );
            }
        }

        // Gera os arquivos
        for (String suffix : suffixes) {

            boolean isSmall =
                    suffix.equals("_f") || suffix.equals("_r");

            BufferedImage image = isSmall ? small : large;

            Path destination = outputFolder.resolve(
                    baseName + suffix + ".png"
            );

            ImageProcessor.saveImage(
                    image,
                    destination.toFile()
            );

            generatedFiles.add(destination);
        }

        return generatedFiles;
    }
}