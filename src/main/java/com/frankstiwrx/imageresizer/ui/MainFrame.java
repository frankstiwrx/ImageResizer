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

public class MainFrame extends JFrame {

    private JButton btnSelectImage;
    private JButton btnResize;
    private JButton btnVariants;

    private JTextField txtWidth;
    private JTextField txtHeight;

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

        JPanel centerPanel = new JPanel(new GridLayout(6, 1, 10, 10));

        centerPanel.setBorder(
                BorderFactory.createEmptyBorder(20, 80, 20, 80)
        );

        lblFileName = new JLabel("Nenhuma imagem selecionada");

        lblOriginalSize = new JLabel("Dimensões originais: --");

        txtWidth = new JTextField("500");

        txtHeight = new JTextField("500");

        centerPanel.add(lblFileName);

        centerPanel.add(lblOriginalSize);

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

        btnVariants.setEnabled(false);

        buttonsPanel.add(btnSelectImage);

        buttonsPanel.add(btnResize);

        buttonsPanel.add(btnVariants);

        add(buttonsPanel, BorderLayout.SOUTH);


        // EVENTOS

        btnSelectImage.addActionListener(e -> selectImage());

        btnResize.addActionListener(e -> resizeSelectedImage());

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
}