package com.frankstiwrx.imageresizer.service;

import com.frankstiwrx.imageresizer.model.ExportProfile;

import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class ImageVariantGenerator {

    public static List<Path> generate(
            File source,
            Path outputFolder,
            int imageId,
            ExportProfile profile) throws IOException {

        BufferedImage original =
                ImageProcessor.loadImage(source);

        Files.createDirectories(outputFolder);

        String id = String.format("%06d", imageId);

        String originalName = source.getName();

        int extension = originalName.lastIndexOf('.');

        if (extension > 0) {
            originalName = originalName.substring(
                    0,
                    extension
            );
        }

        // Evita caracteres inválidos em nomes de arquivos
        originalName = originalName.replaceAll(
                "[^\\p{L}\\p{N}_-]",
                "_"
        );

        List<Path> destinations = new ArrayList<>();

        Set<Path> uniqueDestinations = new HashSet<>();

        // Primeiro valida todos os nomes
        for (ExportProfile.Variant variant :
                profile.variants()) {

            String fileName = variant.template()
                    .replace("{id}", id)
                    .replace("{name}", originalName);

            if (fileName.isBlank()
                    || fileName.contains("/")
                    || fileName.contains("\\")
                    || fileName.contains("..")
                    || fileName.matches(".*[<>:\"|?*].*")
                    || !fileName.toLowerCase(Locale.ROOT)
                            .endsWith(".png")) {

                throw new IOException(
                        "Nome de arquivo inválido: "
                        + fileName
                );
            }

            Path destination = outputFolder.resolve(
                    fileName
            ).toAbsolutePath().normalize();

            if (!uniqueDestinations.add(destination)) {
                throw new IOException(
                        "O perfil gera nomes duplicados: "
                        + fileName
                );
            }

            if (Files.exists(destination)) {
                throw new IOException(
                        "O arquivo já existe: "
                        + fileName
                );
            }

            destinations.add(destination);
        }

        Map<String, BufferedImage> cache =
                new HashMap<>();

        List<Path> generated = new ArrayList<>();

        // Gera todas as variações
        for (int i = 0; i < profile.variants().size(); i++) {

            ExportProfile.Variant variant =
                    profile.variants().get(i);

            String key = variant.width()
                    + "x"
                    + variant.height();

            BufferedImage resized = cache.get(key);

            if (resized == null) {

                resized = ImageProcessor.resizeToFit(
                        original,
                        variant.width(),
                        variant.height()
                );

                cache.put(key, resized);
            }

            Path destination = destinations.get(i);

            ImageProcessor.saveImage(
                    resized,
                    destination.toFile()
            );

            generated.add(destination);
        }

        return generated;
    }
}