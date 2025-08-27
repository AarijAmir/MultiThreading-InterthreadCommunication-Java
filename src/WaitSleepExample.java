import java.util.LinkedList;

class SharedBuffer {
    private final LinkedList<Integer> buffer = new LinkedList<>();
    private final int capacity;

    public SharedBuffer(int capacity) {
        this.capacity = capacity;
    }

    // Producer method
    public void produce(int item) throws InterruptedException {
        synchronized (buffer) {
            // Wait if the buffer is full
            while (buffer.size() == capacity) {
                System.out.println("Buffer is full. Producer is waiting...");
                buffer.wait(); // Releases the lock on 'buffer' and waits
            }

            buffer.add(item);
            System.out.println("Produced item: " + item + ". Buffer size: " + buffer.size());
            buffer.notifyAll(); // Notifies all waiting threads that the buffer is no longer empty
        }
    }

    // Consumer method
    public void consume() throws InterruptedException {
        synchronized (buffer) {
            // Wait if the buffer is empty
            while (buffer.isEmpty()) {
                System.out.println("Buffer is empty. Consumer is waiting...");
                buffer.wait(); // Releases the lock on 'buffer' and waits
            }

            int item = buffer.removeFirst();
            System.out.println("Consumed item: " + item + ". Buffer size: " + buffer.size());
            buffer.notifyAll(); // Notifies all waiting threads that there is space in the buffer
        }
    }
}

class ProducerThread extends Thread {
    private final SharedBuffer buffer;

    public ProducerThread(SharedBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 5; i++) {
                buffer.produce(i);
                // Use sleep() to simulate some work and create a race condition
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

class ConsumerThread extends Thread {
    private final SharedBuffer buffer;

    public ConsumerThread(SharedBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 5; i++) {
                buffer.consume();
                // Use sleep() to simulate some work
                Thread.sleep(200);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

public class WaitSleepExample {
    public static void main(String[] args) {
        SharedBuffer buffer = new SharedBuffer(3);

        ProducerThread producer = new ProducerThread(buffer);
        ConsumerThread consumer = new ConsumerThread(buffer);

        producer.start();
        consumer.start();
    }
}
