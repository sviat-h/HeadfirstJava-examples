package TestLifeSupportSim;

public class SimUnit {
    String botType;
    SimUnit(String type) {
        botType = type;
    }
    int powerUse(){
        if ("thermal machine".equals(botType)){
            return 2;
        } else {
            return 4;
        }
    }
}
