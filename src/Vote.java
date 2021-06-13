public class Vote {
    private String name;
    private int count;

    /**
     * constructor for Vote
     * @param name
     */
    public Vote(String name) {
        this.name = name;
        this.count = 0;
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
     * getter for name
     * @return
     */
    public String getName() {
        return name;
    }
}
