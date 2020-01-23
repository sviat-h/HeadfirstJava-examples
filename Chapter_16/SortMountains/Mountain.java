package SortMountains;

public class Mountain implements Comparable<Mountain> {
    String name;
    int height;

    public String getName() {
        return name;
    }

    public int getHeight() {
        return height;
    }

    public int compareTo(Mountain m){
        return name.compareTo(m.getName());
    }
    public String toString(){
        return name + " " + height;
    }
    Mountain (String n, int h){
        name = n;
        height = h;
    }
}

