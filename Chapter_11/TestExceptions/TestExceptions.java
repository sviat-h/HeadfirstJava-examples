package TestExceptions;

public class TestExceptions {
    public static void main(String[] args) {
        String  test = "Yes!";
        try {
            System.out.println("Start try");
            doRisky(test);
            System.out.println("Stop try");
        } catch (ScaryException se) {
            System.out.println("Scary exception");
        } finally {
            System.out.println("Finally");
        }
        System.out.println("Stop main");
    }
    static void doRisky (String test) throws ScaryException {
        System.out.println("Start dangerous method");
        if ("Yes!".equals(test)) {
            throw new ScaryException();
        }
        System.out.println("Stop dangerous method");
        return;
    }
}
