package task;

public class Task {  //класс-предок для всех задач
    protected String name;
    protected String description;
    protected Status taskStatus;

    public Type getTypeOfTask() {
        return typeOfTask;
    }

    protected Type typeOfTask;
    protected Integer id;


    public Task(String name, String description) {
        this.taskStatus = Status.NEW;
        this.name = name;
        this.description = description;
        this.typeOfTask = Type.TASK;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setTaskStatus(Status taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Status getStatus() {
        return this.taskStatus;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void startTask() {
        taskStatus = Status.IN_PROGRESS;
    }

    public void finishTask() {
        taskStatus = Status.DONE;
    }

    public void createNewTask() {
        taskStatus = Status.NEW;
    }

    public Status getTaskStatus() {
        return taskStatus;
    }

    public Integer getId() {
        return id;
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
