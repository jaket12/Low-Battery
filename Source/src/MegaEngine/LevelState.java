package MegaEngine;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.logging.Logger;
import org.newdawn.slick.*;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.newdawn.slick.state.transition.Transition;
import MegaEngine.Global.Directions;
import MegaEngine.Global.CutSceneType;
import MegaEngine.Global.LevelSetupState;
import MegaEngine.ObjectAI.BatteryHealthBar;
import MegaEngine.ObjectAI.PlayerHealthBar;
import MegaEngine.ObjectAI.SundownTimerUI;
import java.io.IOException;

/**
 * It's the actual game!
 * This is where the game loop actually runs. Entities are updated, drawn, and
 * logic is passed through different objects every frame.
 * Some critical data is shared in the Global state so that everything has access
 * to it, but is only really needed in here.
 * If this state is left and another is switched to (like a pause screen), then
 * it will persist until returned.
 * @author McRib
 */
public class LevelState extends BasicGameState
{
    
    public static final int ID = 4;
    private StateBasedGame game;
    private boolean PreRenderLoad = true;//If true, this is the first frame to be seen for the state. Goes true on Leave()
    private KeyBoard Keyboard;
    private Input Input;
    private Transition FadeOut = new FadeOutTransition(Color.black);
    private Transition FadeIn = new FadeInTransition(Color.black);
    
    //A menu that will appear when the user presses a certain button.
    private MenuOptionManager MenuOptions;
    private boolean ShowMenu = false;
    
    //Map Variables are held here: Tiles, enemy spawn data, default music per level
    static LevelManager Level;
    
    /*
     * Screen variables
     * The screen is based on where the player is (or a specific location during cutscene).
     * Everything is drawn relative to where the screen is located.
     * It cannot go beyond the boundaries of the map.
     * It is locked to the top left of the player.
     */
    ScreenManager Screen;
    
    //Static images that don't do anything on the screen. Fancy UI things.
    private StaticImageManager InterfaceImages;
    private ArrayList<MenuText> Text = new ArrayList<MenuText>();
    //Custom Object that shows a symbol for the current player health.
    private PlayerHealthBar PlayerHealth;
    //Custom Object for a symbol to represent battery health.
    private BatteryHealthBar BatteryHealth;
    private MenuText BatteryHealthPercent;
    private SundownTimerUI SundownUI;
    
    private static ChatWindowManager ChatWindow;
    
    /*
     * Player variables
     * The PLAYER dictates what tiles will be drawn and where the screen will be located during normal play.
     * It will have hitbox detection run against it every frame for entities (items, enemies, objects)
     */
    PlayerCharacter ThePlayer;
    
    /*
     * Array of all bullets currently existing.
     * Bullets are added to this array from any object that envokes the AddBullet() function.
     * A bullet must be Alive in order to properly function.
     */
    public static Bullet[] Bullets;
    int Bi;//Iterator for anything, really.  Just don't use it in parallel
    int Bj;
    static int BulletCountMax = 50;//The size of the bullet array: most bullets allowed at once
    static int BulletCounter = 0;//Current place to check to place the next bullet in the array
    int Owner;
    private static Bullet tempbullet;
    
    /*
     * Items
     */
    static Item[] Items;
    private int Ii;
    static int ItemCountMax = 50;
    static int ItemCounter = 0;
    private static Item tempitem;
    
    /*
     * Enemies
     */
    public static Enemy[] Enemies;
    private int Ei;
    static int EnemyCountMax = 50;
    static int EnemyCounter = 0;
    private static Enemy tempenemy;
    
    /*
     * Objects
     */
    public static GameObject[] Objects;
    static int ObjectCountMax = 50;
    static int ObjectCounter = 0;
    private static GameObject tempobject;
    private Directions temphorz;
    private Directions tempvert;
    
    /*
     * Cutscenes and scripts
     */
    private static ArrayList Script = new ArrayList(); //Each line in a script file is placed in order here.
    private static String splits[]; //Dedicated string splitter for efficency in doing scripts
    public static CutSceneType CutScene = CutSceneType.None;
    private static int ReadLine = 0; //Current Line in the script being read
    private static String ScriptCurrentLine = ""; //A copied peice of that line
    private static Timer CutSceneWaitTimer = new Timer("Delay to run next line of script", 1, true, true);
    
    int RoomTileData;//Used in checking for new rooms on collision
    int RoomCheckX;//Tile the character is in the middle of.  Used to check room tiles
    int RoomCheckY;
    int RoomTransitionDiection = 0;
    private Timer RoomTransitionTimer = new Timer("Wait time during a room move", 2000, true, true);
    float DistanceToNextRoom = 0;//On room transition, how much is left before it's done
    float DistanceToNextRoomHorizontal = 111;//How many pixels the character moves on a left/right transition
    float DistanceToNextRoomVertical = 128;//How many pixels for going up/down
    private static String EntityScriptLocation = "";//Anything can call LevelState.PerformEntityCutScene() to start any scene
    private static boolean PerformEntityCutScene = false;//Once the scene is picked up in the state this is set to false and the scene is run as normal
    //If this window interrupted a CustomEvent already in place, switch the state
    //back to CustomEvent when this window closes.
    private static boolean ResumeCustomEventCutSceneOnChatWindowEnd = false;
    
    /*
     * Iterators
     */
    private int Ri; //Used in the render loop
    private int Ui;//Used in update loop
    private int Uj;//Used in update loop
    private static int Ai;//Used for adding items, enemies...
    private static boolean FreeSpaceFound;//Used for searching for empty spots in arrays
    private static int Alivei;//For searching if enemies are alive
    int entitywidth;
    int entityheight;
    float entityX;
    float entityY;
        
    @Override
    public int getID() {
        return ID;
    }

    @Override
    public void init(GameContainer container, StateBasedGame game)
    { 
        this.game = game; 
    }

    /*
     * This function is called only once when the state is first entered upon a new level.
     * Sets up all the data and starts the level cutscene.
     */
    private void Initialize(GameContainer gc, StateBasedGame sbg) throws SlickException 
    {
        Keyboard = main.Global.getKeyboard();
        Input = gc.getInput();
        
        //Menu options for the player to quit or restart or whatever
        MenuOptions = new MenuOptionManager(2, 1);
        MenuOptions.NewOptionButton(0, 0, "Resume", 0, 128, 200);
        MenuOptions.AddStillImageToOption(0, 0, "Resource/Image/Menu/SuperMenuBackground.png", 100, 160, true, 0);
        MenuOptions.AddTextToOption(0, 0, "Paused", 0, 170, Color.yellow);
        MenuOptions.CenterTextLast(0, 0);
        MenuOptions.ShowLastText(0, 0, "Always");
        MenuOptions.AddTextToOption(0, 0, "Resume Game", 160, 200, Color.cyan);
        MenuOptions.ShowLastText(0, 0, "Always");
        MenuOptions.NewOptionButton(1, 0, "Main Menu", 0f, 128, 230);
        MenuOptions.AddTextToOption(1, 0, "Return to Main Menu", 160, 230, Color.blue);
        MenuOptions.ShowLastText(1, 0, "Always");
        MenuOptions.NewSelector("Resource/Image/Menu/SelectorDot.png", 0, 0, true);
        
        //Load up the level, put the player at the start, determine where the screen should display,
        //determine the current room the player is in, and then spawn the entities within that room.
        //After all of that, start the level intro cutscene and enable control to the player to start.
        
        //Load the tilemap for the Level and get the list of all Entities that will
        //be used in the Level. This also determines the starting pixel of the Player.
        Level = new LevelManager("Resource/Level/Level" + main.Global.CurrentLevel + ".tmx");
        if (main.Global.UseCustomPlayerSpawn) {
            Level.setStartPosition(main.Global.CustomPlayerSpawnX, main.Global.CustomPlayerSpawnY, main.Global.CustomPlayerSpawnMusic);
        }
        
        //Create the Player Character at the position found from the Level's Tilemap.
        ThePlayer = new PlayerCharacter(Level.GetStartX(), Level.GetStartY());
        ThePlayer.SetHealth(main.Global.PlayerHealth);
        
        //Place the Screen on the Player's position so we can see him.
        Screen = new ScreenManager((int)(ThePlayer.LocationX - (main.WindowWidth / 2)), (int)(ThePlayer.LocationY - (main.WindowHeight / 2)), Level.GetLevelHeight(), Level.GetLevelWidth(), main.Global.TileWidth, main.Global.TileHeight);
        
        //If there are any static images that need to be on the screen at any time,
        //they can be placed into this manager.
        InterfaceImages = new StaticImageManager();
        PlayerHealth = new PlayerHealthBar(0, Directions.None, Directions.None, 16, 16, false, false, 0, 0, 0, "");
        BatteryHealth = new BatteryHealthBar(0, Directions.None, Directions.None, 64, 24, false, false, 0, 0, 0, "");
        BatteryHealthPercent = new MenuText("", 108, 24, 1, 1, 1, 1, 0);
        SundownUI = new SundownTimerUI(0, Directions.None, Directions.None, 172, 16, false, false, 0, 0, 0, "");
        ChatWindow = new ChatWindowManager();
        
        //Initialize each Entity array for Bullets, Items, Enemies, Objects.
        //Each array will have a generic object to start, but can be replaced
        //with something that actually gets into the game.
        Bullets = new Bullet[BulletCountMax];
        for (Bi = 0; Bi < BulletCountMax; Bi++)
        {
            Bullets[Bi] = new Bullet();
        }
        
        Items = new Item[ItemCountMax];
        for (Ii = 0; Ii < ItemCountMax; Ii++)
        {
            Items[Ii] = new Item();
        }
        
        Enemies = new Enemy[EnemyCountMax];
        for (Ei = 0; Ei < EnemyCountMax; Ei++)
        {
            Enemies[Ei] = new Enemy();
        }
        
        Objects = new GameObject[ObjectCountMax];
        for (Ei = 0; Ei < ObjectCountMax; Ei++)
        {
            Objects[Ei] = new GameObject();
        }
        
        //Figure out the size of the current room and then activate all Entities in it.
        Level.DetermineCurrentRoom(ThePlayer.GetCurrentTileX(), ThePlayer.GetCurrentTileY());
        Level.SpawnAllRoomItems();
        Level.SpawnAllRoomEnemies();
        Level.SpawnAllRoomObjects();
        Screen.setCurrentRoomBoundaries(Level.getCurrentRoomTop(), Level.getCurrentRoomBottom(), Level.getCurrentRoomLeft(), Level.getCurrentRoomRight());

        //We are done setting up.
        main.Global.SetupState = LevelSetupState.Running;
        
        //Now run the intro cutscene for the player to get ready
        if (main.Global.UseCustomPlayerSpawn) {
            //We were told to use a custom spawn, which includes the movie.
            PerformCutScene("Resource/Movie/" + main.Global.CustomPlayerSpawnCutScene + ".scr", CutSceneType.CustomEvent);
            if (main.Global.CustomPlayerSpawnMusic >= 0) {
                main.Global.Music.LoopMusic(main.Global.CustomPlayerSpawnMusic);
            }
        } else {
            //Use the generic default scene
            PerformCutScene("Resource/Movie/Level" + main.Global.CurrentLevel + "Intro.scr", CutSceneType.CustomEvent);
        }
        
        main.Global.UseCustomPlayerSpawn = false;
    }
    
    /**
     * Perform a partial Level state restart.
     * The Level number will be remembered, and the last checkpoint is still 
     * in use.
     */
    private void RestartLevel()
    {
        //Before the reset, hold on to some important information that we need
        //when rebuilding it.
        int startx = Level.GetStartX();
        int starty = Level.GetStartY();
        int levelmusic = Level.GetLevelMusic();
        
        try {
            //Some Levels or Entities may be able to permanently change the Tilemap.
            //We need to fully reload the Tilemap from disk so that any destroyed or altered
            //tiles are reset to their original form.
            Level.ReloadLevelTiles();
        } catch (SlickException ex) {
            System.out.append("Level file has been totally SCREWED UP while trying to restart. Seriously, if this happens I don't know what.");
            Logger.getLogger(LevelState.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        //Reload the Tile Layer and Room Layer based on the fresh set from the disk.
        //Since this contains Room Layer, it also fully resets the Player start
        //location and music to play. That means you would start at the beginning
        //of the level, which sucks. So we'll use our previously saved values above
        //and keep the check point start values.
        Level.LoadTileData();
        if (main.Global.UseCustomPlayerSpawn) {
            Level.setStartPosition(main.Global.CustomPlayerSpawnX, main.Global.CustomPlayerSpawnY, main.Global.CustomPlayerSpawnMusic);
        } else {
            Level.setStartPosition(startx, starty, levelmusic);
        }
        
        try {
            ThePlayer = new PlayerCharacter(Level.GetStartX(), Level.GetStartY());
            ThePlayer.SetHealth(main.Global.PlayerHealth);
        } catch (SlickException ex) {
            Logger.getLogger(LevelState.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        //Reset the screen so it's tracking the player again, instead of being where ever you last died.
        Screen = new ScreenManager((int)(ThePlayer.LocationX - (main.WindowWidth / 2)), (int)(ThePlayer.LocationY - (main.WindowHeight / 2)), Level.GetLevelHeight(), Level.GetLevelWidth(), main.Global.TileWidth, main.Global.TileHeight);

        PlayerHealth = new PlayerHealthBar(0, Directions.None, Directions.None, 16, 16, false, false, 0, 0, 0, "");
        BatteryHealth = new BatteryHealthBar(0, Directions.None, Directions.None, 64, 24, false, false, 0, 0, 0, "");
        SundownUI = new SundownTimerUI(0, Directions.None, Directions.None, 172, 16, false, false, 0, 0, 0, "");
        BatteryHealthPercent = new MenuText("", 108, 24, 1, 1, 1, 1, 0);
        
        ChatWindow = new ChatWindowManager();
        
        //Fully reset all Entitys so there isn't anything hanging around from before.
        Bullets = new Bullet[BulletCountMax];
        for (Bi = 0; Bi < BulletCountMax; Bi++)
        {
            Bullets[Bi] = new Bullet();
        }
        
        Items = new Item[ItemCountMax];
        for (Ii = 0; Ii < ItemCountMax; Ii++)
        {
            Items[Ii] = new Item();
        }
        
        Enemies = new Enemy[EnemyCountMax];
        for (Ei = 0; Ei < EnemyCountMax; Ei++)
        {
            Enemies[Ei] = new Enemy();
        }
        
        Objects = new GameObject[ObjectCountMax];
        for (Ei = 0; Ei < ObjectCountMax; Ei++)
        {
            Objects[Ei] = new GameObject();
        }

        //This whole function is really just a slight alteration of Initialize, if you didn't notice.
        Level.DetermineCurrentRoom(ThePlayer.GetCurrentTileX(), ThePlayer.GetCurrentTileY());
        Screen.setCurrentRoomBoundaries(Level.getCurrentRoomTop(), Level.getCurrentRoomBottom(), Level.getCurrentRoomLeft(), Level.getCurrentRoomRight());
        Level.SpawnAllRoomItems();
        Level.SpawnAllRoomEnemies();
        Level.SpawnAllRoomObjects();
                
        //We are done setting up.
        main.Global.SetupState = LevelSetupState.Running;
        
        //Now run the intro cutscene for the player to get ready
        if (main.Global.UseCustomPlayerSpawn) {
            //We were told to use a custom spawn, which includes the movie.
            PerformCutScene("Resource/Movie/" + main.Global.CustomPlayerSpawnCutScene + ".scr", CutSceneType.CustomEvent);
            if (main.Global.CustomPlayerSpawnMusic >= 0) {
                main.Global.Music.LoopMusic(main.Global.CustomPlayerSpawnMusic);
            }
        } else {
            //Use the generic default scene
            //There is a generic check point cut scene to run that applies to every
            //time you reset any level. This could honestly be improved to be custom.
            PerformCutScene("Resource/Movie/LevelCheckPoint.scr", CutSceneType.CustomEvent);
        }
        
        main.Global.UseCustomPlayerSpawn = false;
    }
    
    @Override
    public void enter(GameContainer gc, StateBasedGame game)
    {
        Keyboard = main.Global.getKeyboard();
        FadeOut = new FadeOutTransition(Color.black);
        main.ClearFrames(true);
        PollCurrentKeys();
    }

    @Override
    public void leave(GameContainer container, StateBasedGame game)
    {
        FadeIn = new FadeInTransition(Color.black);
        PreRenderLoad = true;
        main.Global.PlayerHealth = ThePlayer.GetHealth();
    }
    
    
    
    /**
     * Permanently alter a Tile on the Level so that it holds a different value.
     * @param x Tile Index
     * @param y Tile Index
     * @param value Special Attribute
     * @param layerindex 0 = Tile 1 = Room
     * @param TileID ?
     */
    public static void ReplaceLevelTile(int x, int y, int value, int layerindex, int TileID)
    {
        Level.ReplaceTile(x, y, value, layerindex, TileID);
    }
    
    /**
     * Draw everything to the screen.
     * This does not sync with Update() which means you can have more than one
     * render call per update frame.
     * The order in which you call something to Draw() matters: the first thing
     * to be drawn may be hidden by the next thing that is drawn.
     * @param gc
     * @param sbg
     * @param g 
     */
    @Override
    public void render(GameContainer gc, StateBasedGame sbg, Graphics g)
    {
        if (PreRenderLoad) {
            //If this is the first frame to show when the State is being entered,
            //run a fake update loop so we have something to draw when the
            //screen fades in.
            try {
                main.Global.endUse();
                //If the Level needs to be reset or completely new, do that now.
                //If it's just running normally, then only do the fake update.
                if (main.Global.SetupState == LevelSetupState.New)
                {
                    Initialize(gc, sbg);
                } else if (main.Global.SetupState == LevelSetupState.Restart)
                {
                    RestartLevel();
                }
                
                //Run a fake update call with a delta of 0 to refresh the Level State.
                update(gc, sbg, 0);
            } catch (SlickException ex) {
                System.out.println("Error on Prerender: " + ex.getMessage()); 
            }
            PreRenderLoad = false;
        }
        
        if (main.Global.DrawRoomTiles) {
            //Debug to draw the Room tiles on the screen to see meta data.
            Level.Draw(g, 0);
            Level.Draw(g, 1);
        } else {
            Level.Draw(g, 0);//Draw the basic tiles (no foreground)
        }
        
        for (Ri = 0; Ri < ObjectCountMax; Ri++) {
            Objects[Ri].Draw(g, Screen.ScreenX, Screen.ScreenY);
        }
        
        for (Ri = 0; Ri < EnemyCountMax; Ri++) {
            Enemies[Ri].Draw(g, Screen.ScreenX, Screen.ScreenY);
        }
        
        for (Ri = 0; Ri < BulletCountMax; Ri++) {
            Bullets[Ri].Draw(g, Screen.ScreenX, Screen.ScreenY);
        }
        
        ThePlayer.Draw(g, Screen.ScreenX, Screen.ScreenY);
        
        for (Ri = 0; Ri < ItemCountMax; Ri++) {
            Items[Ri].Draw(g, Screen.ScreenX, Screen.ScreenY);
        }

        PlayerHealth.Draw(g, 32, 32);
        BatteryHealth.Draw(g, 32, 32);
        SundownUI.Draw(g, 32, 32);
        
        //Clear the spritesheet in use so we can do custom stuff.
        main.Global.endUse();
        
        //Now the UI can be drawn.
        for (Ri = 0; Ri < Text.size(); Ri++)
        {
            Text.get(Ri).draw(g);
        }
        
        BatteryHealthPercent.draw(g);
        
        if (CutScene == CutSceneType.ChatWindow) {
            ChatWindow.draw(g);
        }
        
        if (ShowMenu)
        {
            MenuOptions.Draw(g);
        }
    }

    /**
     * The Big One.
     * The update loop is where the game logic comes into play:
     * each time you run this function you play one frame of the game.
     * Delta is how much actual time has passed since the last frame was run.
     * A large delta number means the game is running very slowly.
     * @param gc
     * @param sbg
     * @param delta 
     */
    @Override
    public void update(GameContainer gc, StateBasedGame sbg, int delta)
    {
        //Get the current input for the start of this frame.
        //We will be using it to determine how some Entities will act.
        //This should be here for every State.
        PollCurrentKeys();
        
        //Any logic which run across different States needs to be updated first.
        //This should be here for every State.
        main.Global.Update(delta);
                
        if (ShowMenu)
        {
            //If the Player is using the menu to exit or whatever, we don't want
            //the game playing in the background. They would just die or something
            //stupid, so don't update anything else.
            MenuOptions.update(delta);
        } else 
        {
            //This is the normal game state update loop.
            //Order is important:
            //1. Player updates first so Entities know his location and state.
            //2. Screen updates to focus on the Player so we know what Tiles are active.
            //3. Level updates based on screen location and triggers any active Tiles.
            //4. Entities are updated with the final knowledge of the current frame.
            
            //The player update call looks a little weird: it passes it's own location to itself.
            //The reason for this is that the top level Entity class is the only update you need.
            //If we want to extend the game further, we can have it pass an array of player data for a multiplayer game.
            //1: Player update.
            ThePlayer.Update(delta, Level.GetTileData(), Level.GetRoomData(), ThePlayer);
            
            //2: Screen update.
            Screen.Update(delta, ThePlayer.GetLocationX(), ThePlayer.GetLocationY());
            
            //3: Level update.
            Level.Update(delta, Screen.ScreenX, Screen.ScreenY);

            //4: Entity update.
            for (Ui = 0; Ui < EnemyCountMax; Ui++)
            {
                Enemies[Ui].Update(delta, Level.GetTileData(), Level.GetRoomData(), ThePlayer);
            }

            for (Ui = 0; Ui < ItemCountMax; Ui++)
            {
                Items[Ui].Update(delta, Level.GetTileData(), Level.GetRoomData(), ThePlayer);
            }
            for (Ui = 0; Ui < BulletCountMax; Ui++)
            {
                Bullets[Ui].Update(delta, Level.GetTileData(), Level.GetRoomData(), ThePlayer);
            }
            for (Ui = 0; Ui < ObjectCountMax; Ui++)
            {
                Objects[Ui].Update(delta, Level.GetTileData(), Level.GetRoomData(), ThePlayer);
            }

    // <editor-fold defaultstate="collapsed" desc="Update entities and Check Collision">
            /*
             * Loops upon loops! We have to compare every possible Entity to each other!
             * We can smartly do this by making sure we don't check the same thing twice.
             * If the Player checks collisions against an Item,
             * The Item does not need to check collisions against the Player.
             * We already know it collided from the first loop, why run it again?
             * At the moment, an Entity of one type cannot collide with another
             * Entity of the same type. So Enemies don't run into Enemies.
             *
             * Collision check system (no overlap):
             * Player checks against: Enemy, Object, Bullet, Item, Other Players(?)
             * Enemy: Object, Bullet, Item, Other Enemies(?)
             * Object: Bullet, Item, Other Objects(?)
             * Bullet: Item, Other Bullets(?)
             * Item: No check; Already compared with everything, unless Other Items(?)
             */

            //Player checks
            for (Ui = 0; Ui < EnemyCountMax; Ui++)
            {
                if (Enemies[Ui].IsAlive)
                {
                    entitywidth = Enemies[Ui].getHitBoxWidth();
                    entityheight = Enemies[Ui].getHitBoxHeight();
                    entityX = Enemies[Ui].getLocationX();
                    entityY = Enemies[Ui].getLocationY();

                    if (ThePlayer.CheckCollision(entityX, entityY, entitywidth, entityheight))
                    {
                        //Basic collision has occurred between Player and entity.
                        //Inform both objects of what they collided with.
                        if (ThePlayer.EnemyCollisionEnabled) {
                            ThePlayer.ProcessCollision(Enemies[Ui]);
                        }
                        if (Enemies[Ui].PlayerCollisionEnabled) {
                            Enemies[Ui].ProcessCollision(ThePlayer);
                        }
                    }
                }

            }
            for (Ui = 0; Ui < ObjectCountMax; Ui++)
            {
                if (Objects[Ui].IsAlive)
                {
                    entitywidth = Objects[Ui].getHitBoxWidth();
                    entityheight = Objects[Ui].getHitBoxHeight();
                    entityX = Objects[Ui].getLocationX();
                    entityY = Objects[Ui].getLocationY();

                    if (ThePlayer.CheckCollision(entityX, entityY, entitywidth, entityheight))
                    {
                        //Basic collision has occurred between Player and entity.
                        //Inform both objects of what they collided with.
                        if (ThePlayer.ObjectCollisionEnabled) {
                            ThePlayer.ProcessCollision(Objects[Ui]);
                        }
                        if (Objects[Ui].PlayerCollisionEnabled) {
                            Objects[Ui].ProcessCollision(ThePlayer);
                        }
                    }
                }

            }
            for (Ui = 0; Ui < BulletCountMax; Ui++)
            {
                if (Bullets[Ui].IsAlive)
                {
                    entitywidth = Bullets[Ui].getHitBoxWidth();
                    entityheight = Bullets[Ui].getHitBoxHeight();
                    entityX = Bullets[Ui].getLocationX();
                    entityY = Bullets[Ui].getLocationY();

                    if (ThePlayer.CheckCollision(entityX, entityY, entitywidth, entityheight))
                    {
                        //Basic collision has occurred between Player and entity.
                        //Inform both objects of what they collided with.
                        if (ThePlayer.BulletCollisionEnabled) {
                            ThePlayer.ProcessCollision(Bullets[Ui]);
                        }
                        if (Bullets[Ui].PlayerCollisionEnabled) {
                            Bullets[Ui].ProcessCollision(ThePlayer);
                        }
                    }
                }

            }
            for (Ui = 0; Ui < ItemCountMax; Ui++)
            {
                if (Items[Ui].IsAlive)
                {
                    entitywidth = Items[Ui].getHitBoxWidth();
                    entityheight = Items[Ui].getHitBoxHeight();
                    entityX = Items[Ui].getLocationX();
                    entityY = Items[Ui].getLocationY();

                    if (ThePlayer.CheckCollision(entityX, entityY, entitywidth, entityheight))
                    {
                        //Basic collision has occurred between Player and entity.
                        //Inform both objects of what they collided with.
                        if (ThePlayer.ItemCollisionEnabled) {
                            ThePlayer.ProcessCollision(Items[Ui]);
                        }
                        if (Items[Ui].PlayerCollisionEnabled) {
                            Items[Ui].ProcessCollision(ThePlayer);
                        }
                    }
                }

            }
            //End Player checks

            //Enemy checks
            for (Ui = 0; Ui < EnemyCountMax; Ui++)
            {
                if (Enemies[Ui].IsAlive)
                {
                    for (Uj = 0; Uj < ObjectCountMax; Uj++)
                    {
                        if (Objects[Uj].IsAlive)
                        {
                            entitywidth = Objects[Uj].getHitBoxWidth();
                            entityheight = Objects[Uj].getHitBoxHeight();
                            entityX = Objects[Uj].getLocationX();
                            entityY = Objects[Uj].getLocationY();

                            if (Enemies[Ui].CheckCollision(entityX, entityY, entitywidth, entityheight))
                            {
                                //Basic collision has occurred between Enemy and entity.
                                //Inform both objects of what they collided with.
                                if (Enemies[Ui].ObjectCollisionEnabled) {
                                    Enemies[Ui].ProcessCollision(Objects[Uj]);
                                }
                                if (Objects[Uj].EnemyCollisionEnabled) {
                                    Objects[Uj].ProcessCollision(Enemies[Ui]);
                                }
                            }
                        }
                    }
                }
            }
            for (Ui = 0; Ui < EnemyCountMax; Ui++)
            {
                if (Enemies[Ui].IsAlive)
                {
                    for (Uj = 0; Uj < BulletCountMax; Uj++)
                    {
                        if (Bullets[Uj].IsAlive)
                        {
                            entitywidth = Bullets[Uj].getHitBoxWidth();
                            entityheight = Bullets[Uj].getHitBoxHeight();
                            entityX = Bullets[Uj].getLocationX();
                            entityY = Bullets[Uj].getLocationY();

                            if (Enemies[Ui].CheckCollision(entityX, entityY, entitywidth, entityheight))
                            {
                                //Basic collision has occurred between Enemy and entity.
                                //Inform both objects of what they collided with.
                                if (Enemies[Ui].BulletCollisionEnabled) {
                                    Enemies[Ui].ProcessCollision(Bullets[Uj]);
                                }
                                if (Bullets[Uj].EnemyCollisionEnabled) {
                                    Bullets[Uj].ProcessCollision(Enemies[Ui]);
                                }
                            }
                        }
                    }
                }
            }
            for (Ui = 0; Ui < EnemyCountMax; Ui++)
            {
                if (Enemies[Ui].IsAlive)
                {
                    for (Uj = 0; Uj < ItemCountMax; Uj++)
                    {
                        if (Items[Uj].IsAlive)
                        {
                            entitywidth = Items[Uj].getHitBoxWidth();
                            entityheight = Items[Uj].getHitBoxHeight();
                            entityX = Items[Uj].getLocationX();
                            entityY = Items[Uj].getLocationY();

                            if (Enemies[Ui].CheckCollision(entityX, entityY, entitywidth, entityheight))
                            {
                                //Basic collision has occurred between Enemy and entity.
                                //Inform both objects of what they collided with.
                                if (Enemies[Ui].ItemCollisionEnabled) {
                                    Enemies[Ui].ProcessCollision(Items[Uj]);
                                }
                                if (Items[Uj].EnemyCollisionEnabled) {
                                    Items[Uj].ProcessCollision(Enemies[Ui]);
                                }
                            }
                        }
                    }
                }
            }
            //End Enemy checks

            //Object checks
            for (Ui = 0; Ui < ObjectCountMax; Ui++)
            {
                if (Objects[Ui].IsAlive)
                {
                    for (Uj = 0; Uj < BulletCountMax; Uj++)
                    {
                        if (Bullets[Uj].IsAlive)
                        {
                            entitywidth = Bullets[Uj].getHitBoxWidth();
                            entityheight = Bullets[Uj].getHitBoxHeight();
                            entityX = Bullets[Uj].getLocationX();
                            entityY = Bullets[Uj].getLocationY();

                            if (Objects[Ui].CheckCollision(entityX, entityY, entitywidth, entityheight))
                            {
                                //Basic collision has occurred between entities.
                                //Inform both objects of what they collided with.
                                if (Objects[Ui].BulletCollisionEnabled) {
                                    Objects[Ui].ProcessCollision(Bullets[Uj]);
                                }
                                if (Bullets[Uj].ObjectCollisionEnabled) {
                                    Bullets[Uj].ProcessCollision(Objects[Ui]);
                                }
                            }
                        }
                    }
                }
            }
            for (Ui = 0; Ui < ObjectCountMax; Ui++)
            {
                if (Objects[Ui].IsAlive)
                {
                    for (Uj = 0; Uj < ItemCountMax; Uj++)
                    {
                        if (Items[Uj].IsAlive)
                        {
                            entitywidth = Items[Uj].getHitBoxWidth();
                            entityheight = Items[Uj].getHitBoxHeight();
                            entityX = Items[Uj].getLocationX();
                            entityY = Items[Uj].getLocationY();

                            if (Objects[Ui].CheckCollision(entityX, entityY, entitywidth, entityheight))
                            {
                                //Basic collision has occurred between entities.
                                //Inform both objects of what they collided with.
                                if (Objects[Ui].ItemCollisionEnabled) {
                                    Objects[Ui].ProcessCollision(Items[Uj]);
                                }
                                if (Items[Uj].ObjectCollisionEnabled) {
                                    Items[Uj].ProcessCollision(Objects[Ui]);
                                }
                            }
                        }
                    }
                }
            }
            //Object collides Object (note the slight trickery for iterator increments)
            for (Ui = 0; Ui < ObjectCountMax; Ui++)
            {
                if (Objects[Ui].IsAlive)
                {
                    for (Uj = Ui + 1; Uj < ObjectCountMax; Uj++)
                    {
                        if (Objects[Uj].IsAlive)
                        {
                            entitywidth = Objects[Uj].getHitBoxWidth();
                            entityheight = Objects[Uj].getHitBoxHeight();
                            entityX = Objects[Uj].getLocationX();
                            entityY = Objects[Uj].getLocationY();

                            if (Objects[Ui].CheckCollision(entityX, entityY, entitywidth, entityheight))
                            {
                                //Basic collision has occurred between two objects.
                                //Inform both objects of what they collided with.
                                if (Objects[Ui].ObjectCollisionEnabled) {
                                    Objects[Ui].ProcessCollision(Objects[Uj]);
                                }
                                if (Objects[Uj].ObjectCollisionEnabled) {
                                    Objects[Uj].ProcessCollision(Objects[Ui]);
                                }
                            }
                        }
                    }
                }
            }
            //End Object checks

            //Bullet checks

            //Bullet Collides with Item
            for (Ui = 0; Ui < BulletCountMax; Ui++)
            {
                if (Bullets[Ui].IsAlive)
                {
                    for (Uj = 0; Uj < ItemCountMax; Uj++)
                    {
                        if (Items[Uj].IsAlive)
                        {
                            entitywidth = Items[Uj].getHitBoxWidth();
                            entityheight = Items[Uj].getHitBoxHeight();
                            entityX = Items[Uj].getLocationX();
                            entityY = Items[Uj].getLocationY();

                            if (Bullets[Ui].CheckCollision(entityX, entityY, entitywidth, entityheight))
                            {
                                //Basic collision has occurred between entities.
                                //Inform both objects of what they collided with.
                                if (Bullets[Ui].ItemCollisionEnabled) {
                                    Bullets[Ui].ProcessCollision(Items[Uj]);
                                }
                                if (Items[Uj].BulletCollisionEnabled) {
                                    Items[Uj].ProcessCollision(Bullets[Ui]);
                                }
                            }
                        }
                    }
                }
            }

            //Bullet collides with Bullet (note the slight trickery on iterator increments)
            for (Ui = 0; Ui < BulletCountMax; Ui++)
            {
                if (Bullets[Ui].IsAlive)
                {
                    for (Uj = Ui + 1; Uj < BulletCountMax; Uj++)
                    {
                        if (Bullets[Uj].IsAlive)
                        {
                            entitywidth = Bullets[Uj].getHitBoxWidth();
                            entityheight = Bullets[Uj].getHitBoxHeight();
                            entityX = Bullets[Uj].getLocationX();
                            entityY = Bullets[Uj].getLocationY();

                            if (Bullets[Ui].CheckCollision(entityX, entityY, entitywidth, entityheight))
                            {
                                //Basic collision has occurred between entities.
                                //Inform both objects of what they collided with.
                                if (Bullets[Ui].BulletCollisionEnabled) {
                                    Bullets[Ui].ProcessCollision(Bullets[Uj]);
                                }
                                if (Bullets[Uj].BulletCollisionEnabled) {
                                    Bullets[Uj].ProcessCollision(Bullets[Ui]);
                                }
                            }
                        }
                    }
                }
            }
            //End Bullet checks

            //Item checks
            
            //There are none! Unless we choose to allow Items to collide with themselves.
            
            //End Item checks
            
    // </editor-fold>

            //Cutscenes might occur during the game play that interrupt whatever
            //is happening at the moment.
            if (CutScene == CutSceneType.None) 
            {
                //No special cut scene is occurring. The game is running normally.
                CheckRoomCollision(Level.GetRoomData());
            } else if (CutScene == CutSceneType.RoomTransition)
            {
                //The Player hit a Meta Room Tile that signals to move to the next Room.
                //This means a transition will occur where the new Room is moved into
                //by the Player, and then the Room will be activated.
                if (RoomTransitionTimer.Update(delta))
                {
                    //transition must be forced to end
                    //Update the game
                    Level.DetermineCurrentRoom(ThePlayer.GetCurrentTileX(), ThePlayer.GetCurrentTileY());
                    Screen.setCurrentRoomBoundaries(Level.getCurrentRoomTop(), Level.getCurrentRoomBottom(), Level.getCurrentRoomLeft(), Level.getCurrentRoomRight());
                    Screen.disableRoomTransition();
                    Level.DisableRoomTransition();
                    Level.SpawnAllRoomEnemies();
                    Level.SpawnAllRoomItems();
                    Level.SpawnAllRoomObjects();
                    ThePlayer.SetRoomTransition(0);
                    CutScene = CutSceneType.None;
                }
            } else if (CutScene == CutSceneType.CustomEvent)
            {
                //A custom cut scene was triggerred, which uses a script file to run.
                if (CutSceneWaitTimer.Update(delta)) {
                    //If there was a customer wait delay, we have already triggered it.
                    CutSceneWaitTimer.SetDuration(0);
                    if (ReadLine < Script.size()) {
                        RunScript();
                    } else {
                        CutScene = CutSceneType.None;
                    }
                }
            } else if (CutScene == CutSceneType.ChatWindow) {
                //Somebody is talking.
                ChatWindow.Update(delta);
                if (ChatWindow.IsClosed()) {
                    if (ResumeCustomEventCutSceneOnChatWindowEnd) {
                        CutScene = CutSceneType.CustomEvent;
                    } else {
                        CutScene = CutSceneType.None;
                    }
                }
            }
        }
        
        //Update any static text or images
        for (MenuText text : Text) {
            text.Update(delta);
        }
        
        
        PlayerHealth.Update(delta, Level.GetTileData(), Level.GetRoomData(), ThePlayer);
        BatteryHealth.Update(delta, Level.GetTileData(), Level.GetRoomData(), ThePlayer);
        BatteryHealthPercent.setText((int)(Math.floor((main.Global.BatteryHealth / main.Global.BatteryHealthMax) * 100)) + "%");
        SundownUI.Update(delta, Level.GetTileData(), Level.GetRoomData(), ThePlayer);
        BatteryHealthPercent.Update(delta);
    }
    
    /**
     * Determine if the Player is colliding with a Room Tile.
     * If the Player's Hitbox is colliding, then see what the special attributes
     * are for that Room Tile, and activate it.
     * @param RoomData 
     */
    private void CheckRoomCollision(int[][] RoomData)
    {
        if (!ThePlayer.IsOffScreen())
        {
            //Player is within the room, so tile collisions can occur.
            //If the Player was outside the room, we don't want other room events happening!
            
            //Get the center of the Player to see what tile it is on.
            //Only one tile can be activated at a time, since this is one pixel in the dead center.
            RoomCheckX = (int)((ThePlayer.GetLocationX() + (ThePlayer.getCurrentWidth() / 2)) / main.Global.TileWidth) - 1;
            RoomCheckY = (int)((ThePlayer.GetLocationY() + (ThePlayer.getCurrentHeight() / 2)) / main.Global.TileHeight) - 1;
            RoomTileData = RoomData[RoomCheckX][RoomCheckY];

            //See if this tile contains room data
            if (RoomTileData == 1 || RoomTileData == 2 || RoomTileData == 3 || RoomTileData == 4)
            {
                //Room Edge Transition Tiles
                
                //This tile will trigger the move to the next Room.
                //A cutscene will occur that clears the screen of Entities, and
                //then move the Player and the Screen to the next Room.
                //Once in the new Room, the Level will trigger all Room spawns.

                CutScene = CutSceneType.RoomTransition;
                RoomTransitionTimer.Reset();

                ClearBullets();
                ClearObjects();
                ClearItems();
                ClearEnemies();
                //Yeah BOIE!

                //Disable all player input, freeze the current animation, and store the room edge type
                Screen.SetRoomTransition(RoomTileData);
                Level.SetRoomTransition();
                ThePlayer.SetRoomTransition(RoomTileData);
            }
            else if (RoomTileData == 5)
            {
                //A cutscene will be played that is custom
                splits = Level.getRoomTileValue(RoomCheckX, RoomCheckY).split("~");
                String path = splits[1];
                PerformCutScene("Resource/Movie/Level" + main.Global.CurrentLevel + path + ".scr", CutSceneType.CustomEvent);
                DisableCutSceneTile(RoomCheckX, RoomCheckY);
            }
            else if (RoomTileData == 6)
            {
                //Player hit a checkpoint tile.  
                //The special attribute for the Tile is the details for how the checkpoint works.
                //SpawnTileX~SpawnTileY~TeleTileX~TeleTileY~MusicIndex
                //These values will be saved into the Level, and will be used if the
                //Game State calls RestartLevel().
                
                //Get the landing position when starting
                String[] rsplits = Level.getRoomTileValue(RoomCheckX, RoomCheckY).split("~");
                int spawnx = Integer.parseInt(rsplits[1]) * main.Global.TileWidth;
                int spawny = Integer.parseInt(rsplits[2]) * main.Global.TileHeight;
                
                //Music to play when porting in
                int music = Integer.parseInt(rsplits[3]);
                
                Level.setStartPosition(spawnx, spawny, music);

                //Check points only trigger once per life. Remove it from the Tile Map.
                Level.RemoveTileMetaData(RoomCheckX, RoomCheckY);
            }
        }

    }
    
    private void DisableCutSceneTile(int X, int Y)
    {
        Level.RemoveTileMetaData(X, Y);
    }
        
    private void ClearEnemies()
    {
        for (int i = 0; i < EnemyCountMax; i++)
        {
            Enemies[i] = new Enemy();
        }
    }
    
    private void ClearObjects()
    {
        for (int i = 0; i < ObjectCountMax; i++)
        {
            Objects[i] = new GameObject();
        }
    }
    
    private void ClearItems()
    {
        for (int i = 0; i < ItemCountMax; i++)
        {
            Items[i] = new Item();
        }
    }
    
    private void ClearBullets()
    {
        for (int i = 0; i < BulletCountMax; i++)
        {
            Bullets[i] = new Bullet();
        }
    }
    
    /**
     * Every entity has a value of DeleteOnPause (default false);
     * If true, this function will remove it from the game.
     */
    private void DeleteEntitiesOnPause()
    {
        for (int i = 0; i < BulletCountMax; i++)
        {
            if (Bullets[i].IsAlive && Bullets[i].DeleteOnPause)
            {
                Bullets[i].IsAlive = false;
            }
        }
        
        for (int i = 0; i < ObjectCountMax; i++)
        {
            if (Objects[i].IsAlive && Objects[i].DeleteOnPause)
            {
                Objects[i].IsAlive = false;
            }
        }
        
        for (int i = 0; i < EnemyCountMax; i++)
        {
            if (Enemies[i].IsAlive && Enemies[i].DeleteOnPause)
            {
                Enemies[i].IsAlive = false;
            }
        }
        
        for (int i = 0; i < ItemCountMax; i++)
        {
            if (Items[i].IsAlive && Items[i].DeleteOnPause)
            {
                Items[i].IsAlive = false;
            }
        }
        
    }
    
    public static void AddBullet(Bullet bullet)
    {
        //Check to see if this bullet is allowed to be placed in game
        //Some times a bullet shouldn't be removed or the limit was reached or something
        //If bullet can be added, then do it.
        
        Ai = 0;//How many spaces were searched
        FreeSpaceFound = false;
        while(!FreeSpaceFound && Ai < BulletCountMax)
        {
            if (!Bullets[BulletCounter].IsAlive)
            {
                //Found an empty bullet location
                Bullets[BulletCounter] = bullet;
                FreeSpaceFound = true;
            }
            else
            {
                Ai++;
            }
            
            BulletCounter++;
            if (BulletCounter >= BulletCountMax)
            {
                BulletCounter = 0;
            }
        }

        if (FreeSpaceFound)
        {
           // System.out.println("Object added");
        } else
        {
            System.err.println("Bullet could not be added: no free space found.");
        }
    }
    
    public static void AddObject(GameObject object)
    {
        Ai = 0;//How many spaces were searched
        FreeSpaceFound = false;
        while(!FreeSpaceFound && Ai < ObjectCountMax)
        {
            if (!Objects[ObjectCounter].IsAlive)
            {
                //Found an empty location
                Objects[ObjectCounter] = object;
                FreeSpaceFound = true;
            }
            else
            {
                Ai++;
            }
            
            ObjectCounter++;
            if (ObjectCounter >= ObjectCountMax)
            {
                ObjectCounter = 0;
            }
        }
        if (FreeSpaceFound)
        {
         //   System.out.println("Object added" + object.Name);
        } else
        {
            System.err.println("Object could not be added: no free space found.");
        }
        
    }
    
    public static void AddItem(Item item)
    {
        Ai = 0;//How many spaces were searched
        FreeSpaceFound = false;
        while(!FreeSpaceFound && Ai < ItemCountMax)
        {
            if (!Items[ItemCounter].IsAlive)
            {
                //Found an empty location
                Items[ItemCounter] = item;
                FreeSpaceFound = true;
            }
            else
            {
                Ai++;
            }
            
            ItemCounter++;
            if (ItemCounter >= ItemCountMax)
            {
                ItemCounter = 0;
            }
        }
        
        if (FreeSpaceFound)
        {
           // System.out.println("Object added");
        } else
        {
            System.err.println("Item could not be added: no free space found.");
        }
    }
    
    public static int AddEnemy(Enemy enemy)
    {
        Ai = 0;//How many spaces were searched
        FreeSpaceFound = false;
        int EnemyArrayLocation = -1;
        while(!FreeSpaceFound && Ai < EnemyCountMax)
        {
            if (!Enemies[EnemyCounter].IsAlive)
            {
                //Found an empty location
                Enemies[EnemyCounter] = enemy;
                Enemies[EnemyCounter].IsAlive = true;
                FreeSpaceFound = true;
                EnemyArrayLocation = EnemyCounter;
            }
            else
            {
                Ai++;
            }
            
            EnemyCounter++;
            if (EnemyCounter >= EnemyCountMax)
            {
                EnemyCounter = 0;
            }
        }
        
        if (FreeSpaceFound)
        {
           // System.out.println("Enemy added");
        } else
        {
            System.err.println("Enemy could not be added: no free space found.");
        }
        return EnemyArrayLocation;
    }
    
    public void ClearAllEnemies()
    {
        for (Enemy e : Enemies)
        {
            e.IsAlive = false;
        }
    }
        
    private void ClearAllObjects()
    {
        for (GameObject o : Objects)
        {
            o.IsAlive = false;
        }
    }
    
    private void ClearNormalObjects()
    {
        for (GameObject o : Objects)
        {
            if (!o.IsOverWritable)
            {
                o.IsAlive = false;
            }
        }
    }
    
    private void ClearAllBullets()
    {
        for (Bullet b : Bullets)
        {
            b.IsAlive = false;
        }
    }
    
    private void ClearNormalBullets()
    {
        for (Bullet b : Bullets)
        {
            if (!b.IsOverWritable)
            {
                b.IsAlive = false;
            }
        }
    }
    
    private void ClearPlayerBullets()
    {
        for (Bullet b : Bullets)
        {
            if (b.OwnerName.equals(ThePlayer.GetName()))
            {
                b.IsAlive = false;
            }
        }
    }
    
    public static boolean isEnemyAlive(int ID)
    {
        //Check each enemy's ID to see if it is found.  if so, see if it is alive
        for (Alivei = 0; Alivei < Enemies.length; Alivei++)
        {
            if (Enemies[Alivei].GetEntityArrayID() == ID && Enemies[Alivei].IsAlive == true)
            {
                //System.out.println("Match found enemy is alive: " + Enemies[i].IsAlive);
                return Enemies[Alivei].IsAlive;
            }
        }
        return false;
    }
    
    public static boolean isItemAlive(int ID)
    {
        //Check each enemy's ID to see if it is found.  if so, see if it is alive
        for (Alivei = 0; Alivei < Items.length; Alivei++)
        {
            if (Items[Alivei].GetEntityArrayID() == ID && Enemies[Alivei].IsAlive == true)
            {
                //System.out.println("Match found enemy is alive: " + Enemies[i].IsAlive);
                return Enemies[Alivei].IsAlive;
            }
        }
        return false;
    }
    
    public static boolean isObjectAlive(int ID)
    {
        for (Alivei = 0; Alivei < Objects.length; Alivei++)
        {
            if (Objects[Alivei].GetEntityArrayID() == ID && Objects[Alivei].IsAlive)
            {
                return Objects[Alivei].IsAlive;
            }
        }
        return false;
    }
    
    public static boolean isObjectTypeAlive(String name)
    {
        //Check each enemy's ID to see if it is found.  if so, see if it is alive
        for (Alivei = 0; Alivei < Objects.length; Alivei++)
        {
            if (Objects[Alivei].IsAlive && Objects[Alivei].Name.equals(name))
            {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isBulletTypeAlive(String name)
    {
        //Check each enemy's ID to see if it is found.  if so, see if it is alive
        for (Alivei = 0; Alivei < Bullets.length; Alivei++)
        {
            if (Bullets[Alivei].IsAlive && Bullets[Alivei].Name.equals(name))
            {
                return true;
            }
        }
        return false;
    }
    
    public static void CreateItem(int type, float LocX, float LocY, boolean Respawnable, boolean DeleteTimer, int ItemArrayID)
    {
        tempitem = new Item();
        
        if (type == -1)
        {
            //create a random item type
            int value = main.Global.Random.nextInt(128);

            if (value <= 1)
            {//Extra life
                type = 5;
            }else if (value <= 6)
            {//Big Weapon
                type = 4;
            }else if (value <= 10)
            {//Big Health
                type = 3;
            }else if (value <= 35)
            {//Small weapon
                type = 2;
            }else if (value <= 50)
            {//Small health
                type = 1;
            }else if (value <= 128)
            {//NOTHING!!
                type = 0;
            }else if (value > 128)
            {//E tank
                //Code exists but is unreachable
                type = 6;
            }
        }
        
        switch (type)
        {
            case 0:
                //No item
                break;
            case 1:
                //Generic Item
                tempitem = new MegaEngine.ItemAI.ItemGeneric(LocX, LocY, Respawnable, DeleteTimer);
                break;
        }
        
        tempitem.setEntityArrayID(ItemArrayID);
        AddItem(tempitem);
    }
    
    public static int CreateEnemy(int type, int aitype, Global.Directions horzfacing, Global.Directions vertfacing, float LocX, float LocY, boolean Respawnable, boolean respawned, boolean alive, int EnemyArrayID, float inertiax, float inertiay, String text)
    {
        tempenemy = new Enemy();
        
        switch (type)
        {
            case 0:
                //none
                break;
            case 1:
                //met
                tempenemy = new MegaEngine.EnemyAI.EnemyGeneric(aitype, horzfacing, vertfacing, LocX, LocY);
                break;
            case 2:
                //Zombie
                tempenemy = new MegaEngine.EnemyAI.Zombie(aitype, horzfacing, vertfacing, LocX, LocY, Respawnable, respawned, EnemyArrayID, inertiax, inertiay, text);
                break;
        }
        
        tempenemy.setEntityArrayID(EnemyArrayID);
        return AddEnemy(tempenemy);
    }
    
    public static void CreateBullet(int bullet_type, Global.Directions horzfacing, Global.Directions vertfacing, float locationx, float locationy, int ai_type, int damage, boolean isplayerowned, boolean isfriendly, boolean ishostile, String ownername, int owner, boolean reclaim_me)
    {
        tempbullet = new Bullet();
        
        switch (bullet_type)
        {
            case 0:
                //none
                break;
            case 1:
                //Mega buster normal shot
                tempbullet = new MegaEngine.BulletAI.BulletGeneric(ai_type, horzfacing, vertfacing, locationx, locationy, isplayerowned, isfriendly, ishostile, ownername, owner);
                break;
        }
        
        AddBullet(tempbullet);
    }
    
    public static void CreateObject(int objecttype, int aitype, Directions horzfacing, Directions vertfacing, float locationx, float locationy, boolean respawnable, boolean respawned, int objectarrayid, float inertiax, float inertiay, String text)
    {
        tempobject = new GameObject();
        
        switch (objecttype)
        {
            case 0:
                //none
                break;
            case 1:
                //Generic Object
                tempobject = new MegaEngine.ObjectAI.GenericObject(aitype, horzfacing, vertfacing, locationx, locationy, respawnable, respawned, objectarrayid, inertiax, inertiay, text);
                break;
            case 2:
                //Cut Scene Starter
                tempobject = new MegaEngine.ObjectAI.SimpleCutSceneObject(aitype, horzfacing, vertfacing, locationx, locationy, respawnable, respawned, objectarrayid, inertiax, inertiay, text);
                break;
            case 3:
                //Player Activator
                tempobject = new MegaEngine.ObjectAI.PlayerActivator(aitype, horzfacing, vertfacing, locationx, locationy, respawnable, respawned, objectarrayid, inertiax, inertiay, text);
                break;
            case 4:
                //Level 1 - Door to leave
                tempobject = new MegaEngine.ObjectAI.Level1Door(aitype, horzfacing, vertfacing, locationx, locationy, respawnable, respawned, objectarrayid, inertiax, inertiay, text);
                break;
            case 5:
                //Level 1 - Bed to rest
                tempobject = new MegaEngine.ObjectAI.Level1Bed(aitype, horzfacing, vertfacing, locationx, locationy, respawnable, respawned, objectarrayid, inertiax, inertiay, text);
                break;
            case 6:
                //Zombie Spawner
                tempobject = new MegaEngine.ObjectAI.ZombieSpawner(aitype, horzfacing, vertfacing, locationx, locationy, respawnable, respawned, objectarrayid, inertiax, inertiay, text);
                break;
            case 7:
                //Level 1 - Life Support System
                tempobject = new MegaEngine.ObjectAI.Level1LifeSupport(aitype, horzfacing, vertfacing, locationx, locationy, respawnable, respawned, objectarrayid, inertiax, inertiay, text);
                break;
            case 8:
                //Level 2 - Door to enter Level 1
                tempobject = new MegaEngine.ObjectAI.Level2DoorToLevel1(aitype, horzfacing, vertfacing, locationx, locationy, respawnable, respawned, objectarrayid, inertiax, inertiay, text);
                break;
            case 9:
                //Level 2 - Door to enter Level 3
                tempobject = new MegaEngine.ObjectAI.Level2DoorToLevel3(aitype, horzfacing, vertfacing, locationx, locationy, respawnable, respawned, objectarrayid, inertiax, inertiay, text);
                break;
            case 10:
                //Level 3 - Door to enter Level 2
                tempobject = new MegaEngine.ObjectAI.Level3DoorToLevel2(aitype, horzfacing, vertfacing, locationx, locationy, respawnable, respawned, objectarrayid, inertiax, inertiay, text);
                break;
            case 11:
                //Level 3 - Door to enter Level 4
                tempobject = new MegaEngine.ObjectAI.Level2DoorToLevel4(aitype, horzfacing, vertfacing, locationx, locationy, respawnable, respawned, objectarrayid, inertiax, inertiay, text);
                break;
            case 12:
                //Level 2 - Dead Guy
                tempobject = new MegaEngine.ObjectAI.Level2DeadGuy(aitype, horzfacing, vertfacing, locationx, locationy, respawnable, respawned, objectarrayid, inertiax, inertiay, text);
                break;
            case 13:
                //Level 2 - Desk Drawer
                tempobject = new MegaEngine.ObjectAI.Level2DeskDrawer(aitype, horzfacing, vertfacing, locationx, locationy, respawnable, respawned, objectarrayid, inertiax, inertiay, text);
                break;
            case 14:
                //Level 3 - Door Passcode
                tempobject = new MegaEngine.ObjectAI.Level3DoorPasscode(aitype, horzfacing, vertfacing, locationx, locationy, respawnable, respawned, objectarrayid, inertiax, inertiay, text);
                break;
            case 15:
                //Level 4 - Door to Level 5
                tempobject = new MegaEngine.ObjectAI.Level4DoorToLevel5(aitype, horzfacing, vertfacing, locationx, locationy, respawnable, respawned, objectarrayid, inertiax, inertiay, text);
                break;
            case 16:
                //Level 5 - Door to Level 4
                tempobject = new MegaEngine.ObjectAI.Level5DoorToLevel4(aitype, horzfacing, vertfacing, locationx, locationy, respawnable, respawned, objectarrayid, inertiax, inertiay, text);
                break;
            case 17:
                //Level 4 - Door to Level 6
                tempobject = new MegaEngine.ObjectAI.Level4DoorToLevel6(aitype, horzfacing, vertfacing, locationx, locationy, respawnable, respawned, objectarrayid, inertiax, inertiay, text);
                break;
            case 18:
                //Level 6 - Door to Level 4
                tempobject = new MegaEngine.ObjectAI.Level6DoorToLevel4(aitype, horzfacing, vertfacing, locationx, locationy, respawnable, respawned, objectarrayid, inertiax, inertiay, text);
                break;
            case 19:
                //Level 6 - Locked Tool Kit
                tempobject = new MegaEngine.ObjectAI.Level6LockedToolKit(aitype, horzfacing, vertfacing, locationx, locationy, respawnable, respawned, objectarrayid, inertiax, inertiay, text);
                break;
            case 20:
                //Level 6 - Furnace
                tempobject = new MegaEngine.ObjectAI.Level6Furnace(aitype, horzfacing, vertfacing, locationx, locationy, respawnable, respawned, objectarrayid, inertiax, inertiay, text);
                break;
            case 21:
                //Level 6 - Door to Level 7
                tempobject = new MegaEngine.ObjectAI.Level6DoorToLevel7(aitype, horzfacing, vertfacing, locationx, locationy, respawnable, respawned, objectarrayid, inertiax, inertiay, text);
                break;
            case 22:
                //Level 7 - Door to Level 6
                tempobject = new MegaEngine.ObjectAI.Level7DoorToLevel6(aitype, horzfacing, vertfacing, locationx, locationy, respawnable, respawned, objectarrayid, inertiax, inertiay, text);
                break;
            case 23:
                //Level 7 - Generator
                tempobject = new MegaEngine.ObjectAI.Level7Generator(aitype, horzfacing, vertfacing, locationx, locationy, respawnable, respawned, objectarrayid, inertiax, inertiay, text);
                break;
            case 24:
                //Simple Door
                tempobject = new MegaEngine.ObjectAI.SimpleDoor(aitype, horzfacing, vertfacing, locationx, locationy, respawnable, respawned, objectarrayid, inertiax, inertiay, text);
                break;
            case 25:
                //Frozen guy
                tempobject = new MegaEngine.ObjectAI.Level5FrozenGuy(aitype, horzfacing, vertfacing, locationx, locationy, respawnable, respawned, objectarrayid, inertiax, inertiay, text);
                break;
            case 26:
                //Level 4- Door to Level 2
                tempobject = new MegaEngine.ObjectAI.Level4DoorToLevel2(aitype, horzfacing, vertfacing, locationx, locationy, respawnable, respawned, objectarrayid, inertiax, inertiay, text);
                break;
        }
        tempobject.setEntityArrayID(objectarrayid);
        AddObject(tempobject);
    }
    
    /*
     * Deletes ALL bullets who share a given name
     */
    public static void DeleteBulletByName(String Name)
    {
        for (Bullet Bullet : Bullets) {
            if (Bullet.IsAlive && Bullet.Name.equals(Name)) {
                Bullet.IsAlive = false;
            }
        }
    }
    
    /**
     * The Level will do something special based on a script file loaded into
     * the state on the next frame.
     * This is good for starting a level with a Ready! message, or ending the
     * level with an animation and screen swap. 
     * @param filepath
     * @param type
     */
    public static void PerformCutScene(String filepath, CutSceneType type)
    {
        OpenScriptFile(filepath);
        CutScene = type;
        CutSceneWaitTimer.SetTimeRemaining(0);
        ReadLine = 0;
    }
    
    /**
     * Access the script file from disk that was requested to be played.
     * This will completely erase any script that is currently in memory or use.
     * @param FileLocation 
     */
    private static void OpenScriptFile(String FileLocation) 
    {
        try 
        {
            Script = new ArrayList();
            ReadLine = 0;
            ScriptCurrentLine = "";
            
            InputStream in;
            in = LevelState.class.getResourceAsStream("/" + FileLocation);
            Reader fr = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(fr);

            String strLine;
            
            while ((strLine = br.readLine()) != null)
            {
                splits = strLine.split("~");

                if (splits[0].charAt(0) == '/')
                {//Comment in the script, ignore and move on.
                    
                }
                else
                {//Add a line to the script. It will be parsed when it should run.
                    Script.add(strLine);
                }
                
            }//End while
            
            in.close();
            
        } 
        catch (IOException e) 
        {
            System.err.println("LevelState Error OpenScriptFile: Input: " + FileLocation + ". Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("LevelState Error OpenScriptFile: Input: " + FileLocation + ". Error: " + e.getMessage());
        }
      
    }
    
    /**
     * Read the line line in the CutScene script list and execute it.
     * Only one line of a script can be run per frame. It's a lazy problem I could
     * fix later.
     */
    private void RunScript()
    {
        ScriptCurrentLine = Script.get(ReadLine).toString();
        ReadLine += 1;
        
        splits = ScriptCurrentLine.split("~");
        
        if (splits[0].equalsIgnoreCase("Load"))
        {//Add a new static image or text to draw to the screen.
            if(splits[1].equalsIgnoreCase("Text"))
            {
                Text.add(new MenuText(splits[2], Float.parseFloat(splits[3]), Float.parseFloat(splits[4]), Integer.parseInt(splits[5])));
            }
        }
        else if (splits[0].equalsIgnoreCase("Delete All"))
        {//Remove all static images or text on the screen.
            if(splits[1].equalsIgnoreCase("Text"))
            {
                for (int l = 0; l < Text.size(); l++)
                {
                    Text.remove(l);
                }
            }
        }
        else if (splits[0].equalsIgnoreCase("Wait"))
        {//Set a time delay before the line line runs in the script.
            CutSceneWaitTimer.SetDuration(Integer.parseInt(splits[1]));
            CutSceneWaitTimer.SetTimeRemaining(Integer.parseInt(splits[1]));
        }
        else if (splits[0].equalsIgnoreCase("Disable Input"))
        {
            //Disable listening to ALL input from the Player.
            //This has to be respected in the KeyPress/Poll functions of the Game State.
            //Entities may still be trying to listen for input, but will not recieve any.
            main.Global.Keyboard.IgnorePlayer1Input = Boolean.valueOf(splits[1]);
            main.Global.Keyboard.IgnorePlayer2Input = Boolean.valueOf(splits[1]);
        }
        else if (splits[0].equalsIgnoreCase("Text"))
        {
            RunTextScript(splits);
        }
        else if (splits[0].equalsIgnoreCase("Sound"))
        {
            if (splits[1].equalsIgnoreCase("Play"))
            {
                main.Global.Sound.PlaySound(Integer.valueOf(splits[2]));
            }
            else if (splits[1].equalsIgnoreCase("Stop All"))
            {
                main.Global.Sound.StopAllSound();
            }
            else if (splits[1].equalsIgnoreCase("Stop"))
            {
                main.Global.Sound.StopSound(Integer.valueOf(splits[2]));
            }
        }
        else if (splits[0].equalsIgnoreCase("Music"))
        {
            if (splits[1].equalsIgnoreCase("Play"))
            {
                if (splits[2].equalsIgnoreCase("Random Range"))
                {//Play any music starting from X to Y
                    int difference = Integer.parseInt(splits[4]) - Integer.parseInt(splits[3]);
                    main.Global.Music.PlayMusic(Integer.parseInt(splits[3]) + main.Global.Random.nextInt(difference));
                } else
                {//Play this exact number
                    main.Global.Music.PlayMusic(Integer.valueOf(splits[2]));
                }
            }
            else if (splits[1].equalsIgnoreCase("Stop"))
            {
                main.Global.Music.StopMusic();
            }
            else if (splits[1].equalsIgnoreCase("Loop"))
            {
                if (splits[2].equalsIgnoreCase("Level Music"))
                {//Music defined by the checkpoint hit. Blank if no checkpoint hit.
                    main.Global.Music.LoopMusic(Level.GetLevelMusic());
                } else if (splits[1].equalsIgnoreCase("Random Range"))
                {//Play any music starting from X to (X + Y)
                    main.Global.Music.LoopMusic(Integer.parseInt(splits[3]) + main.Global.Random.nextInt(Integer.parseInt(splits[4])));
                }else
                {
                    main.Global.Music.LoopMusic(Integer.valueOf(splits[2]));
                }
                
            }
            else if (splits[1].equalsIgnoreCase("Set Level Music"))
            {
                //Set the music for this Level. If the Level restarts, then this 
                //song will automatically play instead of the default.
                Level.setLevelMusic(Integer.parseInt(splits[2]));
            }
            else if (splits[1].equalsIgnoreCase("Store"))
            {//Store the current music data to be retrieved later.
                //This saves the timestamp of the music, so it can be resumed.
                main.Global.Music.StoreCurrentMusic(splits[2]);
            }
            else if (splits[1].equalsIgnoreCase("Load"))
            {//Play the stored music.
                //Great for resuming where it left off in a movie.
                main.Global.Music.LoadMusic(splits[2], Boolean.parseBoolean(splits[3]));
            }
            else if (splits[1].equalsIgnoreCase("Fade Out"))
            {
                main.Global.Music.FadeOut(Integer.parseInt(splits[2]));
            }
            else if (splits[1].equalsIgnoreCase("Fade In"))
            {
                main.Global.Music.FadeIn(Integer.parseInt(splits[2]));
            }
            
        }
        else if (splits[0].equalsIgnoreCase("Resume"))
        {
            //End the cut scene and resume normal play.
            CutScene = CutSceneType.None;
        }
        else if (splits[0].equalsIgnoreCase("PlayerCharacter"))
        {
            //Effect is going to happen to the main character
            RunScriptPlayerCharacter(splits);
        }
        else if (splits[0].equalsIgnoreCase("Enemy"))
        {
            //Script is going to happen to an Enemy
            RunScriptEnemy(splits);
        }
        else if (splits[0].equalsIgnoreCase("Object"))
        {
            //Script is going to happen to an Object Entity
            RunScriptObject(splits);
        }
        else if (splits[0].equalsIgnoreCase("Bullet"))
        {
            //Script is going to happen to a Bullet
            RunScriptBullet(splits);
        }
        else if (splits[0].equalsIgnoreCase("Set Movie"))
        {
            //Change the Movie Script that will be played the next time that the
            //game swaps to the Movie State. This does not activate the Movie,
            //it only points the script.
            main.Global.SetMovieScriptLocation("/Resource/Movie/" + splits[1] + ".scr");
        }
        else if (splits[0].equalsIgnoreCase("Swap State"))
        {
            //Move to another state in the application (Menu, movie, level, etc)
            //This is based on the ID that every Game State has.
            SwapState(Integer.valueOf(splits[1]));
        }
        else if (splits[0].equalsIgnoreCase("Set New Level"))
        {
            //Set the flag for starting the Level from stratch.
            //The next time the Level State is entered, it will do a full reset
            //and start from the beginning of the level.
            main.Global.SetupState = LevelSetupState.New;
        }
        else if (splits[0].equalsIgnoreCase("Set Level"))
        {
            //Set the level number for the CurrentLevel.
            //Used for when you need to restart or load something else.
            main.Global.CurrentLevel = Integer.parseInt(splits[1]);
        }
        else if (splits[0].equalsIgnoreCase("Set Custom Player Spawn"))
        {
            //A custom location for the Player to spawn on the next level load.
            main.Global.CustomPlayerSpawnX = Integer.parseInt(splits[1]);
            main.Global.CustomPlayerSpawnY = Integer.parseInt(splits[2]);
            main.Global.CustomPlayerSpawnMusic = Integer.parseInt(splits[3]);
            main.Global.CustomPlayerSpawnCutScene = splits[4];
            main.Global.UseCustomPlayerSpawn = true;
        }else if (splits[0].equalsIgnoreCase("Restart Level"))
        {
            //Perform a soft partial reset of the current Level.
            main.Global.SetupState = LevelSetupState.Restart;
        }
        else if (splits[0].equals("Swap Script"))
        {
            //Change the script into a new one provided by this script.
            //New script starts at the beginning and runs as usual.
            PerformCutScene(splits[1], CutSceneType.CustomEvent);
        }
        else if (splits[0].equals("Chat File"))
        {
            //Interrupt this scene to play a chat message, and then resume.
            OpenChatWindow(splits[1]);
        }
        else if (splits[0].equals("Enable Battery Timer"))
        {
            //Start the Timer to reduce the battery percentage
            main.Global.BatteryDecayTimer.Start();
        }
        else if (splits[0].equals("Disable Battery Timer"))
        {
            //Start the Timer to reduce the battery percentage
            main.Global.BatteryDecayTimer.Stop();
        }
        else if (splits[0].equals("Reduce Battery Timer"))
        {
            //Reduce the battery timer by this many milliseconds
            main.Global.BatteryDecayTimer.Tick(Integer.parseInt(splits[1]), false);
        }
        else if (splits[0].equals("Enable Sundown Timer"))
        {
            //Start the Timer to reduce the battery percentage
            main.Global.SundownTimer.Start();
        }
        else if (splits[0].equals("Disable Sundown Timer"))
        {
            //Start the Timer to reduce the battery percentage
            main.Global.SundownTimer.Stop();
        }
        else if (splits[0].equals("Reset Sundown Timer"))
        {
            //Start the Timer to reduce the battery percentage
            main.Global.SundownTimer.Reset();
        }
        else if (splits[0].equals("Enable Zombie Invasion"))
        {
            //Start the zombie spawning
            main.Global.ZombieInvasionEnabled = true;
        }
        else if (splits[0].equals("Disable Zombie Invasion"))
        {
            //Stop the zombie spawning
            main.Global.ZombieInvasionEnabled = false;
        }
        else if (splits[0].equals("If Switch Variable"))
        {
            //We are branching scripts.
            String variabletrue = splits[1];
            String variabletruecutscene = splits[2];
            String variablefalsecutscene = splits[3];
            
            if (main.Global.SwitchVariables.GetVariable(variabletrue)) {
                //Variable was true
                PerformCutScene(variabletruecutscene, CutSceneType.CustomEvent);
            } else {
                //Variable was false
                PerformCutScene(variablefalsecutscene, CutSceneType.CustomEvent);
            }
        }
        else if (splits[0].equals("Rest Until Dawn"))
        {
            //Subtract battery time, reset Sundown time, set level to 1 new, restart level
            main.Global.BatteryHealth -= main.Global.BatteryHealthSleepPenalty;
            main.Global.ZombieInvasionEnabled = false;
            main.Global.SundownTimer.Reset();
            main.Global.SundownTimer.Stop();
            main.Global.CurrentLevel = 1;
            main.Global.SetupState = LevelSetupState.New;
            SwapState(LevelStateRestart.ID);
        }
        else
        {//Unknown command
            System.out.println("MovieState Error: What is the meaning of: " + ScriptCurrentLine);
        }
    }
        
    private void RunScriptPlayerCharacter(String[] splits)
    {
        if (splits[1].equalsIgnoreCase("Animation"))
        {
            if (splits[2].equalsIgnoreCase("Lock"))
            {
                ThePlayer.AnimationLock(Boolean.valueOf(splits[3]));
            }
            else if (splits[2].equalsIgnoreCase("Stop"))
            {
                ThePlayer.isAnimating(false);
            }
            else if (splits[2].equalsIgnoreCase("Start"))
            {
                ThePlayer.isAnimating(true);
            }
            else
            {
                ThePlayer.SetAnimation(Integer.parseInt(splits[2]), Boolean.parseBoolean(splits[3]));
            }
        }
        else if (splits[1].equalsIgnoreCase("LocationX"))
        {
            ThePlayer.SetLocationX(Integer.parseInt(splits[2]));
        }
        else if (splits[1].equalsIgnoreCase("LocationY"))
        {
            ThePlayer.SetLocationY(Integer.parseInt(splits[2]));
        }
        else if (splits[1].equalsIgnoreCase("InertiaX"))
        {
            ThePlayer.SetInertiaX(Integer.parseInt(splits[2]));
        }
        else if (splits[1].equalsIgnoreCase("InertiaY"))
        {
            ThePlayer.SetInertiaY(Integer.parseInt(splits[2]));
        }
        else if (splits[1].equalsIgnoreCase("IsVisible"))
        {
            ThePlayer.IsVisible(Boolean.valueOf(splits[2]));
        }
        else if (splits[1].equalsIgnoreCase("Disable Gravity"))
        {
            ThePlayer.GravityEnabled(Boolean.valueOf(splits[2]));
        }
        else if (splits[1].equalsIgnoreCase("Enable Velocity"))
        {
            ThePlayer.VelocityEnabled(Boolean.valueOf(splits[2]));
        }
        else if (splits[1].equalsIgnoreCase("Tile Collision"))
        {
            ThePlayer.TileCollision(Boolean.valueOf(splits[2]));
        }
    }
    
    private void RunScriptEnemy(String[] splits)
    {
        if (splits[1].equalsIgnoreCase("Create By Player"))
        {
            Global.Directions Horizontal;
            Global.Directions Vertical = Global.Directions.None;
            //Create a new enemy in relation to the character's location in the level
            if (splits[3].equalsIgnoreCase("Left"))
            {
                Horizontal = Global.Directions.Left;
            }
            else
            {
                Horizontal = Global.Directions.Right;
            }

            CreateEnemy(Integer.valueOf(splits[2]), Integer.valueOf(splits[3]), Horizontal, Vertical, ThePlayer.GetLocationX() + Integer.valueOf(splits[6]), ThePlayer.GetLocationY() + Integer.valueOf(splits[7]), Boolean.valueOf(splits[8]), Boolean.valueOf(splits[9]), Boolean.valueOf(splits[10]), Integer.valueOf(splits[11]), Float.parseFloat(splits[12]), Float.parseFloat(splits[13]), splits[14]);
        }
        else if (splits[1].equalsIgnoreCase("Create Absolute"))
        {
            Global.Directions Horizontal;
            Global.Directions Vertical = Global.Directions.None;
            //Create a new enemy in relation to the character's location in the level
            if (splits[4].equalsIgnoreCase("Left"))
            {
                Horizontal = Global.Directions.Left;
            }
            else
            {
                Horizontal = Global.Directions.Right;
            }
            if (splits[5].equalsIgnoreCase("Up"))
            {
                Vertical = Global.Directions.Up;
            } else if (splits[5].equalsIgnoreCase("Down"))
            {
                Vertical = Global.Directions.Down;
            }

            CreateEnemy(Integer.valueOf(splits[2]), Integer.valueOf(splits[3]), Horizontal, Vertical, Integer.valueOf(splits[6]), Integer.valueOf(splits[7]), Boolean.valueOf(splits[8]), Boolean.valueOf(splits[9]), Boolean.valueOf(splits[10]), Integer.valueOf(splits[11]), Float.parseFloat(splits[12]), Float.parseFloat(splits[13]), splits[14]);
        }
        else if (splits[1].equalsIgnoreCase("Clear"))
        {
            if (splits[1].equalsIgnoreCase("All"))
            {//Remove ALL enemies, no matter what
                ClearAllEnemies();
            }
        }
    }
    
    private void RunScriptObject(String[] splits) {
        if (splits[1].equalsIgnoreCase("Add Object"))
        {
            if (splits[2].equalsIgnoreCase("By Player"))
            {
                temphorz = Directions.None;
                tempvert = Directions.None;
                if (splits[5].equalsIgnoreCase("Left"))
                {
                    temphorz = Directions.Left;
                }
                else if (splits[5].equalsIgnoreCase("Right"))
                {
                    temphorz = Directions.Right;
                }
                if (splits[6].equalsIgnoreCase("Up"))
                {
                    tempvert = Directions.Up;
                }
                else if (splits[6].equalsIgnoreCase("Down"))
                {
                    tempvert = Directions.Down;
                }
                CreateObject(Integer.valueOf(splits[3]), Integer.valueOf(splits[4]), temphorz, tempvert, ThePlayer.GetLocationX() + Integer.valueOf(splits[7]), ThePlayer.GetLocationY() + Integer.valueOf(splits[8]), false, false, -1, Integer.valueOf(splits[9]), Integer.valueOf(splits[10]), "Null");
            } 
            else if (splits[2].equalsIgnoreCase("Absolute"))
            {
                temphorz = Directions.None;
                tempvert = Directions.None;
                if (splits[4].equalsIgnoreCase("Left"))
                {
                    temphorz = Directions.Left;
                }
                else if (splits[4].equalsIgnoreCase("Right"))
                {
                    temphorz = Directions.Right;
                }
                if (splits[5].equalsIgnoreCase("Up"))
                {
                    tempvert = Directions.Up;
                }
                else if (splits[5].equalsIgnoreCase("Down"))
                {
                    tempvert = Directions.Down;
                }
                CreateObject(Integer.valueOf(splits[3]), Integer.valueOf(splits[4]), temphorz, tempvert, Integer.valueOf(splits[7]), Integer.valueOf(splits[8]), false, false, -1, Integer.valueOf(splits[9]), Integer.valueOf(splits[10]), "Null");
            } else if (splits[2].equalsIgnoreCase("In Room"))
            {
                temphorz = Directions.None;
                tempvert = Directions.None;
                if (splits[4].equalsIgnoreCase("Left"))
                {
                    temphorz = Directions.Left;
                }
                else if (splits[4].equalsIgnoreCase("Right"))
                {
                    temphorz = Directions.Right;
                }
                if (splits[5].equalsIgnoreCase("Up"))
                {
                    tempvert = Directions.Up;
                }
                else if (splits[5].equalsIgnoreCase("Down"))
                {
                    tempvert = Directions.Down;
                }
                CreateObject(Integer.valueOf(splits[3]), Integer.valueOf(splits[4]), temphorz, tempvert, (Level.getCurrentRoomLeft() * main.Global.TileWidth) + Integer.valueOf(splits[7]), (Level.getCurrentRoomTop() * main.Global.TileHeight) + Integer.valueOf(splits[8]), false, false, -1, Integer.valueOf(splits[9]), Integer.valueOf(splits[10]), "Null");

            }


        } 
        else if (splits[1].equalsIgnoreCase("Clear"))
        {
            if (splits[2].equalsIgnoreCase("All"))
            {//Clear ALL objects, even important ones
                ClearAllObjects();
            }else if (splits[2].equalsIgnoreCase("Normal"))
            {//Clear all normal objects, not important ones
                ClearNormalObjects();
            }
        }
    }
    
    private void RunScriptBullet(String[] splits) {
        
        if (splits[1].equalsIgnoreCase("Clear"))
        {
            if (splits[2].equalsIgnoreCase("All"))
            {//Clear ALL bullets, even important ones
                ClearAllBullets();
            } else if (splits[2].equalsIgnoreCase("Normal"))
            {//Clear Only normal bullets
                ClearNormalBullets();
            } else if (splits[2].equalsIgnoreCase("Player"))
            {
                ClearPlayerBullets();
            }
        }
    }
    
    /**
     * A sub function of RunScript, this is a part of the possible options a
     * script might want to perform for Text.
     * All of these conditionals assume the script begins with Text~
     * @param subsplits 
     */
    private void RunTextScript(String[] subsplits)
    {
        int Location = Integer.valueOf(subsplits[1]);
        if (subsplits[2].equalsIgnoreCase("Message"))
        {
            Text.get(Location).setText(subsplits[3]);
            Text.get(Location).centerText();
        }
        else if (subsplits[2].equalsIgnoreCase("Fade In"))
        {
            if (subsplits.length == 3)
            {
                Text.get(Location).fadeIn();
            }
            else if (subsplits.length == 4)
            {
                Text.get(Location).fadeIn(Integer.valueOf(subsplits[3]));
            }
        }
        else if (subsplits[2].equalsIgnoreCase("Fade Out"))
        {
            if (subsplits.length == 3)
            {
                Text.get(Location).fadeOut();
            }
            else if (subsplits.length == 4)
            {
                Text.get(Location).fadeOut(Integer.valueOf(subsplits[3]));
            }
        }
        else if (subsplits[2].equalsIgnoreCase("Vanish"))
        {
            Text.get(Location).Vanish();
        }
        else if (subsplits[2].equalsIgnoreCase("Appear"))
        {
            Text.get(Location).Appear();
        }
        else if (subsplits[2].equalsIgnoreCase("Center"))
        {
            Text.get(Location).centerText();
        }
    }
    
    private void DisplayMenu()
    {
        //Show the Super menu that allows the player to force quit to main menu
        ShowMenu = true;
    }
        
    public static int[] getCurrentRoomBoundaries()
    {
        return Level.getCurrentRoomBoundaries();
    }
    
    public static int getCurrentRoomBoundaryTop()
    {
        return Level.getCurrentRoomTop();
    }
    
    public static int getCurrentRoomBoundaryBottom()
    {
        return Level.getCurrentRoomBottom();
    }
    
    public static int getCurrentRoomBoundaryLeft()
    {
        return Level.getCurrentRoomLeft();
    }
    
    public static int getCurrentRoomBoundaryRight()
    {
        return Level.getCurrentRoomRight();
    }
    
    private void SwapState(int ID) {
        if (game.getState(ID) != null) {
            game.enterState(ID, FadeOut, FadeIn);
        }
    }
        
    public void setPreRenderLoad(boolean value)
    {
        PreRenderLoad = value;
    }
    
    /**
     * Opens a Chat Window on the screen using the provided file name.
     * This will set the CutScene to type ChatWindow, and PAUSE any CutScene
     * of CustomEvent. It will not erase the current scene.
     * Once the ChatWindow closes, if there was a CutScene not finished, it will
     * resume playing.
     * This means that a CustomEvent can trigger a ChatWindow, and then once
     * the player is done reading it, can move on to the rest of the custom event.
     * @param CutSceneScriptFileName 
     */
    public static void OpenChatWindow(String CutSceneScriptFileName) {
        
        ChatWindow.StartChat(CutSceneScriptFileName);
        
        if (CutScene == CutSceneType.CustomEvent) {
            ResumeCustomEventCutSceneOnChatWindowEnd = true;
        }
        
        CutScene = CutSceneType.ChatWindow;
    }
    
    /**
     * Do a one time check on the current keyboard state to see what keys 
     * are held down for this frame.
     * This is how to determine if a key is held down for multiple frames.
     * You must call this function at the start of every update frame in order
     * to properly track keyboard state!
     * 
     * This is useful for when entering a state, so you can tell if the user
     * was holding a key while the transition occurred (so you can ignore that button).
     * 
     * Example: Player hits pause, you go to pause state. First frame in pause 
     * state detects the player holding the pause key, and instantly jumps out
     * of the state. This function would tell you that the button was already held
     * when you go into the new state, so you can ignore that input.
     */
    private void PollCurrentKeys()
    {
        //Polling detects the state of the keys and controllers.
        //It does mouse click and location too! That's why it needs window size.
        Input.poll(main.WindowWidth, main.WindowHeight);
        
        if (!main.Global.Keyboard.IgnorePlayer1Input) {
            if (Input.isKeyDown(Keyboard.Player1Up)) {
                //Do something related to this key being held down for a frame
                //Need to denote the difference between pressed and held.
                //So, menu options would be stupid here, but player movement is great.
                //Check if the player move button is down for this function. If so, 
                Keyboard.Player1UpPressed += 1;
            } else {
                //Do something related to this key not beind held down for a frame,
                //like resetting the frame count for how long it was held.
                Keyboard.Player1UpPressed = 0;
            }

            if (Input.isKeyDown(Keyboard.Player1Down)) {
                Keyboard.Player1DownPressed += 1;
            } else {
                Keyboard.Player1DownPressed = 0;
            }

            if (Input.isKeyDown(Keyboard.Player1Left)) {
                Keyboard.Player1LeftPressed += 1;
            } else {
                Keyboard.Player1LeftPressed = 0;
            }

            if (Input.isKeyDown(Keyboard.Player1Right)) {
                Keyboard.Player1RightPressed += 1;
            } else {
                Keyboard.Player1RightPressed = 0;
            }
            
            if (Input.isKeyDown(Keyboard.Player1Shoot)) {
                Keyboard.Player1ShootPressed += 1;
            } else {
                Keyboard.Player1ShootPressed = 0;
            }
            
            if (Input.isKeyDown(Keyboard.Player1Pause)) {
                Keyboard.Player1PausePressed += 1;
            } else {
                Keyboard.Player1PausePressed = 0;
            }
        }
    }
    
    /**
     * Whenever a key is pressed down, this will trigger once.
     * It only triggers once, even if the player holds the button.
     * There is no trigger for when the key is released.
     * 
     * KeyPressed is good for navigation menu options that don't need to care
     * about key pressed being repeated by holding down.
     * @param key
     * @param c 
     */
    @Override
    public void keyPressed(int key, char c) 
    {
        
        if (ShowMenu)
        {
            if (!main.Global.Keyboard.IgnorePlayer1Input) {
                if (key == Keyboard.Player1Up) {
                    MenuOptions.MoveSelector(Directions.Up);
                } else if (key == Keyboard.Player1Down) {
                    MenuOptions.MoveSelector(Directions.Down);
                } else if (key == Keyboard.Player1Pause)
                {
                    //Activated an option
                    String activation = MenuOptions.SelectedKeyString();
                    if (activation.equals("Main Menu"))
                    {
                        //Close out of the current game and go straight to main menu
                        main.Global.SetupState = LevelSetupState.New;
                        ShowMenu = false;
                        SwapState(MenuStateMain.ID);
                    } else if (activation.equals("Resume"))
                    {
                        //Close the menu and resume as normal
                        ShowMenu = false;
                    }

                } else if (key == Keyboard.Player1Menu)
                {
                    ShowMenu = false;
                }
            }
        } else {
            if (!main.Global.Keyboard.IgnorePlayer1Input) {
                if (key == Keyboard.Player1Pause)
                {
                    //Delete any entities that should go away when you pause
                    DeleteEntitiesOnPause();
                    main.Global.Sound.PlaySound(14);
                    //SwapState(8);
                } else if (key == Keyboard.Player1Menu)
                {
                    ShowMenu = true;
                }
            }
        }
    }
    
    /**
     * Whenever a key is released, this will trigger once.
     * It does not trigger on key down or hold, only when you let go of the button.
     * Useful for when holding a key down for a time period needs to be tracked.
     * @param key
     * @param c 
     */
    @Override
    public void keyReleased(int key, char c)
    {
        
    }
    
    /**
     * Similar to KeyPressed, this tracks input on game pad controllers.
     * It will call the keyPressed function with the correctly mapped input.
     * Since controllers don't have keyboards, they can't send a character value.
     * This means it will always send a default value for that, so don't expect
     * all user input types to be capable of the same things.
     * @param controller Int value for the unique controller (who did it)
     * @param button Int value to represent the button pressed (what they did)
     */
    @Override
    public void controllerButtonPressed(int controller, int button)
    {
        //Since many different controllers use this function, determine
        //which player is calling.
        if (controller == Keyboard.Player1ControllerNumber) 
        {
            if (button == Keyboard.Player1CUp) {
                keyPressed(Keyboard.Player1Up, '^');
            } else if (button == Keyboard.Player1CDown) {
                keyPressed(Keyboard.Player1Down, '^');
            } else if (button == Keyboard.Player1CLeft) {
                keyPressed(Keyboard.Player1Left, '^');
            } else if (button == Keyboard.Player1CRight) {
                keyPressed(Keyboard.Player1Right, '^');
            } else if (button == Keyboard.Player1CJump) {
                keyPressed(Keyboard.Player1Jump, '^');
            } else if (button == Keyboard.Player1CShoot) {
                keyPressed(Keyboard.Player1Shoot, '^');
            } else if (button == Keyboard.Player1CPause) {
                keyPressed(Keyboard.Player1Pause, '^');
            } else if (button == Keyboard.Player1CMenu) {
                keyPressed(Keyboard.Player1Menu, '^');
            }
        } else if (controller == Keyboard.Player2ControllerNumber) {
            if (button == Keyboard.Player2CUp) {
                keyPressed(Keyboard.Player2Up, '^');
            } else if (button == Keyboard.Player2CDown) {
                keyPressed(Keyboard.Player2Down, '^');
            } else if (button == Keyboard.Player2CLeft) {
                keyPressed(Keyboard.Player2Left, '^');
            } else if (button == Keyboard.Player2CRight) {
                keyPressed(Keyboard.Player2Right, '^');
            } else if (button == Keyboard.Player2CJump) {
                keyPressed(Keyboard.Player2Jump, '^');
            } else if (button == Keyboard.Player2CShoot) {
                keyPressed(Keyboard.Player2Shoot, '^');
            } else if (button == Keyboard.Player2CPause) {
                keyPressed(Keyboard.Player2Pause, '^');
            } else if (button == Keyboard.Player2CMenu) {
                keyPressed(Keyboard.Player2Menu, '^');
            }
        }
    }
    
    /**
     * Similar to KeyReleased, this detects when a game pad controller button
     * is released.
     * This will map back to the keyReleased function, but it will not have
     * a character code to return since controllers don't have keyboards.
     * @param controller
     * @param button 
     */
    @Override
    public void controllerButtonReleased(int controller, int button)
    {
        //Since many different controllers use this function, determine
        //which player is calling.
        if (controller == Keyboard.Player1ControllerNumber) 
        {
            if (button == Keyboard.Player1CUp) {
                keyReleased(Keyboard.Player1Up, '^');
            } else if (button == Keyboard.Player1CDown) {
                keyReleased(Keyboard.Player1Down, '^');
            } else if (button == Keyboard.Player1CLeft) {
                keyReleased(Keyboard.Player1Left, '^');
            } else if (button == Keyboard.Player1CRight) {
                keyReleased(Keyboard.Player1Right, '^');
            } else if (button == Keyboard.Player1CJump) {
                keyReleased(Keyboard.Player1Jump, '^');
            } else if (button == Keyboard.Player1CShoot) {
                keyReleased(Keyboard.Player1Shoot, '^');
            } else if (button == Keyboard.Player1CPause) {
                keyReleased(Keyboard.Player1Pause, '^');
            } else if (button == Keyboard.Player1CMenu) {
                keyReleased(Keyboard.Player1Menu, '^');
            }
        } else if (controller == Keyboard.Player2ControllerNumber) {
            if (button == Keyboard.Player2CUp) {
                keyReleased(Keyboard.Player2Up, '^');
            } else if (button == Keyboard.Player2CDown) {
                keyReleased(Keyboard.Player2Down, '^');
            } else if (button == Keyboard.Player2CLeft) {
                keyReleased(Keyboard.Player2Left, '^');
            } else if (button == Keyboard.Player2CRight) {
                keyReleased(Keyboard.Player2Right, '^');
            } else if (button == Keyboard.Player2CJump) {
                keyReleased(Keyboard.Player2Jump, '^');
            } else if (button == Keyboard.Player2CShoot) {
                keyReleased(Keyboard.Player2Shoot, '^');
            } else if (button == Keyboard.Player2CPause) {
                keyReleased(Keyboard.Player2Pause, '^');
            } else if (button == Keyboard.Player2CMenu) {
                keyReleased(Keyboard.Player2Menu, '^');
            }
        }
    }
}
