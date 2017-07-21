package se.cygni.snake.player.bot.snakey;

/**
 * Created by trivo on 2017-03-31.
 */
public class BonusTracker {

    private int foodOnPath;
    private int nearCollisions;
    private int freeHeadSpaces;
    private int middleBonus;
    private boolean middleReachedY;
    private int killBonus;

    public BonusTracker(){
        this.foodOnPath = 0;
        this.nearCollisions = 0;
        this.freeHeadSpaces = 0;
        this.middleBonus = 0;
        this.killBonus = 0;
    }

    public void foodFound(int value){
        foodOnPath += value;
    }


    public void headFree(){
        freeHeadSpaces++;
    }

    public void targetMiddle(){
        middleBonus ++;
    }



    public void killBonus(){
        killBonus += 55;
    }

    public int getNearCollisions(){
        return nearCollisions;
    }

    public int getFoodOnPath(){
        return foodOnPath;
    }

    public int getFreeHeadSpaces(){
        return freeHeadSpaces;
    }

    public int getMiddleBonus(){
        return middleBonus;
    }

    public int getKillBonus(){
        return killBonus;
    }
}
