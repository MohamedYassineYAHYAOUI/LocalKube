package fr.umlv.LocalKube.logs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;


@Component("logBuffer")
class SynchronizedBlockingBuffer<T extends LogModel> {

    @Value("${log.blockingBuffer.size}")
    private int size;
    private final ArrayDeque<T> queue = new ArrayDeque<>();

    /**
     * put and object T in the buffer if the buffer is not full, else wait until
     * methode take is called to add the object
     * @param o object T to add
     * @throws InterruptedException in case thread was interrupted while waiting
     */
    void put(T o) throws InterruptedException{
        synchronized (queue) {
            while (size == queue.size()) {
               queue.wait();
            }
            queue.add(o);
            queue.notifyAll();
        }
    }

    /**
     * take and return object T from buffer if there is one, else wait until
     * methode put is called to add an object to buffer
     * @return the object T
     * @throws InterruptedException if the thread was interrupted while waiting
     */
    T take() throws InterruptedException {
        synchronized (queue) {
            while (queue.isEmpty()) {
                queue.wait();
            }
            queue.notify();
            return queue.remove();
        }
    }


}