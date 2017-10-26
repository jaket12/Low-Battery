package MegaEngine.ItemAI;

import MegaEngine.Item;
import MegaEngine.AnimationComposite;
import MegaEngine.Entity;
import MegaEngine.Global.Directions;
import MegaEngine.PlayerCharacter;
import MegaEngine.main;

public class ItemGeneric extends Item
{
    
    public ItemGeneric (float locationx, float locationy, boolean Respawnable, boolean Respawned)
    {
// <editor-fold defaultstate="collapsed" desc="Basic Setup">
        //Location
        LocationX = locationx;
        LocationY = locationy;
        CurrentTileX = LocationXToCurrentTileX();
        CurrentTileY = LocationYToCurrentTileY();
        
        //Movement
        VelocityEnabled = true;
        GravityEnabled = true;
        HorizontalSpeed = 0f;
        VerticalSpeed = 0f;
        InertiaX = 0f;
        InertiaY = 0f;
        
        //Animations and Hitboxes (this can get stupidly large)
        GenerateAnimationsAndHitboxes();
        
        //Collisions
        PlayerCollisionEnabled = true;
        EnemyCollisionEnabled = false;
        ObjectCollisionEnabled = false;
        BulletCollisionEnabled = false;
        ItemCollisionEnabled = false;
        TileCollisionEnabled = true;
        IsLeftWall = false;
        IsRightWall = false;
        IsPlatform = false;
        IsCeiling = false;
        
        //Sprite Drawing
        IsVisible = true;
        IsAnimating = true;
        DrawHitBox = false;
        AnimationLock = false;
        DirectionLock = false;
        DirectionHorizontal = Directions.None;
        DirectionVertical = Directions.None;
        CurrentAnimation = 0;
        PreviousAnimation = 0;
        
        //Despawn Rules
        IsAlive = true;
        IsOverWritable = false;
        DespawnOffScreen = true;
        ScreenLeashX = 99999;
        ScreenLeashY = 99999;
        DespawnOnRoomChange = true;
        DeleteOnPause = false;
        Respawnable = false;
        Respawned = false;
        
        //Stats and Values
        Name = "Generic Item";
        OwnerName = "None";
        IsPlayerOwned = false;
        OwnerArrayIndex = -1;
        EntityArrayIndex = -1;
        AIType = 0;
        Health = 1;
        HealthMax = 1;
        IsDying = false;
        IsFriendly = true;
        IsHostile = false;
        Invulnerable = false;
        DamageOnTouch = true;
        DamageOnTouchValue = 1;
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
        
// <editor-fold defaultstate="collapsed" desc="Animation0 Generic Item">
        EntityAnimation[0] = new AnimationComposite(3, 1, 32, 32, 0, false, false);
        EntityAnimation[0].AddFrame(0, 2, 300, 0, 0);
        EntityAnimation[0].AddFrame(0, 3, 300, 0, 0);
        HitBoxWidth[0] = 16;
        HitBoxHeight[0] = 16;
        HitBoxOffsetX[0] = 0;
        HitBoxOffsetY[0] = 0;
        SetMinimumTilesForHitBox(0);
// </editor-fold>

    }
    
    @Override
    protected void ComputeStatus(int delta, int[][][] TileData, int[][] RoomData, PlayerCharacter Player) {
        //Stupid item doesn't need to think.
    }
    
    @Override
    protected void ComputeVelocity()
    {
        VelocityX = 0;
        VelocityY = 0;

    }
    
    @Override
    public void GetCurrentAnimation()
    {
        if (!AnimationLock)
        {
            frameanim = 0;
            anim = true;
        }
        
        ChangeCurrentAnimation(frameanim, anim); //Set the finally determined animation and state
    }
    
    @Override
    public void ProcessCollision(PlayerCharacter Entity)
    {
        //Just boop out of existance when the player touches it. It's a simple object, why not?
        IsAlive = false;
    }
    
}
