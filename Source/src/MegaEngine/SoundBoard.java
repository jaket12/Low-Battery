package MegaEngine;

import org.newdawn.slick.*;

public class SoundBoard {
    
    private Sound[] Sounds;
    private boolean AllowSound = true; //Allow or disallow the use of sound entirely.
    private float Volume = 1f;
    private float Pitch = 1f;
    
    public float SoundVolume = 28;
    private float SoundVolumeMax = 28;
    
    public SoundBoard()
    {
        LoadSounds();
    }//End Constructor
    
    private void LoadSounds()
    {
        Sounds = new Sound[6];
        
        try
        {
            Sounds[0] = new Sound("Resource/Sound/Error.ogg");
            Sounds[1] = new Sound("Resource/Sound/SelectorMove.ogg");
            Sounds[2] = new Sound("Resource/Sound/Activate.ogg");
            Sounds[3] = new Sound("Resource/Sound/BulletShoot1.ogg");
            Sounds[4] = new Sound("Resource/Sound/Damage1.ogg");
            Sounds[5] = new Sound("Resource/Sound/Pause.ogg");            
        }
        catch (SlickException e)
        {
            System.out.println("Problem in sound board! " + e.getMessage());
        }
    }
    
    /*
     * Play a specific sound by array value.
     * This sound may be played multiple times concurrently.
     */
    public void PlaySound(int value)
    {
        if (AllowSound && value > -1 && value < Sounds.length)
        {
            Sounds[value].play(Pitch, Volume);
        }
    }
    
    /*
     * Stop a specific sound entirely.
     * All sounds of this number will stop if concurrent others exist.
     */
    public void StopSound(int value)
    {
        if (value > -1 && value < Sounds.length)
        Sounds[value].stop();
    }
    
    /*
     * Instantly stop all sounds currently being played.
     * If sound is enabled and another sound is played right after this
     * function is called, it will still play correctly.
     */
    public void StopAllSound()
    {
        int i;
        for (i = 0; i < Sounds.length; i++)
        {
            Sounds[i].stop();
        }
    }
    
    /*
     * Play a sound infinitely until told to stop.
     */
    public void LoopSound(int value)
    {
        if (AllowSound && value > -1 && value < Sounds.length)
        {
            Sounds[value].loop();
        }
    }
    
    /*
     * Stop any sound from playing in the entire program.
     */
    public void DisableSound()
    {
        AllowSound = false;
    }
    
    /*
     * Allow sound to be played in the program.
     */
    public void EnableSound()
    {
        AllowSound = true;
    }
    
    public boolean GetAllowSound()
    {
        return AllowSound;
    }
    
    public void SetVolume(float value)
    {
        SoundVolume = value;
        
        if (SoundVolume > SoundVolumeMax) {
            SoundVolume = SoundVolumeMax;
        }
        
        if (SoundVolume < 0) {
            SoundVolume = 0;
        }
        
        Volume = SoundVolume / SoundVolumeMax;
    }

}
