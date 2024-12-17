import controller.TaskController;
import view.GraphicalView;

public class OrganizeriumApp {
    public static void main(String[] args) {
        TaskController controller = new TaskController();

        // Uruchamiamy aplikację w trybie graficznym
        GraphicalView graphicalView = new GraphicalView(controller);
        graphicalView.show();
    }
}