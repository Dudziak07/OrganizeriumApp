package model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class IdManager {
    private static final String ID_FILE = "currentId.json";
    private int currentId;

    public IdManager() {
        this.currentId = loadCurrentId();
    }

    public int getNextId() {
        currentId++;
        saveCurrentId();
        return currentId;
    }

    private int loadCurrentId() {
        try (FileReader reader = new FileReader(ID_FILE)) {
            JsonObject jsonObject = new Gson().fromJson(reader, JsonObject.class);
            return jsonObject.get("currentId").getAsInt();
        } catch (IOException e) {
            return 1; // Domyślna wartość, jeśli plik nie istnieje
        }
    }

    private void saveCurrentId() {
        try (FileWriter writer = new FileWriter(ID_FILE)) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("currentId", currentId);
            new Gson().toJson(jsonObject, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
