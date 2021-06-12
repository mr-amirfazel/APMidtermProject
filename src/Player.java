import Roles.Role;

public class Player {

    private String username;
    private Role role;
    private boolean isAlive;

    public Player(String username) {
        this.setUsername(username);
        this.isAlive = true;
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
    /**
     * toString method for player introducing its name and role
     */
    public String toString() {
        return getUsername() +" is: "+ getRole().toString();
    }

    /**
     * getter method for Username
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     * setter method for username
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * getter for the isAlive boolean
     * @return
     */
    public boolean isAlive() {
        return isAlive;
    }

    /**
     * setter for the isAlive boolean
     * @param alive
     */
    public void setAlive(boolean alive) {
        isAlive = alive;
    }
}
