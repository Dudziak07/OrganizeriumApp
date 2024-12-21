package view.graphical;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import controller.*;
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
        frame.setSize(850, 600);

        // Wyśrodkowanie okna na ekranie
        frame.setLocationRelativeTo(null);

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
            controller.switchToTextMode(appController); // Wywołanie metody w TaskController
        });


        JMenuItem switchTheme = new JMenuItem(appController.isDarkMode() ? "Przełącz na tryb jasny" : "Przełącz na tryb ciemny");
        switchTheme.addActionListener(e -> {
            appController.toggleDarkMode();
            Logger.log("Przełączenie motywu", appController.isDarkMode() ? "Ustawiono tryb ciemny" : "Ustawiono tryb jasny");
            setLookAndFeel(appController.isDarkMode());
            switchTheme.setText(appController.isDarkMode() ? "Przełącz na tryb jasny" : "Przełącz na tryb ciemny");
            refreshView();
        });

        JMenuItem toggleAnimations = new JMenuItem(appController.areAnimationsEnabled() ? "Wyłącz animacje" : "Włącz animacje");
        toggleAnimations.addActionListener(e -> {
            // Zmiana stanu animacji w AppController
            appController.toggleAnimations();
            toggleAnimations.setText(appController.areAnimationsEnabled() ? "Wyłącz animacje" : "Włącz animacje");
            Logger.log("Opcje", appController.areAnimationsEnabled() ? "Animacje włączone" : "Animacje wyłączone");

            // Zarządzanie animacjami przycisków
            if (appController.areAnimationsEnabled()) {
                enableAnimationsForButtons();
            } else {
                disableAnimationsForButtons();
            }
        });

        menu.add(toggleAnimations);

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
        JButton listTaskButton = createStyledButton("Wyświetl zadania", new Color(102, 178, 255, 179), new Color(102, 178, 255, 255), e -> new GraphicalListView(controller, frame, appController).show());
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
        addButtonWithAnimation(buttonPanel, addTaskButton);
        addButtonWithAnimation(buttonPanel, listTaskButton);
        addButtonWithAnimation(buttonPanel, editTaskButton);
        addButtonWithAnimation(buttonPanel, deleteTaskButton);
        addButtonWithAnimation(buttonPanel, exitButton);

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
        Color backgroundColor = appController.isDarkMode() ? Color.DARK_GRAY : new Color(240, 240, 240);
        Color buttonBackgroundColor = appController.isDarkMode() ? Color.DARK_GRAY : new Color(235, 235, 235);
        Color buttonForegroundColor = appController.isDarkMode() ? Color.WHITE : Color.BLACK;

        mainPanel.setBackground(backgroundColor);
        frame.getContentPane().setBackground(backgroundColor);

        buttons.forEach(button -> {
            button.setBackground(buttonBackgroundColor); // Ustawienie koloru tła
            button.setForeground(buttonForegroundColor); // Ustawienie koloru tekstu
            button.repaint(); // Odświeżanie wyglądu
        });

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    // Metoda do tworzenia stylizowanego przycisku
    private JButton createStyledButton(String text, Color hoverColor, Color pressedColor, ActionListener action) {
        JButton button = new JButton(text);

        // Kolory domyślne w zależności od trybu
        Color defaultBackground = appController.isDarkMode() ? Color.DARK_GRAY : new Color(235, 235, 235);
        Color defaultForeground = appController.isDarkMode() ? Color.WHITE : Color.BLACK;

        // Ustawienia przycisku
        button.setBackground(defaultBackground);
        button.setForeground(defaultForeground);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(hoverColor.darker(), 2));
        button.setContentAreaFilled(true);
        button.setOpaque(true);

        // Zapis oryginalnego rozmiaru i czcionki
        Font originalFont = button.getFont();
        Dimension originalSize = button.getPreferredSize();

        // Obsługa efektu hover
        button.addMouseListener(new MouseAdapter() {
            private final Color initialBackground = defaultBackground;

            @Override
            public void mouseEntered(MouseEvent e) {
                if (appController.areAnimationsEnabled()) {
                    // Powiększenie czcionki i rozmiaru przycisku
                    button.setFont(originalFont.deriveFont(originalFont.getSize() + 2f));
                    button.setPreferredSize(new Dimension(
                            (int) (originalSize.width * 1.1),
                            (int) (originalSize.height * 1.1)
                    ));
                }
                button.setBackground(hoverColor);
                button.revalidate();
                button.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Przywrócenie oryginalnej czcionki i rozmiaru przycisku
                button.setFont(originalFont);
                button.setPreferredSize(originalSize);

                // Przywrócenie tła na podstawie aktualnego trybu
                button.setBackground(appController.isDarkMode() ? Color.DARK_GRAY : new Color(235, 235, 235));
                button.revalidate();
                button.repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(pressedColor);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBackground(hoverColor);
            }
        });

        // Obsługa akcji
        button.addActionListener(action);

        return button;
    }

    private JButton createExitButton() {
        JButton exitButton = new JButton();
        try {
            ImageIcon originalIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/power_off.png")));
            ImageIcon hoverIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/power_off_mouse.png")));

            // Skalowanie ikon
            Image scaledOriginalIcon = originalIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
            Image scaledHoverIcon = hoverIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
            Image scaledHoverIconLarge = hoverIcon.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);

            exitButton.setIcon(new ImageIcon(scaledOriginalIcon));

            // Ustawienia przycisku dla ograniczenia granic aktywnych
            exitButton.setMargin(new Insets(0, 0, 0, 0)); // Brak marginesów
            exitButton.setBorder(BorderFactory.createEmptyBorder()); // Brak obramowania
            exitButton.setContentAreaFilled(false); // Brak wypełnienia tła
            exitButton.setFocusPainted(false); // Brak efektu fokusu
            exitButton.setPreferredSize(new Dimension(60, 60)); // Rozmiar zgodny z ikoną

            // Obsługa myszy (hover)
            exitButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (appController.areAnimationsEnabled()) {
                        exitButton.setIcon(new ImageIcon(scaledHoverIconLarge));
                        exitButton.setPreferredSize(new Dimension(70, 70));
                    } else {
                        exitButton.setIcon(new ImageIcon(scaledHoverIcon));
                    }
                    exitButton.revalidate();
                    exitButton.repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    exitButton.setIcon(new ImageIcon(scaledOriginalIcon));
                    exitButton.setPreferredSize(new Dimension(60, 60));
                    exitButton.revalidate();
                    exitButton.repaint();
                }
            });

            // Obsługa klawiatury (focus)
            exitButton.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (appController.areAnimationsEnabled()) {
                        exitButton.setIcon(new ImageIcon(scaledHoverIconLarge));
                        exitButton.setPreferredSize(new Dimension(70, 70));
                    } else {
                        exitButton.setIcon(new ImageIcon(scaledHoverIcon));
                    }
                    exitButton.revalidate();
                    exitButton.repaint();
                }

                @Override
                public void focusLost(FocusEvent e) {
                    exitButton.setIcon(new ImageIcon(scaledOriginalIcon));
                    exitButton.setPreferredSize(new Dimension(60, 60));
                    exitButton.revalidate();
                    exitButton.repaint();
                }
            });

        } catch (Exception e) {
            Logger.log("Błąd ładowania ikony", "Nie udało się załadować ikon wyłączania");
        }

        exitButton.setToolTipText("Zamknij aplikację");
        exitButton.addActionListener(e -> showExitConfirmation());

        return exitButton;
    }

    private void showExitConfirmation() {
        UIManager.put("OptionPane.yesButtonText", "Tak");
        UIManager.put("OptionPane.noButtonText", "Nie");
        UIManager.put("OptionPane.cancelButtonText", "Anuluj");

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

    private void addButtonWithAnimation(JPanel panel, JButton button) {
        if (appController.areAnimationsEnabled()) {
            button.setVisible(false); // Ukrycie na początku dla efektu animacji
            panel.add(button);

            Timer timer = new Timer(10, null);
            final float[] opacity = {0.0f};

            timer.addActionListener(e -> {
                opacity[0] += 0.05f; // Stopniowe zwiększanie widoczności
                if (opacity[0] > 1.0f) {
                    opacity[0] = 1.0f; // Ograniczenie maksymalnej widoczności
                    button.setVisible(true);
                    timer.stop(); // Zatrzymanie timera
                }
                // Ustaw przezroczystość, ale nie zmieniaj koloru
                button.setBackground(new Color(
                        button.getBackground().getRed(),
                        button.getBackground().getGreen(),
                        button.getBackground().getBlue(),
                        (int) (opacity[0] * 255) // Ustawienie przezroczystości
                ));
            });

            timer.start();
        } else {
            button.setVisible(true); // Natychmiastowe pojawienie się przycisku
            panel.add(button);
        }
    }

    private void refreshButtonAnimations() {
        buttons.forEach(button -> {
            if (appController.areAnimationsEnabled()) {
                AnimationController.fadeIn(button); // Fade-in dla włączonych animacji
            } else {
                button.setOpaque(true); // Wyłączenie animacji
                button.setBackground(button.getBackground()); // Przywrócenie koloru
                button.setVisible(true); // Natychmiastowe pojawienie się
            }
        });
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void enableAnimationsForButtons() {
        buttons.forEach(button -> {
            button.setVisible(false); // Ukryj przycisk przed animacją
            AnimationController.scaleIn(button); // Włącz efekt skalowania
        });
    }

    private void disableAnimationsForButtons() {
        buttons.forEach(button -> {
            button.setOpaque(true);
            button.setBackground(button.getBackground());
            button.setVisible(true); // Upewnij się, że są widoczne
            button.setPreferredSize(button.getPreferredSize()); // Przywróć oryginalny rozmiar
            button.revalidate(); // Odśwież układ
            button.repaint(); // Odśwież przyciski
        });
    }
}