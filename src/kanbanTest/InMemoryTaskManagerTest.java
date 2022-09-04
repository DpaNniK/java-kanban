package kanbanTest;

import master.InMemoryTaskManager;
import master.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
    }

    //Проверка корректного создания объекта класса InMemoryTaskManager
    @Test
    void testForCorrectCreationInMemoryTaskManager() {
        InMemoryTaskManager inMemoryTaskManager = Managers.getDefault();
        inMemoryTaskManager.createNewTask("task", "desc");

        assertNotNull(inMemoryTaskManager, "Ошибочное создание объекта класса InMemoryTaskManager");
    }

}
