package com.frankstiwrx.imageresizer.service;

import com.frankstiwrx.imageresizer.model.ExportProfile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.nio.file.Files;
import java.nio.file.Path;

import java.text.Normalizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

public class ProfileService {

    private static final Path PROFILE_FOLDER = Path.of(
            System.getProperty("user.home"),
            "ImageResizer",
            "profiles"
    );

    public static List<ExportProfile> listProfiles()
            throws IOException {

        List<ExportProfile> profiles = new ArrayList<>();

        // Perfil padrão para seis variações
        profiles.add(new ExportProfile(
                "Emblemas - 6 variacoes",
                List.of(
                        new ExportProfile.Variant(
                                "e_{id}_f.png", 128, 128
                        ),
                        new ExportProfile.Variant(
                                "e_{id}_f_l.png", 500, 500
                        ),
                        new ExportProfile.Variant(
                                "e_{id}_f_ll.png", 500, 500
                        ),
                        new ExportProfile.Variant(
                                "e_{id}_r.png", 128, 128
                        ),
                        new ExportProfile.Variant(
                                "e_{id}_r_l.png", 500, 500
                        ),
                        new ExportProfile.Variant(
                                "e_{id}_r_ll.png", 500, 500
                        )
                )
        ));

        // Perfil padrão para ícones
        profiles.add(new ExportProfile(
                "Icones",
                List.of(
                        new ExportProfile.Variant(
                                "{name}_64.png", 64, 64
                        ),
                        new ExportProfile.Variant(
                                "{name}_128.png", 128, 128
                        ),
                        new ExportProfile.Variant(
                                "{name}_256.png", 256, 256
                        ),
                        new ExportProfile.Variant(
                                "{name}_512.png", 512, 512
                        )
                )
        ));

        Files.createDirectories(PROFILE_FOLDER);

        try (Stream<Path> files = Files.list(PROFILE_FOLDER)) {

            List<Path> savedProfiles = files
                    .filter(path ->
                            path.toString().endsWith(".properties")
                    )
                    .sorted()
                    .toList();

            for (Path path : savedProfiles) {

                try {
                    profiles.add(loadProfile(path));
                } catch (IOException | RuntimeException ex) {

                    System.err.println(
                            "Perfil ignorado: "
                            + path.getFileName()
                            + " - "
                            + ex.getMessage()
                    );
                }
            }
        }

        return profiles;
    }

    public static void saveProfile(
            ExportProfile profile) throws IOException {

        Files.createDirectories(PROFILE_FOLDER);

        String normalizedName = Normalizer.normalize(
                profile.name(),
                Normalizer.Form.NFD
        );

        String fileName = normalizedName
                .replaceAll("\\p{M}", "")
                .toLowerCase(java.util.Locale.ROOT)
                .replaceAll("[^a-z0-9_-]", "_");

        if (fileName.isBlank()) {
            throw new IOException("Nome de perfil inválido.");
        }

        Path destination = PROFILE_FOLDER.resolve(
                fileName + ".properties"
        );

        Properties properties = new Properties();

        properties.setProperty(
                "name",
                profile.name()
        );

        properties.setProperty(
                "count",
                String.valueOf(profile.variants().size())
        );

        for (int i = 0; i < profile.variants().size(); i++) {

            ExportProfile.Variant variant =
                    profile.variants().get(i);

            properties.setProperty(
                    "variant." + i + ".template",
                    variant.template()
            );

            properties.setProperty(
                    "variant." + i + ".width",
                    String.valueOf(variant.width())
            );

            properties.setProperty(
                    "variant." + i + ".height",
                    String.valueOf(variant.height())
            );
        }

        try (OutputStream output =
                Files.newOutputStream(destination)) {

            properties.store(
                    output,
                    "ImageResizer Export Profile"
            );
        }
    }

    private static ExportProfile loadProfile(
            Path path) throws IOException {

        Properties properties = new Properties();

        try (InputStream input =
                Files.newInputStream(path)) {

            properties.load(input);
        }

        String name = properties.getProperty("name");

        int count = Integer.parseInt(
                properties.getProperty("count")
        );

        if (count < 1 || count > 100) {
            throw new IOException(
                    "Quantidade de variações inválida."
            );
        }

        List<ExportProfile.Variant> variants =
                new ArrayList<>();

        for (int i = 0; i < count; i++) {

            String template = properties.getProperty(
                    "variant." + i + ".template"
            );

            int width = Integer.parseInt(
                    properties.getProperty(
                            "variant." + i + ".width"
                    )
            );

            int height = Integer.parseInt(
                    properties.getProperty(
                            "variant." + i + ".height"
                    )
            );

            variants.add(
                    new ExportProfile.Variant(
                            template,
                            width,
                            height
                    )
            );
        }

        return new ExportProfile(name, variants);
    }
}