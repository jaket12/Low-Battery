package MegaEngine;
import java.io.*;

public class KeyBoard 
{
    //Keyboard buttons
    protected int Player1Up = 200;
    protected int Player1Down = 208;
    protected int Player1Left = 203;
    protected int Player1Right = 205;
    protected int Player1Shoot = 30;
    protected int Player1Jump = 31;
    protected int Player1Pause = 28;
    protected int Player1Menu = 1;
    protected int Player1Chat = 41;
    //Cumulative frame count that any button has been held down. 0 = unpressed 1 = just pressed now 2+ = held
    protected int Player1UpPressed = 0;
    protected int Player1DownPressed = 0;
    protected int Player1LeftPressed = 0;
    protected int Player1RightPressed = 0;
    protected int Player1ShootPressed = 0;
    protected int Player1JumpPressed = 0;
    protected int Player1PausePressed = 0;
    protected int Player1MenuPressed = 0;
    protected int Player1ChatPressed = 0;
    
    //Controller buttons. Only apply when true
    protected boolean Player1UsingController = true;
    protected int Player1ControllerNumber = -1;
    protected int Player1CUp = 0;
    protected int Player1CDown = 0;
    protected int Player1CLeft = 0;
    protected int Player1CRight = 0;
    protected int Player1CShoot = 0;
    protected int Player1CJump = 0;
    protected int Player1CPause = 0;
    protected int Player1CMenu = 0;
    protected int Player1CChat = 0;
    //0-Unpressed 1-Pressed for frame 2-held
    protected int Player1CUpPressed = 0;
    protected int Player1CDownPressed = 0;
    protected int Player1CLeftPressed = 0;
    protected int Player1CRightPressed = 0;
    
    //Keyboard
    protected int Player2Up = 23;
    protected int Player2Down = 37;
    protected int Player2Left = 36;
    protected int Player2Right = 38;
    protected int Player2Shoot = 31;
    protected int Player2Jump = 30;
    protected int Player2Pause = 25;
    protected int Player2Menu = 1;
    protected int Player2Chat = 41;
    //Controller
    protected boolean Player2UsingController = false;
    protected int Player2ControllerNumber = -1;
    protected int Player2CUp = 0;
    protected int Player2CDown = 0;
    protected int Player2CLeft = 0;
    protected int Player2CRight = 0;
    protected int Player2CShoot = 0;
    protected int Player2CJump = 0;
    protected int Player2CPause = 0;
    protected int Player2CMenu = 0;
    protected int Player2CChat = 0;

    //A universal setting to prevent player input from having any impact in the game.
    public boolean IgnorePlayer1Input = false;
    public boolean IgnorePlayer2Input = false;
    
    public KeyBoard()
    {
        LoadKeyboard();
    }
    
    private void LoadKeyboard()
    {
        //Load the control file and set the variables based on it
        try
        {
            FileInputStream fstream = new FileInputStream("Controls.txt");
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            String[] splits;
            while ((strLine = br.readLine()) != null)   
            {
                splits = strLine.split("~");
                if (splits[0].startsWith("//"))
                {
                    //Comment, do nothing
                } else if (splits[0].equals("Player1UsingController"))
                {
                    Player1UsingController = Boolean.valueOf(splits[1]);
                } else if (splits[0].equals("Player1ControllerNumber"))
                {
                    Player1ControllerNumber = Integer.valueOf(splits[1]);
                } else if (splits[0].equals("Player1Up"))
                {
                    Player1Up = Integer.valueOf(splits[1]);
                } else if (splits[0].equals("Player1Down"))
                {
                    Player1Down = Integer.valueOf(splits[1]);
                } else if (splits[0].equals("Player1Left"))
                {
                    Player1Left = Integer.valueOf(splits[1]);
                } else if (splits[0].equals("Player1Right"))
                {
                    Player1Right = Integer.valueOf(splits[1]);
                } else if (splits[0].equals("Player1Jump"))
                {
                    Player1Jump = Integer.valueOf(splits[1]);
                } else if (splits[0].equals("Player1Shoot"))
                {
                    Player1Shoot = Integer.valueOf(splits[1]);
                } else if (splits[0].equals("Player1Pause"))
                {
                    Player1Pause = Integer.valueOf(splits[1]);
                } else if (splits[0].equals("Player1Menu"))
                {
                    Player1Menu = Integer.valueOf(splits[1]);
                } else if (splits[0].equals("Player1Chat"))
                {
                    Player1Chat = Integer.valueOf(splits[1]);
                } else if (splits[0].equals("Player2UsingController"))
                {
                    Player2UsingController = Boolean.valueOf(splits[1]);
                } else if (splits[0].equals("Player2ControllerNumber"))
                {
                    Player2ControllerNumber = Integer.valueOf(splits[1]);
                } else if (splits[0].equals("Player2Up"))
                {
                    Player2Up = Integer.valueOf(splits[1]);
                } else if (splits[0].equals("Player2Down"))
                {
                    Player2Down = Integer.valueOf(splits[1]);
                } else if (splits[0].equals("Player2Left"))
                {
                    Player2Left = Integer.valueOf(splits[1]);
                } else if (splits[0].equals("Player2Right"))
                {
                    Player2Right = Integer.valueOf(splits[1]);
                } else if (splits[0].equals("Player2Jump"))
                {
                    Player2Jump = Integer.valueOf(splits[1]);
                } else if (splits[0].equals("Player2Shoot"))
                {
                    Player2Shoot = Integer.valueOf(splits[1]);
                } else if (splits[0].equals("Player2Pause"))
                {
                    Player2Pause = Integer.valueOf(splits[1]);
                } else if (splits[0].equals("Player2Menu"))
                {
                    Player2Menu = Integer.valueOf(splits[1]);
                } else if (splits[0].equals("Player2Chat"))
                {
                    Player2Chat = Integer.valueOf(splits[1]);
                } else if (splits[0].equals("Player1CUp"))
                {
                    Player1CUp = Integer.valueOf(splits[1]);
                } else if (splits[0].equals("Player1CDown"))
                {
                    Player1CDown = Integer.valueOf(splits[1]);
                } else if (splits[0].equals("Player1CLeft"))
                {
                    Player1CLeft = Integer.valueOf(splits[1]);
                } else if (splits[0].equals("Player1CRight"))
                {
                    Player1CRight = Integer.valueOf(splits[1]);
                } else if (splits[0].equals("Player1CJump"))
                {
                    Player1CJump = Integer.valueOf(splits[1]);
                } else if (splits[0].equals("Player1CShoot"))
                {
                    Player1CShoot = Integer.valueOf(splits[1]);
                } else if (splits[0].equals("Player1CPause"))
                {
                    Player1CPause = Integer.valueOf(splits[1]);
                } else if (splits[0].equals("Player1CMenu"))
                {
                    Player1CMenu = Integer.valueOf(splits[1]);
                } else if (splits[0].equals("Player1CChat"))
                {
                    Player1CChat = Integer.valueOf(splits[1]);
                } else if (splits[0].equals("Player2CUp"))
                {
                    Player2CUp = Integer.valueOf(splits[1]);
                } else if (splits[0].equals("Player2CDown"))
                {
                    Player2CDown = Integer.valueOf(splits[1]);
                } else if (splits[0].equals("Player2CLeft"))
                {
                    Player2CLeft = Integer.valueOf(splits[1]);
                } else if (splits[0].equals("Player2CRight"))
                {
                    Player2CRight = Integer.valueOf(splits[1]);
                } else if (splits[0].equals("Player2CJump"))
                {
                    Player2CJump = Integer.valueOf(splits[1]);
                } else if (splits[0].equals("Player2CShoot"))
                {
                    Player2CShoot = Integer.valueOf(splits[1]);
                } else if (splits[0].equals("Player2CPause"))
                {
                    Player2CPause = Integer.valueOf(splits[1]);
                } else if (splits[0].equals("Player2CMenu"))
                {
                    Player2CMenu = Integer.valueOf(splits[1]);
                } else if (splits[0].equals("Player2CChat"))
                {
                    Player2CChat = Integer.valueOf(splits[1]);
                }
            }
            //Close the input stream
            in.close();
        }catch (Exception e){//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }
    
    public boolean isKeyBound(int key)
    {
        boolean value = false;
        
        if (Player1Up == key || Player1Down == key || Player1Left == key || Player1Right == key 
                || Player1Shoot == key || Player1Jump == key || Player1Pause == key || Player1Menu == key)
        {
            value = true;
        }
        
        
        return value;
    }
    
    public boolean isControllerKeyBound(int key)
    {
        boolean value = false;
        
        if (Player1CUp == key || Player1CDown == key || Player1CLeft == key || Player1CRight == key 
                || Player1CShoot == key || Player1CJump == key || Player1CPause == key || Player1CMenu == key)
        {
            value = true;
        }
        
        
        return value;
    }
    
    public void SaveCurrentSettings()
    {
        try
        {
            // Create file 
            FileWriter fstream = new FileWriter("Controls.txt");
            BufferedWriter out = new BufferedWriter(fstream);
            out.write("Player1UsingController~" + Player1UsingController);
            out.newLine();
            out.write("Player1ControllerNumber~" + Player1ControllerNumber);
            out.newLine();
            out.write("Player1Up~" + Player1Up);
            out.newLine();
            out.write("Player1Down~" + Player1Down);
            out.newLine();
            out.write("Player1Left~" + Player1Left);
            out.newLine();
            out.write("Player1Right~" + Player1Right);
            out.newLine();
            out.write("Player1Jump~" + Player1Jump);
            out.newLine();
            out.write("Player1Shoot~" + Player1Shoot);
            out.newLine();
            out.write("Player1Pause~" + Player1Pause);
            out.newLine();
            out.write("Player1Menu~" + Player1Menu);
            out.newLine();
            out.write("Player1Chat~" + Player1Chat);
            out.newLine();
            out.write("Player1CUp~" + Player1CUp);
            out.newLine();
            out.write("Player1CDown~" + Player1CDown);
            out.newLine();
            out.write("Player1CLeft~" + Player1CLeft);
            out.newLine();
            out.write("Player1CRight~" + Player1CRight);
            out.newLine();
            out.write("Player1CJump~" + Player1CJump);
            out.newLine();
            out.write("Player1CShoot~" + Player1CShoot);
            out.newLine();
            out.write("Player1CPause~" + Player1CPause);
            out.newLine();
            out.write("Player1CMenu~" + Player1CMenu);
            out.newLine();
            out.write("Player1CChat~" + Player1CChat);
            out.newLine();
            out.write("//Player2");
            out.newLine();
            out.write("Player2UsingController~" + Player2UsingController);
            out.newLine();
            out.write("Player2ControllerNumber~" + Player2ControllerNumber);
            out.newLine();
            out.write("Player2Up~" + Player2Up);
            out.newLine();
            out.write("Player2Down~" + Player2Down);
            out.newLine();
            out.write("Player2Left~" + Player2Left);
            out.newLine();
            out.write("Player2Right~" + Player2Right);
            out.newLine();
            out.write("Player2Jump~" + Player2Jump);
            out.newLine();
            out.write("Player2Shoot~" + Player2Shoot);
            out.newLine();
            out.write("Player2Pause~" + Player2Pause);
            out.newLine();
            out.write("Player2Menu~" + Player2Menu);
            out.newLine();
            out.write("Player2Chat~" + Player2Chat);
            out.newLine();
            out.write("Player2CUp~" + Player2CUp);
            out.newLine();
            out.write("Player2CDown~" + Player2CDown);
            out.newLine();
            out.write("Player2CLeft~" + Player2CLeft);
            out.newLine();
            out.write("Player2CRight~" + Player2CRight);
            out.newLine();
            out.write("Player2CJump~" + Player2CJump);
            out.newLine();
            out.write("Player2CShoot~" + Player2CShoot);
            out.newLine();
            out.write("Player2CPause~" + Player2CPause);
            out.newLine();
            out.write("Player2CMenu~" + Player2CMenu);
            out.newLine();
            out.write("Player2CChat~" + Player2CChat);
            //Close the output stream
            out.close();
        }catch (Exception e){//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
          
    }
    
    public void RestoreDefaults()
    {
        Player1Up = 200;
        Player1Down = 208;
        Player1Left = 203;
        Player1Right = 205;
        Player1Shoot = 31;
        Player1Jump = 30;
        Player1Pause = 28;
        Player1Menu = 1;
        Player1Chat = 41;
        Player1UsingController = false;
        Player1ControllerNumber = -1;

        Player2Up = 200;
        Player2Down = 208;
        Player2Left = 203;
        Player2Right = 205;
        Player2Shoot = 31;
        Player2Jump = 30;
        Player2Pause = 28;
        Player2Menu = 1;
        Player2Chat = 41;
        Player2UsingController = false;
        Player2ControllerNumber = -1;
    }
}
