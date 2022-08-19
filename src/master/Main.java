package master;

import java.io.File;

public class Main {
    public static void main(String[] args) {

        File file = new File("file.backed.csv");
        TaskManager taskManager = Managers.getFileBackedManager(file);
        //Создаю задачи:
        taskManager.createNewTask("Помыть машину", "Заехать на мойку в 20:00");
        taskManager.createNewEpic("Переехать", "Нужно собрать вещи и сдать ключи");
        taskManager.createNewSubtask("Собрать вещи", "Не забыть утюг", 1);
        taskManager.createNewSubtask("Сдать ключи", "Встреча с хозяином в 15:30", 1);
        taskManager.createNewSubtask("Посидеть на дорожку", "Прогнать воспоминания в голове", 1);
        taskManager.createNewEpic("Сделать проект", "Уже спать хочется");
        //Получаю историю просмотров:
        taskManager.getTaskById(1);
        taskManager.getTaskById(3);
        taskManager.getTaskById(2);
        //taskManager.printHistoryList();
        //taskManager.printAllTaskList();

        //Создаю нового менеджера, восстанавливая данные из файла:
        TaskManager taskManager1 = FileBackedTaskManager.loadFromFile(file);
        taskManager1.printAllTaskList();
        System.out.println("История просмотров: ");
        taskManager1.printHistoryList();
    }
}
