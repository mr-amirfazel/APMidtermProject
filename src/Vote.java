public class Vote {
    private Player player;
    private int count;

    public Vote(Player player) {
        this.player = player;
        this.count =0;
    }
    /**
     * increments count by one
     */
    public void addCount()
    {
        count++;
    }

    /**
     * getter for integer count
     * @return
     */
    public int getCount() {
        return count;
    }

    /**
     * getter for Player
     * @return
     */
    public Player getPlayer() {
        return player;
    }
}
