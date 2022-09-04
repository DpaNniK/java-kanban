package kanbanTest;

import master.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.function.Executable;
import task.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;

public abstract class TaskManagerTest<T extends TaskManager> {

    T taskManager;

    private void createTaskEpicSubtask() {
        taskManager.createNewEpic("epic", "desc");
        taskManager.createNewSubtask("sub", "desc", 0);
        taskManager.createNewTask("task", "desc");
    }

    private void createEpicWithSubtask() {
        taskManager.createNewEpic("epic", "desc");
        taskManager.createNewSubtask("sub", "desc", 0);
        taskManager.createNewSubtask("sub1", "desc", 0);
    }

    //Тест на создание новой Task. Проверяю, что она создалась и присутствует в taskList и AllTaskList
    @Test
    void testOnCreateNewTask() {
        taskManager.createNewTask("task", "desc");
        Task task = taskManager.getAllTaskList().get(0);
        assertNotNull(task, "Задача не найдена");

        final HashMap<Integer, Task> tasksFromAllTaskList = taskManager.getAllTaskList();
        assertNotNull(tasksFromAllTaskList, "Задача не добавляется в список всех задач");
        assertEquals(1, tasksFromAllTaskList.size(), "Ошибочное число списка всех задач");

        final HashMap<Integer, Task> tasksFromTaskList = taskManager.getTaskList();
        assertNotNull(tasksFromTaskList, "Задача не добавляется в список задач");
        assertEquals(1, tasksFromTaskList.size(), "Ошибочное число задач");

        assertEquals(Type.TASK, task.getTypeOfTask(), "Созданная задача имеет неправильный тип");
    }

    //Тест на создание нового Epic.
    @Test
    void testOnCreateNewEpic() {
        taskManager.createNewEpic("epic", "desc0");
        Epic epic = (Epic) taskManager.getAllTaskList().get(0);
        assertNotNull(epic, "Эпик не найден");

        final HashMap<Integer, Task> epicFromAllTaskList = taskManager.getAllTaskList();
        assertNotNull(epicFromAllTaskList, "Задача не добавляется в список всех задач");
        assertEquals(1, epicFromAllTaskList.size(), "Ошибочное число списка всех задач");

        final HashMap<Integer, Epic> epicFromEpicList = taskManager.getEpicList();
        assertNotNull(epicFromEpicList, "Эпик не добавляется в список эпиков");
        assertEquals(1, epicFromEpicList.size(), "Ошибочное число задач");

        assertEquals(Type.EPIC, epic.getTypeOfTask(), "Созданная эпик имеет неправильный тип");
    }

    //Тест на создание нового Subtask. Здесь же проверяется метод добавления sub в epic - addSubtaskInEpic(),
    // т.к. он реализован в createNewSubtask.
    @Test
    void testOnCreateNewSubtask() {
        taskManager.createNewEpic("epic", "desc");
        taskManager.createNewSubtask("sub", "desc", 0);
        Subtask subtask = (Subtask) taskManager.getAllTaskList().get(1);
        assertNotNull(subtask, "Подзадача не найдена");

        final HashMap<Integer, Task> subtaskFromAllTaskList = taskManager.getAllTaskList();
        assertNotNull(subtaskFromAllTaskList, "Задача не добавляется в список всех задач");
        assertEquals(2, subtaskFromAllTaskList.size(), "Ошибочное число списка всех задач");

        final HashMap<Integer, Subtask> subtaskFromSubtaskList = taskManager.getSubtaskList();
        assertNotNull(subtaskFromSubtaskList, "Подзадача не добавляется в список подзадач");
        assertEquals(1, subtaskFromSubtaskList.size(), "Ошибочное число задач");

        assertEquals(Type.SUBTASK, subtask.getTypeOfTask(), "Созданная подзадача имеет неправильный тип");

        assertNotNull(taskManager.getSubtaskForEpic(0), "Поздача не принадлежит эпику");

        assertEquals(1, taskManager.getSubtaskForEpic(0).size(), "Эпик имеет лишние подзадачи");
    }

    //Проверка метода по удалению подзадачи из эпика
    @Test
    void testDeleteSubtaskForEpicMethod() {
        createTaskEpicSubtask();
        Subtask subtask = (Subtask) taskManager.getAllTaskList().get(1);

        taskManager.deleteSubtaskForEpic(0, subtask);
        assertEquals(0, taskManager.getSubtaskForEpic(0).size(), "Подзадача не удалена из эпика");
    }

    //Тестирование метода по удалению всех задач из общего списка и из конкретного(epicList, subList, taskList)
    @Test
    void testDeleteAllTaskListsMethod() {
        createTaskEpicSubtask();

        assertEquals(3, taskManager.getAllTaskList().size()
                , "Ошибка при добавлении задач в общий лист");

        taskManager.deleteAllTaskList();

        assertEquals(0, taskManager.getAllTaskList().size()
                , "Не все задачи удалились из общего списка");
        assertEquals(0, taskManager.getTaskList().size()
                , "Не удалена задача из спика Task");
        assertEquals(0, taskManager.getEpicList().size()
                , "Не удален эпик из спика Epic");
        assertEquals(0, taskManager.getSubtaskList().size()
                , "Не удалена подзадача из спика Subtask");
    }

    //Тестирование метода startTask, который меняет статус задачи на IN_PROGRESS;
    @Test
    void checkOnInProgressStatusForStartTaskMethod() {
        createTaskEpicSubtask();

        taskManager.startTask(1);
        taskManager.startTask(2);

        assertEquals(Status.IN_PROGRESS, taskManager.getAllTaskList().get(1).getStatus()
                , "Статус задачи не сменился на IN_PROGRESS");
        assertEquals(Status.IN_PROGRESS, taskManager.getAllTaskList().get(2).getStatus()
                , "Статус подзадачи не сменился на IN_PROGRESS");
    }

    //Тестирование метода finishTask, который меняет статус задачи на IN_PROGRESS;
    @Test
    void checkOnDoneStatusForFinishTaskMethod() {
        createTaskEpicSubtask();

        taskManager.startTask(1);
        taskManager.startTask(2);
        taskManager.finishTask(1);
        taskManager.finishTask(2);

        assertEquals(Status.DONE, taskManager.getAllTaskList().get(1).getStatus()
                , "Статус задачи не сменился на Done");
        assertEquals(Status.DONE, taskManager.getAllTaskList().get(2).getStatus()
                , "Статус подзадачи не сменился на Done");
    }

    //Тест статуса эпика (new) с пустым список подзадач
    @Test
    void checkEpicNewStatusWithEmptySubtaskList() {
        Epic epic = new Epic("epic", "desc");

        assertEquals(0, epic.getSubtaskListForEpic().size(), "В эпике есть подзадачи");
        assertEquals(Status.NEW, epic.getStatus(), "Статус эпика с пустым списком не равен NEW");
    }

    //Проверка статуса эпика (new), если ему добавить подзадачи, статусы которых new
    @Test
    void checkEpicNewStatusWithStatusNewSubtask() {
        createEpicWithSubtask();
        Epic epic = (Epic) taskManager.getAllTaskList().get(0);

        assertEquals(Status.NEW, epic.getStatus()
                , "Эпик с подзадачами статусами new имеет некорректный статус");
    }

    //Тест статуса эпика (IN_PROGRESS) с двумя подзадачами - 1)In_Progress 2)New. После того, как одна из подзадача
    //Получила статус IN_PROGRESS, статус Эпика должен смениться также на IN_PROGRESS
    @Test
    void checkEpicInProgressStatusWithTwoSubtaskInProgressAndNew() {
        createEpicWithSubtask();
        Epic epic = (Epic) taskManager.getAllTaskList().get(0);

        taskManager.startTask(1);

        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статус эпика не сменился на IN_PROGRESS");
    }

    //Тест статуса эпика, когда все подзадачи в нем имеют статус DONE
    @Test
    void checkEpicDoneStatusWithTwoSubtaskDoneStatus() {
        createEpicWithSubtask();
        Epic epic = (Epic) taskManager.getAllTaskList().get(0);

        taskManager.startTask(1);
        taskManager.finishTask(1);
        taskManager.startTask(2);
        taskManager.finishTask(2);

        assertEquals(Status.DONE, epic.getStatus(), "Статус эпика не сменился на DONE");
    }

    //Тест статуса эпика, когда одна подзадача в нем - DONE, а другая NEW
    @Test
    void checkEpicInProgressStatusWithTwoSubtaskDoneAndNewStatus() {
        createEpicWithSubtask();
        Epic epic = (Epic) taskManager.getAllTaskList().get(0);

        taskManager.startTask(1);
        taskManager.finishTask(1);

        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статус эпика не сменился на IN_PROGRESS");
    }

    //Тест статуса эпика, когда все его подзадачи имеют статус IN_PROGRESS
    @Test
    void checkEpicInProgressStatusWithTwoSubtaskInProgressStatus() {
        createEpicWithSubtask();
        Epic epic = (Epic) taskManager.getAllTaskList().get(0);

        taskManager.startTask(1);
        taskManager.startTask(2);

        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статус эпика не сменился на IN_PROGRESS");
    }

    //Тест метода deleteTaskById(int id) для задачи типа Task
    @Test
    void checkDeleteTaskByIdForTask() {
        createTaskEpicSubtask();
        taskManager.deleteTaskById(2);

        assertEquals(0, taskManager.getTaskList().size(), "Задача не удалилась из списка");
    }

    //Тест метода deleteTaskById(int id) для задачи типа Epic
    @Test
    void checkDeleteTaskByIdForEpic() {
        createEpicWithSubtask();

        assertEquals(1, taskManager.getEpicList().size(), "Эпик не добавился в лист");
        assertEquals(2, taskManager.getSubtaskList().size(), "Подзадача не добавилась в лист");

        taskManager.deleteTaskById(0);

        assertEquals(0, taskManager.getEpicList().size(), "Эпик не удалился из списка");
        assertEquals(0, taskManager.getSubtaskList().size()
                , "Вместе с эпиком не удалились его подзадачи");
    }

    //Тест метода deleteTaskById(int id) для Subtask. По логике - если остается одна подзадача у эпика,
    //То ее статус присваивается эпику. Если подзадач нет, то эпик превращается в Task.
    @Test
    void checkDeleteTaskByIdForSubtask() {
        createEpicWithSubtask();
        Epic epic = (Epic) taskManager.getAllTaskList().get(0);

        taskManager.startTask(1);
        taskManager.finishTask(1);
        taskManager.startTask(2);
        taskManager.deleteTaskById(2);

        assertEquals(Status.DONE, epic.getStatus()
                , "Статус Эпика не изменился на статус последней оставшейся у него подзадачи");

        taskManager.deleteTaskById(1);

        assertEquals(Type.TASK, taskManager.getAllTaskList().get(0).getTypeOfTask()
                , "Эпик без подзадач не превратился в Task");
    }

    //Тест метода updateTask для Task. При обновлении статус должен смениться на new.
    @Test
    void checkUpdateTaskForTask() {
        taskManager.createNewTask("task", "desc");
        Task task = taskManager.getAllTaskList().get(0);
        taskManager.startTask(task.getId());
        taskManager.finishTask(task.getId());

        assertEquals(Status.DONE, task.getTaskStatus(), "Статус задачи равен DONE");

        taskManager.updateTask(0, "task1", "desc1");

        assertEquals(Status.NEW, taskManager.getAllTaskList().get(task.getId()).getTaskStatus()
                , "Статус обновленной задачи не изменился на NEW");
        assertEquals(1, taskManager.getAllTaskList().size()
                , "Обновленная задача не заменила предыдущую в общем листе задач");
        assertEquals(1, taskManager.getTaskList().size()
                , "Обновленная задача не заменила предыдущую в листе задач");
    }

    //Тест метода updateTask для Epic. При обновлении статус должен остаться прежним, как и число подзадач
    @Test
    void checkUpdateTaskForEpic() {
        createEpicWithSubtask();
        Epic epic = (Epic) taskManager.getAllTaskList().get(0);
        Subtask subtask1 = (Subtask) taskManager.getAllTaskList().get(1);
        taskManager.startTask(subtask1.getId());
        taskManager.finishTask(subtask1.getId());

        assertEquals(Status.IN_PROGRESS, epic.getTaskStatus(), "Статус эпика не равен IN_PROGRESS");

        taskManager.updateTask(0, "epic1", "desc1");

        assertEquals(Status.IN_PROGRESS, taskManager.getAllTaskList().get(epic.getId()).getTaskStatus()
                , "Статус обновленного эпика изменился");
        assertEquals(3, taskManager.getAllTaskList().size()
                , "Обновленный эпик не заменил предыдущий в общем листе задач");
        assertEquals(1, taskManager.getEpicList().size()
                , "Обновленный эпик не заменил предыдущий в листе эпиков");
        assertEquals(2, taskManager.getSubtaskForEpic(0).size()
                , "Обновленный эпик не сохранил список подзадач из предыдущего");
    }

    //Тест метода updateTask для Subtask. Если до обновления статус одной подзадачи был IN_PROGRESS,
    //а статус другой - NEW, то после обновления задачи со статусом IN_PROGRESS, статус
    //эпика должен измениться на NEW;
    @Test
    void checkUpdateTaskForSubtask() {
        createEpicWithSubtask();
        Epic epic = (Epic) taskManager.getAllTaskList().get(0);
        Subtask subtask1 = (Subtask) taskManager.getAllTaskList().get(1);
        taskManager.startTask(subtask1.getId());

        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статус эпика не изменился на IN_PROGRESS");

        taskManager.updateTask(subtask1.getId(), "newSub", "desc");

        assertEquals(Status.NEW, taskManager.getAllTaskList().get(subtask1.getId()).getTaskStatus()
                , "Статус обновленной подзадачи не изменился");
        assertEquals(3, taskManager.getAllTaskList().size()
                , "Обновленная подзадача не заменила предыдущую в общем листе задач");
        assertEquals(2, taskManager.getSubtaskList().size()
                , "Обновленная подзадача не заменила подзадачу в листе подзадач");
        assertEquals(2, taskManager.getSubtaskForEpic(0).size()
                , "Обновленная подзадача пропала из эпика");
    }

    //Тестирование того, что новая история создается пустой
    @Test
    void checkGetHistoryMethodForEmptyHistoryList() {
        assertEquals(0, taskManager.getHistory().size(), "Новая история создается не пустой");
    }

    //Тестирование того, что в историю не добавляется дублирование
    @Test
    void checkGetHistoryMethodForDuplicationTask() {
        createTaskEpicSubtask();
        taskManager.getTaskById(0);
        taskManager.getTaskById(0);

        assertEquals(1, taskManager.getHistory().size()
                , "В списке с историей встречается дублирование");
    }

    //Тест метода getHistory, который возвращает лист с историей просмотров
    @Test
    void checkGetHistoryMethodForTaskSubtaskAndEpic() {
        createTaskEpicSubtask();
        taskManager.getTaskById(0);
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getTaskById(1);

        assertEquals(3, taskManager.getHistory().size(), "Неверное добавление просмотров историю");
    }

    //Проверка метода setTime для задачи типа Task
    @Test
    void checkSetTimeMethodForTask() {
        taskManager.createNewTask("task", "desc");
        Task task = taskManager.getAllTaskList().get(0);
        taskManager.setTimeForTask(0, "17:44 08.05.22", 20);
        Duration duration = Duration.ofMinutes(20);
        LocalDateTime startTime = LocalDateTime.parse("17:44 08.05.22", Task.DATE_TIME_FORMATTER);
        LocalDateTime endTime = startTime.plus(duration);

        assertEquals(duration, task.getDuration(), "Ошибка при добавление продолжительности задачи");
        assertEquals(startTime, task.getStartTime(), "Ошибка при добавление времени старта задачи");
        assertEquals(endTime, task.getEndTime(), "Ошибка рассчета времени окончания задачи");
    }

    //Проверка метода setTime для subtask и epic
    @Test
    void checkSetTimeMethodForSubtaskAndEpic() {
        createEpicWithSubtask();
        Epic epic = (Epic) taskManager.getAllTaskList().get(0);
        taskManager.setTimeForTask(1, "17:44 08.05.22", 20);
        taskManager.setTimeForTask(2, "15:44 08.05.22", 10);
        Duration duration = Duration.ofMinutes(20).plus(Duration.ofMinutes(10));
        LocalDateTime startTime = LocalDateTime.parse("15:44 08.05.22", Task.DATE_TIME_FORMATTER);
        LocalDateTime endTime = LocalDateTime.parse("18:04 08.05.22", Task.DATE_TIME_FORMATTER);

        assertEquals(duration, epic.getDuration(), "Ошибка рассчета продолжительности эпика");
        assertEquals(startTime, epic.getStartTime(), "Ошибка определения стартового времени эпика");
        assertEquals(endTime, epic.getEndTime(), "Ошибка рассчета времени окончания эпика");
    }

    //Проверка на создание исключения при неправильном id задачи для метода getSubtaskForEpic(int id)
    @Test
    void shouldReturnErrorForGetSubtaskForEpic() {
        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                generateExecutableForgetSubtaskForEpic()
        );
        assertEquals("Задачи под таким id не найдено.", ex.getMessage());
    }

    //Проверка на создание исключения при неправильном id задачи для метода getTaskById(int id)
    @Test
    void shouldReturnErrorForGetTaskById() {
        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                generateExecutableGetTaskById()
        );
        assertEquals("Задачи под таким id не найдено.", ex.getMessage());
    }

    //Проверка на создание исключения при попытка вызова метода startTask у Epic
    @Test
    void shouldReturnErrorStartTaskForEpic() {
        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                generateExecutableStartTaskForEpic()
        );
        assertEquals("Статус эпика изменить нельзя", ex.getMessage());
    }

    //Проверка на создание исключения при попытке вызова метода startTask у не существующей задачи
    @Test
    void shouldReturnErrorStartTaskForEmptyTask() {
        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                generateExecutableStartTaskForEmptyTask()
        );
        assertEquals("Задачи под таким номером нет", ex.getMessage());
    }

    //Проверка на создание исключения при попытка вызова метода finishTask у Epic
    @Test
    void shouldReturnErrorFinishTaskForEpic() {
        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                generateExecutableFinishTaskForEpic()
        );
        assertEquals("Стаус эпика изменить нельзя", ex.getMessage());
    }

    //Проверка на создание исключения при попытке вызова метода finishTask у не существующей задачи
    @Test
    void shouldReturnErrorFinishTaskForEmptyTask() {
        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                generateExecutableFinishTaskForEmptyTask()
        );
        assertEquals("Задачи под таким номером нет или над ней не была начата работа", ex.getMessage());
    }

    //Проверка на создание исключения при попытке вызова метода createNewSubtask с указанием id несуществующего Эпика
    @Test
    void shouldReturnErrorCreateNewSubtaskForEmptyEpic() {
        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                generateExecutableDeleteTaskById()
        );
        assertEquals("Задачи под таким id не найдено", ex.getMessage());
    }

    //Проверка на создание исключения при попытке вызова метода deleteTaskById для неверного id
    @Test
    void shouldReturnErrorForDeleteTaskById() {
        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                generateExecutableCreateNewSubtask()
        );
        assertEquals("Эпика под таким номером нет", ex.getMessage());
    }

    //Проверка на создание исключения при попытке вызова метода updateTask для неверного id
    @Test
    void shouldReturnErrorForUpdateTask() {
        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                generateExecutableUpdateTask()
        );
        assertEquals("Задачи под таким id не найдено", ex.getMessage());
    }

    //Проверка на создание исключения при попытки установить время несуществующей задаче
    @Test
    void shouldReturnErrorForSetStartTime() {
        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                generateExecutableSetTimeForNullTask()
        );
        assertEquals("Задачи под таким id не найдено", ex.getMessage());
    }

    //Проверка на создание исключения при попытки установить время эпику
    @Test
    void shouldReturnErrorForSetStartTimeForEpic() {
        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                generateExecutableSetTimeForTwoEqualsTask()
        );
        assertEquals("Время задачи пересекается с временем выполнения другой задачи", ex.getMessage());
    }

    //Проверка на создание исключения при попытки установить пересекающееся время
    @Test
    void shouldReturnErrorForSetStartTimeForTwoEqualsTime() {
        IllegalArgumentException ex = Assertions.assertThrows(
                IllegalArgumentException.class,
                generateExecutableSetTimeForEpic()
        );
        assertEquals("Время начала эпика определяется временем старта его ранней подзадачи", ex.getMessage());
    }

    //Генерация Executable для метода getSubtaskForEpic(int id)
    private Executable generateExecutableForgetSubtaskForEpic() {
        return () -> taskManager.getSubtaskForEpic(0);
    }

    //Генерация Executable для метода getTaskById(int id)
    private Executable generateExecutableGetTaskById() {
        return () -> taskManager.getTaskById(0);
    }

    //Генерация Executable для метода startTask, вызванного у Эпика. Сменить статус эпика пользователю нельзя.
    private Executable generateExecutableStartTaskForEpic() {
        createTaskEpicSubtask();
        return () -> taskManager.startTask(0);
    }

    //Генерация Executable для метода startTask, вызванного у не существующей задачи
    private Executable generateExecutableStartTaskForEmptyTask() {
        return () -> taskManager.startTask(0);
    }

    //Генерация Executable для метода finishTask, вызванного у Эпика. Сменить статус эпика пользователю нельзя.
    private Executable generateExecutableFinishTaskForEpic() {
        createTaskEpicSubtask();
        taskManager.startTask(1);
        return () -> taskManager.finishTask(0);
    }

    //Генерация Executable для метода finishTask, вызванного у не существующей задачи
    private Executable generateExecutableFinishTaskForEmptyTask() {
        return () -> taskManager.finishTask(0);
    }

    //Генерация Executable для метода createNewSubtask, вызванного не для эпика
    private Executable generateExecutableCreateNewSubtask() {
        return () -> taskManager.createNewSubtask("sub", "desc", 2);
    }

    //Генерация Executable для метода deleteTaskById, вызванной у несуществующей задачи
    private Executable generateExecutableDeleteTaskById() {
        return () -> taskManager.deleteTaskById(1);
    }

    //Генерация Executable для метода updateTask, вызванной у несуществующей задачи
    private Executable generateExecutableUpdateTask() {
        return () -> taskManager.updateTask(1, "new", "desc");
    }

    //Генерация Executable для метода setTime, вызванного у несуществующей задачи
    private Executable generateExecutableSetTimeForNullTask (){
        return () -> taskManager.setTimeForTask(0,"",5);
    }

    //Генерация Executable для метода setTime, вызванного у эпика
    private Executable generateExecutableSetTimeForEpic (){
        createEpicWithSubtask();
        return () -> taskManager.setTimeForTask(0,"17:14 08.05.22",5);
    }

    //Генерация Executable для метода setTime, когда задачи пересекаются по времени
    private Executable generateExecutableSetTimeForTwoEqualsTask (){
        createEpicWithSubtask();
        taskManager.setTimeForTask(1, "17:14 08.05.22",50);
        return () -> taskManager.setTimeForTask(2,"17:24 08.05.22",5);
    }
}
