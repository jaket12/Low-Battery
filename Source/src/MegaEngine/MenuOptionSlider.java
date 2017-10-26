package MegaEngine;

import org.newdawn.slick.Graphics;

public class MenuOptionSlider extends MenuOption
{
    private boolean Horizontal = true;
    
    private int BarPointHeight = 12;
    private int BarPointWidth = 4;
    private int EnergyBarTotalHeight = 112;
    private int EnergyBarTotalWidth = 12;
    private int HealthBarX = 272;
    private int HealthBarY = 68;
    
    private float BarLocX;
    private float BarLocY;
            
    public MenuOptionSlider(String keystring, float keyint, float selectorx, float selectory, boolean horizontal, float sliderx, float slidery)
    {
        KeyString = keystring;
        KeyInt = keyint;
        SelectorX = selectorx;
        SelectorY = selectory;
        Horizontal = horizontal;
        isMultiOption = true;
        BarLocX = sliderx;
        BarLocY = slidery;
        OptionExists = true;
        
        AddStillImage("Resource/Image/HealthBarEmptyHorz.png", BarLocX, BarLocY, true, (int)KeyInt);
        AddStillImage("Resource/Image/HealthBarHorz.png", BarLocX, BarLocY, true, (int)KeyInt);
    }
    
    @Override
    public void draw(Graphics g)
    {
        if (OptionExists)
        {
            if (OptionContainsImages)
            {
                //The bar image always exists, so just draw them external to the array
                OptionImage[0].draw();
                OptionImage[1].draw(BarLocX, BarLocY, BarLocX + (KeyInt * BarPointWidth), BarLocY + BarPointHeight, 0, 0, (KeyInt * BarPointWidth), BarPointHeight);
                
                //any other images may be drawn afterwards
                for (iImage = 2; iImage < ImageArraySize; iImage++)
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
    }
    
    @Override
    public void SubtractKeyValue()
    {
        KeyInt -= 1;
        if (KeyInt < 0)
        {
            KeyInt = 0;
        } else if (KeyInt > 28)
        {
            KeyInt = 28;//Current max for a health bar.  Should change later
        }
    }
    
    @Override
    public void AddKeyValue()
    {
        KeyInt += 1;
        if (KeyInt < 0)
        {
            KeyInt = 0;
        } else if (KeyInt > 28)
        {
            KeyInt = 28;//Current max for a health bar.  Should change later
        }
    }
}
