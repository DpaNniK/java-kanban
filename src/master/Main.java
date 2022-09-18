package master;


import com.google.gson.Gson;
import httpResourse.GsonCreate;
import httpResourse.HTTPTaskServer;
import httpResourse.KVServer;
import task.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Main {
    public static void main(String[] args) throws IOException {
        String port = "8080";
        KVServer kvServer = new KVServer();
        kvServer.start();
        TaskManager taskManager = Managers.getHTTPTaskManager(port);
        taskManager.createNewTask("Помыть машину", "Заехать на мойку в 20:00");
        taskManager.createNewEpic("Переехать", "Нужно собрать вещи и сдать ключи");
        taskManager.createNewSubtask("Собрать вещи", "Не забыть утюг", 1);
        taskManager.createNewTask("newTask", "Task");
        taskManager.setTimeForTask(2, "17:44 08.05.22", 20);
        taskManager.getTaskById(1);
        taskManager.getTaskById(0);

        TaskManager taskManager1 = HTTPTaskManager.loadFromServer(port);
        taskManager1.createNewTask("а", "б");
        taskManager1.printHistoryList();
        kvServer.stop();

        /*
        Тестирование работы KVServer
        Gson gson = GsonCreate.createGson();
        TaskManager taskManager = Managers.getDefault();
        taskManager.createNewTask("Помыть машину", "Заехать на мойку в 20:00");
        taskManager.createNewEpic("Переехать", "Нужно собрать вещи и сдать ключи");
        taskManager.createNewSubtask("Собрать вещи", "Не забыть утюг", 1);

        new KVServer().start();
        KVTaskClient kvTaskClient = new KVTaskClient("8080");

        kvTaskClient.put("tasks", gson.toJson(taskManager.getTaskList()));

        kvTaskClient.load("tasks"); */

    }
}
