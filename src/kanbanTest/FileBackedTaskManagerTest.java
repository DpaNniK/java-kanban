package kanbanTest;

import master.FileBackedTaskManager;
import master.ManagerSaveException;
import master.Managers;
import master.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    File file = new File("file.backed.csv");
    File emptyFile = new File("empty.backed.csv");
     File notFound = new File("");

    @BeforeEach
    void beforeEach() {
        taskManager = (FileBackedTaskManager) Managers.getFileBackedManager(file);
    }

    //Тестирование восстановления пустого менеджера
    @Test
    void testSaveAndRestoreManagerWithEmptyTask() {
        TaskManager taskManager1 = FileBackedTaskManager.loadFromFile(emptyFile.getPath());

        assertNotNull(taskManager1, "Ошибка при создании объекта класса FileBackedTaskManager");

        assertEquals(0, taskManager1.getAllTaskList().size()
                , "Восстановленный менеджер не пустой");
        assertEquals(0, taskManager1.getTaskList().size()
                , "Восстановленный менеджер имеет лишние задачи");
        assertEquals(0, taskManager1.getEpicList().size()
                , "Восстановленный менеджер имеет лишние эпики");
        assertEquals(0, taskManager1.getSubtaskList().size()
                , "Восстановленный менеджер имеет лишние подзадачи");
    }

    //Тестирование сохранения и восстановления эпиков с историей
    @Test
    void testSaveAndRestoreManagerWithTwoEpic() {
        taskManager.createNewEpic("epic1", "desc");
        taskManager.createNewEpic("epic2", "desc");
        taskManager.getTaskById(1);

        TaskManager taskManager1 = FileBackedTaskManager.loadFromFile(file.getPath());

        assertEquals(taskManager.getEpicList().size(), taskManager1.getEpicList().size()
                , "Эпики не восстановились в новом менеджере");
        assertEquals(1, taskManager.getHistory().size()
                , "Не восстановилась истори просмотров задач");
    }

    //Тестирование сохранения и восстановления списка задач с пустой историей просмотров
    @Test
    void testSaveAndRestoreManagerWithEmptyHistory() {
        taskManager.createNewEpic("epic", "desc");
        taskManager.createNewTask("task", "desc");
        taskManager.createNewSubtask("subtask", "desc", 0);

        TaskManager taskManager1 = FileBackedTaskManager.loadFromFile(file.getPath());

        assertNotNull(taskManager1.getEpicList()
                , "Не восстановились эпики предыдущего менеджера");
        assertNotNull(taskManager1.getTaskList()
                , "Не восстановились задачи предыдущего менеджера");
        assertNotNull(taskManager1.getSubtaskList()
                , "Не восстановились подзадачи предыдущего менеджера");
        assertEquals(0, taskManager1.getHistory().size()
                , "Ошибочное восстановление истории просмотров");
    }

    //Тест, если ошибочно указан файл сохранения, то появится исключение
    @Test
    void shouldReturnErrorForLoadManageFromFile() {
        ManagerSaveException ex = Assertions.assertThrows(
                ManagerSaveException.class,
                generateExecutableForSaveManagerInFile()
        );
        assertEquals("Ошибка записи данных в файл.", ex.getMessage());
    }

    //Тест, если файл восстановления менеджера не найден, то появится исключение
    @Test
    void shouldReturnErrorForSaveManagerInFile() {
        ManagerSaveException ex = Assertions.assertThrows(
                ManagerSaveException.class,
                generateExecutableForLoadManageFromFile()
        );
        assertEquals("Ошибка чтения данных из файла.", ex.getMessage());
    }

    //Генерация Executable для записи в несуществующий файл
    private Executable generateExecutableForSaveManagerInFile() {
        TaskManager taskManager1 = new FileBackedTaskManager(" ");
        return () -> taskManager1.createNewTask("task", "desc");
    }

    //Генерация Executable для восстановления менеджера из несуществующего файла
    private Executable generateExecutableForLoadManageFromFile() {
        return () -> FileBackedTaskManager.loadFromFile(notFound.getPath());
    }
}
