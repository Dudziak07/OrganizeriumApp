package view;

import controller.TaskController;
import model.Task;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.*;

public class TaskEditView {
    private final TaskController controller;
    private final GUIScreen guiScreen;

    // Lista priorytetów
    private final String[] priorities = {"*bez priorytetu*", "bardzo ważne", "ważne", "normalne", "bez pośpiechu"};
    private int priorityIndex = 0;
    private Label priorityLabel;

    public TaskEditView(TaskController controller, GUIScreen guiScreen) {
        this.controller = controller;
        this.guiScreen = guiScreen;
    }

    public void show() {
        Window window = new Window("Edytuj zadanie");
        Panel panel = new Panel(Panel.Orientation.VERTICAL);

        // Pole do wyboru ID lub nazwy
        TextBox idInput = new TextBox();
        TextBox nameInput = new TextBox();

        panel.addComponent(new Label("Edytuj zadanie po ID:"));
        panel.addComponent(idInput);
        Button editByIdButton = new Button("Edytuj po ID", () -> {
            try {
                int id = Integer.parseInt(idInput.getText());
                editTask(id, null);
            } catch (NumberFormatException e) {
                showError("Nieprawidłowy format ID");
            }
        });

        panel.addComponent(new Label("Edytuj zadanie po nazwie:"));
        panel.addComponent(nameInput);
        Button editByNameButton = new Button("Edytuj po nazwie", () -> {
            String name = nameInput.getText();
            if (name.isEmpty()) {
                showError("Nazwa zadania jest wymagana");
                return;
            }
            editTask(0, name);
        });

        panel.addComponent(editByIdButton);
        panel.addComponent(editByNameButton);

        Button backButton = new Button("Powrót", window::close);
        panel.addComponent(backButton);

        window.addComponent(panel);
        guiScreen.showWindow(window, GUIScreen.Position.CENTER);
    }

    private void editTask(int id, String name) {
        Window editWindow = new Window("Wprowadź nowe dane");
        Panel editPanel = new Panel(Panel.Orientation.VERTICAL);

        TextBox newName = new TextBox();
        TextBox newCategory = new TextBox();
        TextBox newDeadline = new TextBox();

        // Etykieta i strzałki dla wyboru priorytetu
        priorityLabel = new Label(formatPriorityLabelText(priorities[priorityIndex]));
        Panel priorityPanel = new Panel(Panel.Orientation.HORISONTAL);
        Button leftArrow = new Button("<-", () -> {
            priorityIndex = (priorityIndex - 1 + priorities.length) % priorities.length;
            updatePriorityLabel();
        });
        Button rightArrow = new Button("->", () -> {
            priorityIndex = (priorityIndex + 1) % priorities.length;
            updatePriorityLabel();
        });
        priorityPanel.addComponent(leftArrow);
        priorityPanel.addComponent(priorityLabel);
        priorityPanel.addComponent(rightArrow);

        editPanel.addComponent(new Label("Nowa nazwa:"));
        editPanel.addComponent(newName);
        editPanel.addComponent(new Label("Nowa kategoria:"));
        editPanel.addComponent(newCategory);
        editPanel.addComponent(new Label("Nowy termin (RRRR-MM-DD):"));
        editPanel.addComponent(newDeadline);
        editPanel.addComponent(new Label("Nowy priorytet:"));
        editPanel.addComponent(priorityPanel);

        Button saveButton = new Button("Zapisz", () -> {
            boolean success;
            if (id > 0) {
                success = controller.editTaskById(id, newName.getText(), newCategory.getText(), newDeadline.getText(), priorities[priorityIndex]);
            } else {
                success = controller.editTaskByName(name, newName.getText(), newCategory.getText(), newDeadline.getText(), priorities[priorityIndex]);
            }
            if (success) {
                editWindow.close();
                show();
            } else {
                showError("Nie znaleziono zadania");
            }
        });

        Button cancelButton = new Button("Anuluj", editWindow::close);

        editPanel.addComponent(saveButton);
        editPanel.addComponent(cancelButton);

        editWindow.addComponent(editPanel);
        guiScreen.showWindow(editWindow, GUIScreen.Position.CENTER);
    }

    private void updatePriorityLabel() {
        priorityLabel.setText(formatPriorityLabelText(priorities[priorityIndex]));
    }

    private String formatPriorityLabelText(String text) {
        return String.format("%-16s", text);  // Formatowanie tekstu do wyrównania
    }

    private void showError(String message) {
        Window errorWindow = new Window("Błąd");
        Panel errorPanel = new Panel(Panel.Orientation.VERTICAL);
        errorPanel.addComponent(new Label(message));
        Button closeButton = new Button("Zamknij", errorWindow::close);
        errorPanel.addComponent(closeButton);
        errorWindow.addComponent(errorPanel);
        guiScreen.showWindow(errorWindow, GUIScreen.Position.CENTER);
    }
}
