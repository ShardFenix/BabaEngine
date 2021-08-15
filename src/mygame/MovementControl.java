package mygame;

import com.jme3.math.Vector2f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Shard
 */
public class MovementControl extends AbstractControl {

    private Vector2f start;
    private Vector2f end;
    private List<Vector2f> bezierCurve=new LinkedList<>();
    private float totalDuration;
    private float time;
    
    public MovementControl(Spatial s, Point newLocation, float duration){
        spatial=s;
        start=new Vector2f(s.getLocalTranslation().x,s.getLocalTranslation().y);
        end=new Vector2f(newLocation.x*Sprite.SPRITE_SIZE,newLocation.y*Sprite.SPRITE_SIZE);
        bezierCurve.clear();
        bezierCurve.add(start);
        bezierCurve.add(end);
        totalDuration=duration;
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        time+=tpf;
        if (time>totalDuration){
            enabled=false;
            return;
        }
        float perc = time/totalDuration;
        Vector2f location = end.subtract(start).multLocal(perc).addLocal(start);
        spatial.setLocalTranslation(location.x,location.y,spatial.getLocalTranslation().z);
        //Vector2f location = Utils.bezier(bezierCurve,perc);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        
    }
    
}
