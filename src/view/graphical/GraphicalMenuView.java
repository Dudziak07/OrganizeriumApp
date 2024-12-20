package view.graphical;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import controller.AppController;
import controller.ImageController;
import controller.Logger;
import controller.TaskController;
import view.textual.MainMenuView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;

public class GraphicalMenuView {
    private final TaskController controller;
    private final AppController appController;
    private final JFrame frame;
    private JPanel mainPanel;
    private final java.util.List<JButton> buttons = new java.util.ArrayList<>();
    private boolean isKeyboardNavigationActive = false; // Aktywacja dopiero po klawiaturze
    private int selectedButtonIndex = -1; // Żaden przycisk nieaktywny na starcie

    public GraphicalMenuView(TaskController controller, AppController appController) {
        this.controller = controller;
        this.appController = appController;

        // Inicjalizacja JFrame przed użyciem w setLookAndFeel
        this.frame = new JFrame("OrganizeriumApp - Menu Główne");

        setLookAndFeel(appController.isDarkMode()); // Wywołanie po inicjalizacji frame
        Logger.log("Uruchomienie aplikacji", "Zainicjowano tryb graficzny");

        // Obsługa zamknięcia okna
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                showExitConfirmation();
            }
        });
    }

    public void show() {
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                showExitConfirmation();
            }
        });

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Opcje");

        JMenuItem switchToTextMode = new JMenuItem("Przełącz na tryb tekstowy");
        switchToTextMode.addActionListener(e -> {
            Logger.log("Przełączanie trybu", "Przełączono na tryb tekstowy");
            frame.dispose(); // Zamknięcie okna graficznego
            new Thread(() -> new MainMenuView(controller, appController).show()).start(); // Przekazanie obu argumentów
        });

        JMenuItem switchTheme = new JMenuItem(appController.isDarkMode() ? "Przełącz na tryb jasny" : "Przełącz na tryb ciemny");
        switchTheme.addActionListener(e -> {
            appController.toggleDarkMode();
            Logger.log("Przełączenie motywu", appController.isDarkMode() ? "Ustawiono tryb ciemny" : "Ustawiono tryb jasny");
            setLookAndFeel(appController.isDarkMode());
            switchTheme.setText(appController.isDarkMode() ? "Przełącz na tryb jasny" : "Przełącz na tryb ciemny");
            refreshView();
        });

        menu.add(switchToTextMode);
        menu.add(switchTheme);
        menuBar.add(menu);
        frame.setJMenuBar(menuBar);

        mainPanel = createMainPanel();
        frame.add(mainPanel);

        frame.setVisible(true);
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 10, 50));
        buttonPanel.setFocusable(true);
        buttonPanel.requestFocusInWindow();

        // Create buttons
        JButton addTaskButton = createStyledButton("Dodaj zadanie", new Color(102, 255, 102, 179), new Color(102, 255, 102, 255), e -> new GraphicalAddView(controller, frame).show());
        JButton listTaskButton = createStyledButton("Wyświetl zadania", new Color(102, 178, 255, 179), new Color(102, 178, 255, 255), e -> new GraphicalListView(controller, frame).show());
        JButton editTaskButton = createStyledButton("Edytuj zadanie", new Color(255, 204, 102, 179), new Color(255, 204, 102, 255), e -> {
            String taskIdInput = JOptionPane.showInputDialog(frame, "Podaj ID zadania do edycji:", "Edytuj zadanie", JOptionPane.QUESTION_MESSAGE);
            if (taskIdInput != null && !taskIdInput.trim().isEmpty()) {
                try {
                    int taskId = Integer.parseInt(taskIdInput.trim());
                    new GraphicalEditView(controller, frame).show(taskId);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Nieprawidłowy format ID!", "Błąd", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        JButton deleteTaskButton = createStyledButton("Usuń zadanie", new Color(255, 102, 102, 179), new Color(255, 102, 102, 255), e -> new GraphicalDeleteView(controller, frame).show());
        JButton exitButton = createExitButton();

        // Add buttons to panel and list
        buttonPanel.add(addTaskButton);
        buttonPanel.add(listTaskButton);
        buttonPanel.add(editTaskButton);
        buttonPanel.add(deleteTaskButton);
        buttonPanel.add(exitButton);

        buttons.clear();
        buttons.add(addTaskButton);
        buttons.add(listTaskButton);
        buttons.add(editTaskButton);
        buttons.add(deleteTaskButton);
        buttons.add(exitButton);

        // Map UP and DOWN keys
        InputMap inputMap = buttonPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = buttonPanel.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "focusPrevious");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "focusNext");

        actionMap.put("focusPrevious", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedButtonIndex = (selectedButtonIndex - 1 + buttons.size()) % buttons.size();
                updateButtonSelection();
            }
        });

        actionMap.put("focusNext", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedButtonIndex = (selectedButtonIndex + 1) % buttons.size();
                updateButtonSelection();
            }
        });

        panel.add(buttonPanel, BorderLayout.CENTER);
        return panel;
    }

    private void setLookAndFeel(boolean darkMode) {
        try {
            UIManager.setLookAndFeel(darkMode ? new FlatDarkLaf() : new FlatLightLaf());
            if (frame != null) {
                SwingUtilities.updateComponentTreeUI(frame);
            }

            // Ustawienie tła w zależności od trybu
            Color backgroundColor = darkMode ? Color.DARK_GRAY : new Color(240, 240, 240); // Jasnoszary dla trybu jasnego
            if (mainPanel != null) {
                mainPanel.setBackground(backgroundColor);
            }
            frame.getContentPane().setBackground(backgroundColor); // Dostosowanie tła okna
        } catch (UnsupportedLookAndFeelException e) {
            Logger.log("Błąd motywu", "Nie udało się załadować motywu");
        }
    }

    private void refreshView() {
        frame.getContentPane().removeAll();
        mainPanel = createMainPanel();
        frame.add(mainPanel);

        // Zastosowanie tła dla trybu
        Color backgroundColor = appController.isDarkMode() ? Color.DARK_GRAY : new Color(240, 240, 240);
        mainPanel.setBackground(backgroundColor);
        frame.getContentPane().setBackground(backgroundColor);

        SwingUtilities.updateComponentTreeUI(frame);
        frame.repaint();
    }

    // Metoda do tworzenia stylizowanego przycisku
    private JButton createStyledButton(String text, Color hoverColor, Color borderColor, java.awt.event.ActionListener action) {
        JButton button = new JButton(text);

        // Kolory domyślne w zależności od trybu
        Color defaultBackground = appController.isDarkMode() ? Color.DARK_GRAY : new Color(235, 235, 235); // Jasnoszary dla trybu jasnego
        Color defaultForeground = appController.isDarkMode() ? Color.WHITE : Color.BLACK; // Czarny tekst w trybie jasnym

        // Ustawienia stylu przycisku
        button.setBackground(defaultBackground);
        button.setForeground(defaultForeground);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(borderColor, 2)); // Ramka
        button.setContentAreaFilled(true);
        button.setOpaque(true);

        // Obsługa hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(defaultBackground);
            }
        });

        button.addActionListener(action);
        return button;
    }

    private JButton createExitButton() {
        JButton exitButton = new JButton();
        try {
            ImageIcon originalIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/power_off.png")));
            ImageIcon hoverIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/power_off_mouse.png")));

            Image scaledOriginalIcon = originalIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
            Image scaledHoverIcon = hoverIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);

            exitButton.setIcon(new ImageIcon(scaledOriginalIcon));

            // Obsługa myszy (hover)
            exitButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    exitButton.setIcon(new ImageIcon(scaledHoverIcon));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    exitButton.setIcon(new ImageIcon(scaledOriginalIcon));
                }
            });

            // Obsługa klawiatury (focus)
            exitButton.addFocusListener(new java.awt.event.FocusAdapter() {
                @Override
                public void focusGained(java.awt.event.FocusEvent e) {
                    exitButton.setIcon(new ImageIcon(scaledHoverIcon));
                }

                @Override
                public void focusLost(java.awt.event.FocusEvent e) {
                    exitButton.setIcon(new ImageIcon(scaledOriginalIcon));
                }
            });

        } catch (Exception e) {
            Logger.log("Błąd ładowania ikony", "Nie udało się załadować ikon wyłączania");
        }

        exitButton.setToolTipText("Zamknij aplikację");
        exitButton.setPreferredSize(new Dimension(50, 50));
        exitButton.setFocusPainted(true); // Dodaj możliwość focusa
        exitButton.setContentAreaFilled(false);
        exitButton.setBorderPainted(false);
        exitButton.addActionListener(e -> showExitConfirmation());

        return exitButton;
    }

    private void showExitConfirmation() {
        int confirmation = JOptionPane.showConfirmDialog(
                frame,
                "Czy na pewno chcesz zamknąć aplikację?",
                "Potwierdzenie zamknięcia",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                ImageController.resizeIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/power_off_mouse.png"))), 50, 50)
        );

        if (confirmation == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    private void updateButtonSelection() {
        for (int i = 0; i < buttons.size(); i++) {
            JButton button = buttons.get(i);
            if (i == selectedButtonIndex && isKeyboardNavigationActive) {
                button.requestFocusInWindow(); // Set focus
            }
        }
    }
}