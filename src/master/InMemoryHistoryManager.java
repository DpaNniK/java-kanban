package master;

import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager { //Класс, реализующий интерфес HistoryManager
    private List<Task> historyList; // В этом списке будет обновляться история просмотров
    private final CustomLinkedList<Task> customLinkedList; // Лист, сохраняющий порядок просмотров задач

    public InMemoryHistoryManager() {
        this.historyList = new ArrayList<>();
        this.customLinkedList = new CustomLinkedList<>();
    }

    @Override
    public List<Task> getHistory() {
        return historyList;
    }

    @Override
    public void add(Task task) { //Метод вызывается каждый раз, когда происходит запрос в main метода getTuskById()
        customLinkedList.linkLast(task); //Задача добавляется в конец списка
        historyList = customLinkedList.getTasks(); //Полученная история передается в historyList
    }

    @Override
    public void remove(int id) { //Метод удаления вызывается при удалении пользователем задачи из списка
        customLinkedList.removeNode(customLinkedList.getNodeById(id)); //Удаление узла из связанного списка
    }

    public static class CustomLinkedList<T extends Task> {
        public Node<T> head; // 1-й элемент списка
        public Node<T> tail; // последний элемент
        public Node<T> local; // локальная переменная для хранения последнего значения head
        //Нужна, чтобы при повторном запросе истории в список не добавлялась последняя задача из списка предыдщуего запроса
        private int size = 0;
        public ArrayList<T> historyTaskList = new ArrayList<>();
        public HashMap<Integer, Node<T>> historyHashList = new HashMap<>();

        public void linkLast(T task) {
            if (historyHashList.containsKey(task.getId())) { //Перед тем, как добавить задачу, происходит проверка -
                removeNode(historyHashList.get(task.getId())); //есть ли она уже в списке, если да - то удаляется из
            } //предыдущего места в списке и добавляется вниз списка
            final Node<T> oldTail = tail;
            final Node<T> newNode = new Node<>(oldTail, task, null);
            tail = newNode;
            if (oldTail == null) {
                head = newNode;
            } else {
                oldTail.next = newNode;
            }
            historyHashList.put(task.getId(), newNode); //Новая задача добавляется в мапу
            size++;
        }

        public final ArrayList<T> getTasks() { //метод по получению значения листа CustomLinkedList
            for (int i = 0; i < size; i++) {
                if (head == null) { //проверка нужна, чтобы в список при нескольких запросах getTasks() не добавлялась
                    head = local.next; //последняя запись из истории предыдущего запроса
                }
                if (head == tail) { //когда список дойдет до последнего элемента, local переменная сохранит текущее
                    historyTaskList.add(tail.task); //значение head, в свою очередь значение head станет = null
                    local = head;//т.о. при следующем вызове метода getTasks() переменаня head получит следующее значение
                    head = head.next;//на которое ссылается переменная local, тем самым, в историю не будет добавлена
                    break; //последняя запись из предыдщего запроса истории
                } else {
                    historyTaskList.add(head.task);
                    head = head.next;
                }
            }
            return historyTaskList;
        }

        public void removeNode(Node<T> node) { //Удаление узла из LinkedList
            if (historyHashList.containsValue(node)) { //Проверка на наличие узла
                historyTaskList.remove(node.task); // удаление узла
            }
        }

        public Node<T> getNodeById(Integer id) { //Метод для получения узла через id его задачи
            return historyHashList.get(id); //используется в методе remove(int id);
        }
    }
}
