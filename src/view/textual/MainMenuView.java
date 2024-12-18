package view.textual;

import com.googlecode.lanterna.TerminalFacade;
import controller.Logger;
import controller.TaskController;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.component.Label;
import com.googlecode.lanterna.gui.component.Panel;
import com.googlecode.lanterna.gui.component.Panel.Orientation;
import view.graphical.GraphicalMenuView;

public class MainMenuView {
    private final TaskController controller;
    private final GUIScreen guiScreen;

    public MainMenuView(TaskController controller) {
        this.controller = controller;
        this.guiScreen = TerminalFacade.createGUIScreen();
        guiScreen.getScreen().startScreen();
    }

    public void show() {
        Window window = new Window("Organizerium - Menu Główne");
        Panel panel = new Panel(Orientation.VERTICAL);

        panel.addComponent(new Label("Witaj w Organizerium!"));
        panel.addComponent(new Button("Dodaj zadanie", this::showAddTask));
        panel.addComponent(new Button("Pokaż zadania", this::showTaskList));
        panel.addComponent(new Button("Edytuj zadanie", this::showEditTaskMenu));
        panel.addComponent(new Button("Usuń zadanie", this::showDeleteTaskMenu));
        panel.addComponent(new Button("Przełącz na tryb graficzny", this::switchToGraphicalMode));
        panel.addComponent(new Button("Wyłącz aplikację", this::exitApplication));

        window.addComponent(panel);
        guiScreen.showWindow(window, GUIScreen.Position.CENTER);
    }

    private void showAddTask() {
        new TaskAddView(controller, guiScreen, this).show();
    }

    private void showTaskList() {
        new TaskListView(controller, guiScreen).show();
    }

    private void showEditTaskMenu() {
        new TaskEditView(controller, guiScreen).show();
    }

    private void showDeleteTaskMenu() {
        new TaskDeleteView(controller, guiScreen).show();
    }

    // Metoda przełączająca na tryb graficzny
    private void switchToGraphicalMode() {
        Logger.log("Przełączanie trybu", "Przełączono na tryb graficzny");
        guiScreen.getScreen().stopScreen(); // Zamknij ekran Lanterna
        new Thread(() -> {
            new GraphicalMenuView(controller).show(); // Uruchom tryb graficzny
        }).start();
    }

    // Metoda zamykająca aplikację
    private void exitApplication() {
        Logger.log("Zamknięcie aplikacji", "Aplikacja została zamknięta z trybu tekstowego");
        guiScreen.getScreen().stopScreen();  // Zatrzymuje ekran Lanterna
        System.exit(0);  // Kończy działanie programu
    }
}
