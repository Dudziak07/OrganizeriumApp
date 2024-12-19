package view.graphical;

import com.formdev.flatlaf.FlatDarkLaf;
import controller.Logger;
import controller.TaskController;
import view.textual.MainMenuView;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class GraphicalMenuView {
    private final TaskController controller;
    private final JFrame frame;
    private static boolean isInitialized = false; // Flaga do kontrolowania logowania

    public GraphicalMenuView(TaskController controller) {
        this.controller = controller;

        // Ustawienie trybu ciemnego
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (UnsupportedLookAndFeelException e) {
            Logger.log("Błąd motywu", "Nie udało się załadować motywu FlatDarkLaf");
            e.printStackTrace();
        }

        this.frame = new JFrame("OrganizeriumApp - Menu Główne");

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
                showExitConfirmation();
            }
        });

        // Pasek menu z opcją przełączania na tryb tekstowy
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Opcje");
        JMenuItem switchToTextMode = new JMenuItem("Przełącz na tryb tekstowy");
        switchToTextMode.addActionListener(e -> {
            Logger.log("Przełączanie trybu", "Przełączono na tryb tekstowy");
            frame.dispose();
            new Thread(() -> new MainMenuView(controller).show()).start();
        });
        menu.add(switchToTextMode);
        menuBar.add(menu);
        frame.setJMenuBar(menuBar);

        // Główna sekcja menu
        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // Dodanie przycisków
        JButton addTaskButton = new JButton("Dodaj zadanie");
        JButton showTasksButton = new JButton("Wyświetl zadania");
        JButton editTaskButton = new JButton("Edytuj zadanie");
        JButton deleteTaskButton = new JButton("Usuń zadanie");

        // Dodanie akcji przyciskom
        addTaskButton.addActionListener(e -> new GraphicalAddView(controller, frame).show());
        showTasksButton.addActionListener(e -> new GraphicalListView(controller, frame).show());

        // Edytuj zadanie - pobiera ID od użytkownika
        editTaskButton.addActionListener(e -> {
            String taskIdInput = JOptionPane.showInputDialog(frame, "Podaj ID zadania do edycji:", "Edytuj zadanie", JOptionPane.QUESTION_MESSAGE);
            if (taskIdInput != null && !taskIdInput.trim().isEmpty()) {
                try {
                    int taskId = Integer.parseInt(taskIdInput.trim());
                    new GraphicalEditView(controller, frame).show(taskId);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Nieprawidłowy format ID!", "Błąd", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        deleteTaskButton.addActionListener(e -> new GraphicalDeleteView(controller, frame).show());

        // Dodanie przycisków do panelu
        panel.add(addTaskButton);
        panel.add(showTasksButton);
        panel.add(editTaskButton);
        panel.add(deleteTaskButton);

        // Dodanie przycisku wyłączania z ikoną w prawym dolnym rogu
        JButton exitButton = new JButton();
        try {
            ImageIcon originalIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/power_off.png")));
            Image scaledImage = originalIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            exitButton.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            Logger.log("Błąd ładowania ikony", "Nie udało się załadować ikony wyłączania");
        }
        exitButton.setToolTipText("Zamknij aplikację");
        exitButton.setPreferredSize(new Dimension(50, 50));
        exitButton.setFocusPainted(false);
        exitButton.setContentAreaFilled(false);
        exitButton.setBorderPainted(false);
        exitButton.addActionListener(e -> showExitConfirmation());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(exitButton);

        // Dodanie panelu do okna
        frame.add(panel, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        // Wyświetlenie okna
        frame.setVisible(true);
    }

    // Metoda wyświetlająca potwierdzenie wyjścia z aplikacji
    private void showExitConfirmation() {
        int confirmation = JOptionPane.showConfirmDialog(
                frame,
                "Czy na pewno chcesz zamknąć aplikację?",
                "Potwierdzenie zamknięcia",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmation == JOptionPane.YES_OPTION) {
            Logger.log("Zamknięcie aplikacji", "Aplikacja została zamknięta przez użytkownika");
            frame.dispose();
            System.exit(0);
        }
    }
}
