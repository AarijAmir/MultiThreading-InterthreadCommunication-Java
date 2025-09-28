import java.util.LinkedList;
import java.util.List;

class UnsafeSharedBuffer {
    private final List<Integer> buffer = new LinkedList<>();
    private final int capacity = 5;

    public void unsafeProduce(int item) {
        // No check for buffer capacity
        buffer.add(item);
        System.out.println("Unsafely Produced: " + item);
    }

    public int unsafeConsume() {
        // No check for empty buffer
        int item = buffer.remove(0);
        System.out.println("Unsafely Consumed: " + item);
        return item;
    }
}

public class UnsafeProducerConsumerExample {
    public static void main(String[] args) {
        UnsafeSharedBuffer buffer = new UnsafeSharedBuffer();

        // Producer Thread
        Thread producerThread = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                buffer.unsafeProduce(i);
            }
        });

        // Consumer Thread
        Thread consumerThread = new Thread(() -> {
            try {
                // Introduce a delay to guarantee the producer runs first and fills the buffer
                Thread.sleep(100);
                for (int i = 0; i < 10; i++) {
                    buffer.unsafeConsume();
                }
            } catch (Exception e) {
                // This is where the error will be caught
                System.out.println("Error: " + e.getMessage());
            }
        });

        producerThread.start();
        consumerThread.start();
    }
}
