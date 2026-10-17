# ImageResizer

**English** | [Português](#português)

A lightweight desktop tool built with **Java 21, Swing and Maven** for resizing PNG images and generating multiple variants with configurable filenames and sizes.

## Features

- Resize a single image using custom width and height.
- Scale images by percentage (for example, 50% or 200%).
- Batch-process all PNG files in a selected folder.
- Preserve PNG transparency.
- Generate multiple image variants while preserving aspect ratio.
- Create, edit, save and delete custom export profiles.
- Preview the selected image.

## Built-in profiles

| Profile | Output |
| --- | --- |
| Emblemas - 6 variacoes | 6 files: `_f`, `_f_l`, `_f_ll`, `_r`, `_r_l`, `_r_ll` (128 / 500 px) |
| Emblemas - Apenas R | 3 files: `_r`, `_r_l`, `_r_ll` (128 / 500 px) |
| Emblemas - Apenas F | 3 files: `_f`, `_f_l`, `_f_ll` (128 / 500 px) |
| Icones | 64, 128, 256 and 512 px |
| Logos Web | 128, 256, 512 and 1024 px |
| Android - Icones | 48, 72, 96, 144 and 192 px |

The Android preset exports PNG sizes only; it does **not** generate Android adaptive icon resources.

Custom profiles support filename templates such as `{name}_small.png` and `e_{id}_r.png`. `{name}` is the source filename without its extension; `{id}` is a six-digit numeric identifier.

## Requirements

- **To run the JAR:** Java 21 or newer.
- **To build from source:** JDK 21 or newer and Apache Maven.
- **Windows portable package:** includes its own Java runtime; no separate Java installation is needed.

## Build and run

```bash
git clone https://github.com/frankstiwrx/ImageResizer.git
cd ImageResizer
mvn clean package
java -jar target/imageresizer-1.0.0.jar
```

> The JAR filename depends on the `artifactId` and `version` in `pom.xml`. The JAR must contain a `Main-Class` manifest entry pointing to `com.frankstiwrx.imageresizer.Main`.

## Usage

1. Choose **Selecionar imagem** to open a PNG.
2. Set a new width and height, or apply a percentage, then click **Redimensionar**.
3. Use **Processar pasta** for batch resizing (results are placed in `redimensionadas`).
4. Use **Gerar variações** to select a preset or custom profile and export several PNGs.
5. Use **Gerenciar perfis** to create, edit or delete custom profiles. Built-in profiles are protected.

The application is intended for general image processing, including web assets, icons and game graphics. Upscaling changes pixel dimensions but cannot recover detail missing from the source image.

---

## Português

Ferramenta desktop leve, desenvolvida em **Java 21, Swing e Maven**, para redimensionar imagens PNG e gerar várias versões com nomes e dimensões configuráveis.

### Funcionalidades

- Redimensionamento individual com largura e altura personalizadas.
- Redimensionamento percentual (por exemplo, 50% ou 200%).
- Processamento em lote dos PNGs de uma pasta.
- Preservação da transparência dos arquivos PNG.
- Geração de variações mantendo a proporção original.
- Criação, edição, salvamento e exclusão de perfis personalizados.
- Pré-visualização da imagem selecionada.

### Perfis incluídos

| Perfil | Resultado |
| --- | --- |
| Emblemas - 6 variacoes | 6 arquivos: `_f`, `_f_l`, `_f_ll`, `_r`, `_r_l`, `_r_ll` (128 / 500 px) |
| Emblemas - Apenas R | 3 arquivos: `_r`, `_r_l`, `_r_ll` (128 / 500 px) |
| Emblemas - Apenas F | 3 arquivos: `_f`, `_f_l`, `_f_ll` (128 / 500 px) |
| Icones | 64, 128, 256 e 512 px |
| Logos Web | 128, 256, 512 e 1024 px |
| Android - Icones | 48, 72, 96, 144 e 192 px |

O perfil Android exporta apenas PNGs nas dimensões indicadas; ele **não** gera recursos de ícones adaptativos do Android.

Os perfis personalizados permitem modelos como `{name}_small.png` e `e_{id}_r.png`. `{name}` representa o nome da imagem original sem a extensão, e `{id}` representa um identificador numérico com seis dígitos.

### Requisitos

- **Para executar o JAR:** Java 21 ou superior.
- **Para compilar o projeto:** JDK 21 ou superior e Apache Maven.
- **Versão portátil para Windows:** acompanha seu próprio ambiente Java, dispensando instalação separada.

### Compilação e execução

```bash
git clone https://github.com/frankstiwrx/ImageResizer.git
cd ImageResizer
mvn clean package
java -jar target/imageresizer-1.0.0.jar
```

> O nome do JAR depende do `artifactId` e da `version` do `pom.xml`. O arquivo precisa ter a classe principal `com.frankstiwrx.imageresizer.Main` configurada no manifesto.

### Como usar

1. Clique em **Selecionar imagem** para abrir um PNG.
2. Informe largura e altura, ou aplique uma porcentagem, e clique em **Redimensionar**.
3. Use **Processar pasta** para redimensionar vários arquivos de uma vez (salvos em `redimensionadas`).
4. Use **Gerar variações** para selecionar um perfil e exportar várias versões.
5. Use **Gerenciar perfis** para criar, editar ou excluir perfis personalizados. Os perfis padrão são protegidos.

A ferramenta pode ser utilizada com imagens para sites, ícones e recursos gráficos de jogos. A ampliação aumenta a quantidade de pixels, mas não recupera detalhes inexistentes na imagem original.

---

**Repository:** https://github.com/frankstiwrx/ImageResizer  
**Version:** 1.0.0
