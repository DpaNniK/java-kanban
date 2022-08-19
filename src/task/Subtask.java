package task;

public class Subtask extends Epic {

    Integer idEpic;

    public Subtask(String name, String description, Integer idEpic) {
        super(name, description);
        typeOfTask = Type.SUBTASK;
        this.idEpic = idEpic;
    }

    public Integer getIdEpic() {
        return idEpic;
    }

    @Override
    public String toString() { //Для Subtask также переопределен метод вывода в строку
        return String.format(id + "," +
                typeOfTask + "," + name + "," +
                taskStatus + "," + description + "," + idEpic);
    }
}
