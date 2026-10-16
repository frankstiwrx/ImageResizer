package com.frankstiwrx.imageresizer;

import com.frankstiwrx.imageresizer.ui.MainFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName()
            );
        } catch (Exception e) {
            System.err.println(
                "Não foi possível aplicar o tema do sistema."
            );
        }

        SwingUtilities.invokeLater(() -> {

            MainFrame frame = new MainFrame();

            frame.setVisible(true);

        });
    }
}