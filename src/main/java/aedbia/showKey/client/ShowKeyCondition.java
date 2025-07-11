package aedbia.showKey.client;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ShowKeyCondition {

    public boolean hide = false;
    public boolean customPosition = false;
    public Point coordinate;
    public boolean conditionDisplay = false;
    public List<SubCondition> subConditions = new ArrayList<>();
    public boolean hideName = false;
    public boolean drawRight = false;
    public double size = 1.0f;

    public boolean isActive() {
        if (hide) {
            return false;
        }
        if(!conditionDisplay){
            return true;
        }
        for(var a :subConditions){
            if(!a.match()){
                return false;
            }
        }
        return true;
    }

    public static class SubCondition {
        private final Predicate<SubCondition> rule;
        public List<String> list = new ArrayList<>();
        public boolean matchTags = false;
        public boolean blackList = false;

        public SubCondition(Predicate<SubCondition> rule) {
            this.rule = rule;
        }

        public boolean match() {
            if(blackList){
                return !rule.test(this);
            }
            return rule.test(this);
        }
    }

//    private boolean empty(List<String> list) {
//        List<String> list1 = list.stream().filter(a -> !a.contains("example")).toList();
//        return list1.isEmpty();
//    }
}