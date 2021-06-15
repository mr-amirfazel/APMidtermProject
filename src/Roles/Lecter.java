package Roles;

public class Lecter extends Mafia{
    private boolean selsSaved =false;



    public Lecter() {
        super("Lecter");
    }

    /**
     * this method is defined to give each players an ability to
     * affect the game process according to their role
     */
    @Override
    public void nightMove() {

    }

    /**
     * getter for the state wether Lecter has saved himself or not
     * @return
     */
    public boolean isSelfSaved() {
        return selsSaved;
    }

    /**
     * setter for the safeSave of the Lecter
     * @param sselfSaved
     */
    public void setSelfSaved(boolean sselfSaved) {
        this.selsSaved = selsSaved;
    }
}
