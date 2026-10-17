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
import com.frankstiwrx.imageresizer.model.ExportProfile;
import com.frankstiwrx.imageresizer.service.ProfileService;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import javax.swing.ImageIcon;
import javax.swing.SwingConstants;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JScrollPane;
import javax.swing.JComboBox;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.Locale;

public class MainFrame extends JFrame {

    private JButton btnSelectImage;
    private JButton btnResize;
    private JButton btnVariants;
    private JButton btnBatch;

    private JButton btnProfiles;

    private JTextField txtWidth;
    private JTextField txtHeight;

    private JTextField txtScale;
    private JButton btnApplyScale;

    private JLabel lblFileName;
    private JLabel lblOriginalSize;

    private JLabel lblPreview;

    private File selectedFile;
    private BufferedImage originalImage;

    public MainFrame() {

        initializeWindow();
        initializeComponents();

    }

    private void initializeWindow() {

        setTitle("ImageResizer");

        setSize(1000, 600);

        setMinimumSize(new Dimension(900, 550));
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

        // PAINEL DE PRÉ-VISUALIZAÇÃO
        JPanel previewPanel = new JPanel(new BorderLayout());

        previewPanel.setPreferredSize(
                new Dimension(260, 260)
        );

        previewPanel.setBorder(
                BorderFactory.createTitledBorder(
                        "Pré-visualização"
                )
        );

        lblPreview = new JLabel(
                "Nenhuma imagem",
                SwingConstants.CENTER
        );

        previewPanel.add(
                lblPreview,
                BorderLayout.CENTER
        );

        add(previewPanel, BorderLayout.EAST);

        // BOTÕES
        JPanel buttonsPanel = new JPanel(new FlowLayout());

        buttonsPanel.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 30, 20)
        );

        btnSelectImage = new JButton("Selecionar imagem");

        btnResize = new JButton("Redimensionar");

        btnVariants = new JButton("Gerar variações");

        btnBatch = new JButton("Processar pasta");

        btnProfiles = new JButton("Gerenciar perfis");

        buttonsPanel.add(btnSelectImage);

        buttonsPanel.add(btnResize);

        buttonsPanel.add(btnBatch);

        buttonsPanel.add(btnVariants);

        buttonsPanel.add(btnProfiles);

        add(buttonsPanel, BorderLayout.SOUTH);

// EVENTOS
        btnSelectImage.addActionListener(e -> selectImage());

        btnResize.addActionListener(e -> resizeSelectedImage());

        btnApplyScale.addActionListener(e -> applyScale());

        txtScale.addActionListener(e -> applyScale());

        btnBatch.addActionListener(e -> processBatch());

        btnVariants.addActionListener(e -> generateVariants());

        btnProfiles.addActionListener(e -> manageProfiles());

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
                updatePreview();

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
        SwingWorker<BatchProcessor.BatchResult, Void> worker
                = new SwingWorker<>() {

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

    private void manageProfiles() {

        try {

            // Carrega os perfis disponíveis
            List<ExportProfile> profiles
                    = ProfileService.listProfiles();

            // Perfis nativos da aplicação
            Set<String> defaultProfiles = Set.of(
                    "emblemas - 6 variacoes",
                    "emblemas - apenas r",
                    "emblemas - apenas f",
                    "icones",
                    "logos web",
                    "android - icones"
            );

            // Lista de perfis personalizados
            List<String> options = new ArrayList<>();

            options.add("+ Criar novo perfil");

            for (ExportProfile profile : profiles) {

                if (!defaultProfiles.contains(
                        profile.name().toLowerCase(Locale.ROOT))) {

                    options.add(profile.name());
                }
            }

            // Seleção do perfil
            String selectedOption = (String) JOptionPane.showInputDialog(
                    this,
                    "O que deseja fazer?",
                    "Gerenciar perfis",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options.toArray(),
                    options.get(0)
            );

            if (selectedOption == null) {
                return;
            }

// VERIFICA SE O USUÁRIO SELECIONOU UM PERFIL EXISTENTE
            if (!selectedOption.equals("+ Criar novo perfil")) {

                String[] actions = {
                    "Editar",
                    "Excluir",
                    "Cancelar"
                };

                int action = JOptionPane.showOptionDialog(
                        this,
                        "O que deseja fazer com o perfil:\n\n"
                        + selectedOption,
                        "Gerenciar perfil",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        actions,
                        actions[0]
                );

                // EDITAR PERFIL
                if (action == 0) {

                    // Continua normalmente para o editor visual
                } // EXCLUIR PERFIL
                else if (action == 1) {

                    int confirmation = JOptionPane.showConfirmDialog(
                            this,
                            "Tem certeza que deseja excluir o perfil?\n\n"
                            + selectedOption
                            + "\n\nEssa ação não poderá ser desfeita.",
                            "Confirmar exclusão",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE
                    );

                    if (confirmation == JOptionPane.YES_OPTION) {

                        try {

                            ProfileService.deleteProfile(selectedOption);

                            JOptionPane.showMessageDialog(
                                    this,
                                    "Perfil excluído com sucesso!",
                                    "Exclusão concluída",
                                    JOptionPane.INFORMATION_MESSAGE
                            );

                        } catch (IOException | IllegalArgumentException ex) {

                            JOptionPane.showMessageDialog(
                                    this,
                                    "Não foi possível excluir o perfil:\n"
                                    + ex.getMessage(),
                                    "Erro",
                                    JOptionPane.ERROR_MESSAGE
                            );
                        }
                    }

                    return;
                } // CANCELAR
                else {

                    return;
                }
            }

            String profileName;

            ExportProfile existingProfile = null;

            // Criar novo perfil
            if (selectedOption.equals("+ Criar novo perfil")) {

                profileName = JOptionPane.showInputDialog(
                        this,
                        "Informe o nome do novo perfil:",
                        "Novo perfil",
                        JOptionPane.QUESTION_MESSAGE
                );

                if (profileName == null
                        || profileName.isBlank()) {
                    return;
                }

                profileName = profileName.trim();

                // Impede nomes iguais aos perfis existentes
                for (ExportProfile profile : profiles) {

                    if (profile.name().equalsIgnoreCase(profileName)) {

                        JOptionPane.showMessageDialog(
                                this,
                                "Já existe um perfil com esse nome."
                        );

                        return;
                    }
                }

            } else {

                profileName = selectedOption;

                for (ExportProfile profile : profiles) {

                    if (profile.name().equals(profileName)) {

                        existingProfile = profile;

                        break;
                    }
                }
            }

            // MODELO DA TABELA
            String[] columns = {
                "Nome do arquivo",
                "Largura (px)",
                "Altura (px)"
            };

            DefaultTableModel tableModel
                    = new DefaultTableModel(columns, 0) {

                @Override
                public boolean isCellEditable(int row, int column) {
                    return true;
                }
            };

            // Carrega as variações existentes
            if (existingProfile != null) {

                for (ExportProfile.Variant variant
                        : existingProfile.variants()) {

                    tableModel.addRow(new Object[]{
                        variant.template(),
                        variant.width(),
                        variant.height()
                    });
                }

            } else {

                // Variações iniciais para novos perfis
                tableModel.addRow(new Object[]{
                    "{name}_128.png", 128, 128
                });

                tableModel.addRow(new Object[]{
                    "{name}_256.png", 256, 256
                });

                tableModel.addRow(new Object[]{
                    "{name}_512.png", 512, 512
                });
            }

            // CRIAÇÃO DA TABELA
            JTable table = new JTable(tableModel);

            table.setRowHeight(30);

            table.setFillsViewportHeight(true);

            table.getTableHeader().setReorderingAllowed(false);

            table.setSelectionMode(
                    javax.swing.ListSelectionModel.SINGLE_SELECTION
            );

            table.getColumnModel()
                    .getColumn(0)
                    .setPreferredWidth(270);

            table.getColumnModel()
                    .getColumn(1)
                    .setPreferredWidth(100);

            table.getColumnModel()
                    .getColumn(2)
                    .setPreferredWidth(100);

            JScrollPane scrollPane = new JScrollPane(table);

            scrollPane.setPreferredSize(
                    new Dimension(550, 260)
            );

            // BOTÕES
            JButton btnAdd = new JButton("+ Adicionar variação");

            JButton btnRemove = new JButton("- Remover selecionada");

            btnAdd.addActionListener(e -> {

                if (tableModel.getRowCount() >= 100) {

                    JOptionPane.showMessageDialog(
                            this,
                            "O limite é de 100 variações por perfil."
                    );

                    return;
                }

                tableModel.addRow(new Object[]{
                    "{name}_nova.png", 256, 256
                });

                int lastRow = tableModel.getRowCount() - 1;

                table.setRowSelectionInterval(lastRow, lastRow);

                table.scrollRectToVisible(
                        table.getCellRect(lastRow, 0, true)
                );
            });

            btnRemove.addActionListener(e -> {

                int selectedRow = table.getSelectedRow();

                if (selectedRow == -1) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Selecione uma variação para remover."
                    );

                    return;
                }

                tableModel.removeRow(selectedRow);
            });

            JPanel buttonPanel = new JPanel(
                    new FlowLayout(FlowLayout.LEFT)
            );

            buttonPanel.add(btnAdd);
            buttonPanel.add(btnRemove);

            // INSTRUÇÕES
            JLabel instructions = new JLabel(
                    "<html>"
                    + "<b>Variáveis disponíveis:</b><br>"
                    + "{name} = Nome da imagem original<br>"
                    + "{id} = Identificador numérico<br><br>"
                    + "Clique duas vezes em uma célula para editar."
                    + "</html>"
            );

            instructions.setBorder(
                    BorderFactory.createEmptyBorder(10, 5, 10, 5)
            );

            // PAINEL PRINCIPAL
            JPanel editorPanel = new JPanel(new BorderLayout(10, 10));

            editorPanel.add(
                    scrollPane,
                    BorderLayout.CENTER
            );

            editorPanel.add(
                    buttonPanel,
                    BorderLayout.NORTH
            );

            editorPanel.add(
                    instructions,
                    BorderLayout.SOUTH
            );

            // JANELA DE EDIÇÃO
            int result = JOptionPane.showConfirmDialog(
                    this,
                    editorPanel,
                    "Editar perfil: " + profileName,
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            // Finaliza qualquer edição ainda aberta
            if (table.isEditing()) {
                if (!table.getCellEditor().stopCellEditing()) {
                    return;
                }
            }

            // VALIDAÇÃO E SALVAMENTO
            List<ExportProfile.Variant> variants
                    = new ArrayList<>();

            try {

                if (tableModel.getRowCount() == 0) {

                    throw new IllegalArgumentException(
                            "Adicione pelo menos uma variação."
                    );
                }

                Set<String> usedNames = new HashSet<>();

                for (int i = 0; i < tableModel.getRowCount(); i++) {

                    String template = String.valueOf(
                            tableModel.getValueAt(i, 0)
                    ).trim();

                    int width = Integer.parseInt(
                            String.valueOf(
                                    tableModel.getValueAt(i, 1)
                            ).trim()
                    );

                    int height = Integer.parseInt(
                            String.valueOf(
                                    tableModel.getValueAt(i, 2)
                            ).trim()
                    );

                    // Validação do nome
                    if (template.isBlank()
                            || !template.toLowerCase(Locale.ROOT)
                                    .endsWith(".png")
                            || template.contains("/")
                            || template.contains("\\")
                            || template.contains("..")
                            || template.matches(".*[<>:\"|?*].*")) {

                        throw new IllegalArgumentException(
                                "Nome inválido na linha " + (i + 1)
                        );
                    }

                    if (!usedNames.add(
                            template.toLowerCase(Locale.ROOT))) {

                        throw new IllegalArgumentException(
                                "Nome duplicado na linha " + (i + 1)
                        );
                    }

                    variants.add(
                            new ExportProfile.Variant(
                                    template,
                                    width,
                                    height
                            )
                    );
                }

                ExportProfile profile = new ExportProfile(
                        profileName,
                        variants
                );

                ProfileService.saveProfile(profile);

                JOptionPane.showMessageDialog(
                        this,
                        "Perfil salvo com sucesso!\n\n"
                        + "Nome: " + profile.name()
                        + "\nVariações: " + variants.size()
                );

            } catch (IOException | IllegalArgumentException ex) {

                JOptionPane.showMessageDialog(
                        this,
                        "Não foi possível salvar o perfil:\n"
                        + ex.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (IOException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao carregar perfis:\n"
                    + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void generateVariants() {

        if (selectedFile == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecione uma imagem primeiro."
            );

            return;
        }

        try {

            List<ExportProfile> profiles
                    = ProfileService.listProfiles();

            ExportProfile selectedProfile
                    = (ExportProfile) JOptionPane.showInputDialog(
                            this,
                            "Selecione o perfil de exportação:",
                            "Gerar variações",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            profiles.toArray(),
                            profiles.get(0)
                    );

            if (selectedProfile == null) {
                return;
            }

            int parsedId = 0;

            boolean requiresId = selectedProfile.variants()
                    .stream()
                    .anyMatch(variant
                            -> variant.template().contains("{id}")
                    );

            if (requiresId) {

                String input = JOptionPane.showInputDialog(
                        this,
                        "Informe o identificador numérico:"
                );

                if (input == null) {
                    return;
                }

                try {

                    parsedId = Integer.parseInt(input.trim());

                    if (parsedId < 0 || parsedId > 999999) {
                        throw new NumberFormatException();
                    }

                } catch (NumberFormatException ex) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Informe um ID entre 0 e 999999."
                    );

                    return;
                }
            }

            final int imageId = parsedId;

            JFileChooser chooser = new JFileChooser();

            chooser.setDialogTitle(
                    "Selecione a pasta de destino"
            );

            chooser.setFileSelectionMode(
                    JFileChooser.DIRECTORIES_ONLY
            );

            chooser.setCurrentDirectory(
                    selectedFile.getParentFile()
            );

            int result = chooser.showOpenDialog(this);

            if (result != JFileChooser.APPROVE_OPTION) {
                return;
            }

            Path outputFolder
                    = chooser.getSelectedFile().toPath();

            btnVariants.setEnabled(false);

            SwingWorker<List<Path>, Void> worker
                    = new SwingWorker<>() {

                @Override
                protected List<Path> doInBackground()
                        throws Exception {

                    return ImageVariantGenerator.generate(
                            selectedFile,
                            outputFolder,
                            imageId,
                            selectedProfile
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
                                + "Perfil: "
                                + selectedProfile.name()
                                + "\nArquivos: "
                                + generated.size()
                                + "\nDestino:\n"
                                + outputFolder
                        );

                    } catch (Exception ex) {

                        Throwable cause = ex.getCause();

                        JOptionPane.showMessageDialog(
                                MainFrame.this,
                                "Erro ao gerar variações:\n"
                                + (cause != null
                                        ? cause.getMessage()
                                        : ex.getMessage()),
                                "Erro",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            };

            worker.execute();

        } catch (IOException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void updatePreview() {

        if (originalImage == null) {

            lblPreview.setIcon(null);

            lblPreview.setText("Nenhuma imagem");

            return;
        }

        BufferedImage preview
                = ImageProcessor.resizeToFit(
                        originalImage,
                        220,
                        220
                );

        lblPreview.setIcon(
                new ImageIcon(preview)
        );

        lblPreview.setText("");
    }

}
