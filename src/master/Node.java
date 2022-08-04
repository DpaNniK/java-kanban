package master;

import task.Task;

public class Node <T extends Task> {
    public  T task;
    public Node<T> next;
    public Node<T> prev;

    public Node(Node<T> prev, T task, Node<T> next) {
        this.task = task;
        this.next = next;
        this.prev = prev;
    }
}
