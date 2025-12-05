import bagel.Image;
import bagel.Window;
import bagel.util.Point;
import bagel.util.Rectangle;

import java.util.ArrayList;
/**
Projectile that the enemy shoots at
 */
public class FireBall extends Projectile{
    private double xPos,yPos;
    private Point points;
    private double fireBallSpeed;
    private final Image fireBall;
    private boolean onScene=true;
    private Rectangle bounds;
    private Player player;
    private double directionX,directionY;

    /**
     * Initialize FireBall when shot by the enemies
     * @param player Get player from the scene
     * @param enemy Get the enemy that shot this fireball
     */
    public FireBall(Player player, Enemies enemy) {
        this.fireBall = new Image("res/fireball.png");
        this.player=player;
        this.xPos= enemy.getPosition().x;
        this.yPos= enemy.getPosition().y;
        points= new Point(xPos,yPos);
        this.fireBallSpeed= Double.parseDouble(ShadowDungeon.gameProps.getProperty("fireballSpeed"));
        //Calculate initial direction enemy is pointing at
        double distance = this.getPosition().distanceTo(player.getPosition());
        this.directionX=(player.getPosition().x-this.getPosition().x)/distance;
        this.directionY=(player.getPosition().y-this.getPosition().y)/distance;
    }

    /**
     *  Update the ammunition as it shoots towards the target
     */
    public void update(){
        MoveTowards();
        outOfScene();
    }

    /**
     * Check if bullet image has collided with obstacle's image
     * @param obstacles Get obstacles
     * @return boolean
     */
    public boolean hasCollidedWithObstacles(Obstacles obstacles){
        return bounds.intersects(points);
    }

    /**
     * Check if it has collided with player or not
     * @param player player from scene
     * @return boolean
     */
    public boolean hasCollidedWithPlayer(Player player) {
        return fireBall.getBoundingBoxAt(points).intersects(player.getCurrImage().getBoundingBoxAt(player.getPosition()));
    }

    /**
     * Make sure the FireBall move towards the direction of the player when shot
     */
    public void MoveTowards(){
        //Move to player
        xPos+=directionX*fireBallSpeed;
        yPos+=directionY*fireBallSpeed;
        if(onScene){
            //Update the bullets
            points = new Point(xPos,yPos);
        }
    }

    /**
     * Determine whether the bullet is out of scene or not
     */
    public void outOfScene(){
        Rectangle rect = fireBall.getBoundingBoxAt(new Point(xPos,yPos));
        Point topLeft = rect.topLeft();
        Point bottomRight = rect.bottomRight();
        if (!(topLeft.x >= 0 && bottomRight.x <= Window.getWidth() && topLeft.y >= 0 && bottomRight.y <= Window.getHeight())) {
            onScene=false;
        }
    }

    /**
     * Set if the ammunition is on scene or not
     * @param state Whether the ammunition is on scene or not
     */
    public void setOnScene(boolean state){
        onScene=state;
    }

    /**
     * Getters to check if the bullet is on scene or not
     * @return boolean
     */
    public boolean getOnScene(){
        return onScene;
    }

    /**
     * Getters used to get it's position
     * @return Position
     */
    public Point getPosition(){
        return points;
    }

    /**
     * Get its own image
     * @return Image
     */
    public Image getCurrentImage() {
        return fireBall;
    }

    /**
     * Draw the fireball
     */
    public void Draw(){
        fireBall.draw(xPos,yPos);
    }

}
