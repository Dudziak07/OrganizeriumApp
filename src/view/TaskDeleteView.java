package view;

import controller.TaskController;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.component.Label;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.component.TextBox;
import com.googlecode.lanterna.gui.component.Panel.Orientation;

public class TaskDeleteView {
    private final TaskController controller;
    private final GUIScreen guiScreen;

    public TaskDeleteView(TaskController controller, GUIScreen guiScreen) {
        this.controller = controller;
        this.guiScreen = guiScreen;
    }

    public void show() {
        Window window = new Window("Usuń zadanie");
        Panel panel = new Panel(Orientation.VERTICAL);

        TextBox idInput = new TextBox();
        TextBox nameInput = new TextBox();

        panel.addComponent(new Label("Usuń zadanie po ID:"));
        panel.addComponent(idInput);
        Button deleteByIdButton = new Button("Usuń po ID", () -> {
            int id = Integer.parseInt(idInput.getText());
            controller.removeTaskById(id);
            window.close();
        });

        panel.addComponent(new Label("Usuń zadanie po nazwie:"));
        panel.addComponent(nameInput);
        Button deleteByNameButton = new Button("Usuń po nazwie", () -> {
            controller.removeTaskByName(nameInput.getText());
            window.close();
        });

        Button deleteAllButton = new Button("Usuń wszystkie", () -> {
            controller.removeAllTasks();
            window.close();
        });

        panel.addComponent(deleteByIdButton);
        panel.addComponent(deleteByNameButton);
        panel.addComponent(deleteAllButton);

        Button backButton = new Button("Powrót", window::close);
        panel.addComponent(backButton);

        window.addComponent(panel);
        guiScreen.showWindow(window, GUIScreen.Position.CENTER);
    }
}
