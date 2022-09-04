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

        InMemoryTaskManager inMemoryTaskManager1 = Managers.getDefault();

        assertNotNull(inMemoryTaskManager1, "Ошибочное создание объекта класса InMemoryTaskManager");
        assertEquals(0, inMemoryTaskManager1.getAllTaskList().size()
                ,"Новосозданный объект класса InMemoryTaskManager имеет непустой список задач");
    }

}
