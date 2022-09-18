package master;

import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileBackedTaskManager extends InMemoryTaskManager {
    File file;
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd.MM.yy");

    public FileBackedTaskManager(String path) {
        this.file = new File(path);
    }

    public void save() throws ManagerSaveException {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            bufferedWriter.write(toString());
        } catch (IOException exc) {
            throw new ManagerSaveException("Ошибка записи данных в файл.");
        }
    }

    @Override
    public String toString() {
        return String.format("id," + "type," +
                "name," + "status," + "description," + "epic id," +
                "start time," + "duration(min)," + "end time\n" +
                allTaskToString() +
                "\n" +
                InMemoryHistoryManager.historyToString(historyManager));
    }

    private Task fromString(String value) {
        String[] str = value.split(",");
        switch (str[1]) {
            case "TASK":
                Task task = new Task(str[2], str[4]);
                loadTaskFromString(task, str);
                return task;
            case "EPIC":
                Epic epic = new Epic(str[2], str[4]);
                loadTaskFromString(epic, str);
                return epic;
        }
        Subtask subtask = new Subtask(str[2], str[4], Integer.parseInt(str[5]));
        loadTaskFromString(subtask, str);
        return subtask;
    }

    public static FileBackedTaskManager loadFromFile(String patch) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(patch);
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(patch, StandardCharsets.UTF_8))) {
            bufferedReader.readLine();
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                if (line.isBlank()) {
                    line = bufferedReader.readLine();
                    if (line == null) {
                        break;
                    }
                    for (Integer id : InMemoryHistoryManager.historyFromString(line)) {
                        fileBackedTaskManager.addTaskInHistory(fileBackedTaskManager.allTaskList.get(id));
                    }
                    break;
                }
                Task task = fileBackedTaskManager.fromString(line);
                switch (task.getTypeOfTask()) {
                    case TASK:
                        fileBackedTaskManager.createNewTask(task.getName(), task.getDescription());
                        fileBackedTaskManager.createTaskFromFile(fileBackedTaskManager.allTaskList.get(task.getId())
                                , task);
                        break;
                    case EPIC:
                        Epic epic = (Epic) task;
                        fileBackedTaskManager.createNewEpic(epic.getName(), epic.getDescription());
                        fileBackedTaskManager.createTaskFromFile(fileBackedTaskManager.allTaskList.get(task.getId())
                                , epic);
                        break;
                    case SUBTASK:
                        Subtask subtask = (Subtask) task;
                        fileBackedTaskManager.createNewSubtask(subtask.getName(), subtask.getDescription()
                                , subtask.getIdEpic());
                        fileBackedTaskManager.createTaskFromFile(fileBackedTaskManager.allTaskList.get(task.getId())
                                , subtask);
                        break;
                }

            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения данных из файла.");
        }
        return fileBackedTaskManager;
    }

    private static Status getStatusFromString(String str) {
        switch (str) {
            case "NEW":
                return Status.NEW;
            case "DONE":
                return Status.DONE;
            case "IN_PROGRESS":
                return Status.IN_PROGRESS;
        }
        throw new IllegalArgumentException("Неизвестный статус задачи");
    }

    //Метод восстановления полей в зависимости от типа задачи
    private void loadTaskFromString(Task task, String[] str) {
        switch (task.getTypeOfTask()){
            case TASK:
                task.setTaskStatus(getStatusFromString(str[3]));
                task.setId(Integer.parseInt(str[0]));
                if (!(str[5]).equals("null")) {
                    task.setStartTime(LocalDateTime.parse(str[5],DATE_TIME_FORMATTER));
                }
                if (!(str[6]).equals("null")) {
                    task.setDuration(Duration.ofMinutes(Integer.parseInt(str[6])));
                }
                if (!(str[7]).equals("null")) {
                    task.setEndTime(LocalDateTime.parse(str[7], DATE_TIME_FORMATTER));
                }
                break;
            case EPIC:
                Epic epic = (Epic) task;
                epic.setTaskStatus(getStatusFromString(str[3]));
                epic.setId(Integer.parseInt(str[0]));
                if (!(str[5]).equals("null")) {
                    epic.setStartTime(LocalDateTime.parse(str[5],DATE_TIME_FORMATTER));
                }
                if (!(str[6]).equals("null")) {
                    epic.setDuration(Duration.ofMinutes(Integer.parseInt(str[6])));
                }
                if (!(str[7]).equals("null")) {
                    epic.setEndTime(LocalDateTime.parse(str[7], DATE_TIME_FORMATTER));
                }
                break;
            case SUBTASK:
                Subtask subtask = (Subtask) task;
                subtask.setTaskStatus(getStatusFromString(str[3]));
                subtask.setId(Integer.parseInt(str[0]));
                if (!(str[6]).equals("null")) {
                    subtask.setStartTime(LocalDateTime.parse(str[6], DATE_TIME_FORMATTER));
                }
                if (!(str[7]).equals("null")) {
                    subtask.setDuration(Duration.ofMinutes(Integer.parseInt(str[7])));
                }
                if (!(str[8]).equals("null")) {
                    subtask.setEndTime(LocalDateTime.parse(str[8], DATE_TIME_FORMATTER));
                }
        }
    }

    //Присвоение полей для новой задачи, полученных чтением файла
    private void createTaskFromFile(Task newTask, Task oldTask) {
        newTask.setTaskStatus(oldTask.getTaskStatus());
        newTask.setStartTime(oldTask.getStartTime());
        newTask.setDuration(oldTask.getDuration());
        newTask.setEndTime(oldTask.getEndTime());
    }

    //Ниже переопределены методы из InMemoryTaskManager
    @Override
    public void createNewTask(String name, String description) {
        super.createNewTask(name, description);
        save();
    }

    @Override
    public void createNewEpic(String name, String description) {
        super.createNewEpic(name, description);
        save();
    }

    @Override
    public void addSubtaskInEpic(Subtask subtask, Integer id) {
        super.addSubtaskInEpic(subtask, id);
        save();
    }

    @Override
    public void deleteSubtaskForEpic(Integer idEpic, Subtask subtask) {
        super.deleteSubtaskForEpic(idEpic, subtask);
        save();
    }

    @Override
    public void deleteTaskById(Integer id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void updateTask(Integer id, String name, String description) {
        super.updateTask(id, name, description);
        save();
    }

    @Override
    public void startTask(Integer id) {
        super.startTask(id);
        save();
    }

    @Override
    public void finishTask(Integer id) {
        super.finishTask(id);
        save();
    }

    @Override
    protected void addTaskInHistory(Task task) {
        super.addTaskInHistory(task);
        save();
    }

    @Override
    public void setTimeForTask(int id, String localDateTime, int duration) {
        super.setTimeForTask(id, localDateTime, duration);
        save();
    }
}
