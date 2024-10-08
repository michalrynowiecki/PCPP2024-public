// For week 4
// raup@itu.dk * 2023-09-16

package lecture04;

import java.util.Queue;
import java.util.LinkedList;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

import java.util.concurrent.Semaphore;

public interface BoundedBuffer<E> {
    public void put(E e);
    public E take();
    public boolean isEmpty();
    public boolean isFull();
}

class BoundedBufferMonitor<E> implements BoundedBuffer<E> {

    private final E[] items; // visibility ensured by default value (see constructor)
    private int putPtr, takePtr, numElems; // visibility ensured by default value (see constructor)

    private final Lock lock;
    private final Condition notFull;
    private final Condition notEmpty;

    public BoundedBufferMonitor(int capacity) {
        // \forall i \in [0,capacity] . items[i] == null
        this.items    = (E[]) new Object[capacity];
        this.putPtr   = 0;
        this.takePtr  = 0;
        this.numElems = 0;

        this.lock     = new ReentrantLock();
        this.notFull  = lock.newCondition();
        this.notEmpty = lock.newCondition();

    }

    public void put(E element) {
        lock.lock();
        try {
            while(numElems >= items.length) notFull.await();
            doInsert(element);
            numElems++;
            // notEmpty.signal();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public E take() {
        lock.lock();
        try {
            while(numElems <= 0) notEmpty.await();
            E result = doTake();
            numElems--;
            notFull.signal();
            return result;
        }
        catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } finally {
            lock.unlock();
        }
    }

    public boolean isEmpty() {
        lock.lock();
        try {
            return numElems <= 0;
        }
        finally {
            lock.unlock();
        }
    }

    public boolean isFull() {
        lock.lock();
        try {
            return numElems >= items.length;
        }
        finally {
            lock.unlock();
        }
    }



    /*** Auxiliary DS handling methods ***/
    private void doInsert(E element) {
        items[putPtr] = element;
        if (++putPtr == items.length) putPtr = 0;
    }

    private E doTake() {
        E result = items[takePtr];
        items[takePtr] = null; // For garbage collection
        if (++takePtr == items.length) takePtr = 0;
        return result;
    }
}
