package StaticSuper;

public class StaticTests extends StaticSuper {
    static int rand;

    static {
        rand = (int) (Math.random() * 6);
        System.out.println("Static block " + rand);
    }
    StaticTests() {
        System.out.println("Constructor");
    }

    public static void main(String[] args) {
        System.out.println("In main");
        StaticTests st = new StaticTests();
    }
}
