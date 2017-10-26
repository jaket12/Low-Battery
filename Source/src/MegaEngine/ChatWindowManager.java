package MegaEngine;

import MegaEngine.Global.CutSceneType;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class ChatWindowManager {
    
    private String FilePath = "/Resource/Movie/Chat/";
    private String EmergencyErrorMessage = "/Resource/Movie/Chat/ErrorChat.scr";
    
    private ArrayList<ChatMessage> Messages;
    private int CurrentMessageIndex = 0;
    
    private ArrayList<String> MultiChoiceOptionKeys;
    private ArrayList<String> MultiChoiceOptionDisplays;
    private boolean ShowMultiChoice = false;
    private MenuOptionManager MenuOptions;
    private int MultiChoiceOptionLocationX;
    private int MultiChoiceOptionLocationXStart = 80;
    private int MultiChoiceOptionLocationXIncrement = 160;
    private int MultiChoiceOptionLocationY = 202;
    
    private StaticImageManager Images;
    private StaticTextManager Text;
    
    private enum ChatWindowState {
        Closed, Open, Expanding, Collapsing
    }
    private ChatWindowState WindowState;
    private Timer ExpandingTimer = new Timer("Time to expand/collapse", 300, true, true);
        
    private int MessageLineXInitial = 96;
    private int MessageLineYInitial = 64;
    private int MessageLineXIncrement = 0;
    private int MessageLineYIncrement = 32;
    private int MessageLineX;
    private int MessageLineY;
    
    public ChatWindowManager() {
        
        Images = new StaticImageManager();
        Text = new StaticTextManager();
        WindowState = ChatWindowState.Closed;
        Images.AddImage("Resource/Image/ChatWindow.png", 60, 48, true);
        Images.Vanish(0);
    }
    
    public void StartChat(String filename) {
        LoadMessages(filename);
        WindowState = ChatWindowState.Expanding;
        Images.Appear(0);
    }
    
    private void LoadMessages(String filename) {
        
        Text = new StaticTextManager();
        Messages = new ArrayList<ChatMessage>();
        CurrentMessageIndex = 0;
        MultiChoiceOptionKeys = new ArrayList<String>();
        MultiChoiceOptionDisplays = new ArrayList<String>();
        ShowMultiChoice = false;
                
        try 
        {
            System.out.println("Attempting to open chat file: " + FilePath + filename + ".scr");
            String finalsource = FilePath + filename + ".scr";
            InputStream fstream = getClass().getResourceAsStream(finalsource);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            
            while ((strLine = br.readLine()) != null) 
            {
                String[] splits = strLine.split("~");

                if (splits[0].charAt(0) == '/')
                {//Comment in the script, ignore and move on.
                    
                }
                else if (splits[0].equalsIgnoreCase("Message"))
                {
                    //A new frame of text will be displayed. It can have up to 3 lines.
                    
                    ArrayList<String> messagelines = new ArrayList<String>();
                    
                    for (int i = 1; i < splits.length; i++) {
                        messagelines.add(splits[i]);
                    }
                    
                    ChatMessage message = new ChatMessage(messagelines);
                    Messages.add(message);
                } else if (splits[0].equalsIgnoreCase("MultiChoice")) {
                    //There will be a decision to make at the end of this chat.
                    for (int i = 1; i < splits.length; i++) {
                        //Odd value is key for the global SwitchVariables
                        MultiChoiceOptionKeys.add(splits[i++]);
                        //Even value is the text to show on the screen
                        MultiChoiceOptionDisplays.add(splits[i]);
                    }
                }
            }
            in.close();
            
            if (MultiChoiceOptionKeys.size() == 0) {
                MenuOptions = new MenuOptionManager(0, 0);
            } else {
                MenuOptions = new MenuOptionManager(MultiChoiceOptionKeys.size(), 1);
                MultiChoiceOptionLocationX = MultiChoiceOptionLocationXStart;
                for (int i = 0; i < MultiChoiceOptionKeys.size(); i++) {
                    MenuOptions.NewOptionButton(i, 0, MultiChoiceOptionKeys.get(i), 1, MultiChoiceOptionLocationX, MultiChoiceOptionLocationY);
                    MenuOptions.AddTextToOption(i, 0, MultiChoiceOptionDisplays.get(i), MultiChoiceOptionLocationX + 16, MultiChoiceOptionLocationY, Color.white);
                    MenuOptions.ShowLastText(i, 0, "Always");
                    MultiChoiceOptionLocationX += MultiChoiceOptionLocationXIncrement;
                }
                MenuOptions.AddStillImageToOption(0, 0, "Resource/Image/ChatWindowChoice.png", 60, 182, true, 0);
                MenuOptions.NewSelector("Resource/Image/Menu/Selector.png", 0, 0, true);
            }
            
        } 
        catch (IOException e) 
        {
            System.err.println("Couldn't load chatmessage: " + e.getMessage());
            LoadMessages(EmergencyErrorMessage);
        } catch (NumberFormatException e) {
            System.err.println("Couldn't load chatmessage: " + e.getMessage());
            LoadMessages(EmergencyErrorMessage);
        }
    }
    
    public void draw(Graphics g) {
        
        //main.Global.endUse();might need this later
        Images.Draw(g);
        Text.Draw(g);
        if (ShowMultiChoice) {
            MenuOptions.Draw(g);
        }
    }
    
    public void Update(int delta) {
        
        switch (WindowState) {
            case Expanding:
                if (ExpandingTimer.Update(delta)) {
                    WindowState = ChatWindowState.Open;
                    GetNextMessage();
                }
                break;
            case Collapsing:
                if (ExpandingTimer.Update(delta)) {
                    WindowState = ChatWindowState.Closed;
                    Images.Vanish(0);
                }
                break;
            case Open:
                if (!ShowMultiChoice) {
                    if (main.Global.Keyboard.Player1ShootPressed == 1 || main.Global.Keyboard.Player1PausePressed == 1) {
                        GetNextMessage();
                    }
                } else {
                    if (main.Global.Keyboard.Player1LeftPressed == 1) {
                        MenuOptions.MoveSelectorLeft();
                    } else if (main.Global.Keyboard.Player1RightPressed == 1) {
                        MenuOptions.MoveSelectorRight();
                    } else if (main.Global.Keyboard.Player1ShootPressed == 1 || main.Global.Keyboard.Player1PausePressed == 1) {
                        String activation = MenuOptions.SelectedKeyString();
                        
                        for (int i = 0; i < MultiChoiceOptionKeys.size(); i++) {
                            if (activation.equalsIgnoreCase(MultiChoiceOptionKeys.get(i))) {
                                main.Global.SwitchVariables.SetVariable(MultiChoiceOptionKeys.get(i), true);
                            } else {
                                main.Global.SwitchVariables.SetVariable(MultiChoiceOptionKeys.get(i), false);
                            }
                        }
                        WindowState = ChatWindowState.Collapsing;
                        Text.AllVanish();
                    }
                }
                break;
            case Closed:
                break;
        }
        
        Images.Update(delta);
        Text.Update(delta);
        MenuOptions.update(delta);
    }
    
    public boolean IsClosed() {
        return (WindowState == ChatWindowState.Closed);
    }
    
    private void GetNextMessage() {
        
        if (CurrentMessageIndex < Messages.size()) {
            ChatMessage currentmessage = Messages.get(CurrentMessageIndex);
            MessageLineX = MessageLineXInitial;
            MessageLineY = MessageLineYInitial;
            Text = new StaticTextManager();
            for (String messageline : currentmessage.MessageLines) {
                Text.AddText(messageline, MessageLineX, MessageLineY, 255);
                MessageLineX += MessageLineXIncrement;
                MessageLineY += MessageLineYIncrement;
            }
            CurrentMessageIndex++;
        } else if (MultiChoiceOptionKeys.size() > 0) {
            //Chat messages are done showing. Now, show the choices.
            ShowMultiChoice = true;
        } 
        else {
            WindowState = ChatWindowState.Collapsing;
            Text.AllVanish();
        }
    }
}
