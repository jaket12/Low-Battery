package MegaEngine;

public class AnimationData 
{
    private int Layers = 0;//How many layers this frame has
    private int FrameRow[]; //Array location 0 is ALWAYS the default image.
    private int FrameColumn[];//All other locations are additional layers placed on this frame
    private int FrameDuration;
    private int FrameOffsetX[];
    private int FrameOffsetY[];
    private int[] Event = new int[5];//Frames may hold a value that is checked when it is drawn.
    private int EventCount = 0;//How many events are held
    //Current event iddeas:
    //0: no action
    //1: Default Attack
    //2: For met, when it goes back to the hiding state
    private int FrameNumber; //Tracks the order of sequence in an animation. Lets you know when a certain frame is hit.
    
    
    public AnimationData(int layers, int row, int column, int duration, int offsetx, int offsety)
    {
        Layers = layers;
        FrameRow = new int[Layers];
        FrameColumn = new int[Layers];
        FrameOffsetX = new int[Layers];
        FrameOffsetY = new int[Layers];
        FrameRow[0] = row;
        FrameColumn[0] = column;
        FrameDuration = duration;
        FrameOffsetX[0] = offsetx;
        FrameOffsetY[0] = offsety;
    }
    
    public void addEvent(int eventnumber)
    {
        Event[EventCount++] = eventnumber;
        if (EventCount > 4)
        {
            EventCount = 4;
        }
    }
    
    public void AddLayer(int layernumber, int row, int column, int offsetx, int offsety)
    {
        FrameRow[layernumber] = row;
        FrameColumn[layernumber] = column;
        FrameOffsetX[layernumber] = offsetx;
        FrameOffsetY[layernumber] = offsety;
    }
    
    public int getLayerRow(int i)
    {
        return FrameRow[i];
    }
    
    public int getLayerColumn(int i)
    {
        return FrameColumn[i];
    }
    
    public int getDuration()
    {
        return FrameDuration;
    }
    
    public int getLayerOffsetX(int i)
    {
        return FrameOffsetX[i];
    }
    
    public int getLayerOffsetY(int i)
    {
        return FrameOffsetY[i];
    }
    
    public int[] getFrameEvents()
    {
        return Event;
    }

    public int getEventCount()
    {
        return EventCount;
    }
    
    public int getEvent(int value)
    {
        return Event[value];
    }
    
    public void setFrameNumber(int value)
    {
        FrameNumber = value;
    }
    
    public int getFrameNumber()
    {
        return FrameNumber;
    }
}
