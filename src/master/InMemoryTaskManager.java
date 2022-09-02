package master;

import task.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected HashMap<Integer, Task> taskList;
    protected HashMap<Integer, Epic> epicList;
    protected HashMap<Integer, Subtask> subtaskList;
    protected HashMap<Integer, Task> allTaskList;
    HistoryManager historyManager = Managers.getDefaultHistory();
    Comparator<Task> comparator = Comparator.comparing(Task::getStartTime
            , Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getId);

    private Integer id = 0;
    protected Integer numOfSubtaskForEpic = 0;
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
        if (allTaskList.size() != 0 && allTaskList.get(idEpic).getTypeOfTask().equals(Type.EPIC)) {
            subtask = new Subtask(name, description, idEpic);
            subtask.setId(id);
            subtaskList.put(id, subtask);
            allTaskList.put(id, subtask);
            addSubtaskInEpic(subtask, idEpic);
            id++;
        } else {
            throw new IllegalArgumentException("Эпика под таким номером нет");
        }
    }

    @Override
    public void setTimeForTask(int id, String localDateTime, int duration) {
        if (allTaskList.containsKey(id)) {
            if (checkThatTimeIsNotInInterval(localDateTime, duration)) {
                switch (allTaskList.get(id).getTypeOfTask()) {
                    case EPIC:
                        throw new IllegalArgumentException(
                                "Время начала эпика определяется временем старта его ранней подзадачи");
                    case TASK:
                        allTaskList.get(id).getEndTime(localDateTime, duration);
                        break;
                    case SUBTASK:
                        Subtask subtask = (Subtask) allTaskList.get(id);
                        subtask.getEndTime(localDateTime, duration);
                        Epic epic = (Epic) allTaskList.get(subtask.getIdEpic());
                        epic.getEndTime(localDateTime, duration);
                }
            } else throw new IllegalArgumentException("Время задачи пересекается с временем выполнения другой задачи");
        } else throw new IllegalArgumentException("Задачи под таким id не найдено");
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        Set<Task> sortTimeTaskList = new TreeSet<>(comparator);
        for (int id : allTaskList.keySet()) {
            sortTimeTaskList.add(allTaskList.get(id));
        }
        return sortTimeTaskList;
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
    public List<Subtask> getSubtaskForEpic(Integer id) {
        List<Subtask> subtasks = new ArrayList<>();
        if (epicList.containsKey(id)) {
            for (Integer num : epicList.get(id).getSubtaskListForEpic().keySet()) {
                subtasks.add(subtaskList.get(num));
            }
            return subtasks;
        } else {
            throw new IllegalArgumentException("Задачи под таким id не найдено.");
        }
    }

    @Override
    public void deleteAllTaskList() {
        allTaskList.clear();
        taskList.clear();
        epicList.clear();
        subtaskList.clear();
    }

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
            throw new IllegalArgumentException("Задачи под таким id не найдено");
        }
    }

    @Override
    public void updateTask(Integer id, String name, String description) {
        if (allTaskList.containsKey(id)) {
            switch (allTaskList.get(id).getTypeOfTask()) {
                case SUBTASK:
                    subtask = new Subtask(name, description, subtaskList.get(id).getIdEpic());
                    setAllFieldForTask(subtask, id);
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
                    setAllFieldForTask(task, id);
                    taskList.put(id, task);
                    allTaskList.put(id, task);
                    taskList.get(id).createNewTask();
                    break;

                case EPIC:
                    List<Subtask> subtasks = getSubtaskForEpic(id);
                    epic = new Epic(name, description);
                    epic.setTaskStatus(allTaskList.get(id).getStatus());
                    setAllFieldForTask(epic, id);
                    for (Subtask sub : subtasks) {
                        epic.putSubtaskForEpic(sub);
                    }
                    epicList.put(id, epic);
                    allTaskList.put(id, epic);
                    break;
            }
        } else {
            throw new IllegalArgumentException("Задачи под таким id не найдено");
        }
    }

    @Override
    public Task getTaskById(Integer id) { //Пользователь запрашивает получение задачи
        if (allTaskList.containsKey(id)) { //Происходит проверка, есть ли она в списке
            addTaskInHistory(allTaskList.get(id)); //Просмотренная задача добавляется в список истории
            return allTaskList.get(id);
        } else {
            throw new IllegalArgumentException("Задачи под таким id не найдено.");
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
                throw new IllegalArgumentException("Статус эпика изменить нельзя");
            }
        } else {
            throw new IllegalArgumentException("Задачи под таким номером нет");
        }
    }

    @Override
    public void finishTask(Integer id) {
        if (allTaskList.containsKey(id) && checkStatusTask(id)) {
            if (allTaskList.get(id).getTypeOfTask().equals(Type.EPIC)) {
                throw new IllegalArgumentException("Стаус эпика изменить нельзя");
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
            throw new IllegalArgumentException("Задачи под таким номером нет или над ней не была начата работа");
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

    protected void addTaskInHistory(Task task) { //Добавляем задачу в список истории просмотров
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

    private Boolean checkThatTimeIsNotInInterval(String localDataTime, int duration) {
        LocalDateTime currentDataTime = LocalDateTime.parse(localDataTime, Task.DATE_TIME_FORMATTER);
        Duration currentDuration = Duration.ofMinutes(duration);
        for (Task task : getPrioritizedTasks()) {
            if (task.getStartTime() == null) {
                return true;
            }
            if(task.getTypeOfTask().equals(Type.EPIC)) {
                continue;
            }
            if (currentDataTime.isAfter(task.getStartTime()) &&
                    currentDataTime.isBefore(task.getEndTime()) ||
                    currentDataTime.plus(currentDuration).isAfter(task.getStartTime()) &&
                    currentDataTime.plus(currentDuration).isBefore(task.getEndTime()) ||
                    currentDataTime.equals(task.getStartTime())
            ) return false;
        }
        return true;
    }

    private void setAllFieldForTask(Task task, int currentId) {
        task.setId(currentId);
        task.setStartTime(allTaskList.get(currentId).getStartTime());
        task.setDuration(allTaskList.get(currentId).getDuration());
        task.setEndTime(allTaskList.get(currentId).getEndTime());
    }

    public HashMap<Integer, Task> getAllTaskList() {
        return allTaskList;
    }

    public HashMap<Integer, Task> getTaskList() {
        return taskList;
    }

    public HashMap<Integer, Epic> getEpicList() {
        return epicList;
    }

    public HashMap<Integer, Subtask> getSubtaskList() {
        return subtaskList;
    }

    /* Ниже описан метод для представления task, epic, subtask в виде строки*/
    protected String allTaskToString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Integer id : allTaskList.keySet()) {
            stringBuilder.append(allTaskList.get(id).toString()).append("\n");
        }
        return stringBuilder.toString();
    }

}
