package controller;

import javax.swing.*;
import java.awt.*;

public class AnimationController {

    public static void fadeIn(JComponent component) {
        component.setVisible(true); // Upewnij się, że komponent jest widoczny
        Timer timer = new Timer(20, null);
        final float[] alpha = {0.0f};

        timer.addActionListener(e -> {
            alpha[0] += 0.05f; // Zwiększ przezroczystość
            if (alpha[0] >= 1.0f) {
                alpha[0] = 1.0f;
                timer.stop();
            }
            setAlpha(component, alpha[0]);
        });

        timer.start();
    }

    public static void fadeOut(JComponent component) {
        Timer timer = new Timer(20, null);
        final float[] alpha = {1.0f};

        timer.addActionListener(e -> {
            alpha[0] -= 0.05f; // Zmniejsz przezroczystość
            if (alpha[0] <= 0.0f) {
                alpha[0] = 0.0f;
                component.setVisible(false);
                timer.stop();
            }
            setAlpha(component, alpha[0]);
        });

        timer.start();
    }

    private static void setAlpha(JComponent component, float alpha) {
        component.putClientProperty("alpha", alpha); // Ustawienie właściwości klienta

        // Wyzwolenie ponownego malowania
        component.repaint();
    }

    public static void slideIn(Component component, Container parent) {
        Timer timer = new Timer(10, null);
        int startX = parent.getWidth(); // Startowa pozycja (poza ekranem)
        int targetX = component.getX(); // Docelowa pozycja

        component.setVisible(true);
        component.setLocation(startX, component.getY()); // Ustaw startową pozycję

        timer.addActionListener(e -> {
            int currentX = component.getX();
            int step = Math.max((currentX - targetX) / 5, 1); // Krok zmniejszania odległości
            component.setLocation(currentX - step, component.getY());

            if (currentX <= targetX) {
                component.setLocation(targetX, component.getY());
                timer.stop();
            }
        });
        timer.start();
    }

    public static void scaleIn(JComponent component) {
        Timer timer = new Timer(30, null);
        float[] scale = {0.1f}; // Początkowy współczynnik skali

        Dimension originalSize = component.getPreferredSize();
        Point originalLocation = component.getLocation(); // Pobierz pozycję

        // Ukryj komponent na czas przygotowania animacji
        component.setVisible(false);

        timer.addActionListener(e -> {
            scale[0] += 0.02f; // Zwiększ skalę o 5% co krok

            if (scale[0] >= 1.0f) {
                scale[0] = 1.0f;
                timer.stop(); // Zatrzymaj animację po pełnym skalowaniu
            }

            // Oblicz nowy rozmiar i pozycję
            int width = (int) (originalSize.width * scale[0]);
            int height = (int) (originalSize.height * scale[0]);
            int x = originalLocation.x + (originalSize.width - width) / 2;
            int y = originalLocation.y + (originalSize.height - height) / 2;

            // Ustaw nowe wymiary i lokalizację
            component.setBounds(x, y, width, height);

            // Wymuś ponowne renderowanie
            component.revalidate();
            component.repaint();

            // Pokaż komponent po pierwszym kroku
            if (!component.isVisible()) {
                component.setVisible(true);
            }
        });
        timer.start();
    }
}