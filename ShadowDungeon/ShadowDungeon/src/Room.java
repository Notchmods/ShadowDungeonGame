
import bagel.Input;

import java.util.ArrayList;
/**
 Abstract class that contains all room functions
 */
public abstract class Room {
    /**
     * Get list of enemies within the scene
     * @return ArrayList<Enemies>
     */
    public abstract ArrayList<Enemies> getEnemies();

    /** Update the scene every single frame
     *
     * @param input From ShadowDungeon
     */
    public abstract void update(Input input);
}
