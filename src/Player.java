import Roles.Role;

public class Player {

    String username;
    Role role;

    public Player(String username) {
        this.username = username;

    }

    /**
     * getter method for Role
     * @return
     */
    public Role getRole() {
        return role;
    }

    /**
     * setter method for Role
     * @param role
     */
    public void setRole(Role role) {
        this.role = role;
    }


}
