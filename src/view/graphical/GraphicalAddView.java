package view.graphical;

import controller.ImageController;
import controller.TaskController;
import controller.TaskValidator;
import model.Task;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class GraphicalAddView {
    private final TaskController controller;
    private final JFrame parentFrame;

    public GraphicalAddView(TaskController controller, JFrame parentFrame) {
        this.controller = controller;
        this.parentFrame = parentFrame;
    }

    public void show() {
        JDialog dialog = new JDialog(parentFrame, "Dodaj nowe zadanie", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(parentFrame);

        // Pola formularza
        JTextField nameField = new JTextField();
        JTextField categoryField = new JTextField();
        JTextField deadlineField = new JTextField();
        JComboBox<String> priorityComboBox = new JComboBox<>(new String[]{
                "*bez priorytetu*", "bardzo ważne", "ważne", "normalne", "bez pośpiechu"
        });

        // Panel z formularzem
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.add(new JLabel("Nazwa zadania*:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Kategoria:"));
        formPanel.add(categoryField);
        formPanel.add(new JLabel("Termin (YYYY-MM-DD):"));
        formPanel.add(deadlineField);
        formPanel.add(new JLabel("Priorytet:"));
        formPanel.add(priorityComboBox);

        dialog.add(formPanel, BorderLayout.CENTER);

        // Przycisk Zapisz
        JButton saveButton = new JButton("Zapisz");
        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String category = categoryField.getText().trim();
            String deadline = deadlineField.getText().trim();
            String priority = (String) priorityComboBox.getSelectedItem();

            // Walidacja danych
            if (!TaskValidator.validateName(name)) {
                JOptionPane.showMessageDialog(
                        dialog,
                        "Nazwa zadania jest wymagana i nie może przekraczać 20 znaków!",
                        "Błąd",
                        JOptionPane.ERROR_MESSAGE,
                        ImageController.resizeIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/error_icon.png"))), 50, 50)
                );
                return;
            }

            if (!TaskValidator.validateCategory(category)) {
                JOptionPane.showMessageDialog(
                        dialog,
                        "Kategoria nie może przekraczać 20 znaków!",
                        "Błąd",
                        JOptionPane.ERROR_MESSAGE,
                        ImageController.resizeIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/error_icon.png"))), 50, 50)
                );
                return;
            }

            if (!TaskValidator.validateDateFormat(deadline)) {
                JOptionPane.showMessageDialog(
                        dialog,
                        "Niepoprawny format daty. Użyj formatu YYYY-MM-DD!",
                        "Błąd",
                        JOptionPane.ERROR_MESSAGE,
                        ImageController.resizeIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/error_icon.png"))), 50, 50)
                );
                return;
            }

            // Dodanie zadania
            Task newTask = new Task(controller.getNextId(), name, category, deadline, priority);
            controller.addTask(newTask);
            JOptionPane.showMessageDialog(
                    dialog,
                    "Zadanie dodano pomyślnie!",
                    "Sukces",
                    JOptionPane.INFORMATION_MESSAGE,
                    ImageController.resizeIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/info_icon.png"))), 50, 50)
            );
            dialog.dispose();
        });

        // Przycisk Anuluj
        JButton cancelButton = new JButton("Anuluj");
        cancelButton.addActionListener(e -> dialog.dispose());

        // Panel z przyciskami
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}