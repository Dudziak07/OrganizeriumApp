package view.graphical;

import controller.TaskController;
import model.Task;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GraphicalListView {
    private final TaskController controller;
    private final JFrame parentFrame;

    public GraphicalListView(TaskController controller, JFrame parentFrame) {
        this.controller = controller;
        this.parentFrame = parentFrame;
    }

    public void show() {
        // Tworzenie okna dialogowego
        JDialog dialog = new JDialog(parentFrame, "Lista zadań", true);
        dialog.setSize(800, 400);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(parentFrame);

        // Tworzenie modelu tabeli
        String[] columnNames = {"ID", "Nazwa", "Kategoria", "Priorytet", "Termin", "Data utworzenia"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        // Pobranie listy zadań i dodanie ich do modelu tabeli
        List<Task> tasks = controller.getTasks();
        for (Task task : tasks) {
            tableModel.addRow(new Object[]{
                    task.getId(),
                    task.getName(),
                    task.getCategory(),
                    task.getPriority(),
                    task.getDeadline(),
                    task.getCreationTime()
            });
        }

        // Tworzenie tabeli i osadzenie jej w scroll panelu
        JTable taskTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(taskTable);

        // Przycisk zamykający widok
        JButton closeButton = new JButton("Zamknij");
        closeButton.addActionListener(e -> dialog.dispose());

        // Dodanie komponentów do okna
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(closeButton, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}