package httpResourse;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import master.Managers;
import master.TaskManager;
import task.Epic;
import task.Subtask;
import task.Task;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.format.DateTimeFormatter;

import static jdk.internal.util.xml.XMLStreamWriter.DEFAULT_CHARSET;

/* ТЕСТИРОВАНИЕ в Insomnia
Реализованы запросы: 1) POST-запросы:
 *  a) http://localhost:8080/tasks/task - создает задачу типа TASK, в JSON теле можно передать startTime и Duration (дополнительно)
 * основные поля для передачи - name и description (пример тела: {"name" : "task", "description": "desc"} или
 * {"name" : "task2", "description": "desc", "startTime": "17:44 08.05.22", "duration": 20})
 * b) http://localhost:8080/tasks/epic - аналогично, как и у TASK, только поля startTime и Duration НЕ передаются в теле
 * c) http://localhost:8080/tasks/subtask - аналогично TASK, только в теле обязательно нужно передать ID эпика (пример тела:
 * {"idEpic": 2, "name": "subtask", "description": "desc", "startTime": "10:15 08.05.22", "duration": 20})
 * d) http://localhost:8080/tasks/task?id=3 - изменение любой задачи - EPIC, SUBTASK, TASK (метод updateTask)
 * в зависимости от переданного значения ID, пример тела для этого запроса - обновляем подзадачу эпика и меняем время
 * {"idEpic": 2, "name": "subtask", "description": "desc", "startTime": "12:15 08.05.22", "duration": 20}, этот же запрос
 * используется, если при создание задачи время было null, а теперь его необходимо заменить на конкретное значение
 * e)http://localhost:8080/tasks/task/start?id=4 - изменение статуса задача с NEW на IN_PROGRESS (без тела);
 * f)http://localhost:8080/tasks/task/finish?id=4 - изменение статуса задачи с IN_PROGRESS на DONE (без тела);
 * 2) GET-запросы:
 * a) http://localhost:8080/tasks/task - выводит список всех существующих задач, эпиков, подзадач, тело не нужно
 * b) http://localhost:8080/tasks/task?id=2 - выводит задачу, запрошенную по переданному ID, тело не нужно
 * c) http://localhost:8080/tasks/subtask/epic?id=2 - выводит список подзадач, которые имеет эпик с id = переданному
 * значению
 * d) http://localhost:8080/tasks/history - выводит историю просмотров задач (история просмотром создается/обновляется
 * вызовом запроса под буквой "с")
 * d) http://localhost:8080/tasks - выводит приоритетный по времени список задач
 * 3) DELETE-запросы:
 * a) http://localhost:8080/tasks/task?id=3 - удаляет задачу(позадачу, эпик) с переданым ID
 * b) http://localhost:8080/tasks/task - удаляет все задачи, подзадачи, эпики*/

public class HTTPTaskServer {
    static File file = new File("file.backed.csv");
    public static TaskManager taskManager = Managers.getFileBackedManager(file);
    private static final int PORT = 8080;

    static HttpServer httpServer;

    public HTTPTaskServer() throws IOException {
        taskManager = Managers.getFileBackedManager(file);
        httpServer = HttpServer.create();
    }

    public void start() throws IOException {
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler());
        httpServer.start();

        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public  void stop(int i) {
        httpServer.stop(i);
    }

    static class TaskHandler implements HttpHandler {
        String bodyJson;
        int id = 0;
        final DateTimeFormatter DATE_TIME_FORMATTER =
                DateTimeFormatter.ofPattern("HH:mm dd.MM.yy");
        Gson gson = GsonCreate.createGson();

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

            String[] split = httpExchange.getRequestURI().getPath().split("/");
            bodyJson = null;
            String method = httpExchange.getRequestMethod();
            switch (method) {
                case "POST":
                    processPostRequest(split, httpExchange);
                    break;
                case "GET":
                    processGetRequest(split, httpExchange);
                    break;
                case "DELETE":
                    processDeleteRequest(split, httpExchange);
                    break;
            }
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(bodyJson.getBytes());
            }
        }

        //Обработка POST-запросов
        private void processPostRequest(String[] split, HttpExchange httpExchange) throws IOException {
            switch (split[split.length - 1]) {
                case "task":
                    InputStream inputStream = httpExchange.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                    Task task = gson.fromJson(body, Task.class);
                    if (checkForRequestId(httpExchange.getRequestURI().getRawQuery())) {
                        updateTaskOnRequest(task, httpExchange);
                        break;
                    }
                    HTTPTaskServer.taskManager.createNewTask(task.getName(), task.getDescription());
                    checkForAvailabilityLocalTime(id, task);
                    id++;

                    httpExchange.sendResponseHeaders(201, 0);
                    break;

                case "epic":
                    inputStream = httpExchange.getRequestBody();
                    body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                    Epic epic = gson.fromJson(body, Epic.class);

                    if (checkForRequestId(httpExchange.getRequestURI().getRawQuery())) {
                        updateEpicOnRequest(epic, httpExchange);
                        break;
                    }
                    taskManager.createNewEpic(epic.getName(), epic.getDescription());
                    id++;

                    httpExchange.sendResponseHeaders(201, 0);
                    break;

                case "subtask":
                    inputStream = httpExchange.getRequestBody();
                    body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                    Subtask subtask = gson.fromJson(body, Subtask.class);

                    if (checkForRequestId(httpExchange.getRequestURI().getRawQuery())) {
                        updateTaskOnRequest(subtask, httpExchange);
                        break;
                    }
                    taskManager.createNewSubtask(subtask.getName(), subtask.getDescription()
                            , subtask.getIdEpic());
                    checkForAvailabilityLocalTime(id, subtask);
                    id++;

                    httpExchange.sendResponseHeaders(201, 0);
                    break;

                case "start":
                    if (checkForRequestId(httpExchange.getRequestURI().getRawQuery())) {
                        startTaskOnRequest(httpExchange);
                        break;
                    } else {
                        httpExchange.sendResponseHeaders(404, 0);
                    }
                    break;

                case "finish":
                    if (checkForRequestId(httpExchange.getRequestURI().getRawQuery())) {
                        finishTaskOnRequest(httpExchange);
                        break;
                    } else {
                        httpExchange.sendResponseHeaders(404, 0);
                    }
                    break;

                default:
                    bodyJson = null;

                    httpExchange.sendResponseHeaders(404, 0);
            }
        }

        //Обработка GET-запросов
        private void processGetRequest(String[] split, HttpExchange httpExchange) throws IOException {
            switch (split[split.length - 1]) {
                case "task":
                    if (checkForRequestId(httpExchange.getRequestURI().getRawQuery())) {
                        getTaskByPostRequest(httpExchange);
                        break;
                    }
                    bodyJson = gson.toJson(taskManager.getAllTaskList());

                    httpExchange.sendResponseHeaders(200, 0);
                    break;

                case "tasks":
                    bodyJson = gson.toJson(taskManager.getPrioritizedTasks());

                    httpExchange.sendResponseHeaders(200, 0);
                    break;

                case "history":
                    bodyJson = gson.toJson(taskManager.getHistory());

                    httpExchange.sendResponseHeaders(200, 0);
                    break;

                case "epic":
                    if (checkForRequestId(httpExchange.getRequestURI().getRawQuery())) {
                        getSubtaskForEpicId(httpExchange);

                        break;
                    }
                    bodyJson = gson.toJson(taskManager.getAllTaskList());

                    httpExchange.sendResponseHeaders(200, 0);
                    break;
                default:
                    httpExchange.sendResponseHeaders(404, 0);
            }
        }

        //Обработка DELETE-запросов
        private void processDeleteRequest(String[] split, HttpExchange httpExchange) throws IOException {
            //Удаление всех листов с задачами
            if ("task".equals(split[split.length - 1])) {
                //Удаление задачи по ее ID, если он указан в запросе
                if (checkForRequestId(httpExchange.getRequestURI().getRawQuery())) {
                    deleteTaskOnRequest(httpExchange);
                    return;
                }

                if (taskManager.getAllTaskList().size() != 0) {
                    taskManager.deleteAllTaskList();
                    httpExchange.sendResponseHeaders(200, 0);
                } else {
                    httpExchange.sendResponseHeaders(404, 0);
                }
            }
        }

        private boolean checkForRequestId(String patch) {
            return patch != null;
        }

        private void checkForAvailabilityLocalTime(int id, Task task) {
            if (task.getStartTime() != null && task.getDuration() != null) {
                taskManager.setTimeForTask(id, task.getStartTime().format(DATE_TIME_FORMATTER)
                        , (int) task.getDuration().toMinutes());
            }
        }

        private void updateTaskOnRequest(Task task, HttpExchange httpExchange) throws IOException {
            int idRequest = Integer.parseInt(httpExchange
                    .getRequestURI().getRawQuery().substring("id=".length()));

            if (taskManager.getAllTaskList().containsKey(idRequest)) {
                taskManager.updateTask(idRequest, task.getName(), task.getDescription());

                checkForAvailabilityLocalTime(idRequest, task);

                httpExchange.sendResponseHeaders(201, 0);
            } else {
                bodyJson = "Задачи под таким id не найдено";
                httpExchange.sendResponseHeaders(404, 0);
            }
        }

        private void getTaskByPostRequest(HttpExchange httpExchange) throws IOException {
            int idRequest = Integer.parseInt(httpExchange
                    .getRequestURI().getRawQuery().substring("id=".length()));

            if (taskManager.getAllTaskList().containsKey(idRequest)) {
                bodyJson = gson.toJson(taskManager.getTaskById(idRequest));

                httpExchange.sendResponseHeaders(200, 0);
            } else {
                bodyJson = "Задачи под таким id не найдено";
                httpExchange.sendResponseHeaders(404, 0);
            }
        }

        private void getSubtaskForEpicId(HttpExchange httpExchange) throws IOException {
            int idRequest = Integer.parseInt(httpExchange
                    .getRequestURI().getRawQuery().substring("id=".length()));

            if (taskManager.getAllTaskList().containsKey(idRequest)) {
                bodyJson = gson.toJson(taskManager.getSubtaskForEpic(idRequest));

                httpExchange.sendResponseHeaders(200, 0);
            } else {
                bodyJson = "Эпика под таким id не найдено";
                httpExchange.sendResponseHeaders(404, 0);
            }
        }

        private void deleteTaskOnRequest(HttpExchange httpExchange) throws IOException {
            int idRequest = Integer.parseInt(httpExchange
                    .getRequestURI().getRawQuery().substring("id=".length()));

            if (taskManager.getAllTaskList().containsKey(idRequest)) {
                taskManager.deleteTaskById(idRequest);

                httpExchange.sendResponseHeaders(200, 0);
            } else {
                bodyJson = "Задачи под таким id не найдено";
                httpExchange.sendResponseHeaders(404, 0);
            }
        }

        private void updateEpicOnRequest(Epic epic, HttpExchange httpExchange) throws IOException {
            int idRequest = Integer.parseInt(httpExchange
                    .getRequestURI().getRawQuery().substring("id=".length()));

            if (taskManager.getAllTaskList().containsKey(idRequest)) {
                taskManager.updateTask(idRequest, epic.getName(), epic.getDescription());

                httpExchange.sendResponseHeaders(201, 0);
            } else {
                bodyJson = "Задачи под таким id не найдено";
                httpExchange.sendResponseHeaders(404, 0);
            }
        }

        private void startTaskOnRequest(HttpExchange httpExchange) throws IOException {
            int idRequest = Integer.parseInt(httpExchange
                    .getRequestURI().getRawQuery().substring("id=".length()));

            if (taskManager.getAllTaskList().containsKey(idRequest)) {
                taskManager.startTask(idRequest);

                httpExchange.sendResponseHeaders(201, 0);
            } else {
                bodyJson = "Задачи под таким id не найдено";
                httpExchange.sendResponseHeaders(404, 0);
            }
        }

        private void finishTaskOnRequest(HttpExchange httpExchange) throws IOException {
            int idRequest = Integer.parseInt(httpExchange
                    .getRequestURI().getRawQuery().substring("id=".length()));

            if (taskManager.getAllTaskList().containsKey(idRequest)) {
                taskManager.finishTask(idRequest);

                httpExchange.sendResponseHeaders(201, 0);
            } else {
                bodyJson = "Задачи под таким id не найдено или над ней на начата работа";
                httpExchange.sendResponseHeaders(404, 0);
            }
        }

    }

}
