import java.util.LinkedList;
import java.util.List;

public class ProducerConsumerExample2 {
    public static void main(String[] args) {
        // Create a single shared buffer instance.
        SharedBuffer sharedBuffer = new SharedBuffer();

        // Create the Producer and Consumer threads, passing the same shared buffer.
        Thread producerThread = new Thread(new Producer(sharedBuffer));
        Thread consumerThread = new Thread(new Consumer(sharedBuffer));

        // Start both threads
        producerThread.start();
        consumerThread.start();
    }
}


class SharedBuffer {
    // This is the shared resource. All threads will access this list.
    private final List<Integer> buffer = new LinkedList<>();
    // The maximum number of items the buffer can hold.
    private final int capacity = 5;

    // The synchronized produce() method. The lock is on the 'this' object.
    public synchronized void produce() throws InterruptedException {
        // Condition: The buffer is full, so the producer must wait.
        if (buffer.size() == capacity) {
            System.out.println("Buffer is full! Producer is waiting...");
            wait(); // Releases the lock and goes to sleep.
        }

        // Action: If the buffer is not full, add items.
        System.out.println("Producer is adding items.");
        for (int i = 0; i < capacity; i++) {
            buffer.add(i);
            System.out.println("Added: " + i);
        }

        // Signal: The producer has finished filling the buffer.
        notify(); // Wakes up one waiting thread (the consumer).
    }

    // The synchronized consume() method. The lock is on the 'this' object.
    public synchronized void consume() throws InterruptedException {
        // Condition: The buffer is empty, so the consumer must wait.
        if (buffer.size() < capacity) {
            System.out.println("Buffer not full yet. Consumer is waiting...");
            wait(); // Releases the lock and goes to sleep.
        }

        // Action: If the buffer is full, remove all items.
        System.out.println("Consumer is removing items.");
        while (!buffer.isEmpty()) {
            int removedItem = buffer.remove(0);
            System.out.println("Removed: " + removedItem);
            Thread.sleep(300); // Simulate work
        }

        // Signal: The consumer has finished emptying the buffer.
        notify(); // Wakes up one waiting thread (the producer).
    }
}

class Producer implements Runnable {
    private final SharedBuffer sharedBuffer;

    public Producer(SharedBuffer sharedBuffer) {
        this.sharedBuffer = sharedBuffer;
    }

    @Override
    public void run() {
        while (true) {
            try {
                sharedBuffer.produce();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

// A separate thread for the Consumer
class Consumer implements Runnable {
    private final SharedBuffer sharedBuffer;

    public Consumer(SharedBuffer sharedBuffer) {
        this.sharedBuffer = sharedBuffer;
    }

    @Override
    public void run() {
        while (true) {
            try {
                sharedBuffer.consume();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
