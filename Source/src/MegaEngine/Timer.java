package MegaEngine;

public class Timer {
    
    String Name = "Null";
    int Timer = -1;
    int TimerMax = -1;
    boolean Loop = true;
    boolean isRunning = false;
    
    /**
     * Create a timer with set options
     * 
     * Name - An identifier for this timer to separate it from others.
     * Duration - The interval time between ticks. 1000 = one second
     * StartTimer - If true, timer is already running when created.
     * Loop - If true, timer resets once it hits zero. If false, when
     * timer hits zero it will always return true on its update.
     * 
     * @param name
     * @param duration
     * @param starttimer
     * @param loop
     */
    public Timer (String name, int duration, boolean starttimer, boolean loop)
    {
        Name = name;
        Timer = duration;
        TimerMax = duration;
        Loop = loop;
        isRunning = starttimer;
        
    }
    
    public void Start()
    {
        isRunning = true;
    }
    
    public boolean Update (int delta)
    {
        if (isRunning)
        {
            Timer -= delta;
            if (Timer < 0)
            {
                if (Loop)
                {
                    Reset();
                } else
                {
                    Stop();
                }
                
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Force the timer to tick outside of it's normal update cycle.
     * 
     * If activate is false, the timer will not return true and reset
     * until the next update cycle.
     * @param delta
     * @param activate
     * @return 
     */
    public boolean Tick (int delta, boolean activate)
    {
        Timer -= delta;
        if (TimerMax < 0 && activate)
        {
            if (Loop)
            {
                Reset();
            } else
            {
                Stop();
            }
            return true;
        }
        
        return false;
    }
    
    public boolean isExpired()
    {
        if (Timer <= 0)
            return true;
        else
            return false;
    }
    
    public void Stop()
    {
        isRunning = false;
    }
    
    public void Reset()
    {
        Timer = TimerMax;
    }
    
    public String getName()
    {
        return Name;
    }
    
    public boolean isRunning()
    {
        return isRunning;
    }
    
    public void Loop(boolean value)
    {
        Loop = value;
    }
    
    /**
     * Change the time interval that this Timer will trigger.
     * If the new duration interval is lower than the current time remaining,
     * then the remaining will be set to the new duration.
     * @param duration 
     */
    public void SetDuration(int duration)
    {
        TimerMax = duration;
        if (Timer > TimerMax)
        {
            Timer = TimerMax;
        }
    }
    
    public int getDuration()
    {
        return TimerMax;
    }
    
    /**
     * Set the amount of milliseconds left before activation.
     * If the value is above the max duration, it will be to the limit.
     * @param value 
     */
    public void SetTimeRemaining(int value) {
        Timer = value;
        if (Timer > TimerMax)
        {
            Timer = TimerMax;
        }
    }
    
    public int GetTimeRemaining() {
        return Timer;
    }
}
