package master;

import task.Epic;
import task.Subtask;
import task.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileBackedTaskManager extends InMemoryTaskManager {
    File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    /* Метод save() - записывает данные в файл при помощи переопределенного метода toString()*/
    public void save() throws ManagerSaveException {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            bufferedWriter.write(toString());
        } catch (IOException exc) {
            throw new ManagerSaveException("Ошибка записи данных в файл.");
        }
    }

    /*Метод toString() создает формат вывода данных в файл - первой будет строка с описанием,
      затем поочередно будут добавлены задачи из allTaskList класса InMemoryTaskManager.
      После - отступ и вывод истории.
      */
    @Override
    public String toString() {
        return String.format("id," + "type," +
                "name," + "status," + "description," + "epic id\n" +
                allTaskToString() +
                "\n" +
                InMemoryHistoryManager.historyToString(historyManager));
    }

    /* Метод, преобразующий строку в задачу, разбивает полученную строчку по "," передавая ее в массив.
    Затем, в зависимости от значения str[1] создается либо task, либо epic, либо subtask */
    public static Task fromString(String value) {
        String[] str = value.split(",");
        switch (str[1]) {
            case "TASK":
                return new Task(str[2], str[4]);
            case "EPIC":
                return new Epic(str[2], str[4]);
        }
        return new Subtask(str[2], str[4], Integer.parseInt(str[5]));
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            bufferedReader.readLine(); //Читаю первую линую, чтобы шапка (id,type,...) не передалась в методы
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine(); // line-первая строчка с задачей в файле
                /* Далее идет проверка на пустую строчку в файле. Пока она не обнаружится, программа будет вызывать
                метод fromString(line), как только строка обнаруживается, через bufferedReader
                получаем следующую строку - с историей, где методом historyFromString получаем из строки историю.
                Как только цикл отработал - вызывается break;*/
                if (line.isBlank()) {
                    line = bufferedReader.readLine();
                    for (Integer id : InMemoryHistoryManager.historyFromString(line)) {
                        //Здесь происходит добавление истории из полученного списка с id задач
                        fileBackedTaskManager.addTaskInHistory(fileBackedTaskManager.allTaskList.get(id));
                    }
                    break;
                }
                Task task = fromString(line);//Преобразует строчку в Task
                switch (task.getTypeOfTask()) {
                    //Создаем новую задачу в зависимости от ее типа
                    case TASK:
                        fileBackedTaskManager.createNewTask(task.getName(), task.getDescription());
                        break;
                    case EPIC:
                        Epic epic = (Epic) task;
                        fileBackedTaskManager.createNewEpic(epic.getName(), epic.getDescription());
                        break;
                    case SUBTASK:
                        Subtask subtask = (Subtask) task;
                        fileBackedTaskManager.createNewSubtask(subtask.getName(), subtask.getDescription(), subtask.getIdEpic());
                        break;
                }

            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения данных из файла.");
        }
        return fileBackedTaskManager;
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
}
