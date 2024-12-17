package view;

import controller.TaskController;

import javax.swing.*;
import java.awt.*;

public class GraphicalView {
    private TaskController controller;
    private JFrame frame;

    public GraphicalView(TaskController controller) {
        this.controller = controller;
        this.frame = new JFrame("OrganizeriumApp - Tryb Graficzny");
    }

    public void show() {
        // Konfiguracja głównego okna
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Pasek menu
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Opcje");
        JMenuItem switchToTextMode = new JMenuItem("Przełącz na tryb tekstowy");
        menu.add(switchToTextMode);
        menuBar.add(menu);
        frame.setJMenuBar(menuBar);

        // Nagłówek
        JLabel headerLabel = new JLabel("Organizerium - Menu Główne", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        frame.add(headerLabel, BorderLayout.NORTH);

        // Główna sekcja
        JPanel mainPanel = new JPanel(new GridLayout(2, 1));
        JButton listTasksButton = new JButton("Wyświetl listę zadań");
        JButton addTaskButton = new JButton("Dodaj nowe zadanie");

        mainPanel.add(listTasksButton);
        mainPanel.add(addTaskButton);
        frame.add(mainPanel, BorderLayout.CENTER);

        // Akcje przycisków
        listTasksButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Lista zadań:\n" + controller.getTasks()));
        addTaskButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Funkcja dodawania zadania w budowie."));

        // Przełączanie na tryb tekstowy
        switchToTextMode.addActionListener(e -> {
            frame.dispose(); // Zamknij okno graficzne

            // Uruchomienie Lanterna w nowym wątku
            new Thread(() -> {
                new MainMenuView(controller).show();
            }).start();
        });

        // Wyświetlenie okna
        frame.setVisible(true);
    }
}