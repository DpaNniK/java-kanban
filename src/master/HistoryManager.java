package master;

import task.Task;

import java.util.List;

public interface HistoryManager { //Интерфейс для работы с историей просмотров

    List<Task> getHistory();

    void addTaskInHistory(Task task);
}
