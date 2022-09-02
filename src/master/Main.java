package master;


import task.Task;

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

        //Устанавливаю время для подзадач эпика:
        taskManager.setTimeForTask(3, "17:44 08.05.22", 20);
        taskManager.setTimeForTask(2, "11:05 08.05.22", 60);

        //Тут будет ошибка, так получится пересечение времени.
        //taskManager.setTimeForTask(2, "17:54 08.05.22", 60);

        //Устанавливаю время для Task
        taskManager.setTimeForTask(0, "22:05 08.05.22", 45);

        //Вывожу список задач в приоритете по startTime
        for(Task task : taskManager.getPrioritizedTasks()) {
            System.out.println(task);
        }

        //Создаю нового менеджера, восстанавливая данные из файла:
        TaskManager taskManager1 = FileBackedTaskManager.loadFromFile(file);
        System.out.println("Приоритетные задачи, восстановленные из файла ");
        for (Task task : taskManager1.getPrioritizedTasks()) {
            System.out.println(task);
        }

    }
}
