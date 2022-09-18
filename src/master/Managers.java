package master;

import java.io.File;

public class Managers { //Создаю служебный класс

    public static InMemoryTaskManager getDefault() { //возвращаю объект InMemoryTaskManager
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() { //возвращаю объект InMemoryHistoryManager
        return new InMemoryHistoryManager();
    }

    public static TaskManager getFileBackedManager(File file) {
        return new FileBackedTaskManager(file.getPath());
    }

    public static HTTPTaskManager getHTTPTaskManager(String port) {
        return new HTTPTaskManager(port);
    }
}
