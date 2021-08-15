package mygame;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;

public class Level {

    public final static int MAX_ROWS = 18;
    public final static int MAX_COLUMNS = 24;

    private static String filename = "";

    Space[][] grid = new Space[MAX_ROWS][MAX_COLUMNS];
    Space[][] sandbox = null;

    private Node sprites = new Node();

    private List<Rule> rules = new LinkedList<>();

    public static Level instance;

    public Level() {
        instance = this;
    }

    public Level(String name) {
        init(name);
        instance = this;
    }

    private String fileContents = "";
    
    Stack<String> history = new Stack<>();
    
    private void init(String name) {
        try {
            filename = name;
            fileContents="";
            Scanner scanner = new Scanner(new File("assets/Levels/" + name + ".txt"));
            int row = MAX_ROWS - 1;
            while (scanner.hasNextLine() && row >= 0) {
                String line = scanner.nextLine();
                fileContents+=line+"\n";
                row--;
            }
            load(fileContents);
        } catch (IOException e) {
            e.printStackTrace();
            loadDefaultMap();
        }
    }
    
    private void load(String state) {
        int row = MAX_ROWS - 1, column = 0;
        for (String line : state.split("[\\n\\r]+")) {
            if (row<0)return;
            line = line.replaceAll(",", " , ");
            String[] spaces = line.split(",");
            for (String spaceText : spaces) {
                grid[row][column] = new Space();
                String[] tilesOnSpace = spaceText.split("\\s+");
                for (String sTile : tilesOnSpace) {
                    if (sTile.trim().isEmpty()) {
                        continue;
                    }
                    Tile tile = new Tile(sTile);
                    tile.setLocation(column, row);
                    Sprite sprite = new Sprite(sTile);
                    sprite.setTile(tile);
                    sprites.attachChild(sprite);
                    grid[row][column].addTile(tile);
                }
                column++;
                if (column >= MAX_COLUMNS) {
                    break;
                }
            }
            column = 0;
            row--;
        }
    }

    public static void reset() {
        instance.restart();
    }

    public void restart() {
        for (Spatial child : sprites.getChildren()) {
            child.removeFromParent();
        }
        rules.clear();
        grid = new Space[MAX_ROWS][MAX_COLUMNS];
        instance.init(filename);
        recalculateRules();
    }
    
    public void undo(){
        if (!history.isEmpty()){
                for (Spatial child : sprites.getChildren()) {
                child.removeFromParent();
            }
            rules.clear();
            grid = new Space[MAX_ROWS][MAX_COLUMNS];
            String lastState = history.pop();
            load(lastState);
            recalculateRules();
        }
    }

    private void resetRules() {
        for (Space[] row : grid) {
            for (Space space : row) {
                List<Tile> tiles = space.tiles;
                for (Tile t : tiles) {
                    t.setYou(false);
                    t.setDefeat(false);
                    t.setFloat(false);
                    t.setMove(false);
                    t.setOpen(false);
                    t.setPush(false);
                    t.setPull(false);
                    t.setShift(false);
                    t.setWin(false);
                    t.setShut(false);
                    t.setStop(false);
                    t.setWeak(false);
                }
            }
        }
    }

    public void recalculateRules() {
        resetRules();

        rules.clear();
        List<Sentence> allSentences = new ArrayList<>();

        List<Sentence> newSentences = new ArrayList<>();
        List<Sentence> currentSentences = new ArrayList<>();
        for (Space[] row : grid) {
            for (Space space : row) {
                List<Tile> words = space.getWords();
                if (words.isEmpty()) {
                    allSentences.addAll(currentSentences);
                    currentSentences.clear();
                } else {
                    if (currentSentences.isEmpty()) {
                        for (Tile t : words) {
                            Sentence s = new Sentence();
                            s.addToChain(t);
                            currentSentences.add(s);
                        }
                    } else {
                        for (Sentence s : currentSentences) {
                            newSentences.addAll(s.addToChain(space));
                        }
                        currentSentences.addAll(newSentences);
                        newSentences.clear();
                    }
                }
            }
            allSentences.addAll(currentSentences);
            currentSentences.clear();
        }
        allSentences.addAll(currentSentences);
        currentSentences.clear();

        for (int column = 0; column < MAX_COLUMNS; column++) {
            for (int row = MAX_ROWS - 1; row >= 0; row--) {
                Space space = getSpace(column, row);
                List<Tile> words = space.getWords();
                if (words.isEmpty()) {
                    allSentences.addAll(currentSentences);
                    currentSentences.clear();
                } else {
                    if (currentSentences.isEmpty()) {
                        for (Tile t : words) {
                            Sentence s = new Sentence();
                            s.addToChain(t);
                            currentSentences.add(s);
                        }
                    } else {
                        for (Sentence s : currentSentences) {
                            newSentences.addAll(s.addToChain(space));
                        }
                        currentSentences.addAll(newSentences);
                        newSentences.clear();
                    }
                }
            }
            allSentences.addAll(currentSentences);
            currentSentences.clear();
        }
        allSentences.addAll(currentSentences);
        currentSentences.clear();

        Set<Rule> allRules = new HashSet<>();
        System.out.println("All sentences: ");
        for (Sentence s : allSentences) {
            System.out.println("\t" + s);
            List<Rule> rules = s.toRules();
            for (Rule r : rules) {
                System.out.println("\t\t" + r);
                allRules.add(r);
            }
            System.out.println();
        }

        Rule textIsPush = new Rule();
        textIsPush.setSubject("text");
        textIsPush.setPredicate("is");
        textIsPush.setTrait("push");
        allRules.add(textIsPush);

        rules.clear();
        rules.addAll(allRules);
        for (int i = rules.size() - 1; i >= 0; i--) {
            if (rules.get(i).isNegated()) {
                rules.add(rules.remove(i));
            }
        }
        System.out.println("All rules:");
        for (Rule r : rules) {
            System.out.println("\t" + r);
        }
        applyNonTransformativeRules();
        applyTransformativeRules();
        resetRules();
        applyNonTransformativeRules();
    }

    private void applyNonTransformativeRules() {
        for (Rule r : rules) {
            if (r.isTransformativeRule()) {
                continue;
            }
            for (int row = 0; row < grid.length; row++) {
                for (int column = 0; column < grid[row].length; column++) {
                    Space space = getSpace(column, row);
                    for (Tile tile : space.tiles) {
                        if (r.appliesTo(tile)) {
                            r.apply(tile);
                        }
                    }
                }
            }
        }
    }
    
    private void applyTransformativeRules() {
        for (Rule r : rules) {
            if (!r.isTransformativeRule()) {
                continue;
            }
            for (int row = 0; row < grid.length; row++) {
                for (int column = 0; column < grid[row].length; column++) {
                    Space space = getSpace(column, row);
                    for (Tile tile : space.tiles) {
                        if (r.appliesTo(tile)) {
                            r.apply(tile);
                        }
                    }
                }
            }
        }
    }

    private void loadDefaultMap() {
        for (int row = 0; row < 24; row++) {
            for (int column = 0; column < 18; column++) {
                grid[row][column] = new Space();
            }
        }
    }

    public static Space getSpace(Point p) {
        return getSpace(p.x, p.y);
    }
    
    public void addToHistory(){
        history.push(toString());
    }

    /**
     *
     */
    public static Space getSpace(int column, int row) {
        if (row < 0 || column < 0 || row >= MAX_ROWS || column >= MAX_COLUMNS) {
            Space s = new Space();
            Tile tile = new Tile("boundary_" + column + "_" + row);
            tile.setStop(true);
            s.addTile(tile);
            return s;
        }
        return instance.grid[row][column];
    }

    /**
     * Space x,y attempts to push all pushable tiles on the space in the given
     * direction This method recursively calls push on each successive space in
     * the same direction until an empty space (space with no pushes or stops)
     * is found.
     */
    public static boolean push(int x, int y, int direction) {
        Space pushSource = getSpace(x, y);
        Point destination = Utils.offsetPointByDirection(x, y, direction);
        Space s = getSpace(destination.x, destination.y);
        if (s.isStop()) {
            return false;
        }
        if (s.hasPush()) {
            boolean canPush = push(destination.x, destination.y, direction);
            if (canPush) {
                List<Tile> tilesToMove = new LinkedList<>();
                for (Tile t : s.tiles) {
                    if (t.isPush()) {
                        tilesToMove.add(t);
                    }
                }
                for (Tile t : tilesToMove) {
                    t.move(direction);
                }
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * Space x,y attempts to pull tiles from the given direction
     */
    public static boolean pull(int x, int y, int direction) {
        Space pullSource = getSpace(x, y);
        Point destination = Utils.offsetPointByDirection(x, y, direction);
        Space s = getSpace(destination.x, destination.y);
        boolean goOneMore = true;
        while (s.hasPull()) {
            List<Tile> tilesToMove = new LinkedList<>();
            for (Tile t : s.tiles) {
                if (t.isPull()) {
                    tilesToMove.add(t);
                }
            }
            for (Tile t : tilesToMove) {
                t.move(Utils.swapDirection(direction));
            }
            destination = Utils.offsetPointByDirection(destination, direction);
            s = getSpace(destination.x, destination.y);
            if (!goOneMore) {
                return true;
            }
            if (s.isStop()) {
                goOneMore = false;
            }
        }

        return true;
    }

    public void doQueuedMoves() {
        for (Space[] row : grid) {
            for (Space cell : row) {
                for (int i = cell.tiles.size() - 1; i >= 0; i--) {
                    Tile t = cell.tiles.get(i);
                    t.doMove();
                }
            }
        }
    }

    public void addTile(int row, int column, String name) {

    }

    public void update(float tpf) {
        for (Spatial s : sprites.getChildren()) {
            if (s instanceof Sprite) {
                ((Sprite) s).update(tpf);
            }
        }
    }

    public Node getSprites() {
        return sprites;
    }

    public List<Tile> getTilesWhere(TileCriteria criteria) {
        List<Tile> result = new LinkedList<>();
        for (Space[] row : grid) {
            for (Space column : row) {
                for (Tile t : column.tiles) {
                    if (criteria.matches(t)) {
                        result.add(t);
                    }
                }
            }
        }
        return result;
    }

    public static void printLevel() {
        System.out.println(instance.toString());
    }
    
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for (int i = instance.grid.length - 1; i >= 0; i--) {
            Space[] row = instance.grid[i];
            for (Space cell : row) {
                for (Tile t : cell.tiles) {
                    sb.append(t.getName()).append("_").append(t.getFacing()).append(" ");
                }
                sb.append(",");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
