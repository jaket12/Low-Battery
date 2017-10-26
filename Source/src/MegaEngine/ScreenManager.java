/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MegaEngine;

import MegaEngine.Global.CutSceneType;
import MegaEngine.Global.Directions;

public class ScreenManager {

    //Basic requirements for the screen to function.
    //Will always focus on centering the player first, and map edges seconds
    int WindowWidth = 640;
    int WindowHeight = 480;
    float ScreenX = 32;//Where the screen *actually* is
    float ScreenY = 32;//Start one block down and right so the left black is hidden
    int LevelHeight;//Level size in terms of tiles
    int LevelWidth;
    int TileWidth;
    int TileHeight;
    float LevelWidthPrecise;
    float LevelHeightPrecise;
    float MaxScreenX;//The maximum limit of the screen to go to.  This is the screen
    float MaxScreenY;//Moving all the way right or down, and not showing any black
    float HalfScreenX;//Half of the X axis for the visible screen.  Used for screen location
    float HalfScreenY;
    
    //Fancy effects
    boolean ScreenXLockedLeft = false; //If true, this axis will not move this direction.
    boolean ScreenXLockedRight = false;
    boolean ScreenYLockedLeft = false; //If all are true, the screen does not move at all
    boolean ScreenYLockedRight = false;
    
    boolean Room = false; //Whether or not the current area is considered a room
    boolean MovingToNextRoom = false;
    float RoomTopLeftX;//Co-ordinates for two points in the room, making a rectangle
    float RoomTopLeftY;
    float RoomBottomRightX;
    float RoomBottomRightY;
    
    private int CurrentRoomCeiling;//This is a Y co-ordinate that says to stop the screen here
    private int CurrentRoomBottom;//Y co-ordinate
    private int CurrentRoomLeft;//X co-ordinate
    private int CurrentRoomRight;//X co-ordinate
    private int CurrentRoomCeilingPX;//PX is the pixel location of the current room.  So it's the above multiplied by 32
    private int CurrentRoomBottomPX;
    private int CurrentRoomLeftPX;
    private int CurrentRoomRightPX;
    private int CurrentScreenCeiling;//The adjusted co-ordinates to check for the boundries of the screen
    private int CurrentScreenBottom;//These are the numbers to use for locking the screen in the room
    private int CurrentScreenLeft;
    private int CurrentScreenRight;
    
    private int TransitionDirection;//1:top 2:bottom 3:left 4:right
    private CutSceneType CutScene = CutSceneType.None;
    private final float TransitionDistance = 0;//How far the screen pans before finishing the transition
    private Directions RoomTransitionDirection;
    private float RoomTransitionSpeedHorizontal = 320;
    private float RoomTransitionSpeedVertical = 240;
    private float InertiaX = 0;
    private float InertiaY = 0;
    private boolean EnableRoomBoundaries = true;
    
    public ScreenManager(int screenx, int screeny, int levelheight, int levelwidth, int tilewidth, int tileheight)
    {
        ScreenX = screenx;
        ScreenY = screeny;
        LevelHeight = levelheight;
        LevelWidth = levelwidth;
        TileWidth = tilewidth;
        TileHeight = tileheight;
        LevelWidthPrecise = LevelWidth * TileWidth;
        LevelHeightPrecise = LevelHeight * TileHeight;
        MaxScreenX = LevelWidthPrecise - WindowWidth - (TileWidth);
        MaxScreenY = LevelHeightPrecise - WindowHeight - (TileHeight);
        HalfScreenX = WindowWidth / 2;
        HalfScreenY = WindowHeight / 2;
    }
    
    public void Update(int delta, float MegaManX, float MegaManY)
    {

        ScreenAlignment(delta, MegaManX, MegaManY);
        if (EnableRoomBoundaries)
        {
            CheckRoomBoundaries();//Limit the screen to the room
        }
        CheckAbsoluteBoundaries(); //The most the screen should ever go (edge of map)
        
    }
    
    private void RoomTransition(int delta, float MegaManX, float MegaManY, int direction)
    {
        if (direction == 4)
        {//pan right
            ScreenX += WindowWidth * (delta / 1000f);
            if (ScreenX > MegaManX - 32 )
            {
                //Screen moved too far, lock it up
                ScreenX = MegaManX;
            }
        }
        else if (direction == 3)
        {//pan left
            ScreenX -= WindowWidth * (delta / 1000f);
            if (ScreenX > MegaManX - 32 )
            {
                //Screen moved too far, lock it up
                ScreenX = MegaManX;
            }
        }
        else if (direction == 2)
        {
            ScreenY += WindowHeight * (delta / 1000f);
            if (ScreenY > MegaManY - 32 )
            {
                //Screen moved too far, lock it up
                ScreenY = MegaManY;
            }
        }
        else if (direction == 1)
        {
            ScreenY -= WindowHeight * (delta / 1000f);
            if (ScreenY > MegaManY - 32)
            {
                //Screen moved too far, lock it up
                ScreenY = MegaManY;
            }
        }
    }
    
    private void CheckAbsoluteBoundaries()
    {
        if (ScreenX < 32)
        {
            ScreenX = 32;
        }

        if (ScreenY < 32)
        {
            ScreenY = 32;
        }

        if (ScreenX > MaxScreenX)
        {
            ScreenX = MaxScreenX;
        }

        if (ScreenY > MaxScreenY)
        {
            ScreenY = MaxScreenY;
        }
    }
    
    private void CheckRoomBoundaries()
    {
        if (ScreenX < CurrentScreenLeft)
        {
            ScreenX = CurrentScreenLeft;
        }
        if (ScreenX > CurrentScreenRight)
        {
            ScreenX = CurrentScreenRight;
        }
        if (ScreenY < CurrentScreenCeiling)
        {
            ScreenY = CurrentScreenCeiling;
        }
        if (ScreenY > CurrentScreenBottom)
        {
            ScreenY = CurrentScreenBottom;
        }
    }
    
    public void ScreenAlignment(int delta, float MegaManX, float MegaManY)
    {

        if (CutScene == CutSceneType.RoomTransition)
        {
            ScreenX += InertiaX * (delta / 1000f);
            ScreenY += InertiaY * (delta / 1000f);
        }
        else
        {
            ScreenX = MegaManX - HalfScreenX;//Screen is half the width and height from the player, who is always in the center (unless an axis is locked)
            ScreenY = MegaManY - HalfScreenY;
        }
            

    }
    
    public void setCurrentRoomBoundaries(int Top, int Bottom, int Left, int Right)
    {
        CurrentRoomCeiling = Top;//Tile locations
        CurrentRoomBottom = Bottom;
        CurrentRoomLeft = Left;
        CurrentRoomRight = Right;
        
        CurrentRoomCeilingPX = Top * TileWidth;//Pixel locations
        CurrentRoomBottomPX = Bottom * TileWidth;
        CurrentRoomLeftPX = Left * TileWidth;
        CurrentRoomRightPX = Right * TileWidth;
        
        CurrentScreenCeiling = (Top * TileHeight);//Pixel locations for the screen to be locked
        CurrentScreenBottom = (Bottom * TileHeight) - WindowHeight + TileHeight;
        CurrentScreenLeft = (Left * TileWidth);
        CurrentScreenRight = (Right * TileWidth) - WindowWidth + TileWidth;
    }
    
    public void SetRoomTransition(int Direction)
    {
        CutScene = CutSceneType.RoomTransition;
        EnableRoomBoundaries = false;
        
        switch (Direction)
        {
            case 0:
                RoomTransitionDirection = Directions.None;
                setStaticVelocityY(0);
                setStaticVelocityX(0);
                break;
            case 1:
                RoomTransitionDirection = Directions.Up;
                setStaticVelocityY(-RoomTransitionSpeedVertical);
                setStaticVelocityX(0);
                break;
            case 2:
                RoomTransitionDirection = Directions.Down;
                setStaticVelocityY(RoomTransitionSpeedVertical);
                setStaticVelocityX(0);
                break;
            case 3:
                RoomTransitionDirection = Directions.Left;
                setStaticVelocityX(-RoomTransitionSpeedHorizontal);
                setStaticVelocityY(0);
                break;
            case 4:
                RoomTransitionDirection = Directions.Right;
                setStaticVelocityX(RoomTransitionSpeedHorizontal);
                setStaticVelocityY(0);
                break;
        }
    }
    
    public void disableRoomTransition()
    {
        TransitionDirection = 0;
        CutScene = CutSceneType.None;
        EnableRoomBoundaries = true;
    }
    
    public void setStaticVelocityX(float value)
    {
        InertiaX = value;
    }
    
    public void setStaticVelocityY(float value)
    {
        InertiaY = value;
    }
}
