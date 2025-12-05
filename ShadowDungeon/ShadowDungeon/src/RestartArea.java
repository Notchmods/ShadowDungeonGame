import bagel.Image;
import bagel.Input;
import bagel.Keys;
import bagel.util.Point;

import java.util.ArrayList;

/**
 * Area in Prep or End Room where the player can trigger a game reset
 */
public class RestartArea {
    private final Point position;
    private final Image image;

    /**
     * Spawn restart area into the scene
     * @param position Position of the restart area.
     */
    public RestartArea(Point position) {
        this.position = position;
        this.image = new Image("res/restart_area.png");
    }

    /**
     * Update restart area  each frame
     * @param input Input from ShadowDungeon
     * @param player player from scene
     */
    public void update(Input input, Player player) {
        if (hasCollidedWith(player) && input.wasPressed(Keys.ENTER)) {
            ShadowDungeon.resetGameState(ShadowDungeon.getGameProps());
        }
    }

    /**
     * Draw restart area
     */
    public void draw() {
        image.draw(position.x, position.y);
    }

    /**
     * Check if it had collided with player
     * @param player player from scene
     * @return boolean
     */
    public boolean hasCollidedWith(Player player) {
        return image.getBoundingBoxAt(position).intersects(player.getCurrImage().getBoundingBoxAt(player.getPosition()));
    }


}
