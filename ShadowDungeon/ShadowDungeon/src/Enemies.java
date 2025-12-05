import bagel.util.Point;

import java.util.ArrayList;

/**
 * Parent class that all enemy extends to
 */
public abstract class Enemies {

    public abstract void update(Player player);
    public abstract void draw();
    public abstract boolean hasCollidedWith(Player player);
    public abstract boolean isDead();
    public abstract void takeDamage(double Double);
    public abstract boolean isActive();
    public abstract void setActive(boolean active);
    public abstract boolean hasCollidedWithProjectile(Bullets bullet);
    public abstract void ShootAtPlayer(Player player);
    public abstract Point getPosition();
    public abstract boolean isRewardGiven();
}
