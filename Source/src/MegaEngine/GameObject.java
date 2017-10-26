package MegaEngine;

public class GameObject extends Entity
{
    
    public GameObject()
    {
        //Create an empty object
        IsVisible = false;
        IsAnimating = false;
        
        LocationX = 0;
        LocationY = 0;
        
        IsAlive = false;
    }
    
    public GameObject(int objecttype, Global.Directions horzfacing, Global.Directions vertfacing, float locationx, float locationy)
    {
        
    }    
    
}
