import java.util.LinkedList;
import java.util.List;

public class ProducerConsumerExample {

    // --- 1. SharedBuffer Class (The Synchronized Resource) ---
    static class SharedBuffer {
        // This is the shared resource, a simple List.
        private final List<Integer> buffer = new LinkedList<>();
        // The maximum number of items the buffer can hold.
        private final int capacity = 5;

        // The synchronized produce() method. The lock is on the 'this' object (SharedBuffer instance).
        public synchronized void produce() throws InterruptedException {
            // Check the WAIT condition using 'if' (Though 'while' is safer against spurious wakeups, 'if' is used here as per the lecture example's structure).
            if (buffer.size() == capacity) {
                System.out.println("Buffer is full! Producer is waiting...");
                // Releases the lock on 'this' and waits until notified.
                wait();
            }

            // Action: If not waiting, fill the buffer.
            System.out.println("Producer is adding items.");
            for (int i = 0; i < capacity; i++) {
                // In a real scenario, this item might be created elsewhere.
                buffer.add(i);
                System.out.println("  Added: " + i);
            }

            // Signal: The buffer is now full. Wakes up one waiting thread (the Consumer).
            notify();
            // Add a small delay for clearer output separation
            Thread.sleep(500);
        }

        // The synchronized consume() method. The lock is on the 'this' object.
        public synchronized void consume() throws InterruptedException {
            // Check the WAIT condition. The consumer waits until the buffer is full (size == capacity).
            if (buffer.size() < capacity) {
                System.out.println("Buffer not full yet. Consumer is waiting...");
                // Releases the lock on 'this' and waits until notified.
                wait();
            }

            // Action: If not waiting, empty the buffer.
            System.out.println("Consumer is removing items.");
            while (!buffer.isEmpty()) {
                int removedItem = buffer.remove(0);
                System.out.println("  Removed: " + removedItem);
                Thread.sleep(300); // Simulate processing time
            }

            // Signal: The buffer is now empty. Wakes up one waiting thread (the Producer).
            notify();
            // Add a small delay for clearer output separation
            Thread.sleep(500);
        }
    }

    // --- 2. Producer Class (The Runnable Task) ---
    static class Producer implements Runnable {
        private final SharedBuffer sharedBuffer;

        public Producer(SharedBuffer sharedBuffer) {
            this.sharedBuffer = sharedBuffer;
        }

        @Override
        public void run() {
            // Run indefinitely
            while (true) {
                try {
                    sharedBuffer.produce();
                } catch (InterruptedException e) {
                    // Gracefully handle thread interruption
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    // --- 3. Consumer Class (The Runnable Task) ---
    static class Consumer implements Runnable {
        private final SharedBuffer sharedBuffer;

        public Consumer(SharedBuffer sharedBuffer) {
            this.sharedBuffer = sharedBuffer;
        }

        @Override
        public void run() {
            // Run indefinitely
            while (true) {
                try {
                    sharedBuffer.consume();
                } catch (InterruptedException e) {
                    // Gracefully handle thread interruption
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    // --- 4. Main Method (The Execution Entry Point) ---
    public static void main(String[] args) {
        // Create a single shared buffer instance.
        SharedBuffer sharedBuffer = new SharedBuffer();

        // Create the Producer and Consumer threads, both using the SAME shared buffer.
        Thread producerThread = new Thread(new Producer(sharedBuffer), "Producer-Thread");
        Thread consumerThread = new Thread(new Consumer(sharedBuffer), "Consumer-Thread");

        // Start both threads
        System.out.println("--- Starting Producer-Consumer Simulation ---");
        producerThread.start();
        consumerThread.start();
    }
}