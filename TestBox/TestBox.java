package TestBox;

public class TestBox {
    Integer i;
    Integer j;

    public static void main(String[] args) {
        TestBox t = new TestBox();
        t.go();
    }

    public void go() {
        j = i;
        System.out.println(j);
        System.out.println(i);

    }
}
