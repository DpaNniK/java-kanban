package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeSet;

public class Epic extends Task {

    private final HashMap<Integer, Subtask> subtaskListForEpic;
    Comparator<Task> comparator =  Comparator.comparing(Task::getStartTime
            , Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getId);

    public Epic(String name, String description) {
        super(name, description);
        this.typeOfTask = Type.EPIC;
        this.subtaskListForEpic = new HashMap<>();
    }

    //Метод по установки endTime для эпика
    @Override
    public void getEndTime (String localDataTime, int duration) {
        TreeSet<Task> sortTimeSubtaskList = new TreeSet<>(comparator);
        int durationSum = 0;
        for (int id : subtaskListForEpic.keySet()) {
            sortTimeSubtaskList.add(subtaskListForEpic.get(id));
        }
        for (Task subtask : sortTimeSubtaskList) {
            if(subtask.getStartTime() == null){
                break;
            }
            durationSum += subtask.getDuration().toMinutes();
        }
        this.startTime = sortTimeSubtaskList.first().getStartTime();
        this.duration = Duration.ofMinutes(durationSum);
        findLastEndTime(sortTimeSubtaskList);
    }

    public void putSubtaskForEpic(Subtask subtask) { //добавление подзадачи в эпик
        subtaskListForEpic.put(subtask.id, subtask);
    }

    public void removeSubtaskForEpic(Subtask subtask) { //удаление подзадачи из эпика
        subtaskListForEpic.remove(subtask.id);
    }

    //Метод по присвоению эпику время окончания самой поздней задачи
    public void findLastEndTime(TreeSet<Task> set) {
        for(Task task : set) {
            if (task.getStartTime() == null) {
                break;
            }
            this.endTime = task.getEndTime();
        }
    }

    public HashMap<Integer, Subtask> getSubtaskListForEpic() { // получить список подзадач
        return subtaskListForEpic;
    }


}
