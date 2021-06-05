import Roles.Role;

public class Player {

    private String username;
    private Role role;

    public Player(String username) {
        this.setUsername(username);
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

    @Override
    public String toString() {
        return getUsername() +" : "+ getRole().getClass();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
