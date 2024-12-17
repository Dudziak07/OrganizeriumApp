import controller.TaskController;
import view.GraphicalMenuView;

public class OrganizeriumApp {
    public static void main(String[] args) {
        TaskController controller = TaskController.getInstance();

        // Uruchamiamy aplikację w trybie graficznym
        GraphicalMenuView graphicalView = new GraphicalMenuView(controller);
        graphicalView.show();
    }
}