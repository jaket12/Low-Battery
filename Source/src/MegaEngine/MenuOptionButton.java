package MegaEngine;

public class MenuOptionButton extends MenuOption 
{

    public MenuOptionButton(String keystring, float keyint, float SelectX, float SelectY)
    {
        KeyString = keystring;
        KeyInt = keyint;
        SelectorX = SelectX;
        SelectorY = SelectY;
        OptionExists = true;
        OptionImage = new UIImage[0];
        OptionText = new MenuText[0];
        isMultiOption = false;
        
    }

    
    
    @Override
    public void Activate()
    {
        if (isActivated)
            isActivated = false;
        else
            isActivated = true;
    }

}
