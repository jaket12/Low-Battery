package MegaEngine;

public class Item extends Entity
{
    private int ItemType;//Number that represents this item
    private int ItemArrayID;//The meta tile data that says where this item spawns. 0 means it's a temp item
    
    public Item()
    {
        IsVisible = false;
        IsAnimating = false;
        
        LocationX = 0;
        LocationY = 0;
        
        ItemType = 0;
        IsAlive = false;
    }
    
}
