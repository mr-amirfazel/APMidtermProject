package Roles;

public abstract class Role {
    String role;

    public Role(String role) {
        this.role = role;
    }

    /**
     * this method is defined to give each players an ability to
     * affect the game process according to their role
     */
    public abstract void nightMove();

    @Override
    public String toString() {
        return role;
    }
}
