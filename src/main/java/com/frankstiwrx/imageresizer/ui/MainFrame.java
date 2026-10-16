package com.frankstiwrx.imageresizer.ui;

import com.frankstiwrx.imageresizer.service.ImageProcessor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.frankstiwrx.imageresizer.service.BatchProcessor;
import javax.swing.SwingWorker;

import com.frankstiwrx.imageresizer.service.ImageVariantGenerator;
import java.nio.file.Path;
import java.util.List;

public class MainFrame extends JFrame {

    private JButton btnSelectImage;
    private JButton btnResize;
    private JButton btnVariants;
    private JButton btnBatch;

    private JTextField txtWidth;
    private JTextField txtHeight;

    private JTextField txtScale;
    private JButton btnApplyScale;

    private JLabel lblFileName;
    private JLabel lblOriginalSize;

    private File selectedFile;
    private BufferedImage originalImage;

    public MainFrame() {

        initializeWindow();
        initializeComponents();

    }

    private void initializeWindow() {

        setTitle("ImageResizer");

        setSize(800, 500);

        setMinimumSize(new Dimension(650, 400));

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

    }

    private void initializeComponents() {

        // CABEÇALHO
        JPanel headerPanel = new JPanel();

        headerPanel.setBorder(
                BorderFactory.createEmptyBorder(25, 20, 20, 20)
        );

        JLabel title = new JLabel("ImageResizer");

        title.setFont(new Font("Arial", Font.BOLD, 28));

        headerPanel.add(title);

        add(headerPanel, BorderLayout.NORTH);

        // PAINEL CENTRAL
        JPanel centerPanel = new JPanel(new GridLayout(8, 1, 10, 10));

        centerPanel.setBorder(
                BorderFactory.createEmptyBorder(20, 80, 20, 80)
        );

        lblFileName = new JLabel("Nenhuma imagem selecionada");

        lblOriginalSize = new JLabel("Dimensões originais: --");

        txtWidth = new JTextField("500");

        txtHeight = new JTextField("500");

        txtScale = new JTextField("100");

        btnApplyScale = new JButton("Aplicar %");

        JPanel scalePanel = new JPanel(new BorderLayout(10, 0));

        scalePanel.add(txtScale, BorderLayout.CENTER);

        scalePanel.add(btnApplyScale, BorderLayout.EAST);

        centerPanel.add(lblFileName);

        centerPanel.add(lblOriginalSize);

        centerPanel.add(
                new JLabel("Escala (%): 50 = metade | 100 = original | 200 = dobro")
        );

        centerPanel.add(scalePanel);

        centerPanel.add(new JLabel("Nova largura (px):"));

        centerPanel.add(txtWidth);

        centerPanel.add(new JLabel("Nova altura (px):"));

        centerPanel.add(txtHeight);

        add(centerPanel, BorderLayout.CENTER);

        // BOTÕES
        JPanel buttonsPanel = new JPanel(new FlowLayout());

        buttonsPanel.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 30, 20)
        );

        btnSelectImage = new JButton("Selecionar imagem");

        btnResize = new JButton("Redimensionar");

        btnVariants = new JButton("Gerar variações");
        
        btnBatch = new JButton("Processar pasta");

        buttonsPanel.add(btnSelectImage);

        buttonsPanel.add(btnResize);
        
        buttonsPanel.add(btnBatch);

        buttonsPanel.add(btnVariants);

        add(buttonsPanel, BorderLayout.SOUTH);

// EVENTOS
        btnSelectImage.addActionListener(e -> selectImage());

        btnResize.addActionListener(e -> resizeSelectedImage());

        btnApplyScale.addActionListener(e -> applyScale());

        txtScale.addActionListener(e -> applyScale());
        
        btnBatch.addActionListener(e -> processBatch());
        
        btnVariants.addActionListener(e -> generateVariants());

    }

    private void applyScale() {

        if (originalImage == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecione uma imagem primeiro."
            );

            return;
        }

        try {

            // Lê a porcentagem informada pelo usuário
            String input = txtScale.getText()
                    .trim()
                    .replace(",", ".");

            double percentage = Double.parseDouble(input);

            // Validação
            if (!Double.isFinite(percentage) || percentage <= 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Informe uma porcentagem válida maior que zero."
                );

                return;
            }

            // Calcula o fator de escala
            double factor = percentage / 100.0;

            double calculatedWidth = originalImage.getWidth() * factor;
            double calculatedHeight = originalImage.getHeight() * factor;

            // Evita dimensões excessivas
            if (calculatedWidth > 8192 || calculatedHeight > 8192) {

                JOptionPane.showMessageDialog(
                        this,
                        "A resolução máxima permitida é 8192 pixels por dimensão."
                );

                return;
            }

            // Calcula as novas dimensões
            int newWidth = Math.max(
                    1,
                    (int) Math.round(calculatedWidth)
            );

            int newHeight = Math.max(
                    1,
                    (int) Math.round(calculatedHeight)
            );

            // Atualiza os campos automaticamente
            txtWidth.setText(String.valueOf(newWidth));

            txtHeight.setText(String.valueOf(newHeight));

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Informe uma porcentagem numérica válida.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );

        }
    }

    private void selectImage() {

        JFileChooser chooser = new JFileChooser();

        chooser.setDialogTitle("Selecionar imagem");

        chooser.setFileFilter(
                new FileNameExtensionFilter(
                        "Imagens PNG", "png"
                )
        );

        int result = chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {

            try {

                File file = chooser.getSelectedFile();

                BufferedImage image = ImageProcessor.loadImage(file);

                selectedFile = file;
                originalImage = image;

                lblFileName.setText(
                        "Arquivo: " + selectedFile.getName()
                );

                lblOriginalSize.setText(
                        "Dimensões originais: "
                        + originalImage.getWidth()
                        + " x "
                        + originalImage.getHeight()
                );

                txtWidth.setText(
                        String.valueOf(originalImage.getWidth())
                );

                txtHeight.setText(
                        String.valueOf(originalImage.getHeight())
                );

                txtScale.setText("100");

            } catch (IOException ex) {

                JOptionPane.showMessageDialog(
                        this,
                        ex.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                );

            }
        }
    }

    private void resizeSelectedImage() {

        if (originalImage == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecione uma imagem primeiro."
            );

            return;
        }

        try {

            int width = Integer.parseInt(txtWidth.getText().trim());

            int height = Integer.parseInt(txtHeight.getText().trim());

            if (width <= 0 || height <= 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "As dimensões devem ser maiores que zero."
                );

                return;
            }

            JFileChooser chooser = new JFileChooser();

            chooser.setDialogTitle("Salvar imagem redimensionada");

            String originalName = selectedFile.getName();

            int extensionIndex = originalName.lastIndexOf('.');

            if (extensionIndex > 0) {
                originalName = originalName.substring(0, extensionIndex);
            }

            chooser.setSelectedFile(
                    new File(originalName + "_resized.png")
            );

            int result = chooser.showSaveDialog(this);

            if (result == JFileChooser.APPROVE_OPTION) {

                File destination = chooser.getSelectedFile();

                if (!destination.getName().toLowerCase().endsWith(".png")) {
                    destination = new File(
                            destination.getParentFile(),
                            destination.getName() + ".png"
                    );
                }

                if (destination.getCanonicalFile().equals(
                        selectedFile.getCanonicalFile())) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Escolha outro nome para preservar a imagem original."
                    );

                    return;
                }

                if (destination.exists()) {

                    int confirmation = JOptionPane.showConfirmDialog(
                            this,
                            "O arquivo já existe. Deseja substituí-lo?",
                            "Confirmar substituição",
                            JOptionPane.YES_NO_OPTION
                    );

                    if (confirmation != JOptionPane.YES_OPTION) {
                        return;
                    }
                }

                BufferedImage resized = ImageProcessor.resizeImage(
                        originalImage,
                        width,
                        height
                );

                ImageProcessor.saveImage(
                        resized,
                        destination
                );

                JOptionPane.showMessageDialog(
                        this,
                        "Imagem redimensionada com sucesso!"
                );
            }

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Informe dimensões numéricas válidas.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (IOException | IllegalArgumentException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );

        }
    }
    
    private void processBatch() {

    // Solicita a porcentagem de redimensionamento
    double percentage;

    try {

        String input = JOptionPane.showInputDialog(
                this,
                "Informe a escala percentual:\n"
                + "50 = metade | 100 = original | 200 = dobro",
                txtScale.getText()
        );

        if (input == null) {
            return;
        }

        percentage = Double.parseDouble(
                input.trim().replace(",", ".")
        );

        if (!Double.isFinite(percentage) || percentage <= 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Informe uma porcentagem válida maior que zero."
            );

            return;
        }

    } catch (NumberFormatException ex) {

        JOptionPane.showMessageDialog(
                this,
                "Porcentagem inválida.",
                "Erro",
                JOptionPane.ERROR_MESSAGE
        );

        return;
    }

    // Seleciona a pasta de imagens
    JFileChooser chooser = new JFileChooser();

    chooser.setDialogTitle("Selecionar pasta de imagens");

    chooser.setFileSelectionMode(
            JFileChooser.DIRECTORIES_ONLY
    );

    int result = chooser.showOpenDialog(this);

    if (result != JFileChooser.APPROVE_OPTION) {
        return;
    }

    File folder = chooser.getSelectedFile();

    btnBatch.setEnabled(false);

    // Processamento em segundo plano
    SwingWorker<BatchProcessor.BatchResult, Void> worker =
            new SwingWorker<>() {

        @Override
        protected BatchProcessor.BatchResult doInBackground()
                throws Exception {

            return BatchProcessor.resizeFolder(
                    folder,
                    percentage
            );
        }

        @Override
        protected void done() {

            btnBatch.setEnabled(true);

            try {

                BatchProcessor.BatchResult result = get();

                JOptionPane.showMessageDialog(
                        MainFrame.this,
                        "Processamento concluído!\n\n"
                        + "Processadas: " + result.processed()
                        + "\nIgnoradas: " + result.skipped()
                        + "\nFalhas: " + result.failed()
                        + "\n\nPasta: redimensionadas"
                );

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                        MainFrame.this,
                        "Erro no processamento: "
                        + ex.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    };

    worker.execute();
}
    
    private void generateVariants() {

    if (selectedFile == null) {

        JOptionPane.showMessageDialog(
                this,
                "Selecione uma imagem primeiro."
        );

        return;
    }

    // Solicita o identificador
    String input = JOptionPane.showInputDialog(
            this,
            "Informe o identificador numérico da imagem:\n"
            + "Exemplo: 108"
    );

    if (input == null) {
        return;
    }

    int imageId;

    try {

        imageId = Integer.parseInt(input.trim());

        if (imageId <= 0 || imageId > 999999) {
            throw new NumberFormatException();
        }

    } catch (NumberFormatException ex) {

        JOptionPane.showMessageDialog(
                this,
                "Informe um identificador entre 1 e 999999."
        );

        return;
    }

    // Escolha do tipo de variações
    String[] options = {
        "Apenas R",
        "Apenas F",
        "F e R",
        "Cancelar"
    };

    int choice = JOptionPane.showOptionDialog(
            this,
            "Quais variações deseja gerar?",
            "Gerador de variações",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[2]
    );

    if (choice < 0 || choice == 3) {
        return;
    }

    boolean includeR = choice == 0 || choice == 2;
    boolean includeF = choice == 1 || choice == 2;

    // Define a pasta de saída
    String formattedId = String.format("%06d", imageId);

    Path outputFolder = selectedFile.toPath()
            .getParent()
            .resolve("variacoes_" + formattedId);

    btnVariants.setEnabled(false);

    // Executa a geração em segundo plano
    SwingWorker<List<Path>, Void> worker = new SwingWorker<>() {

        @Override
        protected List<Path> doInBackground() throws Exception {

            return ImageVariantGenerator.generate(
                    selectedFile,
                    outputFolder,
                    imageId,
                    includeF,
                    includeR
            );
        }

        @Override
        protected void done() {

            btnVariants.setEnabled(true);

            try {

                List<Path> generated = get();

                JOptionPane.showMessageDialog(
                        MainFrame.this,
                        "Variações geradas com sucesso!\n\n"
                        + "Arquivos criados: "
                        + generated.size()
                        + "\n\nPasta:\n"
                        + outputFolder
                );

            } catch (Exception ex) {

                Throwable cause = ex.getCause();

                String message = cause != null
                        ? cause.getMessage()
                        : ex.getMessage();

                JOptionPane.showMessageDialog(
                        MainFrame.this,
                        "Erro ao gerar variações:\n" + message,
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    };

    worker.execute();
}
    
}
