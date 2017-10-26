package MegaEngine;

import MegaEngine.Global.CutSceneType;
import java.util.logging.Logger;
import org.newdawn.slick.tiled.TiledMap;
import org.newdawn.slick.*;
import java.io.*;
import MegaEngine.Global.Directions;

/**
 *
 * @author Multiplexing
 */
public class LevelManager {
    
    //Map Variables
    private TiledMap Level;
    private int TileData[][][];//X, Y, Attributes
    private AnimatedTile TileAnimation[][];
    private int LevelWidth = 0; //Total WindowWidth in tiles of the map
    private int LevelHeight = 0;
    int TileOffsetX = 0; //The pixels of the screen that are already passed by
    int TileOffsetY = 0;
    int TileX = 0;//Top of the map that is drawn to screen
    int TileY = 0;
    int OldTileX;//last frame's X
    int OldTileY;
    int ScreenHeightTiles;
    int ScreenWidthTiles;
    /*
     * 0: Air Tile - No effects
     * 1:Blocked tile (no movement allowed)
     * 
     * Animation~100~61,1,7`99,1,7`100,1,7
     *           Speed~Tile,attribute0,attribute1`Next Tile
     * AnimationTouch~200~81,1,15`181,1,15`182,1,15`183,1,15`184,1,15`185,1,15`186,1,15`187,1,15`188,1,15
     */
    int TileLayer = 0;//The layer in the Tiled map that represents the blocks in the level (things which are walked on)
    int RoomLayer = 1;//The layer in the Tiled map that represents the data for room and transitions (room edges and start locations)
    int StartX;//This is the pixel location where mega man will start the game
    int StartY;
    int LevelMusic = 0;//Music to play when resuming from a checkpoint
    
    MetaEnemy[][] LevelEnemies;//Each array slot represents an enemy within the map to be loaded by default.
    //LevelEnemies is a STATIC array that does not get modified.  WHen the level needs to check for new enemies
    //it will look here and add them to the playable enemy array.  The playable array holds what is actually used and may be removed
    Enemy enemy;//temp guy to spawn with
    private boolean EnableEnemySpawn = true;
    private boolean EnableObjectSpawn = true;
    
    private int RoomData[][];//Similar to TileData, if the player touches one of these tiles the room will switch
    private int CurrentRoomCeiling;//This is a Y co-ordinate that says to stop the screen here.  It is measured in Tiles, not pixels
    private int CurrentRoomBottom;//Y co-ordinate
    private int CurrentRoomLeft;//X co-ordinate
    private int CurrentRoomRight;//X co-ordinate
    
    private String RoomDataValue[][];
    private CutSceneType CutScene = CutSceneType.None;
        
    private MetaItem[][] LevelItems;
    private MetaObject[][] LevelObjects;
    
    private String LevelFilePath;
    
    public LevelManager(String levelfilepath) throws SlickException
    {
        LevelFilePath = levelfilepath;
        try {
            Level = new TiledMap(LevelFilePath);
        } catch (SlickException e) {
            System.err.println("Tile map was not found for the level! " + levelfilepath + "Error: " + e.getLocalizedMessage());
            //Set a default level so we can just get on with our lives
            main.Global.CurrentLevel = 1;
            LevelFilePath = "Resource/Level/Level" + main.Global.CurrentLevel + ".tmx";
            Level = new TiledMap(LevelFilePath);
        }
        
        LevelWidth = Level.getWidth();
        LevelHeight = Level.getHeight();
        ScreenWidthTiles = (main.WindowWidth / 32);
        ScreenHeightTiles = (main.WindowHeight / 32);
        
        try {
            LoadEntityData();
            LoadTileData();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LevelManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

    }

    /**
     * Access the Level Entity file (.eny) and load up the Entities that should
     * be used in the Level.
     * Each line on the .eny file is a different Entity and each parameter is 
     * split by a character squiggly ~. These Entities are loaded as 'MetaEntities'
     * because they are not actually created until runtime requires them.
     * You can access the MetaEntity array at any time to respawn the original
     * type of Entity that was found at the game start.
     * @throws FileNotFoundException 
     */
    private void LoadEntityData() throws FileNotFoundException
    {
        LevelEnemies = new MetaEnemy[LevelWidth][LevelHeight];
        LevelItems = new MetaItem[LevelWidth][LevelHeight];
        LevelObjects = new MetaObject[LevelWidth][LevelHeight];
        
        try {
            InputStream in = getClass().getResourceAsStream("/Resource/Level/Level" + main.Global.CurrentLevel + ".eny");
            Reader fr = new InputStreamReader(in);
            BufferedReader reader = new BufferedReader(fr);
            
            String splits[];
            String line;
            String EntityType;
            String AIType;
            String HorzFacing;
            String VertFacing;
            String LocX;
            String LocY;
            String Respawnable;
            String Respawned = "False";
            String ObjectArrayId;
            String Alive = "True";
            Directions Horizontal;
            Directions Vertical;
            String InertiaX;
            String InertiaY;
            String Text;
            
            while ((line = reader.readLine()) != null)
            {//Read the line and parse its info
                splits = line.split("~");
                if (splits[0].equals("Enemy"))
                {
                    //Line is an enemy to load
                    EntityType = splits[1];
                    AIType = splits[2];
                    HorzFacing = splits[3];
                    VertFacing = splits[4];
                    LocX = splits[5];
                    LocY = splits[6];
                    Respawnable = splits[7];
                    InertiaX = splits[8];
                    InertiaY = splits[9];
                    Text = splits[10];
                    
                    if (HorzFacing.equalsIgnoreCase("Left"))
                    {
                        Horizontal = Directions.Left;
                    } else if (HorzFacing.equalsIgnoreCase("Right"))
                    {
                        Horizontal = Directions.Right;
                    }else if (HorzFacing.equalsIgnoreCase("Up"))
                    {
                        Horizontal = Directions.Up;
                    }else if (HorzFacing.equalsIgnoreCase("Down"))
                    {
                        Horizontal = Directions.Down;
                    }else
                    {
                        Horizontal = Directions.None;
                    }

                    if (VertFacing.equalsIgnoreCase("Left"))
                    {
                        Vertical = Directions.Left;
                    } else if (VertFacing.equalsIgnoreCase("Right"))
                    {
                        Vertical = Directions.Right;
                    }else if (VertFacing.equalsIgnoreCase("Up"))
                    {
                        Vertical = Directions.Up;
                    }else if (VertFacing.equalsIgnoreCase("Down"))
                    {
                        Vertical = Directions.Down;
                    }else
                    {
                        Vertical = Directions.None;
                    }

                    MetaEnemy EnemyData = new MetaEnemy(Integer.parseInt(EntityType), Integer.parseInt(AIType), Horizontal, Vertical, Float.parseFloat(LocX) + main.Global.TileWidth, Float.parseFloat(LocY) + main.Global.TileHeight, Boolean.valueOf(Respawnable), Boolean.valueOf(Respawned), Boolean.valueOf(Alive), Float.parseFloat(InertiaX), Float.parseFloat(InertiaY), Text);
                    
                    LevelEnemies[(int)(EnemyData.getLocationX() / main.Global.TileWidth)][(int)(EnemyData.getLocationY() / main.Global.TileHeight)] = EnemyData;
                } else if (splits[0].equals("Item"))
                {
                    //Loading an item
                    
                    EntityType = splits[1];
                    LocX = splits[2];
                    LocY = splits[3];
                    Respawnable = splits[4];
                    String DestroyTimer = splits[5];
                    MetaItem ItemData = new MetaItem(Integer.parseInt(EntityType), Float.parseFloat(LocX) + main.Global.TileWidth, Float.parseFloat(LocY) + main.Global.TileHeight, Boolean.valueOf(Respawnable), Boolean.valueOf(DestroyTimer));
                    LevelItems[(int)(ItemData.getLocationX() / main.Global.TileWidth)][(int)(ItemData.getLocationY() / main.Global.TileHeight)] = ItemData;
                    
                } else if (splits[0].equals("Object"))
                {
                    EntityType = splits[1];
                    AIType = splits[2];
                    HorzFacing = splits[3];
                    VertFacing = splits[4];
                    LocX = splits[5];
                    LocY = splits[6];
                    Respawnable = splits[7];
                    Respawned = splits[8];
                    ObjectArrayId = splits[9];
                    InertiaX = splits[10];
                    InertiaY = splits[11];
                    Text = splits[12];

                    if (HorzFacing.equalsIgnoreCase("Left"))
                    {
                        Horizontal = Directions.Left;
                    } else if (HorzFacing.equalsIgnoreCase("Right"))
                    {
                        Horizontal = Directions.Right;
                    }else if (HorzFacing.equalsIgnoreCase("Up"))
                    {
                        Horizontal = Directions.Up;
                    }else if (HorzFacing.equalsIgnoreCase("Down"))
                    {
                        Horizontal = Directions.Down;
                    }else
                    {
                        Horizontal = Directions.None;
                    }

                    if (VertFacing.equalsIgnoreCase("Left"))
                    {
                        Vertical = Directions.Left;
                    } else if (VertFacing.equalsIgnoreCase("Right"))
                    {
                        Vertical = Directions.Right;
                    }else if (VertFacing.equalsIgnoreCase("Up"))
                    {
                        Vertical = Directions.Up;
                    }else if (VertFacing.equalsIgnoreCase("Down"))
                    {
                        Vertical = Directions.Down;
                    }else
                    {
                        Vertical = Directions.None;
                    }

                    MetaObject ObjectData = new MetaObject(Integer.parseInt(EntityType), Integer.parseInt(AIType), Horizontal, Vertical, Float.parseFloat(LocX) + main.Global.TileWidth, Float.parseFloat(LocY) + main.Global.TileHeight, Boolean.valueOf(Respawnable), Boolean.valueOf(Respawned), Float.parseFloat(InertiaX), Float.parseFloat(InertiaY), Text);
                  LevelObjects[(int)(ObjectData.getLocationX() / main.Global.TileWidth)][(int)(ObjectData.getLocationY() / main.Global.TileHeight)] = ObjectData;

                }
                
            }
        } catch (IOException ex) {
            Logger.getLogger(LevelManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (NumberFormatException ex) {
            Logger.getLogger(LevelManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
    }
    
    public void ReloadLevelTiles() throws SlickException
    {
        Level = new TiledMap(LevelFilePath);
        LevelWidth = Level.getWidth();
        LevelHeight = Level.getHeight();
    }
    
    /**
     * Loads the Layers for the Level.
     * Layer 0 is used for the visible tilemap you see on the screen.
     * These tiles have special attributes that determine if it is solid, water,
     * or some special thing like a ladder.
     * Layer 1 is used for the Meta Tile data: things like what is the end of
     * a room, or if it's a check point or something.
     */
    public void LoadTileData()
    {
        //These are the values for the Layer 1 visible blocks. Spikes, floors, water.
        //Stuff that every object can interact with
        TileData = new int[LevelWidth][LevelHeight][main.Global.TileAttributesMax];
        TileAnimation = new AnimatedTile[LevelWidth][LevelHeight];
        
        for (int xAxis = 0; xAxis < LevelWidth; xAxis++)
        {
            for (int yAxis = 0; yAxis < LevelHeight; yAxis++)
            {
                int tileID = Level.getTileId(xAxis, yAxis, TileLayer); //Get tile data for x,y on layer 0 "walls"
                
                for (int i = 0; i < main.Global.TileAttributesMax; i++)
                {
                    String value = Level.getTileProperty(tileID, "Attribute" + i, "0"); //Get the wall type for this tile
                    //if no attribute is found, or the max is not met, value just returns 0, signifying a blank and open tile
                    if (value.startsWith("Animation~"))
                    {//This tile is animated.
                        String splits[] = value.split("~");
                        TileAnimation[xAxis][yAxis] = new AnimatedTile(Integer.parseInt(splits[1]), splits[2]);//1=speed, 2=tileid,attribute0,attribute1`tileid2...
                        value = "0";//Give a nothing value for this attribute, so it hacks in easy to the other tile types.
                    } else if (value.startsWith("AnimationTouch~"))
                    {
                        String splits[] = value.split("~");
                        TileAnimation[xAxis][yAxis] = new AnimatedTile(Integer.parseInt(splits[1]), splits[2]);
                        TileAnimation[xAxis][yAxis].isAnimating(false);
                        TileAnimation[xAxis][yAxis].AnimateOnTouch(true);
                        value = "16";//Animate on touch
                    }
                    TileData[xAxis][yAxis][i] = Integer.valueOf(value);
                    
                }
            }
        }
        
        //This is the stuff that denotes room boundaries and effects. Left, right, room transistons, checkpoints.
        //This stuff is on layer 2.
        RoomData = new int[LevelWidth][LevelHeight];
        RoomDataValue = new String[LevelWidth][LevelHeight];
        
        for (int xAxis = 0; xAxis < LevelWidth; xAxis++)
        {
            for (int yAxis = 0; yAxis < LevelHeight; yAxis++)
            {
                int RoomID = Level.getTileId(xAxis, yAxis, RoomLayer);//Get the value of the tile in the room
                String value = Level.getTileProperty(RoomID, "Attribute0", "none");//Room tiles only have one value so far.  0 defaults to nothing
                int intie = 0;////I shall call him intie!
                if (value.equals("RoomTraverseTop"))
                {
                    intie = 1;
                }
                else if (value.equals("RoomTraverseBottom"))
                {
                    intie = 2;
                }
                else if (value.equals("RoomTraverseLeft"))
                {
                    intie = 3;
                }
                else if (value.equals("RoomTraverseRight"))
                {
                    intie = 4;
                }
                else if (value.equals("StartTile"))
                {
                    StartX = xAxis * main.Global.TileWidth;
                    StartY = yAxis * main.Global.TileHeight;
                }
                else if (value.startsWith("CutScene"))
                {
                    intie = 5;//Indicates spawn boss
                }
                else if (value.startsWith("CheckPoint"))
                {
                    intie = 6;//Check point tile.  Changes the startx then deletes
                }
                else if (value.contains("RoomEdgeLeft"))
                {//MetaTile stops the screen boundary, but mega man can go past it.
                    intie = 7;
                }
                else if (value.contains("RoomEdgeRight"))
                {//MetaTile stops the screen boundary, but mega man can go past it.
                    intie = 8;
                }
                else if (value.contains("RoomEdgeTop"))
                {//MetaTile stops the screen boundary, but mega man can go past it.
                    intie = 9;
                }
                else if (value.contains("RoomEdgeBottom"))
                {//MetaTile stops the screen boundary, but mega man can go past it.
                    intie = 10;
                }
                else if (value.contains("RoomPitLeft"))
                {//MetaTile stops the screen boundary, but mega man dies if its touched.
                    intie = 11;
                }
                else if (value.contains("RoomPitRight"))
                {
                    intie = 12;
                }
                else if (value.contains("RoomPitTop"))
                {
                    intie = 13;
                }
                else if (value.contains("RoomPitBottom"))
                {
                    intie = 14;
                }
                else if (value.contains("RoomTraverseLadderUp"))
                {
                    intie = 15;
                }
                else if (value.contains("RoomTraverseLadderDown"))
                {
                    intie = 16;
                }
                
                RoomData[xAxis][yAxis] = intie;//Go intie!  Do your stuff!
                RoomDataValue[xAxis][yAxis] = value;
            }
        }
        
        
    }
    
    public String getRoomTileValue(int RoomX, int RoomY)
    {
        return RoomDataValue[RoomX][RoomY];
    }
    
    public void Draw(Graphics g, int layer)
    {
        Level.render(TileOffsetX, TileOffsetY, TileX, TileY, 22, 17, layer, false);
        
    }

    public void Update(int delta, float ScreenX, float ScreenY)
    {
        //Get the top left location of the tile that should be shown on the screen
        //This is where the screen is drawn
        OldTileX = TileX;
        OldTileY = TileY;
        TileX = (int)(ScreenX / main.Global.TileWidth);
        TileY = (int)(ScreenY / main.Global.TileHeight);
        TileOffsetX = (int)(-(ScreenX % main.Global.TileWidth));
        TileOffsetY = (int)(-(ScreenY % main.Global.TileHeight));
        
        //Check the boundaries of the playable screen area in Level.
        //If any tile has an enemy located there to be spawned,
        //check if it is already alive. (if yes then do nothing)
        //if not, check if it has already spawned.
        //if not, spawn it.
        //if so, check if it is respawnable.
        //if not, do nothing.
        //if yes, then spawn it again
        
        for (int i = CurrentRoomLeft; i <= CurrentRoomRight; i++)
        {
            for (int j = CurrentRoomCeiling; j <= CurrentRoomBottom; j++)
            {
                //Can make more efficient by getting a list of all tiles to animate, then running off that, instead of checking all tiles in a room.
                //Current: larger the room, more wasted tiles to check. Serious framerate killer.
                if (TileAnimation[i][j] != null)
                {
                    if (TileAnimation[i][j].Update(delta))
                    {//New tile to display in sequence
                        if (TileAnimation[i][j].getAnimateOnTouch() && TileAnimation[i][j].isEndOfSequence())
                        {
                            ReplaceAnimateOnTouchTile(i, j, TileAnimation[i][j].getFirstTileValues(), 0, TileAnimation[i][j].getFirstTileID(), TileAnimation[i][j].getSequence(), TileAnimation[i][j].getSpeed());
                        } else
                        {
                            ReplaceTile(i, j, TileAnimation[i][j].getCurrentTileValues(), 0, TileAnimation[i][j].getCurrentTileID());
                        }
                        
                    }
                }
                
                
            }
        }
           
        //Spawns can be disabled for cutscenes and whatnot
        if (EnableEnemySpawn)
        {
            //Check if a new tile was loaded due to movement
            //If so, load the enemies for this new movement
            //Does not check for tileY yet, but it should
            if (OldTileX != TileX)
            {
                //Player is moving left
                if (OldTileX > TileX)
                {
                    for (int i = 0; i < ScreenHeightTiles; i++)
                    {
                        if (LevelEnemies[TileX][TileY + i] != null)
                        {//Enemy exists in this tile
                            if (LevelState.isEnemyAlive(LevelEnemies[TileX][TileY + i].getEnemyArrayID()))
                            {

                                //Enemy is already active and shold not spawn again
                            }
                            else
                            {//Enemy is not currently active
                                if (LevelEnemies[TileX][TileY + i].isRespawnable())
                                {//Enemy can respawn.  Since it is not currently alive, respawn it
                                    int EnemyType = LevelEnemies[TileX][TileY + i].getEnemyType();
                                    int EnemyAI = LevelEnemies[TileX][TileY + i].getEnemyAI();
                                    Global.Directions HorzFacing = LevelEnemies[TileX][TileY + i].getHorizontal();
                                    Global.Directions VertFacing = LevelEnemies[TileX][TileY + i].getVertical();
                                    float LocX = LevelEnemies[TileX][TileY + i].getLocationX();
                                    float LocY = LevelEnemies[TileX][TileY + i].getLocationY();
                                    boolean Respawnable = LevelEnemies[TileX][TileY + i].isRespawnable();
                                    boolean Respawned = LevelEnemies[TileX][TileY + i].isRespawned();
                                    boolean Alive = LevelEnemies[TileX][TileY + i].isAlive();
                                    int EnemyArrayID = LevelEnemies[TileX][TileY + i].getEnemyArrayID();
                                    float InertiaX = LevelEnemies[TileX][TileY + i].getInertiaX();
                                    float InertiaY = LevelEnemies[TileX][TileY + i].getInertiaY();
                                    String Text = LevelEnemies[TileX][TileY + i].getText();

                                    LevelState.CreateEnemy(EnemyType, EnemyAI, HorzFacing, VertFacing, LocX, LocY, Respawnable, Respawned, Alive, EnemyArrayID, InertiaX, InertiaY, Text);
                                }
                            }
                        }
                    }
                }
                else if (OldTileX < TileX && TileX + ScreenWidthTiles + 1 < LevelWidth)
                {//Player is moving right
                    //Check the rightmost tiles for enemies
                    for (int i = 0; i < ScreenHeightTiles; i++)
                    {
                        if (LevelEnemies[TileX + ScreenWidthTiles + 1][TileY + i] != null)
                        {//Enemy exists in this tile
                            if (LevelState.isEnemyAlive(LevelEnemies[TileX + ScreenWidthTiles + 1][TileY + i].getEnemyArrayID()))
                            {

                                //Enemy is already active and shold not spawn again
                            }
                            else
                            {//Enemy is not currently active
                                if (LevelEnemies[TileX + ScreenWidthTiles + 1][TileY + i].isRespawnable())
                                {//Enemy can respawn.  Since it is not currently alive, respawn it
                                    int EnemyType = LevelEnemies[TileX + ScreenWidthTiles + 1][TileY + i].getEnemyType();
                                    int EnemyAI = LevelEnemies[TileX + ScreenWidthTiles + 1][TileY + i].getEnemyAI();
                                    Global.Directions HorzFacing = LevelEnemies[TileX + ScreenWidthTiles + 1][TileY + i].getHorizontal();
                                    Global.Directions VertFacing = LevelEnemies[TileX + ScreenWidthTiles + 1][TileY + i].getVertical();
                                    float LocX = LevelEnemies[TileX + ScreenWidthTiles + 1][TileY + i].getLocationX();
                                    float LocY = LevelEnemies[TileX + ScreenWidthTiles + 1][TileY + i].getLocationY();
                                    boolean Respawnable = LevelEnemies[TileX + ScreenWidthTiles + 1][TileY + i].isRespawnable();
                                    boolean Respawned = LevelEnemies[TileX + ScreenWidthTiles + 1][TileY + i].isRespawned();
                                    boolean Alive = LevelEnemies[TileX + ScreenWidthTiles + 1][TileY + i].isAlive();
                                    int EnemyArrayID = LevelEnemies[TileX + ScreenWidthTiles + 1][TileY + i].getEnemyArrayID();
                                    float InertiaX = LevelEnemies[TileX + ScreenWidthTiles + 1][TileY + i].getInertiaX();
                                    float InertiaY = LevelEnemies[TileX + ScreenWidthTiles + 1][TileY + i].getInertiaY();
                                    String Text = LevelEnemies[TileX + ScreenWidthTiles + 1][TileY + i].getText();

                                    LevelState.CreateEnemy(EnemyType, EnemyAI, HorzFacing, VertFacing, LocX, LocY, Respawnable, Respawned, Alive, EnemyArrayID, InertiaX, InertiaY, Text);
                                }
                            }
                        }
                    }
                }
            }
            if (OldTileY != TileY)
            {
                //Player is moving up
                if (OldTileY > TileY)
                {
                    for (int i = 0; i < ScreenWidthTiles; i++)
                    {
                        if (LevelEnemies[TileX + i][TileY] != null)
                        {//Enemy exists in this tile
                            if (LevelState.isEnemyAlive(LevelEnemies[TileX + i][TileY].getEnemyArrayID()))
                            {

                                //Enemy is already active and shold not spawn again
                            }
                            else
                            {//Enemy is not currently active
                                if (LevelEnemies[TileX + i][TileY].isRespawnable())
                                {//Enemy can respawn.  Since it is not currently alive, respawn it
                                    int EnemyType = LevelEnemies[TileX + i][TileY].getEnemyType();
                                    int EnemyAI = LevelEnemies[TileX + i][TileY].getEnemyAI();
                                    Global.Directions HorzFacing = LevelEnemies[TileX + i][TileY].getHorizontal();
                                    Global.Directions VertFacing = LevelEnemies[TileX + i][TileY].getVertical();
                                    float LocX = LevelEnemies[TileX + i][TileY].getLocationX();
                                    float LocY = LevelEnemies[TileX + i][TileY].getLocationY();
                                    boolean Respawnable = LevelEnemies[TileX + i][TileY].isRespawnable();
                                    boolean Respawned = LevelEnemies[TileX + i][TileY].isRespawned();
                                    boolean Alive = LevelEnemies[TileX + i][TileY].isAlive();
                                    int EnemyArrayID = LevelEnemies[TileX + i][TileY].getEnemyArrayID();
                                    float InertiaX = LevelEnemies[TileX + i][TileY].getInertiaX();
                                    float InertiaY = LevelEnemies[TileX + i][TileY].getInertiaY();
                                    String Text = LevelEnemies[TileX + i][TileY].getText();

                                    LevelState.CreateEnemy(EnemyType, EnemyAI, HorzFacing, VertFacing, LocX, LocY, Respawnable, Respawned, Alive, EnemyArrayID, InertiaX, InertiaY, Text);
                                }
                            }
                        }
                    }
                }
                else if (OldTileY < TileY && TileY + ScreenHeightTiles < LevelHeight)//Check in place to see if a row of tiles exists to spawn in.
                {//Player is moving down
                    //Check the rightmost tiles for enemies
                    for (int i = 0; i < ScreenWidthTiles; i++)
                    {
                        if (LevelEnemies[TileX + i][TileY + ScreenHeightTiles] != null)
                        {//Enemy exists in this tile
                            if (LevelState.isEnemyAlive(LevelEnemies[TileX + i][TileY + ScreenHeightTiles].getEnemyArrayID()))
                            {

                                //Enemy is already active and shold not spawn again
                            }
                            else
                            {//Enemy is not currently active
                                if (LevelEnemies[TileX + i][TileY + ScreenHeightTiles].isRespawnable())
                                {//Enemy can respawn.  Since it is not currently alive, respawn it
                                    int EnemyType = LevelEnemies[TileX + i][TileY + ScreenHeightTiles].getEnemyType();
                                    int EnemyAI = LevelEnemies[TileX + i][TileY + ScreenHeightTiles].getEnemyAI();
                                    Global.Directions HorzFacing = LevelEnemies[TileX + i][TileY + ScreenHeightTiles].getHorizontal();
                                    Global.Directions VertFacing = LevelEnemies[TileX + i][TileY + ScreenHeightTiles].getVertical();
                                    float LocX = LevelEnemies[TileX + i][TileY + ScreenHeightTiles].getLocationX();
                                    float LocY = LevelEnemies[TileX + i][TileY + ScreenHeightTiles].getLocationY();
                                    boolean Respawnable = LevelEnemies[TileX + i][TileY + ScreenHeightTiles].isRespawnable();
                                    boolean Respawned = LevelEnemies[TileX + i][TileY + ScreenHeightTiles].isRespawned();
                                    boolean Alive = LevelEnemies[TileX + i][TileY + ScreenHeightTiles].isAlive();
                                    int EnemyArrayID = LevelEnemies[TileX + i][TileY + ScreenHeightTiles].getEnemyArrayID();
                                    float InertiaX = LevelEnemies[TileX + i][TileY + ScreenHeightTiles].getInertiaX();
                                    float InertiaY = LevelEnemies[TileX + i][TileY + ScreenHeightTiles].getInertiaY();
                                    String Text = LevelEnemies[TileX + i][TileY + ScreenHeightTiles].getText();

                                    LevelState.CreateEnemy(EnemyType, EnemyAI, HorzFacing, VertFacing, LocX, LocY, Respawnable, Respawned, Alive, EnemyArrayID, InertiaX, InertiaY, Text);
                                }
                            }
                        }
                    }
                }
            }
        }
        
            
        if (EnableObjectSpawn)
        {
            //Check if a new tile was loaded due to movement
            //If so, load the objects for this new movement
            if (OldTileX != TileX)
            {
                //Player is moving left
                if (OldTileX > TileX)
                {
                    for (int i = 0; i < ScreenHeightTiles; i++)
                    {
                        if (LevelObjects[TileX][TileY + i] != null)
                        {//Object exists in this tile
                            if (LevelState.isObjectAlive(LevelObjects[TileX][TileY + i].getObjectArrayID()))
                            {

                                //Object is already active and shold not spawn again
                            }
                            else
                            {//Object is not currently active
                                if (LevelObjects[TileX][TileY + i].isRespawnable())
                                {//Objects can respawn.  Since it is not currently alive, respawn it
                                    int ObjectType = LevelObjects[TileX][TileY + i].getObjectType();
                                    int ObjectAI = LevelObjects[TileX][TileY + i].getObjectAI();
                                    Global.Directions HorzFacing = LevelObjects[TileX][TileY + i].getHorizontal();
                                    Global.Directions VertFacing = LevelObjects[TileX][TileY + i].getVertical();
                                    float LocX = LevelObjects[TileX][TileY + i].getLocationX();
                                    float LocY = LevelObjects[TileX][TileY + i].getLocationY();
                                    boolean Respawnable = LevelObjects[TileX][TileY + i].isRespawnable();
                                    boolean Respawned = LevelObjects[TileX][TileY + i].isRespawned();
                                    boolean Alive = LevelObjects[TileX][TileY + i].isAlive();
                                    int ObjectArrayID = LevelObjects[TileX][TileY + i].getObjectArrayID();
                                    float InertiaX = LevelObjects[TileX][TileY + i].getInertiaX();
                                    float InertiaY = LevelObjects[TileX][TileY + i].getInertiaY();
                                    String Text = LevelObjects[TileX][TileY + i].getText();

                                    LevelState.CreateObject(ObjectType, ObjectAI, HorzFacing, VertFacing, LocX, LocY, Respawnable, Respawned, ObjectArrayID, InertiaX, InertiaY, Text);
                                }
                            }
                        }
                    }
                }
                else if (OldTileX < TileX && TileX + ScreenWidthTiles + 1 < LevelWidth)
                {//Player is moving right
                    //Check the rightmost tiles for Objects
                    for (int i = 0; i < ScreenHeightTiles; i++)
                    {
                        if (LevelObjects[TileX + ScreenWidthTiles + 1][TileY + i] != null)
                        {//Objects exists in this tile
                            if (LevelState.isObjectAlive(LevelObjects[TileX + ScreenWidthTiles + 1][TileY + i].getObjectArrayID()))
                            {

                                //Objects is already active and shold not spawn again
                            }
                            else
                            {//Objects is not currently active
                                if (LevelObjects[TileX + ScreenWidthTiles + 1][TileY + i].isRespawnable())
                                {//Object can respawn.  Since it is not currently alive, respawn it
                                    int ObjectType = LevelObjects[TileX + ScreenWidthTiles + 1][TileY + i].getObjectType();
                                    int ObjectAI = LevelObjects[TileX + ScreenWidthTiles + 1][TileY + i].getObjectAI();
                                    Global.Directions HorzFacing = LevelObjects[TileX + ScreenWidthTiles + 1][TileY + i].getHorizontal();
                                    Global.Directions VertFacing = LevelObjects[TileX + ScreenWidthTiles + 1][TileY + i].getVertical();
                                    float LocX = LevelObjects[TileX + ScreenWidthTiles + 1][TileY + i].getLocationX();
                                    float LocY = LevelObjects[TileX + ScreenWidthTiles + 1][TileY + i].getLocationY();
                                    boolean Respawnable = LevelObjects[TileX + ScreenWidthTiles + 1][TileY + i].isRespawnable();
                                    boolean Respawned = LevelObjects[TileX + ScreenWidthTiles + 1][TileY + i].isRespawned();
                                    boolean Alive = LevelObjects[TileX + ScreenWidthTiles + 1][TileY + i].isAlive();
                                    int ObjectArrayID = LevelObjects[TileX + ScreenWidthTiles + 1][TileY + i].getObjectArrayID();
                                    float InertiaX = LevelObjects[TileX + ScreenWidthTiles + 1][TileY + i].getInertiaX();
                                    float InertiaY = LevelObjects[TileX + ScreenWidthTiles + 1][TileY + i].getInertiaY();
                                    String Text = LevelObjects[TileX + ScreenWidthTiles + 1][TileY + i].getText();

                                    LevelState.CreateObject(ObjectType, ObjectAI, HorzFacing, VertFacing, LocX, LocY, Respawnable, Respawned, ObjectArrayID, InertiaX, InertiaY, Text);
                                }
                            }
                        }
                    }
                }
            }
            if (OldTileY != TileY)
            {
                //Player is moving up
                if (OldTileY > TileY)
                {
                    for (int i = 0; i < ScreenWidthTiles; i++)
                    {
                        if (LevelObjects[TileX + i][TileY] != null)
                        {//Object exists in this tile
                            if (LevelState.isObjectAlive(LevelObjects[TileX + i][TileY].getObjectArrayID()))
                            {

                                //Object is already active and shold not spawn again
                            }
                            else
                            {//Object is not currently active
                                if (LevelObjects[TileX + i][TileY].isRespawnable())
                                {//Object can respawn.  Since it is not currently alive, respawn it
                                    int ObjectType = LevelObjects[TileX + i][TileY].getObjectType();
                                    int ObjectAI = LevelObjects[TileX + i][TileY].getObjectAI();
                                    Global.Directions HorzFacing = LevelObjects[TileX + i][TileY].getHorizontal();
                                    Global.Directions VertFacing = LevelObjects[TileX + i][TileY].getVertical();
                                    float LocX = LevelObjects[TileX + i][TileY].getLocationX();
                                    float LocY = LevelObjects[TileX + i][TileY].getLocationY();
                                    boolean Respawnable = LevelObjects[TileX + i][TileY].isRespawnable();
                                    boolean Respawned = LevelObjects[TileX + i][TileY].isRespawned();
                                    boolean Alive = LevelObjects[TileX + i][TileY].isAlive();
                                    int ObjectArrayID = LevelObjects[TileX + i][TileY].getObjectArrayID();
                                    float InertiaX = LevelObjects[TileX + i][TileY].getInertiaX();
                                    float InertiaY = LevelObjects[TileX + i][TileY].getInertiaY();
                                    String Text = LevelObjects[TileX + i][TileY].getText();

                                    LevelState.CreateObject(ObjectType, ObjectAI, HorzFacing, VertFacing, LocX, LocY, Respawnable, Respawned, ObjectArrayID, InertiaX, InertiaY, Text);
                                }
                            }
                        }
                    }
                }
                else if (OldTileY < TileY && TileY + ScreenHeightTiles < LevelHeight)//Check in place to see if a row of tiles exists to spawn in.
                {//Player is moving down
                    //Check the rightmost tiles for Objects
                    for (int i = 0; i < ScreenWidthTiles; i++)
                    {
                        if (LevelObjects[TileX + i][TileY + ScreenHeightTiles] != null)
                        {//Object exists in this tile
                            if (LevelState.isObjectAlive(LevelObjects[TileX + i][TileY + ScreenHeightTiles].getObjectArrayID()))
                            {

                                //Object is already active and shold not spawn again
                            }
                            else
                            {//Object is not currently active
                                if (LevelObjects[TileX + i][TileY + ScreenHeightTiles].isRespawnable())
                                {//Object can respawn.  Since it is not currently alive, respawn it
                                    int ObjectType = LevelObjects[TileX + i][TileY + ScreenHeightTiles].getObjectType();
                                    int ObjectAI = LevelObjects[TileX + i][TileY + ScreenHeightTiles].getObjectAI();
                                    Global.Directions HorzFacing = LevelObjects[TileX + i][TileY + ScreenHeightTiles].getHorizontal();
                                    Global.Directions VertFacing = LevelObjects[TileX + i][TileY + ScreenHeightTiles].getVertical();
                                    float LocX = LevelObjects[TileX + i][TileY + ScreenHeightTiles].getLocationX();
                                    float LocY = LevelObjects[TileX + i][TileY + ScreenHeightTiles].getLocationY();
                                    boolean Respawnable = LevelObjects[TileX + i][TileY + ScreenHeightTiles].isRespawnable();
                                    boolean Respawned = LevelObjects[TileX + i][TileY + ScreenHeightTiles].isRespawned();
                                    boolean Alive = LevelObjects[TileX + i][TileY + ScreenHeightTiles].isAlive();
                                    int ObjectArrayID = LevelObjects[TileX + i][TileY + ScreenHeightTiles].getObjectArrayID();
                                    float InertiaX = LevelObjects[TileX + i][TileY + ScreenHeightTiles].getInertiaX();
                                    float InertiaY = LevelObjects[TileX + i][TileY + ScreenHeightTiles].getInertiaY();
                                    String Text = LevelObjects[TileX + i][TileY + ScreenHeightTiles].getText();

                                    LevelState.CreateObject(ObjectType, ObjectAI, HorzFacing, VertFacing, LocX, LocY, Respawnable, Respawned, ObjectArrayID, InertiaX, InertiaY, Text);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    public int[][][] GetTileData()
    {
        return TileData;
    }
    
    public int GetTileData(int i, int j, int k)
    {
        return TileData[i][j][k];
    }
    
    public int[] GetTileData(int i, int j)
    {
        return TileData[i][j];
    }
    
    public int GetLevelWidth()
    {
        return LevelWidth;
    }

    public int GetLevelHeight()
    {
        return LevelHeight;
    }
    
    public int[][] GetRoomData()
    {
        return RoomData;
    }
    
    public void DetermineCurrentRoom(int MegaX, int MegaY)
    {
        //Figure out what the room dimensions and location is based on where the Player is right now
        
        //Start by focusing on the character
        //Check above the player for the ceiling.
        //Check each tile above the player one by one (starting on the player)
        //If a tile which says Ceiling is hit, then this is the ceiling level.
        boolean EdgeFound = false;//true once we find the target
        int i = MegaY;//For ceiling, start on MegaMan's tile and move up (subtract from i)
        while (!EdgeFound && i > 0)
        {
            if (RoomData[MegaX][i] == 1 || RoomData[MegaX][i] == 9 || RoomData[MegaX][i] == 13 || RoomData[MegaX][i] == 15)
            {//Ceiling was found.  Set the point and stop here
                CurrentRoomCeiling = i;//Ceiling is a Y co-ordinate
                EdgeFound = true;
            }
            else
            {
                //Ceiling wasn't here.  Move up one square and check again
                i -= 1;
            }
        }
        
        //Do the process again for finding the bottom of the room
        EdgeFound = false;
        i = MegaY;
        while (!EdgeFound && i < LevelHeight)
        {
            if (RoomData[MegaX][i] == 2 || RoomData[MegaX][i] == 10 || RoomData[MegaX][i] == 14 || RoomData[MegaX][i] == 16)
            {
                CurrentRoomBottom = i;
                EdgeFound = true;
            }
            else
            {
                i += 1;
            }
        }
        
        //Check the left and right walls, which are based on the X co-ordinate
        EdgeFound = false;
        i = MegaX;
        while (!EdgeFound && i < LevelWidth)
        {
            if (RoomData[i][MegaY] == 4 || RoomData[i][MegaY] == 8 || RoomData[i][MegaY] == 12)
            {
                CurrentRoomRight = i;
                EdgeFound = true;
            }
            else
            {
                i += 1;
            }
        }
        
        //Checking left
        EdgeFound = false;
        i = MegaX;
        while (!EdgeFound && i > 0)
        {
            if (RoomData[i][MegaY] == 3 || RoomData[i][MegaY] == 7 || RoomData[i][MegaY] == 11)
            {
                CurrentRoomLeft = i;
                EdgeFound = true;
            }
            else
            {
                i -= 1;
            }
        }
        
    }
    
    public int getCurrentRoomTop()
    {
        return CurrentRoomCeiling;
    }
    
    public int getCurrentRoomBottom()
    {
        return CurrentRoomBottom;
    }
    
    public int getCurrentRoomLeft()
    {
        return CurrentRoomLeft;
    }
    
    public int getCurrentRoomRight()
    {
        return CurrentRoomRight;
    }
    
    public void SetRoomTransition()
    {
        CutScene = CutSceneType.RoomTransition;
        EnableEnemySpawn = false;
        EnableObjectSpawn = false;
    }
    
    public void DisableRoomTransition()
    {
        CutScene = CutSceneType.None;
        EnableEnemySpawn = true;
        EnableObjectSpawn = true;
    }
    
    public void SpawnAllRoomEnemies()
    {
        for (int i = CurrentRoomLeft; i <= CurrentRoomRight; i++)
        {
            for (int j = CurrentRoomCeiling; j <= CurrentRoomBottom; j++)
            {
                if (LevelEnemies[i][j] != null)
                {//Enemy exists in this tile
                    if (!LevelState.isEnemyAlive(LevelEnemies[i][j].getEnemyArrayID()) && LevelEnemies[i][j].isRespawnable())
                    {//Enemy can respawn.  Since it is not currently alive, respawn it
                        int EnemyType = LevelEnemies[i][j].getEnemyType();
                        int EnemyAI = LevelEnemies[i][j].getEnemyAI();
                        Global.Directions HorzFacing = LevelEnemies[i][j].getHorizontal();
                        Global.Directions VertFacing = LevelEnemies[i][j].getVertical();
                        float LocX = LevelEnemies[i][j].getLocationX();
                        float LocY = LevelEnemies[i][j].getLocationY();
                        boolean Respawnable = LevelEnemies[i][j].isRespawnable();
                        boolean Respawned = LevelEnemies[i][j].isRespawned();
                        boolean Alive = LevelEnemies[i][j].isAlive();
                        int EnemyArrayID = LevelEnemies[i][j].getEnemyArrayID();
                        float InertiaX = LevelEnemies[i][j].getInertiaX();
                        float InertiaY = LevelEnemies[i][j].getInertiaY();
                        String Text = LevelEnemies[i][j].getText();

                        LevelState.CreateEnemy(EnemyType, EnemyAI, HorzFacing, VertFacing, LocX, LocY, Respawnable, Respawned, Alive, EnemyArrayID, InertiaX, InertiaY, Text);
                  }
                    
                }
            }
        }
    }
    
    public void SpawnAllRoomItems()
    {
        for (int i = CurrentRoomLeft; i <= CurrentRoomRight; i++)
        {
            for (int j = CurrentRoomCeiling; j <= CurrentRoomBottom; j++)
            {
                if (LevelItems[i][j] != null)
                {
                    if (!LevelState.isItemAlive(LevelItems[i][j].getItemArrayID()))
                    {
                        int ItemType = LevelItems[i][j].getItemType();
                        float LocX = LevelItems[i][j].getLocationX();
                        float LocY = LevelItems[i][j].getLocationY();
                        boolean Respawnable = LevelItems[i][j].isRespawnable();
                        int ItemArrayID = LevelItems[i][j].getItemArrayID();
                        boolean DestroyTimer = LevelItems[i][j].DestroyTimer();

                        LevelState.CreateItem(ItemType, LocX, LocY, Respawnable, DestroyTimer, ItemArrayID);
                            

                        
                    }
                    
                }
            }
        }
    }
    
    public void SpawnAllRoomObjects()
    {
        for (int i = CurrentRoomLeft; i <= CurrentRoomRight + 1; i++)//NOTE: CurrentRoomRight is actually 1 short from actual location.  To load objects on this tile, it must be +1
        {
            for (int j = CurrentRoomCeiling; j <= CurrentRoomBottom + 1; j++)
            {
                if (LevelObjects[i][j] != null)
                {
                    if (!LevelState.isObjectAlive(LevelObjects[i][j].getObjectArrayID()))
                    {
                        int ObjectType = LevelObjects[i][j].getObjectType();
                        int AIType = LevelObjects[i][j].getAIType();
                        float LocX = LevelObjects[i][j].getLocationX();
                        float LocY = LevelObjects[i][j].getLocationY();
                        Directions horz = LevelObjects[i][j].getHorizontal();
                        Directions vert = LevelObjects[i][j].getVertical();
                        float inertiax = LevelObjects[i][j].getInertiaX();
                        float inertiay = LevelObjects[i][j].getInertiaY();
                        String text = LevelObjects[i][j].getText();
                        boolean respawnable = LevelObjects[i][j].isRespawnable();
                        boolean respawned = LevelObjects[i][j].isRespawned();
                        int objectarrayid = LevelObjects[i][j].getObjectArrayID();
                        
                        if (ObjectIsInRoom(LocX, LocY))
                        {
                            LevelState.CreateObject(ObjectType, AIType, horz, vert, LocX, LocY, respawnable, respawned, objectarrayid, inertiax, inertiay, text);
                        } else
                        {
                            System.out.println("Weird glitch tried to spawn object out of room");
                        }
                        
                    }
                }
            }
        }
    }
    
    private boolean ObjectIsInRoom(float locationx, float locationy)
    {
        if (locationx >= (CurrentRoomLeft * main.Global.TileWidth) && locationx <= (CurrentRoomRight * main.Global.TileWidth) + main.Global.TileWidth && locationy >= (CurrentRoomCeiling * main.Global.TileHeight) && locationy <= (CurrentRoomBottom * main.Global.TileHeight) + main.Global.TileHeight)
        {
            return true;
        } else
        {
            return false;
        }
    }
    /*
     * Delete an item from the level so it doesn't respawn again.
     * It will come back if the whole level is reloaded.
     */
    public void RemoveItemData(int x, int y)
    {
        LevelItems[x][y] = null;
    }
    
    public int GetStartX()
    {
        return StartX;
    }
    
    public int GetStartY()
    {
        return StartY;
    }
    
    public void RemoveTileMetaData(int X, int Y)
    {
        RoomDataValue[X][Y] = "";
        RoomData[X][Y] = 0;
    }
    
    public void EnableEnemySpawn(boolean value)
    {
        EnableEnemySpawn = value;
    }

    public void EnableObjectSpawn(boolean value)
    {
        EnableObjectSpawn = value;
    }
    
    /*
     * Used when a check point is reached
     */
    public void setStartPosition(int startx, int starty, int music)
    {
        StartX = startx;
        StartY = starty;
        LevelMusic = music;
    }
    
    public int[] getStartPositions()
    {
        int values[] = {StartX, StartY};
        
        return values;
    }
    
    public int[] getCurrentRoomBoundaries()
    {
        int[] room = new int[4];
        room[0] = CurrentRoomCeiling;
        room[1] = CurrentRoomBottom;
        room[2] = CurrentRoomLeft;
        room[3] = CurrentRoomRight;
        
        return room;
    }
    
    public int GetLevelMusic()
    {
        return LevelMusic;
    }
        
    public void setLevelMusic(int value)
    {
        LevelMusic = value;
    }
    
    public int DetermineCeilingOfRoom(int TileX, int TileY)
    {
        //Figure out what the room dimensions and location is for where the character is right now
        
        //Start by focusing on the character
        //Check above the player for the ceiling.
        //Check each tile above the player one by one (starting on the player)
        //If a tile which says Ceiling is hit, then this is the ceiling level.
        boolean EdgeFound = false;//true once we find the target
        int i = TileY;//For ceiling, start on MegaMan's tile and move up (subtract from i)
        int thisRoomCeiling = -1;
        while (!EdgeFound && i > 0)
        {
            if (RoomData[TileX][i] == 1 || RoomData[TileX][i] == 9 || RoomData[TileX][i] == 13 || RoomData[TileX][i] == 15)
            {//Ceiling was found.  Set the point and stop here
                thisRoomCeiling = i;//Ceiling is a Y co-ordinate
                EdgeFound = true;
            }
            else
            {
                //Ceiling wasn't here.  Move up one square and check again
                i -= 1;
            }
        }
        
        return thisRoomCeiling;
    }
    
    public void ReplaceTile(int x, int y, int value, int layerindex, int TileID)
    {
        int[] newtile = new int[]{0};
        
        if (value == 0)
        {//Air tile
            newtile = new int[]{0, 0, 0, 0, 0};
        }
        
        TileData[x][y] = newtile;
        Level.setTileId(x, y, layerindex, TileID);
    }
    
    public void ReplaceTile(int x, int y, int[] value, int layerindex, int TileID)
    {
        TileData[x][y] = value;
        Level.setTileId(x, y, layerindex, TileID);
        
    }

    public void ReplaceAnimateOnTouchTile(int x, int y, int[] value, int layerindex, int TileID, String stringattributes, int speed)
    {
        TileData[x][y] = value;
        
        for (int i = 0; i < main.Global.TileAttributesMax; i++)
        {
            if (value[i] == 16)
            {//This is an animated tile which carries a string.
                //Get the string for this value, and add it to the updated tile.
                        TileAnimation[x][y] = new AnimatedTile(speed, stringattributes);
                        TileAnimation[x][y].isAnimating(false);
                        TileAnimation[x][y].AnimateOnTouch(true);
                        TileData[x][y][i] = 16;//OnTouch
            }
        }
        Level.setTileId(x, y, layerindex, TileID);
        
    }
    
    public void setTileAnimation(int x, int y, boolean value)
    {
        TileAnimation[x][y].isAnimating(value);
    }
}
