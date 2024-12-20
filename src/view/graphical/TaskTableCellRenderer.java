package view.graphical;

import controller.TaskController;
import controller.AppController;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class TaskTableCellRenderer extends DefaultTableCellRenderer {
    private final TaskController controller;
    private final AppController appController;

    public TaskTableCellRenderer(TaskController controller, AppController appController) {
        this.controller = controller;
        this.appController = appController;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Pobranie priorytetu z tabeli
        String priority = (String) table.getValueAt(row, 3); // Zakładamy, że priorytet jest w kolumnie 3

        // Pobierz domyślne kolory dla trybu
        boolean isDarkMode = appController.isDarkMode();
        Color defaultBackground = isDarkMode ? Color.DARK_GRAY : new Color(240, 240, 240);
        Color defaultForeground = isDarkMode ? Color.WHITE : Color.BLACK;

        // Kolory dla różnych priorytetów
        Color backgroundColor = defaultBackground;
        Color foregroundColor = defaultForeground;

        if (priority != null) {
            switch (priority) {
                case "bardzo ważne":
                    backgroundColor = new Color(255, 102, 102); // Czerwony
                    foregroundColor = isDarkMode ? Color.BLACK : Color.BLACK; // Czarne w ciemnym i jasnym
                    break;
                case "ważne":
                    backgroundColor = new Color(255, 178, 102); // Pomarańczowy
                    foregroundColor = isDarkMode ? Color.BLACK : Color.BLACK; // Czarne w ciemnym i jasnym
                    break;
                case "normalne":
                    backgroundColor = new Color(255, 255, 153); // Żółty
                    foregroundColor = isDarkMode ? Color.BLACK : Color.BLACK; // Czarne w ciemnym i jasnym
                    break;
                case "bez pośpiechu":
                    backgroundColor = new Color(204, 255, 204); // Zielony
                    foregroundColor = isDarkMode ? Color.BLACK : Color.BLACK; // Czarne w ciemnym i jasnym
                    break;
                default:
                    backgroundColor = defaultBackground; // Domyślne tło
                    foregroundColor = defaultForeground; // Domyślny kolor tekstu
                    break;
            }
        }

        // Zastosowanie kolorów wiersza
        c.setBackground(backgroundColor);
        c.setForeground(foregroundColor);

        // Obsługa zaznaczenia
        if (isSelected) {
            c.setFont(c.getFont().deriveFont(Font.BOLD, c.getFont().getSize() + 2)); // Powiększ czcionkę
            if (foregroundColor.equals(Color.WHITE)) {
                c.setForeground(Color.WHITE); // Tekst pozostaje biały w ciemnym motywie na szarym
            } else {
                c.setForeground(Color.BLACK); // Tekst pozostaje czarny w innych przypadkach
            }
            c.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2)); // Dodanie obramowania
        } else {
            c.setFont(c.getFont().deriveFont(Font.PLAIN, c.getFont().getSize() - 2)); // Przywrócenie domyślnej czcionki
            c.setBorder(null); // Usunięcie obramowania
        }

        return c;
    }
}
