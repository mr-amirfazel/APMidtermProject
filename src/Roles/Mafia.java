package Roles;

public abstract class Mafia extends Role{
    public Mafia(String role) {
        super(role);
    }

    /**
     * this method is defined to give each players an ability to
     * affect the game process according to their role
     */
    @Override
    public void nightMove() {

    }
}
