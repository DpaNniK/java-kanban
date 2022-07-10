package master;

import task.Task; //импортирую из пакета типы задач
import task.Epic;
import task.Subtask;
import task.Status;

import java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> taskList; // списки для разных типов
    private final HashMap<Integer, Epic> epicList;
    private final HashMap<Integer, Subtask> subtaskList;
    private final HashMap<Integer, Task> allTaskList; // один общий список
    private static Integer id = 0; // использую для счетчика
    private Integer numOfSubtaskForEpic = 0; // нужен для приватных методов по работе с подзадачами
    Task task;
    Epic epic;
    Subtask subtask;
    Status status;

    public TaskManager() {
        this.taskList = new HashMap<>();
        this.epicList = new HashMap<>();
        this.subtaskList = new HashMap<>();
        this.allTaskList = new HashMap<>();
        this.status = new Status();
    }

    public void createNewTask(String name, String description) { //добавляю в оба списка задачу, счетчик увеличиваю
        task = new Task(name, description);
        task.setId(id);
        taskList.put(id, task);
        allTaskList.put(id, task);
        id++;
    }

    public void createNewEpic(String name, String description) {
        epic = new Epic(name, description);
        epic.setId(id);
        epicList.put(id, epic);
        allTaskList.put(id, epic);
        id++;
    }

    public void createNewSubtask(String name, String description, Integer idEpic) { //при создание указывается id эпика
        if (allTaskList.get(idEpic).getClass().equals(epic.getClass())) {
            subtask = new Subtask(name, description, idEpic);
            subtask.setId(id);
            subtaskList.put(id, subtask);
            allTaskList.put(id, subtask);
            addSubtaskInEpic(subtask, idEpic); //вызываю метод для добавления подзадачи в отдельный список внутри эпика
            id++;
        } else {
            System.out.println("Эпика под таким номером нет");
        }
    }

    private void addSubtaskInEpic(Subtask subtask, Integer id) { // добавление подзадачи в лист внутри эпика
        epicList.get(id).putSubtaskForEpic(subtask);
    }

    private void deleteSubtaskForEpic(Integer idEpic, Subtask subtask) { // удаление подзадачи из эпика
        epicList.get(idEpic).removeSubtaskForEpic(subtask);
    }


    public void printSubtaskForEpic(Integer id) { // метод для печати всехподзадач для указанного эпика
        if (epicList.containsKey(id)) {
            System.out.println(epicList.get(id));
            for (Integer num : epicList.get(id).getSubtaskListForEpic().keySet()) {
                System.out.println(subtaskList.get(num));
            }
        } else {
            System.out.println("Эпика под таким номером нет");
        }
    }

    public void deleteAllTaskList() { // метод по удалению всех списков
        allTaskList.clear();
        taskList.clear();
        epicList.clear();
        subtaskList.clear();
    }

    public void deleteTaskById(Integer id) { // метод по удалению задачи по id
        if (allTaskList.containsKey(id)) {
            if (allTaskList.get(id).getClass().equals(epic.getClass())) { // удаление эпика
                for (Integer num : epicList.get(id).getSubtaskListForEpic().keySet()) { // со всеми его подзадачами
                    subtaskList.remove(num);
                }
                epicList.remove(id); // очистка листа от удаленного эпика
                allTaskList.remove(id); // очистка общего листа
            } else if (allTaskList.get(id).getClass().equals(subtask.getClass())) { //класс подзадачи
                deleteSubtaskForEpic(subtaskList.get(id).getIdEpic(), subtaskList.get(id)); // метод по удалению sub. из epic
                if (checkStatusSubtaskForEpic(subtaskList.get(id).getIdEpic())) { // eсли после удаления подзадачи
                    epicList.get(subtaskList.get(id).getIdEpic()). // в списке у эпика остались все задачи "DONE",
                            setTaskStatus(subtaskList.get(id).getTaskStatus()); // то статус эпика изменится на DONE
                }
                if (checkOnNewStatusSubtaskList(subtaskList.get(id).getIdEpic())) { // если все подзадачи эпика имеют
                    epicList.get(subtaskList.get(id).getIdEpic()). // статус NEW, то и сам эпик будет иметь статус NEW
                            setTaskStatus(status.taskNew);
                }
                if (checkOnLastSubtaskInEpic(subtaskList.get(id).getIdEpic())) { // если в списке осталась одна поздача,
                    epicList.get(subtaskList.get(id).getIdEpic()). // то эпик будет принимать ее статус
                            setTaskStatus(getLastStatusOfSubtaskForEpic(subtaskList.get(id).getIdEpic()));
                }
                if (checkOnNullSubtaskListForEpic(subtaskList.get(id).getIdEpic())) { //проверка, если после удаления не
                    task = new Task(epicList.get(subtaskList.get(id).getIdEpic()).getName(), // осталось ни одной подза-
                            epicList.get(subtaskList.get(id).getIdEpic()).getDescription()); // дачи, то Эпик меняет тип
                    task.setId(subtaskList.get(id).getIdEpic()); // На Task (Задача)
                    taskList.put(subtaskList.get(id).getIdEpic(), task);
                    allTaskList.put(subtaskList.get(id).getIdEpic(), task);
                    epicList.remove(subtaskList.get(id).getIdEpic());
                }
                subtaskList.remove(id);
                allTaskList.remove(id);
            } else if (allTaskList.get(id).getClass().equals(task.getClass())) { //удаление обычной задачи
                taskList.remove(id);
                allTaskList.remove(id);
            }
        } else {
            System.out.println("Задачи под таким номером нет");
        }
    }

    public void updateTask(Integer id, String name, String description) { //обновлению задачи, id - обновляемой задачи
        if (allTaskList.containsKey(id)) {
            if (allTaskList.get(id).getClass().equals(subtask.getClass())) { // создание новой подзадачи
                subtask = new Subtask(name, description, subtaskList.get(id).getIdEpic());
                subtask.setId(id); // Новая задача создается со статусом NEW, статус предыдущей меняется
                subtaskList.put(id, subtask);
                allTaskList.put(id, subtask);
                addSubtaskInEpic(subtask, subtaskList.get(id).getIdEpic());
                if (checkOnNewStatusSubtaskList(subtaskList.get(id).getIdEpic())) { // если все подзадачи в эпике
                    epicList.get(subtaskList.get(id).getIdEpic()).createNewTask();// имеют статус "NEW", то и эпик NEW
                } else {
                    epicList.get(subtaskList.get(id).getIdEpic()).startTask(); // в противном случае IN_PROGRESS
                }
            }
            if (allTaskList.get(id).getClass().equals(task.getClass())) { //обновляется задача
                task = new Task(name, description);
                task.setId(id);
                taskList.put(id, task);
                allTaskList.put(id, task);
                taskList.get(id).createNewTask();
            }

            if (allTaskList.get(id).getClass().equals(epic.getClass())) { //обновляется Эпик. Остается все, меняю только
                epic = new Epic(name, description); // имя и описание
                epic.setId(id);
                epicList.put(id, epic);
                allTaskList.put(id, epic);
            }
        } else {
            System.out.println("Задачи под атким номером нет");
        }
    }


    public void printTaskById(Integer id) {
        if (allTaskList.containsKey(id)) {
            System.out.println(allTaskList.get(id));
        } else {
            System.out.println("Задачи под таким номером нет.");
        }
    }

    public void startTask(Integer id) { // метод - начать выполнение задачи
        if (allTaskList.containsKey(id)) { // статус меняется на IN_PROGRESS, в зависимости от класса используются листы
            if (!allTaskList.get(id).getClass().equals(epic.getClass())) { // в которые добавляются новые задачи
                if (allTaskList.get(id).getClass().equals(task.getClass())) {
                    taskList.get(id).startTask();
                } else {
                    subtaskList.get(id).startTask();
                    epicList.get(subtaskList.get(id).getIdEpic()).startTask(); //смена статуса эпика
                }
            } else {
                System.out.println("Статус эпика изменить нельзя"); // статус эпика меняется автоматически
            }
        } else {
            System.out.println("Задачи под таким номером нет");
        }
    }

    public void finishTask(Integer id) { // метод - закончить задачу
        if (allTaskList.containsKey(id) && checkStatusTask(id)) {
            if (allTaskList.get(id).getClass().equals(epic.getClass())) {
                System.out.println("Стаус эпика изменить нельзя");
            }
            if (allTaskList.get(id).getClass().equals(task.getClass())) {
                taskList.get(id).finishTask();
            }
            if (allTaskList.get(id).getClass().equals(subtask.getClass())) {
                subtaskList.get(id).finishTask();
                if (checkStatusSubtaskForEpic(subtaskList.get(id).getIdEpic())) { // проверка, если внутри эпика
                    epicList.get(subtaskList.get(id).getIdEpic()).finishTask(); // все DONE, то и он DONE
                }
            }
        } else {
            System.out.println("Задачи под таким номером нет или над ней не была начата работа");
        }
    }


    private boolean checkStatusTask(Integer id) { //проверка, что задача была отмечена, как "IN_PROGRESS"
        return allTaskList.get(id).getStatus().equals(status.taskInProgress); //Из NEW сразу получить "In_Progr." нельзя
    }

    private boolean checkStatusSubtaskForEpic(Integer id) { // проверка, что все подзадачи внутри эпика выполнены
        numOfSubtaskForEpic = 0;
        for (Integer num : epicList.get(id).getSubtaskListForEpic().keySet()) {
            if (subtaskList.get(num).getStatus().equals(status.taskDone)) {
                numOfSubtaskForEpic++;
            }
        }
        return (numOfSubtaskForEpic.equals(epicList.get(id).getSubtaskListForEpic().size()));
    }

    private boolean checkOnLastSubtaskInEpic(Integer id) { // проверка, что в эпике осталась одна подзадача
        numOfSubtaskForEpic = 1;
        return (epicList.get(id).getSubtaskListForEpic().size() == numOfSubtaskForEpic);
    }

    private boolean checkOnNewStatusSubtaskList(Integer id) { // проверка, что все подзадачи имеют статус "NEW"
        numOfSubtaskForEpic = 0;
        for (Integer num : epicList.get(id).getSubtaskListForEpic().keySet()) {
            if (subtaskList.get(num).getTaskStatus().equals(status.taskNew)) {
                numOfSubtaskForEpic++;
            }
        }
        return (numOfSubtaskForEpic.equals(epicList.get(id).getSubtaskListForEpic().size()));
    }

    private boolean checkOnNullSubtaskListForEpic(Integer id) { // проверка, что в эпике не осталось подзадач
        return (epicList.get(id).getSubtaskListForEpic().size() == 0);
    }

    private String getLastStatusOfSubtaskForEpic(Integer id) { //Получение последнего статуса подзадачи для эпика
        int idLastSubtask = 0; // когда остается одна подзадача, получаем ее статус, и этот статус присваиваем эпику
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
