package controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class AppController {
    private static final String CONFIG_FOLDER = "config";
    private static final String CONFIG_FILE = CONFIG_FOLDER + "/options.json";

    private boolean isDarkMode;

    public AppController() {
        // Wczytaj konfigurację z pliku
        loadConfig();
    }

    public boolean isDarkMode() {
        return isDarkMode;
    }

    public void toggleDarkMode() {
        isDarkMode = !isDarkMode;
        saveConfig("isDarkMode", Boolean.toString(isDarkMode));
    }

    private void loadConfig() {
        try {
            File folder = new File(CONFIG_FOLDER);
            File file = new File(CONFIG_FILE);

            if (!folder.exists()) {
                folder.mkdir();
            }

            if (!file.exists()) {
                createDefaultConfig();
            }

            FileReader reader = new FileReader(file);
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            if (jsonObject.has("isDarkMode")) {
                isDarkMode = jsonObject.get("isDarkMode").getAsBoolean();
            } else {
                createDefaultConfig();
            }
            reader.close();
        } catch (Exception e) {
            Logger.log("Błąd konfiguracji", "Nie udało się wczytać pliku konfiguracyjnego: " + e.getMessage());
            createDefaultConfig();
        }
    }

    public void saveConfig(String key, String value) {
        try {
            // Jeśli plik istnieje, wczytaj istniejący JSON
            JsonObject jsonObject;
            File file = new File(CONFIG_FILE);
            if (file.exists()) {
                FileReader reader = new FileReader(file);
                jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
                reader.close();
            } else {
                jsonObject = new JsonObject();
            }

            // Zaktualizuj dane i zapisz
            jsonObject.add(key, new JsonPrimitive(value));
            FileWriter writer = new FileWriter(CONFIG_FILE);
            writer.write(jsonObject.toString());
            writer.close();
        } catch (Exception e) {
            Logger.log("Błąd konfiguracji", "Nie udało się zapisać pliku konfiguracyjnego: " + e.getMessage());
        }
    }

    private void createDefaultConfig() {
        isDarkMode = true; // Domyślny tryb to ciemny
        saveConfig("isDarkMode", Boolean.toString(isDarkMode));
    }
}