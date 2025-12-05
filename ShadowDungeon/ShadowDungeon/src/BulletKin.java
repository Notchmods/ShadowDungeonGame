import bagel.Image;
import bagel.util.Point;

import java.util.ArrayList;

/**
 * Variation of enemy that shoots slower and has less health
 */
public class BulletKin extends Enemies{
    private Point position;
    private double x,y;
    private final Image image;
    private double health;
    private double damage;
    private boolean active = false; // only true when the Battle Room has been activated
    private boolean dead = false;
    private final  double SHOOTING_RATE;
    private double coolDownRate =0;
    private final double EARNED_COINS;
    private boolean rewardGiven = false;

    /**Constructor for BulletKin enemy
     *
     * @param pos Starting position of the BulleKin Enemy
     */
    public BulletKin(Point pos){
        this.image = new Image("res/bullet_kin.png");
        this.position =pos;
        this.x=position.x;
        this.y=position.y;
        this.health=Double.parseDouble(ShadowDungeon.gameProps.getProperty("bulletKinHealth"));
        this.SHOOTING_RATE= Double.parseDouble(ShadowDungeon.gameProps.getProperty("bulletKinShootFrequency"));
        this.EARNED_COINS=Double.parseDouble(ShadowDungeon.gameProps.getProperty("bulletKinCoin"));
    }

    /**
     * Update the enemy features over frames
     * @param player Get the player from the scene
     */
    @Override
    public void update(Player player){
        //Stop updating if it's already dead
        if (dead){
            return;
        }
        draw();

        //Received damage from player ammunition
        playerDamage(player);

        //Enemy shoots at players
        ShootAtPlayer(player);

        //Damage dealt to player due to collision
        Damage(player);
    }

    /**
     * Draw enemy (Usually used in update)
     */
    @Override
    public void draw(){
        image.draw(x,y);
    }

    /**
     * Check if enemy image has collided with player's image
     * @param player Get player from the scene
     * @return  boolean
     */
    public boolean hasCollidedWith(Player player) {
        return image.getBoundingBoxAt(position).intersects(player.getCurrImage().getBoundingBoxAt(player.getPosition()));
    }

    /**
     * Getters used to check if enemy is dead or not
     * @return boolean
     */
    public boolean isDead() {
        return dead;
    }

    /**
     * Getters used to check if enemy is within the scene or not
     * @return boolean
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Setters used to adjust whether the enemy will be spawned or updated
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Check if the player's ammunition has hit it
     * @param bullet - Get the projectile from the scene
     * @return  boolean
     */
    public boolean hasCollidedWithProjectile(Bullets bullet) {
        return image.getBoundingBoxAt(position).intersects(bullet.getCurrImage().getBoundingBoxAt(bullet.getPosition()));
    }

    /**
     * Setters used to adjust damage that the enemy tyook
     */
    public void takeDamage(double damage){
        health-=damage;
    }

    /**
     * Implement the things that would happen when the player's ammo hit it.
     * @param player Player from the scene
     */
    public void playerDamage(Player player){
        //Check if the player has killed it or not
        //Iterate through each bullets shot by player
        for(Bullets ammo:player.getAmmunition()){
            if (!ammo.getOnScene()) continue;
            //Destroy ammo when it's detected to be in collision with the wall
            if (hasCollidedWithProjectile(ammo)) {
                ammo.setOnScene(false);
                takeDamage(player.DamageDealt());

                break;
            }
        }

        //Check if the charater is dead or not
        if(health<0&&!dead){
            dead = true;
            active = false;

            //Earning coins after killing it
            if(!rewardGiven){
                System.out.println("Enemy " + this + " killed, coins added!");
                player.earnCoins(EARNED_COINS,true);
                rewardGiven=true;
            }
            System.out.println("Enemy " + this + health);

        }

    }


    /**
     * Spawn projectiles that shoot at the player
     * @param player Get the player's direction for the fireball to head to
     */
    public void ShootAtPlayer(Player player){
        //Cool down when shooting
        if(coolDownRate<=0){
            //Direction to player
            BattleRoom.allFireBalls.add(new FireBall(player,this));
            coolDownRate=SHOOTING_RATE;
        }else{
            coolDownRate--;
        }
    }

    /**
     * Getters used to get enemy's position
     * @return Position
     */
    public Point getPosition(){
        return position;
    }

    public boolean isRewardGiven(){
        return rewardGiven;
    }


    private void Damage(Player player){
        //Bullets damage on contact with it
        if(hasCollidedWith(player)) {
            double damage= Double.parseDouble(ShadowDungeon.gameProps.getProperty("riverDamagePerFrame"));
            player.receiveDamage(damage);
        }

    }

}
