public class PowerpuffGirlF extends CharacterF {
    String[] skills;

    public PowerpuffGirlF(String name, int health, String[] skills) {
        super(name, health);
        this.skills = skills;
    }

    public int getSkillDamage(int choice) {
        if (choice == 1)
            return 15;
        if (choice == 2)
            return 25;
        if (choice == 3)
            return 35;
        return 0;
    }
}
