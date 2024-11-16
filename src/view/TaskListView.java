package view;

import controller.TaskController;
import model.Task;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.Label;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.component.Panel.Orientation;

import java.util.List;

public class TaskListView {
    private final TaskController controller;
    private final GUIScreen guiScreen;

    public TaskListView(TaskController controller, GUIScreen guiScreen) {
        this.controller = controller;
        this.guiScreen = guiScreen;
    }

    public void show() {
        Window window = new Window("Lista zadań");
        Panel panel = new Panel(Orientation.VERTICAL);

        // Nagłówki kolumn
        String headers = String.format("%-5s %-21s %-17s %-21s %-12s %-20s",
                "ID", "Nazwa", "Priorytet", "Kategoria", "Termin", "Data utworzenia");
        panel.addComponent(new Label(headers));
        panel.addComponent(new Label("=".repeat(headers.length())));  // Linie pod nagłówkami

        // Wyświetlanie listy zadań w formie tabeli
        List<Task> tasks = controller.getTasks();
        for (Task task : tasks) {
            String taskRow = String.format("%-5d %-21s %-17s %-21s %-12s %-20s",
                    task.getId(), task.getName(), task.getPriority(), task.getCategory(),
                    task.getDeadline(), task.getCreationTime());
            panel.addComponent(new Label(taskRow));
        }

        Button backButton = new Button("Powrót", window::close);
        panel.addComponent(backButton);
        window.addComponent(panel);
        guiScreen.showWindow(window, GUIScreen.Position.CENTER);
    }
}
