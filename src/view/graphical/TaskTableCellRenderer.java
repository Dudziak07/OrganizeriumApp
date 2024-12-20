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
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Pobranie priorytetu z tabeli
        String priority = (String) table.getValueAt(row, 3); // Zakładamy, że priorytet jest w kolumnie 3

        // Pobierz domyślne kolory dla trybu
        Color defaultBackground = appController.isDarkMode() ? Color.DARK_GRAY : new Color(240, 240, 240); // Kolor tła wiersza
        Color defaultForeground = appController.isDarkMode() ? Color.WHITE : Color.BLACK; // Kolor tekstu

        // Ustawienie kolorów wierszy na podstawie priorytetów
        if (priority != null) {
            switch (priority) {
                case "bardzo ważne":
                    c.setBackground(new Color(255, 102, 102)); // Czerwony
                    break;
                case "ważne":
                    c.setBackground(new Color(255, 178, 102)); // Pomarańczowy
                    break;
                case "normalne":
                    c.setBackground(new Color(255, 255, 153)); // Żółty
                    break;
                case "bez pośpiechu":
                    c.setBackground(new Color(204, 255, 204)); // Zielony
                    break;
                default:
                    c.setBackground(defaultBackground); // Domyślny kolor
                    break;
            }
        } else {
            c.setBackground(Color.WHITE);
        }

        // Ustawienie koloru tekstu
        c.setForeground(isSelected ? Color.WHITE : Color.BLACK);

        return c;
    }
}