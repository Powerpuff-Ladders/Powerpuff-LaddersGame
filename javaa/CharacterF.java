
public class CharacterF {
    String name;
    int health;

    public CharacterF(String name, int health) {
        this.name = name;
        this.health = health;
    }

    public void takeDamage(int d) {
        health -= d;
        if (health < 0)
            health = 0;
    }

    public boolean isAlive() {
        return health > 0;
    }
}
