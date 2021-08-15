package mygame;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Tile {

    private String name = "";

    private boolean word;
    private int facing = 6;

    //baba rules
    private boolean stop;
    private boolean push;
    private boolean pull;
    private boolean shift;
    private boolean open;
    private boolean shut;
    private boolean you;
    private boolean defeat;
    private boolean isFloat;
    private boolean weak;
    private boolean win;
    private boolean move;

    private Point location;

    private int queuedMovement = 0;//direction the tile will move in the next action frame

    private boolean reloadSprite = false;


    public Tile(String name) {
        if (name.trim().isEmpty()) {
            throw new RuntimeException("Tile name cannot be empty");
        }
        if (name.matches("\\w+_\\d")){
            //tile name includes facing direction
            facing=name.charAt(name.length()-1)-48;
            if (facing != 2 && facing!=4 && facing!=6 && facing!=8){
                facing=6;
            }
            name=name.substring(0,name.indexOf('_'));
        }
        this.name = name;
        if (wordWords.contains(name)) {
            word = true;
        }
    }

    /**
     * 2=down, 4=left, 6=right, 8=up
     */
    public boolean move(int direction) {
        if (queuedMovement != 0) {
            return true;
        }
        Point nextPoint = Utils.offsetPointByDirection(location, direction);
        Space destination = Level.getSpace(nextPoint);
        if (destination.isStop()) {
            return false;
        }
        queuedMovement = direction;
        return true;
    }

    public void doMove() {
        if (queuedMovement == 0) {
            return;
        }
        Point nextPoint = Utils.offsetPointByDirection(location, queuedMovement);
        Space destination = Level.getSpace(nextPoint);
        Level.getSpace(location).removeTile(this);
        destination.addTile(this);
        location.x = nextPoint.x;
        location.y = nextPoint.y;
        facing = queuedMovement;
        queuedMovement = 0;
    }

    public void transform(String newTile) {
        if (name.trim().isEmpty()) {
            throw new RuntimeException("Tile name cannot be empty");
        }
        this.name = newTile;
        if (wordWords.contains(name)) {
            word = true;
        }
        reloadSprite=true;
    }

    public String getName() {
        return name;
    }

    public boolean isWord() {
        return word;
    }

    public void setWord(boolean word) {
        this.word = word;
    }

    public int getFacing() {
        return facing;
    }

    public void setFacing(int facing) {
        this.facing = facing;
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public boolean isPush() {
        return push;
    }

    public void setPush(boolean push) {
        this.push = push;
    }

    public boolean isPull() {
        return pull;
    }

    public void setPull(boolean pull) {
        this.pull = pull;
    }

    public boolean isShift() {
        return shift;
    }

    public void setShift(boolean shift) {
        this.shift = shift;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isShut() {
        return shut;
    }

    public void setShut(boolean shut) {
        this.shut = shut;
    }

    public boolean isYou() {
        return you;
    }

    public void setYou(boolean you) {
        this.you = you;
    }

    public boolean isDefeat() {
        return defeat;
    }

    public void setDefeat(boolean defeat) {
        this.defeat = defeat;
    }

    public boolean isFloat() {
        return isFloat;
    }

    public void setFloat(boolean isFloat) {
        this.isFloat = isFloat;
    }

    public boolean isWeak() {
        return weak;
    }

    public void setWeak(boolean weak) {
        this.weak = weak;
    }

    public boolean isWin() {
        return win;
    }

    public void setWin(boolean win) {
        this.win = win;
    }

    public boolean isMove() {
        return move;
    }

    public void setMove(boolean move) {
        this.move = move;
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    public void setLocation(int x, int y) {
        this.location = new Point(x, y);
    }

    public void setReloadSprite(boolean reloadSprite) {
        this.reloadSprite = reloadSprite;
    }

    public boolean needsSpriteUpdate() {
        return reloadSprite;
    }
    public final static List<String> subjectWords = new ArrayList<String>() {
        {
            add("baba");
            add("wall");
            add("key");
            add("door");
            add("rock");
            add("belt");
            add("box");
            add("flag");
            add("skull");
        }
    };

    public final static List<String> wordWords = new ArrayList<String>() {
        {
            add("all");
            add("and");
            add("baba");
            add("belt");
            add("box");
            add("defeat");
            add("door");
            add("flag");
            add("float");
            add("has");
            add("is");
            add("key");
            add("make");
            add("move");
            add("not");
            add("on");
            add("open");
            add("push");
            add("pull");
            add("rock");
            add("shift");
            add("shut");
            add("skull");
            add("stop");
            add("text");
            add("wall");
            add("weak");
            add("win");
            add("you");
        }
    };

    public final static List<String> traitWords = new ArrayList<String>() {
        {
            add("defeat");
            add("float");
            add("move");
            add("open");
            add("push");
            add("pull");
            add("shift");
            add("shut");
            add("stop");
            add("weak");
            add("win");
            add("you");
        }
    };

    public final static List<String> predicateWords = new ArrayList<String>() {
        {
            add("has");
            add("is");
            add("make");
        }
    };

    public boolean isSubjectWord() {
        return subjectWords.contains(name);
    }

    public boolean isPredicateWord() {
        return predicateWords.contains(name);
    }

    public boolean isTraitWord() {
        return traitWords.contains(name);
    }

    public String toString() {
        return name;
    }

}
