package master;

import task.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager { //Класс, реализующий интерфес HistoryManager
    private final List<Task> historyList;
    private final static int MAX_SIZE_HISTORY_LIST = 10; //Const. с ограничением размера для HistoryList

    public InMemoryHistoryManager() {
        this.historyList = new ArrayList<>();
    }

    @Override
    public List<Task> getHistory() {
        return historyList;
    }

    @Override
    public void addTaskInHistory(Task task) { //Добавляю в лист запись на место первого элемента, когда лист заполнится
        if (historyList.size() < MAX_SIZE_HISTORY_LIST) { // последний элемент будет самым давним, он будет удаляться
            historyList.add(0, task);
        } else {
            historyList.remove(MAX_SIZE_HISTORY_LIST - 1);
            historyList.add(0, task);
        }
    }
}
