package mygame;

import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.texture.Texture;

public class SceneUtils {
    
    public static Material newBasicMat(String textureName){
        Material mat = new Material(Globals.am,"Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", loadTexture(textureName));
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        return mat;
    }
    
    public static Texture loadTexture(String textureName){
        try {
            Texture t = Globals.am.loadTexture("Textures/"+textureName+".png");
            t.setWrap(Texture.WrapMode.EdgeClamp);
            t.setMagFilter(Texture.MagFilter.Nearest);
            return t;
        } catch (Exception e){
            return Globals.am.loadTexture("Textures/missing.png");
        }
    }
    
}
