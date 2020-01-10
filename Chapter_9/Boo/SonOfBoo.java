package Boo;

public class SonOfBoo extends Boo {
    public SonOfBoo() {
        super("boo");
    }
    public SonOfBoo(int i) {
        super("Fred");
    }
    public SonOfBoo(String s) {
        super(42);
    }
    public SonOfBoo(int i, int j) {
        super("man", j);
    }
}
