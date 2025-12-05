import bagel.Image;
/**
Abstract class that contains all character function
 */
public abstract class character {
    /**
     * Apply skills of the player
     */
    public abstract void applySkill();
    /**
     * Getters to get skills of the player
     */
    public abstract String getSkills();
    /**
     * Getters to get left image of the player
     */
    public abstract Image getLeftImage();
    /**
     * Getters to get right image of the player
     */
    public abstract Image getRightImage();
}
