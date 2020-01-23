package MyRunnable;

public class MyRunnable implements Runnable {
    public void run(){
        go();
    }
    public void go(){
        doMore();
    }
    public void doMore(){
        System.out.println("top on the stack");
    }
}
class TestDrive {
    public static void main(String[] args) {
        Runnable theJob = new MyRunnable();
        Thread t = new Thread(theJob);
        t.start();
        try {
            Thread.sleep(2000);
        }catch (InterruptedException ex){ex.printStackTrace();}
        System.out.println("return in method main");
    }
}

