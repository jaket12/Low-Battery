package MegaEngine.ObjectAI;

import MegaEngine.GameObject;
import MegaEngine.AnimationComposite;
import MegaEngine.Global;
import MegaEngine.PlayerCharacter;
import MegaEngine.Timer;
import MegaEngine.main;

public class GenericObject extends GameObject
{
    //A unique Entity class can have it's own variables to make it extra special.
    //Be sure to use shared Entity variables so you don't end up making a custom everything.
    private Timer DisappearTimer;
    
    public GenericObject(int aitype, Global.Directions horzfacing, Global.Directions vertfacing, float locationx, float locationy, boolean respawnable, boolean respawned, int objectarrayid, float inertiax, float inertiay, String text)
    {
// <editor-fold defaultstate="collapsed" desc="Basic Setup">
        //Location
        LocationX = locationx;
        LocationY = locationy;
        CurrentTileX = LocationXToCurrentTileX();
        CurrentTileY = LocationYToCurrentTileY();
        
        //Movement
        VelocityEnabled = true;
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
        ObjectCollisionEnabled = false;
        BulletCollisionEnabled = false;
        ItemCollisionEnabled = false;
        TileCollisionEnabled = false;
        IsLeftWall = true;
        IsRightWall = true;
        IsPlatform = true;
        IsCeiling = true;
        
        //Sprite Drawing
        IsVisible = true;
        IsAnimating = true;
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
        Name = "Generic Object";
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
        Invulnerable = false;
        DamageOnTouch = false;
        DamageOnTouchValue = 0;
        IsPlayerActivatorEnabled = false;
// </editor-fold>
        
        DisappearTimer = new Timer("Alternate Visibile/Hidden", 2000, true, true);
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
        
// <editor-fold defaultstate="collapsed" desc="Animation0 Generic Object">
        EntityAnimation[0] = new AnimationComposite(5, 1, 32, 32, 0, false, false);
        EntityAnimation[0].AddFrame(0, 0, 200, 0, 0);
        EntityAnimation[0].setCurrentAnimation(0);
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
     */
    @Override
    protected void ComputeStatus(int delta, int[][][] TileData, int[][] RoomData, PlayerCharacter Player) {
        
        if (DisappearTimer.Update(delta)) {
            //The timer has activated, and now we switch between either a solid or invisible.
            ToggleVisible();
        }
    }
    
    /**
     * The second step in the Update() process which determines how far the
     * Entity wants to move this frame, based on the Status that was just computed.
     */
    @Override
    protected void ComputeVelocity() {
        
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
        if (!AnimationLock)
        {
            frameanim = 0;
            anim = true;
        }
        
        //Set the finally determined animation and state
        ChangeCurrentAnimation(frameanim, anim);
    }    
    
    private void ToggleVisible() {
        
        IsVisible = !IsVisible;
        
        if (IsVisible) {
            IsLeftWall = true;
            IsRightWall = true;
            IsPlatform = true;
            IsCeiling = true;
            main.Global.Sound.PlaySound(5);
        } else {
            IsLeftWall = false;
            IsRightWall = false;
            IsPlatform = false;
            IsCeiling = false;
            
        }
        
    }
}
