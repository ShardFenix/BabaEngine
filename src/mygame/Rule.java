package mygame;

import java.util.ArrayList;
import java.util.List;

public class Rule {

    private boolean subjectInverted = false;

    private List<String> subject = null;

    private String predicate = null;

    private boolean affirm = true;

    private String trait = null;

    //valid means the current chain of words on this rule is corect grammar
    private boolean valid = true;

    //completed rules can be applied, and should not be editable
    private boolean completed = false;

    private String lastWord = "";

    private boolean transformativeRule = false;

    public Rule() {
    }

    public Rule(Rule other) {
        subjectInverted = other.subjectInverted;
        subject = new ArrayList<>();
        subject.addAll(other.subject);
        predicate = other.predicate;
        affirm = other.affirm;
        trait = other.trait;
        valid = other.valid;

    }

    /**
     * Adds a new word (tile) to the current rule.
     * If this results in a new rule being started,
     * return the new rule.
     */
    public Rule addWord(Tile tile) {
        valid = false;
        if (subject == null) {
            if (tile.isSubjectWord()) {
                subject = new ArrayList<>(1);
                subject.add(tile.getName());
                lastWord = tile.getName();
                valid = true;
            } else if (tile.getName().equals("not")) {
                subjectInverted = true;
                lastWord = tile.getName();
                valid = true;
            } else if (lastWord.equals("and") && tile.isSubjectWord()){
                
            }
        } else if (predicate == null) {
            if (tile.getName().equals("and")) {
                lastWord = tile.getName();
                valid = true;
            } else if (tile.isPredicateWord()) {
                if (lastWord.equals("and")) {
                    valid = false;
                    return null;
                }
                predicate = tile.getName();
                lastWord = tile.getName();
                valid = true;
            } else if (lastWord.equals("and")){
                if (tile.isSubjectWord()){
                    subject.add(tile.getName());
                    lastWord=tile.getName();
                } else {
                    valid=false;
                }
            } else if (tile.isSubjectWord()){
                subject.clear();
                subject.add(tile.getName());
                lastWord=tile.getName();
                valid=true;
            }
        } else {
            //last part of the sentence - need a subject or a trait or "not"
            if (tile.getName().equals("not")) {
                if (predicate.equals("is")){
                    affirm = false;
                    lastWord = tile.getName();
                    valid = true;
                } else {
                    valid=false;
                    completed=false;
                    return null;
                }
            } else if (tile.isSubjectWord()) {
                if (Tile.predicateWords.contains(lastWord)){
                    transformativeRule = true;
                    trait = tile.getName();
                    lastWord = tile.getName();
                    valid = true;
                    completed = true;
                } else {
                    Rule rule = new Rule();
                    rule.setSubject(tile.getName());
                    rule.setPredicate(null);
                    rule.valid=true;
                    return rule;
                }
            } else if (tile.isTraitWord()) {
                if (Tile.traitWords.contains(lastWord)){
                    valid=false;
                    return null;
                }
                trait = tile.getName();
                lastWord = tile.getName();
                valid = true;
                completed = true;
            } else if (tile.getName().equals("and")) {
                Rule rule = new Rule(this);
                rule.trait = null;
                rule.valid = true;
                return rule;
            } else if (tile.isPredicateWord()){
                if (Tile.predicateWords.contains(lastWord)){
                    valid=false;
                    return null;
                }
                //new rule chaining off the last subject of this rule
                lastWord = tile.getName();
                valid=true;
                Rule rule = new Rule();
                rule.setSubject(trait);
                rule.setPredicate(tile.getName());
                rule.valid=true;
                return rule;
            } else {
                valid=false;
                
            }
        }
        return null;
    }
    
    public boolean appliesTo(Tile tile){
        if (tile==null || subject==null || subject.get(0)==null){
            return false;
        }
        if (tile.isWord() && subject.get(0).equals("text")){
            return true;
        }
        if (("o"+subject.get(0)).equals(tile.getName())){
            return true;
        }
        return false;
    }
    
    public void apply(Tile tile){
        switch (trait){
            case "defeat":tile.setDefeat(affirm);break;
            case "float":tile.setFloat(affirm);break;
            case "open":tile.setOpen(affirm);break;
            case "push":tile.setPush(affirm);break;
            case "pull":tile.setPull(affirm);break;
            case "shift":tile.setShift(affirm);break;
            case "shut":tile.setShut(affirm);break;
            case "stop":tile.setStop(affirm);break;
            case "weak":tile.setWeak(affirm);break;
            case "win":tile.setWin(affirm);break;
            case "move":tile.setMove(affirm);break;
            case "you":tile.setYou(affirm);break;
            
            //transformations
            case "wall":tile.transform("o"+trait);break;
            case "rock":tile.transform("o"+trait);break;
            case "baba":tile.transform("o"+trait);break;
            case "box":tile.transform("o"+trait);break;
            case "door":tile.transform("o"+trait);break;
            case "skull":tile.transform("o"+trait);break;
            case "flag":tile.transform("o"+trait);break;
        }
    }

    public List<String> getSubjects() {
        return subject;
    }

    public boolean isNegated() {
        return !affirm;
    }

    public void setNegated(boolean negated) {
        this.affirm = !negated;
    }

    public String getTrait() {
        return trait;
    }

    public void setTrait(String trait) {
        this.trait = trait;
    }

    public boolean isSubjectInverted() {
        return subjectInverted;
    }

    public void setSubjectInverted(boolean subjectInverted) {
        this.subjectInverted = subjectInverted;
    }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    public boolean isAffirm() {
        return affirm;
    }

    public void setAffirm(boolean affirm) {
        this.affirm = affirm;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isTransformativeRule() {
        return transformativeRule;
    }

    public void setTransformativeRule(boolean transformativeRule) {
        this.transformativeRule = transformativeRule;
    }

    public String toString() {
        String prefix="";
        if (subjectInverted){
            prefix="not ";
        }
        if (completed || true){
            if ("is".equals(predicate)){
                return prefix+Utils.colectionToString(subject)+ (affirm ? " is " : " is not ") + trait;
            } else {
                return prefix+Utils.colectionToString(subject) + " "+ predicate+" " + trait;
            }
        }
        return "";
    }

    public void setSubject(String subject) {
        this.subject=new ArrayList<>();
        this.subject.add(subject);
    }
    
    public int hashCode(){
        return toString().hashCode();
    }
    
    public boolean equals(Object other){
        if (other instanceof Rule){
            return ((Rule)other).toString().equals(toString());
        }
        return false;
    }
}
