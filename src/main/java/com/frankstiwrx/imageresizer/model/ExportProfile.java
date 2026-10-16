package com.frankstiwrx.imageresizer.model;

import java.util.List;

public record ExportProfile(
        String name,
        List<Variant> variants
) {

    public ExportProfile {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "O perfil precisa de um nome."
            );
        }

        if (variants == null || variants.isEmpty()) {
            throw new IllegalArgumentException(
                    "O perfil precisa de pelo menos uma variação."
            );
        }

        variants = List.copyOf(variants);
    }

    public record Variant(
            String template,
            int width,
            int height
    ) {

        public Variant {

            if (template == null || template.isBlank()) {
                throw new IllegalArgumentException(
                        "O nome do arquivo não pode estar vazio."
                );
            }

            if (width <= 0 || height <= 0
                    || width > 8192 || height > 8192) {

                throw new IllegalArgumentException(
                        "As dimensões devem estar entre 1 e 8192."
                );
            }
        }
    }

    @Override
    public String toString() {
        return name;
    }
}