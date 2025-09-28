import java.util.Set;

class Process {
    // This is the object that will be used for the synchronized lock.
    private final Object lock = new Object();

    // The produce method will be executed by one thread
    public void produce() throws InterruptedException {
        synchronized (lock) { // Thread 1 acquires the lock on the 'lock' object
            System.out.println("Running the produce method...");

            // The wait() method is called.
            // 1. Thread 1 releases the lock on 'lock'.
            // 2. Thread 1 goes into a waiting state.
            lock.wait();

            System.out.println("...Back in the produce method after being notified.");
        }
    }

    // The consume method will be executed by another thread
    public void consume() throws InterruptedException {
        // Sleep to ensure the producer thread gets to the wait() call first.
        Thread.sleep(1000);

        synchronized (lock) { // Thread 2 tries to acquire the lock. It succeeds because T1 released it.
            System.out.println("Running the consume method...");

            // The notify() method is called.
            // This wakes up the waiting producer thread (T1).
            // T1 is now ready to re-acquire the lock, but it must wait for T2 to finish.
            lock.notify();

            System.out.println("...After the notify() method call in consume method.");
        } // Thread 2 releases the lock here. T1 can now acquire it.
    }
}


public class WaitNotifyExample {
    public static void main(String[] args) {
        // We create a SINGLE Process object. This is crucial for shared locks.
        Process process = new Process();

        // Thread 1 will run the produce() method
        Thread t1 = new Thread(() -> {
            try {
                process.produce();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "Producer-Thread");

        // Thread 2 will run the consume() method
        Thread t2 = new Thread(() -> {
            try {
                process.consume();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "Consumer-Thread");

        // Start both threads
        t1.start();
        t2.start();
    }
}
