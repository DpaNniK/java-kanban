package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {  //класс-предок для всех задач
    protected String name;
    protected String description;
    protected Status taskStatus;

    protected LocalDateTime startTime;

    protected LocalDateTime endTime;

    protected Duration duration;
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd.MM.yy");

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
        this.startTime = null;
        this.endTime = null;
        this.duration = null;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    //Метод по установке времени окончания задачи
    public void getEndTime(String localDataTime, int duration) {
        this.startTime = LocalDateTime.parse(localDataTime, DATE_TIME_FORMATTER);
        this.duration = Duration.ofMinutes(duration);
        this.endTime = LocalDateTime.parse(localDataTime, DATE_TIME_FORMATTER)
                .plus(this.duration);
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setTaskStatus(Status taskStatus) {
        this.taskStatus = taskStatus;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }


    public Status getStatus() {
        return this.taskStatus;
    }

    public LocalDateTime getEndTime() {
        return endTime;
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
    public String toString() { //Для Task также переопределен метод вывода в строку
        if (startTime == null) {
            return String.format(id + "," +
                    typeOfTask + "," + name + "," +
                    taskStatus + "," + description + "," +
                    startTime + "," +
                    duration + "," +
                    endTime);
        } else {
            return String.format(id + "," +
                    typeOfTask + "," + name + "," +
                    taskStatus + "," + description + "," +
                    startTime.format(DATE_TIME_FORMATTER) + "," +
                    duration.toMinutes() + "," +
                    endTime.format(DATE_TIME_FORMATTER));
        }
    }

}
