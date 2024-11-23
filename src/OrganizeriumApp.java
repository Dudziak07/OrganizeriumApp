import controller.TaskController;
import view.MainMenuView;
import controller.Logger;

public class OrganizeriumApp {
    public static void main(String[] args) {
        Logger.log("Uruchomienie aplikacji", "OrganizeriumApp została uruchomiona");

        TaskController controller= new TaskController();
        MainMenuView mainMenu = new MainMenuView(controller);
        mainMenu.show();
    }
}


