package master;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import httpResourse.GsonCreate;
import httpResourse.KVTaskClient;
import task.Epic;
import task.Subtask;
import task.Task;

import java.util.*;

public class HTTPTaskManager extends FileBackedTaskManager {
    KVTaskClient kvTaskClient;
    static Gson gson = GsonCreate.createGson();
    static Comparator<Task> comparator = Comparator.comparing(Task::getId);
    static Set<Task> sortIdTaskList = new TreeSet<>(comparator);

    public HTTPTaskManager(String port) {
        super(port);
        kvTaskClient = new KVTaskClient(port);
    }

    public static HTTPTaskManager loadFromServer(String port) {

        HTTPTaskManager httpTaskManager = new HTTPTaskManager(port);

        httpTaskManager.taskList = gson.fromJson(httpTaskManager.kvTaskClient.load("tasks")
                , new TypeToken<Map<Integer, Task>>() {
                }.getType());

        httpTaskManager.epicList = gson.fromJson(httpTaskManager.kvTaskClient.load("epics"),
                new TypeToken<Map<Integer, Epic>>() {
                }.getType());

        httpTaskManager.subtaskList = gson.fromJson(httpTaskManager.kvTaskClient.load("subtasks")
                , new TypeToken<Map<Integer, Subtask>>() {
                }.getType());

        List<Task> history = gson.fromJson(httpTaskManager.kvTaskClient.load("history")
                , new TypeToken<List<Task>>() {
                }.getType());


        putAllTasks(httpTaskManager);
        putHistory(history, httpTaskManager);

        return httpTaskManager;
    }

    static void putAllTasks(HTTPTaskManager httpTaskManager) {
        if (httpTaskManager.taskList != null) {
            for (int id : httpTaskManager.taskList.keySet()) {
                sortIdTaskList.add(httpTaskManager.taskList.get(id));
            }
        }
        if (httpTaskManager.epicList != null) {
            for (int id : httpTaskManager.epicList.keySet()) {
                sortIdTaskList.add(httpTaskManager.epicList.get(id));
            }
        }

        if (httpTaskManager.subtaskList != null) {
            for (int id : httpTaskManager.subtaskList.keySet()) {
                sortIdTaskList.add(httpTaskManager.subtaskList.get(id));
            }
        }

        for (Task task : sortIdTaskList) {
            httpTaskManager.allTaskList.put(task.getId(), task);
        }

        httpTaskManager.setId(sortIdTaskList.size());
    }

    static void putHistory(List<Task> history, HTTPTaskManager httpTaskManager) {
        if(history != null) {
            for (Task task : history) {
                httpTaskManager.historyManager.add(task);
            }
        }
    }

    @Override
    public void save() {
        kvTaskClient.put("tasks", gson.toJson(getTaskList()));
        kvTaskClient.put("epics", gson.toJson(getEpicList()));
        kvTaskClient.put("subtasks", gson.toJson(getSubtaskList()));
        kvTaskClient.put("history", gson.toJson(getHistory()));
    }

}
