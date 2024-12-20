package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static final String CONFIG_FOLDER = "config";
    private static final String LOG_FILE = CONFIG_FOLDER + "/log.json";
    private static final String ID_FILE = CONFIG_FOLDER + "/currentId.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static int logCounter = loadLogCounter();

    public static void log(String action, String details) {
        ensureConfigFolderExists();

        JsonObject logEntry = new JsonObject();
        logEntry.addProperty("id", ++logCounter);
        logEntry.addProperty("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        logEntry.addProperty("action", action);
        logEntry.addProperty("details", details);

        JsonArray logArray = loadLogFile();
        logArray.add(logEntry);

        saveLogFile(logArray);
        saveLogCounter();

        System.out.println("#" + logEntry.get("id").getAsInt() + " " + action + ": " + details);
    }

    private static JsonArray loadLogFile() {
        try (FileReader reader = new FileReader(LOG_FILE)) {
            return gson.fromJson(reader, JsonArray.class);
        } catch (IOException e) {
            return new JsonArray();
        }
    }

    private static void saveLogFile(JsonArray logArray) {
        try (FileWriter writer = new FileWriter(LOG_FILE)) {
            gson.toJson(logArray, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static {
        ensureConfigFolderExists();
        // Inicjalizacja logowania
    }

    private static void ensureConfigFolderExists() {
        File folder = new File(CONFIG_FOLDER);
        if (!folder.exists()) {
            folder.mkdir();
        }
    }

    // Wczytanie licznika logów z currentId.json
    private static int loadLogCounter() {
        try (FileReader reader = new FileReader(ID_FILE)) {
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            if (jsonObject != null && jsonObject.has("logCounter")) {
                return jsonObject.get("logCounter").getAsInt();
            }
        } catch (IOException e) {
            System.out.println("Plik currentId.json nie istnieje. Tworzenie nowego.");
        }
        return 0;
    }

    // Zapisanie aktualnego licznika logów do currentId.json
    private static void saveLogCounter() {
        JsonObject jsonObject;
        try (FileReader reader = new FileReader(ID_FILE)) {
            jsonObject = gson.fromJson(reader, JsonObject.class);
            if (jsonObject == null) {
                jsonObject = new JsonObject();
            }
        } catch (IOException e) {
            jsonObject = new JsonObject();
        }

        jsonObject.addProperty("logCounter", logCounter);

        try (FileWriter writer = new FileWriter(ID_FILE)) {
            gson.toJson(jsonObject, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
