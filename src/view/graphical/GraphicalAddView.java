package view.graphical;

import controller.TaskController;
import model.Task;

import javax.swing.*;
import java.awt.*;

public class GraphicalAddView {
    private TaskController controller;
    private JFrame parentFrame;

    public GraphicalAddView(TaskController controller, JFrame parentFrame) {
        this.controller = controller;
        this.parentFrame = parentFrame;
    }

    public void show() {
        JDialog dialog = new JDialog(parentFrame, "Dodaj nowe zadanie", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new GridLayout(6, 2, 10, 10));
        dialog.setLocationRelativeTo(parentFrame);

        // Pola formularza
        JTextField nameField = new JTextField();
        JTextField categoryField = new JTextField();
        JTextField deadlineField = new JTextField();
        JComboBox<String> priorityComboBox = new JComboBox<>(new String[]{"*bez priorytetu*", "bardzo ważne", "ważne", "normalne", "bez pośpiechu"});

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
        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String category = categoryField.getText().trim();
            String deadline = deadlineField.getText().trim();
            String priority = (String) priorityComboBox.getSelectedItem();

            // Walidacja: nazwa i kategoria <= 20 znaków
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Nazwa zadania jest wymagana!", "Błąd", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (name.length() > 20) {
                JOptionPane.showMessageDialog(dialog, "Nazwa zadania nie może przekraczać 20 znaków!", "Błąd", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (category.length() > 20) {
                JOptionPane.showMessageDialog(dialog, "Kategoria nie może przekraczać 20 znaków!", "Błąd", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Walidacja formatu daty
            if (!deadline.isEmpty() && !deadline.matches("\\d{4}-\\d{2}-\\d{2}")) {
                JOptionPane.showMessageDialog(dialog, "Niepoprawny format daty. Użyj formatu YYYY-MM-DD!", "Błąd", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Dodanie zadania
            Task newTask = new Task(controller.getNextId(), name, category, deadline, priority);
            controller.addTask(newTask);
            JOptionPane.showMessageDialog(dialog, "Zadanie dodano pomyślnie!");
            dialog.dispose();
        });

        JButton cancelButton = new JButton("Anuluj");
        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(saveButton);
        dialog.add(cancelButton);

        dialog.setVisible(true);
    }
}