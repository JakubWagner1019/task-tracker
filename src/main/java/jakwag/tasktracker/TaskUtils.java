package jakwag.tasktracker;

public class TaskUtils {

    private TaskUtils() {
    }

    static Task applyDefaults(Task task) {
        Task fixed = task;
        if(task.status() == null || task.status().isBlank()) {
            fixed = new Task(task.id(), task.title(), task.description(), Task.OPEN_STATUS);
        }
        return fixed;
    }

    static Task toTask(TaskEntity taskEntity) {
        return new Task(taskEntity.getId(), taskEntity.getTitle(), taskEntity.getDescription(), taskEntity.getStatus());
    }

    static TaskEntity toTaskEntity(Task task) {
        return new TaskEntity(task.id(), task.title(), task.description(), task.status());
    }
}
