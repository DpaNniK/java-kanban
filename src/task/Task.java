package task;

public class Task {  //класс-предок для всех задач
    protected String name;
    protected String description;
    protected String taskStatus;
    protected String typeOfTask;
    protected Integer id;
    protected Status status = new Status();
    protected Type type = new Type();


    public Task(String name, String description) {
        this.taskStatus = status.taskNew;
        this.name = name;
        this.description = description;
        this.typeOfTask = type.task;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getStatus() {
        return this.taskStatus;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void startTask() {
        taskStatus = status.taskInProgress;
    }

    public void finishTask() {
        taskStatus = status.taskDone;
    }

    public void createNewTask() {
        taskStatus = status.taskNew;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    @Override
    public String toString() { //переобределен мтеод toString для удобного вида println
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", taskStatus='" + taskStatus + '\'' +
                ", id=" + id +
                ", type=" + typeOfTask +
                '}';
    }
}
