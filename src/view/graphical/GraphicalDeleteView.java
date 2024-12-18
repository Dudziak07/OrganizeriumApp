package view.graphical;

import controller.TaskController;
import model.Task;

import javax.swing.*;
import java.awt.*;

public class GraphicalDeleteView {
    private final TaskController controller;
    private final JFrame parentFrame;

    public GraphicalDeleteView(TaskController controller, JFrame parentFrame) {
        this.controller = controller;
        this.parentFrame = parentFrame;
    }

    public void show() {
        JDialog dialog = new JDialog(parentFrame, "Usuń zadanie", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new GridLayout(5, 1, 10, 10));
        dialog.setLocationRelativeTo(parentFrame);

        // Pola i przyciski
        JTextField idOrNameField = new JTextField();
        JButton deleteButton = new JButton("Usuń zadanie");
        JButton deleteAllButton = new JButton("Usuń wszystkie zadania");
        JButton cancelButton = new JButton("Anuluj");

        // Dodanie komponentów
        dialog.add(new JLabel("ID lub nazwa zadania:"));
        dialog.add(idOrNameField);
        dialog.add(deleteButton);
        dialog.add(deleteAllButton);
        dialog.add(cancelButton);

        // Obsługa usuwania pojedynczego zadania
        deleteButton.addActionListener(e -> {
            String input = idOrNameField.getText().trim();
            Task task = null;

            if (!input.isEmpty()) {
                try {
                    int id = Integer.parseInt(input);
                    task = controller.getTasks().stream().filter(t -> t.getId() == id).findFirst().orElse(null);
                } catch (NumberFormatException ex) {
                    task = controller.getTasks().stream().filter(t -> t.getName().equalsIgnoreCase(input)).findFirst().orElse(null);
                }
            }

            if (task != null) {
                String taskDetails = String.format(
                        "ID: %d\nNazwa: %s\nKategoria: %s\nPriorytet: %s\nTermin: %s\nData utworzenia: %s",
                        task.getId(), task.getName(), task.getCategory(), task.getPriority(),
                        task.getDeadline(), task.getCreationTime()
                );

                int confirmation = JOptionPane.showConfirmDialog(
                        dialog,
                        "Czy na pewno chcesz usunąć to zadanie?\n\n" + taskDetails,
                        "Potwierdzenie",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirmation == JOptionPane.YES_OPTION) {
                    boolean success = controller.removeTaskById(task.getId());
                    if (success) {
                        JOptionPane.showMessageDialog(dialog, "Zadanie zostało usunięte pomyślnie!");
                        dialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Nie udało się usunąć zadania!", "Błąd", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "Nie znaleziono zadania!", "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteAllButton.addActionListener(e -> {
            int confirmation = JOptionPane.showConfirmDialog(
                    dialog,
                    "Czy na pewno chcesz usunąć wszystkie zadania?",
                    "Potwierdzenie",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirmation == JOptionPane.YES_OPTION) {
                controller.removeAllTasks();
                JOptionPane.showMessageDialog(dialog, "Wszystkie zadania zostały usunięte!");
                dialog.dispose();
            }
        });

        // Obsługa anulowania
        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }
}