package MegaEngine.BulletAI;

import MegaEngine.AnimationComposite;
import MegaEngine.Bullet;
import MegaEngine.Enemy;
import MegaEngine.Entity;
import MegaEngine.Global.Directions;
import MegaEngine.PlayerCharacter;

public class BulletGeneric extends Bullet
{    
    /*
     * Constructor.
     * Creates a specific type of bullet that works on a preset logic.
     * Any image can use any AI type, although some may look funny.
     */
    public BulletGeneric(int aitype, Directions horzfacing, Directions vertfacing, float locationx, float locationy, boolean isplayerowned, boolean isfriendly, boolean ishostile, String ownername, int owner)
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
        HorizontalSpeed = 165f;
        VerticalSpeed = 0f;
        InertiaX = 0f;
        InertiaY = 0f;
        
        //Animations and Hitboxes (this can get stupidly large)
        GenerateAnimationsAndHitboxes();
        
        //Collisions
        PlayerCollisionEnabled = true;
        EnemyCollisionEnabled = true;
        ObjectCollisionEnabled = false;
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
        DirectionHorizontal = horzfacing;
        DirectionVertical = vertfacing;
        CurrentAnimation = 0;
        PreviousAnimation = 0;
        
        //Despawn Rules
        IsAlive = true;
        IsOverWritable = false;
        DespawnOffScreen = true;
        ScreenLeashX = 99999;
        ScreenLeashY = 99999;
        DespawnOnRoomChange = true;
        DeleteOnPause = true;
        Respawnable = false;
        Respawned = false;
        
        //Stats and Values
        Name = "Generic Bullet";
        OwnerName = ownername;
        IsPlayerOwned = isplayerowned;
        OwnerArrayIndex = -1;
        EntityArrayIndex = -1;
        AIType = aitype;
        Health = 1;
        HealthMax = 1;
        IsDying = false;
        IsFriendly = true;
        IsHostile = false;
        Invulnerable = false;
        DamageOnTouch = true;
        DamageOnTouchValue = 1;
        IsPlayerActivatorEnabled = false;
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
        
// <editor-fold defaultstate="collapsed" desc="Animation0 Bullet">
        EntityAnimation[0] = new AnimationComposite(2, 1, 16, 16, 0, false, false);
        EntityAnimation[0].AddFrame(0, 1, 9001, 0, 0);
        HitBoxWidth[0] = 16;
        HitBoxHeight[0] = 16;
        HitBoxOffsetX[0] = 0;
        HitBoxOffsetY[0] = 0;
        SetMinimumTilesForHitBox(0);
// </editor-fold>

    }
     
    @Override
    protected void ComputeStatus(int delta, int[][][] TileData, int[][] RoomData, PlayerCharacter Player) {
        //Stupid bullet doesn't need to think.
    }
    
    @Override
    protected void ComputeVelocity() {
        
        VelocityX = 0;
        VelocityY = 0;
        
        if (VelocityEnabled) {
        
            if (DirectionHorizontal == Directions.Right) {
                VelocityX += HorizontalSpeed;
            } else {
                VelocityX -= HorizontalSpeed;
            }
        }
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
        //There is only one image to show, and it doesn't animate.
        if (!AnimationLock)
        {
            frameanim = 0;
            anim = false;
        }
        
        //Set the finally determined animation and state
        ChangeCurrentAnimation(frameanim, anim);
        
    }
    
    @Override
    public void ProcessCollision(Enemy Enemy)
    {
        //Just boop out of existance when something touches it that should take damage.
        //It's a simple object, why not?
        if (IsFriendly) {
            IsAlive = false;
        }
    }
    
    @Override
    public void ProcessCollision(PlayerCharacter Player)
    {
        //Just boop out of existance when something touches it that should take damage.
        //It's a simple object, why not?
        if (IsHostile) {
            IsAlive = false;
        }
    }
    
}