package model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final int id;
    private String name;
    private String category;
    private String deadline;
    private String priority;
    private final String creationTime;  // Nowe pole przechowujące datę i godzinę utworzenia

    public Task(int id, String name, String category, String deadline, String priority) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.deadline = deadline;
        this.priority = priority;
        this.creationTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); // Ustawia datę i godzinę
    }

    // Gettery
    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getDeadline() { return deadline; }
    public String getPriority() { return priority; }
    public String getCreationTime() { return creationTime; }  // Getter dla daty i godziny

    // Settery
    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}
