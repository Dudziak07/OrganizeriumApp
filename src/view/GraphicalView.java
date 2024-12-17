package view;

import com.formdev.flatlaf.FlatDarkLaf;
import controller.Logger;
import controller.TaskController;
import model.Task;

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
                Logger.log("Zamknięcie aplikacji", "Aplikacja została zamknięta z trybu graficznego");
                frame.dispose();
                System.exit(0);
            }
        });

        // Pasek menu z opcją przełączania na tryb tekstowy
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Opcje");
        JMenuItem switchToTextMode = new JMenuItem("Przełącz na tryb tekstowy");
        switchToTextMode.addActionListener(e -> {
            Logger.log("Przełączanie trybu", "Przełączono na tryb tekstowy");
            frame.dispose();
            new Thread(() -> {
                new MainMenuView(controller).show();
            }).start();
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
        JButton exitButton = new JButton("Zakończ aplikację");

        // Dodanie akcji przyciskom
        addTaskButton.addActionListener(e -> showAddTaskForm(frame));
        showTasksButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Lista zadań:\n" + controller.getTasks()));
        editTaskButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Funkcja edycji zadania"));
        deleteTaskButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Funkcja usuwania zadania"));
        exitButton.addActionListener(e -> {
            Logger.log("Zamknięcie aplikacji", "Aplikacja została zamknięta przez użytkownika");
            frame.dispose();
            System.exit(0);
        });

        // Dodanie przycisków do panelu
        panel.add(addTaskButton);
        panel.add(showTasksButton);
        panel.add(editTaskButton);
        panel.add(deleteTaskButton);
        panel.add(exitButton);

        // Dodanie panelu do okna
        frame.add(panel, BorderLayout.CENTER);

        // Wyświetlenie okna
        frame.setVisible(true);
    }

    // Metoda wyświetlająca formularz dodawania zadania
    private void showAddTaskForm(JFrame parentFrame) {
        // Okno dialogowe
        JDialog dialog = new JDialog(parentFrame, "Dodaj nowe zadanie", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new GridLayout(6, 2, 10, 10));
        dialog.setLocationRelativeTo(parentFrame);

        // Pola formularza
        JTextField nameField = new JTextField();
        JTextField categoryField = new JTextField();
        JTextField deadlineField = new JTextField();

        // Lista rozwijana dla priorytetu
        String[] priorities = {"*bez priorytetu*", "bardzo ważne", "ważne", "normalne", "bez pośpiechu"};
        JComboBox<String> priorityComboBox = new JComboBox<>(priorities);

        // Komponenty formularza
        dialog.add(new JLabel("Nazwa zadania*:"));
        dialog.add(nameField);
        dialog.add(new JLabel("Kategoria:"));
        dialog.add(categoryField);
        dialog.add(new JLabel("Termin (YYYY-MM-DD):"));
        dialog.add(deadlineField);
        dialog.add(new JLabel("Priorytet:"));
        dialog.add(priorityComboBox);

        // Przycisk Zapisz
        JButton saveButton = new JButton("Zapisz");
        JButton cancelButton = new JButton("Anuluj");

        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String category = categoryField.getText().trim();
            String deadline = deadlineField.getText().trim();
            String priority = (String) priorityComboBox.getSelectedItem();

            // Walidacja nazwy zadania
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Nazwa zadania jest wymagana!", "Błąd", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Dodanie zadania
            Task newTask = new Task(TaskController.getInstance().getNextId(), name, category, deadline, priority);
            TaskController.getInstance().addTask(newTask);
            JOptionPane.showMessageDialog(dialog, "Zadanie zostało dodane pomyślnie!");
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        // Dodanie przycisków
        dialog.add(saveButton);
        dialog.add(cancelButton);

        dialog.setVisible(true);
    }
}
