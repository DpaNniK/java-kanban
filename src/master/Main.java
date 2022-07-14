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
        /* По ТЗ - Просмотром будем считаться вызов у менеджера методов получения задачи по идентификатору — getTask(),
        getSubtask() и getEpic()
         Я сразу сделал универсальный метод getTaskById (поменял реализацию, раньше он был printTaskById(), что ошибка),
         таким образом, я могу получить как эпик, так и подзадачу, а также просто задачу, использовав этот метод,
         почему-то решил, что универсальным он должен быть, так как меньше похожего кода :)
         (Вместо трех методов getTask(),getSubtask() и getEpic() у меня один универсальный - getTaskById() )
         После вызова .getTaskById() просмотренная задача(или эпик, или подзадача), добавляется в список истории,
         внутри метода getTaskById() вызывается метод addTaskInHistory */
        for (int i = 0; i < 10; i++) { //В for вызовом метода getTaskById() я забиваю историю 10-ю получениями задач
            if (i == 0) {
                taskManager.getTaskById(0);
            } else {
                taskManager.getTaskById(2);
            }
        }
        System.out.println("Получившаяся история просмотров: ");
        taskManager.printHistoryList();
        System.out.println("Новая запись для списка:");
        taskManager.getTaskById(5); // переполняю список истории, удалится последняя, самая давняя история (индекс 0)
        System.out.println();
        taskManager.printHistoryList();

    }
}
