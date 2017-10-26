package MegaEngine;

import java.util.ArrayList;

public class SwitchVariableManager {
    
    private ArrayList<SwitchVariable> Variables;
    
    public SwitchVariableManager() {
        Variables = new ArrayList<SwitchVariable>();
    }
    
    public void SetVariable(String key, boolean value) {
        //Search for a variable in the list with the given name.
        boolean indexfound = false;
        for (int i = 0; i < Variables.size(); i++) {
            if (Variables.get(i).Key.equalsIgnoreCase(key)) {
                SwitchVariable newchanges = new SwitchVariable(key, value);
                Variables.set(i, newchanges);
                indexfound = true;
                break;
            }
        }
        //If we didn't find a variable in the list with this name, create it.
        if (!indexfound) {
            SwitchVariable newchanges = new SwitchVariable(key, value);
            Variables.add(newchanges);
        }
    }
    
    public boolean GetVariable(String key) {
        //Search for a variable in the list with the given name.
        for (int i = 0; i < Variables.size(); i++) {
            if (Variables.get(i).Key.equalsIgnoreCase(key)) {
                return Variables.get(i).Value;
            }
        }
        //If we didn't find a variable in the list with this name, return false
        return false;
    }
}
