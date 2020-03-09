package entities;

import java.util.LinkedList;

public class Queue extends LinkedList<Pointer> {
    public boolean add(int index, Dir d) {
        Pointer l = new Pointer(index, d);
        if (super.contains(l))
            return false;
        else {
            super.addLast(l);
            return true;
        }
    }

    public Pointer remove() {
        return super.removeFirst();
    }
}
