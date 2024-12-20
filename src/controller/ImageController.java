package controller;

import javax.swing.*;
import java.awt.*;

public class ImageController {

    // Funkcja skalująca ikonę
    public static ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImg);
    }
}
