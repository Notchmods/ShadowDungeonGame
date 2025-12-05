import bagel.Image;
import bagel.util.Point;

/**
 Abstract class that contains all obstacles function
 */
public abstract class Obstacles {

    /**
     * Update each obstacles
     * @param player player from the scene
     */
    public abstract void update(Player player);

    /**
     * Getters to get collision with player
     * @param player player from scene
     * @return boolean
     */
    public abstract  boolean hasCollidedWithPlayer(Player player);

    /**
     * Getters to get collision with projectile
     * @param projectile projectile shot by both player and enemies
     * @return boolean
     */
    public abstract boolean hasCollidedWithProjectile(Projectile projectile);
}
