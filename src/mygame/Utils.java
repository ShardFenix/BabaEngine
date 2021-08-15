
package mygame;

import java.awt.Point;
import java.util.Collection;

public class Utils {
    
    public static String colectionToString(Collection<String> list){
        StringBuilder sb = new StringBuilder();
        for (String s : list){
            sb.append(s).append(", ");
        }
        int index = sb.lastIndexOf(", ");
        if (index!=-1){
            sb.delete(index,index+2);
        }
        return sb.toString();
    }
    
    public static Point offsetPointByDirection(Point p, int direction){
        return offsetPointByDirection(p.x,p.y, direction);
    }
    
    public static Point offsetPointByDirection(int x, int y, int direction){
        int otherX = x + (direction-1)%3 - 1;
        int otherY = y + (direction-1)/3 - 1;
        return new Point(otherX,otherY);
    }
    
    public static int swapDirection(int direction){
        switch (direction){
            case 2: return 8;
            case 8: return 2;
            case 4: return 6;
            case 6: return 4;
        }
        return -1;
    }
    
    
}
