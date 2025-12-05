import bagel.Image;
import bagel.Window;
import bagel.util.Point;
import bagel.util.Rectangle;

/**
 * Projectile that the player shoots
 */
public class Bullets extends Projectile{
    private double xPos,yPos;
    private Point points;
    private final double AMMOSPEED;
    private final Image bullet;
    private boolean onScene=true;
    private Rectangle bounds;
    private Player player;
    private boolean direction;
    private double dirX,dirY;
    private String roomShotAt;

    /**
     * Initialize bullet when shot by the player
     * @param player Get player from the scene
     * @param facingLeft  Check which direction the player is facing at
     * @param directionX    Check the X direction of the cursor.
     * @param directionY    Check the Y direction of the cursor.
     */
    public Bullets(Player player, boolean facingLeft,double directionX,double directionY){
        this.bullet= new Image("res/bullet.png");
        this.player=player;
        this.xPos= player.getPosition().x;
        this.yPos= player.getPosition().y;
        this.direction=facingLeft;
        this.dirX=directionX;
        this.dirY=directionY;
        this.AMMOSPEED = Double.parseDouble(ShadowDungeon.gameProps.getProperty("bulletSpeed"));
        this.roomShotAt=ShadowDungeon.currRoomName;
        update();
    }

    /**
     * Update the ammunition as it shoots towards the target
     */
    public void update(){

        MoveTowards();

        //Ensure that the bullets is deleted once it has moved out of the game window
        // update the player position accordingly and ensure it can't move past the game window
        outOfScene();
    }

    /**
     * Make sure the ammo move towards the direction of the cursor when clicked
     */
    public void MoveTowards(){
        //Shoot based on the direction of the cursor
        xPos+=dirX*AMMOSPEED;
        yPos+=dirY*AMMOSPEED;


        if(onScene){
            //Update bullet position
            points = new Point(xPos,yPos);
        }

    }

    /**
     * Determine whether the bullet is out of scene or not
     */
    public void outOfScene(){
        Rectangle rect = bullet.getBoundingBoxAt(new Point(xPos,yPos));
        Point topLeft = rect.topLeft();
        Point bottomRight = rect.bottomRight();
        if (!(topLeft.x >= 0 && bottomRight.x <= Window.getWidth() && topLeft.y >= 0 && bottomRight.y <= Window.getHeight())) {
            onScene=false;
        }
        //If the player has entered different room then bullet will disappear
        if(roomShotAt!=ShadowDungeon.currRoomName){
            onScene=false;
        }
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
     * Getters used to get it's position
     * @return Position
     */

    public Point getPosition(){
        return points;
    }

    /**
     * Draw the bullets
     */
    public void Draw() {
        //Draw the bullet
        bullet.draw(xPos,yPos);
    }

    /**
     * Get its own image
     * @return Image
     */
    public Image getCurrImage() {
        return bullet;
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
     * get it's current image
     * @return Image
     */
    public Image getCurrentImage(){
        return bullet;
    }


}
