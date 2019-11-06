package main.utils;

public class Node {

    public long key;
    public Node previous;
    public Node next;

    public void remove() {
        if(this.next != null) {
            this.next.previous = this.previous;
            this.previous.next = this.next;
            this.previous = null;
            this.next = null;
        }

    }

    public boolean hasNext() {
        return this.next != null;
    }
}
