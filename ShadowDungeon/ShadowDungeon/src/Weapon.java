/**
 * Weapon class that stores the weapon information for player.
 */
public class Weapon {
    private int level;
    private double damagePerBullet;

    /**
     * Initialize weapon when the player is spawned
     */
    public Weapon(){
        //Set the weapon original level to 1 when it's instantiated
        setLevel(1);
    }

    /**
     * Change the weapon damage based on the level of weapon that the player holds
     */
    public void DetermineDamage(){
        //Determines damage based on upgrades
        double damage;
        switch(level){
            //Standard weapon damage
            case 1:
                 damage=Double.parseDouble(ShadowDungeon.gameProps.getProperty("weaponStandardDamage"));
                damagePerBullet=damage;
                break;
            case 2:
                damage=Double.parseDouble(ShadowDungeon.gameProps.getProperty("weaponAdvanceDamage"));
                damagePerBullet=damage;
                break;
            case 3:
                damage=Double.parseDouble(ShadowDungeon.gameProps.getProperty("weaponEliteDamage"));
                damagePerBullet=damage;
                break;
            default:
                break;
        }
    }

    /**
     * Getters used to get player stats
     * @return Int
     */
    public int getWeaponStat(){
        return level;
    }

    /**
     * Setters used to set the lvl the weapon is
     * @param lvl 1-3
     */
    public void setLevel(int lvl){
        this.level+=lvl;
    }

    /**
     * Getters to get the damage of the guns
     * @return double
     */
    public double getDamage(){
        return damagePerBullet;
    }



}
