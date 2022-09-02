package master;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public interface TaskManager { //Интерфейс со всеми методами, которые могут пригодиться при работе с Task
    void createNewTask(String name, String description);

    void createNewEpic(String name, String description);

    void createNewSubtask(String name, String description, Integer idEpic);

    void addSubtaskInEpic(Subtask subtask, Integer id);

    void deleteSubtaskForEpic(Integer idEpic, Subtask subtask);

    void setTimeForTask(int id, String localDateTime, int duration);

    Set<Task> getPrioritizedTasks();

    List<Task> getHistory();

    List<Subtask> getSubtaskForEpic(Integer id);

    void printHistoryList();

    void deleteAllTaskList();

    void deleteTaskById(Integer id);

    void updateTask(Integer id, String name, String description);

    Task getTaskById(Integer id);

    void printTaskById(Integer id);

    void startTask(Integer id);

    void finishTask(Integer id);

    HashMap<Integer, Task> getAllTaskList();

    HashMap<Integer, Task> getTaskList();

    HashMap<Integer, Epic> getEpicList();

    HashMap<Integer, Subtask> getSubtaskList();
}
