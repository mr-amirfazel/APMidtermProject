package Roles;

public abstract class Role {
    String role;
    private boolean isSavedByDoc;
    private boolean isSilencedByPshychiatrist;

    public Role(String role) {
        this.role = role;
        this.isSavedByDoc = false;
        this.isSilencedByPshychiatrist = false;
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

    /**
     * getter for isSavedByDoc
     * @return
     */
    public boolean isSavedByDoc() {
        return isSavedByDoc;
    }

    /**
     * setter for isSavedByDoc
     * @param savedByDoc
     */
    public void setSavedByDoc(boolean savedByDoc) {
        isSavedByDoc = savedByDoc;
    }

    /**
     * getter for isSilencedByPshychiatrist
     * @return
     */
    public boolean isSilencedByPshychiatrist() {
        return isSilencedByPshychiatrist;
    }

    /**
     * setter for isSilencedByPshychiatrist
     * @param silencedByPshychiatrist
     */
    public void setSilencedByPshychiatrist(boolean silencedByPshychiatrist) {
        isSilencedByPshychiatrist = silencedByPshychiatrist;
    }
}
