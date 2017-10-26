package MegaEngine;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.newdawn.slick.*;
import org.newdawn.slick.AngelCodeFont;

/**
 * An updated version of MenuText that I never seem to finish implementing.
 * It's better than the original in every form, but the old one is still used.
 * @author McRib
 */
public class MenuText {

    private String Text = "";
    private float LocX = 0;
    private float LocY = 0;
    private float Transparency = 255; //0 is invisible, 255 is visible
    private boolean isFadingOut = false;
    private boolean isFadingIn = false;
    private float Red = 1; //255 is white, 0 is black
    private float Green = 1;
    private float Blue = 1;
    private Color TextColor = new Color(1f, 1f, 1f, 1f);
    private float FadeOutRate = 1f;
    private float FadeOutRateDefault = 1f;//1 second
    private float FadeInRate = 1f;
    private float FadeInRateDefault = 1f;
    protected int KeyInt = -1;//Used for quicker comparisons as a keyword.
    //Current use for Keyint resides in MenuOption:
    //0 means UIImage exists but is not selected nor activated
    //1 is selected
    //2 is activated
    //3 is always show
    private AngelCodeFont Font;
    
    private boolean ShowAlways = false;
    private boolean ShowNever = false;
    private boolean ShowSelected = false;
    private boolean ShowUnSelected = false;
    private boolean ShowActivated = false;
    private boolean ShowUnActivated = false;

    public MenuText(String words, float X, float Y, float Trans)
    {
        
        try {
            Image fontimage = new Image("Resource/Font/MM.png");
            Font = new AngelCodeFont("Resource/Font/MM.fnt", fontimage);
            
        } catch (SlickException ex) {
            Logger.getLogger(MenuText.class.getName()).log(Level.SEVERE, null, ex);
        }
        Text = words;
        LocX = X;
        LocY = Y;
        Transparency = Trans;
        TextColor = new Color(Red, Green, Blue, Transparency);
    }

    public MenuText(String words, float X, float Y, float Trans, int keyint)
    {
        try {
            Image fontimage = new Image("Resource/Font/MM.png");
            Font = new AngelCodeFont("Resource/Font/MM.fnt", fontimage);
        } catch (SlickException ex) {
            Logger.getLogger(MenuText.class.getName()).log(Level.SEVERE, null, ex);
        }
        Text = words;
        LocX = X;
        LocY = Y;
        Transparency = Trans;
        KeyInt = keyint;
        TextColor = new Color(Red, Green, Blue, Transparency);
    }
    
    public MenuText(String words, float X, float Y, float red, float green, float blue, float Trans, int keyint)
    {
        try {
            Image fontimage = new Image("Resource/Font/MM.png");
            Font = new AngelCodeFont("Resource/Font/MM.fnt", fontimage);
            
        } catch (SlickException ex) {
            Logger.getLogger(MenuText.class.getName()).log(Level.SEVERE, null, ex);
        }
        Text = words;
        LocX = X;
        LocY = Y;
        Red = red;
        Green = green;
        Blue = blue;
        Transparency = Trans;
        KeyInt = keyint;
        
        TextColor = new Color(Red, Green, Blue, Transparency);
        
    }
    
    public void draw(Graphics g)
    {
        Font.drawString(LocX, LocY, Text, TextColor);
    }

    public void draw(Graphics g, boolean isSelected, boolean isActivated)
    {
       // System.out.println(isSelected + ", " + isActivated);
        if (ShowNever)
        {
            return;
        } else if (ShowAlways)
        {
            Font.drawString(LocX, LocY, Text, TextColor);
        } else if (ShowSelected && isSelected)
        {
            Font.drawString(LocX, LocY, Text, TextColor);
        } else if (ShowUnSelected && !isSelected)
        {
            Font.drawString(LocX, LocY, Text, TextColor);
        } else if (ShowActivated && isActivated)
        {
            Font.drawString(LocX, LocY, Text, TextColor);
        } else if (ShowUnActivated && !isActivated)
        {
            Font.drawString(LocX, LocY, Text, TextColor);
        }
        
        
    }
    
    public void Update(int delta)
    {
        if (isFadingOut)
        {
            Transparency -= FadeOutRate * (delta / 1000f);
            TextColor.a = Transparency;
        }
        if (isFadingIn)
        {
            Transparency += FadeInRate * (delta / 1000f);
            TextColor.a = Transparency;
        }
        if (Transparency > 1)
        {
            Transparency = 1;
            isFadingIn = false;
        }
        if (Transparency < 0)
        {
            Transparency = 0;
            isFadingOut = false;
        }
        
        
    }
    
    public String getText()
    {
        return Text;
    }

    public void setText(String words)
    {
        Text = words;
    }

    public float getX()
    {
        return LocX;
    }

    public float getY()
    {
        return LocY;
    }

    public void setX(float x)
    {
        LocX = x;
    }

    public void setY(float Y)
    {
        LocX = Y;
    }

    public float getAlpha()
    {
        return Transparency;
    }

    

    public void fadeIn()
    {
        isFadingIn = true;
        isFadingOut = false;
        FadeInRate = FadeInRateDefault;
    }

    /*
     * Reveal the text over time by a specified rate in milliseconds.
     */
    public void fadeIn(int rate)
    {
        isFadingIn = true;
        FadeInRate = rate;
        isFadingOut = false;
    }
    
    public void fadeOut()
    {
        isFadingOut = true;
        isFadingIn = false;
        FadeOutRate = FadeOutRateDefault;
    }

    /*
     * Hide the text over time by a specified rate in milliseconds.
     */
    public void fadeOut(int rate)
    {
        isFadingOut = true;
        FadeOutRate = rate;
        isFadingIn = false;
    }
    
    public void Vanish()
    {
        Transparency = 0;
        isFadingOut = false;
        isFadingIn = false;
        TextColor = new Color(Red, Green, Blue, Transparency);
    }

    public void Appear()
    {
        Transparency = 1;
        isFadingOut = false;
        isFadingIn = false;
        TextColor = new Color(Red, Green, Blue, Transparency);
    }

    public void centerText()
    { //Place the text on the screen so that it is center justified
      //Font is fixed width so each letter is EXACTLY 16 pixels
      int LengthX = Text.length() * 16;
      LocX = ((640 - LengthX) / 2);
    }

    public float getRed()
    {
        return Red;
    }

    public float getGreen()
    {
        return Green;
    }

    public float getBlue()
    {
        return Blue;
    }

    public Color getColor() {
        return TextColor;
    }
    
    public void ShowAlways(boolean value)
    {
        ShowAlways = value;
    }
    
    public void ShowNever(boolean value)
    {
        ShowNever = value;
    }
    
    public void ShowSelected(boolean value)
    {
        ShowSelected = value;
    }
    
    public void ShowUnSelected(boolean value)
    {
        ShowUnSelected = value;
    }
    
    public void ShowActivated(boolean value)
    {
        ShowActivated = value;
    }
    
    public void ShowUnActivated(boolean value)
    {
        ShowUnActivated = value;
    }
}
