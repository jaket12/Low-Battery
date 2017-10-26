/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MegaEngine.ObjectAI;

import MegaEngine.AnimationComposite;
import MegaEngine.GameObject;
import MegaEngine.Global;
import MegaEngine.LevelState;
import MegaEngine.PlayerCharacter;
import MegaEngine.main;
import java.util.Objects;

/**
 *
 * @author McRib
 */
public class Level1Bed extends GameObject {
        
    private float SundownPercent;
    
    public Level1Bed(int aitype, Global.Directions horzfacing, Global.Directions vertfacing, float locationx, float locationy, boolean solidwall, boolean deleteonactivate, int objectarrayid, float spriterow, float spritecolumn, String cutscenefilename)
    {
        
// <editor-fold defaultstate="collapsed" desc="Basic Setup">
        //Location
        LocationX = locationx;
        LocationY = locationy;
        CurrentTileX = LocationXToCurrentTileX();
        CurrentTileY = LocationYToCurrentTileY();
        
        //Movement
        VelocityEnabled = false;
        GravityEnabled = false;
        HorizontalSpeed = 0f;
        VerticalSpeed = 0f;
        InertiaX = 0f;
        InertiaY = 0f;
        
        //Animations and Hitboxes (this can get stupidly large)
        GenerateAnimationsAndHitboxes();
        
        //Collisions
        PlayerCollisionEnabled = false;
        EnemyCollisionEnabled = false;
        ObjectCollisionEnabled = true;
        BulletCollisionEnabled = false;
        ItemCollisionEnabled = false;
        TileCollisionEnabled = false;
        IsLeftWall = false;
        IsRightWall = false;
        IsPlatform = false;
        IsCeiling = false;
        
        //Sprite Drawing
        IsVisible = true;
        IsAnimating = false;
        DrawHitBox = false;
        AnimationLock = false;
        DirectionLock = false;
        DirectionHorizontal = Global.Directions.None;
        DirectionVertical = Global.Directions.None;
        CurrentAnimation = 0;
        PreviousAnimation = 0;
        
        //Despawn Rules
        IsAlive = true;
        IsOverWritable = false;
        DespawnOffScreen = false;
        ScreenLeashX = 99999;
        ScreenLeashY = 99999;
        DespawnOnRoomChange = false;
        DeleteOnPause = false;
        Respawnable = false;
        Respawned = false;
        
        //Stats and Values
        Name = "Simple Object";
        OwnerName = "None";
        IsPlayerOwned = false;
        OwnerArrayIndex = -1;
        EntityArrayIndex = -1;
        AIType = 0;
        Health = 1;
        HealthMax = 1;
        IsDying = false;
        IsFriendly = false;
        IsHostile = false;
        Invulnerable = true;
        DamageOnTouch = false;
        DamageOnTouchValue = 0;
        IsPlayerActivatorEnabled = true;
        CutSceneScriptFileName = "";
// </editor-fold>
                      
    }
    
    /**
     * Create every single animation, frame, layer, hitbox, and so much crap.
     * You can use a white outline for each color to layer a composite image.
     * This will allow you to change colors on the fly per layer.
     * Or, be lazy, and just grab a flat image sprite sheet and have the colors already there.
     */
    @Override
    protected void GenerateAnimationsAndHitboxes()
    {
        //Total animations this Entity will have. It's whatever the array size needs to be.
        AnimationCount = 1;
        //The sprite animations list
        EntityAnimation = new AnimationComposite[AnimationCount];
        //The Hitboxes to match with each sprite in the list
        HitBoxWidth = new int[AnimationCount];
        HitBoxHeight = new int[AnimationCount];
        HitBoxOffsetX = new float[AnimationCount];
        HitBoxOffsetY = new float[AnimationCount];
        MinimumYTilesToCheck = new int[AnimationCount];
        MinimumXTilesToCheck = new int[AnimationCount];
        
// <editor-fold defaultstate="collapsed" desc="Animation0 Default">
        EntityAnimation[0] = new AnimationComposite(4, 1, 32, 32, 0, false, false);
        EntityAnimation[0].AddFrame(0, 18, 5000, 0, 0);

        HitBoxWidth[0] = 32;
        HitBoxHeight[0] = 32;
        HitBoxOffsetX[0] = 0;
        HitBoxOffsetY[0] = 0;
        SetMinimumTilesForHitBox(0);
// </editor-fold>

    }
    
    /**
     * The first step in the Update() process which determines what the Entity
     * wants to do this frame based on input and other current states of the game.
     * @param delta
     * @param TileData
     * @param RoomData
     * @param Player
     */
    @Override
    protected void ComputeStatus(int delta, int[][][] TileData, int[][] RoomData, PlayerCharacter Player) {
        
    }
    
    /**
     * The second step in the Update() process which determines how far the
     * Entity wants to move this frame, based on the Status that was just computed.
     */
    @Override
    protected void ComputeVelocity() {
        
        VelocityX = 0;
        VelocityY = 0;
        
    }
    
    /*
     * Goes through the keyboard for direction to look
     * and then any statuses that may apply (ladder, falling, damage, shoot)
     * Once the final animation is figured out, it's hitbox is used for the
     * rest of the frame.
     */
    @Override
    public void GetCurrentAnimation()
    {
        frameanim = 0;
        anim = false;
        
        //Set the finally determined animation and state
        ChangeCurrentAnimation(frameanim, anim);
    }
    
    @Override
    public void ProcessCollision(GameObject Object)
    {
        if (Object.GetIsAlive() && Objects.equals(Object.GetName(), "Player Activator"))
        {
            //LevelState.OpenChatWindow(CutSceneScriptFileName);
            //LevelState.PerformCutScene(CutSceneScriptFileName, Global.CutSceneType.CustomEvent);
            SundownPercent = ((float)main.Global.SundownTimer.GetTimeRemaining() / main.Global.SundownTimer.getDuration()) * 100;
            
            if (SundownPercent == 100) {
                //Fresh day, no need to sleep
                LevelState.OpenChatWindow("Level1-SleepFullDay");
            } else {
                //Day is a little over. Can choose to rest.
                LevelState.PerformCutScene("Resource/Movie/Level1-Bed.scr", Global.CutSceneType.CustomEvent);
            }
            
        }
        
    }
    
    
}
