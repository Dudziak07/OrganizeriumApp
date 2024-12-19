package view.graphical;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import controller.Logger;
import controller.TaskController;
import view.textual.MainMenuView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;

public class GraphicalMenuView {
    private final TaskController controller;
    private final JFrame frame;
    private static boolean isDarkMode = true; // Flaga dla trybu ciemnego (statyczna)
    private JPanel mainPanel; // Główny panel do dynamicznego odświeżania

    public GraphicalMenuView(TaskController controller) {
        this.controller = controller;

        setLookAndFeel(isDarkMode);

        this.frame = new JFrame("OrganizeriumApp - Menu Główne");

        Logger.log("Uruchomienie aplikacji", "Zainicjowano tryb graficzny");
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
            frame.dispose();
            new Thread(() -> new MainMenuView(controller).show()).start();
        });

        JMenuItem switchTheme = new JMenuItem(isDarkMode ? "Przełącz na tryb jasny" : "Przełącz na tryb ciemny");
        switchTheme.addActionListener(e -> {
            isDarkMode = !isDarkMode;
            Logger.log("Przełączenie motywu", isDarkMode ? "Ustawiono tryb ciemny" : "Ustawiono tryb jasny");
            setLookAndFeel(isDarkMode);
            switchTheme.setText(isDarkMode ? "Przełącz na tryb jasny" : "Przełącz na tryb ciemny");
            refreshView(); // Odświeżenie widoku
        });

        menu.add(switchToTextMode);
        menu.add(switchTheme);
        menuBar.add(menu);
        frame.setJMenuBar(menuBar);

        // Główna zawartość
        mainPanel = createMainPanel();
        frame.add(mainPanel);

        frame.setVisible(true);
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 10, 50));

        buttonPanel.add(createStyledButton("Dodaj zadanie", new Color(102, 255, 102, 204), new Color(51, 153, 51), e -> new GraphicalAddView(controller, frame).show()));
        buttonPanel.add(createStyledButton("Wyświetl zadania", new Color(102, 178, 255, 204), new Color(0, 102, 204), e -> new GraphicalListView(controller, frame).show()));
        buttonPanel.add(createStyledButton("Edytuj zadanie", new Color(255, 204, 102, 204), new Color(255, 153, 51), e -> {
            String taskIdInput = JOptionPane.showInputDialog(frame, "Podaj ID zadania do edycji:", "Edytuj zadanie", JOptionPane.QUESTION_MESSAGE);
            if (taskIdInput != null && !taskIdInput.trim().isEmpty()) {
                try {
                    int taskId = Integer.parseInt(taskIdInput.trim());
                    new GraphicalEditView(controller, frame).show(taskId);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Nieprawidłowy format ID!", "Błąd", JOptionPane.ERROR_MESSAGE);
                }
            }
        }));
        buttonPanel.add(createStyledButton("Usuń zadanie", new Color(255, 102, 102, 204), new Color(204, 0, 0), e -> new GraphicalDeleteView(controller, frame).show()));

        JButton exitButton = createExitButton();

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(exitButton);

        panel.add(buttonPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void setLookAndFeel(boolean darkMode) {
        try {
            UIManager.setLookAndFeel(darkMode ? new FlatDarkLaf() : new FlatLightLaf());
            if (frame != null) {
                SwingUtilities.updateComponentTreeUI(frame);
            }
        } catch (UnsupportedLookAndFeelException e) {
            Logger.log("Błąd motywu", "Nie udało się załadować motywu");
        }
    }

    private void refreshView() {
        // Usuń starą zawartość
        frame.getContentPane().removeAll();

        // Dodaj nową zawartość
        mainPanel = createMainPanel();
        frame.add(mainPanel);

        // Zaktualizuj widok
        SwingUtilities.updateComponentTreeUI(frame);
        frame.repaint();
    }

    private JButton createStyledButton(String text, Color hoverColor, Color borderColor, java.awt.event.ActionListener action) {
        JButton button = new JButton(text);

        // Ustawienie kolorów w zależności od trybu
        Color backgroundColor = isDarkMode ? Color.DARK_GRAY : new Color(235, 235, 235); // Jasnoszary dla trybu jasnego
        Color foregroundColor = isDarkMode ? Color.WHITE : Color.BLACK;

        button.setBackground(backgroundColor);
        button.setForeground(foregroundColor);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(borderColor, 2));
        button.setContentAreaFilled(true);
        button.setOpaque(true);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(backgroundColor);
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

            Image scaledOriginalIcon = originalIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            Image scaledHoverIcon = hoverIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);

            exitButton.setIcon(new ImageIcon(scaledOriginalIcon));
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
        } catch (Exception e) {
            Logger.log("Błąd ładowania ikony", "Nie udało się załadować ikon wyłączania");
        }

        exitButton.setToolTipText("Zamknij aplikację");
        exitButton.setPreferredSize(new Dimension(50, 50));
        exitButton.setFocusPainted(false);
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
                JOptionPane.YES_NO_OPTION
        );

        if (confirmation == JOptionPane.YES_OPTION) {
            Logger.log("Zamknięcie aplikacji", "Aplikacja została zamknięta przez użytkownika");
            frame.dispose();
            System.exit(0);
        }
    }
}