import bagel.Image;
import bagel.util.Point;

/**
 Abstract class that contains all projectile function
 */
public abstract class Projectile {
    /**
     * Update the ammunition as it shoots towards the target
     */
    public abstract void update();
    /**
     * Make sure the ammo move towards the direction of the cursor when clicked
     */
    public abstract void MoveTowards();
    /**
     * Check if bullet image has collided with obstacle's image
     * @param obstacles Get obstacles
     * @return boolean
     */
    public abstract boolean hasCollidedWithObstacles(Obstacles obstacles);

    /**
     * Determine whether the bullet is out of scene or not
     */
    public abstract void outOfScene();

    /**
     * get it's current image
     */
    public abstract Image getCurrentImage();
    /**
     * Getters used to get it's position
     */
    public abstract Point getPosition();

    /**
     * Draw projectile
     */
    public abstract void Draw();
    /**
     * Getters to check if the bullet is on scene or not
     * */
    public abstract boolean getOnScene();
}
