package io.github.ultimateboomer.motionviewer;

import java.util.AbstractList;

/**
 * Circular buffer implementation
 */
@SuppressWarnings("unchecked")
public class CircularList<E> extends AbstractList<E> {

    protected final int size;

    protected E[] array;
    protected int head = 0;

    public CircularList(int size) {
        this.size = size;
        this.array = (E[]) new Object[size];
    }

    @Override
    public boolean add(E t) {
        head = (head + 1) % size;
        array[head] = t;
        return true;
    }

    @Override
    public E get(int index) {
        if (index > this.size()) throw new ArrayIndexOutOfBoundsException(index);
        return array[Math.floorMod(head - index, size)];
    }

    @Override
    public int size() {
        return this.size;
    }
}
