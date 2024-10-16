package jakwag.tasktracker;

public record Task(long id, String title, String description, String status) {
    public Task withId(long id) {
        return new Task(id, title, description, status);
    }
}
