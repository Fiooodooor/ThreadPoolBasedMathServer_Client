
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ThreadPool
{
    private final int threadsNumber;
    private final antWorker[] threadsArray;
    private final LinkedList<Runnable> workQueue;

    public ThreadPool(int theThreadsN)
    {
        if(theThreadsN<=0) { theThreadsN = 1; }
        this.threadsNumber = theThreadsN;
        workQueue = new LinkedList<>();
        threadsArray = new antWorker[threadsNumber];

        for (int i=0; i<threadsNumber; i++) {
            threadsArray[i] = new antWorker();
            threadsArray[i].start();
        }
    }

    public void addJob(Runnable r) {
        synchronized(workQueue) {
            workQueue.addLast(r);
            workQueue.notify();
        }
    }

    private class antWorker extends Thread {
        @Override
        public void run() {
            Runnable r;

            while (true) {
                synchronized(workQueue) {
                    while (workQueue.isEmpty()) {
                        try
                        {
                            workQueue.wait();
                        }
                        catch (InterruptedException ignoredExc)
                        {
                            Logger.getLogger(antWorker.class.getName()).log(Level.INFO, ignoredExc.toString());
                        }
                    }
                    r = (Runnable) workQueue.removeFirst();
                }
                try {
                    r.run();
                }
                catch (RuntimeException e) {
                    System.err.println("[THREAD] One of threads raised runtime exception. Closing it.");
                    Logger.getLogger(antWorker.class.getName()).log(Level.WARNING, "TH_IR:" + this.getId() + " :: " + e.getMessage());
                }
            }
        }
    }
    protected int getThreadsNumber() {
        return threadsNumber;
    }
}

