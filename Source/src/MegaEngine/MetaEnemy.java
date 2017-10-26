package MegaEngine;

public class MetaEnemy 
{
    private int EnemyType;
    private int AIType;
    private Global.Directions DirectionHorizontal;
    private Global.Directions DirectionVertical;
    private float LocationX;
    private float LocationY;
    private boolean Respawnable;
    private boolean Respawned;
    private boolean isAlive;
    private int EnemyArrayID;
    private int TileWidth = main.Global.TileWidth;
    private int TileHeight = main.Global.TileHeight;
    private float InertiaX = -1;
    private float InertiaY = -1;
    private String Text;
    
    public MetaEnemy(int enemytype, int aitype, Global.Directions horz, Global.Directions vert, float locx, float locy, boolean respawnable, boolean respawned, boolean isalive, float inertiax, float inertiay, String text)
    {
      //  System.out.println("Meta enemy " + EnemyType + " was created with InteriaX " + inertiax);
        EnemyType = enemytype;
        AIType = aitype;
        DirectionHorizontal = horz;
        DirectionVertical = vert;
        LocationX = locx;
        LocationY = locy;
        Respawnable = respawnable;
        Respawned = respawned;
        isAlive = isalive;
        InertiaX = inertiax;
        InertiaY = inertiay;
        Text = text;
        
        int IDx = (int)(LocationX / TileWidth);
        int IDy = (int)(LocationY / TileHeight);
        EnemyArrayID = ((IDx * 1000) + IDy);
    }
    
    public float getLocationX()
    {
        return LocationX;
    }
    
    public float getLocationY()
    {
        return LocationY;
    }
    
    public void setEnemyArrayID(int value)
    {
        EnemyArrayID = value;
    }
    
    public int getEnemyArrayID()
    {
        return EnemyArrayID;
    }
    
    public boolean isRespawnable()
    {
        return Respawnable;
    }
    
    public int getEnemyType()
    {
        return EnemyType;
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
    
    public int getEnemyAI()
    {
        return AIType;
    }
    
    public float getInertiaX()
    {
        
        return InertiaX;
    }
    
    public float getInertiaY()
    {
        if (InertiaX == -1)
        {
            System.out.println("Inertia was never set");
            return -1;
        } else
        {
            return InertiaY;
        }
        
    }
    
    public String getText()
    {
        return Text;
    }
}
