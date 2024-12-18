package view.shared;

import javax.swing.*;
import java.awt.*;

public class TaskForm {
    public static JPanel createTaskForm(JTextField nameField, JTextField categoryField,
                                        JTextField deadlineField, JComboBox<String> priorityBox) {
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));

        formPanel.add(new JLabel("Nazwa zadania*:"));
        formPanel.add(nameField);

        formPanel.add(new JLabel("Kategoria:"));
        formPanel.add(categoryField);

        formPanel.add(new JLabel("Termin (YYYY-MM-DD):"));
        formPanel.add(deadlineField);

        formPanel.add(new JLabel("Priorytet:"));
        formPanel.add(priorityBox);

        return formPanel;
    }
}