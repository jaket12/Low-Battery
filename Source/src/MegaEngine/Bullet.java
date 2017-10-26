package MegaEngine;

import MegaEngine.Entity;

/**
 * A category of Entity that holds special cases shared between the group during
 * interactions.
 * @author McRib
 */
public class Bullet extends Entity
{
    /*
     * Constructor.  No parameters creates a blank bullet able to be used, but
     * really should be defined.
     */
    public Bullet()
    {
        AIType = 0;
        
        LocationX = 0;
        LocationY = 0;
        
        IsVisible = false;
        IsAlive = false;
    }
    
}
