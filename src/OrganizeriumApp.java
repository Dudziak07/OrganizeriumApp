import controller.AppController;
import controller.TaskController;
import view.graphical.GraphicalMenuView;

public class OrganizeriumApp {
    public static void main(String[] args) {
        TaskController taskController = TaskController.getInstance();
        AppController appController = new AppController(); // Dodanie AppController

        // Przekazanie AppController do widoku
        GraphicalMenuView graphicalView = new GraphicalMenuView(taskController, appController);
        graphicalView.show();
    }
}
