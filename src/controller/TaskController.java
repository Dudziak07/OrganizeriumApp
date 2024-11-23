package controller;

import model.IdManager;
import model.Task;
import controller.Logger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class TaskController {
    private final List<Task> tasks = new ArrayList<>();
    private static final String FILE_NAME = "tasks.json";
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();  // Ustawienie Pretty Printing
    private final IdManager idManager = new IdManager();

    public TaskController() {
        loadTasks();
    }

    public void addTask(Task task) {
        tasks.add(task);
        saveTasks();

        Logger.log("Dodano zadanie", String.format(
                "id:[%d] (%s), Kategoria: %s, Priorytet: %s, Termin: %s, Utworzone: %s",
                task.getId(), task.getName(), task.getCategory(), task.getPriority(),
                task.getDeadline(), task.getCreationTime()
        ));
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public boolean removeTaskById(int id) {
        Task taskToRemove = tasks.stream().filter(task -> task.getId() == id).findFirst().orElse(null);
        if (taskToRemove != null) {
            tasks.remove(taskToRemove);
            saveTasks();
            Logger.log("Usunięto zadanie", "id:[" + taskToRemove.getId() + "] (" + taskToRemove.getName() + ")");
            return true;
        }
        return false;
    }

    public boolean removeTaskByName(String name) {
        Task taskToRemove = tasks.stream().filter(task -> task.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
        if (taskToRemove != null) {
            tasks.remove(taskToRemove);
            saveTasks();
            Logger.log("Usunięto zadanie", "id:[" + taskToRemove.getId() + "] (" + taskToRemove.getName() + ")");
            return true;
        }
        return false;
    }

    public void removeAllTasks() {
        tasks.clear();
        saveTasks();
        Logger.log("Usunięto wszystkie zadania", "Wyczyszczono listę zadań");
    }

    public int getNextId() {
        return idManager.getNextId();
    }

    private void saveTasks() {
        try (Writer writer = new FileWriter(FILE_NAME)) {
            gson.toJson(tasks, writer);  // Pretty Printing dla tasks.json
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTasks() {
        try (Reader reader = new FileReader(FILE_NAME)) {
            Type taskListType = new TypeToken<List<Task>>() {}.getType();
            List<Task> loadedTasks = gson.fromJson(reader, taskListType);
            if (loadedTasks != null) {
                tasks.addAll(loadedTasks);
            }
        } catch (FileNotFoundException e) {
            Logger.log("Brak pliku zadań", "Tworzenie nowej listy zadań");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getTasksWithDeadlineInfo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate currentDate = LocalDate.now();

        return tasks.stream()
                .map(task -> {
                    String deadlineInfo;
                    try {
                        LocalDate deadlineDate = LocalDate.parse(task.getDeadline(), formatter);
                        long days = ChronoUnit.DAYS.between(currentDate, deadlineDate);
                        deadlineInfo = days >= 0 ? String.format("%s (%dd)", task.getDeadline(), days)
                                : String.format("%s (%dd)", task.getDeadline(), days);
                    } catch (Exception e) {
                        deadlineInfo = task.getDeadline() + " (nieprawidłowy termin)";
                    }
                    return String.format("ID: %d, Nazwa: %s, Priorytet: %s, Kategoria: %s, Termin: %s, Data utworzenia: %s",
                            task.getId(), task.getName(), task.getPriority(), task.getCategory(), deadlineInfo, task.getCreationTime());
                })
                .toList();
    }

}
