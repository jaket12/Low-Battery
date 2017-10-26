package MegaEngine;

import org.newdawn.slick.*;
import org.newdawn.slick.state.*;
import org.newdawn.slick.state.transition.*;
import MegaEngine.Global.Directions;

public class MenuStateOptions extends BasicGameState {

    /** The ID given to this state */
    public static final int ID = 3;
    
    /** The game holding this state */
    private StateBasedGame game;
    
    private Transition FadeOut = new FadeOutTransition(Color.black);
    private Transition FadeIn = new FadeInTransition(Color.black);
    
    private int ShowMenu = 0;
    private boolean InputGiven = false;
    private int Controller = 0;
    private int Key = 0;
    private boolean RecordingKeyPress = false;
    private int Button = -1;

    private boolean PreRenderLoad = true;//If true, this is the first frame to be seen for the state. Goes true on Leave()
    
    private Input Input;
    private boolean ControllerDownHeld = false;
    private boolean ControllerLeftHeld = false;
    private boolean ControllerRightHeld = false;
    private boolean ControllerUpHeld = false;
    private boolean ControllerDownPressed = false;
    private boolean ControllerLeftPressed = false;
    private boolean ControllerRightPressed = false;
    private boolean ControllerUpPressed = false;
    
    private int Player = 1;//Which player's controls are being edited/shown
    /*
     * 
     */
    
    MenuOptionManager Options;//Holds any type of menu option and allows the user to select, activate, enable, or modify them
    MenuOptionManager Options2;//Controls
    StaticTextManager Text = new StaticTextManager();
    StaticTextManager Text2 = new StaticTextManager();

    private KeyBoard Keyboard;
    
    @Override
    public int getID() 
    {
        return ID;
    }

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException 
    {
        this.game = game;
        Initialize();
    }

    /*
     * Called when the state is entered.
     * 
     * Note: Enter() is called AFTER Render() when switching from a state.
     * The purpose of this is to generate an image which the transistion effects
     * can use when fading in or out.
     */
    @Override
    public void enter(GameContainer container, StateBasedGame game) throws SlickException 
    {
        Keyboard = main.Global.getKeyboard();//get the keyboard so it can get used easier in the state
        FadeOut = new FadeOutTransition(Color.black);
        main.ClearFrames(true);
    }

    /*
     * Create the menu and its options.
     */
    private void Initialize() throws SlickException 
    {
        Keyboard = main.Global.getKeyboard();//get the keyboard so it can get used easier in the state
        Input = main.Container.getInput();//Transfer the input of the whole game into this state
        Color TextColor = new Color(1f, 1f, 1f, 1f);
        
        Text.AddText("Options", 0, 8, 255);
        Text.CenterTextLast();

        
        Options = new MenuOptionManager(9, 1);
        
        Options.NewOptionSlider(0, 0, "Music", main.Global.Music.MusicVolume, 24, 64, true, 400, 64);
        Options.AddTextToOption(0, 0, "Music", 48, 64, TextColor);
        Options.ShowLastText(0, 0, "Always");
        
        Options.NewOptionSlider(1, 0, "Sound", main.Global.Sound.SoundVolume, 24, 96, true, 400, 96);
        Options.AddTextToOption(1, 0, "Sound", 48, 96, TextColor);
        Options.ShowLastText(1, 0, "Always");
        
        Options.NewOptionCheckBox(2, 0, "Full Screen", main.FullScreen, 24, 128);
        Options.AddTextToOption(2, 0, "Full Screen", 48, 128, TextColor);
        Options.ShowLastText(2, 0, "Always");
        Options.AddTextToOption(2, 0, "On", 400, 128, TextColor);
        Options.ShowLastText(2, 0, "Activated");
        Options.AddTextToOption(2, 0, "Off", 400, 128, TextColor);
        Options.ShowLastText(2, 0, "UnActivated");
        
        Options.NewOptionCheckBox(3, 0, "Vertical Sync", main.VSync, 24, 160);
        Options.AddTextToOption(3, 0, "V.Sync", 48, 160, TextColor);
        Options.ShowLastText(3, 0, "Always");
        Options.AddTextToOption(3, 0, "On", 400, 160, TextColor);
        Options.ShowLastText(3, 0, "Activated");
        Options.AddTextToOption(3, 0, "Off", 400, 160, TextColor);
        Options.ShowLastText(3, 0, "UnActivated");
        
        Options.NewOptionButton(7, 0, "Controls", 2, 24, 288);
        Options.AddTextToOption(7, 0, "Controls", 48, 288, TextColor);
        Options.ShowLastText(7, 0, "Always");
        
        Options.NewOptionButton(8, 0, "Back", 1, 24, 320);
        Options.AddTextToOption(8, 0, "Back", 48, 320, TextColor);
        Options.ShowLastText(8, 0, "Always");

        Options.NewSelector("Resource/Image/Menu/SelectorDot.png", 0, 0, true);
        
        
        
        
        Options2 = new MenuOptionManager(16, 1);

        Options2.NewOptionList(0, 0, "Player", 28, 32);
        Options2.AddTextToOption(0, 0, "1", 200, 32, TextColor);
        Options2.AddTextToOption(0, 0, "2", 250, 32, TextColor);
        
        Options2.NewOptionList(1, 0, "Control", 28, 64);
        Options2.AddTextToOption(1, 0, "Keyboard", 200, 64, TextColor);
        Options2.AddTextToOption(1, 0, "Controller", 380, 64, TextColor);
        
        Options2.NewOptionButton(2, 0, "SetUp", 1, 28, 160);
        Options2.AddTextToOption(2, 0, "Up", 48, 160, TextColor);
        Options2.ShowLastText(2, 0, "Always");
        Options2.AddTextToOption(2, 0, "Up", 48, 160, TextColor);
        Options2.ShowLastText(2, 0, "Always");
        
        Options2.NewOptionButton(3, 0, "SetDown", 1, 28, 192);
        Options2.AddTextToOption(3, 0, "Down", 48, 192, TextColor);
        Options2.ShowLastText(3, 0, "Always");
        
        Options2.NewOptionButton(4, 0, "SetLeft", 1, 28, 224);
        Options2.AddTextToOption(4, 0, "Left", 48, 224, TextColor);
        Options2.ShowLastText(4, 0, "Always");

        Options2.NewOptionButton(5, 0, "SetRight", 1, 28, 256);
        Options2.AddTextToOption(5, 0, "Right", 48, 256, TextColor);
        Options2.ShowLastText(5, 0, "Always");
        
        Options2.NewOptionButton(6, 0, "SetJump", 1, 28, 288);
        Options2.AddTextToOption(6, 0, "Jump", 48, 288, TextColor);
        Options2.ShowLastText(6, 0, "Always");
        
        Options2.NewOptionButton(7, 0, "SetShoot", 1, 28, 320);
        Options2.AddTextToOption(7, 0, "Shoot", 48, 320, TextColor);
        Options2.ShowLastText(7, 0, "Always");
        
        Options2.NewOptionButton(8, 0, "SetPause", 1, 28, 352);
        Options2.AddTextToOption(8, 0, "Pause", 48, 352, TextColor);
        Options2.ShowLastText(8, 0, "Always");
        
        Options2.NewOptionButton(9, 0, "SetMenu", 1, 28, 384);
        Options2.AddTextToOption(9, 0, "Menu", 48, 384, TextColor);
        Options2.ShowLastText(9, 0, "Always");
        
        Options2.NewOptionButton(10, 0, "SetChat", 1, 28, 416);
        Options2.AddTextToOption(10, 0, "Chat", 48, 416, TextColor);
        Options2.ShowLastText(10, 0, "Always");
        
        Options2.NewOptionButton(11, 0, "Cancel", 1, 28, 448);
        Options2.AddTextToOption(11, 0, "Cancel", 48, 448, TextColor);
        Options2.ShowLastText(11, 0, "Always");
        
        Options2.NewOptionButton(12, 0, "Defaults", 1, 200, 448);
        Options2.AddTextToOption(12, 0, "Defaults(F12)", 228, 448, TextColor);
        Options2.ShowLastText(12, 0, "Always");
        
        Options2.NewOptionButton(13, 0, "Save", 1, 500, 448);
        Options2.AddTextToOption(13, 0, "Save", 524, 448, TextColor);
        Options2.ShowLastText(13, 0, "Always");
        
        Options2.NewSelector("Resource/Image/Menu/SelectorDot.png", 0, 0, true);
        
        DisplayPlayerControls();
    }

    private void DisplayPlayerControls()
    {
        Text2 = new StaticTextManager();
        
        Text2.AddText("Options (Controls)", 0, 8, 255);
        Text2.CenterTextLast();
        Text2.AddText("Info", 0, 88, 255);
        Text2.CenterTextLast();
        Text2.AddText("Player :", 48, 32, 255);
        Text2.AddText("Control:", 48, 64, 255);
        Text2.AddText("Action:", 48, 112, 255);
        Text2.AddText("Key Code", 192, 112, 255);
        if (Player == 1)
        {
            if (Options2.getKeyInt(1, 0) == 0f)
            {
                
                //Show player 1 controls
                Text2.AddText(String.valueOf(Keyboard.Player1Up), 192, 160, 255);//Up
                Text2.AddText(String.valueOf(Keyboard.Player1Down), 192, 192, 255);//Down
                Text2.AddText(String.valueOf(Keyboard.Player1Left), 192, 224, 255);//Left
                Text2.AddText(String.valueOf(Keyboard.Player1Right), 192, 256, 255);//Right
                Text2.AddText(String.valueOf(Keyboard.Player1Jump), 192, 288, 255);//Jump
                Text2.AddText(String.valueOf(Keyboard.Player1Shoot), 192, 320, 255);//Shoot
                Text2.AddText(String.valueOf(Keyboard.Player1Pause), 192, 352, 255);//Pause
                Text2.AddText(String.valueOf(Keyboard.Player1Menu), 192, 384, 255);//Menu
                Text2.AddText(String.valueOf(Keyboard.Player1Chat), 192, 416, 255);//Chat
            } else
            {
                //Show player 1 controls
                Text2.AddText(String.valueOf(Keyboard.Player1CUp), 192, 160, 255);//Up
                Text2.AddText(String.valueOf(Keyboard.Player1CDown), 192, 192, 255);//Down
                Text2.AddText(String.valueOf(Keyboard.Player1CLeft), 192, 224, 255);//Left
                Text2.AddText(String.valueOf(Keyboard.Player1CRight), 192, 256, 255);//Right
                Text2.AddText(String.valueOf(Keyboard.Player1CJump), 192, 288, 255);//Jump
                Text2.AddText(String.valueOf(Keyboard.Player1CShoot), 192, 320, 255);//Shoot
                Text2.AddText(String.valueOf(Keyboard.Player1CPause), 192, 352, 255);//Pause
                Text2.AddText(String.valueOf(Keyboard.Player1CMenu), 192, 384, 255);//Menu
                Text2.AddText(String.valueOf(Keyboard.Player1CChat), 192, 416, 255);//Chat
            }
        }
        else if (Player == 2)
        {
            if (Options2.getKeyInt(1, 0) == 0f)
            {
                //Show player 2 controls
                Text2.AddText(String.valueOf(Keyboard.Player2Up), 192, 160, 255);//Up
                Text2.AddText(String.valueOf(Keyboard.Player2Down), 192, 192, 255);//Down
                Text2.AddText(String.valueOf(Keyboard.Player2Left), 192, 224, 255);//Left
                Text2.AddText(String.valueOf(Keyboard.Player2Right), 192, 256, 255);//Right
                Text2.AddText(String.valueOf(Keyboard.Player2Jump), 192, 288, 255);//Jump
                Text2.AddText(String.valueOf(Keyboard.Player2Shoot), 192, 320, 255);//Shoot
                Text2.AddText(String.valueOf(Keyboard.Player2Pause), 192, 352, 255);//Pause
                Text2.AddText(String.valueOf(Keyboard.Player2Menu), 192, 384, 255);//Menu
                Text2.AddText(String.valueOf(Keyboard.Player2Chat), 192, 416, 255);//Chat
                 
            } else
            {
                //Show player 2 controls
                Text2.AddText(String.valueOf(Keyboard.Player2CUp), 192, 160, 255);//Up
                Text2.AddText(String.valueOf(Keyboard.Player2CDown), 192, 192, 255);//Down
                Text2.AddText(String.valueOf(Keyboard.Player2CLeft), 192, 224, 255);//Left
                Text2.AddText(String.valueOf(Keyboard.Player2CRight), 192, 256, 255);//Right
                Text2.AddText(String.valueOf(Keyboard.Player2CJump), 192, 288, 255);//Jump
                Text2.AddText(String.valueOf(Keyboard.Player2CShoot), 192, 320, 255);//Shoot
                Text2.AddText(String.valueOf(Keyboard.Player2CPause), 192, 352, 255);//Pause
                Text2.AddText(String.valueOf(Keyboard.Player2CMenu), 192, 384, 255);//Menu
                Text2.AddText(String.valueOf(Keyboard.Player2CChat), 192, 416, 255);//Chat
            }
        }
        
        
    }
    
    /*
     * Clean up the game state before it is switched over to another state.
     */
    @Override
    public void leave(GameContainer container, StateBasedGame game) throws SlickException 
    {

        main.Global.Keyboard = this.Keyboard;
        FadeIn = new FadeInTransition(Color.black);
        PreRenderLoad = true;
    }

    private void ResetState() throws SlickException
    {
        Initialize();
    }
    /*
     * Draw the graphics onto the screen for a particular frame
     */
    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) 
    {
        if (PreRenderLoad) {
            try {
                ResetState();
            } catch (SlickException ex) {  }
            PreRenderLoad = false;
        }
        
        
        if (ShowMenu == 0)
        {
            Options.Draw(g);
            Text.Draw(g);
        }
  
        if (ShowMenu == 1)
        {
            Options2.Draw(g);
            Text2.Draw(g);
        }
        

    }

    /*
     * Run game logic for a particular frame
     */
    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) 
    {
        main.Global.Update(delta);
        
            if (ShowMenu == 0)
            {
                Options.update(delta);
                Text.Update(delta);
            }
            
            if (ShowMenu == 1)
            {
                Text2.Update(delta);
                Options2.update(delta);
            }
            

    //    Info.Update(delta);
        //System.out.println(controls.getAxisValue(1, 3));//Left/Right
      //  System.out.println(controls.getAxisValue(1, 2));//Up/Down
        
        if (Keyboard.Player1UsingController || Keyboard.Player2UsingController)
        {
            if (RecordingKeyPress)
            {
                for (int i = 0; i < Input.getControllerCount(); i++)
                {
                    if (Input.isControllerUp(i))
                    {//Action is bound to controller move up
                        SetControllerBind(i, -1);
                    } else if (Input.isControllerDown(i))
                    {//Action is bound to controller move up
                        SetControllerBind(i, -2);
                    } if (Input.isControllerLeft(i))
                    {//Action is bound to controller move up
                        SetControllerBind(i, -3);
                    } if (Input.isControllerRight(i))
                    {//Action is bound to controller move up
                        SetControllerBind(i, -4);
                    } 
                }
            }
            else
            {
                for (int i = 0; i < Input.getControllerCount(); i++)
                {
                    
                    if (Input.isControllerUp(i))
                    {//Action is bound to controller move up
                        if (!ControllerUpHeld)
                        {
                            controllerButtonPressed(i, -1);
                        }
                        ControllerUpPressed = true;
                    } else if (Input.isControllerDown(i))
                    {//Action is bound to controller move up
                        if (!ControllerDownHeld)
                        {
                            controllerButtonPressed(i, -2);
                        }
                        ControllerDownPressed = true;
                    } if (Input.isControllerLeft(i))
                    {//Action is bound to controller move up
                        if (!ControllerLeftHeld)
                        {
                            controllerButtonPressed(i, -3);
                        }
                        ControllerLeftPressed = true;
                    } if (Input.isControllerRight(i))
                    {//Action is bound to controller move up
                        if (!ControllerRightHeld)
                        {
                            controllerButtonPressed(i, -4);
                        }
                        ControllerRightPressed = true;
                    } 
                }
            }
            SaveControllerPress();
        }
        
        
    }

    /*
     * Saves the controller press for the MENUs.
     * In game keys are saved by the character and are not recorded in the game state.
     */
    private void SaveControllerPress()
    {

        ControllerDownHeld = ControllerDownPressed;
        ControllerUpHeld = ControllerUpPressed;
        ControllerLeftHeld = ControllerLeftPressed;
        ControllerRightHeld = ControllerRightPressed;
        ControllerDownPressed = false;
        ControllerUpPressed = false;
        ControllerLeftPressed = false;
        ControllerRightPressed = false;
    }
    
    /*
     * Transistion into another game state based on its ID
     */
    private void SwapState(int ID) 
    {
        game.enterState(ID, FadeOut, FadeIn);
    }
    
    @Override
    public void keyPressed(int key, char c) {
        
        //A key was pressed.  Check each possible event's bound key for a match.
        //Eventually, the game will allow a player 2 and a controller, making 4 checks for each event
        
        if (key == Input.KEY_F12)
        {
            Keyboard.RestoreDefaults();
            RecordingKeyPress = false;
            DisplayPlayerControls();
            Text2.ChangeText(1, "Defaults Loaded");//Info text
            Text2.CenterText(1);
        }
        if (RecordingKeyPress)
        {
            if (Player == 1)
            {
                    SetKeyBind(key);
            }
            else if (Player == 2)
            {
                main.Global.Sound.PlaySound(1);
            }
            
        }
        else
        {
            if (key == Keyboard.Player1Up) 
            {
                if (ShowMenu == 0) {
                    Options.MoveSelector(Directions.Up);
                }
                if (ShowMenu == 1) {
                    Options2.MoveSelector(Directions.Up);
                }
                main.Global.Sound.PlaySound(1);
                
            } else if (key == Keyboard.Player1Left) 
            {
                if (ShowMenu == 0)
                {
                    Options.MoveSelector(Directions.Left);
                    if (Options.isSelectedMultiOption())
                    {
                        Activate(Options.SelectedKeyString(), Options.SelectedKeyInt());
                    }
                }
                if (ShowMenu == 1)
                {
                    Options2.MoveSelector(Directions.Left);
                    if (Options2.isSelectedMultiOption())
                    {
                        Activate(Options2.SelectedKeyString(), Options2.SelectedKeyInt());
                    }
                }
                main.Global.Sound.PlaySound(1);
                
            } else if (key == Keyboard.Player1Down) 
            {
                if (ShowMenu == 0) {
                    Options.MoveSelector(Directions.Down);
                }
                if (ShowMenu == 1) {
                    Options2.MoveSelector(Directions.Down);
                }
                main.Global.Sound.PlaySound(1);
                
            } else if (key == Keyboard.Player1Right) 
            {
                if (ShowMenu == 0)
                {
                    Options.MoveSelector(Directions.Right);
                    if (Options.isSelectedMultiOption())
                    {
                        Activate(Options.SelectedKeyString(), Options.SelectedKeyInt());
                    }
                }
                if (ShowMenu == 1)
                {
                    Options2.MoveSelector(Directions.Right);
                    if (Options2.isSelectedMultiOption())
                    {
                        Activate(Options2.SelectedKeyString(), Options2.SelectedKeyInt());
                    }
                }
                main.Global.Sound.PlaySound(1);
                
            } else if (key == Keyboard.Player1Pause)
            {
                
                if (ShowMenu == 0)
                {
                    Options.ActivateSelection();
                    Activate(Options.SelectedKeyString(), Options.SelectedKeyInt());
                }
                else if (ShowMenu == 1)
                {
                    Options2.ActivateSelection();
                    Activate(Options2.SelectedKeyString(), Options2.SelectedKeyInt());
                }
                
            }
        
        }

        Controller = 2;
        Key = key;
        InputGiven = true;
    }
    
    private void derpinput()
    {
        //Simple template for any state to check for key bound inputs.
        //Fill in with the actual data where it is needed
        
//        if (key == Keyboard.Player1Up) 
//        {
//
//        } else if (key == Keyboard.Player1Down) {
//            
//        } else if (key == Keyboard.Player1Left) {
//       
//        } else if (key == Keyboard.Player1Right) {
//           
//        } else if (key == Keyboard.Player1Jump) {
//
//        } else if (key == Keyboard.Player1Shoot) {
//            
//        } else if (key == Keyboard.Player1Pause) {
//       
//        } else if (key == Keyboard.Player1Menu) {
//
//        } else if (key == Keyboard.Player1Chat) {
//
//        }

        
    }
    
    private void SetKeyBind(int key)
    {
        String keystring = Options2.SelectedKeyString();
        float keyint = Options2.SelectedKeyInt();
        Text2.ChangeText(1, "");
        
        if (!Keyboard.isKeyBound(key))
        {
            if (keystring.equals("SetUp"))
            {
                if (Player == 1)
                {
                    Keyboard.Player1Up = key;
                }
                else
                {
                    Keyboard.Player2Up = key;
                }
            } else if (keystring.equals("SetDown"))
            {
                if (Player == 1)
                {
                    Keyboard.Player1Down = key;
                }
                else
                {
                    Keyboard.Player2Down = key;
                }
            }  else if (keystring.equals("SetLeft"))
            {
                if (Player == 1)
                {
                    Keyboard.Player1Left = key;
                }
                else
                {
                    Keyboard.Player2Left = key;
                }
            } else if (keystring.equals("SetRight"))
            {
                if (Player == 1)
                {
                    Keyboard.Player1Right = key;
                }
                else
                {
                    Keyboard.Player2Right = key;
                }
            } else if (keystring.equals("SetJump"))
            {
                if (Player == 1)
                {
                    Keyboard.Player1Jump = key;
                }
                else
                {
                    Keyboard.Player2Jump = key;
                }
            } else if (keystring.equals("SetShoot"))
            {
                if (Player == 1)
                {
                    Keyboard.Player1Shoot = key;
                }
                else
                {
                    Keyboard.Player2Shoot = key;
                }
            } else if (keystring.equals("SetPause"))
            {
                if (Player == 1)
                {
                    Keyboard.Player1Pause = key;
                }
                else
                {
                    Keyboard.Player2Pause = key;
                }
            } else if (keystring.equals("SetMenu"))
            {
                if (Player == 1)
                {
                    Keyboard.Player1Menu = key;
                }
                else
                {
                    Keyboard.Player2Menu = key;
                }
            } else if (keystring.equals("SetChat"))
            {
                if (Player == 1)
                {
                    Keyboard.Player1Chat = key;
                }
                else
                {
                    Keyboard.Player2Chat = key;
                }
            }
        }
        else
        {
            Text2.ChangeText(1, "Key Already Bound");//Info text
            Text2.CenterText(1);
        }
        RecordingKeyPress = false;
        DisplayPlayerControls();
    }
    
    private void SetControllerBind(int Controller, int key)
    {
        System.out.println("Controller " + Controller + " pressed " + key);
        
        String keystring = Options2.SelectedKeyString();
        float keyint = Options2.SelectedKeyInt();
        Text2.ChangeText(1, "");
            if (keystring.equals("SetUp"))
            {
                if (Player == 1)
                {
                    Keyboard.Player1CUp = key;
                }
                else
                {
                    Keyboard.Player2CUp = key;
                }
            } else if (keystring.equals("SetDown"))
            {
                if (Player == 1)
                {
                    Keyboard.Player1CDown = key;
                }
                else
                {
                    Keyboard.Player2CDown = key;
                }
            }  else if (keystring.equals("SetLeft"))
            {
                if (Player == 1)
                {
                    Keyboard.Player1CLeft = key;
                }
                else
                {
                    Keyboard.Player2CLeft = key;
                }
            } else if (keystring.equals("SetRight"))
            {
                if (Player == 1)
                {
                    Keyboard.Player1CRight = key;
                }
                else
                {
                    Keyboard.Player2CRight = key;
                }
            } else if (keystring.equals("SetJump"))
            {
                if (Player == 1)
                {
                    Keyboard.Player1CJump = key;
                }
                else
                {
                    Keyboard.Player2CJump = key;
                }
            } else if (keystring.equals("SetShoot"))
            {
                if (Player == 1)
                {
                    Keyboard.Player1CShoot = key;
                }
                else
                {
                    Keyboard.Player2CShoot = key;
                }
            } else if (keystring.equals("SetPause"))
            {
                if (Player == 1)
                {
                    Keyboard.Player1CPause = key;
                }
                else
                {
                    Keyboard.Player2CPause = key;
                }
            } else if (keystring.equals("SetMenu"))
            {
                if (Player == 1)
                {
                    Keyboard.Player1CMenu = key;
                }
                else
                {
                    Keyboard.Player2CMenu = key;
                }
            } else if (keystring.equals("SetChat"))
            {
                if (Player == 1)
                {
                    Keyboard.Player1CChat = key;
                }
                else
                {
                    Keyboard.Player2CChat = key;
                }
            }
            if (Player == 1) {
                Keyboard.Player1ControllerNumber = Controller;
            }
            if (Player == 2) {
                Keyboard.Player2ControllerNumber = Controller;
            }

        RecordingKeyPress = false;
        DisplayPlayerControls();
    }
    
    public void Activate(String keystring, float keyint)
    {
        
        if (keystring.equals("Back"))
        {
            //Go back to the previous screen
            main.Global.Sound.PlaySound(2);
            main.Global.SaveCurrentSettings();
            SwapState(2);
        }
        else if (keystring.equals("Full Screen"))
        {
            main.Global.Sound.PlaySound(2);
            main.SetFullScreen(!main.FullScreen);

        } else if (keystring.equals("Vertical Sync"))
        {
            main.Global.Sound.PlaySound(2);
            main.SetVSync(!main.VSync);

        } else if (keystring.equals("Music"))
        {
            main.Global.Music.SetVolume((int)keyint);
        } else if (keystring.equals("Sound"))
        {
            main.Global.Sound.SetVolume((int)keyint);
        }
        else if (keystring.equals("Controls"))
        {
            main.Global.Sound.PlaySound(2);
            Options.SelectOption(0,0);
            ShowMenu = 1;
        } else if (keystring.equals("Player"))
        {
            main.Global.Sound.PlaySound(1);
            //Display a different players controls
            Player = (int)keyint + 1;
            DisplayPlayerControls();
        } else if (keystring.equals("Control"))
        {
            //Player is switching control scheme
            main.Global.Sound.PlaySound(1);
            if (Player == 1)
            {
                if (keyint == 0)
                {
                    //Using keyboard
                    Keyboard.Player1UsingController = false;
                }
                else
                {
                    Keyboard.Player1UsingController = true;
                }
            }
            if (Player == 2)
            {
                if (keyint == 0)
                {
                    //Using keyboard
                    Keyboard.Player2UsingController = false;
                }
                else
                {
                    Keyboard.Player2UsingController = true;
                }
            }
            DisplayPlayerControls();
        } else if (keystring.equals("SetUp"))
        {
            Text2.ChangeText(1, "Press Button for UP");
            Text2.CenterText(1);
            RecordingKeyPress = true;
            Button = 0; //Up number
        } else if (keystring.equals("SetDown"))
        {
            Text2.ChangeText(1, "Press Button for Down");
            Text2.CenterText(1);
            RecordingKeyPress = true;
            Button = 0; //Up number
        } else if (keystring.equals("SetLeft"))
        {
            Text2.ChangeText(1, "Press Button for Left");
            Text2.CenterText(1);
            RecordingKeyPress = true;
            Button = 0; //Up number
        } else if (keystring.equals("SetRight"))
        {
            Text2.ChangeText(1, "Press Button for Right");
            Text2.CenterText(1);
            RecordingKeyPress = true;
            Button = 0; //Up number
        } else if (keystring.equals("SetJump"))
        {
            Text2.ChangeText(1, "Press Button for Jump");
            Text2.CenterText(1);
            RecordingKeyPress = true;
            Button = 0; //Up number
        } else if (keystring.equals("SetShoot"))
        {
            Text2.ChangeText(1, "Press Button for Shoot");
            Text2.CenterText(1);
            RecordingKeyPress = true;
            Button = 0; //Up number
        } else if (keystring.equals("SetPause"))
        {
            Text2.ChangeText(1, "Press Button for Pause");
            Text2.CenterText(1);
            RecordingKeyPress = true;
            Button = 0; //Up number
        } else if (keystring.equals("SetMenu"))
        {
            Text2.ChangeText(1, "Press Button for Menu");
            Text2.CenterText(1);
            RecordingKeyPress = true;
            Button = 0; //Up number
        } else if (keystring.equals("SetChat"))
        {
            Text2.ChangeText(1, "Press Button for Chat");
            Text2.CenterText(1);
            RecordingKeyPress = true;
            Button = 0; //Up number
        }
        else if (keystring.equals("Cancel"))
        {
            main.Global.Sound.PlaySound(2);
            Options2.SelectOption(0,0);
            ShowMenu = 0;
        } else if (keystring.equals("Save"))
        {
            main.Global.Sound.PlaySound(2);
            Keyboard.SaveCurrentSettings();
            Options2.SelectOption(0,0);
            ShowMenu = 0;
        } else if (keystring.equals("Defaults"))
        {
            main.Global.Sound.PlaySound(2);
            Keyboard.RestoreDefaults();
            RecordingKeyPress = false;
            DisplayPlayerControls();
            Text2.ChangeText(1, "Defaults Loaded");//Info text
            Text2.CenterText(1);
        }
        
        
    }
    
    @Override
    public void keyReleased(int key, char c)
    {

    }
    
    @Override
    public void controllerButtonPressed(int controller, int button)
    {
        System.out.println("Controller " + controller + " pressed " + button);
        if (RecordingKeyPress)
        {
            if (Player == 1)
            {
                if (Keyboard.Player1UsingController)
                {
                    SetControllerBind(controller, button);
                }
            }
            else if (Player == 2)
            {
                if (Keyboard.Player2UsingController)
                {
                    SetControllerBind(controller, button);
                }
            }
            
        }
        else
        {
            if (controller == Keyboard.Player1ControllerNumber)
            {
                if (button == Keyboard.Player1CUp) 
                {
                    
                    if (ShowMenu == 0) {
                        Options.MoveSelector(Directions.Up);
                    }
                    if (ShowMenu == 1) {
                        Options2.MoveSelector(Directions.Up);
                    }
                } else if (button == Keyboard.Player1CLeft) {
                    if (ShowMenu == 0)
                    {
                        Options.MoveSelector(Directions.Left);
                        if (Options.isSelectedMultiOption())
                        {
                            Activate(Options.SelectedKeyString(), Options.SelectedKeyInt());
                        }
                    }
                    if (ShowMenu == 1)
                    {
                        Options2.MoveSelector(Directions.Left);
                        if (Options2.isSelectedMultiOption())
                        {
                            Activate(Options2.SelectedKeyString(), Options2.SelectedKeyInt());
                        }
                    }

                } else if (button == Keyboard.Player1CDown) {
                    if (ShowMenu == 0) {
                        Options.MoveSelector(Directions.Down);
                    }
                    if (ShowMenu == 1) {
                        Options2.MoveSelector(Directions.Down);
                    }
                } else if (button == Keyboard.Player1CRight) {
                    if (ShowMenu == 0)
                    {
                        Options.MoveSelector(Directions.Right);
                        if (Options.isSelectedMultiOption())
                        {
                            Activate(Options.SelectedKeyString(), Options.SelectedKeyInt());
                        }
                    }
                    if (ShowMenu == 1)
                    {
                        Options2.MoveSelector(Directions.Right);
                        if (Options2.isSelectedMultiOption())
                        {
                            Activate(Options2.SelectedKeyString(), Options2.SelectedKeyInt());
                        }
                    }

                } else if (button == Keyboard.Player1CPause)
                {

                    if (ShowMenu == 0)
                    {
                        Options.ActivateSelection();
                        Activate(Options.SelectedKeyString(), Options.SelectedKeyInt());
                    }
                    else if (ShowMenu == 1)
                    {
                        Options2.ActivateSelection();
                        Activate(Options2.SelectedKeyString(), Options2.SelectedKeyInt());
                    }

                }
            }
            else if (controller == Keyboard.Player2ControllerNumber)
            {
                
            }
            
        }

    }
    
    @Override
    public void controllerButtonReleased(int controller, int button)
    {
        for (int i = 0; i < Input.getControllerCount(); i++)
        {

            if (Input.isControllerUp(i))
            {//Action is bound to controller move up
                controllerButtonPressed(i, -1);
                ControllerUpPressed = true;
            } else if (Input.isControllerDown(i))
            {//Action is bound to controller move up
                controllerButtonPressed(i, -2);
                ControllerDownPressed = true;
            } if (Input.isControllerLeft(i))
            {//Action is bound to controller move up
                controllerButtonPressed(i, -3);
                ControllerLeftPressed = true;
            } if (Input.isControllerRight(i))
            {//Action is bound to controller move up
                controllerButtonPressed(i, -4);
                ControllerRightPressed = true;
            } 
        }
    }
}
