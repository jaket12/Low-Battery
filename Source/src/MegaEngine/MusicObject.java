/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MegaEngine;

import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;

/**
 *
 * @author Fast Food
 */
public class MusicObject {
    
    private Music Music;
    private int FollowUpSong = -1;
    
    public MusicObject(String ref) throws SlickException
    {
        try {
            Music = new Music(ref);
        } catch (SlickException ex) {
            System.err.println("Music File not found: " + ex.getMessage());
        }
    }
    
    public MusicObject(String ref, boolean stream)
    {
        try {
            Music = new Music(ref, stream);
        } catch (SlickException ex) {
            System.err.println("Music File not found: " + ex.getMessage());
        }
    }
    
    public void FollowUpSong(int slot)
    {
        FollowUpSong = slot;
    }
    
    public int FollowUpSong()
    {
        return FollowUpSong;
    }
    
    public boolean hasFollowUpSong()
    {
        if (FollowUpSong > -1)
        {
            return true;
        } else
        {
            return false;
        }
    }
    public boolean playing()
    {
        return Music.playing();
    }
    
    public void play(float pitch, float volume)
    {
        Music.play(pitch, volume);
    }
    
    public void loop(float pitch, float volume)
    {
        if (FollowUpSong > -1)
        {
            Music.play(pitch, volume);
        } else
        {
            Music.loop(pitch, volume);
        }
        
    }
    
    public void stop()
    {
        Music.stop();
    }
    
    public void setVolume(float volume)
    {
        Music.setVolume(volume);
    }
    
    public void fade(int duration, int gotovolume, boolean stopafterfade)
    {
        Music.fade(duration, gotovolume, stopafterfade);
    }
}
