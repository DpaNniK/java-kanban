package master;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault(); //создаю объект при помощи служебного класса
        taskManager.createNewTask("Помыть машину", "Заехать на мойку в 20:00");
        taskManager.createNewEpic("Переехать", "Нужно собрать вещи и сдать ключи");
        taskManager.createNewSubtask("Собрать вещи", "Не забыть утюг", 1);
        taskManager.createNewSubtask("Сдать ключи", "Встреча с хозяином в 15:30", 1);
        taskManager.createNewEpic("Сделать проект", "Уже спать хочется");
        taskManager.createNewSubtask("Разобраться с наследием", "Уже очень спать хочется", 4);
        for (int i = 0; i < 10; i++) { // Забиваю списока/ истории 10-ю записями (одинаковые, кроме самой первой)
            if (i == 0) { // Запись - просмотр пользователем какой-либо задачи
                taskManager.printTaskById(0);
            } else {
                taskManager.printTaskById(2);
            }
        }
        //System.out.println("Получившаяся история просмотров: ");
        //taskManager.printHistoryList();
        System.out.println("Новая запись для списка:");
        taskManager.printTaskById(5); // переполняю список истории, удалится последняя, самая давняя история (индекс 0)
        System.out.println();
        taskManager.printHistoryList();

    }
}
