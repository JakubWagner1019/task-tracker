package jakwag.tasktracker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskController.class);

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
        List<Task> tasks = all.stream().map(TaskUtils::toTask).toList();
        LOGGER.trace("For status: {} returning list of tasks {}", status, tasks);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTask(@PathVariable("id") long id) {
        Optional<TaskEntity> taskEntity = taskRepository.findById(id);
        Optional<Task> task = taskEntity.map(TaskUtils::toTask);
        if (task.isPresent()) {
            LOGGER.trace("For id: {} returning task {}", id, task.get());
            return ResponseEntity.ok(task.get());
        } else {
            LOGGER.debug("For id: {} task not found", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Void> addTask(@RequestBody Task task) {
        LOGGER.info("Adding task {}", task);
        Task fixed = TaskUtils.applyDefaults(task);
        TaskEntity save = taskRepository.save(TaskUtils.toTaskEntity(fixed));
        Long id = save.getId();
        return ResponseEntity.status(201).header("Location", "/api/tasks/" + id).build();
    }

    @PutMapping
    public ResponseEntity<Void> updateTask(@RequestBody Task task) {
        LOGGER.info("Updating task {}", task);
        taskRepository.save(TaskUtils.toTaskEntity(task));
        return ResponseEntity.status(201).header("Location", "/api/tasks/" + task.id()).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable("id") long id) {
        LOGGER.info("Deleting task with ID: {}", id);
        taskRepository.deleteById(id);
        return ResponseEntity.status(204).header("Location", "/api/tasks/" + id).build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Task> patchTask(@PathVariable("id") long id, @RequestBody Task task) {
        LOGGER.info("Updating task with ID: {}. Update: {}", id, task);
        Optional<TaskEntity> taskEntity = taskRepository.findById(id);
        if (taskEntity.isPresent()) {
            TaskEntity entity = taskEntity.get();
            if (task.title() != null) {
                LOGGER.debug("Updating task with ID: {} with title: {}", id, task.title());
                entity.setTitle(task.title());
            }

            if (task.description() != null) {
                LOGGER.debug("Updating task with ID: {} with description: {}", id, task.description());
                entity.setDescription(task.description());
            }

            if (task.status() != null) {
                LOGGER.debug("Updating task with ID: {} with status: {}", id, task.status());
                entity.setStatus(task.status());
            }

            taskRepository.save(entity);
            Task modifiedTask = TaskUtils.toTask(entity);
            LOGGER.debug("For id: {} modified task: {}", id, modifiedTask);
            return ResponseEntity.status(200).body(modifiedTask);
        }
        return ResponseEntity.notFound().build();
    }

}
