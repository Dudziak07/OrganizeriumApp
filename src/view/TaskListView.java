package view;

import controller.TaskController;
import model.Task;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.Label;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.component.Panel.Orientation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class TaskListView {
    private final TaskController controller;
    private final GUIScreen guiScreen;

    // Maksymalna liczba wierszy na jedną stronę
    private static final int MAX_VISIBLE_ROWS = 15;

    public TaskListView(TaskController controller, GUIScreen guiScreen) {
        this.controller = controller;
        this.guiScreen = guiScreen;
    }

    public void show() {
        List<Task> tasks = controller.getTasks();
        showPage(tasks, 0); // Rozpoczynamy od pierwszej strony
    }

    private void showPage(List<Task> tasks, int pageIndex) {
        Window window = new Window("Lista zadań");
        Panel panel = new Panel(Orientation.VERTICAL);

        // Nagłówki kolumn z nową kolumną "Do terminu"
        String headers = String.format("%-5s %-21s %-17s %-21s %-9s %-12s %-20s",
                "ID", "Nazwa", "Priorytet", "Kategoria", "Do terminu", "Termin", "Data utworzenia");
        panel.addComponent(new Label(headers));
        panel.addComponent(new Label("=".repeat(headers.length())));  // Linie pod nagłówkami

        // Wyświetlanie zadań dla danej strony
        int startIndex = pageIndex * MAX_VISIBLE_ROWS;
        int endIndex = Math.min(startIndex + MAX_VISIBLE_ROWS, tasks.size());
        for (int i = startIndex; i < endIndex; i++) {
            Task task = tasks.get(i);
            String daysUntilDeadline = calculateDaysUntilDeadline(task.getDeadline());
            String taskRow = String.format("%-5d %-21s %-17s %-21s %-9s %-12s %-20s",
                    task.getId(), task.getName(), task.getPriority(), task.getCategory(),
                    daysUntilDeadline, task.getDeadline(), task.getCreationTime());
            panel.addComponent(new Label(taskRow));
        }

        // Informacja o numerze strony i liczbie wszystkich zadań
        int totalPages = (int) Math.ceil((double) tasks.size() / MAX_VISIBLE_ROWS);
        panel.addComponent(new Label(String.format("Strona %d z %d (łącznie zadań: %d)", pageIndex + 1, totalPages, tasks.size())));

        // Panel z przyciskami nawigacji
        Panel navigationPanel = new Panel(Orientation.HORISONTAL);

        if (pageIndex > 0) {
            Button prevButton = new Button("Poprzednia strona", () -> {
                window.close();
                showPage(tasks, pageIndex - 1);
            });
            navigationPanel.addComponent(prevButton);
        }

        if (endIndex < tasks.size()) {
            Button nextButton = new Button("Następna strona", () -> {
                window.close();
                showPage(tasks, pageIndex + 1);
            });
            navigationPanel.addComponent(nextButton);
        }

        panel.addComponent(navigationPanel);

        // Przycisk powrotu
        Button backButton = new Button("Powrót", window::close);
        panel.addComponent(backButton);

        // Dodanie panelu do okna
        window.addComponent(panel);
        guiScreen.showWindow(window, GUIScreen.Position.CENTER);
    }

    // Metoda obliczająca dni do deadline
    private String calculateDaysUntilDeadline(String deadline) {
        if (deadline == null || deadline.isEmpty()) {
            return ""; // Brak daty końcowej
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate deadlineDate = LocalDate.parse(deadline, formatter);
            LocalDate currentDate = LocalDate.now();

            long days = ChronoUnit.DAYS.between(currentDate, deadlineDate);

            if (days > 999) {
                return ">999d";
            } else if (days < -999) {
                return ">-999d";
            } else {
                return days >= 0 ? String.format("%dd", days) : String.format("-%dd", Math.abs(days));
            }
        } catch (Exception e) {
            return "Nieprawidłowy";
        }
    }
}
