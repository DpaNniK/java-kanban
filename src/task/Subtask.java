package task;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Epic {

    Integer idEpic;

    public Subtask(String name, String description, Integer idEpic) {
        super(name, description);
        typeOfTask = Type.SUBTASK;
        this.idEpic = idEpic;
    }

    @Override
    public void getEndTime(String localDataTime, int duration) {
        this.startTime = LocalDateTime.parse(localDataTime, DATE_TIME_FORMATTER);
        this.duration = Duration.ofMinutes(duration);
        this.endTime = LocalDateTime.parse(localDataTime, DATE_TIME_FORMATTER)
                .plus(this.duration);
    }

    public Integer getIdEpic() {
        return idEpic;
    }

    @Override
    public String toString() { //Для Subtask также переопределен метод вывода в строку

        if (startTime == null) {
            return String.format(id + "," +
                    typeOfTask + "," + name + "," +
                    taskStatus + "," + description + ","
                    + idEpic + "," +
                    startTime + "," +
                    duration + "," +
                    endTime);
        } else {
            return String.format(id + "," +
                    typeOfTask + "," + name + "," +
                    taskStatus + "," + description + ","
                    + idEpic + "," +
                    startTime.format(DATE_TIME_FORMATTER) + "," +
                    duration.toMinutes() + "," +
                    endTime.format(DATE_TIME_FORMATTER));
        }
    }
}
