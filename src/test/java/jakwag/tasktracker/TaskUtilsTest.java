package jakwag.tasktracker;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskUtilsTest {

    @Test
    void shouldSetStatusToOpen_whenStatusInNull() {
        Task task = new Task(0, "", "", null);
        Task afterProcessing = TaskUtils.applyDefaults(task);
        String actual = afterProcessing.status();
        assertEquals(Task.OPEN_STATUS, actual);
    }

    @Test
    void shouldSetStatusToOpen_whenStatusInEmptyString() {
        Task task = new Task(0, "", "", "");
        Task afterProcessing = TaskUtils.applyDefaults(task);
        String actual = afterProcessing.status();
        assertEquals(Task.OPEN_STATUS, actual);
    }

    @Test
    void shouldSetStatusToOpen_whenStatusInBlankString() {
        Task task = new Task(0, "", "", "   ");
        Task afterProcessing = TaskUtils.applyDefaults(task);
        String actual = afterProcessing.status();
        assertEquals(Task.OPEN_STATUS, actual);
    }

    @Test
    void shouldHaveAllFieldsEqual_whenConvertingFromTaskToTaskEntity() {
        Task task = new Task(123, "My title", "Some description", "Some status");
        TaskEntity taskEntity = TaskUtils.toTaskEntity(task);
        assertEquals(task.id(), taskEntity.getId());
        assertEquals(task.title(), taskEntity.getTitle());
        assertEquals(task.description(), taskEntity.getDescription());
        assertEquals(task.status(), taskEntity.getStatus());
    }

    @Test
    void shouldHaveAllFieldsEqual_whenConvertingFromTaskEntityToTask() {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(123L);
        taskEntity.setTitle("My title");
        taskEntity.setDescription("Some description");
        taskEntity.setStatus(Task.OPEN_STATUS);
        Task task = TaskUtils.toTask(taskEntity);
        assertEquals(task.id(), taskEntity.getId());
        assertEquals(task.title(), taskEntity.getTitle());
        assertEquals(task.description(), taskEntity.getDescription());
        assertEquals(task.status(), taskEntity.getStatus());
    }
}
