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

        // Pobierz priorytet
        String priority = (String) table.getValueAt(row, 3);

        // Kolory dla trybów
        boolean isDarkMode = appController.isDarkMode();
        Color defaultBackground = isDarkMode ? Color.DARK_GRAY : new Color(240, 240, 240);
        Color defaultForeground = isDarkMode ? Color.WHITE : Color.BLACK;

        // Kolory dla priorytetów
        Color backgroundColor = defaultBackground;
        Color foregroundColor = defaultForeground;

        if (priority != null) {
            switch (priority) {
                case "bardzo ważne":
                    backgroundColor = new Color(255, 102, 102);
                    foregroundColor = isDarkMode ? Color.BLACK : Color.BLACK;
                    break;
                case "ważne":
                    backgroundColor = new Color(255, 178, 102);
                    foregroundColor = isDarkMode ? Color.BLACK : Color.BLACK;
                    break;
                case "normalne":
                    backgroundColor = new Color(255, 255, 153);
                    foregroundColor = isDarkMode ? Color.BLACK : Color.BLACK;
                    break;
                case "bez pośpiechu":
                    backgroundColor = new Color(204, 255, 204);
                    foregroundColor = isDarkMode ? Color.BLACK : Color.BLACK;
                    break;
            }
        }

        // Ustawienia zaznaczenia
        if (isSelected) {
            c.setFont(c.getFont().deriveFont(Font.BOLD, c.getFont().getSize() + 2));
            c.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
        } else {
            c.setFont(c.getFont().deriveFont(Font.PLAIN));
            c.setBorder(null);
        }

        // Zastosuj kolory
        c.setBackground(backgroundColor);
        c.setForeground(foregroundColor);

        return c;
    }
}
