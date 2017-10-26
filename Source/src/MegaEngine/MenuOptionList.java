package MegaEngine;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;



public class MenuOptionList extends MenuOption
{
    //Holds many check boxes
    MenuOptionCheckBox[] Options;
    int OptionCount;
    int i;
    
    public MenuOptionList(String keystring, float SelectX, float SelectY)
    {
        KeyString = keystring;
        KeyInt = 0;//Selected value doesn't exist yet.
        SelectorX = SelectX;
        SelectorY = SelectY;
        OptionExists = true;
        OptionImage = new UIImage[0];
        OptionText = new MenuText[0];
        OptionCount = 0;
        isMultiOption = true;
        Options = new MenuOptionCheckBox[0];
        AddStillImage("Resource/Image/Menu/ListSelector.png", -16, -16, true, 0);
        
    }

    @Override
    public void AddText(String text, float LocX, float LocY, Color textcolor)
    {
        ExpandOptionArraySize(1);
        Options[OptionCount - 1] = new MenuOptionCheckBox(text, false, LocX, LocY);
        Options[OptionCount - 1].AddText(text, LocX, LocY, textcolor);
        Options[OptionCount - 1].OptionText[Options[OptionCount - 1].TextArraySize - 1].ShowAlways(true);
        
        if(OptionCount == 1)
        {
            OptionImage[0].setX(Options[(int)KeyInt].SelectorX - 16);
            OptionImage[0].setY(Options[(int)KeyInt].SelectorY);
        }

    }
    
    protected void ExpandOptionArraySize(int extrasize)
    {
        if (OptionCount > 0)
        {
            int OldArrayLength = OptionCount;
            int NewArraySize = OptionCount + extrasize;

            MenuOptionCheckBox[] TempText = Options;

            //Copy all data to a temporary location
            System.arraycopy(Options, 0, TempText, 0, OptionCount);

            //Increase the length of the array        
            Options = new MenuOptionCheckBox[NewArraySize];

            //Copy the data back into the usable objects
            System.arraycopy(TempText, 0, Options, 0, OldArrayLength);
            OptionCount += extrasize;
        } else
        {
            //Array was either null or 0.  Just initialize it
            Options = new MenuOptionCheckBox[extrasize];
            OptionCount = extrasize;
        }
      
        
    }//End ExpandArraySize()
    
    @Override
    public void SubtractKeyValue()
    {
        Options[(int)KeyInt].Selected(false);
        KeyInt -= 1;
        if (KeyInt < 0)
        {
            KeyInt = OptionCount - 1;
        } else if (KeyInt > OptionCount - 1)
        {
            KeyInt = 0;//Current max for a health bar.  Should change later
        }
        Options[(int)KeyInt].Selected(true);
        OptionImage[0].setX(Options[(int)KeyInt].SelectorX - 16);
        OptionImage[0].setY(Options[(int)KeyInt].SelectorY);
    }
    
    @Override
    public void AddKeyValue()
    {
        Options[(int)KeyInt].Selected(false);
        KeyInt += 1;
        if (KeyInt < 0)
        {
            KeyInt = OptionCount - 1;
        } else if (KeyInt > OptionCount - 1)
        {
            KeyInt = 0;//Current max for a health bar.  Should change later
        }
        Options[(int)KeyInt].Selected(true);
        OptionImage[0].setX(Options[(int)KeyInt].SelectorX - 16);
        OptionImage[0].setY(Options[(int)KeyInt].SelectorY);
    }
    
    @Override
    public void draw(Graphics g) 
    {        
        if (OptionExists)
        {
            if (OptionContainsImages)
            {
                for (iImage = 0; iImage < ImageArraySize; iImage++)
                {
                    OptionImage[iImage].draw();
                }
            }

            if (OptionContainsText)
            {
                for (iText = 0; iText < TextArraySize; iText++)
                {
                        OptionText[iText].draw(g, isSelected, isActivated);
                }
            }
        }
        
        if (OptionCount > 0)
        {
            for (i = 0; i < OptionCount; i++)
            {
                Options[i].draw(g);
            }
        }
        

    }//End Draw

    @Override
    public void Update(int delta) 
    {
        if (OptionExists)
        {
            if (OptionContainsImages)
            {
                for (iImage = 0; iImage < ImageArraySize; iImage++)
                {
                    OptionImage[iImage].Update(delta);
                }
            }

            if (OptionContainsText)
            {
                for (iText = 0; iText < TextArraySize; iText++)
                {
                    OptionText[iText].Update(delta);
                }
            }
        }
  
        if (OptionCount > 0)
        {
            for (i = 0; i < OptionCount; i++)
            {
                Options[i].Update(delta);
            }
        }
  
    }//End Update
    
    @Override
    public void Activate()
    {
        AddKeyValue();
    }
}
