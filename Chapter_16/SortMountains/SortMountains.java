package SortMountains;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

public class SortMountains {
    LinkedList <Mountain> mtn = new LinkedList<Mountain>();

    class NameCompare implements Comparator<Mountain>{
        public int compare(Mountain one, Mountain two) {
            return one.getName().compareTo(two.getName());
        }
    }
    class HeightCompare implements Comparator<Mountain>{
        public int compare(Mountain one, Mountain two){
            return (two.getHeight() - one.getHeight());
        }
    }

    public static void main(String[] args) {
        new SortMountains().go();
    }
    public void go(){
        mtn.add(new Mountain("Long-Range ", 14255));
        mtn.add(new Mountain("Elbert ", 14433 ));
        mtn.add(new Mountain("Marun ", 14156 ));
        mtn.add(new Mountain("Kasl", 14265 ));

        System.out.println("In order of addition:\n" + mtn);
        NameCompare nc = new NameCompare();
        Collections.sort(mtn, nc);
        System.out.println("By name:\n" + mtn);
        HeightCompare hc = new HeightCompare();
        Collections.sort(mtn, hc);
        System.out.println("By height:\n" + mtn);
    }
}

