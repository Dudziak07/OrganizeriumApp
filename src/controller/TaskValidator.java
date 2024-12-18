package controller;

public class TaskValidator {
    public static boolean validateName(String name) {
        return name != null && !name.isEmpty() && name.length() <= 20;
    }

    public static boolean validateCategory(String category) {
        return category == null || category.length() <= 20;
    }

    public static boolean validateDateFormat(String date) {
        return date.isEmpty() || date.matches("\\d{4}-\\d{2}-\\d{2}");
    }
}