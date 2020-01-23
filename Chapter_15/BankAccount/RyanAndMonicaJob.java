package BankAccount;

public class RyanAndMonicaJob implements Runnable{
    private BankAccount account = new BankAccount();

    public static void main(String[] args) {
        RyanAndMonicaJob theJob = new RyanAndMonicaJob();
        Thread one = new Thread(theJob);
        Thread two = new Thread(theJob);
        one.setName("Ryan ");
        two.setName("Monica ");
        one.start();
        two.start();
    }
    public void run(){
        for (int x = 0; x < 10; x++){
            makeWithdrawal(10);
            if (account.getBalance() < 0){
                System.out.println("Exceeding the limit!");
            }
        }
    }
    private synchronized void makeWithdrawal(int amount){
        if (account.getBalance() >= amount){
            System.out.println(Thread.currentThread().getName() + "going to withdraw money");
            try {
                System.out.println(Thread.currentThread().getName() + "go to sleep");
                Thread.sleep(500);
            } catch (InterruptedException ex){ex.printStackTrace();}
            System.out.println(Thread.currentThread().getName() + "awake");
            account.withdraw(amount);
            System.out.println(Thread.currentThread().getName() + "Ends a transaction");
        }
        else {
            System.out.println("Sorry, for client " + Thread.currentThread().getName() + " no enough money");
        }
    }
}

