package view.graphical;

import controller.TaskController;
import model.Task;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GraphicalEditView {
    private TaskController controller;
    private JFrame parentFrame;

    public GraphicalEditView(TaskController controller, JFrame parentFrame) {
        this.controller = controller;
        this.parentFrame = parentFrame;
    }

    public void show() {
        JDialog dialog = new JDialog(parentFrame, "Edytuj zadanie", true);
        dialog.setSize(400, 350);
        dialog.setLayout(new GridLayout(7, 2, 10, 10));
        dialog.setLocationRelativeTo(parentFrame);

        // Pola wyboru zadania
        JTextField idOrNameField = new JTextField();
        JButton searchButton = new JButton("Szukaj");

        // Pola do edycji zadania
        JTextField nameField = new JTextField();
        JTextField categoryField = new JTextField();
        JTextField deadlineField = new JTextField();
        JComboBox<String> priorityComboBox = new JComboBox<>(new String[]{"*bez priorytetu*", "bardzo ważne", "ważne", "normalne", "bez pośpiechu"});

        // Dodanie komponentów
        dialog.add(new JLabel("ID lub nazwa zadania:"));
        dialog.add(idOrNameField);
        dialog.add(new JLabel(""));
        dialog.add(searchButton);

        dialog.add(new JLabel("Nowa nazwa*:"));
        dialog.add(nameField);
        dialog.add(new JLabel("Nowa kategoria:"));
        dialog.add(categoryField);
        dialog.add(new JLabel("Nowy termin (YYYY-MM-DD):"));
        dialog.add(deadlineField);
        dialog.add(new JLabel("Nowy priorytet:"));
        dialog.add(priorityComboBox);

        JButton saveButton = new JButton("Zapisz");
        JButton cancelButton = new JButton("Anuluj");

        searchButton.addActionListener(e -> {
            String input = idOrNameField.getText().trim();
            Task task = null;

            try {
                int id = Integer.parseInt(input); // Wyszukiwanie po ID
                task = controller.getTasks().stream().filter(t -> t.getId() == id).findFirst().orElse(null);
            } catch (NumberFormatException ex) {
                // Wyszukiwanie po nazwie
                task = controller.getTasks().stream().filter(t -> t.getName().equalsIgnoreCase(input)).findFirst().orElse(null);
            }

            if (task != null) {
                // Automatyczne uzupełnianie pól formularza
                nameField.setText(task.getName());
                categoryField.setText(task.getCategory());
                deadlineField.setText(task.getDeadline());
                priorityComboBox.setSelectedItem(task.getPriority());
            } else {
                JOptionPane.showMessageDialog(dialog, "Nie znaleziono zadania!", "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        });

        saveButton.addActionListener(e -> {
            String input = idOrNameField.getText().trim();
            boolean success = false;

            try {
                int id = Integer.parseInt(input);
                success = controller.editTaskById(
                        id,
                        nameField.getText().trim(),
                        categoryField.getText().trim(),
                        deadlineField.getText().trim(),
                        (String) priorityComboBox.getSelectedItem()
                );
            } catch (NumberFormatException ex) {
                success = controller.editTaskByName(
                        input,
                        nameField.getText().trim(),
                        categoryField.getText().trim(),
                        deadlineField.getText().trim(),
                        (String) priorityComboBox.getSelectedItem()
                );
            }

            if (success) {
                JOptionPane.showMessageDialog(dialog, "Zadanie zostało zaktualizowane pomyślnie!");
                dialog.dispose(); // Zamknięcie okna po poprawnym zapisie
            } else {
                JOptionPane.showMessageDialog(dialog, "Nie udało się zaktualizować zadania!", "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(saveButton);
        dialog.add(cancelButton);

        dialog.setVisible(true);
    }
}