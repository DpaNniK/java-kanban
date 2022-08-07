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
}
