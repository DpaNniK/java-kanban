package master;

public class Managers { //Создаю служебынй класс

    public static TaskManager getDefault() { //возвращаю объект InMemoryTaskManager
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() { //возвращаю объект InMemoryHistoryManager
        return new InMemoryHistoryManager();
    }
}
