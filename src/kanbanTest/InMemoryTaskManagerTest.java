package kanbanTest;

import master.InMemoryTaskManager;
import master.Managers;
import org.junit.jupiter.api.BeforeEach;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
    }

}
