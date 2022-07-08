package master;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        taskManager.createNewTask("Помыть машину", "Заехать на мойку в 20:00");
        taskManager.createNewEpic("Переехать", "Нужно собрать вещи и сдать ключи");
        taskManager.createNewSubtask("Собрать вещи", "Не забыть утюг", 1);
        taskManager.createNewSubtask("Сдать ключи", "Встреча с хозяином в 15:30", 1);
        taskManager.createNewEpic("Сделать проект", "Уже спать хочется]");
        taskManager.createNewSubtask("Разобраться с наследием", "Уже очень спать хочется", 4);
        taskManager.startTask(0); //пользователь отмечает, что начинает работу над первой задачей
        //taskManager.printTaskList();
        taskManager.finishTask(0); //пользователь отмечает, что задача выполнена
        // taskManager.printTaskList();
        System.out.println();
        taskManager.startTask(2); //начата работа над подзадачей, ее эпик определяется в программе
        // taskManager.printSubtaskForEpic(1); id - эпика
        taskManager.deleteTaskById(2); // удаляем начатую подзадачу, статус эпика возвращается на NEW
        // taskManager.printSubtaskForEpic(1);
        taskManager.startTask(3);
        taskManager.finishTask(3); // завершаем единственную задачу эпика, статус которого станет DONE
        // taskManager.printSubtaskForEpic(1);
        // taskManager.deleteTaskById(3); // удалим последнюю задачу в эпике, эпик сменит тип на задачу
        // taskManager.printTaskList();
        // taskManager.printAllTaskList();
        taskManager.startTask(5);
        taskManager.finishTask(5);
        //taskManager.printSubtaskForEpic(1); // эпик выполнится вместе с подзадачей
        taskManager.updateTask(4, "Не ложиться спать", "По-другому не разобраться с наследием");
        //обновляю задачу, id - обновляемой задачи
        //taskManager.printSubtaskForEpic(1); // Стаус сменится на NEW, если бы в списке подзадач для эпика
        // была бы не одна задача, и среди них нашлась та, у которой статус не NEW, то стауст
        // сменился бы на IN_PROGRESS (если останется одна задача со статусом DONE, статус эпика будет = DONE)
        //taskManager.printTaskById(3); // по ID получаю любую задачу
    }
}
