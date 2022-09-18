package kanbanTest;

import httpResourse.KVServer;
import master.FileBackedTaskManager;
import master.HTTPTaskManager;
import master.Managers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class HTTPTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private final String port = "8080";
    private KVServer kvServer;

    @BeforeEach
    void beforeEach() {
        try {
            kvServer = new KVServer();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        kvServer.start();
        taskManager = Managers.getHTTPTaskManager(port);
    }

    @AfterEach
    void stop() {
        kvServer.stop();
    }

    //Тестирование восстановления пустого менеджера
    @Test
    void testSaveAndRestoreHTTPManagerWithEmptyTask() {
        HTTPTaskManager httpTaskManager1 = HTTPTaskManager.loadFromServer(port);

        assertNotNull(httpTaskManager1, "Ошибка при создании объекта класса FileBackedTaskManager");

        assertNull(httpTaskManager1.getTaskList()
                , "Восстановленный менеджер имеет лишние задачи");
        assertNull(httpTaskManager1.getEpicList()
                , "Восстановленный менеджер имеет лишние эпики");
        assertNull(httpTaskManager1.getSubtaskList()
                , "Восстановленный менеджер имеет лишние подзадачи");
        assertEquals(0, httpTaskManager1.getHistory().size()
                , "Восстановленный менеджер не должен иметь истории просмотров");
    }

    //Тестирование сохранения и восстановления списка задач с пустой историей просмотров
    @Test
    void testSaveAndRestoreHTTPManagerWithEmptyHistory() {
        taskManager.createNewEpic("epic", "desc");
        taskManager.createNewTask("task", "desc");
        taskManager.createNewSubtask("subtask", "desc", 0);

        HTTPTaskManager httpTaskManager1 = HTTPTaskManager.loadFromServer(port);

        assertNotNull(httpTaskManager1.getEpicList()
                , "Не восстановились эпики предыдущего менеджера");
        assertNotNull(httpTaskManager1.getTaskList()
                , "Не восстановились задачи предыдущего менеджера");
        assertNotNull(httpTaskManager1.getSubtaskList()
                , "Не восстановились подзадачи предыдущего менеджера");
        assertEquals(0, httpTaskManager1.getHistory().size()
                , "Ошибочное восстановление истории просмотров");
    }

    //Тестирование сохранения и восстановления задач с историей
    @Test
    void testSaveAndRestoreHTTPManagerWithHistory() {
        taskManager.createNewEpic("epic", "desc");
        taskManager.createNewTask("task", "desc");
        taskManager.createNewSubtask("subtask", "desc", 0);
        taskManager.getTaskById(1);

        HTTPTaskManager httpTaskManager1 = HTTPTaskManager.loadFromServer(port);

        assertEquals(1, httpTaskManager1.getHistory().size()
                , "Ошибочное восстановление истории просмотров");
    }
}
