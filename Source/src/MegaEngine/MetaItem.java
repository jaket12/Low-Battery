package MegaEngine;

public class MetaItem {
    private int ItemType;
    private float LocationX;
    private float LocationY;
    private boolean Respawnable;
    private boolean Respawned;
    private boolean DestroyTimer;
    private int ItemArrayID;
    private int TileWidth = main.Global.TileWidth;
    private int TileHeight = main.Global.TileHeight;
    
    public MetaItem (int itemtype, float locx, float locy, boolean respawnable, boolean destroytimer)
    {
        ItemType = itemtype;
        LocationX = locx;
        LocationY = locy;
        Respawnable = respawnable;
        DestroyTimer = destroytimer;
        int IDx = (int)(LocationX / TileWidth);
        int IDy = (int)(LocationY / TileHeight);
        ItemArrayID = ((IDx * 1000) + IDy);
    }
    
    public float getLocationX()
    {
        return LocationX;
    }
    
    public float getLocationY()
    {
        return LocationY;
    }
    
    public void setItemArrayID(int value)
    {
        ItemArrayID = value;
    }
    
    public int getItemArrayID()
    {
        return ItemArrayID;
    }
    
    public boolean isRespawnable()
    {
        return Respawnable;
    }
    
    public int getItemType()
    {
        return ItemType;
    }
    
    public boolean isRespawned()
    {
        return Respawned;
    }

    public boolean DestroyTimer()
    {
        return DestroyTimer;
    }
    
    public void setRespawned(boolean value)
    {
        Respawned = value;
        if (!Respawnable && Respawned)
        { //Item only shows up once 
            Respawnable = false;
        }
    }
}
