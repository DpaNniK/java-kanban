package master;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault(); //создаю объект при помощи служебного класса
        taskManager.createNewTask("Помыть машину", "Заехать на мойку в 20:00");
        taskManager.createNewEpic("Переехать", "Нужно собрать вещи и сдать ключи");
        taskManager.createNewSubtask("Собрать вещи", "Не забыть утюг", 1);
        taskManager.createNewSubtask("Сдать ключи", "Встреча с хозяином в 15:30", 1);
        taskManager.createNewSubtask("Посидеть на дорожку", "Прогнать воспоминания в голове", 1);
        taskManager.createNewEpic("Сделать проект", "Уже спать хочется");
        /* По ТЗ - Просмотром будем считаться вызов у менеджера методов получения задачи по идентификатору — getTask(),
        getSubtask() и getEpic()
         Я сразу сделал универсальный метод getTaskById,
         таким образом, я могу получить как эпик, так и подзадачу, а также просто задачу, использовав этот метод,
         почему-то решил, что универсальным он должен быть, так как меньше похожего кода :)
         */
        //Заполняю историю просмотров методом getTaskById():
        taskManager.getTaskById(0);
        taskManager.getTaskById(2);
        taskManager.getTaskById(1);
        taskManager.getTaskById(3);
        taskManager.getTaskById(2); //Задача вызывается повторно, предыдщуая будет удалена, новая добавлена вниз списка
        taskManager.getTaskById(4);
        taskManager.getTaskById(5);
        System.out.println("История просмотров: ");
        taskManager.printHistoryList();
        System.out.println();
        System.out.println("Удаляю подзадачу с ID = 2. Новый лист с историей: ");
        taskManager.deleteTaskById(2);
        taskManager.printHistoryList();
        System.out.println();
        System.out.println("Удаляю эпик с ID = 1. Удалятся все подзадачи и сам эпик. Новая история: ");
        taskManager.deleteTaskById(1);
        taskManager.printHistoryList();

    }
}
