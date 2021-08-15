package mygame;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Defines a single space on the level
 */
public class Space {
    List<Tile> tiles = new LinkedList<>();
    
    public Space(){}
    
    public void addTile(Tile t){
        tiles.add(t);
    }
    
    public void removeTile(Tile t){
        tiles.remove(t);
    }
    
    public List<Tile> getWords(){
        List<Tile> result = new ArrayList<>();
        for (Tile t : tiles){
            if (t.isWord()){
                result.add(t);
            }
        }
        return result;
    }
    
    public boolean isStop(){
        for (Tile t : tiles){
            if (t.isStop() && !t.isPush()){
                return true;
            }
        }
        return false;
    }
    
    public boolean hasPush(){
        for (Tile t : tiles){
            if (t.isPush()){
                return true;
            }
        }
        return false;
    }
    
    public boolean hasPull(){
        for (Tile t : tiles){
            if (t.isPull()){
                return true;
            }
        }
        return false;
    }

}
