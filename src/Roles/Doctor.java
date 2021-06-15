package Roles;

public class Doctor extends Role{
    private boolean selfSaved;
    public Doctor() {
        super("Doctor");
        this.selfSaved = false;
    }

    /**
     * this method is defined to give each players an ability to
     * affect the game process according to their role
     */
    @Override
    public void nightMove() {

    }
    /**
     * getter for selfSaved
     * @return
     */
    public boolean isSelfSaved() {
        return selfSaved;
    }

    /**
     * setter for selfSaved
     * @param selfSaved
     */
    public void setSelfSaved(boolean selfSaved) {
        this.selfSaved = selfSaved;
    }
}
