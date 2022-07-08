package task;

import java.util.HashMap;

public class Epic extends Task {

    HashMap<Integer, Subtask> subtaskListForEpic; //лист для хранения подзадач, у каждого эпика свой

    public Epic(String name, String description) {
        super(name, description);
        typeOfTask = type.epic;
        subtaskListForEpic = new HashMap<>();
    }

    public void putSubtaskForEpic(Subtask subtask) { //добавление подзадачи в эпик
        subtaskListForEpic.put(subtask.id, subtask);
    }

    public void removeSubtaskForEpic(Subtask subtask) { //удаление подзадачи из эпика
        subtaskListForEpic.remove(subtask.id);
    }

    public HashMap<Integer, Subtask> getSubtaskListForEpic() { // получить список подзадач
        return subtaskListForEpic;
    }


}