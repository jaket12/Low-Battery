package MegaEngine;

public class AnimatedTile 
{
    private Timer Speed;
    private int[] TileID;
    private int[][] Attributes;
    private int CurrentTile;
    private int TotalTiles;
    private boolean isAnimating;
    private boolean AnimateOnTouch;
    private String Sequence;
    private boolean EndOfSequence;
    
    private String splits2[];
    
    public AnimatedTile(int speed, String sequence)
    {
        CurrentTile = 0;
        Speed = new Timer("Interval", speed, true, true);
        String splits[] = sequence.split("`");
        Sequence = sequence;
        EndOfSequence = false;
     //   45,0`76,0`77,0`78,0`79,0`80,0`80,0`79,0`78,0`77,0`76,0`45,0
        TotalTiles = splits.length - 1;
        TileID = new int[splits.length];
        Attributes = new int[splits.length][5];
        isAnimating = true;
        AnimateOnTouch = false;
        
        
        for (int i = 0; i < splits.length; i++)
        {
            splits2 = splits[i].split(",");
            TileID[i] = Integer.parseInt(splits2[0]);
        //    System.out.print("Animated Tile " + TileID[i] + " has attributes: ");
            for (int j = 1; j < splits2.length; j++)
            {
                Attributes[i][j - 1] = Integer.parseInt(splits2[j]);
          //     System.out.print(Attributes[i][j] + ", ");
            }
         //   System.out.println("");
        }
    }
    
    public boolean Update(int delta)
    {
        if (isAnimating && Speed.Update(delta))
        {
         //   System.out.println("Tile updated, moving to next tile");
            NextAnimation();
            return true;
        }
        
      //  System.out.println("Tile not updated, isAnimating: " + isAnimating);
        
        return false;
    }
    
    private void NextAnimation()
    {
        if (CurrentTile < TotalTiles)
        {
            CurrentTile++;
        } else
        {
            CurrentTile = 0;
            if (AnimateOnTouch)
            {
                isAnimating = false;
                EndOfSequence = true;
            }
        }
    }
    
    public int[] getCurrentTileValues()
    {
        return Attributes[CurrentTile];
    }
    
    public int getCurrentTileID()
    {
        return TileID[CurrentTile];
    }
    
    public void isAnimating(boolean value)
    {
        isAnimating = value;
    }
    
    public void AnimateOnTouch(boolean value)
    {
        AnimateOnTouch = value;
    }
    
    public String getSequence()
    {
        return Sequence;
    }
    
    public int getSpeed()
    {
        return Speed.TimerMax;
    }
    
    public boolean getAnimateOnTouch()
    {
        return AnimateOnTouch;
    }
    
    public boolean isEndOfSequence()
    {
        if (EndOfSequence)
        {
            return true;
        } else
        {
            return false;
        }
    }
    
    public int[] getFirstTileValues()
    {
     //   System.out.print("ID0 stats: ");
        for (int i = 0; i < Attributes[0].length; i++)
        {
            System.out.append(Attributes[0][i] + ", ");
        }
        return Attributes[0];
    }
    
    public int getFirstTileID()
    {
        
        return TileID[0];
    }
}
