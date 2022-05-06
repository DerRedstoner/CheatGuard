package de.derredstoner.anticheat.util.evicting;

import java.util.Collection;
import java.util.LinkedList;

public class EvictingList<T> extends LinkedList<T> {

    private int maxSize;

    public EvictingList(int maxSize) { this.maxSize = maxSize; }

    public EvictingList(Collection<? extends T> c, int maxSize) {
        super(c);
        this.maxSize = maxSize;
    }

    public int getMaxSize() { return this.maxSize; }

    public boolean add(T t) {
        if(size() >= this.maxSize) {
            removeFirst();
        }
        return super.add(t);
    }

    public boolean isFull() {
        return size() >= this.maxSize;
    }

}
