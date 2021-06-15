package Roles;

public abstract class Mafia extends Role{
    private boolean isSavedByLecter;
    public Mafia(String role) {
        super(role);
        this.isSavedByLecter = false;
    }

    /**
     * this method is defined to give each players an ability to
     * affect the game process according to their role
     */
    @Override
    public void nightMove() {

    }

    /**
     * getter for isSaved by lecter
     * @return
     */
    public boolean isSavedByLecter() {
        return isSavedByLecter;
    }

    /**
     * setter for isSavedBylecter
     * @param savedByLecter
     */
    public void setSavedByLecter(boolean savedByLecter) {
        isSavedByLecter = savedByLecter;
    }
}
