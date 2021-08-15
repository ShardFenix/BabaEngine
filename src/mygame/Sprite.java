package mygame;

import com.jme3.math.FastMath;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.Quad;
import java.awt.Point;

public class Sprite extends Node {
    
    public final static int SPRITE_SIZE=50;
    
    private String textureName;
    private Geometry g;
    
    private float[] texCoord = new float[8];
    
    private Tile tile;
    private Point lastPosition=new Point();
    
    private float layer;
    
    public Sprite(String textureName){
        if (textureName.matches("\\w+_\\d")){
            this.textureName=textureName.substring(0,textureName.indexOf('_'));
        } else {
            this.textureName=textureName;
        }
        init();
    }
    
    public Sprite(){}
    
    private void init(){
        Quad q = new Quad(SPRITE_SIZE,SPRITE_SIZE);
        g = new Geometry("Sprite-"+textureName,q);
        g.setMaterial(SceneUtils.newBasicMat(textureName));
        attachChild(g);
        g.getMesh().setBuffer(VertexBuffer.Type.TexCoord, 2,texCoord);
    }
    
    public void update(float tpf){
        if (tile.needsSpriteUpdate()){
            textureName=tile.getName();
            g.setMaterial(SceneUtils.newBasicMat(textureName));
            tile.setReloadSprite(false);
        }

        
        int column = tile.getFacing()/2-1;
        int row = (int)(Globals.time*4)%3;
        texCoord[0]=.25f*column;
        texCoord[1]=FastMath.ONE_THIRD*(row);
        texCoord[2]=.25f*(column+1);
        texCoord[3]=FastMath.ONE_THIRD*(row);
        texCoord[4]=.25f*(column+1);
        texCoord[5]=FastMath.ONE_THIRD*(row+1);
        texCoord[6]=.25f*column;
        texCoord[7]=FastMath.ONE_THIRD*(row+1);
        
        g.getMesh().setBuffer(VertexBuffer.Type.TexCoord, 2,texCoord);
        
        if (positionChanged()){
            //add control
            this.removeControl(MovementControl.class);
            MovementControl c = new MovementControl(this,tile.getLocation(),0.12f);
            lastPosition.x=tile.getLocation().x;
            lastPosition.y=tile.getLocation().y;
            this.addControl(c);
        }
        //setLocalTranslation(tile.getLocation().x*50,tile.getLocation().y*50,layer);
    }
    
    private boolean positionChanged(){
        return tile.getLocation().x != lastPosition.x || tile.getLocation().y != lastPosition.y;
    }
    
    public void setTile(Tile t){
        this.tile=t;
        setLocalTranslation(tile.getLocation().x*SPRITE_SIZE,tile.getLocation().y*SPRITE_SIZE,layer);
        lastPosition.x=tile.getLocation().x;
        lastPosition.y=tile.getLocation().y;
    }
    
    
}
