package kanbanTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.*;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class AllTaskTest {
    /*В этом классе тестирую методы для Task и его наследников. Основные методы - изменение статуса,
    * присвоение id протестированы на объекте класса Task. На наследниках эти же методы тестировать не стал.
    * Для Epic протестировал его дополнительные методы - putSubtaskForEpic и removeSubtaskForEpic.
    * Для Subtask протестировал его единственный доп. метод - getIdEpic.
    * ИЗМЕНЕНИЕ СТАТУСА ЭПИКА в зависимости от СТАТУСА подзадачи будут описаны в классе TaskManagerTest , так как
    * логика по его изменению прописана именно там (методы checkEpicNewStatusWithEmptySubtaskList,
    * checkEpicNewStatusWithStatusNewSubtask, checkEpicInProgressStatusWithTwoSubtaskInProgressAndNew,
    * checkEpicDoneStatusWithTwoSubtaskDoneStatus , checkEpicInProgressStatusWithTwoSubtaskDoneAndNewStatus,
    * checkEpicInProgressStatusWithTwoSubtaskInProgressStatus) */

    private static Epic epic;
    private static Task task;
    private static Subtask subtask;

    @BeforeEach
    void beforeEach() {
        epic = new Epic("epic", "desc");
        task = new Task("task", "desc");
    }

    //Отдельный метод для создания эпика с добавленной в него подзадачей
    void createEpicWithSubtask() {
        epic.setId(1);
        subtask = new Subtask("subtask", "desc", epic.getId());
        subtask.setId(2);
        epic.putSubtaskForEpic(subtask);
    }

    //Проверка типа созданной задачи
    @Test
    void taskTestTypeTask() {
        assertEquals(Type.TASK, task.getTypeOfTask());
    }

    //Проверка, что ново созданная задача имеет статус - NEW
    @Test
    void taskTestOnNewStatus() {
        assertEquals(Status.NEW, task.getStatus());
    }

    //Проверка, что метод startTask меняет статус на IN_PROGRESS
    @Test
    void taskTestOnInProgressStatus() {
        task.startTask();
        assertEquals(Status.IN_PROGRESS, task.getStatus());
    }

    //Проверка, что метод finishTask меняет статус на DONE
    @Test
    void taskTestOnDoneStatus() {
        task.finishTask();
        assertEquals(Status.DONE, task.getTaskStatus());
    }

    //Тестирование присвоения ID
    @Test
    void taskTestSetIdMethod() {
        task.setId(1);
        assertEquals(1, task.getId());
    }

    //Тестирование того, что подзадача добавляется в эпик методом putSubtaskForEpic
    @Test
    void epicTestPutSubtask() {
        HashMap<Integer, Subtask> subtaskList = new HashMap<>();
        createEpicWithSubtask();
        subtaskList.put(subtask.getId(), subtask);
        assertEquals(subtaskList, epic.getSubtaskListForEpic());
    }

    //Тестирование удаления подзадач из эпика
    @Test
    void epicTestRemoveSubtask() {
        createEpicWithSubtask();
        epic.removeSubtaskForEpic(subtask);
        assertEquals(0, epic.getSubtaskListForEpic().size());
    }

    //Тест получения id эпика, которому принадлежит подзадача
    @Test
    void subtaskTestGetEpic() {
        createEpicWithSubtask();
        assertEquals(1, subtask.getIdEpic());
    }
}