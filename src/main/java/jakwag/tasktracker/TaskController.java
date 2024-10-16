package jakwag.tasktracker;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final AtomicLong counter = new AtomicLong();
    private final Map<Long, Task> tasks = Collections.synchronizedMap(new HashMap<>());

    @GetMapping
    public ResponseEntity<Collection<Task>> getTasks() {
        Collection<Task> values;
        synchronized (tasks) {
            values = new ArrayList<>(tasks.values());
        }
        return ResponseEntity.ok(values);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTask(@PathVariable("id") long id) {
        Task task;
        synchronized (tasks) {
            task = tasks.get(id);
        }
        return ResponseEntity.ok(task);
    }

    @PostMapping
    public ResponseEntity<Void> addTask(@RequestBody Task task) {
        long id = counter.incrementAndGet();
        Task taskWithId = task.withId(id);
        synchronized (tasks) {
            tasks.put(id, taskWithId);
        }
        return ResponseEntity.status(201).header("Location","/api/tasks/" + id).build();
    }

    @PutMapping
    public ResponseEntity<Void> updateTask(@RequestBody Task task) {
        synchronized (tasks) {
            tasks.put(task.id(), task);
        }
        return ResponseEntity.status(201).header("Location","/api/tasks/" + task.id()).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable("id") long id) {
        synchronized (tasks) {
            tasks.remove(id);
        }
        return ResponseEntity.status(204).header("Location","/api/tasks/" + id).build();
    }
}
