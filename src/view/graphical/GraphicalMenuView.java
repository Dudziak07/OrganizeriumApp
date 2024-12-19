package view.graphical;

import com.formdev.flatlaf.FlatDarkLaf;
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
    private static boolean isInitialized = false; // Flaga do kontrolowania logowania

    public GraphicalMenuView(TaskController controller) {
        this.controller = controller;

        // Ustawienie trybu ciemnego
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (UnsupportedLookAndFeelException e) {
            Logger.log("Błąd motywu", "Nie udało się załadować motywu FlatDarkLaf");
            e.printStackTrace();
        }

        this.frame = new JFrame("OrganizeriumApp - Menu Główne");

        // Logowanie tylko przy pierwszym uruchomieniu
        if (!isInitialized) {
            Logger.log("Uruchomienie aplikacji", "Zainicjowano tryb graficzny");
            isInitialized = true; // Ustawienie flagi
        }
    }

    public void show() {
        // Konfiguracja głównego okna
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                showExitConfirmation();
            }
        });

        // Pasek menu z opcją przełączania na tryb tekstowy
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Opcje");
        JMenuItem switchToTextMode = new JMenuItem("Przełącz na tryb tekstowy");
        switchToTextMode.addActionListener(e -> {
            Logger.log("Przełączanie trybu", "Przełączono na tryb tekstowy");
            frame.dispose();
            new Thread(() -> new MainMenuView(controller).show()).start();
        });
        menu.add(switchToTextMode);
        menuBar.add(menu);
        frame.setJMenuBar(menuBar);

        // Główna sekcja menu
        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // Dodanie przycisków z mechanizmem zmiany koloru
        panel.add(createStyledButton("Dodaj zadanie", new Color(102, 255, 102, 204), new Color(51, 153, 51), e -> new GraphicalAddView(controller, frame).show()));
        panel.add(createStyledButton("Wyświetl zadania", new Color(102, 178, 255, 204), new Color(0, 102, 204), e -> new GraphicalListView(controller, frame).show()));
        panel.add(createStyledButton("Edytuj zadanie", new Color(255, 204, 102, 204), new Color(255, 153, 51), e -> {
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
        panel.add(createStyledButton("Usuń zadanie", new Color(255, 102, 102, 204), new Color(204, 0, 0), e -> new GraphicalDeleteView(controller, frame).show()));

        // Dodanie przycisku wyłączania z ikoną w prawym dolnym rogu
        // Dodanie przycisku wyłączania z ikoną w prawym dolnym rogu
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
                    exitButton.setIcon(new ImageIcon(scaledHoverIcon)); // Zmiana ikony na hover
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    exitButton.setIcon(new ImageIcon(scaledOriginalIcon)); // Przywrócenie oryginalnej ikony
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

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(exitButton);

        // Dodanie panelu do okna
        frame.add(panel, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        // Wyświetlenie okna
        frame.setVisible(true);
    }

    // Metoda do tworzenia stylizowanego przycisku
    private JButton createStyledButton(String text, Color hoverColor, Color borderColor, java.awt.event.ActionListener action) {
        JButton button = new JButton(text);
        button.setBackground(Color.DARK_GRAY);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(borderColor, 2)); // Dodanie cieniutkiej ramki
        button.setContentAreaFilled(true);
        button.setOpaque(true);

        // Tworzenie efektu hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(borderColor, 2), // Ramka zewnętrzna
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)     // Margines wewnętrzny
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.DARK_GRAY);
                button.setBorder(BorderFactory.createLineBorder(borderColor, 2));
            }
        });

        button.addActionListener(action);
        return button;
    }

    // Metoda wyświetlająca potwierdzenie wyjścia z aplikacji
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
