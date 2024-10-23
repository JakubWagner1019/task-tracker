package jakwag.tasktracker;

public record Task(long id, String title, String description, String status) {
    public static final String OPEN_STATUS = "Open";
}
