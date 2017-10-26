package MegaEngine;

import MegaEngine.Entity;

public class Enemy extends Entity
{    
    public Enemy()
    {
        IsVisible = false;
        IsAnimating = false;
        
        LocationX = 0;
        LocationY = 0;
        
        IsAlive = false;
    }
    
}
