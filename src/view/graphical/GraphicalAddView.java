package view.graphical;

import controller.TaskController;
import model.Task;
import view.shared.TaskForm;

import javax.swing.*;

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
        dialog.setLocationRelativeTo(parentFrame);

        // Pola formularza
        JTextField nameField = new JTextField();
        JTextField categoryField = new JTextField();
        JTextField deadlineField = new JTextField();
        JComboBox<String> priorityComboBox = new JComboBox<>(new String[]{
                "*bez priorytetu*", "bardzo ważne", "ważne", "normalne", "bez pośpiechu"
        });

        // Dodanie formularza do okna
        JPanel formPanel = TaskForm.createTaskForm(nameField, categoryField, deadlineField, priorityComboBox);
        dialog.add(formPanel);

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
            if (name.length() > 20 || category.length() > 20) {
                JOptionPane.showMessageDialog(dialog, "Nazwa i kategoria nie mogą przekraczać 20 znaków!", "Błąd", JOptionPane.ERROR_MESSAGE);
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

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(buttonPanel, "South");
        dialog.setVisible(true);
    }
}