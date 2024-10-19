package jakwag.tasktracker;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @GetMapping
    public ResponseEntity<Collection<Task>> getTasks(@RequestParam(name = "status", required = false) String status) {
        List<TaskEntity> all;
        if(status == null) {
            all = taskRepository.findAll();
        } else {
            all = taskRepository.findAllByStatus(status);
        }
        List<Task> tasks = all.stream().map(TaskController::toTask).toList();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTask(@PathVariable("id") long id) {
        Optional<TaskEntity> taskEntity = taskRepository.findById(id);
        Optional<Task> task = taskEntity.map(TaskController::toTask);
        if (task.isPresent()) {
            return ResponseEntity.ok(task.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Void> addTask(@RequestBody Task task) {
        TaskEntity save = taskRepository.save(toTaskEntity(task));
        Long id = save.getId();
        return ResponseEntity.status(201).header("Location", "/api/tasks/" + id).build();
    }

    @PutMapping
    public ResponseEntity<Void> updateTask(@RequestBody Task task) {
        taskRepository.save(toTaskEntity(task));
        return ResponseEntity.status(201).header("Location", "/api/tasks/" + task.id()).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable("id") long id) {
        taskRepository.deleteById(id);
        return ResponseEntity.status(204).header("Location", "/api/tasks/" + id).build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Task> patchTask(@PathVariable("id") long id, @RequestBody Task task) {
        Optional<TaskEntity> taskEntity = taskRepository.findById(id);
        if (taskEntity.isPresent()) {
            TaskEntity entity = taskEntity.get();
            if (task.title() != null) {
                entity.setTitle(task.title());
            }

            if (task.description() != null) {
                entity.setDescription(task.description());
            }

            if (task.status() != null) {
                entity.setStatus(task.status());
            }

            taskRepository.save(entity);
            Task modifiedTask = toTask(entity);
            return ResponseEntity.status(200).body(modifiedTask);
        }
        return ResponseEntity.notFound().build();
    }

    private static Task toTask(TaskEntity taskEntity) {
        return new Task(taskEntity.getId(), taskEntity.getTitle(), taskEntity.getDescription(), taskEntity.getStatus());
    }

    private static TaskEntity toTaskEntity(Task task) {
        return new TaskEntity(task.id(), task.title(), task.description(), task.status());
    }
}
