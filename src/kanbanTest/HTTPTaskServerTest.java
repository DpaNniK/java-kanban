package kanbanTest;

import com.google.gson.Gson;
import httpResourse.GsonCreate;
import httpResourse.HTTPTaskServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HTTPTaskServerTest {
    HTTPTaskServer httpTaskServer;
    Gson gson;
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd.MM.yy");

    @BeforeEach
    void beforeEach() throws IOException {
        this.gson = GsonCreate.createGson();
        httpTaskServer = new HTTPTaskServer();
        httpTaskServer.start();
    }

    @AfterEach
    void stop() {
        httpTaskServer.stop(1);
    }

    //Тест добавления задачи по url
    @Test
    void testPOSTMethodForAddTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        Task task = new Task("task", "desc");
        String json = gson.toJson(task);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);

        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
    }

    //Тест добавления эпика по url
    @Test
    void testPOSTMethodForAddEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        Epic epic = new Epic("epic", "desc");
        String json = gson.toJson(epic);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);

        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
    }

    //Тест добавления подзадачи по url
    @Test
    void testPOSTMethodForAddSubtask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        Epic epic = new Epic("epic", "desc");
        String json = gson.toJson(epic);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);

        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        Subtask subtask = new Subtask("subtask", "desc", 0);
        String jsonSub = gson.toJson(subtask);
        final HttpRequest.BodyPublisher bodyEpic = HttpRequest.BodyPublishers.ofString(jsonSub);

        URI urlSub = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest requestSub = HttpRequest.newBuilder().uri(urlSub).POST(bodyEpic).build();
        HttpResponse<String> response = client.send(requestSub, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
    }

    //Тест метода по замене задачи с id, переданном в запросе
    @Test
    void testPOSTMethodOnReplaceTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        Task task = new Task("task", "desc");

        String jsonTask = gson.toJson(task);
        final HttpRequest.BodyPublisher bodyTask = HttpRequest.BodyPublishers.ofString(jsonTask);
        URI urlTask = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest requestTask = HttpRequest.newBuilder().uri(urlTask).POST(bodyTask).build();

        client.send(requestTask, HttpResponse.BodyHandlers.ofString());

        Task newTask = new Task("newTask", "desc");

        String jsonNewTask = gson.toJson(newTask);
        final HttpRequest.BodyPublisher bodyNewTask = HttpRequest.BodyPublishers.ofString(jsonNewTask);
        URI urlNewTask = URI.create("http://localhost:8080/tasks/task/?id=0");
        HttpRequest requestNewTask = HttpRequest.newBuilder().uri(urlNewTask).POST(bodyNewTask).build();

        HttpResponse<String> response = client.send(requestNewTask, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals("newTask", HTTPTaskServer.taskManager.getTaskById(0).getName()
                , "Замены задачи по указанному id не произошло");
    }

    //Тест метода по замене эпика и подзадачи с id, переданном в запросе
    @Test
    void testPOSTMethodOnReplaceEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        Epic epic = new Epic("epic", "desc");
        Subtask subtask = new Subtask("subtask", "desc", 0);

        String jsonEpic = gson.toJson(epic);
        String jsonSub = gson.toJson(subtask);

        final HttpRequest.BodyPublisher bodyEpic = HttpRequest.BodyPublishers.ofString(jsonEpic);
        final HttpRequest.BodyPublisher bodySub = HttpRequest.BodyPublishers.ofString(jsonSub);

        URI urlEpic = URI.create("http://localhost:8080/tasks/epic/");
        URI urlSub = URI.create("http://localhost:8080/tasks/subtask/");

        HttpRequest requestEpic = HttpRequest.newBuilder().uri(urlEpic).POST(bodyEpic).build();
        HttpRequest requestSub = HttpRequest.newBuilder().uri(urlSub).POST(bodySub).build();

        client.send(requestEpic, HttpResponse.BodyHandlers.ofString());
        client.send(requestSub, HttpResponse.BodyHandlers.ofString());

        Epic newEpic = new Epic("newEpic", "desc");
        Subtask newSub = new Subtask("newSub", "desc", 0);

        String jsonNewEpic = gson.toJson(newEpic);
        String jsonNewSub = gson.toJson(newSub);

        final HttpRequest.BodyPublisher bodyNewEpic = HttpRequest.BodyPublishers.ofString(jsonNewEpic);
        final HttpRequest.BodyPublisher bodyNewSub = HttpRequest.BodyPublishers.ofString(jsonNewSub);

        URI urlNewEpic = URI.create("http://localhost:8080/tasks/epic/?id=0");
        URI urlNewSub = URI.create("http://localhost:8080/tasks/subtask/?id=1");

        HttpRequest requestNewEpic = HttpRequest.newBuilder().uri(urlNewEpic).POST(bodyNewEpic).build();
        HttpRequest requestNewSub = HttpRequest.newBuilder().uri(urlNewSub).POST(bodyNewSub).build();

        HttpResponse<String> responseEpic = client.send(requestNewEpic, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responseSub = client.send(requestNewSub, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, responseEpic.statusCode());
        assertEquals(201, responseSub.statusCode());

        assertEquals("newEpic", HTTPTaskServer.taskManager.getTaskById(0).getName()
                , "Замены задачи по указанному id не произошло");
        assertEquals("newSub", HTTPTaskServer.taskManager.getTaskById(1).getName()
                , "Замены задачи по указанному id не произошло");
    }

    //Проверка метода для присвоения задачи статуса IN_PROGRESS
    @Test
    void testPOSTMethodStartTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        Task task = new Task("taskNew", "desc");
        String json = gson.toJson(task);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);

        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        final HttpRequest.BodyPublisher newBody = HttpRequest.BodyPublishers.ofString("");
        URI newUrl = URI.create("http://localhost:8080/tasks/start?id=0");
        HttpRequest newRequest = HttpRequest.newBuilder().uri(newUrl).POST(newBody).build();
        HttpResponse<String> response = client.send(newRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(Status.IN_PROGRESS, HTTPTaskServer.taskManager.getTaskById(0).getTaskStatus()
                , "Замены задачи по указанному id не произошло");
        assertEquals(201, response.statusCode());
    }

    //Проверка метода для присвоения задаче статуса DONE
    @Test
    void testPOSTMethodFinishTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        Task task = new Task("taskNew", "desc");
        String json = gson.toJson(task);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);

        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        final HttpRequest.BodyPublisher newBody = HttpRequest.BodyPublishers.ofString("");
        URI newUrl = URI.create("http://localhost:8080/tasks/task/start?id=0");
        HttpRequest newRequest = HttpRequest.newBuilder().uri(newUrl).POST(newBody).build();
        client.send(newRequest, HttpResponse.BodyHandlers.ofString());

        final HttpRequest.BodyPublisher newEmptyBody = HttpRequest.BodyPublishers.ofString("");
        URI newFinishUrl = URI.create("http://localhost:8080/tasks/task/finish?id=0");
        HttpRequest newFinishRequest = HttpRequest.newBuilder().uri(newFinishUrl).POST(newEmptyBody).build();
        HttpResponse<String> response = client.send(newFinishRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(Status.DONE, HTTPTaskServer.taskManager.getTaskById(0).getTaskStatus()
                , "Замены задачи по указанному id не произошло");
        assertEquals(201, response.statusCode());
    }

    //Тест GET запроса списка всех задач
    @Test
    void testGETMethodTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        Epic epic = new Epic("epic", "desc");
        Subtask subtask = new Subtask("subtask", "desc", 0);

        String jsonEpic = gson.toJson(epic);
        String jsonSub = gson.toJson(subtask);

        final HttpRequest.BodyPublisher bodyEpic = HttpRequest.BodyPublishers.ofString(jsonEpic);
        final HttpRequest.BodyPublisher bodySub = HttpRequest.BodyPublishers.ofString(jsonSub);

        URI urlEpic = URI.create("http://localhost:8080/tasks/epic/");
        URI urlSub = URI.create("http://localhost:8080/tasks/subtask/");

        HttpRequest requestEpic = HttpRequest.newBuilder().uri(urlEpic).POST(bodyEpic).build();
        HttpRequest requestSub = HttpRequest.newBuilder().uri(urlSub).POST(bodySub).build();

        client.send(requestEpic, HttpResponse.BodyHandlers.ofString());
        client.send(requestSub, HttpResponse.BodyHandlers.ofString());

        URI urlGet = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().uri(urlGet).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    //Тест GET запроса задач по приоритету
    @Test
    void testGETMethodGetPrioritizedTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        Task task = new Task("task", "desc");
        Epic epic = new Epic("epic", "desc");
        Subtask subtask = new Subtask("subtask", "desc", 1);

        task.setStartTime(LocalDateTime.parse("17:44 08.05.22", DATE_TIME_FORMATTER));
        task.setDuration(Duration.ofMinutes(10));

        subtask.setStartTime(LocalDateTime.parse("10:44 08.05.22", DATE_TIME_FORMATTER));
        subtask.setDuration(Duration.ofMinutes(10));

        String jsonTask = gson.toJson(task);
        String jsonEpic = gson.toJson(epic);
        String jsonSub = gson.toJson(subtask);

        final HttpRequest.BodyPublisher bodyTask = HttpRequest.BodyPublishers.ofString(jsonTask);
        final HttpRequest.BodyPublisher bodyEpic = HttpRequest.BodyPublishers.ofString(jsonEpic);
        final HttpRequest.BodyPublisher bodySub = HttpRequest.BodyPublishers.ofString(jsonSub);

        URI urlTask = URI.create("http://localhost:8080/tasks/task/");
        URI urlEpic = URI.create("http://localhost:8080/tasks/epic/");
        URI urlSub = URI.create("http://localhost:8080/tasks/subtask/");

        HttpRequest requestTask = HttpRequest.newBuilder().uri(urlTask).POST(bodyTask).build();
        HttpRequest requestEpic = HttpRequest.newBuilder().uri(urlEpic).POST(bodyEpic).build();
        HttpRequest requestSub = HttpRequest.newBuilder().uri(urlSub).POST(bodySub).build();

        client.send(requestTask, HttpResponse.BodyHandlers.ofString());
        client.send(requestEpic, HttpResponse.BodyHandlers.ofString());
        client.send(requestSub, HttpResponse.BodyHandlers.ofString());

        URI urlGet = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(urlGet).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    //Тест GET запроса задачи по id
    @Test
    void testGETMethodGetTaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        Task task = new Task("task", "desc");
        String json = gson.toJson(task);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);

        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        URI urlGet = URI.create("http://localhost:8080/tasks/task?id=0");
        HttpRequest requestGet = HttpRequest.newBuilder().uri(urlGet).GET().build();
        HttpResponse<String> response = client.send(requestGet, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    //Тест GET запроса получения подзадач эпика по его ID
    @Test
    void testGETMethodGetSubtaskForEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        Epic epic = new Epic("epic", "desc");
        Subtask subtask = new Subtask("subtask", "desc", 0);

        String jsonEpic = gson.toJson(epic);
        String jsonSub = gson.toJson(subtask);

        final HttpRequest.BodyPublisher bodyEpic = HttpRequest.BodyPublishers.ofString(jsonEpic);
        final HttpRequest.BodyPublisher bodySub = HttpRequest.BodyPublishers.ofString(jsonSub);

        URI urlEpic = URI.create("http://localhost:8080/tasks/epic/");
        URI urlSub = URI.create("http://localhost:8080/tasks/subtask/");

        HttpRequest requestEpic = HttpRequest.newBuilder().uri(urlEpic).POST(bodyEpic).build();
        HttpRequest requestSub = HttpRequest.newBuilder().uri(urlSub).POST(bodySub).build();

        client.send(requestEpic, HttpResponse.BodyHandlers.ofString());
        client.send(requestSub, HttpResponse.BodyHandlers.ofString());

        URI urlGet = URI.create("http://localhost:8080/tasks/subtask/epic?id=0");
        HttpRequest requestGet = HttpRequest.newBuilder().uri(urlGet).GET().build();

        HttpResponse<String> response = client.send(requestGet, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    //Тестирование получения истории просмотров
    @Test
    void testGETMethodGetHistory() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        Task task = new Task("task", "desc");
        String json = gson.toJson(task);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);

        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        HTTPTaskServer.taskManager.getTaskById(0);

        URI urlGet = URI.create("http://localhost:8080/tasks/history");
        HttpRequest requestGet = HttpRequest.newBuilder().uri(urlGet).GET().build();

        HttpResponse<String> response = client.send(requestGet, HttpResponse.BodyHandlers.ofString());

        assertEquals(1, HTTPTaskServer.taskManager.getHistory().size());
        assertEquals(200, response.statusCode());
    }

    //Тест DELETE-запроса задачи по id
    @Test
    void testDeleteMethodTaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        Epic epic = new Epic("epic", "desc");
        Subtask subtask = new Subtask("subtask", "desc", 0);

        String jsonEpic = gson.toJson(epic);
        String jsonSub = gson.toJson(subtask);

        final HttpRequest.BodyPublisher bodyEpic = HttpRequest.BodyPublishers.ofString(jsonEpic);
        final HttpRequest.BodyPublisher bodySub = HttpRequest.BodyPublishers.ofString(jsonSub);

        URI urlEpic = URI.create("http://localhost:8080/tasks/epic/");
        URI urlSub = URI.create("http://localhost:8080/tasks/subtask/");

        HttpRequest requestEpic = HttpRequest.newBuilder().uri(urlEpic).POST(bodyEpic).build();
        HttpRequest requestSub = HttpRequest.newBuilder().uri(urlSub).POST(bodySub).build();

        client.send(requestEpic, HttpResponse.BodyHandlers.ofString());
        client.send(requestSub, HttpResponse.BodyHandlers.ofString());

        URI urlDelete = URI.create("http://localhost:8080/tasks/task?id=1");

        HttpRequest requestDeleteTaskBuId = HttpRequest.newBuilder().uri(urlDelete).DELETE().build();

        HttpResponse<String> response = client.send(requestDeleteTaskBuId, HttpResponse.BodyHandlers.ofString());

        assertEquals(1, HTTPTaskServer.taskManager.getAllTaskList().size());
        assertEquals(0, HTTPTaskServer.taskManager.getSubtaskList().size());
        assertEquals(200, response.statusCode());
    }

    //Тест DELETE-запроса задачи по id
    @Test
    void testDeleteMethodDeleteAllTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        Epic epic = new Epic("epic", "desc");
        Subtask subtask = new Subtask("subtask", "desc", 0);

        String jsonEpic = gson.toJson(epic);
        String jsonSub = gson.toJson(subtask);

        final HttpRequest.BodyPublisher bodyEpic = HttpRequest.BodyPublishers.ofString(jsonEpic);
        final HttpRequest.BodyPublisher bodySub = HttpRequest.BodyPublishers.ofString(jsonSub);

        URI urlEpic = URI.create("http://localhost:8080/tasks/epic/");
        URI urlSub = URI.create("http://localhost:8080/tasks/subtask/");

        HttpRequest requestEpic = HttpRequest.newBuilder().uri(urlEpic).POST(bodyEpic).build();
        HttpRequest requestSub = HttpRequest.newBuilder().uri(urlSub).POST(bodySub).build();

        client.send(requestEpic, HttpResponse.BodyHandlers.ofString());
        client.send(requestSub, HttpResponse.BodyHandlers.ofString());

        URI urlDelete = URI.create("http://localhost:8080/tasks/task");

        HttpRequest requestDeleteTaskBuId = HttpRequest.newBuilder().uri(urlDelete).DELETE().build();

        HttpResponse<String> response = client.send(requestDeleteTaskBuId, HttpResponse.BodyHandlers.ofString());

        assertEquals(0, HTTPTaskServer.taskManager.getTaskList().size());
        assertEquals(0, HTTPTaskServer.taskManager.getSubtaskList().size());
        assertEquals(200, response.statusCode());
    }

    //Получение ошибки при вызове post метода start без передачи id
    @Test
    void get404NotFoundForPostStartMethod() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/tasks/task/start");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString("");

        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    //Получение ошибки при вызове post метода finish без передачи id
    @Test
    void get404NotFoundForPostFinishMethod() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/tasks/task/finish");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString("");

        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    //Получение ошибки при вызове GET метода по несуществующему адресу
    @Test
    void get404NotFoundForGetMethod() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/tasks/task/404notFound");

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    //Получение ошибки при вызове POST метода с неправильным url
    @Test
    void get404NotFoundForPostMethod() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/tasks/task/404notFound");

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString("");

        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    //Получение ошибки при вызове POST update метода с неправильным id
    @Test
    void get404NotFoundForPostUpdateMethod() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/tasks/task/id=100");

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString("");

        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }
}

