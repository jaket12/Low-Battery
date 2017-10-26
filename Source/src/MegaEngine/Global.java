package MegaEngine;

import org.newdawn.slick.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class Global {
    //This class holds all universal variables which all states would be
    //interested in obtaining.  Pretty much all variables here are public
    //and shared.

    public Random Random = new Random();
    public final int TileWidth = 32;
    public final int TileHeight = 32;
    public final int TileAttributesMax = 5;//The most special statuses a tile may have in a Level.
    
    public enum Directions {
        Right, Left, Up, Down, None
    } //All possible ways to face
    
    //Each type of cutscene that can occur in the Level State. Only one runs at a time.
    public enum CutSceneType {
        RoomTransition, CustomEvent, None, ChatWindow
    }
    
    //The state of the Level in terms of what it needs to do on Enter()
    //and the first time render().
    public enum LevelSetupState {
        New, //If true, Initialize() the LevelState when it is entered.
        Restart, //If true, the LevelState will not fully reset, but continue from a death (checkpoint/items stay)
        Running, //Keep on trucking
    }
    
    private String MovieScriptLocation = "/Resource/Movie/Intro.scr";
    //The movie which will be played when the moviestate is entered.
    //If the filepath is the same as the last movie played, then the movie
    //will be continued and not restarted.
    private String GameSaveDirectory = "";

    //Current Game Variables
    //The true value of health for the player. This transfers between level states.
    public float PlayerHealth;
    public float PlayerHealthMax = 100;
    //Secondary health value for player. This transfers between level states.
    public float BatteryHealth;
    public float BatteryHealthMax = 100;
    //How much the game starts out with for this value.
    public float BatteryHealthStarting = 100;
    //Each trigger will reduce the battery by 1.
    public Timer BatteryDecayTimer;
    public int BatteryDecayTimerDefault = 3000;
    //How much battery is reduced per sleep.
    public float BatteryHealthSleepPenalty = 20;
    //The time left in the game before the zombies appear. Time is in milliseconds.
    public Timer SundownTimer;
    //When a level starts, it gets this much time.
    public int SundownTimerDefault = 60000;
    public boolean ZombieInvasionEnabled = false;
    //Tracks the true/false value of any number of custom created keys.
    public SwitchVariableManager SwitchVariables = new SwitchVariableManager();
   
   //Current game values
   public int CurrentLevel = -1;
   public LevelSetupState SetupState = LevelSetupState.New;
   //One time use spawn overrides for levels. If Use is true, then the Player
   //Will spawn at this location instead of what the map says. Use will be false
   //after each use.
   public int CustomPlayerSpawnX;
   public int CustomPlayerSpawnY;
   //Music index for what to play at start.
   public int CustomPlayerSpawnMusic;
   //Script file to run on level load. Replaces the LevelXIntro.scr normally used.
   public String CustomPlayerSpawnCutScene;
   public boolean UseCustomPlayerSpawn;
   
    /*
     * List of all sprite sheets to be used in the game.
     * All images are stored right here and are shared between all classes.
     */
    public int SheetInUse = -1;//The current sprite sheet to be rendered in use
    private boolean inUse = false;//If a sheet is currently in use for drawing
    public SpriteSheet CurrentSpriteSheet;//Current sheet in use for drawing, may not be inUse at times
    public SpriteSheet[] SpriteSheets;//Complete list of sheets to ever be used.
    private Color TransparentColor = new Color(0, 128, 0); //The color which is used to be invisible.  Default 0,128,0 green
    
    
    
    //All sound files are contained here, and can be accessed from any class.
    //Use Main.SoundBoard to operate the files by their array location.
    //You must refer to the SoundBoard class itself to know the number of the
    //sound you wish to play.
    public SoundBoard Sound = new SoundBoard();
    public MusicBoard Music = new MusicBoard();
    
        
    /*
     * Keyboard and controll buttons
     * By default, the keyboard is the player 1 controls and menu support
     * Player can change these values in options as a saved file
     * 
     * Each player has their own controls.
     * Each control is an array.  Location is equal to player number
     */
    KeyBoard Keyboard = new KeyBoard();
    //
    public boolean IgnorePlayerInput = false;
    
    //Debug settings
    
    //If true, the render() loop will include MetaTiles for Rooms to be seen.
    public boolean DrawRoomTiles = false;
    public boolean DrawAllHitboxes = false;
    
    public Global()
    {
        LoadSettings();
        SaveCurrentSettings();
        
        PlayerHealth = PlayerHealthMax;
        BatteryHealth = BatteryHealthStarting;
        BatteryDecayTimer = new Timer("how long a percent lasts", BatteryDecayTimerDefault, false, true);
        SundownTimer = new Timer("Impending zombie doom", SundownTimerDefault, false, false);
    }
    
    /**
     * Access the disk and load up the values in each save game file.
     * This is used to preview the save game itself, if the player wants
     * to load it and play.
     */
    public void LoadGameSaves()
    { 
        //Wow the original was bad. I'm going to improve this later.
        //Make it more similar to how controls are saved.
    }

    /**
     * Save the current game state to disk. The index of the save must be provided.
     * IE: if you want to overwrite save game 2 on file, the parameter would be 2.
     * @param slot 
     */
    public void SaveGame(int slot)
    {
        //Wow the original was bad. I'm going to improve this later.
        //Make it more similar to how controls are saved.
    }
    
    /**
     * Load a provided game save into the current settings.
     * Whatever that save file had will be inserted into the current game values.
     * @param GameFile Index of the save game to load
     */
    public  void LoadSaveIntoCurrentGame(int GameFile)
    {
        //Wow the original was bad. I'm going to improve this later.
        //Make it more similar to how controls are saved.
    }
     
    /**
     * Copy one save file into another slot.
     * @param source
     * @param destination 
     */
    public void CopySaveGame(int source, int destination)
    {
        //Wow the original was bad. I'm going to improve this later.
        //Make it more similar to how controls are saved.
    }
    
    /**
     * Delete the save file from the disk.
     * @param value 
     */
    public void DeleteSaveGame(int value)
    {
        try {
            File file = new File(GameSaveDirectory + "Game" + value + ".sav");

            if (file.delete()) {
             //   System.out.println(file.getName() + " is deleted!");
            } else {
                System.out.println("Delete operation is failed.");
            }
        } catch (Exception e) {
            System.out.println("Error deleting game #" + value + ": " + e);
        }
    }
     
    public void SetCurrentLevel(int LevelNumber)
    {
        CurrentLevel = LevelNumber;
    }
    
    public int SheetInUse()
    {
        return SheetInUse;
    }
    
    public boolean inUse()
    {
        return inUse;
    }
    
    public void endUse()
    {
        if (inUse)
        {
            CurrentSpriteSheet.endUse();
            inUse = false;
        }
    }
    
    public void SheetInUse(int value)
    {
        SheetInUse = value;
        CurrentSpriteSheet = SpriteSheets[SheetInUse];
    }
    
    public void startUse()
    {
        if (!inUse)
        {
            CurrentSpriteSheet.startUse();
            inUse = true;
        }
    }
    
    /**
     * Access the disk and load up all of the sprites that will be used in the game.
     * The expected location for all sprites should be Resource/Image/Sprite, but
     * can be found elsewhere if required.
     * The sprites are loaded into an array of static size, so when adding/removing
     * sheets from the array be sure to update this number!
     */
    public void LoadSpriteSheets()
    {
        try 
        {
            //File location, Individual sprite Width, Individual sprite Height, Color to denote transparency, Pixel spacing between images (visible red line sprite boxes)
            SpriteSheets = new SpriteSheet[5];
            SpriteSheets[0] = new SpriteSheet("Resource/Image/Sprite/SheetPlayer1.png", 32, 32, TransparentColor, 1);
            SpriteSheets[1] = new SpriteSheet("Resource/Image/Sprite/SheetBullet1.png", 16, 16, TransparentColor, 1);
            SpriteSheets[2] = new SpriteSheet("Resource/Image/Sprite/SheetItem1.png", 32, 32, TransparentColor, 1);
            SpriteSheets[3] = new SpriteSheet("Resource/Image/Sprite/SheetEnemy1.png", 48, 48, TransparentColor, 1);
            SpriteSheets[4] = new SpriteSheet("Resource/Image/Sprite/SheetObject1.png", 32, 32, TransparentColor, 1);
            
        } catch (SlickException ex) {
            System.err.println("Error loading sprites: " + ex.getMessage());
        }
    }
    
    public String GetMovieScriptLocation()
    {
        return MovieScriptLocation;
    }
    
    public void SetMovieScriptLocation(String value)
    {
        MovieScriptLocation = value;
    }
    
    public KeyBoard getKeyboard()
    {
        return Keyboard;
    }
    
    /**
     * Access the Settings.txt file and parse out what default values the
     * game should use.
     * Each line of text in the file is a different variable.
     * The variable name is first, then split with ~ for the value it holds.
     * Variable~Value
     * If no file is found, then default values will be used for the game.
     */
    private void LoadSettings()
    {
        try
        {
            FileInputStream fstream = new FileInputStream("Settings.txt");
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            String[] splits;
            while ((strLine = br.readLine()) != null)   
            {
                splits = strLine.split("~");
                if (splits[0].startsWith("//"))
                {
                    //Comment, do nothing
                } else if (splits[0].equals("Music"))
                {
                    Music.SetVolume(Float.valueOf(splits[1]));
                } else if (splits[0].equals("Sound"))
                {
                    Sound.SetVolume(Float.valueOf(splits[1]));
                } else if (splits[0].equals("FullScreen"))
                {
                    main.FullScreen = Boolean.valueOf(splits[1]);
                } else if (splits[0].equals("VSync"))
                {
                    main.VSync = Boolean.valueOf(splits[1]);
                } else if (splits[0].equals("ShowFPS"))
                {
                    main.ShowFPS = Boolean.valueOf(splits[1]);
                } else if (splits[0].equals("DrawRoomTiles"))
                {
                    DrawRoomTiles = Boolean.valueOf(splits[1]);
                } else if (splits[0].equals("DrawAllHitboxes"))
                {
                    DrawAllHitboxes = Boolean.valueOf(splits[1]);
                }
            }
            in.close();
        }catch (IOException e){
            System.err.println("Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
    
    /**
     * Save the current game settings to disk for later use.
     * If no file is found, it will create the Settings.txt file.
     */
    public void SaveCurrentSettings()
    {
        try
        {
            FileWriter fstream = new FileWriter("Settings.txt");
            BufferedWriter out = new BufferedWriter(fstream);
            out.write("Music~" + Music.MusicVolume);
            out.newLine();
            out.write("Sound~" + Sound.SoundVolume);
            out.newLine();
            out.write("FullScreen~" + main.FullScreen);
            out.newLine();
            out.write("VSync~" + main.VSync);
            out.newLine();
            out.write("ShowFPS~" + main.ShowFPS);
            out.newLine();
            out.write("DrawRoomTiles~" + DrawRoomTiles);
            out.newLine();
            out.write("DrawAllHitboxes~" + DrawAllHitboxes);
            out.newLine();
            
            out.close();
        }catch (IOException e){
            System.err.println("Error: " + e.getMessage());
        }
    }
        
    /**
     * Update values in Global that apply through any screen state.
     * Currently only used for when music ends, it needs to play the next song in a list.
     * @param delta 
     */
    public void Update(int delta)
    {
        if (!Music.isPlaying())
        {
            if (Music.AllowFollowUpSong() && (Music.getCurrentMusic() != null && Music.getCurrentMusic().hasFollowUpSong()))
            {
                if (Music.LoopFollowUpSong())
                {
                    Music.LoopMusic(Music.getCurrentMusic().FollowUpSong());
                } else
                {
                    Music.PlayMusic(Music.getCurrentMusic().FollowUpSong());
                }
            }
        }
        
        if (BatteryDecayTimer.Update(delta)) {
            BatteryHealth -= 1;
            if (BatteryHealth < 0) {
                BatteryHealth = 0;
            }
        }
        
        if (SundownTimer.Update(delta)) {
            //Begin the invasion!
            SundownTimer.Stop();
        }
     
    }
    
    /**
     * Reset the state so that it's a completely new game.
     */
    public void FullReset() {
        PlayerHealth = PlayerHealthMax;
        BatteryHealth = BatteryHealthStarting;
        BatteryDecayTimer = new Timer("how long a percent lasts", BatteryDecayTimerDefault, false, true);
        SundownTimer = new Timer("Impending zombie doom", SundownTimerDefault, false, false);
        ZombieInvasionEnabled = false;
        CurrentLevel = -1;
        SetupState = LevelSetupState.New;
        SwitchVariables = new SwitchVariableManager();
    }
}
