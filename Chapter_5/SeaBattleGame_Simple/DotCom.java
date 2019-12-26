package SeaBattleGame_Simple;
import java.util.ArrayList;

public class DotCom {
    int[] locationCells;
    int numOfHits= 0;

    public void setLocationCells(int[] locs){
        locationCells = locs;
    }
    public String checkYourself(String stringGuess){
        int guess = Integer.parseInt(stringGuess);
        String result = "Miss";

        for (int cell : locationCells) {
            if (guess == cell) {
                result = "Hit";
                numOfHits++;

                break;
            }
        }
        if (numOfHits == locationCells.length) {
            result = "Kill";
        }
        System.out.println(result);
        return result;
    }
}
