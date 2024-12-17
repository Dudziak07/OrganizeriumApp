package view;

import com.formdev.flatlaf.FlatDarkLaf;
import controller.Logger;
import controller.TaskController;

import javax.swing.*;
import java.awt.*;

public class GraphicalView {
    private TaskController controller;
    private JFrame frame;
    private static boolean isInitialized = false; // Flaga do kontrolowania logowania

    public GraphicalView(TaskController controller) {
        this.controller = controller;

        // Ustawienie trybu ciemnego
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (UnsupportedLookAndFeelException e) {
            Logger.log("Błąd motywu", "Nie udało się załadować motywu FlatDarkLaf");
            e.printStackTrace();
        }

        this.frame = new JFrame("OrganizeriumApp - Tryb Graficzny");

        // Logowanie tylko przy pierwszym uruchomieniu
        if (!isInitialized) {
            Logger.log("Uruchomienie aplikacji", "Zainicjowano tryb graficzny");
            isInitialized = true; // Ustawienie flagi
        }
    }

    public void show() {
        // Konfiguracja głównego okna
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                Logger.log("Zamknięcie aplikacji", "Aplikacja została zamknięta z trybu graficznego");
                frame.dispose();
                System.exit(0);
            }
        });

        // Pasek menu
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Opcje");
        JMenuItem switchToTextMode = new JMenuItem("Przełącz na tryb tekstowy");
        menu.add(switchToTextMode);
        menuBar.add(menu);
        frame.setJMenuBar(menuBar);

        // Główna sekcja GUI
        JLabel headerLabel = new JLabel("OrganizeriumApp - Zarządzaj Zadaniami", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        frame.add(headerLabel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new GridLayout(2, 1, 10, 10)); // Dodanie odstępów między elementami
        JButton listTasksButton = new JButton("Wyświetl listę zadań");
        JButton addTaskButton = new JButton("Dodaj nowe zadanie");

        mainPanel.add(listTasksButton);
        mainPanel.add(addTaskButton);
        frame.add(mainPanel, BorderLayout.CENTER);

        // Akcje
        listTasksButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Lista zadań:\n" + controller.getTasks()));
        addTaskButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Funkcja dodawania zadania w budowie."));
        switchToTextMode.addActionListener(e -> {
            Logger.log("Przełączanie trybu", "Przełączono na tryb tekstowy");
            frame.dispose();
            new Thread(() -> {
                new MainMenuView(controller).show();
            }).start();
        });

        // Wyświetlenie okna
        frame.setVisible(true);
    }
}