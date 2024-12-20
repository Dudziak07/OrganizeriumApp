package controller;

import model.IdManager;
import model.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TaskController {
    private static TaskController instance; // Statyczna instancja klasy (Singleton)
    private final List<Task> tasks = new ArrayList<>();
    private static final String CONFIG_FOLDER = "config";
    private static final String FILE_NAME = CONFIG_FOLDER + "/tasks.json";
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final IdManager idManager = new IdManager();

    // Prywatny konstruktor – Singleton
    private TaskController() {
        loadTasks();
        ensureConfigFolderExists();
    }

    // Publiczna metoda zwracająca jedyną instancję klasy
    public static TaskController getInstance() {
        if (instance == null) {
            instance = new TaskController();
        }
        return instance;
    }

    // Dodanie zadania
    public void addTask(Task task) {
        tasks.add(task);
        saveTasks();
        Logger.log("Dodano zadanie", String.format(
                "id:[%d] (%s), Kategoria: %s, Priorytet: %s, Termin: %s, Utworzone: %s",
                task.getId(), task.getName(), task.getCategory(), task.getPriority(),
                task.getDeadline(), task.getCreationTime()
        ));
    }

    // Zwrócenie listy zadań
    public List<Task> getTasks() {
        return tasks;
    }

    public String getTaskDetails(Task task) {
        return String.format(
                "ID: %d\nNazwa: %s\nKategoria: %s\nPriorytet: %s\nTermin: %s\nData utworzenia: %s",
                task.getId(), task.getName(), task.getCategory(), task.getPriority(),
                task.getDeadline(), task.getCreationTime()
        );
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

    public Task getTaskById(int id) {
        return tasks.stream()
                .filter(task -> task.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public boolean editTask(int id, String newName, String newCategory, String newDeadline, String newPriority) {
        Task task = getTaskById(id);
        if (task != null) {
            if (newName != null && !newName.isBlank()) task.setName(newName);
            if (newCategory != null) task.setCategory(newCategory);
            if (newDeadline != null) task.setDeadline(newDeadline);
            if (newPriority != null) task.setPriority(newPriority);
            saveTasks(); // Aktualizacja pliku tasks.json
            return true;
        }
        return false;
    }

    public Task getTaskByIdOrName(String input) {
        try {
            int id = Integer.parseInt(input);
            return tasks.stream().filter(task -> task.getId() == id).findFirst().orElse(null);
        } catch (NumberFormatException e) {
            return tasks.stream().filter(task -> task.getName().equalsIgnoreCase(input)).findFirst().orElse(null);
        }
    }

    public boolean deleteTask(Task task) {
        if (tasks.remove(task)) {
            saveTasks();
            Logger.log("Usunięto zadanie", String.format("ID: %d, Nazwa: %s", task.getId(), task.getName()));
            return true;
        }
        return false;
    }

    public int getNextId() {
        return idManager.getNextId();
    }

    private void ensureConfigFolderExists() {
        File folder = new File(CONFIG_FOLDER);
        if (!folder.exists()) {
            folder.mkdir();
        }
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

    public boolean editTaskById(int id, String newName, String newCategory, String newDeadline, String newPriority) {
        Task taskToEdit = tasks.stream().filter(task -> task.getId() == id).findFirst().orElse(null);
        if (taskToEdit != null) {
            editTaskFields(taskToEdit, newName, newCategory, newDeadline, newPriority);
            saveTasks();
            Logger.log("Edytowano zadanie", "id:[" + taskToEdit.getId() + "] (" + taskToEdit.getName() + ")");
            return true;
        }
        return false;
    }

    public boolean editTaskByName(String name, String newName, String newCategory, String newDeadline, String newPriority) {
        Task taskToEdit = tasks.stream().filter(task -> task.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
        if (taskToEdit != null) {
            editTaskFields(taskToEdit, newName, newCategory, newDeadline, newPriority);
            saveTasks();
            Logger.log("Edytowano zadanie", "id:[" + taskToEdit.getId() + "] (" + taskToEdit.getName() + ")");
            return true;
        }
        return false;
    }

    // Prywatna metoda pomocnicza do edycji pól zadania
    private void editTaskFields(Task task, String newName, String newCategory, String newDeadline, String newPriority) {
        if (newName != null && !newName.isEmpty()) {
            task.setName(newName);
        }
        if (newCategory != null && !newCategory.isEmpty()) {
            task.setCategory(newCategory);
        }
        if (newDeadline != null && !newDeadline.isEmpty()) {
            task.setDeadline(newDeadline);
        }
        if (newPriority != null && !newPriority.isEmpty()) {
            task.setPriority(newPriority);
        }
    }

    public List<Task> filterTasks(String priority, String category, String name, String creationDate, String deadline) {
        return tasks.stream()
                .filter(task -> "Wszystkie".equals(priority) || task.getPriority().equalsIgnoreCase(priority))
                .filter(task -> "Wszystkie".equals(category) || task.getCategory().equalsIgnoreCase(category))
                .filter(task -> name == null || name.isEmpty() || task.getName().toLowerCase().contains(name.toLowerCase()))
                .filter(task -> creationDate == null || creationDate.isEmpty() || task.getCreationTime().startsWith(creationDate))
                .filter(task -> deadline == null || deadline.isEmpty() || task.getDeadline().startsWith(deadline))
                .collect(Collectors.toList());
    }

    public List<Task> sortTasks(String criteria, boolean ascending) {
        Comparator<Task> comparator = switch (criteria) {
            case "ID" -> Comparator.comparing(Task::getId);
            case "Nazwa" -> Comparator.comparing(Task::getName);
            case "Kategoria" -> Comparator.comparing(Task::getCategory);
            case "Priorytet" -> Comparator.comparing(Task::getPriority);
            case "Termin" -> Comparator.comparing(task -> {
                try {
                    return LocalDate.parse(task.getDeadline());
                } catch (Exception e) {
                    return LocalDate.MIN; // Nieprawidłowa data trafia na koniec listy
                }
            });
            default -> Comparator.comparing(task -> {
                try {
                    return LocalDateTime.parse(task.getCreationTime());
                } catch (Exception e) {
                    return LocalDateTime.MIN; // Nieprawidłowa data trafia na koniec listy
                }
            });
        };

        if (!ascending) comparator = comparator.reversed();
        return tasks.stream().sorted(comparator).collect(Collectors.toList());
    }
}
