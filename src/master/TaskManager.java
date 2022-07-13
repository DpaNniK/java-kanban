package master;

import task.Subtask;
import task.Task;

import java.util.List;

public interface TaskManager { //Интерфейс со всеми методами, которые могут пригодиться при работе с Task
    void createNewTask(String name, String description);

    void createNewEpic(String name, String description);

    void createNewSubtask(String name, String description, Integer idEpic);

    void addSubtaskInEpic(Subtask subtask, Integer id);

    void deleteSubtaskForEpic(Integer idEpic, Subtask subtask);

    List<Task> getHistory();

    void printSubtaskForEpic(Integer id);

    void printHistoryList();

    void deleteAllTaskList();

    void deleteTaskById(Integer id);

    void updateTask(Integer id, String name, String description);

    void printTaskById(Integer id);

    void startTask(Integer id);

    void finishTask(Integer id);

    void printAllTaskList();

    void printTaskList();

    void printEpicList();

    void printSubtaskList();
}
