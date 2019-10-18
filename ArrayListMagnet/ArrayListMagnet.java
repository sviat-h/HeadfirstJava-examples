package ArrayListMagnet;

import java.util.ArrayList;

public class ArrayListMagnet {
    public static void main(String[] args) {
        ArrayList<String> a = new ArrayList<>();
        a.add(0, "Zero");
        a.add(1, "One");
        a.add(2, "Two");
        a.add(3, "Three");
        printAL(a);

        if (a.contains("Three")){
            a.add("Four");
        }
        a.remove(2);
        printAL(a);

        if (a.indexOf("Four") != 4) {
            a.add(4, "4.2");
        }
        printAL(a);
        if (a.contains("Two")) {
            a.add("2.2");
        }
        printAL(a);
    }

    public static void printAL(ArrayList<String> al) {
        for (String element : al) {
            System.out.print(element + " ");
        }
        System.out.println(" ");
    }
}
