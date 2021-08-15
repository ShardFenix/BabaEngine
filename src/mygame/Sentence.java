package mygame;

import java.util.ArrayList;
import java.util.List;

public class Sentence {
    private List<Tile> tiles = new ArrayList<>();
    
    public Sentence(){}
    
    public Sentence(Sentence other){
        for (Tile s : other.tiles){
            tiles.add(s);
        }
    }
    
    public List<Sentence> addToChain(Space s){
        boolean first=true;
        List<Sentence> newSentences = new ArrayList<>();
        for (Tile wordTile : s.getWords()){
            if (first){
                tiles.add(wordTile);
                first=false;
            } else {
                Sentence sentence = new Sentence(this);
                sentence.tiles.remove(sentence.tiles.size()-1);
                sentence.tiles.add(wordTile);
                newSentences.add(sentence);
            }
        }
        return newSentences;
    }
    
    public void addToChain(Tile t){
        tiles.add(t);
    }
    
    public List<Rule> toRules(){
        List<Rule> rules = new ArrayList<>();
        if (tiles.size()<3){
            return rules;
        }
        Rule currentRule = new Rule();
        for (int i=0;i<tiles.size();i++){
            Tile tile = tiles.get(i);
            Rule clone = currentRule.addWord(tile);
            //if current rule has been invalidated, start a new blank rule and keep going
            if (!currentRule.isValid()){
                currentRule = new Rule();
            }
            if (currentRule.isCompleted()){
                System.out.println("Completed rule: "+currentRule.toString());
                rules.add(currentRule);
            }
            if (clone!=null){
                currentRule=clone;
                System.out.println("Chaining new rule: "+currentRule.toString());
            }
        }
        //split rules that have multiple subjects
        for (int i=0;i<rules.size();i++){
            Rule rule = rules.get(i);
            if (rule.getSubjects().size()>1){
                rules.remove(i);
                i--;
                for (int j=0;j<rule.getSubjects().size();j++){
                    Rule r = new Rule(rule);
                    r.setSubject(rule.getSubjects().get(j));
                    rules.add(0,r);
                    i++;
                }
            }
        }
        return rules;
    }
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for (Tile t : tiles){
            sb.append(t.getName()).append(" ");
        }
        return sb.toString();
    }
    
    
}
