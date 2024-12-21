package view.graphical;

import controller.TaskController;
import controller.ImageController; // Dodano kontroler do obsługi ikon
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
        UIManager.put("OptionPane.yesButtonText", "Tak");
        UIManager.put("OptionPane.noButtonText", "Nie");
        UIManager.put("OptionPane.cancelButtonText", "Anuluj");

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
            Task task = controller.getTaskByIdOrName(input);

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
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        ImageController.resizeIcon(new ImageIcon("resources/icons/error_icon.png"), 50, 50)
                );

                if (confirmation == JOptionPane.YES_OPTION) {
                    boolean success = controller.deleteTask(task);
                    if (success) {
                        JOptionPane.showMessageDialog(
                                dialog,
                                "<html><b>Zadanie zostało usunięte pomyślnie!</b><br>" + taskDetails + "</html>",
                                "Usunięto zadanie",
                                JOptionPane.INFORMATION_MESSAGE,
                                ImageController.resizeIcon(new ImageIcon("resources/icons/info_icon.png"), 50, 50)
                        );
                        dialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(
                                dialog,
                                "Nie udało się usunąć zadania!",
                                "Błąd",
                                JOptionPane.ERROR_MESSAGE,
                                ImageController.resizeIcon(new ImageIcon("resources/icons/error_icon.png"), 50, 50)
                        );
                    }
                }
            } else {
                JOptionPane.showMessageDialog(
                        dialog,
                        "Nie znaleziono zadania!",
                        "Błąd",
                        JOptionPane.ERROR_MESSAGE,
                        ImageController.resizeIcon(new ImageIcon("resources/icons/error404_icon.png"), 50, 50)
                );
            }
        });

        deleteAllButton.addActionListener(e -> {
            int confirmation = JOptionPane.showConfirmDialog(
                    dialog,
                    "Czy na pewno chcesz usunąć wszystkie zadania?",
                    "Potwierdzenie",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    ImageController.resizeIcon(new ImageIcon("resources/icons/error_icon.png"), 50, 50)
            );

            if (confirmation == JOptionPane.YES_OPTION) {
                controller.removeAllTasks();
                JOptionPane.showMessageDialog(
                        dialog,
                        "Wszystkie zadania zostały usunięte!",
                        "Usunięto wszystkie zadania",
                        JOptionPane.INFORMATION_MESSAGE,
                        ImageController.resizeIcon(new ImageIcon("resources/icons/info_icon.png"), 50, 50)
                );
                dialog.dispose();
            }
        });

        // Obsługa anulowania
        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }
}