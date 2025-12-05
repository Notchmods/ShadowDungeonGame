import bagel.*;
import bagel.Image;
import bagel.Window;
import bagel.util.Point;
import bagel.util.Rectangle;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Player character that can move around and between rooms, defeat enemies, collect coins
 */
public class Player {
    private Point prevPosition;
    private Point position;
    private Image currImage;
    private double health;
    private double key;
    private double speed,damage;
    private double coins = 0;
    private Robot robot;
    private Marines marines;
    private Original original;
    private boolean faceLeft = false;
    private character characters;
    private ArrayList<Bullets> ammunition = new ArrayList<>();
    private Weapon weapon;
    private int timer;

    private final double BONUS;
    private final int totalWeaponsLvl=3;
    private final int FIRING_RATE=30;

    /**
     * Initialize player (Only to be done once at starting scene.
     * @param position Starting position
     */
    public Player(Point position) {
        this.position = position;
        this.speed = Double.parseDouble(ShadowDungeon.getGameProps().getProperty("movingSpeed"));
        this.health = Double.parseDouble(ShadowDungeon.getGameProps().getProperty("initialHealth"));
        this.BONUS = Double.parseDouble(ShadowDungeon.gameProps.getProperty("robotExtraCoin"));
        this.weapon = new Weapon();
        this.damage= weapon.getDamage();

        //Initialize original character
        original= new Original();
        this.characters=original;
        this.currImage= original.getCurrentImage();
        this.timer=FIRING_RATE;
    }

    /**
     * Update the player within the scene
     * @param input get input controls from scene class
     */
    public void update(Input input) {
        // check movement keys and mouse cursor
        double currX = position.x;
        double currY = position.y;

        if (input.isDown(Keys.A)) {
            currX -= speed;
        }
        if (input.isDown(Keys.D)) {
            currX += speed;
        }
        if (input.isDown(Keys.W)) {
            currY -= speed;
        }
        if (input.isDown(Keys.S)) {
            currY += speed;
        }

        //To time the shots
        timer--;

        //Weapons matter
        Aiming(input,currX,currY);

        // update the player position accordingly and ensure it can't move past the game window
        outofBounds(currX,currY);
        applyCharacter(input);
        removeExcessAmmo();
    }

    /**
     * Get the player's current position and store it into Points
     * @param x x position
     * @param y y position
     */
    public void move(double x, double y) {
        prevPosition = position;
        position = new Point(x, y);
    }

    /**
     * Determine where the player is facing and draw the image based on that direction
     * and draw the player's UI,
     */
    public void draw() {
        // NOTE: this is an example of using the ternary operator
        //Draw character to face the cursors horizontal direction
        currImage = faceLeft ? characters.getLeftImage() : characters.getRightImage() ;
        currImage.draw(position.x, position.y);

        //Display player statistics on the UI
        UserInterface.drawStats(health, coins,key,weapon);
    }


    private void applyCharacter(Input input){
        //You're only allowed to change characters if you're in the prep room
        //Or through shop
        if(ShadowDungeon.currRoomName.equals(ShadowDungeon.PREP_ROOM_NAME)){
            //Apply robot
            if(input.isDown(Keys.R)){
                robot = new Robot();
                characters = robot;
                currImage=robot.getCurrentImage();
            }

            //Apply marines
            if(input.isDown(Keys.M)){
                marines = new Marines();
                characters=marines;
                currImage=marines.getCurrentImage();
            }
        }

    }

    /**
     * Earn coins
     * @param coins Determines how much coins the player earns
     * @param enemies Determines if it's from killing enemies or other items
     */
    public void earnCoins(double coins, boolean enemies) {
        if(characters.getSkills().equals("robot")&& enemies){
            this.coins+=(coins+BONUS);
        }else{
            this.coins += coins;
        }
    }

    /**
     * Receive damage from enemies
     * @param damage Amount of damage received
     */
    public void receiveDamage(double damage) {
        health -= damage;
        if (health <= 0) {
            ShadowDungeon.changeToGameOverRoom();
        }
    }

    /**
     * Get the character the player has chosen
     * @return character
     */
    public character getCharacters(){
        return characters;
    }

    /**
     * Getters to get player's current position
     * @return Point
     */
    public Point getPosition() {
        return position;
    }

    /**
     * Getters to get player's image
     * @return Image
     */
    public Image getCurrImage() {
        return currImage;
    }

    /**
     * Getters to get player's previous position
     * @return Point
     */
    public Point getPrevPosition() {
        return prevPosition;
    }

    /**
     * Setters to set player's key count
     */
    public void setKeyCount(double count){
        key+=count;
    }

    /**
     * Getters to get the list of ammunition the player had shot
     * @return ArrayList<Bullets>
     */
    public ArrayList<Bullets> getAmmunition(){
        return ammunition;
    }

    /**
     * Clean the ArrayList of bullets after it has been deactivated
     */
    public void removeExcessAmmo(){
        ArrayList<Bullets> toRemove = new ArrayList<>();
        for(Bullets ammo:ammunition){
            if(!ammo.getOnScene()){
                toRemove.add(ammo);
            }
        }
        ammunition.removeAll(toRemove);
    }

    /**
     * Getters to get the amount of keys the player has
     * @return double
     */
    public double getKey() {
        return key;
    }

    private void outofBounds(double currX,double currY){
        Rectangle rect = currImage.getBoundingBoxAt(new Point(currX, currY));
        Point topLeft = rect.topLeft();
        Point bottomRight = rect.bottomRight();
        if (topLeft.x >= 0 && bottomRight.x <= Window.getWidth() && topLeft.y >= 0 && bottomRight.y <= Window.getHeight()) {
            move(currX, currY);
        }
    }

    //Aiming by getting coordinate of cursor and asking bullet to move there
    private void Aiming(Input input, double currX,double currY){
        faceLeft = input.getMouseX() < currX;
        //Get direction of the cursor
        double cursorX=input.getMouseX();
        double cursorY=input.getMouseY();
        Point cursor = new Point(cursorX,cursorY);
        double distance=position.distanceTo(cursor);
        double directionX=(cursorX-position.x)/distance;
        double directionY=(cursorY-position.y)/distance;

        //Shooting function
        //Ensure that original character is unable to shoot
        if(input.wasReleased(MouseButtons.LEFT) && !characters.getSkills().equals("original")&&
                timer<0){
            ammunition.add(new Bullets(this,faceLeft,directionX,directionY));
            timer=FIRING_RATE;//Restart firing rate
        }
        weapon.DetermineDamage();//Define damage based on weapon stage
        damage= weapon.getDamage();//Get damage from weapon class

        //Update ammo's movement
        for(Bullets ammo: ammunition){
            ammo.update();
        }
    }

    /**
     * Getters used to get the amount damage the player's weapon is dealing
     * @return double
     */
    public double DamageDealt(){
        return damage;
    }

    /*Stores function*/

    /**
     * Buy health from the secure
     */
    public void BuyHealth(){
        double healthCosts=Double.parseDouble(ShadowDungeon.gameProps.getProperty("healthPurchase"));
        if(coins>=healthCosts){
            health+=healthCosts;
            coins-=healthCosts;
        }
    }

    /**
     * Upgrade weapons from the store
     */
    public void BuyWeapons(){
        double costUpgrade=Double.parseDouble(ShadowDungeon.gameProps.getProperty("healthPurchase"));
        //If weapon level is less than 3 and there's more ore equal to 50 coins then upgrade
        if(weapon.getWeaponStat()<totalWeaponsLvl&&coins>=costUpgrade){
            //Upgrade weapon levels and get rid of coins
            weapon.setLevel(1);
            coins-=costUpgrade;
        }
    }
}
