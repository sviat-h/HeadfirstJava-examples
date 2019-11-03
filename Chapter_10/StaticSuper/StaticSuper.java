package StaticSuper;

class StaticSuper {
    static {
        System.out.println("Parents static block");
    }
    StaticSuper() {
        System.out.println("Parents constructor");
    }
}
