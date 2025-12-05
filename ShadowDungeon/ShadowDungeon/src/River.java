import bagel.Image;
import bagel.util.Point;

    /**
     * Hazard that applies damage for as long as the player is on it
     */
public class River{
    private final Point position;
    private final Image image;
    private final double damagePerFrame;

    /**
     * River constructor to initialize it
     * @param position River's position
     */
    public River(Point position) {
        this.position = position;
        this.image = new Image("res/river.png");
        damagePerFrame = Double.parseDouble(ShadowDungeon.getGameProps().getProperty("riverDamagePerFrame"));
    }

    /**
     * Update river every frame
     * @param player player from the scene
     */
    public void update(Player player) {
        if (hasCollidedWith(player) && !player.getCharacters().getSkills().equals("marines")) {
            player.receiveDamage(damagePerFrame);
        }
    }

    /**
     * Draw River
     */
    public void draw() {
        image.draw(position.x, position.y);
    }

    /**
     * Check if player has collided to it
     * @param player player from scene
     * @return  boolean
     */
    public boolean hasCollidedWith(Player player) {
        return image.getBoundingBoxAt(position).intersects(player.getCurrImage().getBoundingBoxAt(player.getPosition()));
    }
}