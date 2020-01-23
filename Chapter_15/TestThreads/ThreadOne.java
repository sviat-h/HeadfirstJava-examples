package TestThreads;

public class ThreadOne implements Runnable {
    Accum a = Accum.getAccum();
    public void run(){
        for (int x = 0; x < 98; x++){
            a.updateCounter(1000);
            try {
                Thread.sleep(50);
            }catch (InterruptedException ex) {}
        }
        System.out.println("one " + a.getCount());
    }
}

