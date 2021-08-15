package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;
import java.util.List;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    private String nextInput=null;
    
    //map size max: 24x18
    private Level level;
    
    public static void main(String[] args) {
        Main app = new Main();
        app.setShowSettings(false);
        app.setDisplayStatView(false);
        AppSettings settings = new AppSettings(true);
        settings.setHeight(900);
        settings.setWidth(1200);
        settings.setFrameRate(60);
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        Globals.am=assetManager;
        initInputs();
        level = new Level("test");
        guiNode.attachChild(level.getSprites());
        level.recalculateRules();
        flyCam.setEnabled(false);
    }

    @Override
    public void simpleUpdate(float tpf) {
        Globals.time+=tpf;
        if (nextInput!=null){
            //process game loop
            doPlayerMove();
            Level.printLevel();
            applyOtherMoves();
            level.recalculateRules();
            nextInput=null;
        }
        level.update(tpf);
    }
    
    private void doPlayerMove(){
        switch (nextInput){
            case "up":move(8);break;
            case "down":move(2);break;
            case "left":move(4);break;
            case "right":move(6);break;
            case "advance":;break;
        }
        Level.instance.addToHistory();
    }
    
    private void move(int direction){
        List<Tile> tiles = level.getTilesWhere(new TileCriteria(){
            @Override
            public boolean matches(Tile t) {
                return t.isYou();
            }
        });
        for (Tile tile : tiles){
            int x = tile.getLocation().x;
            int y = tile.getLocation().y;
            if (Level.push(x,y,direction)){
                if (tile.move(direction)){
                    Level.pull(x,y, Utils.swapDirection(direction));
                }
            }
        }
        Level.instance.doQueuedMoves();
    }
    
    private void applyOtherMoves(){
    
    }
    
    private void initInputs(){
        inputManager.clearMappings();
        inputManager.addMapping("down", new KeyTrigger(KeyInput.KEY_NUMPAD2));
        inputManager.addMapping("down", new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping("left", new KeyTrigger(KeyInput.KEY_NUMPAD4));
        inputManager.addMapping("left", new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("right", new KeyTrigger(KeyInput.KEY_NUMPAD6));
        inputManager.addMapping("right", new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("up", new KeyTrigger(KeyInput.KEY_NUMPAD8));
        inputManager.addMapping("up", new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("advance", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("escape", new KeyTrigger(KeyInput.KEY_ESCAPE));
        inputManager.addMapping("restart", new KeyTrigger(KeyInput.KEY_R));
        inputManager.addMapping("undo", new KeyTrigger(KeyInput.KEY_BACK));
        inputManager.addListener(actionListener, "down","left","right","up","advance","escape","restart","undo");
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
    private ActionListener actionListener = new ActionListener(){
        @Override
        public void onAction(String name, boolean pressed, float tpf) {
            if ("down".equals(name) && pressed){
                nextInput=name;
            } else if ("left".equals(name) && pressed){
                nextInput=name;
            } else if ("right".equals(name) && pressed){
                nextInput=name;
            } else if ("up".equals(name) && pressed){
                nextInput=name;
            } else if ("advance".equals(name) && pressed){
                nextInput=name;
            } else if ("escape".equals(name) && pressed){
                System.exit(1);
            } else if ("restart".equals(name) && pressed){
                Level.reset();
            } else if ("undo".equals(name) && pressed){
                Level.instance.undo();
            }
        }
        
    };
}
