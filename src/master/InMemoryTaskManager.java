package master;

import task.*;

import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> taskList;
    private final HashMap<Integer, Epic> epicList;
    private final HashMap<Integer, Subtask> subtaskList;
    private final HashMap<Integer, Task> allTaskList;
    HistoryManager historyManager = Managers.getDefaultHistory(); //Создаю объект, хранящий историю, на основе Интерфейса

    private static Integer id = 0;
    private Integer numOfSubtaskForEpic = 0;
    Task task;
    Epic epic;
    Subtask subtask;

    public InMemoryTaskManager() {
        this.taskList = new HashMap<>();
        this.epicList = new HashMap<>();
        this.subtaskList = new HashMap<>();
        this.allTaskList = new HashMap<>();
    }

    @Override
    public void createNewTask(String name, String description) {
        task = new Task(name, description);
        task.setId(id);
        taskList.put(id, task);
        allTaskList.put(id, task);
        id++;
    }

    @Override
    public void createNewEpic(String name, String description) {
        epic = new Epic(name, description);
        epic.setId(id);
        epicList.put(id, epic);
        allTaskList.put(id, epic);
        id++;
    }

    @Override
    public void createNewSubtask(String name, String description, Integer idEpic) {
        if (allTaskList.get(idEpic).getTypeOfTask().equals(Type.EPIC)) {
            subtask = new Subtask(name, description, idEpic);
            subtask.setId(id);
            subtaskList.put(id, subtask);
            allTaskList.put(id, subtask);
            addSubtaskInEpic(subtask, idEpic);
            id++;
        } else {
            System.out.println("Эпика под таким номером нет");
        }
    }

    @Override
    public void addSubtaskInEpic(Subtask subtask, Integer id) {
        epicList.get(id).putSubtaskForEpic(subtask);
    }

    @Override
    public void deleteSubtaskForEpic(Integer idEpic, Subtask subtask) {
        epicList.get(idEpic).removeSubtaskForEpic(subtask);
    }

    @Override
    public void printSubtaskForEpic(Integer id) {
        if (epicList.containsKey(id)) {
            System.out.println(epicList.get(id));
            for (Integer num : epicList.get(id).getSubtaskListForEpic().keySet()) {
                System.out.println(subtaskList.get(num));
            }
        } else {
            System.out.println("Эпика под таким номером нет");
        }
    }

    @Override
    public void deleteAllTaskList() {
        allTaskList.clear();
        taskList.clear();
        epicList.clear();
        subtaskList.clear();
    }

    //Переделано) Выглядит действительно более изящно :) Также поправил другие методы, где использовался getClass()
    @Override
    public void deleteTaskById(Integer id) {
        if (allTaskList.containsKey(id)) {
            switch (allTaskList.get(id).getTypeOfTask()) {
                case EPIC:
                    for (Integer num : epicList.get(id).getSubtaskListForEpic().keySet()) {
                        historyManager.remove(num); //удаление подзадач эпика из истории
                        subtaskList.remove(num);
                    }
                    epicList.remove(id);
                    allTaskList.remove(id);
                    historyManager.remove(id); //удаление эпика из истории
                    break;

                case SUBTASK:
                    deleteSubtaskForEpic(subtaskList.get(id).getIdEpic(), subtaskList.get(id));
                    if (checkStatusSubtaskForEpic(subtaskList.get(id).getIdEpic())) {
                        epicList.get(subtaskList.get(id).getIdEpic()).
                                setTaskStatus(subtaskList.get(id).getTaskStatus());
                    }
                    if (checkOnNewStatusSubtaskList(subtaskList.get(id).getIdEpic())) {
                        epicList.get(subtaskList.get(id).getIdEpic()).
                                setTaskStatus(Status.NEW);
                    }
                    if (checkOnLastSubtaskInEpic(subtaskList.get(id).getIdEpic())) {
                        epicList.get(subtaskList.get(id).getIdEpic()).
                                setTaskStatus(getLastStatusOfSubtaskForEpic(subtaskList.get(id).getIdEpic()));
                    }
                    if (checkOnNullSubtaskListForEpic(subtaskList.get(id).getIdEpic())) {
                        task = new Task(epicList.get(subtaskList.get(id).getIdEpic()).getName(),
                                epicList.get(subtaskList.get(id).getIdEpic()).getDescription());
                        task.setId(subtaskList.get(id).getIdEpic());
                        taskList.put(subtaskList.get(id).getIdEpic(), task);
                        allTaskList.put(subtaskList.get(id).getIdEpic(), task);
                        epicList.remove(subtaskList.get(id).getIdEpic());
                    }
                    subtaskList.remove(id);
                    allTaskList.remove(id);
                    historyManager.remove(id); // удаление подзадачи из истории
                    break;

                case TASK:
                    taskList.remove(id);
                    allTaskList.remove(id);
                    historyManager.remove(id); // удаление задачи из истории
                    break;
            }
        } else {
            System.out.println("Задачи под таким номером нет");
        }
    }

    @Override
    public void updateTask(Integer id, String name, String description) {
        if (allTaskList.containsKey(id)) {
            switch (allTaskList.get(id).getTypeOfTask()) {
                case SUBTASK:
                    subtask = new Subtask(name, description, subtaskList.get(id).getIdEpic());
                    subtask.setId(id);
                    subtaskList.put(id, subtask);
                    allTaskList.put(id, subtask);
                    addSubtaskInEpic(subtask, subtaskList.get(id).getIdEpic());
                    if (checkOnNewStatusSubtaskList(subtaskList.get(id).getIdEpic())) {
                        epicList.get(subtaskList.get(id).getIdEpic()).createNewTask();
                    } else {
                        epicList.get(subtaskList.get(id).getIdEpic()).startTask();
                    }
                    break;

                case TASK:
                    task = new Task(name, description);
                    task.setId(id);
                    taskList.put(id, task);
                    allTaskList.put(id, task);
                    taskList.get(id).createNewTask();
                    break;

                case EPIC:
                    epic = new Epic(name, description);
                    epic.setId(id);
                    epicList.put(id, epic);
                    allTaskList.put(id, epic);
                    break;
            }
        } else {
            System.out.println("Задачи под атким номером нет");
        }
    }

    @Override
    public Task getTaskById(Integer id) { //Пользователь запрашивает получение задачи
        if (allTaskList.containsKey(id)) { //Происходит проверка, есть ли она в списке
            addTaskInHistory(allTaskList.get(id)); //Просмотренная задача добавляется в список истории
            return allTaskList.get(id);
        } else {
            System.out.println("Задачи под таким номером нет.");
            return null;
        }
    }

    @Override
    public void printTaskById(Integer id) {
        System.out.println(getTaskById(id));
    }

    @Override
    public void startTask(Integer id) {
        if (allTaskList.containsKey(id)) {
            if (!allTaskList.get(id).getTypeOfTask().equals(Type.EPIC)) {
                if (allTaskList.get(id).getTypeOfTask().equals(Type.TASK)) {
                    taskList.get(id).startTask();
                } else {
                    subtaskList.get(id).startTask();
                    epicList.get(subtaskList.get(id).getIdEpic()).startTask();
                }
            } else {
                System.out.println("Статус эпика изменить нельзя");
            }
        } else {
            System.out.println("Задачи под таким номером нет");
        }
    }

    @Override
    public void finishTask(Integer id) {
        if (allTaskList.containsKey(id) && checkStatusTask(id)) {
            if (allTaskList.get(id).getTypeOfTask().equals(Type.EPIC)) {
                System.out.println("Стаус эпика изменить нельзя");
            }
            if (allTaskList.get(id).getTypeOfTask().equals(Type.TASK)) {
                taskList.get(id).finishTask();
            }
            if (allTaskList.get(id).getTypeOfTask().equals(Type.SUBTASK)) {
                subtaskList.get(id).finishTask();
                if (checkStatusSubtaskForEpic(subtaskList.get(id).getIdEpic())) {
                    epicList.get(subtaskList.get(id).getIdEpic()).finishTask();
                }
            }
        } else {
            System.out.println("Задачи под таким номером нет или над ней не была начата работа");
        }
    }

    @Override
    public List<Task> getHistory() {  //Метод для получения списка истории
        return historyManager.getHistory();
    }

    @Override
    public void printHistoryList() { //Метод для вывода на экран истории просмотра задач
        for (Task task : historyManager.getHistory()) {
            System.out.println(task);
        }
    }

    private void addTaskInHistory(Task task) { //Добавляем задачу в список истории просмотров
        historyManager.add(task);
    }

    private boolean checkStatusTask(Integer id) {
        return allTaskList.get(id).getStatus().equals(Status.IN_PROGRESS);
    }

    private boolean checkStatusSubtaskForEpic(Integer id) {
        numOfSubtaskForEpic = 0;
        for (Integer num : epicList.get(id).getSubtaskListForEpic().keySet()) {
            if (subtaskList.get(num).getStatus().equals(Status.DONE)) {
                numOfSubtaskForEpic++;
            }
        }
        return (numOfSubtaskForEpic.equals(epicList.get(id).getSubtaskListForEpic().size()));
    }

    private boolean checkOnLastSubtaskInEpic(Integer id) {
        numOfSubtaskForEpic = 1;
        return (epicList.get(id).getSubtaskListForEpic().size() == numOfSubtaskForEpic);
    }

    private boolean checkOnNewStatusSubtaskList(Integer id) {
        numOfSubtaskForEpic = 0;
        for (Integer num : epicList.get(id).getSubtaskListForEpic().keySet()) {
            if (subtaskList.get(num).getTaskStatus().equals(Status.NEW)) {
                numOfSubtaskForEpic++;
            }
        }
        return (numOfSubtaskForEpic.equals(epicList.get(id).getSubtaskListForEpic().size()));
    }

    private boolean checkOnNullSubtaskListForEpic(Integer id) {
        return (epicList.get(id).getSubtaskListForEpic().size() == 0);
    }

    private Status getLastStatusOfSubtaskForEpic(Integer id) {
        int idLastSubtask = 0;
        for (Integer num : epicList.get(id).getSubtaskListForEpic().keySet()) {
            idLastSubtask = num;
        }
        return subtaskList.get(idLastSubtask).getTaskStatus();
    }

    public void printAllTaskList() {
        for (Integer id : allTaskList.keySet()) {
            System.out.println(allTaskList.get(id));
        }
    }

    public void printTaskList() {
        for (Integer id : taskList.keySet()) {
            System.out.println(taskList.get(id));
        }
    }

    public void printEpicList() {
        for (Integer id : epicList.keySet()) {
            System.out.println(epicList.get(id));
        }
    }

    public void printSubtaskList() {
        for (Integer id : subtaskList.keySet()) {
            System.out.println(subtaskList.get(id));
        }
    }
}
