package MegaEngine;

import java.util.HashMap;
import org.newdawn.slick.SlickException;

public class MusicBoard {
    private MusicObject[] Music;
    private int CurrentlyPlaying = -1; //The song which is playing right now.
    private boolean AllowMusic = true; //Allow or disallow the use of music entirely.
    private float Volume = 1f;//Percentage of volume to play
    public float Pitch = 1f;//How it sounds. Required to play at specific volumes.
    private HashMap MusicStore = new HashMap(100);
    private int Blank = 14;//Location of a blank sound file. Used to instantly stop music from playing, instead of finishing it's chunk.
    
    private boolean AllowFollowUpSong = true;
    private boolean LoopFollowUpSong = false;
    
    public float MusicVolume = 28;//Sound levels of the game.  28 is just used in percentage at the end
    private float MusicVolumeMax = 28;//Highest the bar can go. Used for making the percentage of volume
    
    public MusicBoard()
    {
        LoadMusic();
        
    }//End Constructor
    
    private void LoadMusic()
    {
        Music = new MusicObject[4];
        CurrentlyPlaying = -1; //No song is played.
        
        try
        {//Load up the music into the array so any of them can be played instantly.
            //Set any ogg file to true to allow streaming play.
            //Downside: if true, song will not immediately .stop() It must finish it's current chunk in memory, then stop.
            //Wav files only work for very small file sizes. Anything under 5kb will not work as an ogg file.
            Music[0] = new MusicObject("Resource/Music/Blank.wav");
            Music[1] = new MusicObject("Resource/Music/Troy Grant - The Hospital.ogg", true);
            Music[2] = new MusicObject("Resource/Music/Troy Grant - Overrun.ogg", true);
            Music[3] = new MusicObject("Resource/Music/Troy Grant - Survival.ogg", true);
        }
        catch (SlickException e)
        {
            System.err.println("Music File not found: " + e.getMessage());
        }
    }
    /*
     * Play a specific song based on array number.
     * If music is not allowed to play, then no effect will be taken.
     */
    public void PlayMusic(int value)
    {
       // System.out.println("Call to PlayMusic");
        if (AllowMusic && value > -1 && value < Music.length)
        {
                if (value == CurrentlyPlaying && Music[CurrentlyPlaying].playing())
                {//New music is same as old, so continue playing
                    //Do nothing
                 //   System.out.println("Song is already playing");
                }
                else
                {
                  //  System.out.println("Now playing: " + value);
                    Music[value].play(Pitch, Volume);
                    CurrentlyPlaying = value;
                }
                AllowFollowUpSong = true;
                LoopFollowUpSong = false;
        } else
        {
            System.out.println("failed: " + AllowMusic + ", " + value);
        }
        
    }
    
    /*
     * Stop the currently playing song.
     */
    public void StopMusic()
    {
    //    System.out.println("Call to StopMusic");
        if (CurrentlyPlaying > -1 && CurrentlyPlaying < Music.length)
        {
          //  System.out.println("Stopping Current Music: " + CurrentlyPlaying);
            Music[Blank].play(Pitch, Volume);//Play a blank file to immediately stop any streaming music.
            Music[Blank].stop();//If this isn't here, then the stop actually works after the current chunk ends.
            Music[CurrentlyPlaying].stop();
            CurrentlyPlaying = -1;
            AllowFollowUpSong = false;
        }
    }
    
    /*
     * Play a specified song infinitely, until told to stop.
     */
    public void LoopMusic(int value)
    {
      //  System.out.println("AllowMusic: " + AllowMusic + ", Song: " + value);
        if (AllowMusic && value > -1 && value < Music.length)
        {
                if (value == CurrentlyPlaying && Music[CurrentlyPlaying].playing())
                {//New music is same as old, so continue playing
                    //Do nothing
                 //   System.out.println("Same song already playing. No Action");
                }
                else
                {
                  //  System.out.println("Now looping: " + value);
                    Music[value].loop(Pitch, Volume);
                    CurrentlyPlaying = value;
                }
                AllowFollowUpSong = true;
                LoopFollowUpSong = true;
        }
        
    }
    
    /*
     * Stop any music from playing in the entire program.
     */
    public void DisableMusic()
    {
        AllowMusic = false;
    }
    
    /*
     * Allow music to be played in the program.
     */
    public void EnableMusic()
    {
        AllowMusic = true;
    }
    
    public boolean GetAllowMusic()
    {
        return AllowMusic;
    }
    
    public void SetVolume(float value)
    {
        MusicVolume = value;
        
        if (MusicVolume > MusicVolumeMax) {
            MusicVolume = MusicVolumeMax;
        }
        
        if (MusicVolume < 0) {
            MusicVolume = 0;
        }
        
        Volume = MusicVolume / MusicVolumeMax;
        setCurrentMusicVolume(Volume);
    }
    
    public void setCurrentMusicVolume(float value)
    {
        if (CurrentlyPlaying > -1 && CurrentlyPlaying < Music.length) {
            Music[CurrentlyPlaying].setVolume(value);
        }
    }
    
    public void StoreCurrentMusic(String title)
    {
        MusicStore.put(title, CurrentlyPlaying);
    }
    
    public void LoadMusic(String key, boolean loop)
    {
      //  System.out.println("Loading Music by key: " + key);
        int value = Integer.parseInt(MusicStore.get(key).toString());
        if (loop)
        {
            LoopMusic(value);
        } else
        {
            PlayMusic(value);
        }
    }
    
    public void FadeOut(int duration)
    {
        Music[CurrentlyPlaying].fade(duration, 0, true);
     //   System.out.println("Fading Out " + CurrentlyPlaying + " over " + duration + " milliseconds");
    }
    
    public void FadeIn(int duration)
    {
        Music[CurrentlyPlaying].fade(duration, 1, true);
    }
    
    public void IncreasePitch(float value)
    {
        Pitch += value;
    }
    
    public void DecreasePitch(float value)
    {
        Pitch -= value;
        
    }
    
    public boolean isPlaying()
    {
        if (CurrentlyPlaying > -1 && CurrentlyPlaying < Music.length)
        {
            return Music[CurrentlyPlaying].playing();
        }
        return false;
    }
    
    public MusicObject getCurrentMusic()
    {
        if (CurrentlyPlaying > -1 && CurrentlyPlaying < Music.length)
        {
            return Music[CurrentlyPlaying];
        }
        return null;
    }
    
    public boolean AllowFollowUpSong()
    {
        return AllowFollowUpSong;
    }
    
    public boolean LoopFollowUpSong()
    {
        return LoopFollowUpSong;
    }
}