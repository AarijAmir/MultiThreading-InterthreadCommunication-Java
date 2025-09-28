import java.util.LinkedList;

public class ProducerConsumerExample1 {

    // --- 1. SharedBuffer Class (The Shared Resource and Synchronization Logic) ---
    static class SharedBuffer {
        // The shared resource is a LinkedList
        private final LinkedList<Integer> buffer = new LinkedList<>();
        private final int capacity;

        public SharedBuffer(int capacity) {
            this.capacity = capacity;
        }

        /**
         * Producer method: Adds an item to the buffer.
         * Uses 'buffer' object as the synchronization monitor (lock).
         */
        public void produce(int item) throws InterruptedException {
            // Locks the 'buffer' object, ensuring only one thread accesses it at a time.
            synchronized (buffer) {
                // Use a while loop to check the condition. This is safer than 'if'
                // because it guards against "spurious wakeups" (waking up without a notify).
                while (buffer.size() == capacity) {
                    System.out.println("Buffer is full. Producer is waiting...");
                    // Releases the lock on 'buffer' and moves the thread to the waiting state.
                    buffer.wait();
                }

                buffer.add(item);
                System.out.println("Produced item: " + item + ". Buffer size: " + buffer.size());
                // Wakes up ALL waiting threads (Consumer or other Producers) that the condition has changed.
                buffer.notifyAll();
            }
        }

        /**
         * Consumer method: Removes an item from the buffer.
         * Uses 'buffer' object as the synchronization monitor (lock).
         */
        public void consume() throws InterruptedException {
            synchronized (buffer) {
                // Wait if the buffer is empty
                while (buffer.isEmpty()) {
                    System.out.println("Buffer is empty. Consumer is waiting...");
                    // Releases the lock and waits.
                    buffer.wait();
                }

                int item = buffer.removeFirst();
                System.out.println("Consumed item: " + item + ". Buffer size: " + buffer.size());
                // Wakes up ALL waiting threads (Producer or other Consumers).
                buffer.notifyAll();
            }
        }
    }

    // --- 2. ProducerThread Class (The Producer Thread) ---
    static class ProducerThread extends Thread {
        private final SharedBuffer buffer;

        public ProducerThread(SharedBuffer buffer) {
            this.buffer = buffer;
        }

        @Override
        public void run() {
            try {
                // Producer attempts to produce 5 items
                for (int i = 0; i < 5; i++) {
                    buffer.produce(i);
                    // Sleep to slow down the production rate relative to consumption
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // --- 3. ConsumerThread Class (The Consumer Thread) ---
    static class ConsumerThread extends Thread {
        private final SharedBuffer buffer;

        public ConsumerThread(SharedBuffer buffer) {
            this.buffer = buffer;
        }

        @Override
        public void run() {
            try {
                // Consumer attempts to consume 5 items
                for (int i = 0; i < 5; i++) {
                    buffer.consume();
                    // Sleep to simulate longer processing time
                    Thread.sleep(200);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // --- 4. Main Class (Entry Point) ---
    public static void main(String[] args) {
        // Create a single shared buffer with a capacity of 3
        SharedBuffer buffer = new SharedBuffer(3);

        // Create the producer and consumer threads, sharing the single buffer instance
        ProducerThread producer = new ProducerThread(buffer);
        ConsumerThread consumer = new ConsumerThread(buffer);

        System.out.println("Starting Producer-Consumer simulation with buffer capacity: 3");

        producer.start();
        consumer.start();
    }
}

