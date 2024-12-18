package view.graphical;

import controller.TaskController;
import model.Task;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

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
        dialog.setSize(800, 500);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(parentFrame);

        // Tworzenie modelu tabeli
        String[] columnNames = {"ID", "Nazwa", "Kategoria", "Priorytet", "Termin", "Data utworzenia"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        loadTasksToTable(tableModel, controller.getTasks());

        // Tworzenie tabeli i osadzenie jej w scroll panelu
        JTable taskTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(taskTable);

        // Panel z przyciskami
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        JButton filterButton = new JButton("Filtruj");
        JButton sortButton = new JButton("Sortuj");
        JButton closeButton = new JButton("Zamknij");

        buttonPanel.add(filterButton);
        buttonPanel.add(sortButton);
        buttonPanel.add(closeButton);

        closeButton.addActionListener(e -> dialog.dispose());
        filterButton.addActionListener(e -> showFilterDialog(tableModel));
        sortButton.addActionListener(e -> showSortDialog(tableModel));

        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void loadTasksToTable(DefaultTableModel tableModel, List<Task> tasks) {
        tableModel.setRowCount(0);
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
    }

    private void showFilterDialog(DefaultTableModel tableModel) {
        JDialog filterDialog = new JDialog(parentFrame, "Filtruj zadania", true);
        filterDialog.setSize(400, 400);
        filterDialog.setLayout(new GridLayout(7, 2, 10, 10));
        filterDialog.setLocationRelativeTo(parentFrame);

        JComboBox<String> priorityComboBox = new JComboBox<>(new String[]{
                "Wszystkie", "*bez priorytetu*", "bardzo ważne", "ważne", "normalne", "bez pośpiechu", "Wyszukaj"
        });

        JComboBox<String> categoryComboBox = new JComboBox<>(new String[]{"Wszystkie"});
        controller.getTasks().stream()
                .map(Task::getCategory)
                .filter(Objects::nonNull)
                .distinct()
                .forEach(categoryComboBox::addItem);

        JTextField nameField = new JTextField();
        JTextField creationDateField = new JTextField();
        JTextField deadlineField = new JTextField();

        JButton filterButton = new JButton("Filtruj");
        JButton cancelButton = new JButton("Anuluj");

        filterDialog.add(new JLabel("Priorytet:"));
        filterDialog.add(priorityComboBox);
        filterDialog.add(new JLabel("Kategoria:"));
        filterDialog.add(categoryComboBox);
        filterDialog.add(new JLabel("Nazwa:"));
        filterDialog.add(nameField);
        filterDialog.add(new JLabel("Data utworzenia (YYYY-MM-DD):"));
        filterDialog.add(creationDateField);
        filterDialog.add(new JLabel("Data zakończenia (YYYY-MM-DD):"));
        filterDialog.add(deadlineField);
        filterDialog.add(filterButton);
        filterDialog.add(cancelButton);

        filterButton.addActionListener(e -> {
            String priority = (String) priorityComboBox.getSelectedItem();
            String category = (String) categoryComboBox.getSelectedItem();
            String name = nameField.getText().trim();
            String creationDate = creationDateField.getText().trim();
            String deadline = deadlineField.getText().trim();

            List<Task> filteredTasks = controller.filterTasks(priority, category, name, creationDate, deadline);
            loadTasksToTable(tableModel, filteredTasks);
            filterDialog.dispose();
        });

        cancelButton.addActionListener(e -> filterDialog.dispose());

        filterDialog.setVisible(true);
    }

    // Metoda dopasowująca zadanie do wyszukiwania ogólnego
    private boolean matchesSearch(Task task, String name, String date, String deadline) {
        return (name.isEmpty() || task.getName().toLowerCase().contains(name.toLowerCase())) ||
                (date.isEmpty() || task.getCreationTime().startsWith(date)) ||
                (deadline.isEmpty() || task.getDeadline().startsWith(deadline));
    }

    private void showSortDialog(DefaultTableModel tableModel) {
        JDialog sortDialog = new JDialog(parentFrame, "Sortuj zadania", true);
        sortDialog.setSize(400, 200);
        sortDialog.setLayout(new GridLayout(3, 2, 10, 10));
        sortDialog.setLocationRelativeTo(parentFrame);

        JComboBox<String> columnComboBox = new JComboBox<>(new String[]{"ID", "Nazwa", "Kategoria", "Priorytet", "Termin", "Data utworzenia"});
        JComboBox<String> orderComboBox = new JComboBox<>(new String[]{"Rosnąco", "Malejąco"});
        JButton sortButton = new JButton("Sortuj");

        sortDialog.add(new JLabel("Sortuj po kolumnie:"));
        sortDialog.add(columnComboBox);
        sortDialog.add(new JLabel("Kierunek:"));
        sortDialog.add(orderComboBox);
        sortDialog.add(new JLabel());
        sortDialog.add(sortButton);

        sortButton.addActionListener(e -> {
            String column = (String) columnComboBox.getSelectedItem();
            boolean ascending = "Rosnąco".equals(orderComboBox.getSelectedItem());

            List<Task> sortedTasks = controller.sortTasks(column, ascending);
            loadTasksToTable(tableModel, sortedTasks);
            sortDialog.dispose();
        });

        sortDialog.setVisible(true);
    }
}