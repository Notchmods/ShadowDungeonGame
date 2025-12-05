import bagel.Image;
import bagel.util.Point;

import java.util.ArrayList;

/**
 * Enemy that gets removed when the player overlaps with it
 */
public class KeyBulletKin extends Enemies {
    private Point position;
    private double x,y;
    private double health;
    private final Image image;
    private boolean active = false; // only true when the Battle Room has been activated
    private boolean dead = false;
    private ArrayList<Point> patrolRoutes;
    private int headingTo=0;
    private final double STEP_SIZE;
    private final int HOWCLOSE =5;
    private int pP;

    /**
     * Constructor to initialize keybulletkin
     * @param startPos Starting position
     * @param patrolPoints List of patrol points
     * @param pp Amount of patrol points
     */
    public KeyBulletKin(Point startPos,ArrayList<Point> patrolPoints,int pp) {
        this.position = startPos;
        this.image = new Image("res/key_bullet_kin.png");
        //Get patrol routes
        this.patrolRoutes= new ArrayList<>();
        this.patrolRoutes=patrolPoints;
        //Gets player step size
        STEP_SIZE=Double.parseDouble(ShadowDungeon.gameProps.getProperty("keyBulletKinSpeed"));
        this.x= position.x;
        this.y=position.y;
        this.pP=pp;
        this.health=Double.parseDouble(ShadowDungeon.gameProps.getProperty("keyBulletKinHealth"));
    }

    /**
     * Update keyBulletKin each frame
     * @param player player from scene
     */
    @Override
    public void update(Player player) {
        //Update keyBulletKin position
        position = new Point(x,y);

        //Check if the player has killed it or not
        //Iterate through each bullets shot by player
        for(Bullets ammo:player.getAmmunition()){
            //Destroy ammo when it's detected to be in collision with the wall
            if (hasCollidedWithProjectile(ammo)) {
                ammo.setOnScene(false);
                takeDamage(player.DamageDealt());
            }
        }

        //Deal damage to paly er
        Damage(player);

        //Patrol the perimeter
        //Cycles the patrol route
        Patrol();
    }

    /**
     * Draw KeyBulletKin
     */
    public void draw() {
        image.draw(position.x, position.y);
    }

    private void Patrol(){
        double distance=0;
        if(headingTo<pP){
            //Head to the patrol point
            Point currentPoint=patrolRoutes.get(headingTo);
            distance=position.distanceTo(currentPoint);
            if(distance>0){
                //Find out the direction that it's moving to
                double directionX=(currentPoint.x-position.x)/distance;
                double directionY=(currentPoint.y-position.y)/distance;
                //Move towards the patrol points
                x+=directionX*STEP_SIZE;
                y+=directionY*STEP_SIZE;
            }

            //Ensure the robot keeps on
            if(distance<HOWCLOSE){
                //Once it has arrived at its patrol point, it then goes to the next
                headingTo++;
            }
        }else{
            //Reset patrol point once it has cycled through everything
            headingTo=0;
        }
    }

    /**
     * Implement the things that would happen when the player's ammo hit it.
     * @param player Player from the scene
     */
    //Dealing damage to player and checking if it's(enemy) dead or not
    private void Damage(Player player){
        //Player damage on contact with it
        if(hasCollidedWith(player)) {
                double damage= Double.parseDouble(ShadowDungeon.gameProps.getProperty("riverDamagePerFrame"));
            player.receiveDamage(damage);
        }

        //Check if the charater is dead or not
        if(health<0 &&!dead){
            dead = true;
            active = false;
        }
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
     * Check if the player's ammunition has hit it
     * @param bullet - Get the projectile from the scene
     * @return  boolean
     */
    public boolean hasCollidedWithProjectile(Bullets bullet) {
        return image.getBoundingBoxAt(position).intersects(bullet.getCurrImage().getBoundingBoxAt(bullet.getPosition()));
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
     * Take damage when shot by player
     * @param damage damage that's inflicted
     */
    public void takeDamage(double damage){
        health-=damage;
    }

    /**
     * Shoot at player
     * @param player player from scene
     */
    public void ShootAtPlayer(Player player){
        //Nothing happens
    }

    /**
     * Getters used to get enemey's position
     * @return Position
     */
    public Point getPosition(){
        return position;
    }


    public boolean isRewardGiven(){
        return dead;
    }

}
