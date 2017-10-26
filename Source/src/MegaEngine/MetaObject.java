package MegaEngine;

import MegaEngine.Global.Directions;

public class MetaObject {
    
    private int ObjectType;
    private int AIType;
    private Global.Directions DirectionHorizontal;
    private Global.Directions DirectionVertical;
    private float LocationX;
    private float LocationY;
    private boolean Respawnable;
    private boolean Respawned;
    private boolean isAlive;
    private int ObjectArrayID;
    private int TileWidth = main.Global.TileWidth;
    private int TileHeight = main.Global.TileHeight;
    private float InertiaX;
    private float InertiaY;
    private String Text;
    
    public MetaObject(int type, int aitype, Directions horz, Directions vert, float Locx, float Locy, boolean respawnable, boolean respawned, float inertiax, float inertiay, String text)
    {
        ObjectType = type;
        AIType = aitype;
        DirectionHorizontal = horz;
        DirectionVertical = vert;
        LocationX = Locx;
        LocationY = Locy;
        Respawnable = respawnable;
        Respawned = respawned;
        
        int IDx = (int)(LocationX / TileWidth);
        int IDy = (int)(LocationY / TileHeight);
        ObjectArrayID = ((IDx * 1000) + IDy);
        
        InertiaX = inertiax;
        InertiaY = inertiay;
        Text = text;
    }
    
    public float getInertiaX()
    {
        return InertiaX;
    }
    
    public float getInertiaY()
    {
        return InertiaY;
    }
    
    public String getText()
    {
        return Text;
    }
    
    public int getAIType()
    {
        return AIType;
    }
    
    public float getLocationX()
    {
        return LocationX;
    }
    
    public float getLocationY()
    {
        return LocationY;
    }
    
    public void setObjectArrayID(int value)
    {
        ObjectArrayID = value;
    }
    
    public int getObjectArrayID()
    {
        return ObjectArrayID;
    }
    
    public boolean isRespawnable()
    {
        return Respawnable;
    }
    
    public int getObjectType()
    {
        return ObjectType;
    }
    
    public int getObjectAI()
    {
        return AIType;
    }
    
    public Global.Directions getHorizontal()
    {
        return DirectionHorizontal;
    }
    
    public Global.Directions getVertical()
    {
        return DirectionVertical;
    }
    
    public boolean isRespawned()
    {
        return Respawned;
    }
    
    public boolean isAlive()
    {
        return isAlive;
    }
    
}