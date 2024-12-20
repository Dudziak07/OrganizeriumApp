package view.graphical;

import controller.TaskController;
import controller.ImageController; // Import do obsługi ikon
import model.Task;

import javax.swing.*;
import java.awt.*;

public class GraphicalEditView {
    private final TaskController controller;
    private final JFrame parentFrame;

    public GraphicalEditView(TaskController controller, JFrame parentFrame) {
        this.controller = controller;
        this.parentFrame = parentFrame;
    }

    public void show(int taskId) {
        // Pobranie zadania przez kontroler
        Task task = controller.getTaskById(taskId);
        if (task == null) {
            JOptionPane.showMessageDialog(
                    parentFrame,
                    "Nie znaleziono zadania o ID: " + taskId,
                    "Błąd",
                    JOptionPane.ERROR_MESSAGE,
                    ImageController.resizeIcon(new ImageIcon("resources/icons/error404_icon.png"), 50, 50)
            );
            return;
        }

        // Okno dialogowe
        JDialog dialog = new JDialog(parentFrame, "Edytuj zadanie", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(parentFrame);

        // Pola formularza
        JTextField nameField = new JTextField(task.getName());
        JTextField categoryField = new JTextField(task.getCategory());
        JTextField deadlineField = new JTextField(task.getDeadline());
        JComboBox<String> priorityBox = new JComboBox<>(new String[]{
                "*bez priorytetu*", "bardzo ważne", "ważne", "normalne", "bez pośpiechu"
        });
        priorityBox.setSelectedItem(task.getPriority());

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.add(new JLabel("Nazwa:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Kategoria:"));
        formPanel.add(categoryField);
        formPanel.add(new JLabel("Termin:"));
        formPanel.add(deadlineField);
        formPanel.add(new JLabel("Priorytet:"));
        formPanel.add(priorityBox);

        dialog.add(formPanel, BorderLayout.CENTER);

        // Przyciski
        JButton saveButton = new JButton("Zapisz");
        saveButton.addActionListener(e -> {
            boolean updated = controller.editTask(
                    taskId,
                    nameField.getText().trim(),
                    categoryField.getText().trim(),
                    deadlineField.getText().trim(),
                    (String) priorityBox.getSelectedItem()
            );

            if (updated) {
                JOptionPane.showMessageDialog(
                        dialog,
                        String.format("<html><b>Zadanie zaktualizowano pomyślnie!</b><br>" +
                                        "ID: %d<br>" +
                                        "Nazwa: %s<br>" +
                                        "Kategoria: %s<br>" +
                                        "Priorytet: %s<br>" +
                                        "Termin: %s</html>",
                                task.getId(),
                                nameField.getText().trim(),
                                categoryField.getText().trim(),
                                priorityBox.getSelectedItem(),
                                deadlineField.getText().trim()
                        ),
                        "Zadanie zaktualizowano",
                        JOptionPane.INFORMATION_MESSAGE,
                        ImageController.resizeIcon(new ImageIcon("resources/icons/info_icon.png"), 50, 50)
                );
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(
                        dialog,
                        "Nie udało się zaktualizować zadania.",
                        "Błąd",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        JButton cancelButton = new JButton("Anuluj");
        cancelButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}