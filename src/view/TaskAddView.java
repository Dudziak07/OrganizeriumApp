package view;

import controller.TaskController;
import model.Task;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.component.Label;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.component.TextBox;
import com.googlecode.lanterna.gui.component.Panel.Orientation;

public class TaskAddView {
    private final TaskController controller;
    private final GUIScreen guiScreen;
    private final MainMenuView mainMenuView;
    private final String[] priorities = {"*bez priorytetu*", "bardzo ważne", "ważne", "normalne", "bez pośpiechu"};
    private int priorityIndex = 0;  // Domyślnie ustawione na "bez priorytetu"
    private Label priorityLabel;    // Deklaracja priorityLabel na poziomie klasy
    private static final int MAX_TEXT_LENGTH = 20;  // Maksymalna liczba znaków dla pola tekstowego

    public TaskAddView(TaskController controller, GUIScreen guiScreen, MainMenuView mainMenuView) {
        this.controller = controller;
        this.guiScreen = guiScreen;
        this.mainMenuView = mainMenuView;
    }

    public void show() {
        Window window = new Window("Dodaj nowe zadanie");
        Panel panel = new Panel(Orientation.VERTICAL);

        // Pola tekstowe o szerokości 20 znaków
        TextBox nameInput = new TextBox("", 20);
        TextBox categoryInput = new TextBox("", 20);
        TextBox deadlineInput = new TextBox("", 11);

        panel.addComponent(new Label("Nazwa zadania*:"));
        panel.addComponent(nameInput);
        panel.addComponent(new Label("Kategoria:"));
        panel.addComponent(categoryInput);
        panel.addComponent(new Label("Termin (RRRR-MM-DD):"));
        panel.addComponent(deadlineInput);

        // Etykieta priorytetu z przyciskami strzałek w poziomie
        panel.addComponent(new Label("Priorytet:"));
        Panel priorityRow = new Panel(Orientation.HORISONTAL);  // Wewnętrzny panel z układem poziomym

        // Inicjalizacja etykiety wyświetlającej priorytet
        priorityLabel = new Label(formatPriorityLabelText(priorities[priorityIndex]));

        // Przyciski strzałek do zmiany priorytetu
        Button leftArrow = new Button("<-", () -> {
            priorityIndex = (priorityIndex - 1 + priorities.length) % priorities.length;
            updatePriorityLabel();
        });

        Button rightArrow = new Button("->", () -> {
            priorityIndex = (priorityIndex + 1) % priorities.length;
            updatePriorityLabel();
        });

        // Dodajemy strzałki i etykietę priorytetu w jednym wierszu
        priorityRow.addComponent(leftArrow);
        priorityRow.addComponent(priorityLabel);
        priorityRow.addComponent(rightArrow);

        // Dodanie panelu priorityRow (z elementami w poziomie) do głównego panelu
        panel.addComponent(priorityRow);

        // Przycisk Zapisz
        Button saveButton = new Button("Zapisz", () -> {
            if (nameInput.getText().trim().isEmpty()) {
                showError("Nazwa zadania jest wymagana");
                return;
            }

            // Sprawdzanie długości pól tekstowych
            if (nameInput.getText().length() > MAX_TEXT_LENGTH) {
                showError("Nazwa zadania nie może przekraczać 20 znaków.");
                return;
            }
            if (categoryInput.getText().length() > MAX_TEXT_LENGTH) {
                showError("Kategoria nie może przekraczać 20 znaków.");
                return;
            }

            String datePattern = "\\d{4}-\\d{2}-\\d{2}";
            if (!deadlineInput.getText().isEmpty() && !deadlineInput.getText().matches(datePattern)) {
                showError("Niepoprawny format daty. Użyj formatu RRRR-MM-DD");
                return;
            }

            Task task = new Task(controller.getNextId(), nameInput.getText(), categoryInput.getText(), deadlineInput.getText(), priorities[priorityIndex]);
            controller.addTask(task);

            window.close();
            mainMenuView.show();
        });

        // Przycisk Anuluj poniżej przycisku Zapisz
        Button cancelButton = new Button("Anuluj", window::close);

        // Dodajemy przyciski do głównego panelu w pionie, co umieszcza "Anuluj" poniżej "Zapisz"
        panel.addComponent(saveButton);
        panel.addComponent(cancelButton);

        window.addComponent(panel);
        guiScreen.showWindow(window, GUIScreen.Position.CENTER);
    }

    // Metoda formatująca tekst priorytetu do stałej długości
    private String formatPriorityLabelText(String text) {
        return String.format("%-16s", text);  // Formatowanie do lewej z długością 14 znaków
    }

    // Aktualizacja tekstu priorytetu w etykiecie
    private void updatePriorityLabel() {
        priorityLabel.setText(formatPriorityLabelText(priorities[priorityIndex]));
    }

    private void showError(String message) {
        Window errorWindow = new Window("Błąd");
        Panel errorPanel = new Panel(Orientation.VERTICAL);
        errorPanel.addComponent(new Label(message));
        Button closeButton = new Button("Zamknij", errorWindow::close);
        errorPanel.addComponent(closeButton);
        errorWindow.addComponent(errorPanel);
        guiScreen.showWindow(errorWindow, GUIScreen.Position.CENTER);
    }
}
