
package MegaEngine;


public class Status {
    
    public boolean PlayerCollision = false;
    public boolean EnemyCollision = false;
    public boolean ObjectCollision = false;
    public boolean BulletCollision = false;
    public boolean ItemCollision = false;
    
    public boolean isFriendly = false;
    public boolean isHostile = false;
    public boolean DamageOnTouch = false;
    public boolean KnockBackOnTouch = false;
    public boolean VelocityOnTouch = false;
    public boolean isPlatform = false;
    public boolean isSolidBlock = false;
    public boolean ReflectsBullets = false;
    public boolean isInvuln = false;
    public boolean isMidAir = false;
    public boolean isVisible = false;
    
    public float DamageOnTouchValue = 0;
    public float KnockBackOnTouchX = 0;
    public float KnockBackOnTouchY = 0;
    public float VelocityOnTouchX = 0;
    public float VelocityOnTouchY = 0;
    
    public float LocationX = 0;
    public float LocationY = 0;
    public float VelocityX = 0;
    public float VelocityY = 0;
    public int HitBoxWidth = 0;
    public int HitBoxHeight = 0;
    
    
    /*
     * Simple tracking class for all objects in the game to have a common background.
     * This class keeps tally to simplify the ProcessCollision() function which ALL
     * objects have.
     * 
     * This class holds the boolean and int/float values of status affects an object
     * may possess.
     */
    public Status()
    {
        
    }
    
    public void UpdateLocation(float locationx, float locationy, float velocityx, float velocityy, int hitboxwidth, int hitboxheight)
    {
        LocationX = locationx;
        LocationY = locationy;
        VelocityX = velocityx;
        VelocityY = velocityy;
        HitBoxWidth = hitboxwidth;
        HitBoxHeight = hitboxheight;
    }
}
