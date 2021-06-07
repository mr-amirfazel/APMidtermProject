package Roles;

import Roles.Role;

public class SimpleCitizen extends Role {
    public SimpleCitizen() {
        super("SimpleCitizen");
    }

    /**
     * this method is defined to give each players an ability to
     * affect the game process according to their role
     */
    @Override
    public void nightMove() {

    }
}
