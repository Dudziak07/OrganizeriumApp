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
        filterDialog.setSize(400, 300);
        filterDialog.setLayout(new GridLayout(5, 2, 10, 10));
        filterDialog.setLocationRelativeTo(parentFrame);

        // Pobranie unikalnych kategorii
        Set<String> categories = controller.getTasks().stream()
                .map(Task::getCategory)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        categories.add("Wszystkie");

        JComboBox<String> priorityComboBox = new JComboBox<>(new String[]{"Wszystkie", "bardzo ważne", "ważne", "normalne", "bez pośpiechu"});
        JComboBox<String> categoryComboBox = new JComboBox<>(categories.toArray(new String[0]));
        JTextField dateField = new JTextField();

        JButton filterButton = new JButton("Filtruj");

        filterDialog.add(new JLabel("Priorytet:"));
        filterDialog.add(priorityComboBox);
        filterDialog.add(new JLabel("Kategoria:"));
        filterDialog.add(categoryComboBox);
        filterDialog.add(new JLabel("Data zakończenia (YYYY-MM-DD):"));
        filterDialog.add(dateField);
        filterDialog.add(new JLabel());
        filterDialog.add(filterButton);

        filterButton.addActionListener(e -> {
            String priority = (String) priorityComboBox.getSelectedItem();
            String category = (String) categoryComboBox.getSelectedItem();
            String date = dateField.getText().trim();

            List<Task> filteredTasks = controller.getTasks().stream()
                    .filter(task -> "Wszystkie".equals(priority) || task.getPriority().equalsIgnoreCase(priority))
                    .filter(task -> "Wszystkie".equals(category) || task.getCategory().equalsIgnoreCase(category))
                    .filter(task -> date.isEmpty() || task.getDeadline().equals(date))
                    .collect(Collectors.toList());

            loadTasksToTable(tableModel, filteredTasks);
            filterDialog.dispose();
        });

        filterDialog.setVisible(true);
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

            Comparator<Task> comparator = switch (column) {
                case "ID" -> Comparator.comparing(Task::getId);
                case "Nazwa" -> Comparator.comparing(Task::getName);
                case "Kategoria" -> Comparator.comparing(Task::getCategory);
                case "Priorytet" -> Comparator.comparing(Task::getPriority);
                case "Termin" -> Comparator.comparing(Task::getDeadline);
                default -> Comparator.comparing(Task::getCreationTime);
            };

            if (!ascending) comparator = comparator.reversed();

            List<Task> sortedTasks = controller.getTasks().stream()
                    .sorted(comparator)
                    .collect(Collectors.toList());

            loadTasksToTable(tableModel, sortedTasks);
            sortDialog.dispose();
        });

        sortDialog.setVisible(true);
    }
}