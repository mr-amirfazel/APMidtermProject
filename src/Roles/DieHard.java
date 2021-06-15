package Roles;

public class DieHard extends Role{
    private int announceCount;
    public DieHard() {
        super("Diehard");
        this.announceCount =0;
    }

    /**
     * this method is defined to give each players an ability to
     * affect the game process according to their role
     */
    @Override
    public void nightMove() {

    }
    public void increment(){
        announceCount++;
    }

    /**
     * getter for announceCount
     * @return
     */
    public int getAnnounceCount() {
        return announceCount;
    }
}
