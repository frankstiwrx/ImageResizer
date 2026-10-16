package com.frankstiwrx.imageresizer.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class MainFrame extends JFrame {

    private JButton btnSelectImage;
    private JButton btnResize;
    private JButton btnVariants;

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

        // Painel superior

        JPanel headerPanel = new JPanel();

        headerPanel.setBorder(
            BorderFactory.createEmptyBorder(25, 20, 20, 20)
        );

        JLabel title = new JLabel("ImageResizer");

        title.setFont(new Font("Arial", Font.BOLD, 28));

        headerPanel.add(title);

        add(headerPanel, BorderLayout.NORTH);


        // Painel central

        JPanel centerPanel = new JPanel();

        JLabel description = new JLabel(
            "Redimensione imagens e gere múltiplas variações."
        );

        description.setFont(
            new Font("Arial", Font.PLAIN, 15)
        );

        centerPanel.add(description);

        add(centerPanel, BorderLayout.CENTER);


        // Painel inferior

        JPanel buttonsPanel = new JPanel();

        buttonsPanel.setBorder(
            BorderFactory.createEmptyBorder(20, 20, 30, 20)
        );

        btnSelectImage = new JButton("Selecionar imagem");

        btnResize = new JButton("Redimensionar");

        btnVariants = new JButton("Gerar variações");

        buttonsPanel.add(btnSelectImage);

        buttonsPanel.add(btnResize);

        buttonsPanel.add(btnVariants);

        add(buttonsPanel, BorderLayout.SOUTH);


        // Eventos

        btnSelectImage.addActionListener(e -> {

            System.out.println("Selecionar imagem");

        });

        btnResize.addActionListener(e -> {

            System.out.println("Redimensionar imagem");

        });

        btnVariants.addActionListener(e -> {

            System.out.println("Gerar variações");

        });

    }
}